package gov.va.common.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import gov.va.common.XMLTestClass;
import junit.framework.Assert;
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
public class MongoDBTest extends XMLTestClass {

    DB testDB;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testDB = MongoDB.getDatabase(getProperties("config/mongo.properties"));
    }
    
    @After
    public void tearDown() {
        testDB.dropDatabase();
        testDB = null;
    }
    
    
    @Test
    public void testInstancesAreSame() {
        Assert.assertSame(testDB, MongoDB.getDatabase(getProperties("config/mongo.properties")));
    }
    
    @Test
    public void testInstancesAreNotSame() {
        Assert.assertNotSame(testDB, MongoDB.getDatabase(getProperties("config/mongo2.properties")));
    }

    @Test
    public void testCollection() {
        DBCollection collection = testDB.getCollection("testCollection");
        assertNotNull(collection);
    }
    
    @Test
    public void testDatabaseIsEmpty() {
        CommandResult stats = testDB.getStats();
        assertEquals(Integer.valueOf(0), (Integer)stats.get("collections"));
        assertEquals(Double.valueOf(0), (Double)stats.get("avgObjSize"));
    }

    
    @Test
    public void testInsert() {
        DBCollection collection = testDB.getCollection("testCollection");

        BasicDBObject document = new BasicDBObject();
        document.put("id", 1001);
        document.put("msg", "test message");

        collection.insert(document);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("id", 1001);

        DBCursor cursor = collection.find(searchQuery);

        assertTrue("There should be a result available.", cursor.hasNext());
        
//        { "_id" : { "$oid" : "50ad153db8ecfadbd9492e0e"} , "id" : 1001 , "msg" : "test message"}

        DBObject result = cursor.next();
        assertTrue(result.toString().contains("_id"));
        assertTrue(result.toString().contains("$oid"));
        assertTrue(result.toString().contains("1001"));
        assertTrue(result.toString().contains("msg"));
        assertTrue(result.toString().contains("test message"));
        
        assertEquals(1001, result.get("id"));
        assertEquals("test message", result.get("msg"));
        
        assertFalse("There should be no result available.", cursor.hasNext());

        

    }
}
