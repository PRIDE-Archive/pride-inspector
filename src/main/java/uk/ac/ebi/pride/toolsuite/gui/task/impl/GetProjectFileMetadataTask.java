package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetailList;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.utilities.util.Tuple;

import javax.swing.*;
import java.util.List;

/**
 * Get file details belong to the assay
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetProjectFileMetadataTask extends Task<Void, Tuple<FileDetail, Boolean>> {

    private static final String DEFAULT_TASK_TITLE = "Retrieving project file summary";
    private static final String DEFAULT_TASK_DESCRIPTION = "Retrieving project file summary";

    private final RestTemplate restTemplate;
    private final DesktopContext context;
    private final String projectAccession;

    /**
     * Constructor
     */
    public GetProjectFileMetadataTask(String projectAccession) {
        this.restTemplate = new RestTemplate();
        this.context = PrideInspector.getInstance().getDesktopContext();
        this.projectAccession = projectAccession;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            String assayFileUrl = context.getProperty("prider.project.file.url");

            FileDetailList assayFileList = restTemplate.getForObject(assayFileUrl, FileDetailList.class, projectAccession);

            List<FileDetail> assayFiles = assayFileList.getList();
            for (FileDetail assayFile : assayFiles) {
                publish(new Tuple<>(assayFile, true));
            }
        } catch (Exception ex) {
            EDTUtils.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(PrideInspector.getInstance().getMainComponent(),
                            "Failed to retrieve assay file details from PRIDE for assay " +
                                    projectAccession,
                            "Access error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        return null;
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(Void results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}

