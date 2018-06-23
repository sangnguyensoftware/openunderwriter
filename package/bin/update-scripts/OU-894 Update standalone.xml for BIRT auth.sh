#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/configuration" ]; then
	STANDALONE_XML=$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/configuration/standalone.xml
	
	if ! grep -q 'AILBaseReportDS' $STANDALONE_XML; then
		echo "OU-625 adding new datasources: AILBaseReportDS"
		TEMP_FILE=$(mktemp)
		cat > $TEMP_FILE << END_OF_CAT1
                <datasource jndi-name="java:jboss/datasources/AILBaseReportDS" pool-name="AILBaseReportDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/OU_trunk_trunk_DWH_AIL_Base_Report</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <security-domain>DWHRealm</security-domain>
                    </security>
                </datasource>
END_OF_CAT1
	
		awk '/<drivers>/ {while(getline line<"'$TEMP_FILE'"){print line}} //' $STANDALONE_XML > $STANDALONE_XML,
		mv $STANDALONE_XML, $STANDALONE_XML
		rm $TEMP_FILE
	fi
		
		
	if ! grep -q 'AILBaseMasterMotorDS' $STANDALONE_XML; then
		echo "OU-625 adding new datasources: AILBaseMasterMotorDS"
		TEMP_FILE=$(mktemp)
		cat > $TEMP_FILE << END_OF_CAT2
                <datasource jndi-name="java:jboss/datasources/AILBaseMasterMotorDS" pool-name="AILBaseMasterMotorDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/OU_trunk_trunk_DWH_AIL_Base_DataSource_Master_Motor</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <security-domain>DWHRealm</security-domain>
                    </security>
                </datasource>
END_OF_CAT2
	
		awk '/<drivers>/ {while(getline line<"'$TEMP_FILE'"){print line}} //' $STANDALONE_XML > $STANDALONE_XML,
		mv $STANDALONE_XML, $STANDALONE_XML
		rm $TEMP_FILE
	fi
		
	if ! grep -q 'security-domain name="DWHRealm"' $STANDALONE_XML; then
		echo "OU-625 adding new security-domain: DWHRealm"
		TEMP_FILE=$(mktemp)
		cat > $TEMP_FILE << END_OF_CAT3
                <security-domain name="DWHRealm">
					<authentication>
						<login-module code="SecureIdentity" flag="required">
							<module-option name="username" value="report"/>
							<module-option name="password" value="7e79e7b0a5cfa097d3a3d352613ace19ad5ba810ccc889437c449b3168405c38df8592078de921bc"/>
						</login-module>
					</authentication>
				</security-domain>
END_OF_CAT3
	
		awk '/<security-domain name="other" cache-type="default">/ {while(getline line<"'$TEMP_FILE'"){print line}} //' $STANDALONE_XML > $STANDALONE_XML,
		mv $STANDALONE_XML, $STANDALONE_XML
		rm $TEMP_FILE
	fi
fi