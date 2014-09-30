package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.quant.QuantSamplePane;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author ypriverol
 * @author rwang
 */
public class SampleInfoAction extends PrideAction {

    private static final String DIALOG_TITLE = "Samples Description";
    DataAccessController controller;
    Point point;
    Frame owner;

    public SampleInfoAction(Frame owner, String name, Icon icon, DataAccessController controller, Point point) {
        super(name, icon);
        this.controller = controller;
        this.point = point;
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JDialog sampleInfo = new JDialog(owner);
        sampleInfo.setTitle(DIALOG_TITLE);
        sampleInfo.setPreferredSize(new Dimension(400, 245));

        sampleInfo.setSize(new Dimension(400, 245));
        Container c = sampleInfo.getContentPane();
        c.setLayout( new FlowLayout() );
        QuantSamplePane quantSamplePane = new QuantSamplePane(controller);
        //sampleInfo.add(quantSamplePane);
        c.add( quantSamplePane );

        sampleInfo.setLocationRelativeTo(sampleInfo.getOwner());
        sampleInfo.pack();
        sampleInfo.show();
        sampleInfo.setModal(true);
        sampleInfo.setVisible(true);
    }
}
