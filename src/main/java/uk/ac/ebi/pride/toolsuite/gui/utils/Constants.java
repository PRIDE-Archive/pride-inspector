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
    String GENERAL = "General";
    String SPECTRUM = "Spectrum";
    String CHROMATOGRAM = "Chromatogram";
    String DATA_PROCESSING = "Data Processing";
    String SOURCE_FILE = "Source File";
    String SCAN = "Scan";
    String SCAN_WINDOW = "Scan Window";
    String PRECURSOR = "Precursor";
    String PRODUCTS = "Products";
    String PRODUCT = "Product";
    String PROCESSING_METHOD = "Method";

    String INDEX = "Index";
    String DEFAULT_ARR_LEN = "Default Array Length";
    String ORDER = "Order";
    String SOFTWARE_ID = "Software ID";
    String SOFTWARE_VERSION = "Software Version";
    String SOURCE_FILE_ID = "Source File ID";
    String SOURCE_FILE_NAME = "Source File Name";
    String SOURCE_FILE_PATH = "Source File Path";
    String EXTERNAL_SPECTRUM_REF = "External Spectrum ID";
    String SPECTRUM_ID = "Spectrum ID";
    String ISOLATION_WINDOW = "Isolation Window";
    String SELECTED_ION = "Selected Ion";
    String ACTIVATION = "Activation";

    String ID = "ID";
    String ACCESSION = "Accession";
    String VERSION = "Version";
    String TITLE = "Title";
    String SHORT_LABEL = "Short Label";
    String PUBLIC_DATE = "Public Date";
    String CREATION_DATE = "Creation Date";
    String NAME = "Name";
    String PATH = "Path";
    String PARAMETER = "Parameter";
    String SCAN_SETTING_ID = "Scan Setting ID";
    String SOURCE = "Source";
    String ANALYZER = "Analyzer";
    String DETECTOR = "Detector";
    String TARGET = "Target";
    String EMPTY = "";
    String PROTOCOL_STEP = "Step";
    String FULL_REFERENCE = "Full Reference";

    String PUBMED = "PubMed";
    String DOI = "DOI";

    String DOT = ".";
    String COMMA = ",";
    String TAB = "\t";
    String TAB_SEP_FILE = ".tsv";
    String MZTAB_SEP_FILE=".mztab";
    String MZML_FILE = ".mzml";
    String XML_FILE = ".xml";
    String GZIPPED_FILE = ".gz";
    String MZIDENT_FILE =".mzid";
    String MZIDENT_FILE_LARGE=".mzidentml";
    String MZXML_FILE   = ".mzxml";
    String MGF_FILE = ".mgf";
    String DTA_FILE = ".dta";
    String MS2_FILE = ".ms2";
    String PKL_FILE = ".pkl";
    String APL_FILE = ".apl";
    String MZTAB_FILE = ".mztab";
    String CDF_FILE = ".cdf";


    String LINE_SEPARATOR = System.getProperty("line.separator");
    String UNKNOWN = "Unknown";
    String FIT = "Fuzzy Fit";
    String NOT_FIT = "No Fit";
    String STRICT_FIT = "Fit";
    String OVERLAP = "Overlap";
    String PTM = "PTM";
    String SELECTED = "Selected";

    String VIEW = "View";

    String PUBMED_URL_PERFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

    String DOI_URL_PREFIX = "http://dx.doi.org/";

    String OLS_URL_PREFIX = "http://www.ebi.ac.uk/ontology-lookup/?termId=";

    String NOT_AVAILABLE = "N/A";

    /**
     * Quantification
     */
    String QUANTIFICATION_RATIO_CHAR = "/";

    /**
     * Colours
     */
    Color PTM_BACKGROUND_COLOUR = new Color(215, 39, 41, 100);
    Color PTM_HIGHLIGHT_COLOUR = Color.yellow.darker();

    Color STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(40, 175, 99, 100);
    Color FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(251, 182, 1, 100);
    Color PEPTIDE_OVERLAP_COLOUR = new Color(40, 175, 99);
    Color PEPTIDE_HIGHLIGHT_COLOUR = Color.yellow;


    Color NOT_FIT_PEPTIDE_BACKGROUND_COLOUR = new Color(215, 39, 41, 100);
    /**
     * color for tables
     */
    Color ALTER_ROW_COLOUR = new Color(214, 241, 249);
    Color ROW_SELECTION_BACKGROUD = new Color(193, 210, 238);
    Color ROW_SELECTION_FOREGROUND = Color.black;
    Color DELTA_MZ_WARNING = new Color(215, 39, 41, 100);
    Color DELTA_MZ_NORMAL = new Color(40, 175, 99, 100);

    /**
     * Number Formatter
     */
    DecimalFormat LARGE_DECIMAL_NUMBER_FORMATTER = new DecimalFormat("0.###E0");
    NumberFormat DECIMAL_FORMATTER = NumberFormat.getInstance();

    /**
     * Scientific number threshold
     */
    double MAX_NON_SCIENTIFIC_NUMBER = 999;
    double MIN_MON_SCIENTIFIC_NUMBER = 0.001;

}
