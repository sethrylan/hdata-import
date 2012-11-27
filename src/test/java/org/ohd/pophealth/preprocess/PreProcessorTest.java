package org.ohd.pophealth.preprocess;

import gov.va.common.XMLTestClass;
import org.astm.ccr.ContinuityOfCareRecord;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author gaineys
 */
public class PreProcessorTest extends XMLTestClass {
    
    public PreProcessorTest() {
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
    @Ignore
    public void testPreProcess() {
        PreProcessor.getInstance().preProcess(null);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


    @Test
    @Ignore
    public void testFixTobaccoHx() {
        System.out.println("fixTobaccoHx");
        ContinuityOfCareRecord ccr = null;
        PreProcessor instance = null;
        ContinuityOfCareRecord expResult = null;
        ContinuityOfCareRecord result = instance.fixTobaccoHx(ccr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testFixEncounters() {
        System.out.println("fixEncounters");
        ContinuityOfCareRecord ccr = null;
        PreProcessor instance = null;
        ContinuityOfCareRecord expResult = null;
        ContinuityOfCareRecord result = instance.fixEncounters(ccr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testInferCodes() {
        System.out.println("inferCodes");
        ContinuityOfCareRecord ccr = null;
        PreProcessor instance = null;
        ContinuityOfCareRecord expResult = null;
        ContinuityOfCareRecord result = instance.inferCodes(ccr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
