package uk.ac.ebi.pride.toolsuite.gui.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.*;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Open a group of files belong to the same assay silently (without dialogs)
 *
 * NOTE: calling the open method can take a long time
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SilentSingleAssayFileOpener implements TaskListener<Void, File> {

    private static final Logger logger = LoggerFactory.getLogger(SilentSingleAssayFileOpener.class);

    private final PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();

    public void open(final List<File> inputFiles, final File workingDirectory) throws IOException {

        // make a copy of the file list
        ArrayList<File> files = new ArrayList<File>(inputFiles);

        // find zipped files
        List<File> zippedFiles = findZippedFiles(files);

        // remove all the zipped files from the list
        files.removeAll(zippedFiles);

        // unzip files
        List<File> unzippedFiles = unzipFiles(zippedFiles, workingDirectory);

        // add all the unzipped files back into the file list
        files.addAll(unzippedFiles);

        // open unzipped inputFiles
        openFiles(files);
    }

    private List<File> findZippedFiles(List<File> files) {
        // separate files to unzipped and zipped
        List<File> zippedFiles = new ArrayList<File>();

        for (File file : files) {
            if (isGzipFile(file)) {
                zippedFiles.add(file);
            }
        }

        return zippedFiles;
    }


    /**
     * Check whether a file is gzip file based its extension.
     *
     * @param file input file
     * @return boolean true means it is a gzip file
     */
    private boolean isGzipFile(File file) {
        return file.getName().endsWith(".gz");
    }

    /**
     * <code> openFiles </code> opens a list of files.
     *
     * @param files files to open
     */
    private void openFiles(List<File> files) {

        long fileSizeThreshold = Long.parseLong(context.getProperty("memory.mzidentml.file.threshold"));

        Map<File, Class> openFiles = new HashMap<File, Class>();
        List<File> mzIdentMLFiles = new ArrayList<File>();

        for (File selectedFile : files) {
            // check the file type
            Class classType = null;
            try {
                classType = getFileType(selectedFile);
            } catch (IOException e1) {
                logger.error("Failed to check the file type", e1);
            }

            if (classType != null) {
                if (MzIdentMLControllerImpl.isValidFormat(selectedFile)) {
                    mzIdentMLFiles.add(selectedFile);
                } else {
                    openFiles.put(selectedFile, classType);
                }
            }
        }

        // Open all mzIdentML Files
        if (!mzIdentMLFiles.isEmpty()) {
            for (File mzIdentML : mzIdentMLFiles) {
                String msg = "Opening " + mzIdentML.getName();

                Set<File> peakFiles = openFiles.keySet();
                OpenFileTask newTask = (isFileToBig(mzIdentML, fileSizeThreshold)) ?
                        new OpenFileTask(mzIdentML, new ArrayList<File>(peakFiles), MzIdentMLControllerImpl.class, msg, msg, false) :
                        new OpenFileTask(mzIdentML, new ArrayList<File>(peakFiles), MzIdentMLControllerImpl.class, msg, msg, true);

                TaskUtil.startBackgroundTask(newTask);
            }
        } else {
            // Open the rest of the selected files
            for (File selectedFile : openFiles.keySet()) {
                String msg = "Opening " + selectedFile.getName();
                OpenFileTask newTask = new OpenFileTask(selectedFile, openFiles.get(selectedFile), msg, msg);
                TaskUtil.startBackgroundTask(newTask);
            }
        }
    }


    private List<File> unzipFiles(List<File> zippedFiles, File outputFolder) throws IOException {
        List<File> unzippedFiles = new ArrayList<File>();

        for (File inputFile : zippedFiles) {
            FileInputStream fis = null;
            GZIPInputStream gs = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fis = new FileInputStream(inputFile);
                gs = new GZIPInputStream(fis);

                String outputFile = outputFolder + File.separator + inputFile.getName().replace(".gz", "");
                fos = new FileOutputStream(outputFile);
                bos = new BufferedOutputStream(fos, 2048);
                byte data[] = new byte[2048];
                int count;
                while ((count = gs.read(data, 0, 2048)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();

                unzippedFiles.add(new File(outputFile));
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

        return unzippedFiles;
    }

        /**
         * Check the file type
         *
         * @param file input file
         * @return Class    the class type of the data access controller
         * @throws IOException exception while checking the file type
         */

    private Class getFileType(File file) throws IOException {
        Class classType = null;

        // check file type
        if (MzMLControllerImpl.isValidFormat(file)) {
            classType = MzMLControllerImpl.class;
        } else if (PrideXmlControllerImpl.isValidFormat(file)) {
            classType = PrideXmlControllerImpl.class;
        } else if (MzIdentMLControllerImpl.isValidFormat(file)) {
            classType = MzIdentMLControllerImpl.class;
        } else if (MzXmlControllerImpl.isValidFormat(file)) {
            classType = MzXmlControllerImpl.class;
        } else if (MzDataControllerImpl.isValidFormat(file)) {
            classType = MzDataControllerImpl.class;
        } else if (PeakControllerImpl.isValidFormat(file) != null) {
            classType = PeakControllerImpl.class;
        } else if (MzTabControllerImpl.isValidFormat(file)) {
            classType = MzTabControllerImpl.class;
        } else {
            GUIUtilities.error(Desktop.getInstance().getMainComponent(),
                    "<html><h4>The files you selected are not in supported format.</h4> The formats are supported by " +
                            "PRIDE Inspector are: <br> <b> PRIDE XML </b> <br> <b> mzIdentML </b> <br> <b> mzML </b> </html>",
                    "Wrong File Format");
        }

        return classType;
    }

    private Boolean isFileToBig(File file, long fileSizeThreshold) {
        long length = file.length();
        return (length / (1024 * 1024)) > fileSizeThreshold;
    }

    @Override
    public void process(TaskEvent<List<File>> listTaskEvent) {
        openFiles(listTaskEvent.getValue());
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<Void> voidTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}