package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Download files using FTP protocol
 *
 * @author Rui Wang
 * @version $Id$
 */
public class FTPDownloadTask extends FileDownloadTask {

    public static final Logger logger = LoggerFactory.getLogger(AsperaDownloadTask.class);

    /**
     * Constructor used for a new submission
     */
    public FTPDownloadTask(List<FileDetail> filesToDownload, File outputFolder, boolean openFile) {
        super(filesToDownload, outputFolder, openFile);
    }

    private void setupFTP(FTPClient ftpClient) throws IOException {
        String host = context.getProperty("ftp.EBI.host");
        int port = Integer.parseInt(context.getProperty("ftp.EBI.port"));

        ftpClient.connect(host, port);
        ftpClient.login("anonymous", "anonymous");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }


    @Override
    protected Void doInBackground() throws Exception {

        FTPClient ftpClient = new FTPClient();

        try {
            setupFTP(ftpClient);

            long bytesAlreadyRead = 0;

            for (FileDetail fileDetail : filesToDownload) {
                // get download link path
                URL downloadLink = fileDetail.getDownloadLink();
                String downloadLinkPath = downloadLink.getPath();

                String fileName = fileDetail.getFileName();
                File outputFile = new File(outputFolder, fileName);
                BufferedOutputStream outputStream = new BufferedOutputStream((new FileOutputStream((outputFile))));
                InputStream inputStream = ftpClient.retrieveFileStream(downloadLinkPath);

                byte[] bytesArray = new byte[4096];
                int bytesRead;

                while((bytesRead = inputStream.read(bytesArray)) != -1) {
                    outputStream.write(bytesArray, 0, bytesRead);
                    // track progress
                    bytesAlreadyRead += bytesRead;
                    setDownloadProgress(bytesAlreadyRead);
                }

                boolean success = ftpClient.completePendingCommand();
                if (!success) {
                    publish("Failed to download file " + fileName + " via FTP");
                    return null;
                }

                outputStream.close();
                inputStream.close();
            }

            // set progress as completed
            setProgress(100);

            // open files
            if (openFile) {
                openFiles();
            }
        } catch (IOException ex) {
            logger.error("Failed to download files via FTP", ex);
            publish(ex.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                logger.error("Failed to close FTP connection");
            }
        }

        return null;
    }

}
