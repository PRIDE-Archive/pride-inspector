package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class OpenMyAssayTask extends OpenMyProjectTask {

    public OpenMyAssayTask(String accession, String username, char[] password) {
        super(accession, username, password);
    }


    @Override
    protected void getFileMetadata(String un, char[] pwd, String accession) {
        Task task = new GetMyAssayFilesMetadataTask(un, pwd, accession);
        task.addTaskListener(this);
        TaskUtil.startBackgroundTask(task);
    }
}
