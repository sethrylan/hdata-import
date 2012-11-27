package gov.va.iehr.umls;

import gov.nih.nlm.nls.lvg.Api.LuiNormApi;
import gov.va.common.xml.XMLValidation;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gaineys
 */
public class LVGClient {
    
    protected static LuiNormApi lui;
    protected static LVGClient instance;
    private static final String lvgConfigResource = "config/lvg.properties";
    
    public static LVGClient getInstance(){
        if (instance == null) {
            instance = new LVGClient();
        }
        return instance;
    }
    
    private LVGClient() {
        // NOTE: Hashtable construction for LuiNormApi does not work
        
        lui = new LuiNormApi(getResource(lvgConfigResource).getFile());
        
        // NOTE: CleanUp() method must be called if persisting to LVG database
        
    }

    
    public String normalize(String str) {
        try {
            return lui.Mutate(str);
        } catch (Exception ex) {
            Logger.getLogger(LVGClient.class.getName()).log(Level.SEVERE, null, ex);
            return str;
        }
    }

    
    private static URL getResource(String resourceClassPath) {
        return XMLValidation.class.getClassLoader().getResource(resourceClassPath);
    }
    
}
