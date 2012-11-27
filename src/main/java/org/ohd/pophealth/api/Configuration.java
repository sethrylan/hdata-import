package org.ohd.pophealth.api;

/**
 *
 * @author ohdohd
 */
public class Configuration {
    private static String umlsConfLocation = "config/umls.properties";
    private static String lvgConfLocation = "config/lvg.properties";
    private static String ccrVocabLocation = "schema/ccrvocabulary.json";
    private static String ccrXSDLocation = "schema/ADJE2369-05.xsd";

    public static String getCcrVocabLocation() {
        return ccrVocabLocation;
    }

    public static String getCcrXSDLocation() {
        return ccrXSDLocation;
    }

    public static String getLvgConfLocation() {
        return lvgConfLocation;
    }

    public static String getUmlsConfLocation() {
        return umlsConfLocation;
    }

}
