package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;


import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterFeatures;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.AttributedString;
import java.text.DecimalFormat;


/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * == General Description ==
 * <p>
 * This class Provides a general information or functionalities for
 * <p>
 * ==Overview==
 * <p>
 * How to used
 * <p>
 * Created by yperez (ypriverol@gmail.com) on 30/11/2016.
 */
public class IconScoreCellRender extends JLabel implements TableCellRenderer {

    private static final Color PTM_COLOR = new Color(255, 0, 0, 150);
    private AttributedString scoreString = null;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public IconScoreCellRender() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object valueRaw, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Color alternate = null;

        if(valueRaw == null){
            this.setText("NA");
        }else{
            ClusterFeatures value = (ClusterFeatures) valueRaw;
            String tooltip = getToolTipText(value);
            if (!tooltip.trim().equals("")) {
                this.setToolTipText(tooltip);
            }
            if(value.getTypeCluster() == 1) {
                this.setText("High-Confidence");
                alternate = new Color(0x00c000);
            }else if(value.getTypeCluster() == 2){
                this.setText("Good-Confidence");
                alternate = new Color(0xacffac);
            }else if(value.getTypeCluster() == 3){
                this.setText("Moderate");
                alternate = new Color(0xc1edff);
            }else if(value.getTypeCluster() == 4){
                this.setText("Low-Quality");
                alternate = new Color(215, 39, 41, 100);
            }
        }
        // set background
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }else if(alternate != null) {
            setBackground(alternate);
        }else if ( alternate == null && table instanceof JXTable) {
             alternate = UIManager.getColor("Table.alternateRowColor");
            if (row % 2 == 1) {
                setBackground(alternate);
            } else {
                setBackground(Color.WHITE);
            }
        }

        return this;
    }


    private String getToolTipText(ClusterFeatures value) {
        StringBuilder tip = new StringBuilder();
        if (value != null) {
            tip.append("<html>");
            tip.append("<p>");
            tip.append("<b>" + "<font size=\"3\" color=\"red\">");
            tip.append("<b>Number Spectra</b>:");
            tip.append(value.getSpectra());
            tip.append("</font></b><br>");
            tip.append("<b>Incorrect Spectra</b>:");
            tip.append(value.getIncorrectSpectra());
            tip.append("<br>");
            tip.append("<b>Quality Score</b>:");
            tip.append(df2.format(value.getDiff()));
            tip.append("<br>");
            tip.append("<b>Contaminant Spectra</b>:");
            tip.append(value.getContaminantIncorrectSpectra());
            tip.append("</p>");
            tip.append("<br>");
            tip.append("</html>");
        }
        return tip.toString();
    }
}
