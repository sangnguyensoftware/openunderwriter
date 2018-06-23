#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

if [ -e "$OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/bin/standalone.conf" ]; then
	sed -ie 's#-XX:MaxPermSize=[0-9A-Za-z]*##g' $OU_HOME/liferay-portal-6.2.0-ce-ga1/jboss-7.1.1/bin/standalone.conf
fi