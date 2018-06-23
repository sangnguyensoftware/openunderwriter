#!/bin/bash
[ -z "$OU_HOME" ] && echo "OU_HOME is not set" && exit
[ -z "$DB_USERNAME" ] && echo "DB_USERNAME is not set" && exit
[ -z "$DB_PASSWORD" ] && echo "DB_PASSWORD is not set" && exit
[ -z "$DB_HOST" ] && echo "DB_HOST is not set" && exit
[ -z "$DB_PORT" ] && echo "DB_PORT is not set" && exit

USER_EXISTS_CHECK=$(mysql -u $DB_USERNAME --password=$DB_PASSWORD --host=$DB_HOST --port=$DB_PORT ou_trunk_trunk_liferay -se "select count(*) from user_ where screenname='upgradehandler';" )

if [ ! "1" -eq "$USER_EXISTS_CHECK" ]; then
	echo ""
	echo "OpenUnderwriter Product Upgrade User"
	echo "===================================="
	echo "As of this release OpenUnderwriter allows product developers to include upgrade scripts"
	echo "inside products. Whenever a new script is detected during product deployment it will be"
	echo "automatically executed giving the developer the opportunity to perform any automated"
	echo "installation or upgrade functions that might be n necessary."
	echo ""
	echo "For this product upgrade mechanism to execute scripts as an Administrator user you first"
	echo "need to setup that use. To do this, first download the OU-1042.bs script (link below); "
	echo "then login to the OU instance as an Administrator, open Admin -> Control Panel -> "
	echo "Server Administration -> Script. Select BeanShell in the language drop down, then paste "
	echo "the text of the script into the script area and 'Execute'."
	echo ""	
	echo "Script URL: https://openunderwriter.atlassian.net/secure/attachment/18654/OU-1042.bs"
	echo ""
	echo "The script will add a new 'upgradehandler' user to Liferay with Administrator rights which" 
	echo "the product upgrade mechanism will use when running upgrade scripts. The user is created "
	echo "with a randomly generated 128 bit password."
	echo ""
	read -p "Press any key to continue."
fi