@echo off
cls

echo ==================================================
echo         MVN INSTALLATION AND BUILD TOOL
echo ==================================================

set BLD_HOME=C:\Development\Java\ING_UIAUTILITY
set OLDPATH=%PATH%
set path=%path%;C:\apache\apache-maven-3.3.9\bin

:: ***** BUILD TARGET BRANCH *****

:: UIAUtility.jar
echo.
echo Building artifacts ...
echo.
call mvn clean install
echo.
::echo Packaging artifacts ...
::echo.
::call mvn package

echo .
if exist .\target\UIAUtility-1.0-jar-with-dependencies.jar (
  echo Copying jar artifact to %BLD_HOME%\run folder !!
  echo.
  copy /Y %BLD_HOME%\target\UIAUtility-1.0-jar-with-dependencies.jar %BLD_HOME%\run\UIAUtility.jar
)

echo Build Process Complete !!

:DONE
if NOT "%OLDPATH%" == "" set path=%OLDPATH%
set OLDPATH=
