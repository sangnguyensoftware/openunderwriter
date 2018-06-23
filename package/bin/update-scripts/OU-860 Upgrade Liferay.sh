#!/bin/bash

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit

OLD_LIFERAY_VERSION=$OU_HOME/liferay-portal-6.2.0-ce-ga1
NEW_LIFERAY_VERSION=$OU_HOME/liferay-portal-6.2-ce-ga6
SRC=${UNPACK_DIR}/openunderwriter-community-${NEW_VERSION}

if [ ! -d $NEW_LIFERAY_VERSION ] && [ -d $OLD_LIFERAY_VERSION ]; then
	echo Moving $OLD_LIFERAY_VERSION to $NEW_LIFERAY_VERSION
	mv $OLD_LIFERAY_VERSION $NEW_LIFERAY_VERSION
	rm -rf $NEW_LIFERAY_VERSION/jboss-7.1.1/standalone/deployments/welcome-theme*
	
	cp ${SRC}/liferay-portal-6.2-ce-ga6/jboss-7.1.1/bin/standalone.conf $NEW_LIFERAY_VERSION/jboss-7.1.1/bin
	cp ${SRC}/liferay-portal-6.2-ce-ga6/jboss-7.1.1/bin/standalone.conf.bat $NEW_LIFERAY_VERSION/jboss-7.1.1/bin
	sed -i '$a company.default.home.url=/web/ou/welcome' $NEW_LIFERAY_VERSION/portal-ext.properties
	
	COMPONENTS="calendar-portlet.war kaleo-web.war marketplace-portlet.war notifications-portlet.war opensocial-portlet.war resources-importer-web.war ROOT.war sync-web.war web-form-portlet.war"
	for f in $COMPONENTS; do
		echo Replacing ${f}
		rm -rf ${NEW_LIFERAY_VERSION}/jboss-7.1.1/standalone/deployments/${f}*
		cp -R ${SRC}/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/deployments/${f} ${NEW_LIFERAY_VERSION}/jboss-7.1.1/standalone/deployments/
		touch ${NEW_LIFERAY_VERSION}/jboss-7.1.1/standalone/deployments/${f}.dodeploy
		chown -R openunderwriter.openunderwriter ${NEW_LIFERAY_VERSION}/jboss-7.1.1/standalone/deployments/${f}*
	done
fi