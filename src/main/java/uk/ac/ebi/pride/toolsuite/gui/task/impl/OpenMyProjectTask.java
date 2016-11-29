package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetailList;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.MyProjectDownloadDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.PrideLoginDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for opening my project
 *
 * @author Rui Wang
 * @version $Id$
 */
public class OpenMyProjectTask extends TaskAdapter<Void, Void> implements TaskListener<FileDetailList, String> {

    private static final String DEFAULT_TASK_TITLE = "Open project";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open project";
    /**
     * list of proteomexchange accession
     */
    private String accession;
    /**
     * PRIDE account user name
     */
    private String username;
    /**
     * PRIDE account password
     */
    private char[] password;


    public OpenMyProjectTask(String accession, String username, char[] password) {
        this.accession = accession;
        this.username = username;
        this.password = password;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);

    }

    @Override
    protected Void doInBackground() throws Exception {
        // retrieve submission details
        getFileMetadata(username, password, accession);

        return null;
    }

    protected void getFileMetadata(String un, char[] pwd, String accession) {
        Task task = new GetMyProjectFilesMetadataTask(un, pwd, accession);
        task.addTaskListener(this);
        TaskUtil.startBackgroundTask(task);
    }

    @Override
    public void succeed(TaskEvent<FileDetailList> listTaskEvent) {
        FileDetailList fileDetailList = listTaskEvent.getValue();

        if (fileDetailList != null) {
            List<SubmissionFileDetail> submissionFileDetails = new ArrayList<>();
            for (FileDetail fileDetail : fileDetailList.getList()) {
                submissionFileDetails.add(new SubmissionFileDetail(fileDetail));
            }

            // open project summary dialog
            MyProjectDownloadDialog myProjectDownloadDialog = new MyProjectDownloadDialog(PrideInspector.getInstance().getMainComponent(), username, password, submissionFileDetails);
            myProjectDownloadDialog.setVisible(true);
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<String>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        Throwable exception = event.getValue();

        if (exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpException = (HttpClientErrorException) exception;
            if (httpException.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                login();
            } else {
                warning();
            }
        } else {
            warning();
        }
    }

    private void login() {
        JFrame frame = Desktop.getInstance().getMainComponent();
        PrideLoginDialog loginDialog = new PrideLoginDialog(frame) {
            @Override
            protected void loginAction() {
                username = getUserName();
                password = getPassword();
                getFileMetadata(username, password, accession);
                dispose();
            }
        };
        loginDialog.setVisible(true);
    }

    private void warning() {
        JOptionPane.showMessageDialog(Desktop.getInstance().getMainComponent(),
                "Sorry, failed to retrieve details for " + accession,
                "Download Warning", JOptionPane.WARNING_MESSAGE);
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
