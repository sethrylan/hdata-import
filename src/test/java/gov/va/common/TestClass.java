package gov.va.common;

import gov.va.common.mongo.MongoDB;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberRange;

/**
 *
 * @author gaineys
 */
public abstract class TestClass {
    
//    protected static final String ccrPattern = ".*\\\\xml\\\\ccr\\\\.*\\.xml";
//    protected static final String c32Pattern = ".*\\\\xml\\\\c32\\\\.*\\.xml";
//    protected static final String badPattern = ".*\\\\xml\\\\badxml\\\\.*\\.xml";
    
    protected static String ccrPattern = null;
    protected static String c32Pattern = null;
    protected static String badPattern = null;
    protected static NumberRange nr = null;
    protected static int minNumberTestFiles = 0;

    protected TestClass() {
        Properties testProps = getProperties("config/test.properties");
        ccrPattern = testProps.getProperty("ccrPattern");
        c32Pattern = testProps.getProperty("c32Pattern");
        badPattern = testProps.getProperty("badPattern");
        
        nr = new NumberRange(0, Integer.valueOf(testProps.getProperty("maxXMLIndex")));
        minNumberTestFiles = Integer.valueOf(testProps.getProperty("minNumberTestFiles"));

        
    }
    
    /**
     * Returns true if a filename contains a number within the numeric range specified for the test
     */
    protected static boolean isFileInRange(String filename) {
        return nr.containsNumber(Integer.valueOf(filename.replaceAll("[^0-9]+", "")));
    }
    
    protected static Properties getProperties(String propertiesLocation) {
        Properties props = new Properties();
        try {
            props.load(TestClass.class.getClassLoader().getResourceAsStream(propertiesLocation));
        } catch (IOException ex) {
            Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
    
    
    protected static URL getResource(String resourceClassPath) {
        return TestClass.class.getClassLoader().getResource(resourceClassPath);
    }    
    
}
