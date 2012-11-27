package gov.va.common.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gaineys
 */
public class MongoDB {
    
    private static Map<Integer, DB> clientConfigurations = null;
//    protected Mongo mongo;
//    protected DB db;
    

    public static DB getDatabase(){
        return getDatabase(getProperties("config/mongo.properties"));
    }
    
    public static DB getDatabase(Properties properties) {
        initMapIfNecessary();
        int hash = properties.hashCode();
        if (clientConfigurations.get(hash) == null) {
            
            Mongo mongo = null;
            try {
                mongo = new Mongo(properties.getProperty("host"), Integer.valueOf(properties.getProperty("port")));
            } catch (UnknownHostException ex) {
                Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MongoException ex) {
                Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            // get database from MongoDB,
            // if database doesn't exists, mongoDB will create it automatically
            DB db = mongo.getDB(properties.getProperty("database"));
            if(Boolean.parseBoolean((String)properties.get("authenicate")) == true) {
                db.authenticate(properties.getProperty("authUser"), properties.getProperty("authPass").toCharArray());
            }
            clientConfigurations.put(hash, db);
        }
        return clientConfigurations.get(hash);
    }
    
//    private MongoDB(Properties properties) {
//        try {
//            mongo = new Mongo(properties.getProperty("host"), Integer.valueOf(properties.getProperty("port")));
//            // get database from MongoDB,
//            // if database doesn't exists, mongoDB will create it automatically
//            db = mongo.getDB(properties.getProperty("database"));
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MongoException ex) {
//            Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    private static synchronized void initMapIfNecessary() {
        if (clientConfigurations == null) {
            clientConfigurations = new HashMap<Integer, DB>();
        } 
    }
    
    private static Properties getProperties(String propertiesLocation) {
        Properties props = new Properties();
        try {
            props.load(MongoDB.class.getClassLoader().getResourceAsStream(propertiesLocation));
        } catch (IOException ex) {
            Logger.getLogger(MongoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
    
}
