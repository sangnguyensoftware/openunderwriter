@echo off

@rem Copyright Applied Industrial Logic Limited 2017. All rights Reserved
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

set BIN=%OPENUNDERWRITER_HOME%\bin
set TMP=%OPENUNDERWRITER_HOME%\tmp

if defined OPENUNDERWRITER_JBOSS_HOME (
	set JBOSS_HOME=%OPENUNDERWRITER_JBOSS_HOME%
) else (
	set LIFERAY_HOME=%$OPENUNDERWRITER_HOME%\liferay-portal-6.2-ce-ga6
	set JBOSS_HOME=%$LIFERAY_HOME%\jboss-7.1.1
)

if defined JAVA_HOME (
	set PATH=%JAVA_HOME%\bin;%PATH%
)

set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\ail\insurance\main\insurance.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\ail\commercial\main\commercial.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\ail\pageflow\main\pageflow.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\ail\core\main\core.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\ail\core\main\annotation-4.5.jar"
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\liferay\portal\main\mysql.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\beanshell\main\bsh-2.0b5.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\hibernate\main\hibernate-core-4.1.12.Final.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\dom4j\main\dom4j-1.6.1.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\hibernate\main\hibernate-commons-annotations-4.0.1.Final.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\bin\client\jboss-client.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\javax\persistence\api\main\hibernate-jpa-2.0-api-1.0.1.Final.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\apache\commons\jxpath\main\commons-jxpath-1.3.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\hamcrest\main\hamcrest-core-1.3.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\apache\commons\lang\main\commons-lang-2.6.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\apache\commons\collections\main\commons-collections-3.2.1.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\javassist\main\javassist-3.22.0-GA.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\antlr\main\antlr-2.7.7.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\codehaus\castor\main\castor-xml-ail-1.1.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\standalone\deployments\ROOT.war\WEB-INF\lib\commons-logging.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\standalone\deployments\ROOT.war\WEB-INF\lib\xercesImpl.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\standalone\deployments\ROOT.war\WEB-INF\lib\portal-impl.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\itext\itextpdf\main\itextpdf-5.1.2.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\liferay\portal\main\portlet.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\com\liferay\portal\main\portal-service.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\javax\servlet\api\main\jboss-servlet-api_3.0_spec-1.0.0.Final.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\apache\commons\beanutils\main\commons-beanutils-1.8.3.jar
set SETUP_CLASSPATH=%SETUP_CLASSPATH%:%JBOSS_HOME%\modules\org\apache\xalan\main\serializer-2.7.1.jbossorg-1.jar

java -Dcom.ail.core.configure.loaderPropertiesFile=%TMP%\loader.properties -cp %SETUP_CLASSPATH% bsh.Interpreter %BIN%\update-scripts\%1 %JBOSS_HOME% %BIN%
