package uk.ac.ebi.pride.toolsuite.gui.utils;

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
public class ClusterFeatures {

    int spectra;
    int correctSpectra;
    int incorrectSpectra;
    int contaminantCorrectSpectra;
    int contaminantIncorrectSpectra;
    double  diff;
    double  diffCont;
    int    order;
    int typeCluster;

    public ClusterFeatures(int spectra, int correctSpectra, int incorrectSpectra,
                           int contaminantCorrectSpectra, int contaminantIncorrectSpectra,
                           double diff, double diffCont, int order, int typeCluster) {
        this.spectra = spectra;
        this.correctSpectra = correctSpectra;
        this.incorrectSpectra = incorrectSpectra;
        this.contaminantCorrectSpectra = contaminantCorrectSpectra;
        this.contaminantIncorrectSpectra = contaminantIncorrectSpectra;
        this.diff = diff;
        this.diffCont = diffCont;
        this.order = order;
        this.typeCluster = typeCluster;
    }

    public int getSpectra() {
        return spectra;
    }

    public void setSpectra(int spectra) {
        this.spectra = spectra;
    }

    public int getCorrectSpectra() {
        return correctSpectra;
    }

    public void setCorrectSpectra(int correctSpectra) {
        this.correctSpectra = correctSpectra;
    }

    public int getIncorrectSpectra() {
        return incorrectSpectra;
    }

    public void setIncorrectSpectra(int incorrectSpectra) {
        this.incorrectSpectra = incorrectSpectra;
    }

    public int getContaminantCorrectSpectra() {
        return contaminantCorrectSpectra;
    }

    public void setContaminantCorrectSpectra(int contaminantCorrectSpectra) {
        this.contaminantCorrectSpectra = contaminantCorrectSpectra;
    }

    public int getContaminantIncorrectSpectra() {
        return contaminantIncorrectSpectra;
    }

    public void setContaminantIncorrectSpectra(int contaminantIncorrectSpectra) {
        this.contaminantIncorrectSpectra = contaminantIncorrectSpectra;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public double getDiffCont() {
        return diffCont;
    }

    public void setDiffCont(double diffCont) {
        this.diffCont = diffCont;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTypeCluster() {
        return typeCluster;
    }

    public void setTypeCluster(int typeCluster) {
        this.typeCluster = typeCluster;
    }
}
