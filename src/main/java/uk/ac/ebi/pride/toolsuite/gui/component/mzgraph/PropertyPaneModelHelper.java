package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.util.Collection;
import java.util.List;

/**
 * User: rwang
 * Date: 11-May-2010
 * Time: 14:56:02
 */
public class PropertyPaneModelHelper {

    public static void addSpectrum(PropertyPaneModel paneModel, Spectrum spec) {
        // add common data content
        addGeneralProperties(paneModel, spec);
        DataProcessing dataProc = spec.getDataProcessing();
        if (dataProc != null) {
            addDataProcessings(paneModel, dataProc, Constants.DATA_PROCESSING, Constants.PROCESSING_METHOD);
        }

        // spectrum specific
        // source file
        SourceFile sourceFile = spec.getSourceFile();
        if (sourceFile != null) {
            addSourceFile(paneModel, sourceFile, Constants.SOURCE_FILE, Constants.SOURCE_FILE);
        }
        // scan list
        ScanList scanList = spec.getScanList();
        if (scanList != null) {
            addScanList(paneModel, scanList);
        }

        // precursor
        List<Precursor> precursors = spec.getPrecursors();
        if (!precursors.isEmpty()) {
            addPrecursors(paneModel, precursors);
        }
        // product
        List<ParamGroup> products = spec.getProducts();
        if (!products.isEmpty()) {
            addProducts(paneModel, products);
        }
    }

    public static void addChromatogram(PropertyPaneModel paneModel, Chromatogram chroma) {
        // add common data content
        addGeneralProperties(paneModel, chroma);
        DataProcessing dataProc = chroma.getDataProcessing();
        if (dataProc != null) {
            addDataProcessings(paneModel, dataProc, Constants.DATA_PROCESSING, Constants.PROCESSING_METHOD);
        }
    }


    private static void addGeneralProperties(PropertyPaneModel paneModel, MzGraph mzGraph) {
        // id
        appendParamEntry(paneModel, Constants.ID, mzGraph.getId().toString(), Constants.GENERAL, Constants.GENERAL);
        // index
        appendParamEntry(paneModel, Constants.INDEX, mzGraph.getIndex() + "", Constants.GENERAL, Constants.GENERAL);
        // defaultArrayLength
        appendParamEntry(paneModel, Constants.DEFAULT_ARR_LEN, mzGraph.getDefaultArrayLength() + "", Constants.GENERAL, Constants.GENERAL);
        // additional parameters
        appendParamGroup(paneModel, mzGraph, Constants.GENERAL, Constants.GENERAL);
    }

    private static void addDataProcessings(PropertyPaneModel paneModel, DataProcessing dataProc,
                                           String category, String subCategory) {
        // data processing - processing method
        List<ProcessingMethod> proMethods = dataProc.getProcessingMethods();

        for (int i = 0; i < proMethods.size(); i++) {
            ProcessingMethod proMethod = proMethods.get(i);
            String subCategoryTitle = subCategory + " " + i;

            // order
            appendParamEntry(paneModel, Constants.ORDER, proMethod.getOrder() + "", category, subCategoryTitle);

            // software
            Software software = proMethod.getSoftware();
            if (software != null) {
                // software id
                appendParamEntry(paneModel, Constants.SOFTWARE_ID, software.getId().toString(), category, subCategoryTitle);
                // software version
                appendParamEntry(paneModel, Constants.SOFTWARE_VERSION, software.getVersion(), category, subCategoryTitle);
                // software param group
                appendParamGroup(paneModel, software, category, subCategoryTitle);
            }

            // param group
            appendParamGroup(paneModel, proMethod, category, subCategoryTitle);
        }
    }

    private static void addSourceFile(PropertyPaneModel paneModel, SourceFile sourceFile,
                                      String category, String subcategory) {
        // id
        appendParamEntry(paneModel, Constants.SOURCE_FILE_ID, sourceFile.getId().toString(), category, subcategory);
        // name
        appendParamEntry(paneModel, Constants.SOURCE_FILE_NAME, sourceFile.getName(), category, subcategory);
        // path
        appendParamEntry(paneModel, Constants.SOURCE_FILE_PATH, sourceFile.getPath(), category, subcategory);
        // additional params
        appendParamGroup(paneModel, sourceFile, category, subcategory);
    }

    private static void addScanList(PropertyPaneModel paneModel, ScanList scanList) {
        // scans
        List<Scan> scans = scanList.getScans();

        for (int i = 0; i < scans.size(); i++) {
            Scan scan = scans.get(i);
            String category = Constants.SCAN + " [" + (i + 1) + "]";
            // general
            appendParamGroup(paneModel, scanList, category, Constants.GENERAL);
            // external spectrum reference
            appendParamEntry(paneModel, Constants.EXTERNAL_SPECTRUM_REF, scan.getExternalSpecRef(), category, Constants.GENERAL);
            // spectrum reference
            appendParamEntry(paneModel, Constants.SPECTRUM_ID, scan.getSpectrumRef(), category, Constants.GENERAL);

            // source file
            SourceFile sourceFile = scan.getSourceFile();
            if (sourceFile != null) {
                addSourceFile(paneModel, sourceFile, category, Constants.SOURCE_FILE);
            }
            // instrument
            // ToDo: not instrument details yet
            // scan windows
            List<ParamGroup> scanWins = scan.getScanWindows();
            if (!scanWins.isEmpty()) {
                for (int j = 0; j < scanWins.size(); j++) {
                    String subCategory = Constants.SCAN_WINDOW + " [" + (j + 1) + "]";
                    appendParamGroup(paneModel, scanWins.get(j), category, subCategory);
                }
            }
        }
    }

    private static void addPrecursors(PropertyPaneModel paneModel, List<Precursor> precursors) {

        for (int i = 0; i < precursors.size(); i++) {
            Precursor precursor = precursors.get(i);
            String category = Constants.PRECURSOR + " [" + (i + 1) + "]";
            // general info
            Spectrum spectrum = precursor.getSpectrum();
            if (spectrum != null) {
                appendParamEntry(paneModel, Constants.SPECTRUM_ID, spectrum.getId().toString(), category, Constants.GENERAL);
            }
            // external spectrum id
            appendParamEntry(paneModel, Constants.EXTERNAL_SPECTRUM_REF, precursor.getExternalSpectrumID(), category, Constants.GENERAL);
            // source file
            SourceFile sourceFile = precursor.getSourceFile();
            if (sourceFile != null) {
                addSourceFile(paneModel, sourceFile, category, Constants.SOURCE_FILE);
            }
            // isolation window
            ParamGroup isoWin = precursor.getIsolationWindow();
            if (isoWin != null) {
                appendParamGroup(paneModel, isoWin, category, Constants.ISOLATION_WINDOW);
            }
            // selected ions
            List<ParamGroup> selectedIons = precursor.getSelectedIons();
            if (!selectedIons.isEmpty()) {
                for (int j = 0; j < selectedIons.size(); j++) {
                    String subCategory = Constants.SELECTED_ION + " [" + (j + 1) + "]";
                    appendParamGroup(paneModel, selectedIons.get(j), category, subCategory);
                }
            }

            // activation
            ParamGroup act = precursor.getActivation();
            if (act != null) {
                appendParamGroup(paneModel, act, category, Constants.ACTIVATION);
            }
        }
    }

    private static void addProducts(PropertyPaneModel paneModel, List<ParamGroup> products) {
        for (ParamGroup product : products) {
            appendParamGroup(paneModel, product, Constants.PRODUCTS, Constants.PRODUCT);
        }
    }

    private static void appendParamEntry(PropertyPaneModel paneModel, String name, String value,
                                         String category, String subCategory) {
        if (name != null && value != null) {
            Parameter userParam = new UserParam(name, null, value, null, null, null);
            paneModel.appendData(category, subCategory, userParam);
        }
    }

    private static void appendParamGroup(PropertyPaneModel paneModel, ParamGroup params,
                                         String category, String subCategory) {
        List<CvParam> cvParams = params.getCvParams();
        if (!cvParams.isEmpty()) {
            paneModel.appendData(category, subCategory, (Collection) cvParams);
        }
        List<UserParam> userParams = params.getUserParams();
        if (!userParams.isEmpty()) {
            paneModel.appendData(category, subCategory, (Collection) userParams);
        }
    }
}
