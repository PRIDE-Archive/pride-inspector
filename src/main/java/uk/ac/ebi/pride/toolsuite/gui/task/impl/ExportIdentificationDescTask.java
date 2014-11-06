package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * User: rwang, ypriverol
 * Date: 01-Sep-2013
 * Time: 17:21:07
 */
public class ExportIdentificationDescTask extends AbstractDataAccessTask<Void, Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Identification Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Identification Descriptions";


    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file path to output the result.
     */
    public ExportIdentificationDescTask(DataAccessController controller, String outputFilePath) {
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
            if (controller.getType().equals(DataAccessController.Type.XML_FILE) || controller.getType().equals(DataAccessController.Type.MZIDENTML) || controller.getType().equals(DataAccessController.Type.MZTAB)) {
                writer.println("# Data source: " + ((File) controller.getSource()).getAbsolutePath());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                writer.println("# Data source: pride public mysql instance");
            }

            // accession if exist
            String acc = (exp.getId() != null) ? exp.getId().toString() : null;
            if (acc != null) {
                writer.println("# Experiment Accession: " + acc);
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

            writer.print("Submitted Protein Accession" + Constants.TAB
                    + "Mapped Protein Accession" + Constants.TAB
                    + "Protein Name" + Constants.TAB
                    + "Threshold" + Constants.TAB
                    + "PSM" + Constants.TAB
                    + "Peptides" + Constants.TAB
                    + "PTMs");
            List<Object> headerScore = TableDataRetriever.getProteinScoreHeaders(controller);

            boolean scorePresent = false;
            if (!headerScore.isEmpty()) {
                scorePresent = true;
                writer.print(Constants.TAB);
                for (int i = 0; i < headerScore.size() - 1; i++) {
                    writer.print(headerScore.get(i).toString() + Constants.TAB);
                }
                writer.println(headerScore.get(headerScore.size() - 1));
            } else {
                // line break
                writer.print(Constants.LINE_SEPARATOR);
            }


            Collection<Comparable> identIds = controller.getProteinIds();

            for (Comparable identId : identIds) {
                // a row of data
                ProteinTableRow content = TableDataRetriever.getProteinTableRow(controller, identId, null);
                writeContent(content, writer, scorePresent);

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

    private void writeContent(ProteinTableRow content, PrintWriter writer, boolean scorePresent) {

        // Submitted Protein Accession
        writer.print(content.getProteinAccession().getAccession() == null ? "" : content.getProteinAccession().getAccession());
        writer.print(Constants.TAB);

        //Mapped Protein Accession
        writer.print(content.getProteinAccession().getMappedAccession() == null ? "" : content.getProteinAccession().getMappedAccession());
        writer.print(Constants.TAB);

        //Protein Name
        writer.print(content.getProteinName() == null ? "" : content.getProteinName());
        writer.print(Constants.TAB);

        //Thershold
        writer.print(content.getThreshold() == null ? "" : content.getThreshold());
        writer.print(Constants.TAB);

        //PSM
        writer.print(content.getNumberOfPeptides() == null ? "" : content.getNumberOfPeptides());
        writer.print(Constants.TAB);

        //Peptides
        writer.print(content.getNumberOfUniquePeptides() == null ? "" : content.getNumberOfUniquePeptides());
        writer.print(Constants.TAB);

        //PTMs
        writer.print(content.getNumberOfPTMs() == null ? "" : content.getNumberOfPTMs());
        writer.print(Constants.TAB);

        if (scorePresent) {
            for (int i = 0; i < content.getScores().size() - 1; i++) {
                writer.print((content.getScores().get(i) == null ? "" : content.getScores().get(i)) + Constants.TAB);
            }
            writer.print((content.getScores().get(content.getScores().size() - 1) == null) ? "" : content.getScores().get(content.getScores().size() - 1));
        }

        // line break
        writer.print(Constants.LINE_SEPARATOR);
    }
}
