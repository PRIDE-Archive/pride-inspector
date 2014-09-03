package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Download a list of submitted project files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetPrideFileTask extends TaskAdapter<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(GetPrideFileTask.class);

    private static final int BUFFER_SIZE = 1024;

    private List<SubmissionFileDetail> submissionEntries;
    private File folder;
    private String user;
    private String password;
    private boolean toOpenFile;

    public GetPrideFileTask(List<SubmissionFileDetail> submissionEntries,
                            File path,
                            String usr,
                            String pwd,
                            boolean toOpenFile) {
        this.submissionEntries = submissionEntries;
        this.folder = path;
        this.user = usr;
        this.password = pwd;
        this.toOpenFile = toOpenFile;

        String msg = "Downloading PRIDE submission";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected Void doInBackground() throws Exception {
        if (folder != null) {
            try {

                for (SubmissionFileDetail submissionEntry : submissionEntries) {
                    // create a http connection
                    DesktopContext context = Desktop.getInstance().getDesktopContext();

                    // initialize the download
                    String url = buildFileDownloadUrl(context.getProperty("prider.file.download.url"), submissionEntry.getProjectAccession(), submissionEntry.getFileName());

                    // get output file path
                    File output = new File(folder.getAbsolutePath() + File.separator + submissionEntry.getFileName());

                    // download submission file
                    String host = context.getProperty("prider.host.url");
                    int port = Integer.parseInt(context.getProperty("prider.host.port"));
                    output = downloadFile(host, port, url, output, user, password, submissionEntry.getFileSize());

                    // open file
                    if (toOpenFile && output != null) {
                        OpenFileAction openFileAction = new OpenFileAction(null, null, Arrays.asList(output));
                        openFileAction.actionPerformed(null);
                    }

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Experiment downloading has been interrupted");
            }
        }
        return null;
    }

    private String buildFileDownloadUrl(String url, String accession, String fileName) throws UnsupportedEncodingException {
        url = url.replace("{accession}", accession);
        url = url.replace("{file}", URLEncoder.encode(fileName, "UTF-8").replace("+", "%20"));
        return url;
    }

    private File downloadFile(String host, int port, String url, File output, String userName, String password, long fileSize) throws IOException {
        HttpHost target = new HttpHost(host, port, "http");

        // Create an instance of HttpClient.
        CloseableHttpClient client = HttpClients.createDefault();

        if (userName != null && password != null) {
            // credential provider
            BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(new AuthScope(AuthScope.ANY),
                                                    new UsernamePasswordCredentials(userName, password));

            client = HttpClients.custom().setDefaultCredentialsProvider(basicCredentialsProvider).build();

        }

        HttpClientContext httpClientContext = HttpClientContext.create();
        BasicAuthCache authCache = new BasicAuthCache();
        BasicScheme basicScheme = new BasicScheme();
        authCache.put(target, basicScheme);
        httpClientContext.setAuthCache(authCache);

        // Create a method instance.
        HttpGet method = new HttpGet(url);
        CloseableHttpResponse response = client.execute(target, method, httpClientContext);

        BufferedOutputStream boutStream = null;
        InputStream inputStream = null;
        try {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();

                try {
                    Header contentTypeHeader = response.getFirstHeader("Content-Type");
                    if (contentTypeHeader != null && "application/x-gzip".equalsIgnoreCase(contentTypeHeader.getValue())) {
                        inputStream = new GZIPInputStream(inputStream);
                    }

                    output = new File(output.getAbsolutePath().replace(".gz", ""));

                    boutStream = new BufferedOutputStream(new FileOutputStream(output), BUFFER_SIZE);

                    copyStream(inputStream, boutStream, fileSize);

                    publish("Download has finished");

                } finally {
                    inputStream.close();
                }
            }

            return output;
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning:Wrong login credentials");
            } else if (msg.contains("400")) {
                logger.warn("Fail to connect to the remote server: {}", msg);
                publish("Warning:Fail to connect to the remote server");
            } else {
                logger.warn("Unexpected error: " + ex.getMessage());
                publish("Unexpected error: " + ex.getMessage());
            }
        } finally {
            if (boutStream != null) {
                boutStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return output;
    }

    private void copyStream(InputStream inputStream, BufferedOutputStream outputStream, long fileSize) throws IOException {
        int readCount = 0;
        byte data[] = new byte[BUFFER_SIZE];
        int count;

        while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
            readCount += count;
            outputStream.write(data, 0, count);

            float ratio = ((float) readCount) / fileSize;
            int progress = Math.abs(Math.round(ratio * 100));
            if (progress >= 100) {
                progress = 99;
            }

            if (progress > getProgress()) {
                setProgress(progress);
            }
        }
        outputStream.flush();
        setProgress(100);
    }
}
