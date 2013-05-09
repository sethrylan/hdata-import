# hData Importer
	
Proof of concept for CCR and CDA validation techniques and import to a Mongo database

## Install Steps

1. install org.projecthdata.hdata-schemas with command (in cmd, not powershell):
```
mvn install:install  
     or  
mvn install:install-file -DgroupId=org.projecthdata -DartifactId=hdata-schemas -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/hdata-schemas/target/hdata-schemas-1.0-SNAPSHOT.jar -DgeneratePom=true  
```

1. install org.projecthdata.java-hstore with command (in cmd, not powershell):
```
mvn install:install-file -DgroupId=org.projecthdata -DartifactId=java-hstore -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=C:\Projects\IEHR\hData\java-hstore\target\java-hstore-1.0-SNAPSHOT.jar -DgeneratePom=true
```

1. Use local repo with:
```
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/hdata-schemas/target/hdata-schemas-1.0-SNAPSHOT.jar -DgroupId=org.projecthdata -DartifactId=hdata-schemas -Dversion=1.0-SNAPSHOT  
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=C:/Projects/IEHR/hData/java-hstore/target/java-hstore-1.0-SNAPSHOT.jar -DgroupId=org.projecthdata -DartifactId=java-hstore -Dversion=1.0-SNAPSHOT
```

1. Install [LVG2012](http://lexsrv3.nlm.nih.gov/LexSysGroup/Projects/lvg/2012/docs/userDoc/install/mySql.html):
```
CREATE USER 'lvg'@'localhost' IDENTIFIED BY 'lvg';
```
NOTE: set root password to something without special characters  
Recommended settings of my.ini for a large database
```
query_cache_size=100M  
query_cache_limit=3M  
myisam_sort_buffer_size=200M  
bulk_insert_buffer_size=100M  
join_buffer_size=100M  
key_buffer_size=600M  
read_buffer_size=200M  
sort_buffer_size=500M
```
Disable InnoDB/transaction support.  
NOTE: Do not create database lvg2012 as described in online documentation. This step is done automatically by the scripts used later.  
EDIT E:\lvg\lvg2012\loadDb\sources\gov\nih\nlm\nls\lvg\loadDb\MySql\db.cfg
```
#-------------------  
#DB_DRIVER = org.gjt.mm.mysql.Driver  
DB_DRIVER=com.mysql.jdbc.Driver  
DB_HOST = localhost  
DB_NAME = lvg2012  
DB_USERNAME = lvg  
DB_PASSWORD = lvg  
#-------------------  
DBA_USERNAME=root  
DBA_PASSWORD=MYPASSWORD  
#-------------------  
LVG_DIR = LVG_DIR =E:/lvg/lvg2012  
```
Edit and run E:\lvg\lvg2012\loadDb\bin\1.SetupMySql_win.bat
```
set LVG_DIR=E:\lvg\lvg2012\
set JDBC=C:\Users\gaineys.FWT2HS1\Downloads\mysql-connector-java-5.1.22-bin.jar
```
```
cd ${LVG_DIR}/loadDb/bin/
.\1.SetupMySql_win.bat
```
Build LoadDB project with Ant.
Edit and run E:\lvg\lvg2012\loadDb\bin\2.LoadDb_win.bat
```
set LVG_DIR=E:\lvg\lvg2012\
set JDBC=C:\Users\gaineys.FWT2HS1\Downloads\mysql-connector-java-5.1.22-bin.jar
```
```
.\2.LoadDb_win.bat
```
Edit and run E:\lvg\lvg2012\loadDb\bin\3.TestDb_win.bat
```
set LVG_DIR=E:\lvg\lvg2012\
set JDBC=C:\Users\gaineys.FWT2HS1\Downloads\mysql-connector-java-5.1.22-bin.jar
```
```
.\3.TestDb_win.bat
```
1. Install [MongoDB](http://www.mongodb.org/) and edit config/mongo.properties to match local configuration.
1. Run with
```
mvn jetty:run
```
1. Access hData rest page at http://localhost:8080/webresources/12345/allergies/doc1


##Notes

JAXP in 1.6 has a SAXParserFactory that doesn't override setSchema().

This may be a version of org.apache.xerces.jaxp.SAXParserFactoryImpl before 2.7.0, so set the parser factory manually with
```
    System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
```
Before calling SAXParserFactory.newInstance().


##References
http://www.projecthdata.org/documents/pubs/hData%20Record%20Format-v8.pdf


## License

[sethrylan.mit-license.org](http://sethrylan.mit-license.org/)

