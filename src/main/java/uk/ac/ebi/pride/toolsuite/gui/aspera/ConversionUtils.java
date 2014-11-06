package uk.ac.ebi.pride.toolsuite.gui.aspera;

/**
 * Utility functions
 */
public class ConversionUtils
{
    public static String stringFromRate(long bps)
    {
        int i;
        char prefixes[] = {' ', 'K', 'M', 'G', 'T', 'P'};
        for (i = 1; (bps >= 1000000) && (i < 5); i++)
        {
            bps /= 1000;
        }
        return "" + bps / 1000 + "." + bps % 1000 * 10 / 1000 + " "
                + prefixes[i] + "b/s";
    }

    public static String stringFromSizeFraction(long numerator, long denominator)
    {
        int i;
        String unit[] = {"B", "KB", "MB", "GB", "TB", "PB"};
        for (i = 0; (denominator >= 10000) && (i < 5); i++)
        {
            numerator >>= 10;
            denominator >>= 10;
        }
        return numerator + "/" + denominator + " " + unit[i];
    }

    public static String stringFromSizeFraction(String numerator,
                                                String denominator)
    {
        long size1 = 0, size2 = 0;
        try
        {
            size1 = Long.valueOf(numerator).longValue();
            size2 = Long.valueOf(denominator).longValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringFromSizeFraction(size1, size2);
    }
}
