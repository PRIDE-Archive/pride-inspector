package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class FilterPeptideRankingTask extends Task<Void, Void> {

    private static final String DEFAULT_TASK_TITLE = "Filtering peptide by ranking";
    private static final String DEFAULT_TASK_DESCRIPTION = "Filtering peptide by ranking";

    private PeptideSpeciesTableModel peptideSpeciesTableModel;
    private int rankingThreshold;

    public FilterPeptideRankingTask(PeptideSpeciesTableModel peptideSpeciesTableModel, int rankingThreshold) {
        this.peptideSpeciesTableModel = peptideSpeciesTableModel;
        this.rankingThreshold = rankingThreshold;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        peptideSpeciesTableModel.setRankingThreshold(rankingThreshold);
        return null;
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(Void results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}
