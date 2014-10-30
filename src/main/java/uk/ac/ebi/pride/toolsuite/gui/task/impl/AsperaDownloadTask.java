package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import com.asperasoft.faspmanager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.aspera.AsperaFileUploader;
import uk.ac.ebi.pride.toolsuite.gui.component.utils.OSDetector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.FileUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class AsperaDownloadTask extends TaskAdapter<Void, String> implements TransferListener {

    public static final Logger logger = LoggerFactory.getLogger(AsperaDownloadTask.class);

    private final List<FileDetail> filesToDownload;

    private final boolean openFile;

    private final File outputFolder;

    /**
     * Total file size need to be uploaded
     */
    private long totalFileSize;

    /**
     * Constructor used for a new submission
     */
    public AsperaDownloadTask(List<FileDetail> filesToDownload, File outputFolder, boolean openFile) {
        this.filesToDownload= filesToDownload;
        this.outputFolder = outputFolder;
        this.openFile = openFile;
        this.totalFileSize = calculateTotalFileSize();
    }

    private long calculateTotalFileSize() {
        long fileSize = 0;

        for (FileDetail fileDetail : filesToDownload) {
            fileSize += fileDetail.getFileSize();
        }

        return fileSize;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // upload via aspera
        asperaUpload();

        // wait for aspera to upload
        waitUpload();

        return null;
    }

    private void waitUpload() throws InitializationException {
        final FaspManager faspManager = FaspManager.getSingleton();
        // this is keep the fasp manager running
        while (faspManager.isRunning()) {
        }
    }

    private void asperaUpload() throws FaspManagerException {

        // choose aspera binary according to operating system
        String ascpLocation = chooseAsperaBinary();

        if (ascpLocation != null) {
            logger.debug("Aspera binary location {}", ascpLocation);

            File executable = new File(ascpLocation);

            // set aspera connection details
            AsperaFileUploader uploader = new AsperaFileUploader(executable);

            // set upload parameters
            XferParams params = AsperaFileUploader.defaultTransferParams();
            params.createPath = true;
            uploader.setTransferParameters(params);

            // add transfer listener
            uploader.setListener(this);

            // start upload
            String[] asperaFilePaths = convertToAsperaDownloadPaths();
            String transferId = null;
            try {
                transferId = uploader.downloadFiles(asperaFilePaths, outputFolder.getAbsolutePath());
            } catch (UnsupportedEncodingException e) {
                final String msg = "Failed to locate aspera private key";
                logger.error(msg, e);
                publish(msg);
            }
            logger.debug("TransferEvent ID: {}", transferId);
        }
    }

    private String[] convertToAsperaDownloadPaths() {
        List<String> filePaths = new ArrayList<String>();

        for (FileDetail fileDetail : filesToDownload) {
            URL downloadLink = fileDetail.getDownloadLink();
            filePaths.add(downloadLink.getPath());
        }

        return filePaths.toArray(new String[filePaths.size()]);
    }

    private String chooseAsperaBinary() {
        //detect Operating System
        final OSDetector.OS os = OSDetector.getOS();
        final DesktopContext appContext = PrideInspector.getInstance().getDesktopContext();

        //detect jar directory
        try {
            String jarDir = FileUtils.getAbsolutePath();
            // get aspera client binary
            String ascpLocation = "";

            switch (os) {
                case MAC:
                    ascpLocation = appContext.getProperty("aspera.client.mac.binary");
                    break;
                case LINUX_32:
                    ascpLocation = appContext.getProperty("aspera.client.linux32.binary");
                    break;
                case LINUX_64:
                    ascpLocation = appContext.getProperty("aspera.client.linux64.binary");
                    break;
                case WINDOWS:
                    ascpLocation = appContext.getProperty("aspera.client.windows.binary");
                    break;
                default:
                    String msg = "Unsupported platform detected:" + OSDetector.os + " arch: " + OSDetector.arch;
                    logger.error(msg);
                    publish(msg);
            }

            //concatenate jar directory plus relative ascp binaries directory
            return jarDir + File.separator + ascpLocation;
        } catch (UnsupportedEncodingException e) {
            final String msg = "Failed to locate aspera binary";
            logger.error(msg, e);
            publish(msg);
        }

        return null;
    }



    @Override
    protected void cancelled() {
        publish("Download has been cancelled");
    }

    @Override
    public void fileSessionEvent(TransferEvent transferEvent, SessionStats sessionStats, FileInfo fileInfo) {

        switch (transferEvent) {
            case PROGRESS:
                int numberOfDownloadedFiles = (int) sessionStats.getFilesComplete();
                logger.debug("Aspera download in progress");
                logger.debug("Total files: ");
                logger.debug("Total files: " + filesToDownload.size());
                logger.debug("Files downloaded: " + numberOfDownloadedFiles);
                logger.debug("Total file size " + totalFileSize);
                logger.debug("Uploaded file size " + sessionStats.getTotalTransferredBytes());
                int progress = (int) ((sessionStats.getTotalTransferredBytes() * 1.0 / totalFileSize)*100);
                setProgress(progress);
                break;
            case SESSION_STOP:
                FaspManager.destroy();
                publish("Download finished");
                logger.debug("Aspera session stopped");
                setProgress(100);

                // open file
                if (openFile) {
                    openFiles();
                }

                break;
            case SESSION_ERROR:
                logger.debug("Aspera session error: " + transferEvent.getDescription());
                FaspManager.destroy();
                publish("Failed to upload via Aspera: " + transferEvent.getDescription());
                break;
        }
    }

    private void openFiles() {
        List<File> downloadedFiles = new ArrayList<File>();

        for (FileDetail fileDetail : filesToDownload) {
            File downloadedFile = new File(outputFolder.getAbsolutePath(), fileDetail.getFileName());
            if (downloadedFile.exists()) {
                downloadedFiles.add(downloadedFile);
            }
        }

        OpenFileAction openFileAction = new OpenFileAction(null, null, downloadedFiles);
        openFileAction.actionPerformed(null);
    }
}