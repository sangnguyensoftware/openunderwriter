@echo off

set OPENUNDERWRITER_HOME=%CD%\..
set JBOSS_HOME=%OPENUNDERWRITER_HOME%\liferay-portal-6.2-ce-ga6\jboss-7.1.1

cd %JBOSS_HOME%\bin
.\jboss-cli.bat --connect command=:shutdown
