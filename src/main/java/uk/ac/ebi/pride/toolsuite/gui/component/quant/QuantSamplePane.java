package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.QuantitativeSample;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;

/**
 * User: rwang
 * Date: 15/08/2011
 * Time: 11:34
 */
public class QuantSamplePane extends DataAccessControllerPane {

    private static final Logger logger = LoggerFactory.getLogger(QuantSamplePane.class);

    private JTable sampleDetailTable;

    public QuantSamplePane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        QuantitativeSample sample = null;
        try {
            sample = controller.getQuantSample();
            sampleDetailTable = TableFactory.createQuantSampleTable(sample);
        } catch (DataAccessException e) {
            logger.error("Fail to get quantitative sample");
        }

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);


        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(sampleDetailTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This builds the top panel to display, it includes
     *
     * @return JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JToolBar buttonPanel = buildButtonPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        return titlePanel;
    }

    /**
     * Build meta data pane, this panel displays the identification type, search engine and search database
     *
     * @return JPanel   meta data pane
     */
    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        // protein table label
        JLabel tableLabel = new JLabel("<html><b>Sample</b></html>");
        metaDataPanel.add(tableLabel);

        return metaDataPanel;
    }


    /**
     * Build toolbar which contains all the buttons.
     *
     * @return JToolbar    tool bar
     */
    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);


        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.quant");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }
}
