package gov.va.iehr.hdata;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import gov.va.common.ClassPathSearcher;
import gov.va.common.TestLogger;
import gov.va.common.XMLTestClass;
import gov.va.common.mongo.MongoDB;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author gaineys
 * @see http://www.edankert.com/validate.html
 */
public class MongoHdrImporterTest extends XMLTestClass {

    DB testDB;
    
    private static final String c32Path = "schema/C32_CDA.xsd";
    private static final String ccrPath = "schema/ADJE2369-05.xsd";

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
    @Ignore //   src-resolve: Cannot resolve the name 'NullFlavor' to a(n) 'type definition' component.
    public void validateXMLAgainstSchema() throws SAXException {
        
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema ccrSchema = factory.newSchema(MongoHdrImporterTest.class.getClassLoader().getResource(ccrPath));
//        Schema c32Schema = factory.newSchema(MongoHdrImporterTest.class.getClassLoader().getResource("schema/CDA_HL7.xsd"));
        Schema c32Schema = factory.newSchema(MongoHdrImporterTest.class.getClassLoader().getResource(c32Path));
        Validator ccrValidator = ccrSchema.newValidator();
        Validator c32Validator = c32Schema.newValidator();
        
        ccrValidator.setErrorHandler(new SimpleErrorHandler());
        c32Validator.setErrorHandler(new SimpleErrorHandler());
        
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> ccrXmlMap = cps.findFilesInClassPath(ccrPattern);
        Assert.assertTrue("Too few XML files.", ccrXmlMap.size() > 10);
        for(String filename : ccrXmlMap.keySet()) {
            try {
                Source source = new StreamSource(ccrXmlMap.get(filename));
                ccrValidator.validate(source);
            } catch (SAXException ex) {
                TestLogger.getLogger().log(Level.FINE, filename + " is not valid CCR XML.");
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
        
        Map<String, InputStream> c32XmlMap = cps.findFilesInClassPath(c32Pattern);
        Assert.assertTrue("Too few XML files.", c32XmlMap.size() > minNumberTestFiles);
        for(String filename : c32XmlMap.keySet()) {
            try {
                Source source = new StreamSource(c32XmlMap.get(filename));
                c32Validator.validate(source);
            } catch (SAXException ex) {
                TestLogger.getLogger().log(Level.FINE, filename + " is not valid C32.");
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
    }

    // DOM parsed documents are null for the examples used. Look into this later.
    @Test
    @Ignore
    public void validateXMLAgainstSchemaDOM() throws ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", MongoHdrImporterTest.class.getClassLoader().getResource(ccrPath));
        DocumentBuilder ccrBuilder = factory.newDocumentBuilder();
        
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", MongoHdrImporterTest.class.getClassLoader().getResource(c32Path));
        DocumentBuilder c32Builder = factory.newDocumentBuilder();
        
        ccrBuilder.setErrorHandler(new SimpleErrorHandler());
        c32Builder.setErrorHandler(new SimpleErrorHandler());
        
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> ccrXmlMap = cps.findFilesInClassPath(ccrPattern);
        Assert.assertTrue("Too few XML files.", ccrXmlMap.size() > minNumberTestFiles);
        for(String filename : ccrXmlMap.keySet()) {
            try {
                Document document = ccrBuilder.parse(ccrXmlMap.get(filename));
                Assert.assertNotNull(document.getParentNode());
            } catch (SAXException ex) {
                TestLogger.getLogger().log(Level.FINE, filename + " is not valid CCR XML because ");
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
        
        Map<String, InputStream> c32XmlMap = cps.findFilesInClassPath(c32Pattern);
        Assert.assertTrue("Too few XML files.", c32XmlMap.size() > minNumberTestFiles);
        for(String filename : c32XmlMap.keySet()) {
            try {
                Document document = c32Builder.parse(c32XmlMap.get(filename));
                Assert.assertNotNull(document.getParentNode());
            } catch (SAXException ex) {
                TestLogger.getLogger().log(Level.FINE, filename + " is not valid C32 XML because ");
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
    }
    
    @Test
    @Ignore
    public void validateXMLAgainstSchemaSAX() throws ParserConfigurationException, SAXNotRecognizedException, SAXException{
                
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
                
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        SAXParser parser = factory.newSAXParser();
        parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",new File(MongoHdrImporterTest.class.getClassLoader().getResource(ccrPath).getFile()));
        
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());
        
        ClassPathSearcher cps = new ClassPathSearcher();
        
        Map<String, InputStream> ccrXmlMap = cps.findFilesInClassPath(ccrPattern);
        Assert.assertTrue("Too few XML files.", ccrXmlMap.size() > minNumberTestFiles);
        for(String filename : ccrXmlMap.keySet()) {
            try {
                reader.parse(new InputSource(ccrXmlMap.get(filename)));
            } catch (SAXException ex) {
                Assert.fail(filename + " is not valid CCR XML because " + ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
        
        parser = factory.newSAXParser();
        parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",new File(MongoHdrImporterTest.class.getClassLoader().getResource(c32Path).getFile()));
        reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());

        Map<String, InputStream> c32XmlMap = cps.findFilesInClassPath(c32Pattern);
        Assert.assertTrue("Too few XML files.", c32XmlMap.size() > minNumberTestFiles);
        for(String filename : c32XmlMap.keySet()) {
            try {
                reader.parse(new InputSource(c32XmlMap.get(filename)));
            } catch (SAXException ex) {
                Assert.fail(filename + " is not valid CCR XML because " + ex.getMessage());
            } catch (IOException ex) {
                TestLogger.getLogger().log(Level.FINE, ex.getMessage());
            }
        }
    }
    
    
//    @Test
//    public void validateXMLWellformednessXOM() {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        factory.setValidating(false);
//        factory.setNamespaceAware(true);
//        SAXParser parser;
//        XMLReader reader = null;
//        try {
//            parser = factory.newSAXParser();
//            reader = parser.getXMLReader();
//        } catch (ParserConfigurationException ex) {
//            TestLogger.getLogger().log(Level.SEVERE, null, ex);
//        } catch (SAXException ex) {
//            TestLogger.getLogger().log(Level.SEVERE, null, ex);
//        }
//        
//        reader.setErrorHandler(new SimpleErrorHandler());
//        Builder builder = new Builder(reader);
//
//        ClassPathSearcher cps = new ClassPathSearcher();
//        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(".*\\\\xml\\\\c(c|3)(r|2)\\\\.*\\.xml");
//        Assert.assertTrue("Too few XML files.", xmlMap.keySet().size() > 10);
//        for(String filename : xmlMap.keySet()) {
//            try {
//                nu.xom.Document document = builder.build(xmlMap.get(filename));
//                Assert.assertNotNull(document.getRootElement());
//            } catch (Exception e) {
//                Assert.fail(filename + " is not a wellformed.");
//            }
//        }
//    }
    
    // DOM parsed example documents tend to be null; look into this later.
    @Test
    @Ignore
    public void validateXMLWellformednessDOM() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            TestLogger.getLogger().log(Level.SEVERE, null, ex);
        }
        
        builder.setErrorHandler(new SimpleErrorHandler());
        
        ClassPathSearcher cps = new ClassPathSearcher();
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(".*\\\\xml\\\\c(c|3)(r|2)\\\\.*\\.xml");
        Assert.assertTrue("Too few XML files.", xmlMap.size() > 10);
        for(String filename : xmlMap.keySet()) {
            try {
                Document document = builder.parse(xmlMap.get(filename));
                Assert.assertNotNull(document.getParentNode());
            } catch (Exception e) {
                Assert.fail(filename + " is not a wellformed.");
            }
        }
    }
    
    @Test
    @Ignore //   src-resolve: Cannot resolve the name 'NullFlavor' to a(n) 'type definition' component.
    public void validateXMLWellformednessSAX() {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        SAXParser parser = null;
        XMLReader reader = null;

        try {
            parser = factory.newSAXParser();
            reader = parser.getXMLReader();
        } catch (ParserConfigurationException ex) {
            TestLogger.getLogger().log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            TestLogger.getLogger().log(Level.SEVERE, null, ex);
        }
//        reader.setErrorHandler(new SimpleErrorHandler());

        ClassPathSearcher cps = new ClassPathSearcher();
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(".*\\\\xml\\\\c(c|3)(r|2)\\\\.*\\.xml");
        Assert.assertTrue("Too few XML files.", xmlMap.size() > 10);
        for(String filename : xmlMap.keySet()) {
            try {
                parser.parse(xmlMap.get(filename), new DefaultHandler());
            } catch (Exception e) {
                Assert.fail(filename + " is not a wellformed.");
            }
        }
    }
    
    @Test
    public void testImportRecord() {
        ClassPathSearcher cps = new ClassPathSearcher();
        Map<String, InputStream> xmlMap = cps.findFilesInClassPath(ccrPattern);
        xmlMap.putAll(cps.findFilesInClassPath(c32Pattern));
        Assert.assertTrue("Too few XML files.", xmlMap.size() > 10);
        DBCollection testCollection = testDB.getCollection("testImportXML");
        for(String filename : xmlMap.keySet()) {
            TestLogger.getLogger().log(Level.FINEST, "Testing " + filename);
            InputStream xmlInputStream = xmlMap.get(filename);
            String result = MongoHdrImporter.importRecord(xmlInputStream, testCollection);
            Assert.assertNull("Error on import: " + result, result);
        }
        
        CommandResult stats = testCollection.getStats();
        Assert.assertNotNull(stats);
        Assert.assertEquals(Integer.valueOf(xmlMap.size()), (Integer)stats.get("count"));
        
        BasicDBObject query = new BasicDBObject();
        
        /*
        {     
        "title": "Cypress C32 Patient Test Record: George Skeritt",
        }
        */
        
        query.put("title", "Continuity of Care Document from EHR Doctors");
        
        DBObject singleResult = testCollection.findOne(query);
        
        DBCursor cursor = testCollection.find(query);
        
        Assert.assertEquals("There should be only one result in cursor.", 1, cursor.size());
        
        try {
            while (cursor.hasNext()) {
                Assert.assertEquals("Cursor.next() should equal single result.", cursor.next(), singleResult);
            }
        } finally {
            cursor.close();
        }
    }
    
    
    class SimpleErrorHandler implements ErrorHandler {
        public void warning(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
            System.out.println("**Parsing Warning**" + "  Line:    " + exception.getLineNumber() + "  URI:     " + exception.getSystemId() + "  Message: " + exception.getMessage());        
            throw new SAXException("Warning encountered");
        }

        public void error(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
            System.out.println("**Parsing Error**" + "  Line:    " + exception.getLineNumber() + "  URI:     " + exception.getSystemId() + "  Message: " + exception.getMessage());        
            throw new SAXException("Error encountered");
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
            System.out.println("**Parsing Fatal Error **" + "  Line:    " + exception.getLineNumber() + "  URI:     " + exception.getSystemId() + "  Message: " + exception.getMessage());        
            throw new SAXException("Fatal Error encountered");
        }
    }
    
}
