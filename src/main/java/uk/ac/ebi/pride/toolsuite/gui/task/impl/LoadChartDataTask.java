package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.chart.io.DataAccessReader;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataException;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataReader;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.filter.AccessionFilter;

/**
 * <p>Class to load the charts data in background.</p>
 *
 * @author Antonio Fabregat
 *         Date: 26-ago-2010
 *         Time: 11:59:12
 */
public class LoadChartDataTask extends AbstractDataAccessTask<PrideDataReader, Void> {

    private static final Logger logger = LoggerFactory.getLogger(LoadChartDataTask.class);

    private AccessionFilter<String> filter;

    public LoadChartDataTask(DataAccessController controller, AccessionFilter<String> filter) {
        super(controller);
        this.setName("Loading chart data");
        this.setDescription("Loading chart data");
        this.filter = filter;
    }


    @Override
    protected PrideDataReader retrieve() throws Exception {
        PrideDataReader reader = null;
        long date = System.currentTimeMillis();
        EventBus.publish(new ProcessingDataSourceEvent<>(controller, ProcessingDataSourceEvent.Status.CHART_GENERATION, controller));
        try {

            reader = new DataAccessReader(controller, filter);
        } catch (PrideDataException ex) {
            String msg = "Failed to get summary charts";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }
        logger.debug("CHARTS LOAD | All the charts has been shown in: |{}| milliseconds", System.currentTimeMillis() - date );
        EventBus.publish(new ProcessingDataSourceEvent<>(controller, ProcessingDataSourceEvent.Status.CHART_GENERATION, controller));
        return reader;
    }
}
