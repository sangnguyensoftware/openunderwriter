#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments" ]; then
	STANDALONE_XML=$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/configuration/standalone.xml
	
	if ! grep -q 'JBPMRealm' $STANDALONE_XML; then
		echo "OU-853 adding new security-domain: JBPMRealm"
		TEMP_FILE=$(mktemp)
		cat > $TEMP_FILE << END_OF_CAT
	                <security-domain name="JBPMRealm">
	                    <authentication>
	                        <login-module code="com.ail.workflow.LiferayLoginModule" flag="required" module="com.ail.workflow"/>
	                        <login-module code="org.jboss.security.auth.spi.RoleMappingLoginModule" flag="optional"> 
	                            <module-option name="rolesProperties" value="\${jboss.server.config.dir}/jbpm-role-mapping.properties"/>
	                        </login-module>
	                    </authentication>
	                </security-domain>
END_OF_CAT
	
		awk '/<security-domain name="other" cache-type="default">/ {while(getline line<"'$TEMP_FILE'"){print line}} //' $STANDALONE_XML > $STANDALONE_XML,
		mv $STANDALONE_XML, $STANDALONE_XML
		rm $TEMP_FILE
	
		if grep -q 'UsersRoles' $STANDALONE_XML; then
			echo "OU-853 removing redundant login-module: UsersRoles"
			awk '/login-module code="UsersRoles"/ {del=1} {if (!del) {print}} /\/login-module/ {del=0} ' < $STANDALONE_XML > $STANDALONE_XML,
			mv $STANDALONE_XML, $STANDALONE_XML
		fi
		
		chown openunderwriter.openunderwriter $STANDALONE_XML 
	fi
fi