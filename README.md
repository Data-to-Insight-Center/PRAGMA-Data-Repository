# PRAGMA-Data-Repository
PRAGMA data repository is designed for managing scientific data objects across the boundaries among different domains. 
Our data repository present a convenient and clearly-defined interface that can host both long-tail data objects or large data sets. 

PRAGMA data repository is implemented with MongoDB, which provides sharding feature that distributes the database among different machines while maintain replicas in other machines. Besides, with MongoDB as backend, we also use a single framework to store both metadata and data and offer users the possibility to decide the information they want to have as data objects metdata.

# Architecture
<img src="https://raw.githubusercontent.com/Data-to-Insight-Center/PRAGMA-Data-Repository/master/docs/repo-arch.png" width="400" height="300">

# Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. MongoDB Server V3.0 or higher
4. Apache Tomcat 7 or higher

## Hardware Requirements

1. This software can be deployed on physical resources or VM instance with public network interface.
2. For public access, it requires 1 open port which iptables rules allow traffic through the firewall for Tomcat webapp container.

## Building the Source
Check out source codes:
```
git clone https://github.com/Data-to-Insight-Center/PRAGMA-Data-Repository.git
```

Edit the MongoConfig.xml file found under src/main/resources and set your backend mongoDB uri with username/password if exists.
```
vi PRAGMA-Data-Repository/src/main/resources/MongoConfig.xml
```
Edit the SpringConfig.properties file under src/main/resources;
```
vi PRAGMA-Data-Repository/src/main/resources/SpringConfig.properties
```

Build PRAGMA Data Repository
```
mvn clean install
```
If you want to skip maven test, run the following cmd:
``` 
mvn clean install -Dmaven.test.skip=true
```

## Deploy Data Repository Service 
Please deploy the below war file into your tomcat /webapps directory and enable the service.

```
cp ./target/pragma-data-repo.war <tomcat>/webapps/
```

## Contributing
This software release is under Apache 2.0 Licence.

## Release History
* 0.1.0 1st release 2016.06.24
