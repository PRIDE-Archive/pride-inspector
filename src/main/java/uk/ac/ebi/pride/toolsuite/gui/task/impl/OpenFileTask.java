package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.access.EmptyDataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.SpectraData;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateProtein;
import uk.ac.ebi.pride.utilities.pia.intermediate.prideimpl.PrideIntermediateProtein;
import uk.ac.ebi.pride.utilities.pia.modeller.PIAModeller;
import uk.ac.ebi.pride.utilities.pia.modeller.protein.inference.InferenceProteinGroup;
import uk.ac.ebi.pride.utilities.pia.modeller.protein.inference.OccamsRazorInference;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.CvScore;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.peptide.PeptideScoring;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.peptide.PeptideScoringUseBestPSM;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.protein.ProteinScoring;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.protein.ProteinScoringAdditive;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.protein.ProteinScoringMultiplicative;
import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Task to open mzML/MzIdentML or PRIDE xml files.
 * <p/>
 * <p/>
 * @author rwang
 * @author ypriverol
 */
public class OpenFileTask<D extends DataAccessController> extends TaskAdapter<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(OpenFileTask.class);
    /**
     * file to open
     */
    private File inputFile;

    private static final int BUFFER_SIZE = 2048;

    /**
     * reference pride inspector context
     */
    private PrideInspectorContext context;

    /**
     * the class type of the data access controller to open the file
     */
    private Class<D> dataAccessControllerClass;

    private List<File> msFiles = null;

    private Boolean inMemory   = false;
    
    private Boolean runProteinInferenceLater = false;

    File path = null;

    public OpenFileTask(File inputFile, Class<D> dataAccessControllerClass, String name, String description) {
        this.inputFile = inputFile;
        this.dataAccessControllerClass = dataAccessControllerClass;
        this.setName(name);
        this.setDescription(description);

        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
    }

    public OpenFileTask(File inputFile, List<File> msFiles, Class<D> dataAccessControllerClass,
                        String name, String description, Boolean inMemory) {
        this.inputFile = inputFile;
        this.dataAccessControllerClass = dataAccessControllerClass;
        this.setName(name);
        this.setDescription(description);
        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
        this.msFiles = msFiles;
        this.inMemory = inMemory;
    }
    
    
    
    public OpenFileTask(File inputFile, List<File> msFiles, Class<D> dataAccessControllerClass,
            String name, String description, Boolean inMemory, Boolean runProteinInferenceLater, File path) {
        this(inputFile, msFiles, dataAccessControllerClass, name, description, inMemory);
        this.path = path;
        this.runProteinInferenceLater = runProteinInferenceLater;
    }
    
    
    @Override
    protected Void doInBackground() throws Exception {


        boolean opened = alreadyOpened(inputFile);
        if (opened) {
            openExistingDataAccessController(inputFile);
        } else {
            checkInterruption();
            // publish a notice for starting the file loading
            publish("Loading " + inputFile.getName());
            if(inMemory)
                createNewDataAccessController(inputFile,Boolean.TRUE);
            else
                createNewDataAccessController(inputFile);
        }
        return null;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.currentThread().interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * Check whether the file has been opened before
     *
     * @param file the input file to check
     * @return boolean true if it has been opened before
     */
    private boolean alreadyOpened(File file) {
        boolean isOpened = false;

        List<DataAccessController> controllers = context.getControllers();
        for (DataAccessController controller : controllers) {
            if (file.equals(controller.getSource())) {
                isOpened = true;
            }
        }

        return isOpened;
    }

    /**
     * This method is called if the experiment is already open, then the experiment will be
     * bring to the foreground.
     *
     * @param file file to open.
     */
    private void openExistingDataAccessController(File file) {
        java.util.List<DataAccessController> controllers = context.getControllers();
        for (DataAccessController controller : controllers) {
            if (DataAccessController.Type.XML_FILE.equals(controller.getType()) &&
                    controller.getSource().equals(file)) {
                context.setForegroundDataAccessController(controller, "loading.title");
            }
        }
    }

    /**
     * Create new DB data access controller
     *
     * @param file file to open
     */
    private void createNewDataAccessController(File file) {

        try {

            long date = System.currentTimeMillis();

            String message = (runProteinInferenceLater)?"loading.proteininferece":"loading.title";
            // create dummy
            EmptyDataAccessController dummy = createEmptyDataAccessController(message);

            Constructor<D> cstruct;
            DataAccessController controller;
            
            if (runProteinInferenceLater) {
                cstruct = dataAccessControllerClass.getDeclaredConstructor(File.class, Boolean.TYPE, Boolean.TYPE);
                controller = cstruct.newInstance(inputFile, Boolean.FALSE, Boolean.TRUE);
            } else {
                cstruct = dataAccessControllerClass.getDeclaredConstructor(File.class);
                controller = cstruct.newInstance(inputFile);
            }

            if(path != null){
                msFiles = unzipMSFiles(msFiles);
            }

            if (MzIdentMLControllerImpl.class.equals(dataAccessControllerClass) && msFiles != null) {
                try {
                    //todo: this is strange way of implement
                    Map<SpectraData, File> msFileMap = ((MzIdentMLControllerImpl) controller).checkMScontrollers(msFiles);
                    ((MzIdentMLControllerImpl) controller).addMSController(msFileMap);
                } catch (DataAccessException e1) {
                    logger.error("Failed to check the files as controllers", e1);
                }
            }

            if (runProteinInferenceLater) {
                infereProteins(controller);
            }
            
            // this is important for cancelling
            if (Thread.interrupted()) {
                // remove dummy
                context.removeDataAccessController(dummy, false, message);
                throw new InterruptedException();
            } else {
                // add the real thing
                context.replaceDataAccessController(dummy, controller, false, message);
            }

            logger.debug("FIRST LOAD | File loading first loading has been done in: |{}| milliseconds", System.currentTimeMillis() - date);

        } catch (InterruptedException ex) {
            logger.warn("File loading has been interrupted: {}", file.getName());
        } catch (Exception err) {
            String msg = "Failed to loading from the file: " + file.getName();
            logger.error(msg, err);
//            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Open File Error");
        }
    }

    /**
     * Create new DB data access controller
     *
     * @param file file to open
     */
    private void createNewDataAccessController(File file, Boolean inMemory) {
        try {
            // create dummy

            long date = System.currentTimeMillis();

            String message = (runProteinInferenceLater)?"loading.proteininferece":"loading.title";

            EmptyDataAccessController dummy = createEmptyDataAccessController(message);

            Constructor<D> cstruct;
            DataAccessController controller;
            
            if (runProteinInferenceLater) {
                cstruct = dataAccessControllerClass.getDeclaredConstructor(File.class, Boolean.TYPE, Boolean.TYPE);
                controller = cstruct.newInstance(inputFile, Boolean.TRUE,  Boolean.TRUE);
            } else {
                cstruct = dataAccessControllerClass.getDeclaredConstructor(File.class, Boolean.TYPE);
                controller = cstruct.newInstance(inputFile, Boolean.TRUE);
            }
            if(path != null){
                msFiles = unzipMSFiles(msFiles);
            }
            
            if (MzIdentMLControllerImpl.class.equals(dataAccessControllerClass) && msFiles != null) {
                try {
                    //todo: this is strange way of implement
                    Map<SpectraData, File> msFileMap = ((MzIdentMLControllerImpl) controller).checkMScontrollers(msFiles);
                    ((MzIdentMLControllerImpl) controller).addMSController(msFileMap);
                } catch (DataAccessException e1) {
                    logger.error("Failed to check the files as controllers", e1);
                }
            }

            if (runProteinInferenceLater) {
                infereProteins(controller);
            }
            
            // this is important for cancelling
            if (Thread.interrupted()) {
                // remove dummy
                context.removeDataAccessController(dummy, false, message);
                throw new InterruptedException();
            } else {
                // add the real thing
                context.replaceDataAccessController(dummy, controller, false, message);
            }

            logger.debug("FIRST LOAD | File loading first loading has been done in: |{}| milliseconds", System.currentTimeMillis() - date);

        } catch (InterruptedException ex) {
            logger.warn("File loading has been interrupted: {}", file.getName());
        } catch (Exception err) {
            String msg = "Failed to loading from the file: " + file.getName();
            logger.error(msg, err);
//            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Open File Error");
        }
    }
    /**
     * Check whether a file is gzip file based its extension.
     *
     * @param file input file
     * @return boolean true means it is a gzip file
     */
    private boolean isGzipFile(File file) {
        return file.getName().endsWith(".gz");
    }

    private List<File> unzipMSFiles(List<File> msFiles) throws Exception {
        String namePath = path.getAbsolutePath().endsWith(System.getProperty("file.separator")) ? path.getAbsolutePath() : path.getAbsolutePath() + System.getProperty("file.separator");
        List<File> newFiles = new ArrayList<>(msFiles.size());
        for (File inputFile : msFiles) {
            if(isGzipFile(inputFile)){
                FileInputStream fis = null;
                GZIPInputStream gs = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                try {
                    fis = new FileInputStream(inputFile);
                    gs = new GZIPInputStream(fis);

                    String outputFile = namePath + inputFile.getName().replace(".gz", "");
                    fos = new FileOutputStream(outputFile);
                    bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                    byte data[] = new byte[BUFFER_SIZE];
                    int count;
                    while ((count = gs.read(data, 0, BUFFER_SIZE)) != -1) {
                        bos.write(data, 0, count);
                    }
                    bos.flush();
                    bos.close();
                    newFiles.add(new File(outputFile));
                } finally {
                    if (fis != null) {
                        fis.close();
                    }

                    if (gs != null) {
                        gs.close();
                    }

                    if (fos != null) {
                        fos.close();
                    }

                    if (bos != null) {
                        bos.close();
                    }
                }
            }else{
                newFiles.add(inputFile);
            }

        }
        return newFiles;
    }

    private EmptyDataAccessController createEmptyDataAccessController(String welcomeMessage) {
        EmptyDataAccessController dummy = new EmptyDataAccessController();
        dummy.setName(inputFile.getName());

        if (dataAccessControllerClass == MzIdentMLControllerImpl.class) {
            dummy.setType(DataAccessController.Type.MZIDENTML);
        } else {
            dummy.setType(DataAccessController.Type.XML_FILE);
        }

        // add a closure hook
        this.addOwner(dummy);
        context.addDataAccessController(dummy, welcomeMessage);
        return dummy;
    }
    
    
    
    /**
     * Run the protein inference using the PIA algorithms and add the resulting
     * protein ambiguity groups to the {@link DataAccessController}
     * 
     * @param controller
     */
    private void infereProteins(DataAccessController controller) {
        PIAModeller piaModeller = new PIAModeller();
        
        CvScore cvScore = null;
        String scoreAccession = null;
        // try to get the main-score
        for (SearchEngineScoreCvTermReference termRef : controller.getAvailablePeptideLevelScores()) {
            CvScore newCvScore;
            scoreAccession = termRef.getAccession();
            newCvScore = CvScore.getCvRefByAccession(termRef.getAccession());
            if ((newCvScore != null) && newCvScore.getIsMainScore()) {
                cvScore = newCvScore;
                scoreAccession = cvScore.getAccession();
                break;
            }
        }
        
        // add the input file to modeller and import data
        Integer controllerID = piaModeller.addPrideControllerAsInput(controller);
        piaModeller.importAllDataFromFile(controllerID);
        
        // first create the intermediate structure from the data given by the controller
        piaModeller.buildIntermediateStructure();
        
        PeptideScoring pepScoring = new PeptideScoringUseBestPSM(scoreAccession, false);
        ProteinScoring protScoring;
        if ((cvScore != null) && !cvScore.getHigherScoreBetter()) {
            protScoring = new ProteinScoringMultiplicative(false, pepScoring);
        } else {
            protScoring = new ProteinScoringAdditive(false, pepScoring);
        }

        
        // perform the protein inferences
        piaModeller.getProteinModeller().infereProteins(pepScoring, protScoring, OccamsRazorInference.class, null, false);
        
        // create the protein groups
        int nrGroups = piaModeller.getProteinModeller().getInferredProteins().size();
        Map<Comparable, Map<Comparable, List<Comparable>>> prideProteinGroupMapping = new HashMap<>(nrGroups);
        
        for (InferenceProteinGroup piaGroup : piaModeller.getProteinModeller().getInferredProteins()) {
            
            Map<Comparable, List<Comparable>> proteinPeptideMap;

            Set<IntermediateProtein> proteinSet = new HashSet<>(piaGroup.getProteins());
            // include the subGroups
            for (InferenceProteinGroup subGroup : piaGroup.getSubGroups()) {
                proteinSet.addAll(subGroup.getProteins());
            }

            proteinPeptideMap = new HashMap<>(proteinSet.size());

            for (IntermediateProtein protein : proteinSet) {
                Comparable proteinID = ((PrideIntermediateProtein)protein).getPrideProteinID();
                // null as the peptide list is interpreted as taking all peptides (PSMs)
                proteinPeptideMap.put(proteinID, null);
            }

            prideProteinGroupMapping.put(piaGroup.getID(), proteinPeptideMap);
        }
        
        controller.setInferredProteinGroups(prideProteinGroupMapping);
    }
}
