#!/bin/bash

# Copyright Applied Industrial Logic Limited 2014. All rights Reserved
#
# This program is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free Software
# Foundation; either version 2 of the License, or (at your option) any later 
# version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
# You should have received a copy of the GNU General Public License along with
# this program; if not, write to the Free Software Foundation, Inc., 51 
# Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

#|
#| NAME
#|    upgrade - Upgrade an existing OpenUnderwriter installation.
#|
#| SYNOPSIS
#|    upgrade [--norestart] [--noarchive] [--inplace] [--help] [--debug] [--ouhome=<home directory>] [--archive=<archive directory>] --version=[target version] 
#|
#| DESCIPTION 
#|     Essentially this script downloads the requested version of OpenUnderwriter
#|     from the project's nexus server; unpacks it; archives the existing installation; stops
#|     service; installs the upgrade and starts the server again. As part of this process it will 
#|     run any version specific upgrade commands - some of which may prompt for input.
#|
#|    --norestart
#|    Do not attempt to stop or start the server. The server must not be running while the 
#|    upgrade script is running, so it is assumed that the server is being stopped and 
#|    started externally to the upgrade script.
#|
#|    --noarchive
#|    Do not archive the existing installation. By default the upgrade script will archive
#|    the existing installation and databases before making any changes.
#|
#|    --inplace
#|    The default installation of OpenUnderwriter creates a versioned directory containing the installation
#|    itself, and a maintains a link to it whose name will be consistent across updates. The --inplace
#|    option will ignore this convention and assume that OpenUnderwriter is simply installed in a folder
#|    whose name will remain consistent across updates.  
#|
#|    --help
#|    Display this help page.
#|
#|    --ouhome=<home directory>
#|    By default OpenUnderwriter is installed in /opt/openunderwriter-community. The --ouhome option can
#|    be used to update installation in some other location. 
#|
#|    --archive=<archive directory>
#|    The upgrade process creates an archive of the OpenUnderwriter installation and databases before 
#|    makeing any changes (unless --noarchive is specified). By default these backups are stored in 
#|    /opt/openunderwriter-community/archive. This can be location can be overridden using this option.
#|
#|    --version=[target version]
#|    The version of OpenUnderwriter that the local installation is being upgraded to.
#|
#|    --debug
#|    Output trace information (set -x) 
#|

function help {
	sed -n 's:^#|::p' $0
}

function usage {
	echo "upgrade.sh: $1"
	echo "usage: upgrade.sh [--noarchive] [--norestart] [--inplace] [--debug] [--archive=<archive directory>] [--ouhome=<OU home directory] --version=<target version>"
}

function setup {
	OU_BASE=/opt/openunderwriter
	OU_HOME=${OU_BASE}/openunderwriter-community
	
	for i in "$@"; do
		case $i in
            --version=*) NEW_VERSION="${i#*=}"; shift;;
            --norestart) NO_RESTART_OPT="$i"; shift;;
            --noarchive) NO_ARCHIVE_OPT="$i"; shift;;
            --archive=*) ARCHIVE_DIR="${i#*=}"; shift;;
             --ouhome=*) OU_HOME="${i#*=}"; shift;;
              --inplace) IN_PLACE_OPT="$i"; shift;;
                --debug) DEBUG_OPT="$i"; shift;;
                 --help) help; exit;;
                      *) usage "$i not recognised"; exit 1;;
		esac
	done
	
	[ -z "$NEW_VERSION"  ] && usage "--version=<target version> not specified. Use --help for more info." && exit 1
	[ -z "$ARCHIVE_DIR" -a -z "$NO_ARCHIVE_OPT" -a ! -z "$IN_PLACE_OPT" ] && usage "--archive=<archive directory> or --noarchive must be specified when --inplace is used. Use --help for more info." && exit 1
		
	if [ -z "$IN_PLACE_OPT" ]; then
		OLD_VERSION=$(readlink $OU_HOME|sed 's:^.*community-::')
		OLD=${OU_BASE}/openunderwriter-community-${OLD_VERSION}
		[ ! -d "$OLD" ] && echo "Cannot find $OLD_VERSION ($OLD does not exist)." && exit 1
		OU_HOME=${OU_BASE}/openunderwriter-community
	fi
		
	[ ! -w "$OU_HOME" ] && echo "$OU_HOME is not writable. Did you sudo? Use --help for more info." && exit 1

	UNPACK_DIR=/tmp/${NEW_VERSION}
	
	mkdir -p $UNPACK_DIR
}

function fetch_database_settings {
	DB_CONNECTION=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/connection-url/text()[1]')
	DB_USERNAME=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/security/user-name/text()[1]')
	DB_PASSWORD=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/security/password/text()[1]')
	DB_HOST=$(echo $DB_CONNECTION | awk -F/ '{split($3,e,":");if (e[1]=="localhost") print "127.0.0.1"; else print e[1];}')
	DB_PORT=$(echo $DB_CONNECTION | awk -F/ '{if(split($3,e,":")==2) print e[2]; else print "3306";}')
	DB_NAME=$(echo $DB_CONNECTION | awk -F/ '{print $4}')
}

function eval_configuration_xpath {
	cat $OU_HOME/liferay*/jboss*/standalone/configuration/standalone.xml | sed 's:xmlns=".*"::g' | xmllint --xpath "$1" -
}

function run_domain_update_scripts {
	SCRIPTS_DIR=$OU_HOME/bin/update-scripts
	export OU_HOME DB_CONNECTION DB_USERNAME DB_PASSWORD DB_HOST DB_PORT DB_NAME UNPACK_DIR NEW_VERSION
	
	for SCRIPT in $SCRIPTS_DIR/*.sh; do
		if [ ! -f "${SCRIPT}.done" ]; then
			echo "Executing domain update script: ${SCRIPT}"
			bash -e "${SCRIPT}"
			if [ "$?" -ne "0" ]; then
				echo "Fatal error: Upgrade script failed. Please fix this issue before retrying the upgrade."
				exit 1
			fi
			touch "${SCRIPT}.done"
		fi
	done
}

function archive_old_installation {
	if [ -z "$NO_ARCHIVE_OPT" ]; then
		if [ ! -z "$OLD_VERSION" ]; then
			STAMP=$OLD_VERSION
		else
			STAMP=$(date +%y%m%d%H%M)
		fi
		[ -z "$ARCHIVE_DIR" ] && ARCHIVE_DIR=$OU_BASE/archive
		[ ! -d "$ARCHIVE_DIR" ] && mkdir $ARCHIVE_DIR
		zip -qr ${ARCHIVE_DIR}/openunderwriter-${STAMP}.zip ${OU_HOME}
		mysqldump -u $DB_USERNAME --password=$DB_PASSWORD --host=$DB_HOST ou_trunk_trunk_liferay > ${ARCHIVE_DIR}/liferay.sql
		mysqldump -u $DB_USERNAME --password=$DB_PASSWORD --host=$DB_HOST $DB_NAME > ${ARCHIVE_DIR}/openunderwriter.sql
		zip -q ${ARCHIVE_DIR}/database-${STAMP}.zip ${ARCHIVE_DIR}/*.sql
		rm ${ARCHIVE_DIR}/openunderwriter.sql ${ARCHIVE_DIR}/liferay.sql
	fi
}

function download_and_unpack {
	curl http://www.openunderwritercommunity.org/nexus/service/local/repositories/releases/content/openunderwriter/openunderwriter-community/${NEW_VERSION}/openunderwriter-community-${NEW_VERSION}.zip > $UNPACK_DIR/openunderwriter-community-${NEW_VERSION}.zip
	unzip -q $UNPACK_DIR/openunderwriter-community-${NEW_VERSION}.zip -d $UNPACK_DIR
}

function stop_server {
	if [ -z "$NO_RESTART_OPT" ]; then
		service openunderwriter stop 
		while pgrep -u openunderwriter java > /dev/null; do sleep 1; echo -n '.'; done
		echo
	fi
}

function start_server {
	if [ -z "$NO_RESTART_OPT" ]; then 
		service openunderwriter start
	fi
}

function upgrade_openunderwriter {
	SRC=${UNPACK_DIR}/openunderwriter-community-${NEW_VERSION}
	DST=${OU_HOME}
	
	cp -R ${SRC}/liferay-portal-6.2-ce-ga6/jboss-7.1.1/modules/* ${DST}/liferay-portal-*/jboss-7.1.1/modules
	chown -R openunderwriter.openunderwriter ${DST}/liferay-portal-*/jboss-7.1.1/modules
	
	cp -R ${SRC}/bin/* ${DST}/bin
	cp -R ${SRC}/lib/* ${DST}/lib
	
	run_domain_update_scripts
		
	COMPONENTS="bluestone-theme.war claim-service.war command-server-ejb.jar ghana-theme.war greentree-theme.war gwtui-portlet.war integrator-service.war narrowbar-theme.war openunderwriter-theme.war pageflow-portlet.war policy-service.war pageflow-service.war product-change-listener-hook.war base-product-loader-hook.war product-service.war reportwidget-portlet.war workflow.war jbpm-console.war security-service.war"
	[ -f "${DST}/liferay-portal-6.2-*/jboss-7.1.1/standalone/deployments/demo-product-loader-hook.war" ] && COMPONENTS="$COMPONENTS demo-product-loader-hook.war"
	for f in $COMPONENTS; do
		rm -rf ${DST}/liferay-portal-*/jboss-7.1.1/standalone/deployments/${f}
		cp -R ${SRC}/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/deployments/${f} ${DST}/liferay-portal-*/jboss-7.1.1/standalone/deployments
		chown -R openunderwriter.openunderwriter ${DST}/liferay-portal-*/jboss-7.1.1/standalone/deployments/${f}
	done
	
	if [ -z "$IN_PLACE_OPT" ]; then
		mv ${OU_BASE}/openunderwriter-community-${OLD_VERSION} ${OU_BASE}/openunderwriter-community-${NEW_VERSION} 
		rm ${OU_HOME}
		ln -s ${OU_BASE}/openunderwriter-community-${NEW_VERSION} ${OU_HOME}
	fi
}

function run_unpacked_upgrade_script {
	export OU_UPGRADE_INTERNAL_CALL="yes"
	export NEW_VERSION OLD_VERSION NO_RESTART_OPT OU_HOME OU_BASE NO_ARCHIVE_OPT ARCHIVE_DIR IN_PLACE_OPT DEBUG_OPT UNPACK_DIR
			
	${UNPACK_DIR}/openunderwriter-community-${NEW_VERSION}/bin/upgrade.sh
}

function tidy_up {
	rm -rf ${UNPACK_DIR}
}

function main {
	if [ "$OU_UPGRADE_INTERNAL_CALL" = "yes" ]; then
		[ ! -z "$DEBUG_OPT" ] && set -x

		fetch_database_settings
		
		archive_old_installation
	
		stop_server
	
		upgrade_openunderwriter
		
		start_server
	else	
		setup $*
		
		[ ! -z "$DEBUG_OPT" ] && set -x
		
		download_and_unpack
		
		run_unpacked_upgrade_script
		
		tidy_up
	fi
}

main $*
 