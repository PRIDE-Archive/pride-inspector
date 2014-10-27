package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.utilities.util.NumberUtilities;
import uk.ac.ebi.pride.utilities.util.RegExUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: rwang
 * Date: 25-May-2010
 * Time: 10:52:22
 */
public class MetaDataHelper {
    /**
     * Get general metadata into a collection of paraemters
     *
     * @param metaData
     * @return
     */
    public static Collection<Parameter> getGeneralContent(ExperimentMetaData metaData) {
        Collection<Parameter> params = new ArrayList<Parameter>();

        // id
        addUserParam(params, Constants.ID, metaData.getId());

        // accession
        addUserParam(params, Constants.ACCESSION, metaData.getId().toString());

        // version
        addUserParam(params, Constants.VERSION, metaData.getVersion());

        // add experiment related meta data
        if (metaData instanceof ExperimentMetaData) {
            addUserParam(params, Constants.TITLE, (metaData).getName());
            addUserParam(params, Constants.SHORT_LABEL, (metaData).getShortLabel());
            addUserParam(params, Constants.CREATION_DATE, (metaData).getCreationDate());
            addUserParam(params, Constants.PUBLIC_DATE, (metaData).getPublicDate());
        }

        // additional parameters
        addParamGroup(params, metaData, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getParamGroup(ParamGroup paramGroup) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        addParamGroup(params, paramGroup, Constants.EMPTY);
        return params;
    }

    public static Collection<Parameter> getSourceFile(SourceFile sourceFile) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        // id
        addUserParam(params, Constants.ID, sourceFile.getId());
        // name
        addUserParam(params, Constants.NAME, sourceFile.getName());
        // path
        addUserParam(params, Constants.PATH, sourceFile.getPath());

        // additional parameters
        addParamGroup(params, sourceFile, Constants.EMPTY);
        return params;
    }

    public static Collection<Parameter> getSample(Sample sample) {
        Collection<Parameter> params = new ArrayList<Parameter>();

        // id
        addUserParam(params, Constants.ID, sample.getId());
        // name
        addUserParam(params, Constants.NAME, sample.getName());
        // additional parameters
        addParamGroup(params, sample, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getContact(ParamGroup contact) {
        Collection<Parameter> params = getParamGroup(contact);

        // iterate over all params, check for email addresses.
        for (Parameter param : params) {
            if (RegExUtilities.isValidEmail(param.getValue())) {
                String email = param.getValue();
                param.setValue("<html><head></head><body><a href='mailto:" + email + "'>" + email + "</a></body></html>");
            }
        }

        return params;
    }

    public static Collection<Parameter> getSoftware(Software software) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        // id
        addUserParam(params, Constants.ID, software.getId());
        // name
        addUserParam(params, Constants.VERSION, software.getVersion());
        // additional parameters
        addParamGroup(params, software, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getScanSetting(ScanSetting scanSetting) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        // id
        addUserParam(params, Constants.ID, scanSetting.getId());
        // source file id
        List<SourceFile> sourceFiles = scanSetting.getSourceFile();
        if (!sourceFiles.isEmpty()) {
            for (SourceFile sourceFile : sourceFiles) {
                addUserParam(params, Constants.SOURCE_FILE_ID, sourceFile.getId());
            }
        }

        // targets
        List<ParamGroup> targets = scanSetting.getTargets();
        if (!targets.isEmpty()) {
            for (int i = 0; i < targets.size(); i++) {
                String prefix = Constants.TARGET + " " + (i + 1) + " - ";
                addParamGroup(params, targets.get(i), prefix);
            }
        }

        // additional parameters
        addParamGroup(params, scanSetting, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getInstrumentConfiguration(InstrumentConfiguration instrument) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        // id
        addUserParam(params, Constants.ID, instrument.getId());
        // scan settings
        ScanSetting scanSetting = instrument.getScanSetting();
        if (scanSetting != null) {
            addUserParam(params, Constants.SCAN_SETTING_ID, scanSetting.getId());
        }
        // software
        Software software = instrument.getSoftware();
        if (software != null) {
            addUserParam(params, Constants.SOFTWARE_ID, software.getId());
        }
        // source
        // source order
        InstrumentComponent source = instrument.getSource().get(0);
        if (source != null) {
            String sourcePrefix = Constants.SOURCE + " - ";
            //addUserParam(params, sourcePrefix + SharedLabels.ORDER, source.getOrder());
            addParamGroup(params, source, sourcePrefix);
        }

        // analyzer
        InstrumentComponent analyzer = instrument.getAnalyzer().get(0);
        if (analyzer != null) {
            String analyzerPrefix = Constants.ANALYZER + " - ";
            //addUserParam(params, analyzerPrefix + SharedLabels.ORDER, analyzer.getOrder());
            addParamGroup(params, analyzer, analyzerPrefix);
        }

        // source
        InstrumentComponent detector = instrument.getDetector().get(0);
        if (detector != null) {
            String detectorPrefix = Constants.DETECTOR + " - ";
            //addUserParam(params, detectorPrefix + SharedLabels.ORDER, detector.getOrder());
            addParamGroup(params, detector, detectorPrefix);
        }

        // additional parameters
        addParamGroup(params, instrument, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getDataProcessing(DataProcessing dataProc) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        // id
        addUserParam(params, Constants.ID, dataProc.getId());

        // processing methods
        List<ProcessingMethod> proMethods = dataProc.getProcessingMethods();
        if (proMethods.size() > 0) {
            for (int i = 0; i < proMethods.size(); i++) {
                String prefix = Constants.PROCESSING_METHOD + " " + (i + 1) + " - ";
                ProcessingMethod proMethod = proMethods.get(i);
                // order
                //addUserParam(params, prefix + SharedLabels.ORDER, proMethod.getOrder());
                // software
                Software software = proMethod.getSoftware();
                if (software != null) {
                    addUserParam(params, prefix + Constants.SOFTWARE_ID, software.getId());
                }
                // param group
                addParamGroup(params, proMethod, prefix);
            }
        }
        return params;
    }

    public static Collection<Parameter> getProtocol(Protocol protocol) {
        Collection<Parameter> params = new ArrayList<Parameter>();

        // id
        addUserParam(params, Constants.ID, protocol.getId());
        // name
        addUserParam(params, Constants.NAME, protocol.getName());
        // protocol steps
        List<ParamGroup> steps = new ArrayList<ParamGroup>();
        steps.add(protocol.getAnalysisParam());
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                String prefix = Constants.PROTOCOL_STEP + " " + (i + 1) + " - ";
                ParamGroup step = steps.get(i);
                addParamGroup(params, step, prefix);
            }
        }
        // additaional param
        addParamGroup(params, protocol, Constants.EMPTY);

        return params;
    }

    public static Collection<Parameter> getReference(Reference reference) {
        Collection<Parameter> params = new ArrayList<Parameter>();

        // full reference
        addUserParam(params, Constants.FULL_REFERENCE, reference.getFullReference());
        // make a copy of the param group
        ParamGroup customParams = new ParamGroup();
        List<CvParam> originalCvParams = reference.getCvParams();
        if (originalCvParams != null) {
            customParams.addCvParams(originalCvParams);
        }

        List<UserParam> originalUserParams = reference.getUserParams();
        if (originalUserParams != null) {
            customParams.addUserParams(originalUserParams);
        }
        // find the pubmed cv param
        List<CvParam> cvParams = customParams.getCvParams();
        CvParam pubmedCvParam = null;
        CvParam doiCvParam = null;
        for (CvParam cvParam : cvParams) {
            if (cvParam.getCvLookupID().toLowerCase().equals("pubmed") || cvParam.getName().toLowerCase().equals("pubmed")) {
                pubmedCvParam = cvParam;
            } else if (cvParam.getCvLookupID().toLowerCase().equals("doi") || cvParam.getName().toLowerCase().equals("doi")) {
                doiCvParam = cvParam;
            }
        }

        // remove then set pubmed cv param
        if (pubmedCvParam != null) {
            customParams.removeCvParam(pubmedCvParam);
            // add pubmed cv param
            String acc = pubmedCvParam.getValue();
            if (acc == null || !NumberUtilities.isNumber(acc)) {
                acc = pubmedCvParam.getAccession();
            }

            if (acc != null || !NumberUtilities.isNumber(acc)) {
                String hyperLink = "<html><head></head><body><a href='http://www.ncbi.nlm.nih.gov/pubmed/" + acc + "'>" + acc + "</a></body></html>";
                CvParam newCvParam = new CvParam(acc, Constants.PUBMED, pubmedCvParam.getCvLookupID(),
                        hyperLink, pubmedCvParam.getUnitAcc(), pubmedCvParam.getUnitName(), pubmedCvParam.getUnitCVLookupID());
                params.add(newCvParam);
            }
        }

        // remove then set doi cv param
        if (doiCvParam != null) {
            customParams.removeCvParam(doiCvParam);
            // add doi cv param
            String doi = doiCvParam.getValue();
            if (doi == null) {
                doi = doiCvParam.getAccession();
            }

            if (doi != null) {
                String hyperLink = "<html><head></head><body><a href='http://dx.doi.org/" + doi + "'>" + doi + "</a></body></html>";
                CvParam newCvParam = new CvParam(doi, Constants.DOI, doiCvParam.getCvLookupID(),
                        hyperLink, doiCvParam.getUnitAcc(), doiCvParam.getUnitName(), doiCvParam.getUnitCVLookupID());
                params.add(newCvParam);
            }
        }

        // additional parameters
        addParamGroup(params, customParams, Constants.EMPTY);

        return params;
    }

    private static void addParamGroup(Collection<Parameter> params, ParamGroup paramGroup, String prefix) {
        if (paramGroup != null && !paramGroup.isEmpty()) {
            List<CvParam> cvParams = paramGroup.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    String name = cvParam.getName();
                    String value = cvParam.getValue();

                    if (value == null || "".equals(value.trim())) {
                        value = name;
                        name = prefix + Constants.PARAMETER;
                    } else {
                        name = prefix + name;
                    }
                    params.add(new CvParam(cvParam.getAccession(), name, cvParam.getCvLookupID(),
                            convertToHyperLink(value), cvParam.getUnitAcc(), cvParam.getUnitName(), cvParam.getUnitCVLookupID()));
                }
            }

            List<UserParam> userParams = paramGroup.getUserParams();
            if (userParams != null) {
                for (UserParam userParam : userParams) {
                    params.add(new UserParam(prefix + userParam.getName(), userParam.getType(), convertToHyperLink(userParam.getValue()),
                            userParam.getUnitAcc(), userParam.getUnitName(), userParam.getUnitCVLookupID()));
                }
            }
        }
    }

    private static String convertToHyperLink(String val) {
        if (val != null) {
            val = val.trim();
            if (val.startsWith("http") && !val.contains(" ")) {
                val = "<html><a href='" + val + "'> Link</a></html>";
            }
        }
        return val;
    }

    private static void addUserParam(Collection<Parameter> params, String name, Object value) {
        if (value != null) {
            params.add(new UserParam(name, null, value.toString(), null, null, null));
        }
    }
}
