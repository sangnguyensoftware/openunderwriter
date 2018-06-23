rem ### -*- batch file -*- ######################################################
rem #                                                                          ##
rem #  JBoss Bootstrap Script Configuration                                    ##
rem #                                                                          ##
rem #############################################################################

rem # $Id: run.conf.bat 88820 2009-05-13 15:25:44Z dimitris@jboss.org $

rem #
rem # This batch file is executed by run.bat to initialize the environment
rem # variables that run.bat uses. It is recommended to use this file to
rem # configure these variables, rather than modifying run.bat itself.
rem #

rem Uncomment the following line to disable manipulation of JAVA_OPTS (JVM parameters)
rem set PRESERVE_JAVA_OPTS=true

if not "x%JAVA_OPTS%" == "x" (
  echo "JAVA_OPTS already set in environment; overriding default settings with values: %JAVA_OPTS%"
  goto JAVA_OPTS_SET
)

rem #
rem # Specify the JBoss Profiler configuration file to load.
rem #
rem # Default is to not load a JBoss Profiler configuration file.
rem #
rem set "PROFILER=%JBOSS_HOME%\bin\jboss-profiler.properties"

rem #
rem # Specify the location of the Java home directory (it is recommended that
rem # this always be set). If set, then "%JAVA_HOME%\bin\java" will be used as
rem # the Java VM executable; otherwise, "%JAVA%" will be used (see below).
rem #
rem set "JAVA_HOME=C:\opt\jdk1.6.0_23"

rem #
rem # Specify the exact Java VM executable to use - only used if JAVA_HOME is
rem # not set. Default is "java".
rem #
rem set "JAVA=C:\opt\jdk1.6.0_23\bin\java"

rem #
rem # Specify options to pass to the Java VM. Note, there are some additional
rem # options that are always passed by run.bat.
rem #

rem # JVM memory allocation pool parameters - modify as appropriate.
set "JAVA_OPTS=-Xms3072M -Xmx3072M"

rem # Enable TLS 1.2 for PayPay integration
set "JAVA_OPTS=%JAVA_OPTS% -Dhttps.protocols=TLSv1.1,TLSv1.2"

rem # Reduce the RMI GCs to once per hour for Sun JVMs.
set "JAVA_OPTS=%JAVA_OPTS% -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.net.preferIPv4Stack=true"

rem # Warn when resolving remote XML DTDs or schemas.
set "JAVA_OPTS=%JAVA_OPTS% -Dorg.jboss.resolver.warning=true"

rem # Make Byteman classes visible in all module loaders
rem # This is necessary to inject Byteman rules into AS7 deployments
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.modules.system.pkgs=org.jboss.byteman"

rem # Set the default configuration file to use if -c or --server-config are not used
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.server.default.config=standalone.xml"

rem # Sample JPDA settings for remote socket debugging
rem set "JAVA_OPTS=%JAVA_OPTS% -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

rem # Sample JPDA settings for shared memory debugging
rem set "JAVA_OPTS=%JAVA_OPTS% -Xrunjdwp:transport=dt_shmem,address=jboss,server=y,suspend=n"

rem # Use JBoss Modules lockless mode
rem set "JAVA_OPTS=%JAVA_OPTS% -Djboss.modules.lockless=true"

:JAVA_OPTS_SET
				
set "M2_HOME=%DIRNAME%\..\standalone\data\m2"

rem OpenUnderwriter specific options. These are always applied.
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.protocol.handler.modules=com.ail.core"
set "JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8"
set "JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true"
set "JAVA_OPTS=%JAVA_OPTS% -Duser.timezone=GMT"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseCodeCacheFlushing -XX:ReservedCodeCacheSize=128m"
set "JAVA_OPTS=%JAVA_OPTS% -Dhttps.protocols=TLSv1.1,TLSv1.2"
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.arjuna.ats.jta.JTAEnvironmentBean.orphanSafetyInterval=40000"
set "JAVA_OPTS=%JAVA_OPTS% -Dkie.maven.settings.custom=%M2_HOME%\settings.xml"
set "JAVA_OPTS=%JAVA_OPTS% -Dmaven.repo.local=%M2_HOME%\repository"
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.media.jai.disableMediaLib=true"
set "JAVA_OPTS=%JAVA_OPTS% -Dorg.codehaus.janino.source_debugging.enable=true"