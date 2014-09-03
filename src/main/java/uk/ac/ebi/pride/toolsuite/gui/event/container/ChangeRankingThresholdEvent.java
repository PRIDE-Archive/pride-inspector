package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ChangeRankingThresholdEvent extends AbstractEventServiceEvent {

    private final int rankingThreshold;
    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param rankingThreshold    ranking threshold
     */
    public ChangeRankingThresholdEvent(Object source, int rankingThreshold) {
        super(source);
        this.rankingThreshold = rankingThreshold;
    }

    public int getRankingThreshold() {
        return rankingThreshold;
    }
}