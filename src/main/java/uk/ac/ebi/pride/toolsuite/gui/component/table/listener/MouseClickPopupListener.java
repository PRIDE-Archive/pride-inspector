package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * Mouse listener, which
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MouseClickPopupListener extends MouseAdapter {

    private JTable table;
    private PopupDialog textDialog;

    private Collection<String> columnHeader;

    public MouseClickPopupListener(JTable table,
                                   Collection<String> columnHeader) {
        this.table = table;
        this.columnHeader = columnHeader;
        this.textDialog = new PopupDialog();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        String header = table.getColumnName(col);
        if (columnHeader.contains(header)) {
            int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
            TableModel tableModel = table.getModel();
            Object val = tableModel.getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(col));
            if (val != null) {
                String text = val.toString();
                textDialog.setText(text);
                textDialog.setVisible(true);
            }
        }
    }

    private static class PopupDialog extends JDialog {
        private JTextArea textArea;

        private PopupDialog() {
            addComponents();
        }

        private void addComponents() {
            this.setLayout(new BorderLayout());
            this.setSize(new Dimension(350, 200));
            // set display location
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createLineBorder(Color.gray));
            JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PopupDialog.this.setVisible(false);
                }
            });
            buttonPanel.add(closeButton);
            this.add(buttonPanel, BorderLayout.SOUTH);
        }

        void setText(String t) {
            textArea.setText(t);
        }
    }
}
