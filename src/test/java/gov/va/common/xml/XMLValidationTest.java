package gov.va.common.xml;

import gov.va.common.ClassPathSearcher;
import gov.va.common.TestClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author gaineys
 */
public class XMLValidationTest extends TestClass {
    
    public XMLValidationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateXMLFiles_CCR() {                
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> ccrXmlMap = cps.findFilesInClassPath(ccrPattern);
        Assert.assertTrue("Too few XML files.", ccrXmlMap.size() > minNumberTestFiles);
        for(String filename : ccrXmlMap.keySet()) {

            Assert.assertNotNull("InputStream is null for " + filename, ccrXmlMap.get(filename));
            try {
                Assert.assertTrue(ccrXmlMap.get(filename).available() > 0);
            } catch (IOException ex) {
                Assert.fail(filename + " is not available.");
            }

            Map<String, String> validationResults = XMLValidation.isXMLValidForSchema(ccrXmlMap.get(filename), XMLValidation.Schema.CCR);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> error : validationResults.entrySet()) {
                sb.append(error.getKey() + " : " + error.getValue() + "\n");
            }
            Assert.assertTrue("Validation error for " + filename + ". " + sb.toString(), validationResults.keySet().size() == 1 && validationResults.get("Result").equals("PASSED"));
        }
    }
    
    @Test
    @Ignore
    public void validateXMLFiles_C32() {                
        ClassPathSearcher cps = new ClassPathSearcher();
        
        {
            Map<String, InputStream> c32XmlMap = cps.findFilesInClassPath(c32Pattern);
            Assert.assertTrue("Too few XML files.", c32XmlMap.size() > minNumberTestFiles);
            for(String filename : c32XmlMap.keySet()) {

                Assert.assertNotNull("InputStream is null for " + filename, c32XmlMap.get(filename));
                try {
                    Assert.assertTrue(c32XmlMap.get(filename).available() > 0);
                } catch (IOException ex) {
                    Assert.fail(filename + " is not available.");
                }

                Map<String, String> validationResults = XMLValidation.isXMLValidForSchema(c32XmlMap.get(filename), XMLValidation.Schema.CCD_HL7);
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> error : validationResults.entrySet()) {
                    sb.append(error.getKey() + " : " + error.getValue() + "\n");
                }
                Assert.assertTrue("Validation error for " + filename + ". " + sb.toString(), validationResults.keySet().size() == 1 && validationResults.get("Result").equals("PASSED"));
            }
        }
    }
    
    
    @Test
    public void testIsXMLWellFormed() throws Exception {
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(ccrPattern);
        xmlMap.putAll(cps.findFilesInClassPath(c32Pattern));
        Assert.assertTrue("Too few XML files.", xmlMap.size() > minNumberTestFiles);
        for(String filename : xmlMap.keySet()) {

            Assert.assertNotNull("InputStream is null for " + filename, xmlMap.get(filename));
            try {
                Assert.assertTrue(xmlMap.get(filename).available() > 0);
            } catch (IOException ex) {
                Assert.fail(filename + " is not available.");
            }

            Assert.assertTrue("File " + filename + " could not be parsed.", XMLValidation.isXMLWellFormed(xmlMap.get(filename)));

        }
    }
    
    @Test
    public void testIsXMLNotWellFormed() throws Exception {
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(badPattern);
        Assert.assertTrue("Too few XML files.", xmlMap.keySet().size() > minNumberTestFiles);
        for(String filename : xmlMap.keySet()) {

            Assert.assertNotNull("InputStream is null for " + filename, xmlMap.get(filename));
            try {
                Assert.assertTrue(xmlMap.get(filename).available() > 0);
            } catch (IOException ex) {
                Assert.fail(filename + " is not available.");
            }

            Assert.assertFalse("File " + filename + " should not be parseable.", XMLValidation.isXMLWellFormed(xmlMap.get(filename)));
 
        }
    }
    


}
