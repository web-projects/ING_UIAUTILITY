# README #

This a utility application to reboot Ingenico UIA Devices.

### What is this repository for? ###

* Quick summary
* Version
* Location: https://github.com/web-projects/ING_UIAUTILITY.git

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies

Install local jars as follows:
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
    -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
    
example:
mvn install:install-file -Dfile=C:\Development\Java\ING_UIAUTILITY\lib\jar-in-jar-loader-1.1.jar -DgroupId=org.eclipse.jdt.internal -DartifactId=jar-in-jar-loader -Dversion=1.1 -Dpackaging=jar

* https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html

* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Development References ###

* https://www.databasesandlife.com/java-always-explicitly-specify-which-xml-parser-to-use/

### HISTORY ###

* 20181212 - Initial repository.
* 20181213 - Added REBOOT and IDENTIFY commands.
* 20181213 - Fixes to REBOOT options.
* 20190921 - Added maven build support.
* 20190923 - Added sample maven jar install.
