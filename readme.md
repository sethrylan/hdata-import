# hData Importer
	
Proof of concept for CCR and CDA validation techniques and import to a Mongo database

## Install Steps

install org.projecthdata.hdata-schemas with command (in cmd, not powershell):
```
mvn install:install  
     or  
mvn install:install-file -DgroupId=org.projecthdata -DartifactId=hdata-schemas -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/hdata-schemas/target/hdata-schemas-1.0-SNAPSHOT.jar -DgeneratePom=true  
```

install org.projecthdata.java-hstore with command (in cmd, not powershell):
```
mvn install:install-file -DgroupId=org.projecthdata -DartifactId=java-hstore -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=C:\Projects\IEHR\hData\java-hstore\target\java-hstore-1.0-SNAPSHOT.jar -DgeneratePom=true
```

Use local repo with:
```
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/hdata-schemas/target/hdata-schemas-1.0-SNAPSHOT.jar -DgroupId=org.projecthdata -DartifactId=hdata-schemas -Dversion=1.0-SNAPSHOT


mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/java-hstore/target/java-hstore-1.0-SNAPSHOT.jar -DgroupId=org.projecthdata -DartifactId=java-hstore -Dversion=1.0-SNAPSHOT
```

Install [MongoDB](http://www.mongodb.org/) and edit config/mongo.properties to match local configuration.


Run with

```
mvn jetty:run
```

Access hData rest page at http://localhost:8080/webresources/12345/allergies/doc1


##Notes

JAXP in 1.6 has a SAXParserFactory that doesn't override setSchema().

This may be a version of org.apache.xerces.jaxp.SAXParserFactoryImpl before 2.7.0, so set the parser factory manually with
```
    System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
```
Before calling SAXParserFactory.newInstance().


##References
http://www.projecthdata.org/documents/pubs/hData%20Record%20Format-v8.pdf


