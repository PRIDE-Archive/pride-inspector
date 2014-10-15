package uk.ac.ebi.pride.toolsuite.gui.utils;

/**
 * Thread calculator computes the number of threads should be used by
 * a thread pool for PRIDE Inspector
 *
 * The calculation follows the following formula:
 *
 * Number of threads = Number of CPUs (number of hardware threads) * Target CPU utilization (Between 0 and 1) * (1 + ratio of wait over compute time)
 *
 * Ratio of wait over compute time = wait time in percentage / (100 - wait time in percentage)
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ThreadCalculator {

    // This implies that the wait time and compute time are almost equal
    public static final float DEFAULT_RATIO_OF_WAIT_OVER_COMPUTE_TIME = 1.0f;

    // This implies we want 80% CPU utilization
    public static final float DEFAULT_CPU_UTILIZATION = 0.8f;

    public static int calculateNumberOfThreads() {
        return calculateNumberOfThreads(DEFAULT_RATIO_OF_WAIT_OVER_COMPUTE_TIME);
    }

    public static int calculateNumberOfThreads(float ratioOfWaitOverComputeTime) {
        int numberOfCPUs = Runtime.getRuntime().availableProcessors();

        int numberOfCPUsWithHyperThreading = numberOfCPUs * 2;

        int numberOfThreads = Math.round(numberOfCPUsWithHyperThreading * DEFAULT_CPU_UTILIZATION * (1 + ratioOfWaitOverComputeTime));

        return numberOfThreads;
    }
}
