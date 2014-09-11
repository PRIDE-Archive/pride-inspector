package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Constants shared by all classes
 * <p/>
 * User: rwang, ypriverol
 * Date: 25-May-2010
 * Time: 14:09:16
 */
public interface Constants {
    /**
     * Labels
     */
    public static final String GENERAL = "General";
    public static final String SPECTRUM = "Spectrum";
    public static final String CHROMATOGRAM = "Chromatogram";
    public static final String DATA_PROCESSING = "Data Processing";
    public static final String SOURCE_FILE = "Source File";
    public static final String SCAN = "Scan";
    public static final String SCAN_WINDOW = "Scan Window";
    public static final String PRECURSOR = "Precursor";
    public static final String PRODUCTS = "Products";
    public static final String PRODUCT = "Product";
    public static final String PROCESSING_METHOD = "Method";

    public static final String INDEX = "Index";
    public static final String DEFAULT_ARR_LEN = "Default Array Length";
    public static final String ORDER = "Order";
    public static final String SOFTWARE_ID = "Software ID";
    public static final String SOFTWARE_VERSION = "Software Version";
    public static final String SOURCE_FILE_ID = "Source File ID";
    public static final String SOURCE_FILE_NAME = "Source File Name";
    public static final String SOURCE_FILE_PATH = "Source File Path";
    public static final String EXTERNAL_SPECTRUM_REF = "External Spectrum ID";
    public static final String SPECTRUM_ID = "Spectrum ID";
    public static final String ISOLATION_WINDOW = "Isolation Window";
    public static final String SELECTED_ION = "Selected Ion";
    public static final String ACTIVATION = "Activation";

    public static final String ID = "ID";
    public static final String ACCESSION = "Accession";
    public static final String VERSION = "Version";
    public static final String TITLE = "Title";
    public static final String SHORT_LABEL = "Short Label";
    public static final String PUBLIC_DATE = "Public Date";
    public static final String CREATION_DATE = "Creation Date";
    public static final String NAME = "Name";
    public static final String PATH = "Path";
    public static final String PARAMETER = "Parameter";
    public static final String SCAN_SETTING_ID = "Scan Setting ID";
    public static final String SOURCE = "Source";
    public static final String ANALYZER = "Analyzer";
    public static final String DETECTOR = "Detector";
    public static final String TARGET = "Target";
    public static final String EMPTY = "";
    public static final String PROTOCOL_STEP = "Step";
    public static final String FULL_REFERENCE = "Full Reference";

    public static final String PUBMED = "PubMed";
    public static final String DOI = "DOI";

    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String TAB = "\t";
    public static final String TAB_SEP_FILE = ".tsv";
    public static final String MZML_FILE = ".mzml";
    public static final String XML_FILE = ".xml";
    public static final String GZIPPED_FILE = ".gz";
    public static final String MZIDENT_FILE =".mzid";
    //Todo: We added the large extension for mzidentml files but in the future will be only mzid
    public static final String MZIDENT_FILE_LARGE=".mzidentml";
    public static final String MZXML_FILE   = ".mzxml";
    public static final String MGF_FILE = ".mgf";
    public static final String DTA_FILE = ".dta";
    public static final String MS2_FILE = ".ms2";
    public static final String PKL_FILE = ".pkl";
    public static final String APL_FILE = ".apl";
    public static final String MZTAB_FILE = ".mztab";


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String UNKNOWN = "Unknown";
    public static final String FIT = "Fuzzy Fit";
    public static final String NOT_FIT = "No Fit";
    public static final String STRICT_FIT = "Fit";
    public final static String OVERLAP = "Overlap";
    public final static String PTM = "PTM";
    public final static String SELECTED = "Selected";

    public final static String VIEW = "View";

    public final static String PUBMED_URL_PERFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

    public final static String DOI_URL_PREFIX = "http://dx.doi.org/";

    public static final String OLS_URL_PREFIX = "http://www.ebi.ac.uk/ontology-lookup/?termId=";

    public static final String NOT_AVAILABLE = "N/A";

    /**
     * Quantification
     */
    public static final String QUANTIFICATION_RATIO_CHAR = "/";

    /**
     * Colours
     */
    public static final Color PTM_BACKGROUND_COLOUR = new Color(215, 39, 41, 100);
    public static final Color PTM_HIGHLIGHT_COLOUR = Color.yellow.darker();

    public final static Color STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(40, 175, 99, 100);
    public final static Color FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(251, 182, 1, 100);
    public final static Color PEPTIDE_OVERLAP_COLOUR = new Color(40, 175, 99);
    public final static Color PEPTIDE_HIGHLIGHT_COLOUR = Color.yellow;


    public final static Color NOT_FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(215, 39, 41, 100);
    /**
     * color for tables
     */
    public static final Color ALTER_ROW_COLOUR = new Color(214, 241, 249);
    public static final Color ROW_SELECTION_BACKGROUD = new Color(193, 210, 238);
    public static final Color ROW_SELECTION_FOREGROUND = Color.black;
    public static final Color DELTA_MZ_WARNING = new Color(215, 39, 41, 100);
    public static final Color DELTA_MZ_NORMAL = new Color(40, 175, 99, 100);

    /**
     * Number Formatter
     */
    public static final DecimalFormat LARGE_DECIMAL_NUMBER_FORMATTER = new DecimalFormat("0.###E0");
    public static final NumberFormat DECIMAL_FORMATTER = NumberFormat.getInstance();

    /**
     * Scientific number threshold
     */
    public static final double MAX_NON_SCIENTIFIC_NUMBER = 999;
    public static final double MIN_MON_SCIENTIFIC_NUMBER = 0.001;
}
