package uk.ac.ebi.pride.toolsuite.gui.utils;

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
public class ClusterProjectProperties {

    Map<String, ClusterFeatures> clusterFeaturesMap = new HashMap<>();

    public void addPRIDEProject(String key, String spectra,
                                String correctSpectra, String incorrectSpectra,
                                String contaminantCorrectSpectra,
                                String contaminantIncorrectSpectra, String diff, String diffCont,
                                String order, String clusterType) {
        clusterFeaturesMap.put(key, new ClusterFeatures(Integer.parseInt(spectra),
                Integer.parseInt(correctSpectra), Integer.parseInt(incorrectSpectra),
                Integer.parseInt(contaminantCorrectSpectra),Integer.parseInt(contaminantIncorrectSpectra),
                Double.parseDouble(diff), Double.parseDouble(diffCont), Integer.parseInt(order), Integer.parseInt(clusterType)
                ));
    }

    public ClusterFeatures getFeatures(String key){
        return clusterFeaturesMap.get(key);
    }
}
