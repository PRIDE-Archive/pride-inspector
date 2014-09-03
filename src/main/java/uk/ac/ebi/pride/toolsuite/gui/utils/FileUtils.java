package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
}
