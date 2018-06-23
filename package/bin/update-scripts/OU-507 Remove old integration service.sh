#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments" ]; then
	for COMP in acceptance-ejb.jar configuration-reset-hook.war configure-server-ejb.jar onrisk-ejb.jar persistence-server-ejb.jar product-manager-ejb.jar quotation-ejb.jar subrogation-ejb.jar; do
		find $OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments -name "${COMP}'*'" -delete
	done
fi