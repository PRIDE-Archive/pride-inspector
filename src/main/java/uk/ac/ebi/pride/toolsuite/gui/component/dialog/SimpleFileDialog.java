package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:54:20
 */
public class SimpleFileDialog extends JFileChooser {
    private static final String OPEN_FILE = "Open File";
    private static final String SAVE_FILE = "Save File";

    private String[] fileFormats;

    /**
     * @param path       default path
     * @param title      title for the component
     * @param extensions a list of supported file extensions
     */
    public SimpleFileDialog(String path, String title, boolean multiSelection, String defaultFileName,
                            boolean openDialog, String... extensions) {
        super(path);
        this.fileFormats = extensions;
        // set dialog title
        this.setDialogTitle(title == null ?
                (openDialog ? OPEN_FILE : SAVE_FILE) :
                title);
        // set dialog default file name
        if (defaultFileName != null) {
            this.setSelectedFile(new File(defaultFileName));
        }
        // set dialog type, either open or save
        this.setDialogType(openDialog ? JFileChooser.OPEN_DIALOG : JFileChooser.SAVE_DIALOG);

        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setMultiSelectionEnabled(multiSelection);
        this.setFileFilter(new InnerFileFilter());
    }

    private class InnerFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            boolean result = false;
            String fileName = f.getName().toLowerCase();

            for (String fileFormat : fileFormats) {
                if (fileName.endsWith(fileFormat))
                    result = true;
            }

            result = f.isDirectory() || result;

            return result;
        }

        @Override
        public String getDescription() {
            StringBuilder str = new StringBuilder();
            for (String fileFormat : fileFormats) {
                if (str.length() != 0)
                    str.append(" or ");

                str.append(fileFormat);
            }
            return str.toString();
        }
    }


}
