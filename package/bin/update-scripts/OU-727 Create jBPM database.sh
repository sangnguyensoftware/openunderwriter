#!/bin/bash

[ -z "$OU_HOME" ] && echo "OU_HOME is not set" && exit
[ -z "$DB_USERNAME" ] && echo "DB_USERNAME is not set" && exit
[ -z "$DB_PASSWORD" ] && echo "DB_PASSWORD is not set" && exit
[ -z "$DB_HOST" ] && echo "DB_HOST is not set" && exit
[ -z "$DB_PORT" ] && echo "DB_PORT is not set" && exit

set +e
DB_EXISTS_CHECK="$(mysql -u $DB_USERNAME --password=$DB_PASSWORD --host=$DB_HOST --port=$DB_PORT -se 'USE ou_trunk_trunk_jbpm' 2>&1 | grep 'ERROR')"
set -e

if [ ! -z "$DB_EXISTS_CHECK" ]; then
    echo ""
    echo "OpenUnderwriter jBPM Database Setup"
    echo "==================================="
    echo "This update upgrades OpenUnderwriter to use the jBPM workflow engine. Please enter " 
    echo "the username and password of a MySQL user who has the necessary permissions to "
    echo "create databases to allow this script to create a jBPM database."
    echo ""
    
    read -p "Please enter your MySQL username (default: root): " ROOT_DB_USERNAME
    read -p "Please enter your MySQL password (leave blank for no password): " ROOT_DB_PASSWORD

    [ "x$ROOT_DB_USERNAME" = "x" ] && ROOT_DB_USERNAME="root"
    [ "x$ROOT_DB_PASSWORD" != "x" ] && PW_OPTION="--password=$ROOT_DB_PASSWORD"
    
    echo "Running database scripts..."
	
	mysql -u $ROOT_DB_USERNAME --password=$ROOT_DB_PASSWORD --host=$DB_HOST --port=$DB_PORT < $OU_HOME/lib/content/jbpm/Setup.sql 
fi
