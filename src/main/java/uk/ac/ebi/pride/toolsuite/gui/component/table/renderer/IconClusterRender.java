package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.regex.Matcher;

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
 * Created by yperez (ypriverol@gmail.com) on 01/12/2016.
 */
public class IconClusterRender extends JLabel implements TableCellRenderer {

    Icon icon;

    public IconClusterRender(Icon icon){
        this.icon = icon;
        setOpaque(true);
        this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        String peptideSequence = (String) value;

        if (value != null) {
            this.setText("<html><a href='https://www.ebi.ac.uk/pride/cluster/#/list?q=" + peptideSequence + "&page=1&size=20'>"+peptideSequence+"</a>" + "</html>");
        }
        // set background
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            Color alternate = UIManager.getColor("Table.alternateRowColor");
            if (row % 2 == 1) {
                this.setBackground(alternate);
            } else {
                this.setBackground(Color.WHITE);
            }
        }
        // repaint the component
        this.revalidate();
        this.repaint();
        return this;
    }
}
