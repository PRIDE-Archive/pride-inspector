package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetailList;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;

import java.util.Arrays;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class GetMyProjectFilesMetadataTask extends Task<FileDetailList, String> {
    private static final String DEFAULT_TASK_TITLE = "Getting project files";
    private static final String DEFAULT_TASK_DESCRIPTION = "Getting project files";

    private String accession;
    private RestTemplate restTemplate;
    private HttpEntity<String> requestEntity;

    /**
     * Constructor
     *
     * @param userName pride user name
     * @param password pride password
     */
    public GetMyProjectFilesMetadataTask(String userName, char[] password, String projectAccession) {
        this.accession = projectAccession;
        final HttpHeaders headers = getHeaders(userName, password);
        this.requestEntity = new HttpEntity<>(headers);
        this.restTemplate = new RestTemplate();

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    private HttpHeaders getHeaders(String userName, char[] password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        if (userName != null && password != null) {
            String auth = userName + ":" + new String(password);
            byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
            headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        }

        return headers;
    }

    @Override
    protected FileDetailList doInBackground() throws Exception {
        String fileDownloadUrl = getFileDownloadUrl();

        try {
            ResponseEntity<FileDetailList> entity = restTemplate.exchange(fileDownloadUrl, HttpMethod.GET, requestEntity, FileDetailList.class, accession);
            return entity.getBody();
        } catch (RestClientException ex) {
            publish("Failed to retrieve file details for project " + accession);
            throw ex;
        }
    }

    protected String getFileDownloadUrl() {
        DesktopContext context = PrideInspector.getInstance().getDesktopContext();
        return context.getProperty("prider.project.file.metadata.url");
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(FileDetailList results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}