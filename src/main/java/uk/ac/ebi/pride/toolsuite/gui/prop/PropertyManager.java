package uk.ac.ebi.pride.toolsuite.gui.prop;

import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterAssayProperties;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterProjectProperties;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Scanner;

/**
 * PropteryManager manages all the properties for the application.
 * 
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 21:14:01
 */
public class PropertyManager {
    private final Properties system;
    private final Properties user;
    private final ClusterProjectProperties clusterProjects;
    private final ClusterAssayProperties clusterAssays;

    public PropertyManager() {
        system = new Properties();
        user = new Properties();
        clusterProjects = new ClusterProjectProperties();
        clusterAssays = new ClusterAssayProperties();
    }

    /**
     * Get all properties
     * @return Properties   property object.
     */
    public Properties getProperties() {
        Properties results = new Properties();
        results.putAll(system);
        results.putAll(user);
        return results;
    }

    public ClusterProjectProperties getProjectClusters(){
        return clusterProjects;
    }

    public ClusterAssayProperties getAssayClusters(){
        return clusterAssays;
    }

    /**
     * Load system properties. System properties will be overwritten by user properties.
     *
     * @param in    property file input stream.
     * @throws IOException  fail to read property file.
     */
    public void loadSystemProps(InputStream in) throws IOException {
        loadProps(system, in);
    }

    /**
     * Load user properties. User properties will overwrite syste properties with identical name.
     *
     * @param in    property file input steam.
     * @throws IOException  fail to read property file.
     */
    public void loadUserProps(InputStream in) throws IOException {
        loadProps(user, in);
    }

    /**
     * Get property value from the given property name.
     *
     * @param name  property name.
     * @return String   property value.
     */
    public String getProperty(String name) {
        String value = user.getProperty(name);
        if (value != null) {
            return value;
        } else {
            return system.getProperty(name);
        }
    }

    /**
     * Get the property with the specified name.
     *
     * The elements of the args array are substituted into the value of the property.
     *
     * @param name  name of the property.
     * @param args  a list of arguments.
     * @return String   property value.
     */
    public String getProperty(String name, Object[] args) {
        if (name == null) {
            return null;
        }

        if (args == null) {
            return getProperty(name);
        }else {
            String value = getProperty(name);
            if (value == null) {
                return null;
            } else {
                return MessageFormat.format(value, args);
            }
        }
    }

    /**
     * Set property. If none previous property exists,
     * a new property will be created under user properties.
     *
     * @param name  name of the property
     * @param value value of the property
     */
    public void setProperty(String name, String value) {
        if (user.containsKey(name)) {
            user.setProperty(name, value);
        } else if (system.containsKey(name)){
            system.setProperty(name, value);
        } else {
            user.setProperty(name, value);
        }
    }

    private void loadProps(Properties into, InputStream in) throws IOException {
        try {
            into.load(in);
        } finally {
            in.close();
        }
    }

    public void loadClusterProjectsProperties(InputStream in) {

        Scanner sc = new Scanner(in).useDelimiter("\n");
        boolean header = false;
        while (sc.hasNextLine()){
            String line = sc.next();
            String[] lineArr = line.split("\t");
            if(header)
                clusterProjects.addPRIDEProject(lineArr[1], lineArr[2],
                        lineArr[3], lineArr[4], lineArr[5], lineArr[6],
                        lineArr[7], lineArr[8], lineArr[9], lineArr[10]
                        );
            header = true;
        }
    }

    public void loadClusterAssayProperties(InputStream in) {

        Scanner sc = new Scanner(in).useDelimiter("\n");
        boolean header = false;
        while (sc.hasNextLine()){
            String line = sc.next();
            String[] lineArr = line.split("\t");
            if(header)
                clusterAssays.addAssayProject(lineArr[1],lineArr[2],
                        lineArr[3], lineArr[4], lineArr[5], lineArr[6], lineArr[7],
                        lineArr[8], lineArr[9], lineArr[10], lineArr[11]
                );
            header = true;
        }

    }
}
