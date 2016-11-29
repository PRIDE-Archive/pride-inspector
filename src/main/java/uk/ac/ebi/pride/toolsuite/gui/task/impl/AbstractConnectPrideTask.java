package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

/**
 * Abstract class to provide methods to connect pride web services
 * <p/>
 * User: rwang
 * Date: 14/09/2011
 * Time: 13:46
 */
public abstract class AbstractConnectPrideTask extends TaskAdapter<List<Map<String, String>>, String> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnectPrideTask.class);
    private static final int BUFFER_SIZE = 1024;

    /**
     * login to pride to download
     *
     * @param baseUrl   base url for http request
     * @param accession pride experiment accession
     * @param user      user name
     * @param password  password
     */
    String buildExperimentDownloadURL(String baseUrl, Comparable accession,
                                      String user, String password) {
        StringBuilder cmd = new StringBuilder();

        try {
            cmd.append(baseUrl);
            cmd.append("?");
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=downloadFile");
            cmd.append("&accession=");
            cmd.append(accession);
        } catch (IOException ex) {
            logger.warn("Fail to construct url for downloading PRIDE experiment: {}", ex.getMessage());
        }

        return cmd.toString();
    }

    /**
     * log in for ProteomeXchange meta data information.
     *
     * @param baseUrl    base url for http request
     * @param accessions a list of pride experiment accessions
     * @param user       user name
     * @param password   password
     */
    String buildPxMetaDataDownloadURL(String baseUrl,
                                      Collection<Comparable> accessions,
                                      String user,
                                      String password) {
        StringBuilder cmd = new StringBuilder();

        try {
            cmd.append(baseUrl);
            cmd.append("?");
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            if (accessions != null && !accessions.isEmpty()) {
                cmd.append("&pxaccession=");
                String accStr = "";
                for (Comparable accession : accessions) {
                    accStr += accession.toString() + Constants.COMMA;
                }
                cmd.append(accStr);
            }
        } catch (IOException ex) {
            logger.warn("Fail to construct url for downloading proteoemexchange metadata: {}", ex.getMessage());
        }

        return cmd.toString();
    }

    /**
     * log in for meta data information.
     *
     * @param baseUrl    base url to request for metadata information
     * @param accessions a list of pride experiment accessions
     * @param user       user name
     * @param password   password
     */
    String buildPrideMetaDataDownloadURL(String baseUrl,
                                         Collection<Comparable> accessions,
                                         String user,
                                         String password) {
        StringBuilder cmd = new StringBuilder();

        try {
            cmd.append(baseUrl);
            cmd.append("?");
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=metadata");
            if (accessions != null && !accessions.isEmpty()) {
                cmd.append("&accession=");
                String accStr = "";
                for (Comparable accession : accessions) {
                    accStr += accession.toString() + Constants.COMMA;
                }
                cmd.append(accStr);
            }
        } catch (IOException ex) {
            logger.warn("Fail to send construct url for downloading PRIDE experiment metadata: {}", ex.getMessage());
        }

        return cmd.toString();
    }

    /**
     * Download experiment metadata
     *
     * @param url URL to get experiment metadata
     * @return List<Map<String, String>>   experiment meta data
     */
    List<Map<String, String>> downloadMetaData(String url) {
        BufferedReader in = null;
        List<Map<String, String>> result = new ArrayList<>();
        try {
            HttpResponse httpResponse = doHttpGet(url);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();
            if (statusCode == 200 && httpEntity != null) {
                in = new BufferedReader(new InputStreamReader(httpEntity.getContent()));

                Map<String, String> entry = new HashMap<>();
                String str;
                while ((str = in.readLine()) != null) {
                    str = str.trim();
                    if ("//".equals(str) && !entry.isEmpty()) {
                        result.add(entry);
                        entry = new HashMap<>();
                    } else if (!"".equals(str)) {
                        String[] parts = str.split(Constants.TAB);
                        entry.put(parts[0], parts[1]);
                    }
                }
                in.close();
                publish("Success:Login successful");
            } else {
                if (statusCode == 404) {
                    logger.warn("No experiments found: {}", httpResponse.getStatusLine().getReasonPhrase());
                    publish("Warning:No record found");
                } else {
                    logger.warn("Failed to login: {}", httpResponse.getStatusLine().getReasonPhrase());
                    publish("Warning:Failed to login");
                }
            }
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning:Wrong login credentials");
            } else {
                logger.warn("Fail to connect to the remote server: {}", msg);
                publish("Warning:Fail to connect the server");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }

        return result;
    }


    /**
     * Perform a http get request using a given url
     */
    private HttpResponse doHttpGet(String url) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet);
    }
}
