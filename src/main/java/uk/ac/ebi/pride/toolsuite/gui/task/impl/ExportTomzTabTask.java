package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileConverter;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabErrorList;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.exporters.AbstractMzTabConverter;
import uk.ac.ebi.pride.utilities.data.exporters.MzIdentMLMzTabConverter;
import uk.ac.ebi.pride.utilities.data.exporters.PRIDEMzTabConverter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * This class allow pride inspector to export the results inf the controller to an mztab files. The
 * @author yperez
 *
 */


public class ExportTomzTabTask extends AbstractDataAccessTask<Void, Void> {

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Results to mzTab";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Results to mzTab";


    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file path to output the result.
     */
    public ExportTomzTabTask(DataAccessController controller, String outputFilePath) {
        super(controller);
        this.outputFilePath = outputFilePath;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {
        AbstractMzTabConverter mzTabConverter = null;
        MZTabFile mzTabFile = null;
        if(controller instanceof PrideXmlControllerImpl){
            mzTabConverter = new PRIDEMzTabConverter(controller);
            mzTabFile = mzTabConverter.getMZTabFile();
        }else if(controller instanceof MzIdentMLControllerImpl){
            mzTabConverter = new MzIdentMLMzTabConverter(controller);
            mzTabFile = mzTabConverter.getMZTabFile();
        }
        if(mzTabFile != null){
            MZTabFileConverter checker = new MZTabFileConverter();
            checker.check(mzTabFile);

            MZTabErrorList errorList = checker.getErrorList();

            OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFilePath));

            if (errorList.isEmpty()) {
                mzTabFile.printMZTab(out);
            }
        }
        return null;
    }
}
