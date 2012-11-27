package gov.va.common;

import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.math.NumberRange;

/**
 *
 * @author gaineys
 */
public abstract class XMLTestClass extends TestClass {
    
//    protected static final String ccrPattern = ".*\\\\xml\\\\ccr\\\\.*\\.xml";
//    protected static final String c32Pattern = ".*\\\\xml\\\\c32\\\\.*\\.xml";
//    protected static final String badPattern = ".*\\\\xml\\\\badxml\\\\.*\\.xml";
    
    protected static String ccrPattern = null;
    protected static String c32Pattern = null;
    protected static String badPattern = null;
    protected static NumberRange nr = null;
    protected static int minNumberTestFiles = 0;
    protected static List<String> filesWithSchematronErrors = null;
    protected static List<String> filesWithoutSchematronErrors = null;
    protected static boolean writeToFile = false;
    
    protected XMLTestClass() {
        PropertiesConfiguration testProps = getPropertiesConfiguration("config/xmltest.properties");
        ccrPattern = testProps.getString("ccrPattern");
        c32Pattern = testProps.getString("c32Pattern");
        badPattern = testProps.getString("badPattern");
        
        nr = new NumberRange(0, testProps.getInt("maxXMLIndex"));
        minNumberTestFiles = testProps.getInt("minNumberTestFiles");

        filesWithSchematronErrors = testProps.getList("filesWithSchematronErrors");
        filesWithoutSchematronErrors = testProps.getList("filesWithoutSchematronErrors");
        
        writeToFile = testProps.getBoolean("writeToFile");
    }
    
    /**
     * Returns true if a filename contains a number within the numeric range specified for the test
     */
    protected static boolean isFileInRange(String filename) {
        return nr.containsNumber(Integer.valueOf(filename.replaceAll("[^0-9]+", "")));
    }
    

    
}
