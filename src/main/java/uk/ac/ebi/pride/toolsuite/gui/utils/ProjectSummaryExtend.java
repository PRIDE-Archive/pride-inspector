package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.pride.archive.web.service.model.project.ProjectSummary;

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
 * Created by yperez (ypriverol@gmail.com) on 30/11/2016.
 */
public class ProjectSummaryExtend extends ProjectSummary {

    ClusterFeatures clusterFeatures = null;

    public ProjectSummaryExtend(ClusterFeatures clusterFeatures) {
        this.clusterFeatures = clusterFeatures;
    }
}
