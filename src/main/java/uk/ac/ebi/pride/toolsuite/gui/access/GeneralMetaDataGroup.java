package uk.ac.ebi.pride.toolsuite.gui.access;

import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.utilities.data.utils.CollectionUtils;

import java.util.List;

/**
 * User: ypriverol
 * Date: 2/1/12
 * Time: 5:15 AM
 */
public class GeneralMetaDataGroup {

    IdentificationMetaData identificationMetaData = null;

    ExperimentMetaData metaData = null;

    MzGraphMetaData mzGraphMetaData = null;

    public GeneralMetaDataGroup(IdentificationMetaData identificationMetaData, ExperimentMetaData metaData, MzGraphMetaData mzGraphMetaData) {
        this.identificationMetaData = identificationMetaData;
        this.metaData = metaData;
        this.mzGraphMetaData = mzGraphMetaData;
    }

    public IdentificationMetaData getIdentificationMetaData() {
        return identificationMetaData;
    }

    public void setIdentificationMetaData(IdentificationMetaData identificationMetaData) {
        this.identificationMetaData = identificationMetaData;
    }

    public ExperimentMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ExperimentMetaData metaData) {
        this.metaData = metaData;
    }

    public MzGraphMetaData getMzGraphMetaData() {
        return mzGraphMetaData;
    }

    public void setMzGraphMetaData(MzGraphMetaData mzGraphMetaData) {
        this.mzGraphMetaData = mzGraphMetaData;
    }

    public Object getId() {
        if (getMetaData() == null) {
            return null;
        }
        return this.getMetaData().getId();
    }

    public String getName() {
        if (getMetaData() == null) {
            return null;
        }
        return this.getMetaData().getName();
    }

    public String getShortLabel() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().getShortLabel();
    }

    public List<InstrumentConfiguration> getInstrumentConfigurations() {
        List<InstrumentConfiguration> instrumentConfigurations = null;
        instrumentConfigurations = CollectionUtils.createListFromList(instrumentConfigurations);
        if (hasMzGraphMetadata()) {
            instrumentConfigurations = getMzGraphMetaData().getInstrumentConfigurations();
        }
        return instrumentConfigurations;
    }

    public List<Reference> getReferences() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().getReferences();
    }

    public List<Person> getPersonList() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().getPersons();
    }

    public List<Sample> getSampleList() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().getSamples();
    }

    public ExperimentProtocol getProtocol() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().getProtocol();
    }

    public List<DataProcessing> getDataProcessings() {
        if (getMzGraphMetaData() == null) {
            return CollectionUtils.createEmptyList();
        }
        return getMzGraphMetaData().getDataProcessings();
    }

    public List<SearchDataBase> getSearchDatabases() {
        if (getIdentificationMetaData() == null) {
            return null;
        }
        return getIdentificationMetaData().getSearchDataBases();
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() {
        if (getIdentificationMetaData() == null) {
            return CollectionUtils.createEmptyList();
        }
        return getIdentificationMetaData().getSpectrumIdentificationProtocols();
    }

    public Protocol getProteinDetectionProtocol() {
        if (getIdentificationMetaData() == null) {
            return null;
        }
        return this.getIdentificationMetaData().getProteinDetectionProtocol();
    }

    public boolean hasIdentificationMetadata() {
        return getIdentificationMetaData() != null;
    }

    public boolean hasMzGraphMetadata() {
        return getMzGraphMetaData() != null;

    }

    public boolean hasSampleProtocolMetadata() {
        return metaData.getSamples().size() > 0 || metaData.getProtocol() != null;
    }
}
