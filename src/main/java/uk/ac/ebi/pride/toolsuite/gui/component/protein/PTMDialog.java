package uk.ac.ebi.pride.toolsuite.gui.component.protein;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PTMTableModel;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 11:10:51
 */
public class PTMDialog extends JDialog implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(PTMDialog.class);

    private final Peptide peptide;
    private JTable ptmTable;

    private final static String EXPORT_ACTION = "Export";
    private final static String CLOSE_ACTION = "Close";

    public PTMDialog(Frame owner, Peptide peptide) {
        super(owner);
        this.peptide = peptide;
        this.setTitle("PTM");
        this.setMinimumSize(new Dimension(600, 300));
        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addComponents();
    }

    private void addComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        // modification label
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><p>");
        stringBuilder.append("<font size='3'><b>Peptide</b>:");
        stringBuilder.append(peptide.getSequence());
        stringBuilder.append("</font><br></p></html>");

        JLabel label = new JLabel(stringBuilder.toString());
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(labelPanel, BorderLayout.NORTH);

        // modification table
        ptmTable = TableFactory.createPTMTable();
        PTMTableModel peptideTableModel = (PTMTableModel) ptmTable.getModel();
        peptideTableModel.addData(peptide);
        JScrollPane scrollPane = new JScrollPane(ptmTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        // button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export");
        exportButton.setActionCommand(EXPORT_ACTION);
        exportButton.addActionListener(this);
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand(CLOSE_ACTION);
        closeButton.addActionListener(this);
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        this.setContentPane(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (EXPORT_ACTION.equals(command)) {
            PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
            SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select Path Save To", true, "ptm", false, Constants.TAB_SEP_FILE);
            ofd.setMultiSelectionEnabled(false);
            int result = ofd.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = ofd.getSelectedFile();
                String filePath = selectedFile.getPath();
                context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new FileWriter(selectedFile));
                    TableModel tableModel = ptmTable.getModel();
                    int rowCnt = tableModel.getRowCount();
                    int colCnt = tableModel.getColumnCount();
                    for (int i = 0; i < rowCnt; i++) {
                        for (int j = 0; j < colCnt; j++) {
                            Object val = tableModel.getValueAt(i, j);
                            writer.print(val != null ? val.toString() : "");
                            if (j < colCnt - 1) {
                                writer.print(Constants.TAB);
                            }
                        }
                        writer.println();
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    logger.error("Failed to export PTM details", e1);
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        } else if (CLOSE_ACTION.equals(command)) {
            this.dispose();
        }
    }
}
