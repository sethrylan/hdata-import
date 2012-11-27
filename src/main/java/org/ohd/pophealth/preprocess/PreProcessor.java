package org.ohd.pophealth.preprocess;

import gov.nih.nlm.umls.uts.webservice.RootSourceDTO;
import gov.nih.nlm.umls.uts.webservice.UiLabelRootSource;
import gov.va.iehr.uts.FinderSearchTarget;
import gov.va.iehr.uts.FinderSearchType;
import gov.va.iehr.uts.UTSFinderServiceClient;
import gov.va.iehr.uts.UTSMetadataServiceClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.astm.ccr.Agent;
import org.astm.ccr.AlertType;
import org.astm.ccr.CodeType;
import org.astm.ccr.CodedDescriptionType;
import org.astm.ccr.ContinuityOfCareRecord;
import org.astm.ccr.ContinuityOfCareRecord.Body.Problems;
import org.astm.ccr.EncounterType;
import org.astm.ccr.ProblemType;
import org.astm.ccr.ResultType;
import org.astm.ccr.SocialHistoryType;
import org.astm.ccr.StructuredProductType;
import org.astm.ccr.StructuredProductType.Product;
import org.astm.ccr.TestType;
import gov.va.iehr.umls.LVGClient;

/**
 *
 * @author ohdohd
 */
public class PreProcessor  {

    private static UTSMetadataServiceClient utsMetadata;
    private static UTSFinderServiceClient utsFinder;

    private static LVGClient lvg;
    private static Map<String,RootSourceDTO> rootSources; // list of Coding Systems

    private static PreProcessor instance;
    
    public static PreProcessor getInstance(){
        if (instance == null) {
            instance = new PreProcessor();
        }
        return instance;
    }
    
    private PreProcessor() {
        utsMetadata = new UTSMetadataServiceClient();
        utsFinder = new UTSFinderServiceClient();
        lvg = LVGClient.getInstance();
        setAvailableCS();
    }

    private void setAvailableCS() {
        for (RootSourceDTO rs : utsMetadata.getAllRootSources()) {
            rootSources.put(rs.getAbbreviation(), rs);
        }
        Logger.getLogger(PreProcessor.class.getName()).log(Level.FINEST, "Size of available Coding Systems: {0}", rootSources.size());
    }

    public ContinuityOfCareRecord preProcess(ContinuityOfCareRecord ccr) {
        ccr = inferCodes(ccr);
        ccr = fixTobaccoHx(ccr);
        ccr = fixEncounters(ccr);
        return ccr;
    }

    public ContinuityOfCareRecord fixTobaccoHx(ContinuityOfCareRecord ccr) {
        if (ccr.getBody().getSocialHistory() != null) {
            ProblemType p = fixTobaccoHx(ccr.getBody().getSocialHistory().getSocialHistoryElement());
            if (p != null) {
                // fix the date and set to CCR creation date
                if (p.getDateTime().isEmpty()) {
                    p.getDateTime().add(ccr.getDateTime());
                }
                //Add to existing list of problems or create a new list of needed
                if (ccr.getBody().getProblems() != null) {
                    ccr.getBody().getProblems().getProblem().add(p);
                } else {
                    Problems prblms = new Problems();
                    prblms.getProblem().add(p);
                    ccr.getBody().setProblems(prblms);
                }
            }
        }
        return ccr;
    }
    private static final String[] cptEncounterList = {"99201", "99202", "99203", "99204",
        "99205", "99211", "99212", "99213", "99214", "99215", "99217", "99218", "99219",
        "99220", "99241", "99242", "99243", "99244", "99245", "99341", "99342", "99343",
        "99344", "99345", "99347", "99348", "99349", "99350", "99384", "99385", "99386",
        "99387", "99394", "99395", "99396", "99397", "99401", "99402", "99403", "99404",
        "99411", "99412", "99420", "99429", "99455", "99456"};
    private ArrayList<CodeType> encounterCodes = null;

    private void createEncounterCodes() {
        encounterCodes = new ArrayList<CodeType>();
        for (String c : cptEncounterList) {
            CodeType ct = new CodeType();
            ct.setCodingSystem("CPT");
            ct.setValue(c);
            encounterCodes.add(ct);
        }
    }

    public ContinuityOfCareRecord fixEncounters(ContinuityOfCareRecord ccr) {
        if (encounterCodes == null) {
            createEncounterCodes();
        }
        if (ccr.getBody().getEncounters() != null) {
            for (EncounterType et : ccr.getBody().getEncounters().getEncounter()) {
                if (et.getDescription() == null) {
                    CodedDescriptionType cdt = new CodedDescriptionType();
                    cdt.setText("Fixed Encounter");
                    et.setDescription(cdt);
                }
                for (CodeType ct : encounterCodes) {
                    et.getDescription().getCode().add(ct);
                }
            }
        }
        return ccr;
    }

    public ContinuityOfCareRecord inferCodes(ContinuityOfCareRecord ccr) {
        if (ccr.getBody().getMedications() != null) {
            codeMeds(ccr.getBody().getMedications().getMedication());
        }
        if (ccr.getBody().getImmunizations() != null) {
            codeMeds(ccr.getBody().getImmunizations().getImmunization());
        }
        if (ccr.getBody().getProblems() != null) {
            codeProblems(ccr.getBody().getProblems().getProblem());
        }
        if (ccr.getBody().getAlerts() != null) {
            codeAlerts(ccr.getBody().getAlerts().getAlert());
        }
        if (ccr.getBody().getResults() != null) {
            codeResults(ccr.getBody().getResults().getResult());
        }
        if (ccr.getBody().getVitalSigns() != null) {
            codeResults(ccr.getBody().getVitalSigns().getResult());
        }
        return ccr;
    }

    private void codeMeds(List<StructuredProductType> medications) {
        for (StructuredProductType spt : medications) {
            for (Product p : spt.getProduct()) {
                Logger.getLogger(PreProcessor.class.getName()).log(Level.FINE, "Looking up code for {0}", p.getProductName().getText());
                addCode(p.getProductName(), "rxnorm");
                if (p.getBrandName() != null) {
                    addCode(p.getBrandName(), "rxnorm");
                }
            }
        }
    }

    private void codeAlerts(List<AlertType> alerts) {
        for (AlertType at : alerts) {
            if (at.getDescription() != null) {
                addCode(at.getDescription(), "rxnorm");
            }
            for (Agent ag : at.getAgent()) {
                if (ag.getProducts() != null) {
                    for (StructuredProductType spt : ag.getProducts().getProduct()) {
                        for (Product p : spt.getProduct()) {
                            addCode(p.getProductName(), "rxnorm");
                            if (p.getBrandName() != null) {
                                addCode(p.getBrandName(), "rxnorm");
                            }
                        }
                    }
                }
            }
        }
    }

    private void codeProblems(List<ProblemType> problems) {
        for (ProblemType pt : problems) {
            if (pt.getDescription() != null) {
                addCode(pt.getDescription(), "snomedct");
            }
        }
    }

    private void codeResults(List<ResultType> results) {
        for (ResultType rt : results) {
            if (rt.getDescription() != null) {
                addCode(rt.getDescription(), "lnc");
                addCode(rt.getDescription(), "snomedct");
            }
            for (TestType tt : rt.getTest()) {
                if (tt.getDescription() != null) {
                    // TODO Hack to fix systolic/diastolic
                    if (tt.getDescription().getText() != null) {
                        if ("systolic".equalsIgnoreCase(tt.getDescription().getText())) {
                            // Setting the text to include "blood pressure" will allow for the correct infered code
                            tt.getDescription().setText("Systolic Blood Pressure");
                        } else if ("diastolic".equalsIgnoreCase(tt.getDescription().getText())) {
                            // Setting the text to include "blood pressure" does not work with diastolic, need to force it
                            tt.getDescription().setText("Diastolic Blood Pressure");
                            // Force the add a SNOMEDCT Code
                            CodeType dbp = new CodeType();
                            dbp.setCodingSystem("SNOMEDCT");
                            dbp.setVersion("07/2009");
                            dbp.setValue("163031004");
                            tt.getDescription().getCode().add(dbp);

                        }
                    }
                    // End Hack
                    addCode(tt.getDescription(), "lnc");
                    addCode(tt.getDescription(), "snomedct");
                }
            }
        }
    }

    private ProblemType fixTobaccoHx(List<SocialHistoryType> socialHistoryElements) {
        for (SocialHistoryType sht : socialHistoryElements) {
            if (sht.getType() != null && sht.getStatus() != null) {
                if (sht.getType().getText().equalsIgnoreCase("smoking")) {
                    if (sht.getStatus().getText().equalsIgnoreCase("current")) {
                        ProblemType tobacAddic = new ProblemType();
                        tobacAddic.setCCRDataObjectID(Long.toString(System.currentTimeMillis()));
                        CodedDescriptionType desc = new CodedDescriptionType();
                        desc.setText("Current Smoker");
                        CodeType smoker = new CodeType();
                        smoker.setCodingSystem("SNOMEDCT");
                        smoker.setValue("160603005");
                        smoker.setVersion("2009");
                        desc.getCode().add(smoker);
                        tobacAddic.setDescription(desc);
                        Logger.getLogger(PreProcessor.class.getName()).log(Level.FINE, "Found Current Smoker");
                        return tobacAddic;
                    } else {
                        ProblemType tobacAddic = new ProblemType();
                        tobacAddic.setCCRDataObjectID(Long.toString(System.currentTimeMillis()));
                        CodedDescriptionType desc = new CodedDescriptionType();
                        desc.setText("Non Smoker");
                        CodeType smoker = new CodeType();
                        smoker.setCodingSystem("SNOMEDCT");
                        smoker.setValue("105539002");
                        smoker.setVersion("2009");
                        desc.getCode().add(smoker);
                        tobacAddic.setDescription(desc);
                        Logger.getLogger(PreProcessor.class.getName()).log(Level.FINE, "Found Non Smoker");
                        return tobacAddic;
                    }
                }
            }
        }
        return null;
    }

    private void addCode(CodedDescriptionType cdt, String vocab) {
        if (cdt.getText() != null || vocab != null) {
            RootSourceDTO source = rootSources.get(vocab);
            if (source == null) {
                Logger.getLogger(PreProcessor.class.getName()).log(Level.WARNING, "Could not find vocabulary source: {0}", vocab);
                return;
            }

            String normalizedString = lvg.normalize(cdt.getText());
            
            Logger.getLogger(PreProcessor.class.getName()).log(Level.FINEST, "Normalized to: {0}", normalizedString);
                        
//            // find atoms for normalized string
//            List<UiLabel> atoms = utsFinder.findAtoms(FinderSearchTarget.ATOM, FinderSearchType.NORMALIZEDSTRING,"lou gehrig disease");
//            Set<String> auis = new HashSet<String>();
//            for(UiLabel atom : atoms) {
//                // if the atom is part of the rootsource we want, then add it to the collection
//                if (utsContent.getAtom(atom.getUi()).getSourceDescriptor().getRootSource().equals(source)) {
//                    auis.add(atom.getUi());
//                }    
//            }
//            
//            // find concepts for the collection of atoms
//
//            // multiple codes and values (str) will be returned, specific to the coding system (aka, RootSource)
//            /*
//            mysql> SELECT cui, scui, code, str, sab FROM umls.MRCONSO where cui = "C0032212" order by code;
//            +----------+------------+------------+--------------------------------------+---------------------+
//            | cui      | scui       | code       | str                                  | sab                 |
//            +----------+------------+------------+--------------------------------------+---------------------+
//            | C0032212 | 0000009843 | 0000009843 | duckbill                             | CHV2011_02          |
//            | C0032212 | 0000009843 | 0000009843 | platypus                             | CHV2011_02          |
//            | C0032212 | 395556009  | 395556009  | Platypus                             | SNOMEDCT_2012_01_31 |
//            | C0032212 | 395556009  | 395556009  | Ornithorhynchus anatinus             | SNOMEDCT_2012_01_31 |
//            | C0032212 | 395556009  | 395556009  | Ornithorhynchus anatinus (organism)  | SNOMEDCT_2012_01_31 |
//            | C0032212 | 395556009  | 395556009  | Ornithorhynchus anatinus (organismo) | SCTSPA_2011_10_31   |
//            | C0032212 | 395556009  | 395556009  | Ornithorhynchus anatinus             | SCTSPA_2011_10_31   |
//            | C0032212 | 395556009  | 395556009  | duck-billed platypus                 | SNOMEDCT_2012_01_31 |
//            | C0032212 | 395556009  | 395556009  | Duck-billed platypus                 | SNOMEDCT_2012_01_31 |
//            | C0032212 | 50652007   | 50652007   | Platypus                             | SNOMEDCT_2012_01_31 |
//            
//            */
//            
//            Set<String> cuis = new HashSet<String>();
//            for(String aui : auis) {
//                List<UiLabel> concepts = utsFinder.findConcepts(FinderSearchTarget.AUI, FinderSearchType.EXACT,  aui);
//                for(UiLabel concept: concepts) {
//                    cuis.add(concept.getUi());
//                }
//            }
//            
//            // cuis should all be in the form C####### (e.g., C0000498).
//            // SELECT CUI FROM UMLS.MRCONSO;
//                        
//            Logger.getLogger(PreProcessor.class.getName()).log(Level.FINEST, "Found [{0}] unique CUI matches", cuis.size());
//            
//            // find codes for auis
            
            List<UiLabelRootSource> codes = utsFinder.findCodes(FinderSearchTarget.ATOM, FinderSearchType.NORMALIZEDSTRING, normalizedString);
            
            boolean codeAdded = false;
            for (UiLabelRootSource code : codes) {
                if(code.getRootSource().equals(source.getAbbreviation())){
                    Logger.getLogger(PreProcessor.class.getName()).log(Level.FINEST, "Adding Code: {0} [{1}] for [{2}]", new Object[]{code.getLabel(), code.getUi(), cdt.getText()});
                    CodeType ct = new CodeType();
                    ct.setCodingSystem(source.getAbbreviation());
//                    ct.setVersion(source.getVersion());
                    ct.setValue(code.getUi());
                    cdt.getCode().add(ct);         
                    codeAdded = true;
                } 
            }
            
            if(!codeAdded) {
                Logger.getLogger(PreProcessor.class.getName()).log(Level.FINEST, "No Code Found for: {0}", normalizedString);
            }
            

        }
    }

}
