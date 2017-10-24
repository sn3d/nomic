@echo off

set ORIGINAL_DIR=%CD%
cd /d %~dp0\..\
set CURRENT_DIR=%CD%

if exist "%CURRENT_DIR%\conf\setenv.bat" call "%CURRENT_DIR%\conf\setenv.bat"

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo JAVA_HOME is not set
goto end
:gotJavaHome

if not "%NOMIC_HOME%" == "" goto gotNomicHome
set NOMIC_HOME=%CURRENT_DIR%
echo Using NOMIC_HOME: %NOMIC_HOME%
:gotNomicHome

if not "%NOMIC_CONF%" == "" goto gotNomicConf
set NOMIC_CONF=%NOMIC_HOME%\conf\nomic.conf
:gotNomicConf

:exec
SET NOMIC_ALL_OPTS=-Dconfig.file=%NOMIC_CONF% -Dlog4j.configuration=file:%NOMIC_HOME%\conf\log4j.xml -Dnomic.home=%NOMIC_HOME% %JAVA_OPTS% %NOMIC_OPTS%
call java %NOMIC_ALL_OPTS% -jar %NOMIC_HOME%\lib\nomic.jar %*

:end
cd %ORIGINAL_DIR%