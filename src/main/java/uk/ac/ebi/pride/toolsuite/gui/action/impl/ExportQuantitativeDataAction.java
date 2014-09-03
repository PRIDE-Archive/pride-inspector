package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.quant.QuantExportDialog;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * Action for showing ensembl browser
 * <p/>
 * @author: rwang
 * @author ypriverol
 */
public class ExportQuantitativeDataAction extends PrideAction {

    private static final Logger logger = LoggerFactory.getLogger(ExportQuantitativeDataAction.class);
    /**
     * JTable where protein name will be displayed
     */
    private JTable table;

    /**
     * data access controller
     */
    private DataAccessController controller;

    /**
     * Constructor
     *
     * @param table      protein table
     * @param controller data access controller
     */
    public ExportQuantitativeDataAction(JTable table,
                                        DataAccessController controller) {
        super(Desktop.getInstance().getDesktopContext().getProperty("export.quantitative.data.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("export.quantitative.data.small.icon")));
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable code = new Runnable() {
        @Override
            public void run() {
            QuantExportDialog quantExportDialog = null;
            try {
                quantExportDialog = new QuantExportDialog(Desktop.getInstance().getMainComponent(), table, controller,controller.getAvailableProteinLevelScores());
            } catch (DataAccessException e1) {
                logger.error("Failed to create an new instance of QuantExportDialog", e1);
            }
            if(quantExportDialog != null)
               quantExportDialog.setVisible(true);
            }
        };

        try {
            EDTUtils.invokeAndWait(code);
        } catch (InvocationTargetException e1) {
            logger.error("Failed to create an new instance of QuantExportDialog", e1);
        } catch (InterruptedException e1) {
            logger.error("Failed to create an new instance of QuantExportDialog", e1);
        }
    }
}
