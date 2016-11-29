package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.task.*;

import java.io.File;
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

    public OpenMyFolderProjectTask(List<File> files){
        this.files = files;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        // retrieve submission details
        File folderFile = null;
        if(folder != null)
             folderFile = new File(folder);
        if(folderFile != null && folderFile.isDirectory() && folderFile.listFiles().length > 0)
            getFileMetadata(Arrays.asList(folderFile));
        else if(files != null)
                getFileMetadata(files);

        return null;
    }

    protected void getFileMetadata(List<File> files) {
            // open downloaded files
            this.files = files;
            OpenFileAction openFileAction = new OpenFileAction(null, null, files, true);
            openFileAction.actionPerformed(null);

    }

}
