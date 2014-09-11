package uk.ac.ebi.pride.toolsuite.gui.component.mzdata;

import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.List;

/**
 * MzDataCountLabel is to display the number spectrum and chromatogram has been loaded.
 *
 * User: rwang
 * Date: 23-Sep-2010
 * Time: 13:56:27
 */
public class MzDataCountLabel extends JLabel
        implements TaskListener<Void, Tuple<TableContentType, List<Object>>>, Constants {

    /** the message format for the label */
    private MessageFormat displayFormat;

    /** the number of spectrum */
    private int spectraCount = 0;

    /** the number of chromatogram */
    private int chromaCount = 0;

    /**
     * Constructor
     *
     * @param totalNumOfSpectra total number of spectra
     * @param totalNumOfChroma  total number of chromatogram
     */
    public MzDataCountLabel(int totalNumOfSpectra, int totalNumOfChroma) {
        String msg = "<html><b>Loaded</b> [ ";
        // if there is spectra
        if (totalNumOfSpectra > 0) {
            msg += "<b>" + SPECTRUM + "</b>: {0}/" +totalNumOfSpectra + " ";
        }

        // if there is chromatogram
        if (totalNumOfChroma > 0) {
            msg += "<b>" + CHROMATOGRAM + "</b>: {1}/" + totalNumOfChroma + " ";
        }

        msg += "]</html>";

        this.displayFormat = new MessageFormat(msg);
        // set label
        setLabelTitle();
    }

    /**
     * Update the label title
     */
    private void setLabelTitle() {
        // set the starting label
        Object[] args = {spectraCount, chromaCount};
        this.setText(displayFormat.format(args));
    }

    /**
     * Change the label title according to the number of spectra and chromatogram has been loaded.
     * 
     * @param listTaskEvent list task event
     */
    @Override
    public void process(TaskEvent<List<Tuple<TableContentType, List<Object>>>> listTaskEvent) {
        for (Tuple<TableContentType, List<Object>> value : listTaskEvent.getValue()) {
            switch(value.getKey()) {
                case SPECTRUM:
                    spectraCount ++;
                    setLabelTitle();
                    break;
                case CHROMATOGRAM:
                    chromaCount ++;
                    setLabelTitle();
                    break;
            }
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<Void> voidTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
