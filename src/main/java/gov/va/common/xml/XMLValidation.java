package gov.va.common.xml;

import gov.va.common.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLValidation {
    
    public static enum Schema {CCR, CCD, C32, CDA, CDA_HL7, CCD_HL7};
    
    private static final Map<Schema, String> schemata = new HashMap<Schema, String> () {{
//        put(Schema.C32, "schema/C32_CDA.xsd");
//        put(Schema.C32_EXTENDED, "schema/C32_POCD_MT000040.xsd");
        put(Schema.CCR, "schema/ADJE2369-05.xsd");
        put(Schema.CCD, "schema/ccd.xsd");
        put(Schema.C32, "schema/C32_CDA.xsd");
        put(Schema.CDA, "schema/cda.xsd");
        put(Schema.CDA_HL7, "schema/CDA_HL7.xsd");
        put(Schema.CCD_HL7, "schema/CCD_HL7.xsd");
    }};
    
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";    
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";


    /**
     * Parses the file using SAX to check that it is a well-formed XML file
     *
     * @param xmlInputStream
     * @return
     */
    public static boolean isXMLWellFormed(InputStream xmlInputStream)  {
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        try {
            if (xmlInputStream.available() == 0) {
                return false;
            } else {
                SAXParser parser;
                DefaultHandler handler;
                try {
                    SAXParserFactory spfactory = SAXParserFactory.newInstance();
                    spfactory.setNamespaceAware(true);
                    spfactory.setXIncludeAware(true);
                    spfactory.setValidating(true);
                    parser = spfactory.newSAXParser();
                    handler = new DefaultHandler();

                } catch (Exception e) {
                    Log.getLogger().log(Level.SEVERE, e.toString());
                    return false;
                }
                
                try {
                    parser.parse(xmlInputStream, handler);
                    return true;
                } catch (SAXException e) {
                    Log.getLogger().log(Level.SEVERE, "Error: SAX parser Exception");
                    return false;
                } catch (Exception e) {
                    Log.getLogger().log(Level.SEVERE, "exception when parsing the file: {0}", e.getMessage());
                    return false;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(XMLValidation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            System.clearProperty("javax.xml.parsers.SAXParserFactory");
        }
    }
    
    /**
     * Checks that the XML document is valid for the specified schema (referenced in XSD lookup table)
     *
     * @param xmlInputStream
     * @return
     */
    public static Map<String, String> isXMLValidForSchema(InputStream xmlInputStream, Schema schema) {
        try {
            if (xmlInputStream.available() == 0) {
                return null;
            } else {
                return isXMLValidForSchema(xmlInputStream, getResource(schemata.get(schema)));
            }
        } catch (IOException ex) {
            Logger.getLogger(XMLValidation.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     *
     * @param xmlInputStream
     * @param xsdLocation
     * @return
     */
    private static Map<String, String> isXMLValidForSchema(InputStream xmlInputStream, URL xsdLocation) {

        Map<String, String> result = new HashMap<String, String>();
        try {
            ValidationErrorHandler handler = new ValidationErrorHandler();
            DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
            docfactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            docfactory.setAttribute(JAXP_SCHEMA_SOURCE, new File(xsdLocation.getFile()));
            docfactory.setNamespaceAware(true);
            docfactory.setValidating(false);
//            docfactory.setXIncludeAware(true); Not Supported
            DocumentBuilder parser = docfactory.newDocumentBuilder();
            parser.setErrorHandler(handler);
            Document document = parser.parse(xmlInputStream);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            javax.xml.validation.Schema schema = factory.newSchema(xsdLocation);
            Validator validator = schema.newValidator();
            validator.setErrorHandler(handler);
            DOMSource domSource = new DOMSource(document);
            validator.validate(domSource);
            List<ValidationException> exceptions = handler.getExceptions();

            if (exceptions == null || exceptions.isEmpty()) {
                result.put("Result", "PASSED");
                return result;
            } else {
                Integer nbOfErrors = 0;
                Integer nbOfWarnings = 0;
                Integer exceptionCounter = 0;
                for (ValidationException ve : exceptions) {
                    exceptionCounter++;
                    if (ve.getSeverity().equals("warning")) {
                        nbOfWarnings++;
                    } else {
                        nbOfErrors++;
                    }
                    result.put("message" + exceptionCounter.toString(), ve.getSeverity() + ": " + ve.getMessage());
                }
                result.put("nbOfErrors", nbOfErrors.toString());
                result.put("nbOfWarnings", nbOfWarnings.toString());
                if (nbOfErrors > 0) {
                    result.put("Result", "FAILED");
                } else {
                    result.put("Result", "PASSED");
                }
                return result;
            }

        } catch (SAXException e) {
            return handleException(e);
        } catch (ParserConfigurationException e) {
            return handleException(e);
        } catch (IOException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static Map<String, String> handleException(Exception e) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("Result", "FAILED");
        result.put("nbOfErrors", "1");
        result.put("message", "error: " + e.getMessage());
        return result;
    }
    
    private static URL getResource(String resourceClassPath) {
        return XMLValidation.class.getClassLoader().getResource(resourceClassPath);
    }
    
    
        /**
     * Validates a document against XSD schemas.
     * NOTE: Document must be build with namespace awareness and order of the List is significant
     */
	private boolean validate(Document document, List<Source> sources) throws Exception {
    	boolean isXmlValid;
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    	
		javax.xml.validation.Schema schema = factory.newSchema((Source[])sources.toArray());
		
        try {
    		Validator validator = schema.newValidator();
    		validator.validate(new DOMSource(document));
            isXmlValid = true;
        } catch (SAXException ex) {
        	ex.printStackTrace();
            isXmlValid = false;
        } catch (IOException ex) {
        	ex.printStackTrace();
	        isXmlValid = false;
	    }
        return isXmlValid;
	}
    
    public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}


}
