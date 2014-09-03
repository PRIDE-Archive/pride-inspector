package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Task to export to MGF file format
 * <p/>
 * User: dani, rwang
 * Date: 18-Oct-2010
 * Time: 10:46:54
 */
public class ExportSpectrumMGFTask extends AbstractDataAccessTask<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumDescTask.class);

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Spectrum MGF format";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Spectrum MGF format";
    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve spectrum data in an MGF file format
     *
     * @param controller     DataAccessController
     * @param outputFilePath file to output the result.
     */
    public ExportSpectrumMGFTask(DataAccessController controller, String outputFilePath) {
        super(controller);
        this.outputFilePath = outputFilePath;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(new File(outputFilePath)));
            ExperimentMetaData exp = controller.getExperimentMetaData();

            //------- Comment section -------

            // data source
            if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
                writer.println("# Data source: " + ((File) controller.getSource()).getAbsolutePath());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                writer.println("# Data source: pride public mysql instance");
            }

            // accession if exist
            String acc = (exp.getId() !=null)?exp.getId().toString():null;
            if (acc != null) {
                writer.println("# PRIDE accession: " + acc);
            }

            String title = exp.getName();
            if (title != null) {
                writer.println("# Experiment title: " + title);
            }

            // number of spectrum
            if (controller.hasSpectrum()) {
                writer.println("# Number of spectra: " + controller.getNumberOfSpectra());
            }

            // number of protein identifications
            if (controller.hasProtein()) {
                writer.println("# Number of protein identifications: " + controller.getNumberOfProteins());
            }

            // number of peptides
            if (controller.hasPeptide()) {
                writer.println("# Number of peptides: " + controller.getNumberOfPeptides());
            }

            //------- MGF content section -------
            for (Comparable spectrumId : controller.getSpectrumIds()) {
                Spectrum spectrum = controller.getSpectrumById(spectrumId);
                int msLevel = controller.getSpectrumMsLevel(spectrumId);
                if (msLevel == 2) {
                    writer.println("BEGIN IONS");
                    writer.println("TITLE=" + spectrumId);
                    writer.println("PEPMASS=" + controller.getSpectrumPrecursorMz(spectrumId));
                    // precursor charge
                    Integer charge = controller.getSpectrumPrecursorCharge(spectrumId);
                    if (charge != null) {
                        writer.println("CHARGE=" + charge + (charge >= 0 ? "+" : "-"));
                    }
                    //get both arrays
                    double[] mzBinaryArray = spectrum.getMzBinaryDataArray().getDoubleArray();
                    double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();

                    for (int i = 0; i < mzBinaryArray.length; i++) {
                        writer.println(mzBinaryArray[i] + Constants.TAB + intensityArray[i]);
                    }
                    writer.println("END IONS" + Constants.LINE_SEPARATOR);

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    writer.flush();
                }
            }
            writer.flush();
        } catch (DataAccessException e2) {
            String msg = "Failed to retrieve data from data source";
            logger.error(msg, e2);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (IOException e1) {
            String msg = "Failed to write data to the output file, please check you have the right permission";
            logger.error(msg, e1);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (InterruptedException e3) {
            logger.warn("Exporting spectrum in MGF format has been interrupted");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
