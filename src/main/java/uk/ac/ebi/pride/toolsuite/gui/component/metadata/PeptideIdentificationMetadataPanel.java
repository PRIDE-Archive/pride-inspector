package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ypriverol
 * Date: 2/1/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideIdentificationMetadataPanel extends JPanel {

    public PeptideIdentificationMetadataPanel(SpectrumIdentificationProtocol peptideProtocol) {
        populateComponents(peptideProtocol);
        initComponents();
    }

    private void populateComponents(SpectrumIdentificationProtocol peptideProtocol) {
        // SearchType
        if (peptideProtocol != null) {
            ParamGroup searchType = peptideProtocol.getSearchType();
            ParamGroup analysisParam = peptideProtocol.getAnalysisParam();
            if (searchType != null) {
                if (!analysisParam.isEmpty()) {
                    searchType.addCvParams(analysisParam.getCvParams());
                    searchType.addUserParams(analysisParam.getUserParams());
                }
            } else if (!analysisParam.isEmpty()) {
                searchType = analysisParam;
            }
            if (searchType != null) {
                searchtypeTable = TableFactory.createParamTable(searchType);
            } else {
                searchtypeTable = TableFactory.createParamTable(new ArrayList<Parameter>());
            }
            // Enzyme
            List<Enzyme> enzymeList = peptideProtocol.getEnzymes();
            ParamGroup enzymeParamGroup = new ParamGroup();
            if (!enzymeList.isEmpty()) {
                for (Enzyme enzyme : enzymeList) {
                    if (!enzyme.getEnzymeName().isEmpty())
                        enzymeParamGroup.addCvParams(enzyme.getEnzymeName().getCvParams());
                    if (!enzyme.getEnzymeName().isEmpty())
                        enzymeParamGroup.addUserParams(enzyme.getEnzymeName().getUserParams());
                }
            }

            if (enzymeParamGroup != null) {
                thresholdTable = TableFactory.createParamTable(enzymeParamGroup);
            } else {
                thresholdTable = TableFactory.createParamTable(new ArrayList<Parameter>());
            }

            // detector
            List<CvParam> fragmentTolerance = peptideProtocol.getFragmentTolerance();
            List<CvParam> parentTolerance = peptideProtocol.getParentTolerance();
            ParamGroup threshold = peptideProtocol.getThreshold();

            ParamGroup allThreshold = new ParamGroup();

            if (!threshold.getCvParams().isEmpty()) allThreshold.addCvParams(threshold.getCvParams());
            if (!threshold.getUserParams().isEmpty()) allThreshold.addUserParams(threshold.getUserParams());
            if (!fragmentTolerance.isEmpty()) allThreshold.addCvParams(fragmentTolerance);
            if (!parentTolerance.isEmpty()) allThreshold.addCvParams(parentTolerance);

            if (allThreshold != null) {
                enzymesTable = TableFactory.createParamTable(allThreshold);
            } else {
                enzymesTable = TableFactory.createParamTable(new ArrayList<Parameter>());
            }
        } else {
            searchtypeTable = TableFactory.createParamTable(new ArrayList<Parameter>());
            thresholdTable = TableFactory.createParamTable(new ArrayList<Parameter>());
            enzymesTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }


    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        searchtypeLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        enzymesLabel = new JLabel();
        thresholdLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        scrollPane3 = new JScrollPane();

        //======== this ========

        //---- searchtypeLabel ----
        searchtypeLabel.setText("Search Type & Search Parameters");


        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(searchtypeTable);
        }

        //---- enzymesLabel ----
        enzymesLabel.setText("Enzymes");

        //---- thresholdLabel ----
        thresholdLabel.setText("Thresholds & Fragment/Parent Tolerances");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(enzymesTable);
        }

        //======== scrollPane3 ========
        {
            scrollPane3.setViewportView(thresholdTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .addComponent(scrollPane3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .addComponent(searchtypeLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(enzymesLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(thresholdLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(searchtypeLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(enzymesLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thresholdLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel searchtypeLabel;
    private JScrollPane scrollPane1;
    private JTable searchtypeTable;
    private JLabel enzymesLabel;
    private JLabel thresholdLabel;
    private JScrollPane scrollPane2;
    private JTable enzymesTable;
    private JScrollPane scrollPane3;
    private JTable thresholdTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
