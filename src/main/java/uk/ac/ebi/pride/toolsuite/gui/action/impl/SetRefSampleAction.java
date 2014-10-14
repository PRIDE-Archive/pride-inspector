package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.QuantitativeSample;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.ReferenceSampleChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action to set a control sample
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 15/08/2011
 * Time: 17:07
 */
public class SetRefSampleAction extends PrideAction {
    private static final Logger logger = LoggerFactory.getLogger(SetRefSampleAction.class);

    private DataAccessController controller;
    private JPopupMenu controlSampleMenu;

    public SetRefSampleAction(DataAccessController controller) {
        super(!controller.getType().equals(DataAccessController.Type.MZTAB)?Desktop.getInstance().getDesktopContext().getProperty("set.control.sample.title"):Desktop.getInstance().getDesktopContext().getProperty("set.quantification.variables.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("set.control.sample.small.icon")));
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (controlSampleMenu == null) {
            // create new dialog
            controlSampleMenu = createPopupMenu(button);
        }
        Point location = button.getLocation();
        controlSampleMenu.show(button, (int) location.getX(), (int) location.getY() + button.getHeight());
    }

    private JPopupMenu createPopupMenu(JComponent source) {
        JPopupMenu menu = new JPopupMenu();

        try {
            if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
                QuantitativeSample sample = controller.getQuantSample();
                int refSampelIndex = controller.getReferenceSubSampleIndex();
                ButtonGroup buttonGroup = new ButtonGroup();
                for (int i = 1; i <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
                    CvParam reagent = sample.getReagent(i);
                    if (reagent != null) {
                        JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(reagent.getName(), i == refSampelIndex);
                        radioButton.addActionListener(new RefSampleActionListener(source, i));
                        menu.add(radioButton);
                        buttonGroup.add(radioButton);
                    }
                }
            }else{
                ButtonGroup buttonGroup = new ButtonGroup();
                JRadioButtonMenuItem radioButtonStudy     = new JRadioButtonMenuItem("Study Variables", true);
                JRadioButtonMenuItem radioButtonAbundace  = new JRadioButtonMenuItem("Abundance Variables", false);
                radioButtonStudy.addActionListener(new RefSampleActionListener(source, 0));
                radioButtonAbundace.addActionListener(new RefSampleActionListener(source, 1));
                menu.add(radioButtonStudy);
                menu.add(radioButtonAbundace);
                buttonGroup.add(radioButtonStudy);
                buttonGroup.add(radioButtonAbundace);
            }
        } catch (DataAccessException e) {
            logger.error("Failed to get quantitative sample descriptions");
        }
        return menu;
    }

    private class RefSampleActionListener implements ActionListener {

        private JComponent source;
        private int refSampleIndex;

        private RefSampleActionListener(JComponent source, int refSampleIndex) {
            this.source = source;
            this.refSampleIndex = refSampleIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // publish the event to local event bus
            EventService eventBus = ContainerEventServiceFinder.getEventService(source);
            eventBus.publish(new ReferenceSampleChangeEvent(source, refSampleIndex));
        }


    }
}
