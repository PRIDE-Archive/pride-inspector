package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.jmzidml.xml.util.Tuple;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * == General Description ==
 * <p>
 * This class Provides a general information or functionalities for
 * <p>
 * ==Overview==
 * <p>
 * How to used
 * <p>
 * Created by yperez (ypriverol@gmail.com) on 29/11/2016.
 */
public class ClusterAssayProperties {

    Map<Map.Entry<String,String>, ClusterFeatures> clusterFeaturesMap = new HashMap<>();

    public void addAssayProject(String px, String assay, String spectra,
                                String correctSpectra, String incorrectSpectra,
                                String contaminantCorrectSpectra,
                                String contaminantIncorrectSpectra, String diff, String diffCont,
                                String order, String clusterType) {
        clusterFeaturesMap.put(new AbstractMap.SimpleEntry<String, String>(px,assay), new ClusterFeatures(Integer.parseInt(spectra),
                Integer.parseInt(correctSpectra), Integer.parseInt(incorrectSpectra),
                Integer.parseInt(contaminantCorrectSpectra),Integer.parseInt(contaminantIncorrectSpectra),
                Double.parseDouble(diff), Double.parseDouble(diffCont), Integer.parseInt(order), Integer.parseInt(clusterType)
        ));
    }

    public ClusterFeatures getFeatures(String px, String assay){
        return clusterFeaturesMap.get(new AbstractMap.SimpleEntry<String, String>(px, assay));
    }

}
