package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.access.SilentSingleAssayFileOpener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class provide basic method for downloading files
 *
 * @author Rui Wang
 * @version $Id$
 */
abstract class FileDownloadTask extends TaskAdapter<Void, String> {

    public static final Logger logger = LoggerFactory.getLogger(FileDownloadTask.class);

    private static final String DEFAULT_TASK_TITLE = "File download in progress";
    private static final String DEFAULT_TASK_DESCRIPTION = "File download in progress";

    protected final List<FileDetail> filesToDownload;

    protected final boolean openFile;

    protected final File outputFolder;

    /**
     * Total file size need to be uploaded
     */
    protected long totalFileSize;

    protected final PrideInspectorContext context;

    /**
     * Constructor used for a new submission
     */
    public FileDownloadTask(List<FileDetail> filesToDownload, File outputFolder, boolean openFile) {
        this.filesToDownload= filesToDownload;
        this.outputFolder = outputFolder;
        this.openFile = openFile;
        this.totalFileSize = calculateTotalFileSize();
        this.context = (PrideInspectorContext)PrideInspector.getInstance().getDesktopContext();

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    private long calculateTotalFileSize() {
        long fileSize = 0;

        for (FileDetail fileDetail : filesToDownload) {
            fileSize += fileDetail.getFileSize();
        }

        return fileSize;
    }

    @Override
    protected void cancelled() {
        publish("Download has been cancelled");
    }


    protected void openFiles() throws IOException {
        List<File> filesToOpen = new ArrayList<File>();

        for (FileDetail fileDetail : filesToDownload) {
            File downloadedFile = new File(outputFolder.getAbsolutePath(), fileDetail.getFileName());
            if (downloadedFile.exists() && (fileDetail.getFileType().equals(ProjectFileType.RESULT) || fileDetail.getFileType().equals(ProjectFileType.PEAK))) {
                filesToOpen.add(downloadedFile);
            }
        }

        SilentSingleAssayFileOpener fileOpener = new SilentSingleAssayFileOpener();
        fileOpener.open(filesToOpen, outputFolder);
    }

    protected void setDownloadProgress(long bytesTransferred) {
        int progress = (int) ((bytesTransferred * 1.0 / totalFileSize)*100);
        setProgress(progress);
    }
}