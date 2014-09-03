package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.ExportIdentificationPeptideTask;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.DOT;
import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.TAB_SEP_FILE;

/**
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 15:35:05
 */
public class ExportIdentificationPeptideAction extends PrideAction {
    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationPeptideAction.class);
    private static final String FILE_NAME = "protein_peptide";


    public ExportIdentificationPeptideAction(String name, Icon icon) {
        super(name, icon);

        // enable annotation
        AnnotationProcessor.process(this);
        // enable component
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        DataAccessController controller = context.getForegroundDataAccessController();
        String defaultFileName = controller.getName().split("\\" + DOT)[0] + "_" + FILE_NAME;
        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Export Identifications and Peptides", true, defaultFileName, false, TAB_SEP_FILE);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
            ExportIdentificationPeptideTask newTask = new ExportIdentificationPeptideTask(controller, filePath + (filePath.endsWith(TAB_SEP_FILE) ? "" : TAB_SEP_FILE));
            TaskUtil.startBackgroundTask(newTask, controller);
        }
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        try {
            DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();
            this.setEnabled(controller != null && controller.hasProtein());
        } catch (DataAccessException e) {
            logger.error("Failed to check data access controller", e);
        }
    }
}
