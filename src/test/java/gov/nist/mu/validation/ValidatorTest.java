package gov.nist.mu.validation;

import gov.va.common.ClassPathSearcher;
import gov.va.common.TestLogger;
import gov.va.common.XMLTestClass;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author gaineys
 */
public class ValidatorTest extends XMLTestClass {
        
    public ValidatorTest() {
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
    public void testValidate() {

        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(ccrPattern);
        xmlMap.putAll(cps.findFilesInClassPath(c32Pattern));
        Assert.assertTrue("Too few XML files.", xmlMap.size() > 10);
        for(String filename : xmlMap.keySet()) {
            TestLogger.getLogger().log(Level.FINE, "Validating " + filename);
            
            Assert.assertNotNull("InputStream is null for " + filename, xmlMap.get(filename));
            try {
                Assert.assertTrue(xmlMap.get(filename).available() > 0);
            } catch (IOException ex) {
                Assert.fail(filename + " is not available.");
            }

            String validationResult = Validator.validate(xmlMap.get(filename));

            if (writeToFile) {
                // write xml-formatted validation result to file in target folder
                String date = Validator.createDateOfTest();
                String time = Validator.createTimeOfTest();

                String resourceRootDirectory = getResource("").getFile(); // i.e., <project-root>/target/test-classes
                File targetDirectory = new File(resourceRootDirectory).getParentFile();
                String outputfilename = targetDirectory.getAbsolutePath() + "\\nist.mu.Validator\\" + filename.substring(filename.lastIndexOf("\\")) + "." + date + "-" + time + ".xml";

                new File(outputfilename).getParentFile().mkdirs();

                FileWriter outputStream = null;
                try {
                    outputStream = new FileWriter(outputfilename);
                    outputStream.write(validationResult);
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                            Assert.fail(ex.getMessage());
                        }
                    }
                }
            }

            
            // check results for known files
            
            boolean hasErrors = false;
            boolean hasNotErrors = false;
            
            for(String s : filesWithSchematronErrors) {
                if (filename.endsWith(s)) {
                    hasErrors = true;
                }
            }
            
            for(String s : filesWithoutSchematronErrors) {
                if (filename.endsWith(s)) {
                    hasNotErrors = true;
                }
            }
            
            
            
            if (hasErrors) {
                /* XML will be in form of:
                <Report>
                    <ReportHeader>
                        <ValidationStatus>Complete</ValidationStatus>
                        <ServiceName>Meaningful Use HITSP/C32 v2.5 Validation</ServiceName>
                        <ResultOfTest>Failed</ResultOfTest>
                        <ErrorCount>4</ErrorCount>
                */
                try {
                    Document document = Validator.stringToDom(validationResult);
                    NodeList validationStatusNodeList = document.getElementsByTagName("ValidationStatus");
                    Assert.assertEquals("There should be only one ValidationStatus node.", 1, validationStatusNodeList.getLength());
                    Assert.assertEquals("Complete", validationStatusNodeList.item(0).getTextContent());
                    
                    NodeList resultOfTestNodeList = document.getElementsByTagName("ResultOfTest");
                    Assert.assertEquals("There should be only one ResultOfTest node.", 1, resultOfTestNodeList.getLength());
                    Assert.assertEquals("Failed", resultOfTestNodeList.item(0).getTextContent());
                    
                    NodeList errorCountNodeList = document.getElementsByTagName("ErrorCount");
                    Assert.assertEquals("There should be only one ErrorCount node.", 1, errorCountNodeList.getLength());
                    Assert.assertTrue(Integer.valueOf(errorCountNodeList.item(0).getTextContent()) > 0);
                    
                } catch (SAXException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            if (hasNotErrors) {
                /* XML will be in form of:
                <Report>
                    <ReportHeader>
                        <ValidationStatus>Complete</ValidationStatus>
                        <ServiceName>Meaningful Use HITSP/C32 v2.5 Validation</ServiceName>
                        <ResultOfTest>Passed</ResultOfTest>
                        <ErrorCount>0</ErrorCount>
                */
                try {
                    Document document = Validator.stringToDom(validationResult);
                    NodeList validationStatusNodeList = document.getElementsByTagName("ValidationStatus");
                    Assert.assertEquals("There should be only one ValidationStatus node.", 1, validationStatusNodeList.getLength());
                    Assert.assertEquals("Complete", validationStatusNodeList.item(0).getTextContent());
                    
                    NodeList resultOfTestNodeList = document.getElementsByTagName("ResultOfTest");
                    Assert.assertEquals("There should be only one ResultOfTest node.", 1, resultOfTestNodeList.getLength());
                    Assert.assertEquals("Passed", resultOfTestNodeList.item(0).getTextContent());
                    
                    NodeList errorCountNodeList = document.getElementsByTagName("ErrorCount");
                    Assert.assertEquals("There should be only one ErrorCount node.", 1, errorCountNodeList.getLength());
                    Assert.assertEquals("0", errorCountNodeList.item(0).getTextContent());
                    
                } catch (SAXException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ValidatorTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            
        }
    }


}
