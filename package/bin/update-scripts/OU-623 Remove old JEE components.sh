#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments" ]; then
	find $OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/standalone/deployments -name 'integrator.war*' -delete
fi
