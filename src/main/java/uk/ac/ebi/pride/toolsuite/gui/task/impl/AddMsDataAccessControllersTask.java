package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.SpectraData;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.chart.ChartTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.protein.ProteinTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.event.SpectrumAddEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ypriverol
 * Date: 3/4/13
 * Time: 2:48 PM
 */
public class AddMsDataAccessControllersTask extends TaskAdapter<Void, Map<SpectraData, File>> {

    DataAccessController controller;
    Map<SpectraData, File> spectraDataFileMap;
    Map<Comparable, File> newFiles;
    Map<Comparable, String> fileTypes;
    private PrideInspectorContext context = null;

    public AddMsDataAccessControllersTask(DataAccessController controller, Map<Comparable, File> newfiles, Map<Comparable, String> fileTypes, Map<SpectraData, File> spectraDataMap) {
        this.controller = controller;
        spectraDataFileMap = spectraDataMap;
        this.newFiles = newfiles;
        this.fileTypes = fileTypes;
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            boolean status = ((MzIdentMLControllerImpl) controller).addNewMSController(spectraDataFileMap, newFiles, fileTypes);

            if (status) {
                //((MzIdentMLControllerImpl) controller).addMSController(msFileMap);
                ControllerContentPane contentPane = (ControllerContentPane) context.getDataContentPane(controller);

                //Update the Summary Charts Tab
                ChartTabPane chartContentPane = contentPane.getChartTabPane();
                chartContentPane.populate();

                //Update the Spectrum Tab
                MzDataTabPane mzDataTabPane;
                int index = contentPane.getMzDataTabIndex();
                mzDataTabPane = new MzDataTabPane(controller, contentPane);
                contentPane.removeTab(index);
                contentPane.setMzDataTab(mzDataTabPane);
                contentPane.insertTab(mzDataTabPane.getTitle(), mzDataTabPane.getIcon(), mzDataTabPane, mzDataTabPane.getTitle(), index);
                boolean hasSpectrum = controller.hasSpectrum();
                boolean hasChromatogram = controller.hasChromatogram();
                contentPane.setEnableAt(index, hasSpectrum || hasChromatogram);
                if (hasSpectrum || hasChromatogram) {
                    mzDataTabPane.spectrumChange();
                    mzDataTabPane.populate();
                }
                PeptideTabPane peptideContentPane = contentPane.getPeptideTabPane();
                //Update Protein Tab
                ProteinTabPane proteinTabPane = contentPane.getProteinTabPane();

                if (hasSpectrum || hasChromatogram) {

                    //Add Spectrum View
                    peptideContentPane.getVizTabPane().addSpectrumViewPane();
                    //Add Spectrum View
                    peptideContentPane.getVizTabPane().addFragmentationViewPane();

                    peptideContentPane.peptideChange();
                    contentPane.populate();

                    proteinTabPane.getVizTabPane().addSpectrumViewPane();
                    proteinTabPane.getVizTabPane().addFragmentationViewPane();
                    proteinTabPane.peptideChange();
                    proteinTabPane.populate();

                    RetrievePeptideSpectrumDetailTask task = new RetrievePeptideSpectrumDetailTask(controller);
                    JTable table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
                    TableModel tableModel = table.getModel();
                    task.addTaskListener((TaskListener) tableModel);

                    TaskUtil.startBackgroundTask(task, controller);
                    EventBus.publish(new SpectrumAddEvent<DataAccessController>(this, controller, SpectrumAddEvent.Status.SPECTRUM_ADDED));

                    contentPane.populate();
                } else {
                    peptideContentPane.getVizTabPane().removeSpectrumViewPane();
                    peptideContentPane.getVizTabPane().removeFragmentationViewPane();


                    proteinTabPane.getVizTabPane().removeSpectrumViewPane();
                    proteinTabPane.getVizTabPane().removeFragmentationViewPane();
                    EventBus.publish(new SpectrumAddEvent<DataAccessController>(this, controller, SpectrumAddEvent.Status.SPECTRUM_REMOVED));
                }
            }


        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
