#!/bin/bash

[ -z "$OU_HOME" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments" ]; then
	STANDALONE_DIR=$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone
	M2_HOME=$STANDALONE_DIR/data/m2
	
	if [ -d "$STANDALONE_DIR" -a ! -d "$M2_HOME" ]; then
		tee -a $OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1//bin/standalone.conf > /dev/null << 'END'
	
	# Added by "OU-816 Create m2 data folder" upgrade script
	export M2_HOME=$DIRNAME/../standalone/data/m2/
	JAVA_OPTS="$JAVA_OPTS -Dkie.maven.settings.custom=$M2_HOME/settings.xml"
	JAVA_OPTS="$JAVA_OPTS -Dmaven.repo.local=$M2_HOME/repository"
END
	
		mkdir -p $M2_HOME
		
		tee $M2_HOME/settings.xml > /dev/null << 'END'
	<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
		<localRepository>${env.M2_HOME}/repository</localRepository>
	</settings>
END
	
		chown -R openunderwriter.openunderwriter $M2_HOME
	fi
fi

exit 0
