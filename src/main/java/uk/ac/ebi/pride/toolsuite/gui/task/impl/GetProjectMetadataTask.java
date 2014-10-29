package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectSummary;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectSummaryList;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;

import javax.swing.*;
import java.util.List;

/**
 * Get project metadata via PRIDE Archive web service
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetProjectMetadataTask extends Task<Void, ProjectSummary> {

    private static final String DEFAULT_TASK_TITLE = "Retrieving project summary";
    private static final String DEFAULT_TASK_DESCRIPTION = "Retrieving project summary";
    public static final int BATCH_SIZE = 100;

    private final RestTemplate restTemplate;
    private final DesktopContext context;
    private final String searchTerm;

    /**
     * Constructor
     */
    public GetProjectMetadataTask(String searchTerm) {
        this.restTemplate = new RestTemplate();
        this.context = PrideInspector.getInstance().getDesktopContext();
        this.searchTerm = searchTerm;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);

    }

    @Override
    protected Void doInBackground() throws Exception {

        // get the number of projects
        try {
            int numberOfProjects = getNumberOfProjects();
            int pages = (int) Math.ceil((numberOfProjects * 1.0) / BATCH_SIZE);

            for (int i = 1; i <= pages; i++) {
                List<ProjectSummary> projectDetails = getProjectDetails(i);
                publish(projectDetails.toArray(new ProjectSummary[projectDetails.size()]));
            }
        } catch (Exception ex) {
            EDTUtils.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(PrideInspector.getInstance().getMainComponent(),
                            "Failed to retrieve project details from PRIDE, please make sure that you have Internet connection.",
                            "Access error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        return null;
    }

    private List<ProjectSummary> getProjectDetails(int page) {
        String restriction = (searchTerm == null || searchTerm.length() == 0) ? "" : "q=" + searchTerm + "&";
        String projectMetadataUrl = context.getProperty("prider.project.metadata.url") + "?" + restriction + "show=" + BATCH_SIZE + "&page=" + page;

        ProjectSummaryList projectDetailList = restTemplate.getForObject(projectMetadataUrl, ProjectSummaryList.class);

        return projectDetailList.getList();
    }

    private int getNumberOfProjects() {
        String projectCountUrl = context.getProperty("prider.project.count.url");
        Integer count = restTemplate.getForObject(projectCountUrl, Integer.class);

        return count == null ? 0 : count;
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