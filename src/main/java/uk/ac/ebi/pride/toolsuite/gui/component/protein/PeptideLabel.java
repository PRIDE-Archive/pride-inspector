package uk.ac.ebi.pride.toolsuite.gui.component.protein;

import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.Peptide;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * PeptideLabel visualize peptide sequence and ptms
 * <p/>
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 12:28:56
 */
public class PeptideLabel extends JPanel {
    private static final Color PTM_COLOR = new Color(255, 0, 0, 150);
    public final static Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    public final static Font DEFAULT_FONT_BOLD = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private AttributedString ptmString;

    public PeptideLabel(Peptide peptide) {
        ptmString = getPTMString(peptide);
        this.setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int offset = 0;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(ptmString.getIterator(), offset, 15);
        g2.dispose();
    }

    private AttributedString getPTMString(Peptide peptide) {
        String sequence = peptide.getSequence();
        java.util.List<Modification> mods = peptide.getModifications();
        AttributedString str = new AttributedString(sequence);
        str.addAttribute(TextAttribute.FONT, DEFAULT_FONT);
        if (!mods.isEmpty()) {
            int seqLength = sequence.length();
            for (Modification mod : mods) {
                int location = mod.getLocation();
                location = location == 0 ? 1 : location;
                if (seqLength + 1 > location && location > 0) {
                    str.addAttribute(TextAttribute.FOREGROUND, PTM_COLOR, location - 1, location);
                    str.addAttribute(TextAttribute.FONT, DEFAULT_FONT_BOLD, location - 1, location);
                }
            }
        }
        return str;
    }
}
