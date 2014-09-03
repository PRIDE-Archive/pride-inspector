package uk.ac.ebi.pride.toolsuite.gui.component.peptide;

import java.util.ArrayList;

/**
 * @author Rui Wang
 * @version $Id$
 */
public enum PeptideRankingFilter {
    LESS_EQUAL_THAN_ONE("<= 1", 1),
    LESS_EQUAL_THAN_TWO("<= 2", 2),
    LESS_EQUAL_THAN_THREE("<= 3", 3),
    ALL("All", 1000);


    private String rankingFilter;
    private int rankingThreshold;

    private PeptideRankingFilter(String rankingFilter, int rankingThreshold) {
        this.rankingFilter = rankingFilter;
        this.rankingThreshold = rankingThreshold;
    }

    public String getRankingFilter() {
        return rankingFilter;
    }

    public int getRankingThreshold() {
        return rankingThreshold;
    }

    public static java.util.List<String> getRankingFilters() {
        java.util.List<String> filters = new ArrayList<String>();

        for (PeptideRankingFilter peptideRankingFilter : values()) {
            filters.add(peptideRankingFilter.getRankingFilter());
        }

        return filters;
    }

    public static int getRankingThreshold(String filter) {
        for (PeptideRankingFilter peptideRankingFilter : values()) {
            if (peptideRankingFilter.getRankingFilter().equalsIgnoreCase(filter)) {
                return peptideRankingFilter.getRankingThreshold();
            }
        }

        return -1;
    }
}
