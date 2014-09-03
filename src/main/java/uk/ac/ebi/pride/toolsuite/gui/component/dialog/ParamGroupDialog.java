package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import uk.ac.ebi.pride.utilities.data.core.ParamGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog to show all the params in a param group
 *
 * User: rwang
 * Date: 17/09/2011
 * Time: 08:19
 */
public class ParamGroupDialog extends JDialog {

    public ParamGroupDialog(JFrame owner, String title, ParamGroup params) {
        super(owner, title);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(700, 250));
        addComponents(params);
        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);
    }

    private void addComponents(ParamGroup params) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTable table = TableFactory.createParamTable(params);

        JScrollPane scrollPane = new JScrollPane();

        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.gray));

        //---- searchResultTable ----
        // set view experiment cell renderer
        table.setBorder(null);
        table.setFillsViewportHeight(true);
        scrollPane.setViewportView(table);

        container.add(scrollPane, BorderLayout.CENTER);

        // close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ParamGroupDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(closeButton);

        this.add(container, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}
