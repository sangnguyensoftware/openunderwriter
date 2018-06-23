@echo off

@rem Copyright Applied Industrial Logic Limited 2014. All rights Reserved
@rem
@rem This program is free software; you can redistribute it and/or modify it under
@rem the terms of the GNU General Public License as published by the Free Software
@rem Foundation; either version 2 of the License, or (at your option) any later 
@rem version.
@rem
@rem This program is distributed in the hope that it will be useful, but WITHOUT
@rem ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
@rem FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
@rem more details.
@rem
@rem You should have received a copy of the GNU General Public License along with
@rem this program; if not, write to the Free Software Foundation, Inc., 51 
@rem Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

setlocal
set OPENUNDERWRITER_HOME=%CD%\..
set TMP=%OPENUNDERWRITER_HOME%\tmp
set LIB=%OPENUNDERWRITER_HOME%\lib
set BIN=%OPENUNDERWRITER_HOME%\bin
set JBOSS_HOME=%OPENUNDERWRITER_HOME%\liferay-portal-@liferay.version@\jboss-@jboss.version@
set PROGNAME=run.bat
set DB_USERNAME=
set DB_PASSWORD=

set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\com\ail\insurance\main\insurance.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\com\ail\core\main\core.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\com\liferay\portal\main\mysql.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\org\beanshell\main\bsh-2.0b5.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\org\apache\xalan\main\xalan-2.7.1.jbossorg-1.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\org\apache\xalan\main\serializer-2.7.1.jbossorg-1.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%JBOSS_HOME%\modules\org\apache\commons\jxpath\main\commons-jxpath-1.3.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%;%LIB%\liquibase.jar

if exist "%TMP%\setup" (goto start-server)

echo.
echo OpenUnderwriter Database Setup
echo ==============================
echo The first time that you run OpenUnderwriter this script will create databases in 
echo MySQL and populate them with content. It will also create an OpenUnderwriter database
echo user. To do this successfully the script will need the username and password 
echo of a user who has the necessary permissions.
echo This user will only be used to run the scripts. The OpenUnderwriter server itself
echo uses the less privileged user created by the scripts.
echo. 
echo You will not be prompted for these user details again.
echo. 

echo %* | findstr /c:"/nodemo" 1>nul
if "%ERRORLEVEL%" == "0" ( set NODEMO=yes)
echo %* | findstr /c:"/nostart" 1>nul
if "%ERRORLEVEL%" == "0" ( set NOSTART=yes)

set /p DB_HOST="Please enter your MySQL host (default: localhost):"
set /p DB_USERNAME="Please enter your MySQL username (default: root):"
set /p DB_PASSWORD="Please enter your MySQL password (leave blank for no password):"
    
if "x%DB_HOST%" == "x" (set DB_HOST=localhost) 
if "x%DB_USERNAME%" == "x" (set DB_USERNAME=root) 
if "x%DB_PASSWORD%" == "x" (set PW_OPTION=) else (set PW_OPTION=--password=%DB_PASSWORD%) 

echo.
echo Checking environment...

java -cp "%SETUP_CLASSPATH%" bsh.Interpreter "%BIN%\env-checker.bsh" "%DB_HOST%" "%DB_USERNAME%" "%DB_PASSWORD%"
if "%ERRORLEVEL%" == "1" (goto :eof)

echo Running database scripts...

cd "%LIB%"
mysql --host=%DB_HOST% -u %DB_USERNAME% %PW_OPTION% < "%LIB%\Setup.sql"
if "%ERRORLEVEL%" == "1" (cd "%BIN%" & echo "Failed to execute the MySQL database setup script." & goto :eof)
    
if "%NODEMO%" == "yes" (
	if exist %JBOSS_HOME%\standalone\deployments\demo-product-loader-hook.war (
		del /Q %JBOSS_HOME%\standalone\deployments\demo-product-loader-hook.war*
	)
) else (
	cd "%BIN%"

	java -cp "%SETUP_CLASSPATH%" com.ail.insurance.actuarial.DataGenerator ^
		com.mysql.jdbc.Driver ^
		jdbc:mysql://%DB_HOST%/@dbname.dwh.prefix@_AIL_Base_DataSource_Master_Motor ^
		%DB_USERNAME% ^
		"%DB_PASSWORD%"
	    
	java -cp "%SETUP_CLASSPATH%" com.ail.insurance.policy.DataGenerator ^
		com.mysql.jdbc.Driver ^
		jdbc:mysql://%DB_HOST%/@dbname.dwh.prefix@_AIL_Base_Report ^
		%DB_USERNAME% ^
		"%DB_PASSWORD%"
	    
	cd "%LIB%\content\openunderwriter"
	mysql --host=%DB_HOST% -u %DB_USERNAME% %PW_OPTION% < ./TestData.sql
)

mkdir "%TMP%"
echo > "%TMP%\setup"

:start-server

java -cp "%SETUP_CLASSPATH%" bsh.Interpreter "%BIN%/liquibase.bsh" "%JBOSS_HOME%" "%OPENUNDERWRITER_HOME%"
if "%ERRORLEVEL%" == "1" (goto :eof)

if "%NOSTART%" == "yes" (goto :eof)
    
cd "%JBOSS_HOME%\bin"
.\standalone.bat
