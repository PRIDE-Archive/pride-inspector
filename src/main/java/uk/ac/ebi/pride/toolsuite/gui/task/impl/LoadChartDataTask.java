package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.chart.io.DataAccessReader;
import uk.ac.ebi.pride.toolsuite.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataException;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataReader;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.io.db.PooledConnectionFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class to load the charts data in background.</p>
 *
 * @author Antonio Fabregat
 * Date: 26-ago-2010
 * Time: 11:59:12
 */
public class LoadChartDataTask extends AbstractDataAccessTask<PrideDataReader, Void> {

    private static final Logger logger = LoggerFactory.getLogger(LoadChartDataTask.class);

    private String accession;

    public LoadChartDataTask(DataAccessController controller, String accession) {
        super(controller);
        this.setName("Loading chart data");
        this.setDescription("Loading chart data");

        this.accession = accession;
    }

    public LoadChartDataTask(DataAccessController controller) {
        this(controller, null);
    }

    /**
     * Query database and return JSON String list based on accession number.
     * each line structure like this:
     *
     * 1, ........
     * 2, ........
     */
    private String[] getJSONStringList() throws SQLException {
        String sql = "select concat(c.chart_type, ', ', c.intermediate_data) content " +
                "from pride_2.pride_chart_data c, pride_2.pride_experiment e " +
                "where c.experiment_id = e.experiment_id " +
                "and e.accession = ? " +
                "order by 1";

        Connection conn = PooledConnectionFactory.getConnection();
        PreparedStatement stat = conn.prepareStatement(sql);
        stat.setString(1, accession);
        ResultSet rs = stat.executeQuery();

        List<String> jsonList = new ArrayList<String>();
        while (rs.next()) {
            jsonList.add(rs.getString(1));
        }

        rs.close();
        conn.close();

        return jsonList.toArray(new String[jsonList.size()]);
    }

    @Override
    protected PrideDataReader retrieve() throws Exception {
        PrideDataReader reader = null;
        EventBus.publish(new ProcessingDataSourceEvent<DataAccessController>(controller, ProcessingDataSourceEvent.Status.CHART_GENERATION, controller));
        try {
            if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                reader = new ElderJSONReader(getJSONStringList());
            } else {
                reader = new DataAccessReader(controller);
            }
        } catch (PrideDataException ex) {
            String msg = "Failed to get summary charts";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        } catch (SQLException ex) {
            String msg = "Failed to query database";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }
        EventBus.publish(new ProcessingDataSourceEvent<DataAccessController>(controller, ProcessingDataSourceEvent.Status.CHART_GENERATION, controller));
        return reader;
    }
}
