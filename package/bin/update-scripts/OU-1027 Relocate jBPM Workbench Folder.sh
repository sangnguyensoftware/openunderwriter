#!/bin/bash

[ -z "$OU_HOME" ] && echo "OU_HOME is not set" && exit

STANDALONE_CONF="$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/bin/standalone.conf"

if [ -f "$STANDALONE_CONF" ]; then
	if [ -z "`grep org.uberfire.nio.git.dir $STANDALONE_CONF`" ]; then
		UBERFIRE_DIR="$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/data/uberfire"
	
		[ -d $OU_HOME/bin/.niogit ] && mv $OU_HOME/.niogit $UBERFIRE_DIR
		
		echo 'JAVA_OPTS="$JAVA_OPTS -Dorg.uberfire.nio.git.dir=$DIRNAME/../standalone/data/uberfire"' >> $STANDALONE_CONF
	fi
fi
