package uk.ac.ebi.pride.toolsuite.gui.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: rwang
 * Date: 03/03/11
 * Time: 10:42
 */
public class AccessionUtilsTest {

    @Test
    public void testExpandSingleRange() throws Exception {
        String acc = "1-3";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandSingleAccession() throws Exception {
        String acc = "1";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandRangeAndAccession() throws Exception {
        String acc = "1-3,4";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "4"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandRangeAndMultipleAccessions() throws Exception {
        String acc = "1-3,5,12";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "5", "12"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandMultipleRanges() throws Exception {
        String acc = "1-3,5-8";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "5", "6", "7", "8"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandMultipleAccessions() throws Exception {
        String acc = "1,3,6";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "3", "6"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandMultipleAll() throws Exception {
        String acc = "1-3,4-5,6,7,8-9,10";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testExpandDuplicates() throws Exception {
        String acc = "1,3,6,7-8,10,7-8";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "3", "6", "7", "8", "10"));
        assertEquals(list.toString(), AccessionUtils.expand(acc).toString());
    }

    @Test
    public void testCompactSingleRange() throws Exception {
        String acc = "1-3";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactSingleAccession() throws Exception {
        String acc = "1";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactRangeAndAccession() throws Exception {
        String acc = "1-3,5";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "5"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactRangeAndMultipleAccessions() throws Exception {
        String acc = "1-3,5,12";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "5", "12"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactMultipleRanges() throws Exception {
        String acc = "1-3,5-8";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "5", "6", "7", "8"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactMultipleAccessions() throws Exception {
        String acc = "1,3,6";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "3", "6"));
        assertEquals(acc, AccessionUtils.compact(list));
    }

    @Test
    public void testCompactMultipleAll() throws Exception {
        String acc = "1-4,6-10";
        List<Comparable> list = new ArrayList<Comparable>(Arrays.asList("1", "2", "3", "4", "6", "7", "8", "9", "10"));
        assertEquals(acc, AccessionUtils.compact(list));
    }
}
