package uk.ac.ebi.pride.toolsuite.gui.access;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.AbstractDataAccessController;
import uk.ac.ebi.pride.utilities.data.core.*;

import java.util.Collection;
import java.util.Collections;

/**
 * This represents an dummy data access controller, which has not source and not data.
 *
 * User: rwang
 * Date: 01-Nov-2010
 * Time: 15:30:42
 */
public class EmptyDataAccessController extends AbstractDataAccessController {

    public EmptyDataAccessController() {
    }

    public EmptyDataAccessController(Object source) {
        super(source);
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Sample> getSamples() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getProteinIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Protein getProteinById(Comparable id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getChromatogramIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        return null;
    }
}
