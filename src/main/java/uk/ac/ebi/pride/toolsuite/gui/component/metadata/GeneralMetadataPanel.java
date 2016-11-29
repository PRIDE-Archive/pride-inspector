package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.HyperLinkCellMouseClickListener;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.HyperLinkCellRenderer;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
/*
 * Created by JFormDesigner on Sat Jul 23 08:30:00 BST 2011
 */


/**
 * @author User #2
 */
public class GeneralMetadataPanel extends JPanel {

    public GeneralMetadataPanel(GeneralMetaDataGroup metaData) {
        populateComponents(metaData);
        initComponents();
    }

    /**
     * Create key components and populate them with values
     *
     * @param metaData meta data
     */
    private void populateComponents(GeneralMetaDataGroup metaData) {

        // get accession
//        String accession = (metaData.getId() != null) ? metaData.getId().toString() : null;
//        accessionField = new JTextField();
//        if (accession != null) {
//            accessionField.setText(accession);
//        }

        expTitleField = new JTextField();
        shortLabelField = new JTextField();

        // get experiment title
        String expTitle = metaData.getName();
        if (expTitle != null) {
            expTitleField.setText(expTitle);
        }

        // get short label
        String sl = metaData.getShortLabel();
        if (sl != null) {
            shortLabelField.setText(sl);
        }

        expTitleField.setCaretPosition(0);
        shortLabelField.setCaretPosition(0);

        projectField = new JTextField();
        expDescArea = new JTextPane();
        List<CvParam> cvs = metaData.getMetaData().getCvParams();
        if (!cvs.isEmpty()) {
            for (CvParam cv : cvs) {
                // get project name
                if (cv != null) {
                    if (CvTermReference.PROJECT_NAME.getAccession().equals(cv.getAccession())) {
                        projectField.setText(cv.getValue());
                    } else if (CvTermReference.EXPERIMENT_DESCRIPTION.getAccession().equals(cv.getAccession())) {
                        // get experiment description
                        expDescArea.setText(cv.getValue());
                    }
                }
            }
        }

        projectField.setCaretPosition(0);
        expDescArea.setCaretPosition(0);

        // species field
        speciesField = new JTextField();
        String species = "";
        Set<String> speciesAcc = new HashSet<>();
        String tissues = "";
        Set<String> tissuesAcc = new HashSet<>();

        List<Sample> samples = metaData.getMetaData().getSamples();
        if (!samples.isEmpty()) {
            for (Sample sample : samples) {
                for (CvParam cvParam : sample.getCvParams()) {
                    String cvAcc = cvParam.getAccession();
                    String name = cvParam.getName();
                    String cvLabel = (cvParam != null && cvParam.getCvLookupID() != null)?cvParam.getCvLookupID().toLowerCase():"";
                    if ("newt".equals(cvLabel)) {
                        if (!speciesAcc.contains(cvAcc)) {
                            species += ("".equals(species) ? "" : ", ") + name;
                            speciesAcc.add(cvAcc);
                        }
                    } else if ("bto".equals(cvLabel)) {
                        if (!tissuesAcc.contains(cvAcc)) {
                            tissues += ("".equals(tissues) ? "" : ", ") + name;
                            tissuesAcc.add(cvAcc);
                        }
                    }
                }
            }
        }

        speciesField.setText(species);
        speciesField.setCaretPosition(0);

        // tissue field
        tissueField = new JTextField();
        tissueField.setText(tissues);
        tissueField.setCaretPosition(0);


        // instrument field
        instrumentField = new JTextField();
        String instrumentStr = "";

        List<InstrumentConfiguration> instruments = metaData.getInstrumentConfigurations();
        if (instruments.size() > 0) {
            for (InstrumentConfiguration instrument : instruments) {
                instrumentStr += instrument.getId();
            }
        }
        instrumentField.setText(instrumentStr);
        instrumentField.setCaretPosition(0);


        // reference
        if (metaData.getMetaData().getReferences().size() > 0) {
            List<Reference> references = metaData.getReferences();
            referenceTable = TableFactory.createReferenceTable(references);
        } else {
            referenceTable = TableFactory.createReferenceTable(new ArrayList<Reference>());
        }

        // contact

        List<Person> contacts = metaData.getPersonList();
        contactTable = TableFactory.createContactTable(contacts == null ? new ArrayList<Person>() : contacts);

        // additional params
        ParamGroup paramGroup = new ParamGroup();
        List<CvParam> cvParams = metaData.getMetaData().getCvParams();
        if (!cvParams.isEmpty()) {
            for (CvParam cvParam : cvParams) {
                if (cvParam != null) {
                    String acc = cvParam.getAccession();
                    // get project name
                    if (!CvTermReference.PROJECT_NAME.getAccession().equals(acc) &&
                            !CvTermReference.EXPERIMENT_DESCRIPTION.getAccession().equals(acc)) {
                        paramGroup.addCvParam(cvParam);
                    }
                }
            }
        }

        List<UserParam> userParams = metaData.getMetaData().getUserParams();
        if (userParams.size() > 0) {
            paramGroup.addUserParams(userParams);
        }

        additionalTable = TableFactory.createParamTable(paramGroup);
        // hyperlink ontology accessions
        String valColumnHeader = ParamTableModel.TableHeader.VALUE.getHeader();
        TableColumnExt accColumn = (TableColumnExt) additionalTable.getColumn(valColumnHeader);
        accColumn.setCellRenderer(new HyperLinkCellRenderer(Pattern.compile("http.*"), true));

        // add mouse motion listener
        additionalTable.addMouseMotionListener(new TableCellMouseMotionListener(additionalTable, valColumnHeader));
        additionalTable.addMouseListener(new HyperLinkCellMouseClickListener(additionalTable, valColumnHeader, null));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
//        accessionLabel = new JLabel();
        expTitleLabel = new JLabel();
        shortLabel = new JLabel();
        projectLabel = new JLabel();
        expDescLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        referenceLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        contactLabel = new JLabel();
        scrollPane3 = new JScrollPane();
        additionalLabel = new JLabel();
        scrollPane4 = new JScrollPane();
        speciesLabel = new JLabel();
        tissueLabel = new JLabel();
        instrumentLabel = new JLabel();

        //======== this ========
        setFocusable(false);

        //---- accessionLabel ----
//        accessionLabel.setText("Experiment Accession/ID");
//        accessionLabel.setFont(accessionLabel.getFont().deriveFont(accessionLabel.getFont().getStyle() | Font.BOLD));

        //---- expTitleLabel ----
        expTitleLabel.setText("Experiment Title");
        expTitleLabel.setFont(expTitleLabel.getFont().deriveFont(expTitleLabel.getFont().getStyle() | Font.BOLD));

        //---- shortLabel ----
        shortLabel.setText("Experiment Label");
        shortLabel.setFont(shortLabel.getFont().deriveFont(shortLabel.getFont().getStyle() | Font.BOLD));

        //---- projectLabel ----
        projectLabel.setText("Project Name");
        projectLabel.setFont(projectLabel.getFont().deriveFont(projectLabel.getFont().getStyle() | Font.BOLD));

        //---- expDescLabel ----
        expDescLabel.setText("Experiment Description");
        expDescLabel.setFont(expDescLabel.getFont().deriveFont(expDescLabel.getFont().getStyle() | Font.BOLD));

        //---- accessionField ----
//        accessionField.setEditable(false);

        //---- expTitleField ----
        expTitleField.setEditable(false);

        //---- shortLabelField ----
        shortLabelField.setEditable(false);

        //---- projectField ----
        projectField.setEditable(false);

        //======== scrollPane1 ========
        {

            //---- expDescArea ----
            expDescArea.setEditable(false);
            scrollPane1.setViewportView(expDescArea);
        }

        //---- referenceLabel ----
        referenceLabel.setText("Reference");
        referenceLabel.setFont(referenceLabel.getFont().deriveFont(referenceLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setPreferredSize(new Dimension(300, 220));

            //---- referenceTable ----
            referenceTable.setPreferredScrollableViewportSize(new Dimension(400, 200));
            scrollPane2.setViewportView(referenceTable);
        }

        //---- contactLabel ----
        contactLabel.setText("Contact");
        contactLabel.setFont(contactLabel.getFont().deriveFont(contactLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane3 ========
        {

            //---- contactTable ----
            contactTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane3.setViewportView(contactTable);
        }

        //---- additionalLabel ----
        additionalLabel.setText("Additional");
        additionalLabel.setFont(additionalLabel.getFont().deriveFont(additionalLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane4 ========
        {

            //---- additionalTable ----
            additionalTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane4.setViewportView(additionalTable);
        }

        //---- speciesLabel ----
        speciesLabel.setText("Species");
        speciesLabel.setFont(speciesLabel.getFont().deriveFont(speciesLabel.getFont().getStyle() | Font.BOLD));

        //---- speciesField ----
        speciesField.setEditable(false);

        //---- tissueLabel ----
        tissueLabel.setText("Tissue");
        tissueLabel.setFont(tissueLabel.getFont().deriveFont(tissueLabel.getFont().getStyle() | Font.BOLD));
        tissueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //---- tissueField ----
        tissueField.setEditable(false);

        //---- instrumentLabel ----
        instrumentLabel.setText("Instrument");
        instrumentLabel.setFont(instrumentLabel.getFont().deriveFont(instrumentLabel.getFont().getStyle() | Font.BOLD));

        //---- instrumentField ----
        instrumentField.setEditable(false);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(projectLabel)
                                                .addContainerGap(701, Short.MAX_VALUE))
                                        .addComponent(instrumentLabel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(contactLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(referenceLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(additionalLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(scrollPane4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                                                        .addComponent(scrollPane3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup()
//                                                                        .addComponent(accessionLabel)
                                                                        .addComponent(expTitleLabel)
                                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                                .addComponent(speciesLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(shortLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                        .addComponent(expDescLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                                .addComponent(speciesField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(tissueLabel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(tissueField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(expTitleField, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
//                                                                        .addComponent(accessionField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(projectField, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                                                        .addComponent(shortLabelField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(instrumentField, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE))))
                                                .addGap(11, 11, 11))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
//                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
//                                        .addComponent(accessionLabel)
//                                        .addComponent(accessionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(expTitleLabel)
                                        .addComponent(expTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(shortLabel)
                                        .addComponent(shortLabelField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(projectLabel)
                                        .addComponent(projectField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(speciesLabel)
                                        .addComponent(speciesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tissueLabel)
                                        .addComponent(tissueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(instrumentLabel)
                                        .addComponent(instrumentField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(34, 34, 34)
                                                .addComponent(expDescLabel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(referenceLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contactLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(additionalLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                                .addGap(26, 26, 26))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
//    private JLabel accessionLabel;
    private JLabel expTitleLabel;
    private JLabel shortLabel;
    private JLabel projectLabel;
    private JLabel expDescLabel;
//    private JTextField accessionField;
    private JTextField expTitleField;
    private JTextField shortLabelField;
    private JTextField projectField;
    private JScrollPane scrollPane1;
    private JTextPane expDescArea;
    private JLabel referenceLabel;
    private JScrollPane scrollPane2;
    private JTable referenceTable;
    private JLabel contactLabel;
    private JScrollPane scrollPane3;
    private JTable contactTable;
    private JLabel additionalLabel;
    private JScrollPane scrollPane4;
    private JTable additionalTable;
    private JLabel speciesLabel;
    private JTextField speciesField;
    private JLabel tissueLabel;
    private JTextField tissueField;
    private JLabel instrumentLabel;
    private JTextField instrumentField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
