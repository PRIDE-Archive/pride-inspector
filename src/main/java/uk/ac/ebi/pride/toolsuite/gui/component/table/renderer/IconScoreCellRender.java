package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;


import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectSummary;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterFeatures;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterProjectProperties;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


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


    public IconScoreCellRender() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object valueRaw, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {


       Color alternate = null;
       Integer value = (Integer) valueRaw;
       if (value == 100)
           this.setText("NA");
       else if(value == 1) {
           this.setText("High-Confidence");
           alternate = new Color(0x00c000);
       }else if(value == 2){
            this.setText("Good-Confidence");
            alternate = new Color(0xacffac);
        }else if(value == 3){
            this.setText("Moderate");
            alternate = new Color(0xc1edff);
        }else if(value == 4){
            this.setText("Low-Quality");
            alternate = new Color(215, 39, 41, 100);
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
}
