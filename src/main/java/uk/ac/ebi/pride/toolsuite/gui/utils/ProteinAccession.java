package uk.ac.ebi.pride.toolsuite.gui.utils;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ProteinAccession implements Comparable{

    private String accession;
    private String mappedAccession;

    public ProteinAccession(String accession, String mappedAccession) {
        this.accession = accession;
        this.mappedAccession = mappedAccession;
    }

    public String getAccession() {
        return accession;
    }

    public String getMappedAccession() {
        return mappedAccession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProteinAccession)) return false;

        ProteinAccession that = (ProteinAccession) o;

        return !(accession != null ? !accession.equals(that.accession) : that.accession != null) && !(mappedAccession != null ? !mappedAccession.equals(that.mappedAccession) : that.mappedAccession != null);

    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + (mappedAccession != null ? mappedAccession.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProteinAccession{" +
                "accession='" + accession + '\'' +
                ", mappedAccession='" + mappedAccession + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if(o == null) return 1;
        if (!(o instanceof ProteinAccession)) return 1;

        ProteinAccession that = (ProteinAccession) o;
        return (this.accession.compareTo(that.accession));
    }
}
