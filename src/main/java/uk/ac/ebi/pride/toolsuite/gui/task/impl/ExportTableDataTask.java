package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Task to export quantitative data
 *
 * User: rwang
 * Date: 23/08/2011
 * Time: 15:35
 */
public class ExportTableDataTask extends TaskAdapter<Void, Void>{

    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescTask.class);

    /**
     * output File
     */
    private String outputFilePath;

    /**
     * quantitative data table
     */
    private JTable table;

    public ExportTableDataTask(JTable table, String fileName,
                               String taskName, String taskDesc) {
        this.table = table;
        this.outputFilePath = fileName;
        this.setName(taskName);
        this.setDescription(taskDesc);
    }

    @Override
    protected Void doInBackground() throws Exception {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(new File(outputFilePath)));

            // header
            int rowCnt = table.getRowCount();
            int colCnt = table.getColumnCount();

            String header = "";
            for (int i = 0; i < colCnt; i++) {
                header += table.getColumnName(i) + Constants.TAB;
            }
            writer.println(header);

            // rows
            for (int i = 0; i < rowCnt; i++) {
                String row = "";
                for (int j = 0; j < colCnt; j++) {
                    Object value = table.getValueAt(i, j);
                    row += (value == null ? "" : value) + Constants.TAB;
                }
                writer.println(row);

                checkInterruption();
            }

            writer.flush();
        } catch (IOException e1) {
            String msg = "Failed to write data to the output file, please check you have the right permission";
            logger.error(msg, e1);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.currentThread().interrupted()) {
            throw new InterruptedException();
        }
    }
}
