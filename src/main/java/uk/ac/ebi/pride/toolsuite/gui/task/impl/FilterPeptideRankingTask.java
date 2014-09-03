package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class FilterPeptideRankingTask extends Task<Void, Void> {

    private PeptideSpeciesTableModel peptideSpeciesTableModel;
    private int rankingThreshold;

    public FilterPeptideRankingTask(PeptideSpeciesTableModel peptideSpeciesTableModel, int rankingThreshold) {
        this.peptideSpeciesTableModel = peptideSpeciesTableModel;
        this.rankingThreshold = rankingThreshold;
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
