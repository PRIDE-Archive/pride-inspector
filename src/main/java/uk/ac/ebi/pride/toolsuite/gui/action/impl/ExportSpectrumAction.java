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
import uk.ac.ebi.pride.toolsuite.gui.event.SpectrumAddEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.ExportSpectrumMGFTask;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.DOT;
import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.MGF_FILE;

/**
 * Export spectra to mgf format.
 * <p/>
 * User: dani
 * Date: 23-Aug-2010
 * Time: 11:38:26
 * To change this template use File | Settings | File Templates.
 */
public class ExportSpectrumAction extends PrideAction {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumAction.class);
    private static final String FILE_NAME = "spectrum";

    public ExportSpectrumAction(String name, Icon icon) {
        super(name, icon);

        // enable annotation
        AnnotationProcessor.process(this);

        // enable
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        DataAccessController controller = context.getForegroundDataAccessController();
        String defaultFileName = controller.getName().split("\\" + DOT)[0] + "_" + FILE_NAME;
        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select File To Export Spectrum Data To", true, defaultFileName, false, MGF_FILE);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ofd.getSelectedFile();
            // store file path for reuse
            String filePath = selectedFile.getPath();
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));

            ExportSpectrumMGFTask newTask = new ExportSpectrumMGFTask(controller, filePath + (filePath.endsWith(MGF_FILE) ? "" : MGF_FILE));
            TaskUtil.startBackgroundTask(newTask, controller);
        }
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        try {
            DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();
            this.setEnabled(controller != null && controller.hasSpectrum());
        } catch (DataAccessException e) {
            logger.error("Failed to check the data access controller", e);
        }
    }

    @EventSubscriber(eventClass = SpectrumAddEvent.class)
    public void onSpectrumAddEvent(SpectrumAddEvent evt) {
        try {
            DataAccessController controller = evt.getController();
            this.setEnabled(controller != null && controller.hasSpectrum());
        } catch (DataAccessException e) {
            logger.error("Failed to check the data access controller", e);
        }
    }
}


