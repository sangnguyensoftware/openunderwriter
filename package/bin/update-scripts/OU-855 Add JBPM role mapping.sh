#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments" ]; then
	JBPM_ROLE_MAPPING_FILE=$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/configuration/jbpm-role-mapping.properties
	
	if [ ! -f $JBPM_ROLE_MAPPING_FILE ]; then
		echo "OU-855 Adding JBPM role mapping file"
	
		cat > $JBPM_ROLE_MAPPING_FILE << END_OF_CAT
Product\u0020Administrator=admin,analyst
Product\u0020Developer=analyst
Agent=user
END_OF_CAT
	
		chown openunderwriter.openunderwriter $JBPM_ROLE_MAPPING_FILE
	fi
fi