package gov.va.iehr.umls;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gaineys
 */
public class LVGClientTest {

    private LVGClient testClient;
    
    public LVGClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testClient = LVGClient.getInstance();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testNormalize() {
        assertEquals("disease gehrig lou", testClient.normalize("lou gehrig's disease")); 
        assertEquals("disease gehrig lou", testClient.normalize("lou gehrigs disease")); 
        assertEquals("disease gehrig lou", testClient.normalize("lou gehrig's diseases")); 
        assertEquals("disease gehrig lou", testClient.normalize("lou's gehrig's disease")); 
        
        assertEquals("run", testClient.normalize("running"));
        assertEquals("run", testClient.normalize("ran"));
        assertEquals("runningly", testClient.normalize("runningly"));
        
        assertEquals("perspicacity", testClient.normalize("perspicacity"));
        assertEquals("immunizationally", testClient.normalize("immunizationally"));
        
    }
}
