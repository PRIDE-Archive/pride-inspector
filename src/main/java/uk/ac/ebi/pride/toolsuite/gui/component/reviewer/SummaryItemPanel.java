package uk.ac.ebi.pride.toolsuite.gui.component.reviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * SummaryItemPanel displays a list of file counts for a submission
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SummaryItemPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(SummaryItemPanel.class);

    private static final float DEFAULT_TITLE_FONT_SIZE = 13f;

    private java.util.List<SubmissionFileDetail> submissionFileDetails;

    public SummaryItemPanel(Collection<SubmissionFileDetail> submissionFileDetails) {
        this.submissionFileDetails = new ArrayList<SubmissionFileDetail>(submissionFileDetails);
        populateSummaryItemPanel();
    }

    /**
     * Populate summary item panel
     */
    private void populateSummaryItemPanel() {
        // remove all existing components
        this.removeAll();

        // set layout
        this.setLayout(new BorderLayout());

        JPanel summaryPanel = new JPanel();
        GridLayout layout = new GridLayout(2, 3);
        layout.setVgap(10);
        summaryPanel.setLayout(layout);

        // count different type of submission files
        int[] submissionFileCount = countSubmissionFiles();

        // add total count
        summaryPanel.add(createCountLabel("Total", submissionFileCount[0]));

        // add result file count
        summaryPanel.add(createCountLabel(ProjectFileType.RESULT.toString(), submissionFileCount[1]));

        // add raw file count
        summaryPanel.add(createCountLabel(ProjectFileType.RAW.toString(), submissionFileCount[2]));

        // add peak file count
        summaryPanel.add(createCountLabel(ProjectFileType.PEAK.toString(), submissionFileCount[3]));

        // add search result file count
        summaryPanel.add(createCountLabel(ProjectFileType.SEARCH.toString(), submissionFileCount[4]));

        // add other file count
        summaryPanel.add(createCountLabel(ProjectFileType.OTHER.toString(), submissionFileCount[5]));

        this.add(summaryPanel, BorderLayout.CENTER);

        // repaint
        this.revalidate();
        this.repaint();
    }

    /**
     * Create a JLabel to represent a file count
     *
     * @param message file type message
     * @param count   file count
     * @return a label represents the file count
     */
    private JLabel createCountLabel(String message, int count) {
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(DEFAULT_TITLE_FONT_SIZE));

        // set icon
        if (count > 0) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }

        // set message
        label.setText(message + ": " + count);

        return label;
    }


    /**
     * Count different type of submission files
     *
     * @return an array of integer represents the count
     */
    private synchronized int[] countSubmissionFiles() {
        int[] count = new int[6];
        count[0] = submissionFileDetails.size();
        for (SubmissionFileDetail fileDetail : submissionFileDetails) {

            final ProjectFileType fileType = fileDetail.getFileType();

            if (fileType.equals(ProjectFileType.RESULT)) {
                count[1]++;
            } else if (fileType.equals(ProjectFileType.RAW)) {
                count[2]++;
            } else if (fileType.equals(ProjectFileType.PEAK)) {
                count[3]++;
            } else if (fileType.equals(ProjectFileType.SEARCH)) {
                count[4]++;
            } else if (fileType.equals(ProjectFileType.OTHER)) {
                count[5]++;
            }
        }
        return count;
    }
}
