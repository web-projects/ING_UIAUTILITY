@echo off
cls

IF "%1"=="" GOTO HELP1
IF "%~2"=="" GOTO HELP2
IF "%1"=="IDENTIFY" GOTO RUN
IF "%3"=="" GOTO HELP3
IF "%4"=="" GOTO HELP4

echo  Ingenico Device Updater Compiler

rem Setup here the location of the TC_HOME
rem =================================
SET TC_HOME=C:\TrustCommerce
rem =================================

:: %HOMEDRIVE% = C:
:: Program Files > ProgramFiles

SET JRE_DIR=jre7
SET JDK_BIN=%TC_HOME%/devices/ingenico/%JRE_DIR%
SET TCINGENJAR=%TC_HOME%/jDAL/tcIngenico.jar
SET JPOSJAR=%TC_HOME%/jDAL/jpos/res/jpos113.jar
SET JPOSSUNJAR=%TC_HOME%/jDAL/jpos/res/ijpos113_svcs_sun.jar
SET COMMJAR=%TC_HOME%/devices/ingenico/%JRE_DIR%/lib/ext/comm.jar
::SET XERCESJAR=xercesImpl-2.11.0.jar
::SET XALANJAR=xalan-2.7.2.jar;serializer-2.7.2.jar

SET JRE_PATH=./%JRE_DIR%/bin
SET JRE_BIN=%JDK_BIN%\bin

SET TARGET=UIAUtility
SET OBJ1=UIACheck
SET OBJ2=UIAReboot

:RUN
::@echo on
set DEBUG_JAR=
::set DEBUG_JAR=-Djaxp.debug=1 -Djavax.xml.parsers.SAXParserFactory=%XERCESJAR%
::%JRE_BIN%\java %DEBUG_JAR% -cp %XERCESJAR%;%XALANJAR%;%TCINGENJAR%;%JPOSJAR%;%JPOSSUNJAR%;%COMMJAR%;%TARGET%.jar com.TrustCommerce.%TARGET% %1 %2 %3 %4
%JRE_BIN%\java %DEBUG_JAR% -cp %TCINGENJAR%;%JPOSJAR%;%JPOSSUNJAR%;%COMMJAR%;%TARGET%.jar com.TrustCommerce.%TARGET% %1 %2 %3 %4
echo.

::JPOS Settings lives in: C:\TrustCommerce\jDAL\jpos\res\jpos.xml
@goto END

:HELP1
echo  Missing argument 1:
echo  ------------------- 
echo  arg1 : [REBOOT, IDENTIFY]
echo  Command examples: 
echo  %0 IDENTIFY FQP/JPOS.XML
echo  %0 REBOOT FQP/JPOS.XML iSC250 COM250
goto END

:HELP2
echo  Missing argument 2:
echo  ------------------- 
echo  arg2 : JPOS FILENAME
echo  Command examples: 
echo  %0 IDENTIFY FQP/JPOS.XML
echo  %0 REBOOT FQP/JPOS.XML iSC250 COM250
goto END

:HELP3
echo  Missing argument 3:
echo  ------------------- 
echo  arg3 : Device Name
echo  Command examples: 
echo  %0 REBOOT FQP/JPOS.XML iSC250 COM250
goto END

:HELP4
echo  Missing argument 4:
echo  ------------------- 
echo  arg4 : COM PORT
echo  Command examples: 
echo  %0 REBOOT FQP/JPOS.XML iSC250 COM250
goto END

:END
