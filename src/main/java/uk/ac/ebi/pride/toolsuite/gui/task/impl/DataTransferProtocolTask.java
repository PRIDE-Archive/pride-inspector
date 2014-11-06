package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferConfiguration;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferPort;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferProtocol;
import uk.ac.ebi.pride.toolsuite.gui.utils.RemotePortTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deciding on the best data transfer protocol
 *
 * @author Rui Wang
 * @version $Id$
 */
public class DataTransferProtocolTask extends TaskAdapter<List<DataTransferProtocol>, Void> {

    private final List<DataTransferConfiguration> dataTransferConfigurations = new ArrayList<DataTransferConfiguration>();

    public DataTransferProtocolTask(DataTransferConfiguration ... dataTransferConfigurations) {
        if (dataTransferConfigurations != null)
            this.dataTransferConfigurations.addAll(Arrays.asList(dataTransferConfigurations));
    }

    @Override
    protected List<DataTransferProtocol> doInBackground() throws Exception {

        List<DataTransferProtocol> validProtocol = new ArrayList<DataTransferProtocol>();

        for (DataTransferConfiguration dataTransferConfiguration : dataTransferConfigurations) {
            String host = dataTransferConfiguration.getHost();
            DataTransferProtocol dataTransferProtocol = dataTransferConfiguration.getProtocol();
            DataTransferPort[] ports = dataTransferConfiguration.getPort();

            boolean valid = true;

            for (DataTransferPort port : ports) {
                switch (port.getType()) {
                    case TCP:
                        if (!RemotePortTester.testTCP(host, port.getPort(), 5)) {
                            valid = false;
                        }
                        break;
                    case UDP:
                        if (!RemotePortTester.testUDP(host, port.getPort())) {
                            valid = false;
                        }
                        break;
                }

            }

            if (valid)
                validProtocol.add(dataTransferProtocol);
        }

        return validProtocol;
    }
}
