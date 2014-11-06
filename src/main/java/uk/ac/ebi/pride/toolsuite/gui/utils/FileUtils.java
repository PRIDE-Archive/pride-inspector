package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Rui Wang
 * @version $Id$
 */
public final class FileUtils {

    private FileUtils() {
    }

    public static String tail(File file, int numberOfChars) throws IOException {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new java.io.RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer > (fileLength - numberOfChars); filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } finally {
            if (fileHandler != null)
                fileHandler.close();
        }
    }


    /**
     * Get the root path from the jar
     *
     * @return root path in string
     */
    public static String getAbsolutePath() throws UnsupportedEncodingException {
        String jarDir;

        //get absolute path including jar filename
        String jarPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        String decodedJarPath = URLDecoder.decode(jarPath, "UTF-8");
        //convert String object to File object in order to be able to use getParent()
        File jarFile = new File(decodedJarPath);
        //this is the directory one level above the jar file
        File jarParent = new File(jarFile.getParent());
        // this is the directory two levels above the jar file
        if (!decodedJarPath.endsWith("jar"))
            jarDir = jarParent.getParent();
        else
            jarDir = jarParent.getAbsolutePath();


        return jarDir;
    }
}
