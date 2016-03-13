package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetailList;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.MyProjectDownloadDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.PrideLoginDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 13/03/2016
 */
public class OpenMyFolderProjectTask extends TaskAdapter<Void, Void> {

    private static final String DEFAULT_TASK_TITLE = "Open Folder Results";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open Folder Results";
    /**
     * list of proteomexchange accession
     */
    private String folder;

    private List<File>files;


    public OpenMyFolderProjectTask(String folder) {
        this.folder = folder;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);

    }

    @Override
    protected Void doInBackground() throws Exception {
        // retrieve submission details
        getFileMetadata(folder);

        return null;
    }

    protected void getFileMetadata(String folder) {
        File folderFile = new File(folder);
        if(folderFile != null && folderFile.isDirectory() && folderFile.listFiles().length > 0){
            // open downloaded files
            OpenFileAction openFileAction = new OpenFileAction(null, null, Arrays.asList(folderFile.listFiles()), true);
            openFileAction.actionPerformed(null);
        }
    }

}
