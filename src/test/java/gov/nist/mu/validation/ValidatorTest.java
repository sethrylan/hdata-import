package gov.nist.mu.validation;

import gov.va.common.ClassPathSearcher;
import gov.va.common.TestClass;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gaineys
 */
public class ValidatorTest extends TestClass {
        
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

            Assert.assertNotNull("InputStream is null for " + filename, xmlMap.get(filename));
            try {
                Assert.assertTrue(xmlMap.get(filename).available() > 0);
            } catch (IOException ex) {
                Assert.fail(filename + " is not available.");
            }
            
            String validationResult = Validator.validate(xmlMap.get(filename));
            
            String date = Validator.createDateOfTest();
            String time = Validator.createTimeOfTest();
            
            String resourceRootDirectory = getResource("").getFile(); // i.e., <project-root>/target/test-classes
            File targetDirectory = new File(resourceRootDirectory).getParentFile();
            String outputfilename = targetDirectory.getAbsolutePath() + "\\nist.mu.Validator\\validationResult" + date + "-" + time + ".xml";
            
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
    }

   

}
