package uk.ac.ebi.pride.toolsuite.gui;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Triple;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.Collection;

/**
 * Cache manager manages all the cache
 * <p/>
 * This class is a singleton
 * <p/>
 * User: rwang
 * Date: 23/06/11
 * Time: 13:17
 */
public class PrideInspectorCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(PrideInspectorCacheManager.class);
    private static final PrideInspectorCacheManager instance = new PrideInspectorCacheManager();

    /**
     * Protein name cache
     */
    private final Ehcache proteinNameCache;

    /**
     * Protein sequence coverage cache
     */
    private final Ehcache sequenceCoverageCache;

    /**
     * Peptide fit cache
     */
    private final Ehcache peptideFitCache;


    private PrideInspectorCacheManager() {
        // get cache manager
        CacheManager cacheManager = CacheManager.getInstance();

        // protein name
        this.proteinNameCache = cacheManager.addCacheIfAbsent("proteinDetailsCache");

        // protein sequence coverage
        this.sequenceCoverageCache = cacheManager.addCacheIfAbsent("sequenceCoverageCache");

        // peptide fit
        this.peptideFitCache = cacheManager.addCacheIfAbsent("peptideFitCache");
    }

    /**
     * Get the singleton instance back
     *
     * @return PrideInspectorCacheManager cache manager
     */
    public static PrideInspectorCacheManager getInstance() {
        return instance;
    }

    /**
     * Get protein name
     *
     * @param protAcc protein accession
     * @return Protein    protein object which contains protein name ,protein sequence and etc
     */
    public Protein getProteinDetails(String protAcc) {
        Element element = proteinNameCache.get(protAcc);
        Object val = null;
        if (element != null) {
            val = element.getObjectValue();
        }
        return val == null ? null : (Protein) val;
    }

    /**
     * Store protein details
     * This is for saving tasks from retrieving the protein names many times
     *
     * @param protein protein details object
     */
    public void addProteinDetails(Protein protein) {
        if (protein == null) {
            String msg = "Protein details cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        proteinNameCache.put(new Element(protein.getAccession(), protein));
    }

    /**
     * Store a collection of protein details
     *
     * @param proteins a collection of proteins
     */
    public void addProteinDetails(Collection<Protein> proteins) {
        if (proteins == null) {
            String msg = "Collection of proteins cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // add protein details
        for (Protein protein : proteins) {
            proteinNameCache.put(new Element(protein.getAccession(), protein));
        }
    }


    /**
     * Get protein sequence coverage
     *
     * @param uid     unique id for the data access controller
     * @param identId protein identification id
     * @return Double  sequence coverage
     */
    public Double getSequenceCoverage(String uid, Comparable identId) {
        Element element = sequenceCoverageCache.get(new Tuple<String, Comparable>(uid, identId));
        Object val = null;
        if (element != null) {
            val = element.getObjectValue();
        }
        return val == null ? null : (Double) val;
    }

    /**
     * Add protein sequence coverage
     *
     * @param uid      unique for data access controller
     * @param identId  protein identification id
     * @param coverage protein sequence coverage
     */
    public void addSequenceCoverage(String uid, Comparable identId, Double coverage) {
        if (uid == null || identId == null || coverage == null) {
            String msg = "Input arguments can not be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        sequenceCoverageCache.put(new Element(new Tuple<String, Comparable>(uid, identId), coverage));
    }

    /**
     * Get the state on whether the peptide fit protein sequence
     *
     * @param uid       unqiue id
     * @param identId   protein identification id
     * @param peptideId peptide id
     * @return Integer peptide fit state
     */
    public Integer getPeptideFitState(String uid, Comparable identId, Comparable peptideId) {
        Element element = peptideFitCache.get(new Triple<String, Comparable, Comparable>(uid, identId, peptideId));
        Object val = null;
        if (element != null) {
            val = element.getObjectValue();
        }
        return val == null ? null : (Integer) val;
    }

    /**
     * Add peptide fit state to cache
     *
     * @param uid       unique id
     * @param identId   protein identification id
     * @param peptideId peptide id
     * @param state     peptide fit state
     */
    public void addPeptideFitState(String uid, Comparable identId, Comparable peptideId, Integer state) {
        if (uid == null || identId == null || peptideId == null || state == null) {
            String msg = "Input arguments can not be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        peptideFitCache.put(new Element(new Triple<String, Comparable, Comparable>(uid, identId, peptideId), state));
    }
}
