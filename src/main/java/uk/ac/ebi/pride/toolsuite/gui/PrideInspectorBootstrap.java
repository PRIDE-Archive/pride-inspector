package uk.ac.ebi.pride.toolsuite.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.IOUtilities;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * PrideInspectorBootstrap calls PrideInspector with specific settings.
 *
 * User: rwang
 * Date: 06-Oct-2010
 * Time: 15:04:32
 */
public class PrideInspectorBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(PrideInspectorBootstrap.class);

    public static void main(String[] args) {
        new PrideInspectorBootstrap().go();
    }

    /**
     * Method to run the pride inspector
     */
    private void go() {
        // read bootstrap properties
        Properties bootstrapProps = getBootstrapSettings();

        // get max memory
        String maxMem = bootstrapProps.getProperty("pride.inspector.max.memory");


        // createAttributedSequence the command
        StringBuilder cmdBuffer = new StringBuilder();
        cmdBuffer.append("java -cp ");
        if (isWindowsPlatform()) {
            cmdBuffer.append("\"");
        }
        cmdBuffer.append(System.getProperty("java.class.path"));
        if (isWindowsPlatform()) {
            cmdBuffer.append("\"");
        }
        cmdBuffer.append(" -Xmx");
        cmdBuffer.append(maxMem);
        cmdBuffer.append("m ");
        cmdBuffer.append(PrideInspector.class.getName());

        // call the command
        Process process;
        try {
            logger.info(cmdBuffer.toString());
            process = Runtime.getRuntime().exec(cmdBuffer.toString());

            StreamProxy errorStreamProxy = new StreamProxy(process.getErrorStream(), System.err);
            StreamProxy outStreamProxy = new StreamProxy(process.getInputStream(), System.out);

            errorStreamProxy.start();
            outStreamProxy.start();

        } catch (IOException e) {
            logger.error("Error while bootstrapping the PRIDE Inspector", e);
        }
    }


    /**
     * Read bootstrap settings from config/config.props file.
     *
     * @return Properties   bootstrap settings.
     */
    private Properties getBootstrapSettings() {
        // load properties
        Properties props = new Properties();

        InputStream inputStream = null;
        try {
            URL pathURL = IOUtilities.getFullPath(PrideInspectorBootstrap.class, "config/config.props");
            File file = IOUtilities.convertURLToFile(pathURL);
            // input stream of the property file
            inputStream = new FileInputStream(file);
            props.load(inputStream);
        } catch (IOException e) {
            logger.error("Failed to load config/config.props file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("Failed to close InputStream while reading config.props file", e);
                }
            }
        }

        return props;
    }

    /**
     * Check whether it is Windows platform
     *
     * @return boolean  true means it is running on windows
     */
    private boolean isWindowsPlatform() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Windows");
    }

    /**
     * StreamProxy redirect the output stream and error stream to screen.
     */
    private static class StreamProxy extends Thread {
        final InputStream is;
        final PrintStream os;

        StreamProxy(InputStream is, PrintStream os) {
            this.is = is;
            this.os = os;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    os.println(line);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
