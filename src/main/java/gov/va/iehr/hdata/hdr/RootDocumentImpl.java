/*
 *    Copyright 2009 The MITRE Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package gov.va.iehr.hdata.hdr;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.projecthdata.javahstore.hdr.Extension;
import org.projecthdata.javahstore.hdr.RootDocument;
import org.projecthdata.javahstore.hdr.Section;

/**
 * Sample implementation of RootDocument with a single section for allergies
 *
 * @author marc
 */
public class RootDocumentImpl implements RootDocument {
    
    private String id;
    Map<String, Extension> extensions;
    List<Section> rootSections;
    Date created;
    Date updated;
    private static final String TEST_AUTHOR = "Fred Bloggs";
    private static final String TEST_METADATA = "<DocumentMetaData xmlns='http://projecthdata.org/hdata/schemas/2009/11/metadata'>"
            + "<PedigreeInfo>"
            + "<Author>"
            + TEST_AUTHOR
            + "</Author>"
            + "</PedigreeInfo>"
            + "<DocumentId>xyzzy</DocumentId>"
            + "<Title>The Title</Title>"
            + "<RecordDate>"
            + "<CreatedDateTime>2006-05-04T18:13:51.0Z</CreatedDateTime>"
            + "</RecordDate>"
            + "</DocumentMetaData>";

    /**
     * @param xmlFilePath
     */
    RootDocumentImpl(String patientId) {
        
        // SAX parser used because we do not require the xml manipulation of DOM
        SAXBuilder parser = new SAXBuilder();
        Document doc = null;
        try {
            doc = parser.build(new File("C:\\Projects\\IEHR\\xml\\ccr\\1_Sofia_Weideman.xml"));
        } catch (JDOMException ex) {
            Logger.getLogger(RootDocumentImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RootDocumentImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element root = doc.getRootElement();
        Namespace ns = root.getNamespace(); // urn:astm-org:CCR
        String ccrId = root.getChildText("CCRDocumentObjectID", ns );
        
        System.out.println("root = " + root.toString());
        System.out.println("ns = " + ns.toString());
//        System.out.println("children = " + root.getChildren);
        System.out.println("ccrId = " + ccrId);
                
        this.id = patientId;
        this.created = this.updated = new Date();
        
        
        /* hData extensions here
         * 
         */
        this.extensions = new HashMap<String, Extension>();
        this.rootSections = new ArrayList<Section>();
//        Extension allergiesExtension = new AllergiesExtension();
//        this.extensions.put(allergiesExtension.getId(), allergiesExtension);
//        String sectionPath = "allergies";
//        createChildSection(allergiesExtension, sectionPath, "Allergy Section");
//        Section allergiesSection = getChildSection(sectionPath);
//        try {
//            SectionDocument secDoc = allergiesSection.createDocument("text/plain", new ByteArrayInputStream("Hello World !".getBytes()));
//            DocumentMetadata docMeta = new DocumentMetadata(TEST_METADATA);
//            secDoc.setMetadata(docMeta);
//        } catch (Exception ex) {
//            Logger.getLogger(RootDocumentImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getLastModified() {
        return updated;
    }

    @Override
    public Collection<Extension> getExtensions() {
        return extensions.values();
    }

    @Override
    public Extension getExtension(String id) {
        Extension e = extensions.get(id);
        if (e == null) {
            throw new IllegalArgumentException("Unknown extension ID: " + id);
        }
        return e;
    }

    @Override
    public Collection<Section> getRootSections() {
        return rootSections;
    }

    @Override
    public final void createChildSection(Extension e, String path, String name) {
        this.rootSections.add(new GenericChildSection(e, path, name));
    }

    @Override
    public final Section getChildSection(String segment) {
        Section sec = null;
        for (Section section : rootSections) {
            if (section.getPath().equals(segment)) {
                sec = section;
            }
        }
        return sec;
    }
    
    
    
    

}
