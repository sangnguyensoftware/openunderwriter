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

#| NAME
#|    run.sh - OpenUnderwriter run (and setup) script
#|
#| SYNOPSIS
#|    run.sh [--nodemo] [--nostart] [--startonly] [--debug] [--signals] [--help] 
#| 
#| DESCRIPTOIN
#|    The run.sh script is used to start the OpenUnderwriter server. When it is run for the first
#|    time it will initialise the system.
#|
#|    The initialisation steps prompt the user for details of the database server that OpenUnderwriter 
#|    is to use. It will then create the necessary databases and populate them with data. It will also
#|    optionally create demonstration policy and data warehouse information. 
#|
#|    --nostart
#|    Perform the initialisation steps (if not executed before) but do not start the server.
#|
#|    --startonly
#|    Perform the minimum steps necessary to start the server. Specifically: do not execute setup actions 
#|    and do not perform environment checks.
#|
#|    --nodemo
#|    Do not install demonstration data and remove demonstration components.
#|
#|    --signals
#|    Respect process signals. When this option is in effect a HUP signal will cause OpenUnderwriter 
#|    to reload, TERM will cause it to gracefully exit. 
#|
#|    --debug
#|    Output trace information (set -x) 
#|
#|    --help
#|    Display this help page.
#|

OPENUNDERWRITER_HOME=`dirname "$PWD/$0"`/..
BIN="$OPENUNDERWRITER_HOME/bin"
TMP="$OPENUNDERWRITER_HOME/tmp"
SETUP="$OPENUNDERWRITER_HOME/.setup"
LIB="$OPENUNDERWRITER_HOME/lib"
LIFERAY_HOME="$OPENUNDERWRITER_HOME/liferay-portal-@liferay.version@"
JBOSS_HOME="$LIFERAY_HOME/jboss-@jboss.version@"
PROGNAME=`basename $0`
OU_HOST=`hostname -I|sed 's: ::g'`

SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/com/ail/insurance/main/insurance.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/com/ail/core/main/core.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/com/liferay/portal/main/mysql.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/org/beanshell/main/bsh-2.0b5.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/org/apache/xalan/main/xalan-2.7.1.jbossorg-1.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/org/apache/xalan/main/serializer-2.7.1.jbossorg-1.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/org/apache/commons/jxpath/main/commons-jxpath-1.3.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$JBOSS_HOME/modules/org/apache/commons/io/main/commons-io-2.1.jar"
SETUP_CLASSPATH="$SETUP_CLASSPATH:$LIB/liquibase.jar"

[ "x$JAVA_HOME" != "x" ] && PATH=$JAVA_HOME/bin:$PATH

function help() {
    sed -n 's:^#|::p' $0
}

function usage() {
    echo "run.sh: $1"
    echo "usage: run.sh [--nodemo] [--nostart] [--startonly] [--debug] [--signals] [--help]"
}

for i in "$@"; do
    case $i in
       --signals) SIGNALS_OPT="$i"; shift;;
        --nodemo) NO_DEMO_OPT="$i"; shift;;
       --nostart) NO_START_OPT="$i"; shift;;
     --startonly) START_ONLY_OPT="$i"; shift;;
         --debug) DEBUG_OPT="$i"; shift;;
          --help) help; exit;;
               *) usage "$i not recognised"; exit 1;;
    esac
done
    
[ ! -z "$DEBUG_OPT" ] && set -x

mkdir -p $TMP

if [ ! -f "$SETUP" -a -z "$START_ONLY_OPT" ]; then
    
    echo ""
    echo "OpenUnderwriter Database Setup"
    echo "=============================="
    echo "The first time that you run OpenUnderwriter this script will create databases in" 
    echo "MySQL and populate them with content. It will also create an OpenUnderwriter database"
    echo "user. To do this successfully the script will need the username and password" 
    echo "of a user who has the necessary permissions."
    echo "This user will only be used to run the scripts. The OpenUnderwriter server itself"
    echo "uses the less privileged user created by the scripts."
    echo ""
    echo "You will not be prompted for these user details again."
    echo ""
    
    read -p "Please enter your MySQL host (default: localhost): " DB_HOST
    read -p "Please enter your MySQL username (default: root): " DB_USERNAME
    read -p "Please enter your MySQL password (leave blank for no password): " DB_PASSWORD

    [ "x$DB_HOST" = "x" ] && DB_HOST="localhost"
    [ "x$DB_USERNAME" = "x" ] && DB_USERNAME="root"
    [ "x$DB_PASSWORD" != "x" ] && PW_OPTION="--password=$DB_PASSWORD"
    
    [ "$DB_HOST" = "localhost" ] && DB_HOST="127.0.0.1"
        
    echo
    echo "Checking environment..."
    
    java -cp "$SETUP_CLASSPATH" bsh.Interpreter "$BIN/env-checker.bsh" "$DB_HOST" "$DB_USERNAME" "$DB_PASSWORD"
    [ "$?" = "1" ] && exit 1

    echo "Running database scripts..."
    
    cd "$LIB"
    if [ ! -z "$NO_DEMO_OPT" ]; then
        mv content/data-warehouse/Setup.sql content/data-warehouse/Setup.sql.safe
        mv content/data-warehouse/Teardown.sql content/data-warehouse/Teardown.sql.safe
        echo > content/data-warehouse/Setup.sql
        echo > content/data-warehouse/Teardown.sql
    fi
    if [ "$DB_HOST" != "localhost" ]; then
        find $LIB/content -name 'Setup.sql' -exec sed -i "s:@'localhost':@'$OU_HOST':g" {} \;
    fi
    mysql --host=$DB_HOST -u $DB_USERNAME $PW_OPTION < "./Setup.sql"
    [ "$?" = "1" ] && cd "$BIN" && echo "Failed to execute the MySQL database setup script." && exit 1

    if [ ! -z  "$NO_DEMO_OPT" ]; then
        rm $JBOSS_HOME/standalone/deployments/demo-product-loader-hook.war*
    else
        java -cp "$SETUP_CLASSPATH" com.ail.insurance.actuarial.DataGenerator \
            com.mysql.jdbc.Driver \
            jdbc:mysql://$DB_HOST/@dbname.dwh.prefix@_AIL_Base_DataSource_Master_Motor \
            "$DB_USERNAME" \
            "$DB_PASSWORD"
            
        java -cp "$SETUP_CLASSPATH" com.ail.insurance.policy.DataGenerator \
            com.mysql.jdbc.Driver \
            jdbc:mysql://$DB_HOST/@dbname.dwh.prefix@_AIL_Base_Report \
            "$DB_USERNAME" \
            "$DB_PASSWORD"

        cd $LIB/content/openunderwriter
        mysql --host=$DB_HOST -u $DB_USERNAME $PW_OPTION < ./TestData.sql
    fi

    if [ "$DB_HOST" != "127.0.0.1" ]; then
        LIFERAY_PROPERTY_FILE=$(find $OPENUNDERWRITER_HOME/liferay-portal* -maxdepth 1 -name 'portal-ext.properties')
        awk -F= '/jdbc.default.url/ {sub("localhost","'$DB_HOST'", $2)} {print $1"="$2;}' < $LIFERAY_PROPERTY_FILE > $LIFERAY_PROPERTY_FILE,
        mv $LIFERAY_PROPERTY_FILE, $LIFERAY_PROPERTY_FILE

        JBOSS_STANDALONE=$JBOSS_HOME/standalone/configuration/standalone.xml
        sed 's#<connection-url>jdbc:mysql://localhost#<connection-url>jdbc:mysql://'$DB_HOST'#' < $JBOSS_STANDALONE > $JBOSS_STANDALONE,
        mv $JBOSS_STANDALONE, $JBOSS_STANDALONE
    fi
    
    touch "$SETUP"
elif [ -z "$START_ONLY_OPT" ]; then 
    function eval_configuration_xpath {
        cat $OPENUNDERWRITER_HOME/liferay*/jboss*/standalone/configuration/standalone.xml | sed 's:xmlns=".*"::g' | xmllint --xpath "$1" -
    }

    DB_CONNECTION=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/connection-url/text()[1]')
    DB_USERNAME=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/security/user-name/text()[1]')
    DB_PASSWORD=$(eval_configuration_xpath '//datasource[@pool-name="PersistenceDS"]/security/password/text()[1]')
    DB_HOST=$(echo $DB_CONNECTION | awk -F/ '{split($3,e,":");if (e[1]=="localhost") print "127.0.0.1"; else print e[1];}')

    java -cp "$SETUP_CLASSPATH" bsh.Interpreter "$BIN/env-checker.bsh" "$DB_HOST" "$DB_USERNAME" "$DB_PASSWORD"
    [ "$?" = "1" ] && exit 1
fi 

echo "Checking for database updates..."
cd $BIN
java -cp "$SETUP_CLASSPATH" bsh.Interpreter "$BIN/liquibase.bsh" "$JBOSS_HOME" "$OPENUNDERWRITER_HOME"
[ "$?" = "1" ] && exit 1
          
[ ! -z "$NO_START_OPT" ] && exit

[ ! -z "$SIGNALS_OPT" ] && export LAUNCH_JBOSS_IN_BACKGROUND="yes"

cd "$JBOSS_HOME/bin"
./standalone.sh
