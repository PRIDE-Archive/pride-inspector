package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.peptide.PeptideRankingFilter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesPSMTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.LINE_SEPARATOR;
import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.TAB;

/**
 * Task to export peptide related information.
 * <p/>
 * User: rwang, ypriverol
 * Date: 13-Oct-2013
 * Time: 16:08:37
 */
public class ExportPeptideDescTask extends AbstractDataAccessTask<Void, Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Peptide Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Peptide Descriptions";
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
    public ExportPeptideDescTask(DataAccessController controller, String outputFilePath) {
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

            // data source
            if (controller.getType().equals(DataAccessController.Type.XML_FILE) || controller.getType().equals(DataAccessController.Type.MZIDENTML) || controller.getType().equals(DataAccessController.Type.MZTAB)) {
                writer.println("# Data source: " + ((File) controller.getSource()).getAbsolutePath());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                writer.println("# Data source: pride public mysql instance");
            }

            // accession if exist
            String acc = (exp.getId() != null) ? exp.getId().toString() : null;
            if (acc != null) {
                writer.println("# Experiment accession: " + acc);
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

            // in order to get a list of headers for export
            // first, we need to create an instance of PeptideTableModel
            PeptideSpeciesPSMTableModel pepTableModel = new PeptideSpeciesPSMTableModel(controller.getAvailablePeptideLevelScores(), PeptideRankingFilter.LESS_EQUAL_THAN_ONE.getRankingThreshold());
            // a list of columns to be skipped
            List<Integer> skipIndexes = new ArrayList<>();

            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.PROTEIN_NAME.getHeader()));
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.PROTEIN_STATUS.getHeader()));
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_FIT.getHeader()));
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.ADDITIONAL.getHeader()));
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader()));
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader()));


            int numOfCols = pepTableModel.getColumnCount();
            // iterate over each column to construct the header
            StringBuilder header = new StringBuilder();
            // ignore the last two columns
            // ignore row number
            for (int i = 0; i < numOfCols; i++) {
                if (!skipIndexes.contains(i)) {
                    header.append(pepTableModel.getColumnName(i));
                    header.append(TAB);
                }
            }
            writer.println(header.toString());

            Collection<Comparable> identIds = controller.getProteinIds();

            for (Comparable identId : identIds) {
                Collection<Comparable> pepIds = controller.getPeptideIds(identId);
                if (pepIds != null) {
                    for (Comparable pepId : pepIds) {

                        // get row data
                        PeptideTableRow content = TableDataRetriever.getPeptideTableRow(controller, identId, pepId);

                        //Peptide Sequence
                        writer.print(content.getSequence().getSequence() == null ? "" : content.getSequence().getSequence());
                        writer.print(TAB);

                        //Protein ID
                        writer.print(content.getProteinAccession().getAccession() == null ? "" : content.getProteinAccession().getAccession());
                        writer.print(TAB);

                        //Sequence Coverage
                        writer.print(content.getSequenceCoverage() == null ? "" : content.getSequenceCoverage());
                        writer.print(TAB);

                        //Peptide Ranking
                        writer.print(content.getRanking() == null ? "" : content.getRanking());
                        writer.print(TAB);

                        //Peptide Delta
                        writer.print(content.getDeltaMz() == null ? "" : content.getDeltaMz());
                        writer.print(TAB);

                        //Precursor charge
                        writer.print(content.getPrecursorCharge() == null ? "" : content.getPrecursorCharge());
                        writer.print(TAB);

                        //Precursor m/z
                        writer.print(content.getPrecursorMz() == null ? "" : content.getPrecursorMz());
                        writer.print(TAB);

                        //Modifications
                        writer.print(content.getModificationNames() == null ? "" : content.getModificationNames());
                        writer.print(TAB);

                        //# Ions
                        writer.print(content.getNumberOfFragmentIons() == null ? "" : content.getNumberOfFragmentIons());
                        writer.print(TAB);

                        //Scores
                        if (content.getScores() != null && content.getScores().size() > 0) {
                            for (Double value : content.getScores()) {
                                writer.print(value == null ? "" : value);
                                writer.print(TAB);

                            }
                        }

                        //# Length
                        writer.print(content.getSequenceLength() <= 0 ? "" : content.getSequenceLength());
                        writer.print(TAB);

                        //# Start
                        writer.print(content.getSequenceStartPosition() == 0 ? "" : content.getSequenceStartPosition());
                        writer.print(TAB);

                        //# End
                        writer.print(content.getSequenceEndPosition() == 0 ? "" : content.getSequenceEndPosition());
                        writer.print(TAB);

                        //# Spectrum ID
                        writer.print(content.getSpectrumId() == null ? "" : content.getSpectrumId());
                        writer.print(TAB);

                        // line break
                        writer.print(LINE_SEPARATOR);

                        // this is important for cancelling
                        checkInterruption();
                    }
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
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}