#!/bin/sh

[ "$OU_HOME" = "" ] && echo "OU_HOME is not set" && exit
[ "$UNPACK_DIR" = "" ] && echo "UNPACK_DIR is not set" && exit

PORTAL_IMPL_PATH=liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/deployments/ROOT.war/WEB-INF/lib/portal-impl.jar

cp $UNPACK_DIR/openunderwriter-community-*/$PORTAL_IMPL_PATH $OU_HOME/$PORTAL_IMPL_PATH
