package gov.va.iehr.hdata;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.io.InputStream;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;


/**
 *
 * @author gaineys
 */
public class MongoHdrImporter {
    
    public static String importRecord(InputStream inputFile, DBCollection collection) {
        XMLSerializer xmlSerializer = new XMLSerializer();  
        JSON jsonDocument = xmlSerializer.readFromStream( inputFile );
        DBObject bson = (DBObject)com.mongodb.util.JSON.parse(jsonDocument.toString() );
        CommandResult result = collection.insert(bson).getLastError();
        if(result != null) {
            return result.getErrorMessage();
        }
        return null;
    }
}
