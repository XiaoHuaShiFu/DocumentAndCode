@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem NT Service Install/Uninstall script
rem
rem Options
rem install                Install the service using Tomcat5 as service name.
rem                        Service is installed using default settings.
rem remove                 Remove the service from the System.
rem
rem name        (optional) If the second argument is present it is considered
rem                        to be new service name                                           
rem
rem $Id: service.bat,v 1.3 2003/12/24 04:40:43 billbarker Exp $
rem ---------------------------------------------------------------------------

rem Guess CATALINA_HOME if not defined
set CURRENT_DIR=%cd%
if not "%CATALINA_HOME%" == "" goto gotHome
set CATALINA_HOME=%cd%
if exist "%CATALINA_HOME%\bin\tomcat.exe" goto okHome
rem CD to the upper dir
cd ..
set CATALINA_HOME=%cd%
:gotHome
if exist "%CATALINA_HOME%\bin\tomcat.exe" goto okHome
echo The tomcat.exe was not found...
echo The CATALINA_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program
goto end
:okHome

set EXECUTABLE=%CATALINA_HOME%\bin\tomcat.exe

rem Set default Service name
set SERVICE_NAME=Tomcat5

if "%1" == "" goto displayUsage
if "%2" == "" goto setServiceName
set SERVICE_NAME=%2
:setServiceName
if %1 == install goto doInstall
if %1 == remove goto doRemove
echo Unknown parameter "%1"
:displayUsage
echo 
echo Usage: service.bat install/remove [service_name]
goto end

:doRemove
rem Remove the service
"%EXECUTABLE%" //DS//%SERVICE_NAME%
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
rem Install the service
"%EXECUTABLE%" //IS//%SERVICE_NAME% --DisplayName "Apache Tomcat" --Description "Apache Tomcat Server - http://jakarta.apache.org/tomcat/"  --Install "%EXECUTABLE%" --ImagePath "%CATALINA_HOME%\bin\bootstrap.jar" --StartupClass org.apache.catalina.startup.Bootstrap;main;start --ShutdownClass org.apache.catalina.startup.Bootstrap;main;stop --Java java --Startup manual
rem Set extra parameters
"%EXECUTABLE%" //US//%SERVICE_NAME% --JavaOptions -Dcatalina.home="\"%CATALINA_HOME%\""#-Djava.endorsed.dirs="\"%CATALINA_HOME%\common\endorsed\""#-Xrs --StdOutputFile "%CATALINA_HOME%\logs\stdout.log" --StdErrorFile "%CATALINA_HOME%\logs\stderr.log" --WorkingPath "%CATALINA_HOME%\bin"
echo The service '%SERVICE_NAME%' has been installed

:end
cd %CURRENT_DIR%
