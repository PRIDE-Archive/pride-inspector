package uk.ac.ebi.pride.toolsuite.gui.aspera;

import com.asperasoft.faspmanager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PersistedAsperaFileUploader {
    private static final Logger logger = LoggerFactory.getLogger(PersistedAsperaFileUploader.class);

    public PersistedAsperaFileUploader(File ascpExecutable) {
        // set the location of the ascp executable
        Environment.setFasp2ScpPath(getAscpPath(ascpExecutable));
    }

    public String startTransferSession(RemoteLocation remoteLocation, XferParams transferParameters) throws InitializationException, ValidationException, LaunchException {
        if (remoteLocation == null) {
            throw new IllegalStateException("Can not upload without remote location being specified!");
        }

        TransferOrder order = new TransferOrder(new LocalLocation(), remoteLocation, transferParameters);
        return FaspManager.getSingleton().startTransfer(order);
    }

    private String getAscpPath(File excutable) {
        if (excutable == null || !excutable.exists()) {
            throw new IllegalArgumentException("Specified ascp executable does not exist.");
        }

        logger.info("Aspera executable location: " + excutable);
        return excutable.getAbsolutePath();
    }

    public void addTransferListener(TransferListener transferListener) throws InitializationException {
        FaspManager.getSingleton().addListener(transferListener);
    }

    public void removeTransferListener(TransferListener transferListener) throws InitializationException {
        FaspManager.getSingleton().removeListener(transferListener);
    }
}
