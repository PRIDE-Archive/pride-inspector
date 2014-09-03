package uk.ac.ebi.pride.toolsuite.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.url.HttpUtilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check whether there is a new update
 * <p/>
 * User: rwang
 * Date: 11-Nov-2010
 * Time: 17:19:36
 */
public class UpdateChecker {

    public static final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);

    private static final Pattern VERSION_PATTERN = Pattern.compile(".*href=\"([\\d\\.]+)/\".*");

    private final String updateUrl;

    public UpdateChecker(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    /**
     * Check whether there is a new update
     *
     * @return boolean return true if there is a new update.
     */
    public boolean hasUpdate(String currentVersion) {
        boolean toUpdate = false;

        // get the url for checking the update
        BufferedReader reader = null;

        try {
            URL url = new URL(updateUrl);
            // connect to the url
            int response = ((HttpURLConnection) url.openConnection()).getResponseCode();
            if (response != 404) {
                // parse the web page
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = VERSION_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String version = matcher.group(1);
                        if (isHigherVersion(currentVersion, version)) {
                            toUpdate = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to check for updates", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("Failed to check for updates");
                }
            }
        }

        return toUpdate;
    }

    private boolean isHigherVersion(String currentVersion, String version) {
        String[] parts = currentVersion.split("-");
        String[] currentVersionNumbers = parts[0].split("\\.");
        String[] versionNumbers = version.split("\\.");

        for (int i = 0; i < currentVersionNumbers.length; i++) {
            int currentVersionNumber = Integer.parseInt(currentVersionNumbers[i]);
            int versionNumber = Integer.parseInt(versionNumbers[i]);
            if (versionNumber > currentVersionNumber) {
                return true;
            } else if (versionNumber < currentVersionNumber) {
                break;
            }

        }
        return false;
    }

    /**
     * Show update dialog
     */
    public static void showUpdateDialog() {
        int option = JOptionPane.showConfirmDialog(null, "<html><b>A new version of PRIDE Inspector is available</b>.<br><br> " +
                "Would you like to update?</html>", "Update Info", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            DesktopContext context = Desktop.getInstance().getDesktopContext();
            String website = context.getProperty("pride.inspector.download.website");
            HttpUtilities.openURL(website);
        }
    }
}
