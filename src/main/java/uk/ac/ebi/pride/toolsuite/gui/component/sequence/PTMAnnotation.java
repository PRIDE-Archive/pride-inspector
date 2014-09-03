package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Annotation for PTM
 *
 * User: rwang
 * Date: 14/06/11
 * Time: 15:48
 */
public class PTMAnnotation {
    /**
     * modification ontology accession
     */
    private String accession = null;
    /**
     *  modification location with a peptide
     */
    private int location = -1;
    /**
     * modification name
     */
    private String name = null;
    /**
     * modification database where accession is from
     */
    private String modDatabase = null;
    /**
     * modification database version
     */
    private String modDatabaseVersion = null;
    /**
     * a list of ModMonoDelta
     */
    private List<Double> monoMassDeltas = null;
    /**
     * a list of ModAvgDelta
     */
    private List<Double> avgMassDeltas = null;

    public PTMAnnotation() {
        this(null, -1, null, null, null);
    }

    public PTMAnnotation(String accession, int location, String name,
                         String modDatabase, String modDatabaseVersion) {
        this.accession = accession;
        this.location = location;
        this.name = name;
        this.modDatabase = modDatabase;
        this.modDatabaseVersion = modDatabaseVersion;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModDatabase() {
        return modDatabase;
    }

    public void setModDatabase(String modDatabase) {
        this.modDatabase = modDatabase;
    }

    public String getModDatabaseVersion() {
        return modDatabaseVersion;
    }

    public void setModDatabaseVersion(String modDatabaseVersion) {
        this.modDatabaseVersion = modDatabaseVersion;
    }

    public List<Double> getMonoMassDeltas() {
        return monoMassDeltas;
    }

    public void setMonoMassDeltas(List<Double> monoMassDeltas) {
        this.monoMassDeltas = (monoMassDeltas != null)?new ArrayList<Double>(monoMassDeltas): new ArrayList<Double>();
    }

    public List<Double> getAvgMassDeltas() {
        return avgMassDeltas;
    }

    public void setAvgMassDeltas(List<Double> avgMassDeltas) {
        this.avgMassDeltas = (avgMassDeltas != null)? new ArrayList<Double>(avgMassDeltas): new ArrayList<Double>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PTMAnnotation)) return false;

        PTMAnnotation that = (PTMAnnotation) o;

        if (location != that.location) return false;
        if (accession != null ? !accession.equals(that.accession) : that.accession != null) return false;
        if (avgMassDeltas != null ? !avgMassDeltas.equals(that.avgMassDeltas) : that.avgMassDeltas != null)
            return false;
        if (modDatabase != null ? !modDatabase.equals(that.modDatabase) : that.modDatabase != null) return false;
        if (modDatabaseVersion != null ? !modDatabaseVersion.equals(that.modDatabaseVersion) : that.modDatabaseVersion != null)
            return false;
        return !(monoMassDeltas != null ? !monoMassDeltas.equals(that.monoMassDeltas) : that.monoMassDeltas != null) && !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + location;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (modDatabase != null ? modDatabase.hashCode() : 0);
        result = 31 * result + (modDatabaseVersion != null ? modDatabaseVersion.hashCode() : 0);
        result = 31 * result + (monoMassDeltas != null ? monoMassDeltas.hashCode() : 0);
        result = 31 * result + (avgMassDeltas != null ? avgMassDeltas.hashCode() : 0);
        return result;
    }
}
