package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Unzip a list of gzipped files
 * <p/>
 * User: rwang
 * Date: 16/12/10
 * Time: 12:16
 */
public class OpenGzippedFileTask extends TaskAdapter<Void, File> {

    private static final int BUFFER_SIZE = 2048;

    /**
     * A list of input files to be unzipped
     */
    private List<File> inputFiles;

    private String path;

    public OpenGzippedFileTask(List<File> files, String path) {
        this.inputFiles = new ArrayList<File>(files);
        this.path = path.endsWith(System.getProperty("file.separator")) ? path : path + System.getProperty("file.separator");
        String msg = "Unzipping Files";
        this.setName(msg);
        this.setDescription(msg);
    }


    @Override
    protected Void doInBackground() throws Exception {
        for (File inputFile : inputFiles) {
            FileInputStream fis = null;
            GZIPInputStream gs = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fis = new FileInputStream(inputFile);
                gs = new GZIPInputStream(fis);

                String outputFile = path + inputFile.getName().replace(".gz", "");
                fos = new FileOutputStream(outputFile);
                bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                byte data[] = new byte[BUFFER_SIZE];
                int count;
                while ((count = gs.read(data, 0, BUFFER_SIZE)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                publish(new File(outputFile));
            } finally {
                if (fis != null) {
                    fis.close();
                }

                if (gs != null) {
                    gs.close();
                }

                if (fos != null) {
                    fos.close();
                }

                if (bos != null) {
                    bos.close();
                }
            }
        }

        return null;
    }
}
