package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.*;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.mzidentml.MzIdMsDialog;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.OpenGzippedFileTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.toolsuite.gui.utils.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * OpenFileAction opens files supported by PRIDE Viewer
 * so far: mzML, mzid,  PRIDE XML
 * <p/>
 * @author rwang
 * @author ypriverol
 *
 */
public class OpenFileAction extends PrideAction implements TaskListener<Void, File> {

    private static final Logger logger = LoggerFactory.getLogger(OpenFileAction.class);

    private static final String PROTEIN_AMBIGUITY_GROUP_XML_TAG = "ProteinAmbiguityGroup";

    private List<File> inputFilesToOpen;

    private PrideInspectorContext context;

    private File path;

    private boolean skipFiles = false;

    public OpenFileAction(String name, Icon icon) {
        this(name, icon, null, false);
        setAccelerator(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
    }

    public OpenFileAction(String name, Icon icon, List<File> files, boolean skipFiles) {
        super(name, icon);
        this.skipFiles = skipFiles;
        inputFilesToOpen = files == null ? null : new ArrayList<File>(files);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        // create a open file dialog
        List<File> filesToOpen = inputFilesToOpen == null ? createFileOpenDialog() : inputFilesToOpen;

        // unzip zipped files
        unzipFiles(filesToOpen);

        // show warning messages if the file is too big
        boolean choice = showBigFileWarningMessage(filesToOpen);

        if (choice) {
            // open unzipped files
            openFiles(filesToOpen);
        }
    }

    /**
     * Show a warning message if the file size is over certain threshold
     *
     * @param files a list of input files
     * @return boolean open files if returns true
     */
    private boolean showBigFileWarningMessage(List<File> files) {
        boolean tooBig = false;
        boolean toOpen = true;

        // get the threshold for file size first
        long fileSizeThreshold = Long.parseLong(context.getProperty("open.file.threshold"));

        // check the size of each file
        for (File file : files) {
            // get the length in bytes
            if(isFileToBig(file, fileSizeThreshold)){
                tooBig = true;
                break;
            }
        }

        if (tooBig) {
            // check whether the user still want to open
            Object[] options = {"Continue"};
            int n = JOptionPane.showOptionDialog(Desktop.getInstance().getMainComponent(),
                    "Selected File is over " + fileSizeThreshold + "M in size, it will take longer to open fhe file.",
                    "Big File Found",
                    JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            toOpen = (n == 0);
        }

        return toOpen;
    }

    /**
     * Unzip files
     *
     * @param files a list of input files
     */
    private void unzipFiles(List<File> files) {
        if (hasGzipFiles(files)) {
            // separate files to unzipped and zipped
            List<File> zippedFiles = new ArrayList<File>();

            for (File file : files) {
                if (isGzipFile(file)) {
                    zippedFiles.add(file);
                }
            }

            // remove all the zipped files from the list
            files.removeAll(zippedFiles);

            // check whether the user want to unzip
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(Desktop.getInstance().getMainComponent(),
                    "Would you like to unzip compressed files before loading?",
                    "Gzip Files Found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            if (n == JOptionPane.YES_OPTION) {
                // ask for the path to save unzipped files
                JFileChooser ofd = new JFileChooser(context.getOpenFilePath());
                ofd.setDialogTitle("Select folder to save unzipped files");
                ofd.setDialogType(JFileChooser.OPEN_DIALOG);
                ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                ofd.setMultiSelectionEnabled(false);

                int result = ofd.showOpenDialog(Desktop.getInstance().getMainComponent());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File path = ofd.getSelectedFile();
                    this.path = path;
                    // start new tasks to unzip files
                    openGzippedFiles(zippedFiles, path.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Check whether a list of files contains gzip files.
     *
     * @param files a list of input files
     * @return boolean true means there is gzip files
     */
    private boolean hasGzipFiles(List<File> files) {
        boolean hasGzip = false;

        for (File file : files) {
            if (isGzipFile(file)) {
                hasGzip = true;
                break;
            }
        }

        return hasGzip;
    }

    private boolean hasGzipFiles(Map<File, List<File>> files){
        for(File file: files.keySet()){
            List<File> filesToOpen = files.get(file);
            if(filesToOpen != null && filesToOpen.size() > 0){
                for(File fileToOpen: filesToOpen)
                    if(isGzipFile(fileToOpen))
                        return true;
            }

        }
        return false;
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
     * create a file open dialog if not input files has been specified
     *
     * @return List<File>  a list of input files
     */
    private List<File> createFileOpenDialog() {

        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/mzXML/mzid/PRIDE xml Files", true, null, true,
                Constants.MZIDENT_FILE,
                Constants.MZIDENT_FILE_LARGE,
                Constants.MZML_FILE,
                Constants.XML_FILE,
                Constants.MZXML_FILE,
                Constants.MGF_FILE,
                Constants.MS2_FILE,
                Constants.PKL_FILE,
                Constants.DTA_FILE,
                Constants.APL_FILE,
                Constants.MZTAB_FILE,
                Constants.CDF_FILE,
                Constants.GZIPPED_FILE);

        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);

        List<File> filesToOpen = new ArrayList<File>();

        // check the selection results from open file dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }
        return filesToOpen;
    }

    /**
     * <code> openFiles </code> opens a list of files.
     *
     * @param files files to open
     */
    @SuppressWarnings("unchecked")
    private void openFiles(List<File> files) {

        long fileSizeThreshold = Long.parseLong(context.getProperty("memory.mzidentml.file.threshold"));

        Map<File, Class> openFiles = new HashMap<File, Class>();
        Map<File, List<File>> mzIdentMLFiles = new HashMap<File, List<File>>();

        for (File selectedFile : files) {
            // check the file type
            Class classType = null;
            try {
                classType = getFileType(selectedFile, skipFiles);
            } catch (IOException e1) {
                logger.error("Failed to check the file type", e1);
            }

            if (classType != null) {
                if (MzIdentMLControllerImpl.isValidFormat(selectedFile)) {
                    mzIdentMLFiles.put(selectedFile, null);
                } else {
                    openFiles.put(selectedFile, classType);

                }
            }
        }

        for(File msFile: openFiles.keySet()){
            if(mzIdentMLFiles != null && isSpectraFile(openFiles.get(msFile))){
                for(File file : mzIdentMLFiles.keySet()){
                    List<File> msFiles = mzIdentMLFiles.get(file);
                    if(msFiles == null)
                        msFiles = new ArrayList<File>();
                    msFiles.add(msFile);
                    mzIdentMLFiles.put(file, msFiles);
                }
            }
        }

        // detect protein grouping
        boolean runProteinInference = false;
        
        if (mzIdentMLFiles.size() > 0) {
            List<File> mzIdentMLWithoutProteinGroups = new ArrayList<File>();
            for (File mzIdentMLFile : mzIdentMLFiles.keySet()) {
                if (!hasProteinGroups(mzIdentMLFile)) {
                    mzIdentMLWithoutProteinGroups.add(mzIdentMLFile);
                }
            }

            if (mzIdentMLWithoutProteinGroups.size() > 0) {
                String[] options = new String[]{"No", "Compute Protein Inference"};
                int option = JOptionPane.showOptionDialog(null,
                        "<html> <b>Protein grouping information missing from mzIdentML file. " +
                                "</b><br/> <br/> Would you like to run the Protein inference Algorithm </html>" , "mzIdentML",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[1]);

                if (option == 0) {
                    runProteinInference = false;
                } else{
                    runProteinInference = true;
                }
            }
        }

        // load peak list files
        if (mzIdentMLFiles.size() > 0 && hasFiles(mzIdentMLFiles)) {
            int option = JOptionPane.showConfirmDialog(null,
                    "Would you like to load spectrum files related to the mzIdentML files?", "mzIdentML", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                MzIdMsDialog mzidDialog = new MzIdMsDialog(Desktop.getInstance().getMainComponent(), new ArrayList<File>(mzIdentMLFiles.keySet()));
                mzidDialog.setModal(true);
                mzidDialog.setVisible(true);
                mzIdentMLFiles.putAll(mzidDialog.getMzIdentMlMap());
            }
        }


        // Open all mzIdentML Files
        if (!mzIdentMLFiles.isEmpty()) {
            // unzip zipped files
            boolean hasGzipFiles = hasGzipFiles(mzIdentMLFiles);

            // check whether the user want to unzip
            if(hasGzipFiles &&  this.path == null){
                Object[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(Desktop.getInstance().getMainComponent(),
                        "Would you like to unzip compressed files before loading?",
                        "Gzip Files Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);

                if (n == JOptionPane.YES_OPTION) {
                    // ask for the path to save unzipped files
                    JFileChooser ofd = new JFileChooser(context.getOpenFilePath());
                    ofd.setDialogTitle("Select folder to save unzipped files");
                    ofd.setDialogType(JFileChooser.OPEN_DIALOG);
                    ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    ofd.setMultiSelectionEnabled(false);

                    int result = ofd.showOpenDialog(Desktop.getInstance().getMainComponent());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        path = ofd.getSelectedFile();
                    }
                }
            }else if(!hasGzipFiles){
                path = null;
            }

            for (File mzIdentML : mzIdentMLFiles.keySet()) {
                String msg = "Opening " + mzIdentML.getName();
                List<File> openNewFiles = mzIdentMLFiles.get(mzIdentML);

                OpenFileTask newTask;
                if (mzIdentMLFiles.get(mzIdentML) != null && mzIdentMLFiles.get(mzIdentML).size() > 0) {
                    newTask = (isFileToBig(mzIdentML,fileSizeThreshold))?
                            new OpenFileTask(mzIdentML, openNewFiles, MzIdentMLControllerImpl.class, msg, msg,false, !hasProteinGroups(mzIdentML) && runProteinInference, this.path):
                            new OpenFileTask(mzIdentML, openNewFiles, MzIdentMLControllerImpl.class, msg, msg,true, !hasProteinGroups(mzIdentML) && runProteinInference, this.path);
                } else {
                    newTask = (isFileToBig(mzIdentML,fileSizeThreshold))?
                            new OpenFileTask(mzIdentML, null, MzIdentMLControllerImpl.class, msg, msg,false, !hasProteinGroups(mzIdentML) && runProteinInference, this.path):
                            new OpenFileTask(mzIdentML, null, MzIdentMLControllerImpl.class, msg, msg,true, !hasProteinGroups(mzIdentML) && runProteinInference, this.path);
                }
                TaskUtil.startBackgroundTask(newTask);
            }
        }

        // Open the rest of the selected files
        for (File selectedFile : openFiles.keySet()) {
            String msg = "Opening " + selectedFile.getName();
            OpenFileTask newTask = new OpenFileTask(selectedFile, openFiles.get(selectedFile), msg, msg);
            TaskUtil.startBackgroundTask(newTask);
        }
    }

    private boolean hasFiles(Map<File, List<File>> mzIdentMLFiles) {
        if(mzIdentMLFiles == null)
            return true;
        for(File mzIdentML: mzIdentMLFiles.keySet()){
            if(mzIdentMLFiles.get(mzIdentML) == null || mzIdentMLFiles.get(mzIdentML).size() == 0)
                return true;
        }
        return false;

    }

    private boolean hasProteinGroups(File mzIdentMLFile) {
        boolean proteinGroupPresent = false;

        try {
            //Todo: We should look for a different way to see if PROTEIN GROUPS ARE PRESENT
            if (FileUtils.tail(mzIdentMLFile, 10000).contains(PROTEIN_AMBIGUITY_GROUP_XML_TAG)) {
                proteinGroupPresent = true;
            }
        } catch (IOException e) {
            String msg = "Failed to read the end of the mzIdentML file: " + mzIdentMLFile.getAbsolutePath();
            logger.error(msg, e);
        }

        return proteinGroupPresent;
    }

    private void openGzippedFiles(List<File> files, String path) {

        OpenGzippedFileTask newTask = new OpenGzippedFileTask(files, path);

        // listen this this task
        newTask.addTaskListener(this);

        TaskUtil.startBackgroundTask(newTask);
    }

    /**
     * Check the file type
     *
     * @param file input file
     * @return Class    the class type of the data access controller
     * @throws IOException exception while checking the file type
     */
    private Class getFileType(File file, boolean skipFiles) throws IOException {
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
        } else if (NetCDFControllerImpl.isValidFormat(file)){
           classType = NetCDFControllerImpl.class;
        } else if (MzTabControllerImpl.isValidFormat(file)){
            classType = MzTabControllerImpl.class;
        }else if (!skipFiles){
            GUIUtilities.error(Desktop.getInstance().getMainComponent(),
                    "<html><h4>The files you selected are not in supported format.</h4> The formats are supported by PRIDE Inspector are: <br> <b> PRIDE XML </b> <br> <b> mzIdentML </b> <br> <b> mzML </b> </html>",
                    "Wrong File Format");
        }

        return classType;
    }

    boolean isSpectraFile(Class classType){
        return (classType == MzMLControllerImpl.class || classType == MzXmlControllerImpl.class
        || classType == MzDataControllerImpl.class ||
           classType == PeakControllerImpl.class ||
           classType == NetCDFControllerImpl.class);
    }

    private Boolean isFileToBig(File file, long fileSizeThreshold){
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
