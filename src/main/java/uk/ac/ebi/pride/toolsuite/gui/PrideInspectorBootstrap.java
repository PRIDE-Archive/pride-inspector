package uk.ac.ebi.pride.toolsuite.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.IOUtilities;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * PrideInspectorBootstrap calls PrideInspector with specific settings.
 *
 * @author rwang
 * @author ypriverol
 * Date: 06-Oct-2010
 * Time: 15:04:32
 */
public class PrideInspectorBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(PrideInspectorBootstrap.class);

    public static void main(String[] args) {
        new PrideInspectorBootstrap().go(args);
    }

    /**
     * Method to run the pride inspector
     */
    private void go(String[] args) {
        // read bootstrap properties
        Properties bootstrapProps = getBootstrapSettings();
        String generalArgs = "";
        if(args != null && args.length > 0){
            for(String arg: args)
                generalArgs += arg + " ";
        }
        generalArgs = generalArgs.trim();

        // get max memory
        String maxMem = bootstrapProps.getProperty("pride.inspector.max.memory");

        List<String> arguments = new ArrayList<>();


        // createAttributedSequence the command
        StringBuilder cmdBuffer = new StringBuilder();
        arguments.add("java");
        arguments.add("-cp");
        String classPath = System.getProperty("java.class.path");
        logger.info(classPath);
        if (isWindowsPlatform()) {
            arguments.add("\"");
        }
        arguments.add(System.getProperty("java.class.path"));
        if (isWindowsPlatform()) {
            arguments.add("\"");
        }
        arguments.add("-Xmx"+maxMem+"m");
        arguments.add(PrideInspector.class.getName());

        for(String arg: args)
                arguments.add(arg);

        // call the command
        Process process;
        try {
            logger.info(cmdBuffer.toString());
            process = Runtime.getRuntime().exec(arguments.toArray(new String[arguments.size()]));

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
