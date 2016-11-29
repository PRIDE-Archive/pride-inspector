package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.mol.*;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonUtilities;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnnotationUtil provides static methods for converting information into mzgraph-browser format.
 * <p/>
 * User: rwang
 * Date: 03-Aug-2010
 * Time: 11:01:14
 */
public class AnnotationUtils {

    public static List<IonAnnotation> convertToIonAnnotations(List<FragmentIon> ions) {
        List<IonAnnotation> ionAnnotations = new ArrayList<>();
        if (ions != null) {
            for (FragmentIon ion : ions) {
                // get the fragment ion type
                FragmentIonType ionType = getIonType(ion);
                // get the fragment loss
                NeutralLoss fragLoss = FragmentIonUtilities.getFragmentIonNeutralLoss(ion.getIonType());
                // m/z and intensity
                IonAnnotation ionAnnotation = getOverlapIonAnnotation(ion, ionAnnotations);
                IonAnnotationInfo ionInfo;
                if (ionAnnotation == null) {
                    ionInfo = new IonAnnotationInfo();
                    ionAnnotation = new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
                    ionAnnotations.add(ionAnnotation);
                } else {
                    ionInfo = ionAnnotation.getAnnotationInfo();
                }
                ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
            }
        }
        return ionAnnotations;
    }

    public static IonAnnotation getOverlapIonAnnotation(FragmentIon ion, List<IonAnnotation> ionAnnotations) {
        IonAnnotation result = null;
        double mz = ion.getMz();
        double intensity = ion.getIntensity();

        for (IonAnnotation ionAnnotation : ionAnnotations) {
            if (ionAnnotation.getMz().doubleValue() == mz
                    && ionAnnotation.getIntensity().doubleValue() == intensity) {
                result = ionAnnotation;
            }
        }
        return result;
    }

    /**
     * Convert fragment ion.
     *
     * @param ion fragment ion.
     * @return IonAnnotation    ion annotation.
     */
    public static IonAnnotation getIonAnnotation(FragmentIon ion) {
        // get the fragment ion type
        FragmentIonType ionType = getIonType(ion);

        // get the fragment loss
        NeutralLoss fragLoss = FragmentIonUtilities.getFragmentIonNeutralLoss(ion.getIonType());
        IonAnnotationInfo ionInfo = new IonAnnotationInfo();
        ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
        return new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
    }

    public static FragmentIonType getIonType(FragmentIon ion) {
        return FragmentIonUtilities.getFragmentIonType(ion.getIonType());
    }

    public static Map<Integer, List<PTModification>> createModificationMap(List<Modification> mods, int peptideLength) {
        Map<Integer, List<PTModification>> modMap
                = new HashMap<>();
        for (uk.ac.ebi.pride.utilities.data.core.Modification mod : mods) {
            int location = mod.getLocation();
            // merge the N-terminus modification to the first amino acid
            location = location == 0 ? 1 : location;
            // merge the C-terminus modification to the last amino acid
            location = location == peptideLength ? location - 1 : location;

            List<PTModification> subMods = modMap.get(location);
            if (subMods == null) {
                subMods = new ArrayList<>();
                modMap.put(mod.getLocation(), subMods);
            }
            subMods.add(new PTModification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoisotopicMassDelta(), mod.getAvgMassDelta()));
        }
        return modMap;
    }

    public static List<PTModification> convertModifications(List<Modification> modifications) {
        List<PTModification> newMods = new ArrayList<>();
        for (Modification mod : modifications) {
            newMods.add(new PTModification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoisotopicMassDelta(), mod.getAvgMassDelta()));
        }
        return newMods;
    }

//    public static Peptide getPeptideFromString(String peptideStr) {
//        Peptide peptide = new Peptide();
//        if (peptideStr != null) {
//            char[] chars = peptideStr.toCharArray();
//            for (char aChar : chars) {
//                AminoAcid aminoAcid = MoleculeUtilities.getAminoacid(aChar);
//                if (aminoAcid != null) {
//                    peptide.addAminoAcid(aminoAcid);
//                }
//            }
//
//            if (peptide.getNumberOfAminoAcids() != peptideStr.length()) {
//                peptide.removeAll();
//            }
//        }
//        return peptide;
//    }
}
