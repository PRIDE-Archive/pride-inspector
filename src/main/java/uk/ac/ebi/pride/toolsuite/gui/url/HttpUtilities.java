package uk.ac.ebi.pride.toolsuite.gui.url;

import javax.swing.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: rwang
 * Date: 20-Aug-2010
 * Time: 12:53:03
 */
public class HttpUtilities {


    public static HttpURLConnection createHttpConnection(String url, String method) throws Exception {

        HttpURLConnection connection;
        URL downloadURL = new URL(url);
        connection = (HttpURLConnection) downloadURL.openConnection();
        connection.setRequestMethod(method);
        connection.setUseCaches(true);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, url);
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;

                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }

                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }
}
