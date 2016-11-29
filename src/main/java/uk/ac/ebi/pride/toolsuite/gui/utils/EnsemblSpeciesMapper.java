package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map NEWT species id to Ensembl species name
 *
 * This class is a singleton
 *
 * User: rwang
 * Date: 24/08/2011
 * Time: 08:29
 */
public class EnsemblSpeciesMapper {
    private static final EnsemblSpeciesMapper mapper = new EnsemblSpeciesMapper();

    private Map<String, String> mappings;

    private EnsemblSpeciesMapper() {
        mappings = new HashMap<>();
        mappings.put("9606" , "Homo_sapiens");
        mappings.put("10090" , "Mus_musculus");
        mappings.put("7955" , "Danio_rerio");
        mappings.put("6239" , "Caenorhabditis_elegans");
        mappings.put("7227" , "Drosophila_melanogaster");
        mappings.put("4932" , "Saccharomyces_cerevisiae");
        mappings.put("30611" , "Otolemur_garnettii");
        mappings.put("30611" , "Otolemur_garnettii");
        mappings.put("9598" , "Pan_troglodytes");
        mappings.put("61853" , "Nomascus_leucogenys");
        mappings.put("9593" , "Gorilla_gorilla");
        mappings.put("9544" , "Macaca_mulatta");
        mappings.put("9483" , "Callithrix_jacchus");
        mappings.put("30608" , "Microcebus_murinus");
        mappings.put("9601" , "Pongo_abelii");
        mappings.put("9478" , "Tarsius_syrichta");
        mappings.put("10141" , "Cavia_porcellus");
        mappings.put("10020" , "Dipodomys_ordii");
        mappings.put("9978" , "Ochotona_princeps");
        mappings.put("9986" , "Oryctolagus_cuniculus");
        mappings.put("10116" , "Rattus_norvegicus");
        mappings.put("43179" , "Spermophilus_tridecemlineatus");
        mappings.put("37347" , "Tupaia_belangeri");
        mappings.put("30538" , "Vicugna_pacos");
        mappings.put("9685" , "Felis_catus");
        mappings.put("9913" , "Bos_taurus");
        mappings.put("9615" , "Canis_familiaris");
        mappings.put("9739" , "Tursiops_truncatus");
        mappings.put("9365" , "Erinaceus_europaeus");
        mappings.put("9796" , "Equus_caballus");
        mappings.put("132908" , "Pteropus_vampyrus");
        mappings.put("59463" , "Myotis_lucifugus");
        mappings.put("9646" , "Ailuropoda_melanoleuca");
        mappings.put("9823" , "Sus_scrofa");
        mappings.put("42254" , "Sorex_araneus");
        mappings.put("9785" , "Loxodonta_africana");
        mappings.put("9813" , "Procavia_capensis");
        mappings.put("9371" , "Echinops_telfairi");
        mappings.put("9361" , "Dasypus_novemcinctus");
        mappings.put("9358" , "Choloepus_hoffmanni");
        mappings.put("13616" , "Monodelphis_domestica");
        mappings.put("9258" , "Ornithorhynchus_anatinus");
        mappings.put("9315" , "Macropus_eugenii");
        mappings.put("28377" , "Anolis_carolinensis");
        mappings.put("9031" , "Gallus_gallus");
        mappings.put("9103" , "Meleagris_gallopavo");
        mappings.put("59729" , "Taeniopygia_guttata");
        mappings.put("8364" , "Xenopus_tropicalis");
        mappings.put("31033" , "Takifugu_rubripes");
        mappings.put("8090" , "Oryzias_latipes");
        mappings.put("69293" , "Gasterosteus_aculeatus");
        mappings.put("99883" , "Tetraodon_nigroviridis");
        mappings.put("7719" , "Ciona_intestinalis");
        mappings.put("51511" , "Ciona_savignyi");
    }

    public static EnsemblSpeciesMapper getInstance() {
        return mapper;
    }

    public String getEnsemblName(String newtID) {
        return mappings.get(newtID);
    }
}
