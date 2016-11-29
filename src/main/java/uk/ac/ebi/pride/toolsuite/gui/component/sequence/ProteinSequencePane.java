package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveProteinDetailModelTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveSelectedPeptideAnnotation;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.beans.PropertyChangeEvent;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.pride.toolsuite.gui.component.sequence.AttributedSequenceBuilder.PROTEIN_SEGMENT_GAP;
import static uk.ac.ebi.pride.toolsuite.gui.component.sequence.AttributedSequenceBuilder.PROTEIN_SEGMENT_LENGTH;

/**
 * Panel to visualize protein sequence
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 08/06/11
 * Time: 11:57
 */
public class ProteinSequencePane extends DataAccessControllerPane<AnnotatedProtein, Void> implements EventBusSubscribable {

    private final static Logger logger = LoggerFactory.getLogger(ProteinSequencePane.class);
    private final static int TOP_MARGIN = 5;
    private final static int TOP_MARGIN_MESSAGE = 45;
    private final static int BOTTOM_MARGIN = 20;
    private final static int LEFT_MARGIN = 70;
    private final static int RIGHT_MARGIN = 70;
    private final static int ROW_GAP = 15;
    private final static int COLUMN_GAP = 10;

    /**
     * property indicates a change of protein model
     */
    private static final String MODEL_PROP = "model";
    /**
     * This contains protein details and service as a data model for the protein sequence pane
     */
    private AnnotatedProtein proteinModel;
    /**
     * Unique protein id
     */
    private Comparable proteinId;
    /**
     * Subscribe peptide event
     */
    private PeptideEventSubscriber peptideEventSubscriber;
    /**
     * Action to download protein details
     */
    private Action downloadProteinAction;

    public ProteinSequencePane(DataAccessController controller) {
        this(controller, null);
    }

    public ProteinSequencePane(DataAccessController controller, Action downloadProteinAction) {
        super(controller);

        this.downloadProteinAction = downloadProteinAction;
        this.setLayout(new BorderLayout());
        this.addPropertyChangeListener(this);
    }

    @Override
    protected void setupMainPane() {
        this.setBackground(Color.white);
    }

    public Comparable getProteinId() {
        return proteinId;
    }

    public void setProteinId(Comparable proteinId) {
        this.proteinId = proteinId;
    }

    /**
     * Get protein model for this pane
     *
     * @return Protein protein detail object
     */
    public Protein getProteinModel() {
        return proteinModel;
    }

    /**
     * Set a new protein model
     *
     * @param protein protein detail object
     */
    public void setProteinModel(AnnotatedProtein protein) {
        AnnotatedProtein oldProt, newProt;
        synchronized (this) {
            oldProt = this.proteinModel;
            this.proteinModel = protein;
            newProt = protein;

            // property change listener
            if (oldProt != null) {
                oldProt.removePropertyChangeListener(this);
            }

            if (newProt != null) {
                newProt.addPropertyChangeListener(this);
            }
        }
        // notify
        firePropertyChange(MODEL_PROP, oldProt, newProt);
    }

    /**
     * Listen to the property change events
     *
     * @param evt property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (MODEL_PROP.equals(propName) || AnnotatedProtein.PEPTIDE_SELECTION_PROP.equals(propName)) {
            this.removeAll();
            if (proteinModel == null) {
                if (downloadProteinAction != null) {
                    // protein sequence is not downloaded
                    String msg = appContext.getProperty("protein.sequence.missing.title");
                    JPanel panel = createWarningPanel(msg, true);
                    this.add(panel, BorderLayout.NORTH);
                }
            } else if (downloadProteinAction != null && proteinModel.getSequenceString() != null) {
                // protein sequence can not be download
                String msg = appContext.getProperty("protein.sequence.unchecked.title");
                JPanel panel = createWarningPanel(msg, true);
                this.add(panel, BorderLayout.NORTH);
            } else if (proteinModel.getSequenceString() == null) {
                // protein sequence can not be download
                String msg = appContext.getProperty("protein.sequence.unavailable.title") +
                        ", " + proteinModel.getAccession() + " is either changed or deleted";
                JPanel panel = createWarningPanel(msg, false);
                this.add(panel, BorderLayout.NORTH);
            }
            revalidate();
            repaint();
        }
    }

    /**
     * Show warning message panel
     *
     * @param msg          Warning message
     * @param launchButton whether to include a launch button
     * @return JPanel   warning message panel
     */
    private JPanel createWarningPanel(String msg, boolean launchButton) {
        JPanel msgPanel = new JPanel();
        msgPanel.setPreferredSize(new Dimension(500, 40));
        msgPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.LINE_AXIS));

        msgPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        // warning message label
        JLabel msgLabel = new JLabel(GUIUtilities.loadIcon(appContext.getProperty("protein.sequence.missing.icon.small")));
        msgLabel.setFont(msgLabel.getFont().deriveFont(Font.BOLD));
        msgLabel.setText(msg);
        msgPanel.add(msgLabel);

        // add a glue to fill the empty space
        msgPanel.add(Box.createHorizontalGlue());

        if (launchButton) {
            // button to start calculate charts
            JButton computeButton = new JButton(downloadProteinAction);
            computeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ProteinSequencePane.this.removeAll();
                }
            });
            msgPanel.add(computeButton);
        }

        msgPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        return msgPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // create a new graphics 2D
        Graphics2D g2 = (Graphics2D) g.create();

        // get formatted protein sequence string
        AttributedString sequence = AttributedSequenceBuilder.build(proteinModel);

        // rendering hits
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (sequence != null) {
            drawProteinSequence(g2, sequence, proteinModel);
        }

        // dispose graphics 2D
        g2.dispose();
    }

    /**
     * This method is called when a protein sequence needs to be drawn
     *
     * @param g2       graphics 2D
     * @param sequence formatted protein sequence
     */
    private void drawProteinSequence(Graphics2D g2, AttributedString sequence, AnnotatedProtein protein) {
        Container viewport = getParent();
        int width = viewport.getWidth();
        // spacing within the panel
        int yMargin = (downloadProteinAction != null) ? drawLegend(g2, TOP_MARGIN_MESSAGE) + ROW_GAP : drawLegend(g2, TOP_MARGIN) + ROW_GAP;
        yMargin = drawProteinMetaData(g2, protein, yMargin) + ROW_GAP;
        int rightMargin = width - RIGHT_MARGIN;

        // line spacing
        int lineSpacing = 20;

        // AttributedString iterator
        AttributedCharacterIterator sequenceIter = sequence.getIterator();

        // renderer context
        FontRenderContext fontContext = g2.getFontRenderContext();

        // line break measurer
        LineBreakMeasurer measurer = new LineBreakMeasurer(sequenceIter, fontContext);

        // set the initial value of vertical position
        int yPos = yMargin;

        // calculate each protein sequence segment's length
        int proteinSegLength = PROTEIN_SEGMENT_LENGTH + PROTEIN_SEGMENT_GAP.length();

        // index of formatted protein sequence
        int textPosIndex = proteinSegLength;
        // the length of each line
        int lineLengthIndex = -1;
        // the width of each line
        float lineWidth = -1;
        // tracking the position of previous line end
        int previousLineEndPosition = 0;

        while (measurer.getPosition() < sequenceIter.getEndIndex()) {
            float xPos = LEFT_MARGIN;
            // line contains text already
            boolean lineContainText = false;
            boolean lineComplete = false;

            // stores all the text layout to be drawn and their starting horizontal position
            List<Tuple<TextLayout, Float>> layouts = new ArrayList<>();


            while (!lineComplete) {
                float wrappingWidth = rightMargin - xPos;
                TextLayout layout = measurer.nextLayout(wrappingWidth, textPosIndex, lineContainText);

                if (layout != null) {
                    // add an layout entry
                    Tuple<TextLayout, Float> element = new Tuple<>(layout, xPos);
                    layouts.add(element);

                    // increment horizontal position
                    xPos += layout.getAdvance();
                } else {
                    // line finished
                    lineComplete = true;
                }

                lineContainText = true;

                // increment text position index
                if (measurer.getPosition() == textPosIndex) {
                    textPosIndex += proteinSegLength;
                }

                // check whether reached the end of sequence
                if (measurer.getPosition() == sequenceIter.getEndIndex()
                        || (measurer.getPosition() - previousLineEndPosition) == lineLengthIndex) {
                    lineComplete = true;
                }

                if (lineComplete) {
                    if (lineLengthIndex == -1) {
                        lineLengthIndex = measurer.getPosition();
                    }

                    if (lineWidth == -1) {
                        lineWidth = xPos;
                    }
                    previousLineEndPosition = measurer.getPosition();
                }
            }

            // move to next line
            yPos += lineSpacing;

            // adjust the size of the panel
            if (yPos > getHeight()) {
                setPreferredSize(new Dimension(width, yPos));
                revalidate();
            }

            // draw the line
            for (Tuple<TextLayout, Float> entry : layouts) {
                TextLayout nextLayout = entry.getKey();
                Float nextPosition = entry.getValue();
                nextLayout.draw(g2, nextPosition, yPos);
            }

            // draw amino acid count number
            g2.setFont(AttributedSequenceBuilder.DEFAULT_FONT.deriveFont(Font.ITALIC));
            g2.setColor(AttributedSequenceBuilder.DEFAULT_FOREGROUND);

            int mod = measurer.getPosition() % proteinSegLength;
            int count = ((measurer.getPosition() - mod) / proteinSegLength) * PROTEIN_SEGMENT_LENGTH + mod;
            g2.drawString(count + "", LEFT_MARGIN + lineWidth - 40, yPos);
        }

        // set a margin gap at the bottom of the panel
        setPreferredSize(new Dimension(width, yPos + BOTTOM_MARGIN));
        revalidate();
    }

    /**
     * Draw the legend for protein sequence veiwer
     *
     * @param g2 Graphics 2d
     * @param y  starting vertical position
     * @return int stop vertical position
     */
    private int drawLegend(Graphics2D g2, int y) {
        Graphics2D ng2 = (Graphics2D) g2.create();
        ng2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ng2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int startXPos = getWidth() - RIGHT_MARGIN + 40;
        int xPos = startXPos - 5;
        int yPos = y;

        // draw overlap
        xPos = drawLegendText(Constants.OVERLAP, ng2, xPos, yPos);
        xPos = drawLegendIcon(Constants.PEPTIDE_OVERLAP_COLOUR, ng2, xPos - COLUMN_GAP, yPos);
        xPos -= 2 * COLUMN_GAP;
        // draw fit
        xPos = drawLegendText(Constants.FIT, ng2, xPos, yPos);
        xPos = drawLegendIcon(Constants.FIT_PEPTIDE_BACKGROUND_COLOUR, ng2, xPos - COLUMN_GAP, yPos);
        xPos -= 2 * COLUMN_GAP;
        // draw strict fit
        xPos = drawLegendText(Constants.STRICT_FIT, ng2, xPos, yPos);
        xPos = drawLegendIcon(Constants.STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR, ng2, xPos - COLUMN_GAP, yPos);
        xPos -= 2 * COLUMN_GAP;
        // draw PTM
        xPos = drawLegendText(Constants.PTM, ng2, xPos, yPos);
        xPos = drawLegendIcon(Constants.PTM_BACKGROUND_COLOUR, ng2, xPos - COLUMN_GAP, yPos);
        xPos -= 2 * COLUMN_GAP;
        // draw selected
        xPos = drawLegendText(Constants.SELECTED, ng2, xPos, yPos);
        xPos = drawLegendIcon(Constants.PEPTIDE_HIGHLIGHT_COLOUR, ng2, xPos - COLUMN_GAP, yPos);
        // draw rounded rectangle
        ng2.setColor(Color.gray);
        ng2.drawRoundRect(xPos - 5, yPos, startXPos - xPos + 5, ng2.getFontMetrics().getHeight() + 5, 5, 5);

        ng2.dispose();

        return yPos + 20;
    }

    private int drawLegendIcon(Color iconColour, Graphics2D g2, int xPos, int yPos) {
        int w = 10;
        int x = xPos - w;
        int y = yPos + w / 2;

        g2.setColor(iconColour);
        g2.fillRect(x, y, w, w);

        return x;
    }

    private int drawLegendText(String text, Graphics2D g2, int xPos, int yPos) {
        // font metrix
        FontMetrics fontMetrics = g2.getFontMetrics();

        int x = xPos;
        int y = yPos;

        // text width
        int w = fontMetrics.stringWidth(text);
        x -= w;
        int h = fontMetrics.getHeight();
        y += h;

        // set color
        g2.setColor(Color.black);

        // draw text
        g2.drawString(text, x, y);

        return x;
    }

    private int drawProteinMetaData(Graphics2D g2, AnnotatedProtein protein, int y) {
        Graphics2D ng2 = (Graphics2D) g2.create();
        ng2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ng2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = ng2.getFont().deriveFont(Font.BOLD, 12f);
        ng2.setFont(font);

        // starting position
        int xPos = LEFT_MARGIN;
        int yPos = y;
        FontMetrics fontMetrics = ng2.getFontMetrics();
        int lineSpace = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + 5;

        if (protein != null) {
            // draw accession
            String accession = protein.getAccession();
            if (accession != null) {
                String msg = "Accession: " + accession;
                ng2.drawString(msg, xPos, yPos);
                xPos += fontMetrics.stringWidth(msg);
            }
            // draw protein name if any
            String name = protein.getName();
            if (name != null && !"".equals(name.trim())) {
                String msg = (xPos > LEFT_MARGIN ? ", " : "") + "Name: " + name;
                ng2.drawString(msg, xPos, yPos);
            }

            // move to the next line
            yPos += lineSpace;
            xPos = LEFT_MARGIN;

//            // highlight this peptide section
//            ng2.setColor(new Color(40, 175, 99));

            // draw peptide counts
            int totalPeptides = protein.getAnnotations().size();
            if (totalPeptides > 0) {
                // total number of peptides
                int numOfPeptides = protein.getNumOfPeptides();
                // number of valid peptides
                int validPeptides = protein.getNumOfValidPeptides();
                // number of unique peptides
                int uniquePeptides = protein.getNumOfUniquePeptides();

                String msg = numOfPeptides + " peptides";
                if (validPeptides >= 0 || uniquePeptides >= 0) {
                    msg += " (";
                    if (validPeptides >= 0) {
                        msg += validPeptides + " matched";
                    }


                    if (uniquePeptides >= 0) {
                        msg += (msg.endsWith("(") ? "" : ", ") + uniquePeptides + " distinct";
                    }
                    msg += ")";
                }

                ng2.drawString(msg, xPos, yPos);
                xPos += fontMetrics.stringWidth(msg);

                // draw sequence coverage
                int aminoAcidCoverage = protein.getNumOfAminoAcidCovered();
                aminoAcidCoverage = aminoAcidCoverage == -1 ? 0 : aminoAcidCoverage;
                String sequence = protein.getSequenceString();
                int sequenceLen = sequence == null ? 0 : sequence.length();
                // formatter
                DecimalFormat format = new DecimalFormat("##.#%");
                // text to display
                double coverage = protein.getSequenceCoverage();
                coverage = coverage == -1 ? 0 : coverage;
                String percentage = format.format(coverage);

                msg = (xPos > LEFT_MARGIN ? ", " : "") + aminoAcidCoverage + "/" + sequenceLen + " amino acids (" + percentage + " coverage)";
                ng2.drawString(msg, xPos, yPos);
                xPos += fontMetrics.stringWidth(msg);
            }

            ng2.dispose();
        }
        return yPos;
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // create subscriber
        peptideEventSubscriber = new PeptideEventSubscriber();

        // subscribe
        eventBus.subscribe(PSMEvent.class, peptideEventSubscriber);
    }

    @Override
    public void succeed(TaskEvent<AnnotatedProtein> proteinTaskEvent) {
        // set a new protein model
        this.setProteinModel(proteinTaskEvent.getValue());
    }

    /**
     * Subscribe to peptide event
     */
    private class PeptideEventSubscriber implements EventSubscriber<PSMEvent> {

        @Override
        public void onEvent(PSMEvent event) {
            Comparable identId = event.getIdentificationId();
            Comparable peptideId = event.getPeptideId();

            logger.debug("Peptide selected: Ident {} - Peptide {}", identId, peptideId);

            Task task;
            if (proteinModel != null && proteinId != null && proteinId.equals(identId)) {
                logger.debug("New peptide selection on protein sequence");
                task = new RetrieveSelectedPeptideAnnotation(controller, proteinModel, identId, peptideId);
            } else {
                logger.debug("New protein sequence will be shown");
                proteinId = identId;
                task = new RetrieveProteinDetailModelTask(controller, identId, peptideId);
                task.addTaskListener(ProteinSequencePane.this);
            }

            TaskUtil.startBackgroundTask(task, controller);
        }
    }
}
