package uk.ac.ebi.pride.toolsuite.gui.utils;

import org.junit.Test;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ThreadCalculatorTest {

    @Test
    public void testNumberOfThreads() throws Exception {
        int numberOfThreads = ThreadCalculator.calculateNumberOfThreads(8);
        System.out.println(numberOfThreads);
    }
}
