package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;

import java.util.*;

/**
 * Connect to PRIDE server for reviewer download.
 * <p/>
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 13:51:20
 */
public class GetPrideExperimentDetailTask extends AbstractConnectPrideTask {
    private static final Logger logger = LoggerFactory.getLogger(GetPrideExperimentDetailTask.class);

    private String user;
    private String password;
    private Set<Comparable> accessions;

    public GetPrideExperimentDetailTask(String user, String password) {
        this(user, password, null);
    }

    public GetPrideExperimentDetailTask(String user, String password, Collection<Comparable> accessions) {
        this.user = user;
        this.password = password;
        if (accessions != null) {
            this.accessions = new HashSet<Comparable>();
            this.accessions.addAll(accessions);
        }

        String msg = "Download PRIDE Experiment";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {

        // create a http connection
        DesktopContext context = Desktop.getInstance().getDesktopContext();

        List<Map<String, String>> metadata = null;

        try {
            // login for meta data
            String url = buildPrideMetaDataDownloadURL(context.getProperty("pride.experiment.download.url"), accessions, user, password);

            // download experiment meta data
            metadata = downloadMetaData(url);

            // this is important for cancelling
            checkInterruption();
        } catch (InterruptedException ex) {
            logger.warn("Download session has been cancelled");
        }

        return metadata;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.currentThread().interrupted()) {
            throw new InterruptedException();
        }
    }
}
