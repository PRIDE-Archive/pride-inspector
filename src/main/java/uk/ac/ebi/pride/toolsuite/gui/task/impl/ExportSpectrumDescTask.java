package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.LINE_SEPARATOR;
import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.TAB;

/**
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 17:36:06
 */
public class ExportSpectrumDescTask extends AbstractDataAccessTask<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumDescTask.class);

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Spectrum Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Spectrum Descriptions";
    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file to output the result.
     */
    public ExportSpectrumDescTask(DataAccessController controller, String outputFilePath) {
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
            String acc = (exp.getId() != null) ? exp.getId().toString() : null;
            if (acc != null) {
                writer.println("# PRIDE accession: " + acc);
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

            //------- Content section -------
            writer.println("Spectrum ID" + TAB + "MS Level" + TAB + "Precursor Charge" +
                    TAB + "Precursor m/z" + TAB + "Precursor Intensity" + TAB +
                    "Sum of Intensity" + TAB + "Number of peaks");
            Collection<Comparable> spectrumIds = controller.getSpectrumIds();
            for (Comparable specId : spectrumIds) {
                writer.print(specId);
                writer.print(TAB);
                writer.print(controller.getSpectrumMsLevel(specId));
                writer.print(TAB);
                Integer charge = controller.getSpectrumPrecursorCharge(specId);
                if (charge != null) {
                    writer.print(controller.getSpectrumPrecursorCharge(specId));
                }
                writer.print(TAB);
                writer.print(controller.getSpectrumPrecursorMz(specId));
                writer.print(TAB);
                writer.print(controller.getSpectrumPrecursorIntensity(specId));
                writer.print(TAB);
                writer.print(controller.getSumOfIntensity(specId));
                writer.print(TAB);
                writer.print(controller.getNumberOfSpectrumPeaks(specId));
                writer.print(LINE_SEPARATOR);

                // this is important for cancelling
                checkInterruption();
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
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
