#!/bin/bash

[ -z "$OU_HOME" ] && echo "OU_HOME is not set" && exit
[ -z "$DB_USERNAME" ] && echo "DB_USERNAME is not set" && exit
[ -z "$DB_PASSWORD" ] && echo "DB_PASSWORD is not set" && exit
[ -z "$DB_HOST" ] && echo "DB_HOST is not set" && exit
[ -z "$DB_PORT" ] && echo "DB_PORT is not set" && exit

set +e
ROLE_EXISTS_CHECK=$(mysql -u $DB_USERNAME --password=$DB_PASSWORD --host=$DB_HOST --port=$DB_PORT ou_trunk_trunk_liferay -Nse 'select count(*) from role_ where name="Policy Administrator"')
set -e

if [ ! "$ROLE_EXISTS_CHECK" -eq "1" ]; then
    echo ""
    echo "OU-996 Add new Policy Administrator role"
    echo "========================================"
    echo "This update requires a manual step. Once the server has started, an update script" 
    echo "must be run manually from the Liferay console. To do this, press [enter] now and"
    echo "once the server has started: "
	echo "1) Login to Liferay as an administrator."
	echo "2) Open the control panel (top right menu 'Admin' -> 'Control Panel'."
	echo "3) Click 'Server Administration' in the Configuration section."
	echo "4) Open the 'Script' tab."
	echo "5) Select 'Beanshell' in the language drop down."
	echo "6) Copy and past the script (below) into the script text area."
	echo "7) Click 'Execute'."
	echo ""
	echo "The script can be found here: https://openunderwriter.atlassian.net/secure/attachment/18649/OU-996%20Upgrade%20script.txt"
	echo ""
    
    read -p "Please enter to continue. "
fi

exit 0