package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.web.service.model.assay.AssayDetail;
import uk.ac.ebi.pride.archive.web.service.model.assay.AssayDetailList;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;

import javax.swing.*;
import java.util.List;

/**
 * Get assay metadata via PRIDE Archive web service
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetAssayMetadataTask extends Task<Void, AssayDetail> {

    private static final String DEFAULT_TASK_TITLE = "Retrieving assay summary";
    private static final String DEFAULT_TASK_DESCRIPTION = "Retrieving assay summary";

    private final RestTemplate restTemplate;
    private final DesktopContext context;
    private final String projectAccession;
    private final int numberOfAssays;

    /**
     * Constructor
     */
    public GetAssayMetadataTask(String projectAccession, int numberOfAssays) {
        this.restTemplate = new RestTemplate();
        this.context = PrideInspector.getInstance().getDesktopContext();
        this.projectAccession = projectAccession;
        this.numberOfAssays = numberOfAssays;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);

    }

    @Override
    protected Void doInBackground() throws Exception {
        if (numberOfAssays == 0) {
            return null;
        }

        try {
            String assayMetadataUrl = context.getProperty("prider.assay.metadata.url");

            AssayDetailList assayDetailList = restTemplate.getForObject(assayMetadataUrl, AssayDetailList.class, projectAccession);

            List<AssayDetail> assayDetails = assayDetailList.getList();
            publish(assayDetails.toArray(new AssayDetail[assayDetails.size()]));

        } catch (Exception ex) {
            EDTUtils.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(PrideInspector.getInstance().getMainComponent(),
                            "Failed to retrieve assay details from PRIDE for project " +
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
