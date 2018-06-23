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
#|    setup.sh - OpenUnderwriter instance setup script.
#|
#| SYNOPSIS
#|    setup.sh --version=<target version> --host=<host name> --mysqlpassword=<password> [--mysqlhost=<host>] [--mysqluser=<username>] [--noservice] [--noapache] [--nomysqlserver] [--nomysql] [--nojava] [--nostart] [--nodemo] [--noapache] [--debug] [--inplace] [--help] 
#| 
#| DESCRIPTOIN
#|    OpenUnderwriter instance setup script. This script is provided in the hope that it will be helpful to anyone 
#|    installing OpenUnderwriter on a Unix system. The OpenUnderwriter team use it internally to setup demo servers
#|    in Amazon's EC2 cloud, and the settings are somewhat specific to that task. However, it attempts to cover most
#|    installation scenarios, and where it cannot be used directly it may form the basis of a setup script for 
#|    other environments.
#|
#|    The typical scenario is the installation of OpenUnderwriter on a new Linux server. The installation process
#|    covers: updating the server's patch level; installing (configuring and, in a limited way, hardening) mysql and 
#|    apache; downloading the OpenUnderwriter binary, unpacking it and configuring it for the local installation; 
#|    creating init services to start and stop OpenUnderwriter; and starting the server. Many of these steps are 
#|    optional and may be turned off by command line options.
#|
#|    --version=<targt version>
#|    The version of OpenUnderwriter to be installed.
#|
#|    --host=<host name>
#|    The hostname of the server to be used in Apache and Liferay configuration 
#|
#|    --mysqlhost=<hostname> 
#|    The hostname of the database server which will host OU's databases. This defaults to "localhost". If
#|    a host other than localhost is used, it is assumed that MySQL server is already installed on that host
#|    and is running.
#|
#|    --mysqluser=<username> 
#|    The username of a privileged user on MySQL which will be used to install OU database and configure users.
#|    This defaults to "root".   
#|
#|    --mysqlpassword=<password> 
#|    If MySQL is to be installed (with respect to the --nomysql option) it will be configured with the
#|    specified password. Regardless of the --nomysql option, this password will be used while configuring
#|    OpenUnderwriter to allow it to create the necessary databases and initial database content. 
#|
#|    --ouhome=<OU home directory>
#|    Optionally, specify the location into which OpenUnderwriter should be installed. By default, this is
#|    /opt/openunderwriter-community.
#|
#|    --noservice
#|    Optionally, do not create init services. As a result, OpenUnderwriter will also not be left running
#|    when the script exits.
#|
#|    --noapache
#|    Optionally, do not install Apache.
#|
#|    --nojava
#|    Optionally, do not install Java.
#|
#|    --nomysql
#|    Optionally, do not install any mysql components at all (neither the client or the server).
#|
#|    --nomysqlserver
#|    Optionally, do not install the mysql server (mysql client will be installed as the sretup process 
#|	  depends on it). This option would typically be used when the database is hosted on another server.
#|
#|    --nostart
#|    Optionally, do not start the OpenUnderwriter service once the installation is complete (implied by
#|    --noservice)
#|
#|    --nodemo
#|    Optionally, do not install OpenUnderwriter's demonstration products and themes.
#|
#|    --noswap
#|    Optionally, do not create swap space. By default the setup script will set aside swap space to
#|    improve performace on small/medium size servers.
#|
#|    --inplace
#|    The default installation of OpenUnderwriter creates a versioned directory containing the installation
#|    itself, and a maintains a link to it whose name will be consistent across updates. The --inplace
#|    option will ignore this convention and assume that OpenUnderwriter is simply installed in a folder
#|    whose name will remain consistent across updates. 
#|
#|    --debug
#|    Output trace information (set -x) 
#|
#|    --help
#|    Display this help page.
#|
#| USAGE
#|     Typical usage installing on a remote host:
#|
#|     scp setup.sh <user>@<host>:.
#|     ssh -t <user>@<host> chmod +x ./setup.sh
#|     ssh -t <user>@<host> sudo -s ./setup.sh --host=<instance IP/domain name> --mysqlpassword=<mysql root pw> --version=<OU version>
#|

function help {
	sed -n 's:^#|::p' $0
}

function usage {
	echo "setup.sh: $1"
	echo "usage: seutp.sh --version=<target version> --host=<host name> --mysqlpassword=<password> [--mysqlhost=<host>] [--mysqluser=<username>] [--noservice] [--noapache] [--nomysqlserver] [--nomysql] [--nojava] [--nostart] [--nodemo] [--noapache] [--debug] [--inplace] [--help]"
}

function setup {
	OU_INSTALL_DIR=/opt/openunderwriter
	
	for i in "$@"; do
		case $i in
                     --host=*) HOSTNAME="${i#*=}"; shift;;
                --mysqlhost=*) MYSQL_HOST="${i#*=}"; shift;;
                --mysqluser=*) MYSQL_ROOT_USER="${i#*=}"; shift;;
			--mysqlpassword=*) MYSQL_ROOT_PASSWORD="${i#*=}"; shift;;
                  --version=*) VERSION="${i#*=}"; shift;;
                   --ouhome=*) OU_INSTALL_DIR="${i#*=}"; shift;;
                     --nodemo) NO_DEMO_OPT="$i"; shift;;
					--nomysql) NO_MYSQL_OPT="$i"; shift;;
              --nomysqlserver) NO_MYSQL_SERVER_OPT="$i"; shift;;
                    --nostart) NO_START_OPT="$i"; shift;;
                     --noswap) NO_SWAP_OPT="$i"; shift;;
                  --noservice) NO_SERVICE_OPT="$i"; NO_START_OPT="yes"; shift;;
                     --nojava) NO_JAVA_OPT="$i"; shift;;
                   --noapache) NO_APACHE_OPT="$i"; shift;;
                    --inplace) IN_PLACE_OPT="$i"; shift;;
                      --debug) DEBUG_OPT="$i"; shift;;
                       --help) help; exit;;
                            *) usage "$i not recognised"; exit 1;;
		esac
	done
	
	[ -z "$VERSION" ] && usage "--version=<version number> was not specified. Use --help for more info." && exit 1
	[ -z "$MYSQL_ROOT_PASSWORD" ] && usage "--mysqlpassword=<password> was not specified. Use --help for more info." && exit 1
	[ -z "$HOSTNAME" ] && usage " --host=<host> was not specified. Use --help for more info." && exit 1
	
	OU_ARTIFACT_URL=http://www.openunderwritercommunity.org/nexus/service/local/repositories/releases/content/openunderwriter/openunderwriter-community/$VERSION/openunderwriter-community-$VERSION.zip
	
	if [ -z "$IN_PLACE_OPT" ]; then
		OU_HOME=$OU_INSTALL_DIR/openunderwriter-community
	else
		OU_HOME=$OU_INSTALL_DIR
	fi

	[ ! -z "$DEBUG_OPT" ] && set -x
}

function patch_os {
	yum -y update
} 

function setup_swap_space {
	[ ! -z "$NO_SWAP_OPT" ] && return

	dd if=/dev/zero of=/swapfile bs=1M count=1024
	chmod 0600 /swapfile
	mkswap /swapfile
	swapon /swapfile
	sed -i '$a /swapfile swap swap defaults 0 0' /etc/fstab
}

function install_mysql_server {
	[ ! -z "$NO_MYSQL_OPT$NO_MYSQL_SERVER_OPT" ] && return

	yum -y install mysql57-server
	sed -i '/\[mysqld\]/a lower_case_table_names = 1' /etc/my.cnf 
	sed -i '/\[mysqld\]/a max_allowed_packet=16M' /etc/my.cnf 
	sed -i '/\[mysqld\]/a sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' /etc/my.cnf
	
	chkconfig mysqld on
	service mysqld start
	
	mysql -u root -e "DROP USER ''@'localhost'"
	mysql -u root -e "DROP USER ''@'$(hostname)'"
	mysql -u root -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1')"
	mysql -u root -e "DROP DATABASE test"
	mysql -u root -e "SET PASSWORD = '$MYSQL_ROOT_PASSWORD'; FLUSH PRIVILEGES"
}

function install_mysql_client {
	[ ! -z "$NO_MYSQL_OPT" ] && return

	yum -y install mysql57
}

function install_java {
	[ ! -z "$NO_JAVA_OPT" ] && return

	yum -y install java-1.8.0-openjdk-devel
	yum -y remove java-1.7.0-openjdk
}

function install_apache {
	[ ! -z "$NO_APACHE_OPT" ] && return

	yum install -y httpd24 
	yum install -y mod24_proxy_html
	yum install -y mod24_ssl
	chkconfig httpd on
	groupadd www
	usermod -a -G www ec2-user
	chown -R root:www /var/www
	chmod 2775 /var/www
	find /var/www -type d -exec sudo chmod 2775 {} +
	find /var/www -type f -exec sudo chmod 0664 {} +
	tee /etc/httpd/conf.d/openunderwriter.conf >/dev/null <<END
<VirtualHost *:80>
    ServerAdmin contact@openunderwriter.com

    ServerName $HOSTNAME 
    ServerAlias $HOSTNAME 

    AddOutputFilterByType SUBSTITUTE application/json
    Substitute "s|http://localhost:8080/|http://$HOSTNAME/|n"
    
    ProxyPass / http://localhost:8080/
    ProxyPassReverse / http://localhost:8080/

    ProxyHTMLURLMap http://localhost:8080/ http://$HOSTNAME/
</VirtualHost>
END
	sed -i 's:^:#:' /etc/httpd/conf.d/welcome.conf
	sed -i "s/#ServerName www.example.com:80/ServerName $HOSTNAME:80/" /etc/httpd/conf/httpd.conf
	service httpd start
}

function install_openunderwriter {
	groupadd openunderwriter
	useradd -g openunderwriter --system openunderwriter
	
	curl $OU_ARTIFACT_URL > /tmp/openunderwriter.zip
	unzip -q /tmp/openunderwriter.zip -d /tmp
	rm /tmp/openunderwriter.zip
	if [ -z "$IN_PLACE_OPT" ]; then
		mkdir $OU_INSTALL_DIR
		mv /tmp/openunderwriter-community* $OU_INSTALL_DIR
		ln -s $OU_INSTALL_DIR/* $OU_INSTALL_DIR/openunderwriter-community
	else 
		[ ! -d "$OU_INSTALL_DIR" ] && mkdir $(dirname $OU_INSTALL_DIR)
		mv /tmp/openunderwriter-community* $OU_INSTALL_DIR
	fi
}

function configure_liferay {
	PORTAL_EXT=$OU_HOME/liferay-portal-6.2-ce-ga6/portal-ext.properties
	BIRT_WAR=$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/deployments/birt.war
	sed -i 's/javascript.fast.load=false/javascript.fast.load=true/' $PORTAL_EXT
	sed -i '$a last.modified.check=true' $PORTAL_EXT
	sed -i '$a theme.css.fast.load=true' $PORTAL_EXT
	sed -i '$a web.server.host='$HOSTNAME $PORTAL_EXT
	sed -i '$a web.server.http.port=80' $PORTAL_EXT
	jar -xf $BIRT_WAR WEB-INF/viewer.properties
	sed -i 's@#base_url=http://127.0.0.1:8080@base_url=http://'$HOSTNAME'@' WEB-INF/viewer.properties
	jar -uf $BIRT_WAR WEB-INF/viewer.properties
	chown -R -L openunderwriter.openunderwriter $OU_HOME
}

function configure_openunderwriter {
	su openunderwriter -c 'cd '$OU_HOME'/bin; printf "'$MYSQL_HOST'\n'$MYSQL_ROOT_USER'\n'$MYSQL_ROOT_PASSWORD'\n" | ./run.sh --nostart '$NO_DEMO_OPT
}

function configure_init_script {
	[ ! -z "$NO_SERVICE_OPT" ] && return

	tee /etc/init.d/openunderwriter >/dev/null <<END
### BEGIN INIT INFO
# Provides:          openunderwriter
# Required-Start:    \$mysqld \$network \$syslog
# Required-Stop:     \$mysqld \$network \$syslog
# Default-Start:     3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start/Stop OpenUnderwriter 3
### END INIT INFO
#
LAUNCH_JBOSS_IN_BACKGROUND=1
OU_HOME=$OU_HOME
OU_BIN=\$OU_HOME/bin
JBOSS_BIN=\$OU_HOME/liferay-portal-6.2-ce-ga6/jboss-7.1.1/bin

case "\$1" in
    start)
        echo "Starting OpenUnderwriter 3"
        sudo su openunderwriter -c 'cd '\$OU_BIN'; ./run.sh >/dev/null &'
        printf "\nServer starting...\n";;
    stop)
        echo "Stopping OpenUnderwriter 3"
        sudo su openunderwriter -c 'cd '\$JBOSS_BIN'; ./jboss-cli.sh --connect command=:shutdown';;
    *)
        echo "Usage: /etc/init.d/jboss {start|stop}"
        exit 1;;
esac

exit 0
END
	chmod 755 /etc/init.d/openunderwriter
	chkconfig --add openunderwriter
	sed -i '$a 127.0.0.1 '$(uname -n) /etc/hosts
}

function start_service {
	[ ! -z "$NO_START_OPT" ] && return
	service openunderwriter start
}

function main {
	setup $*
	patch_os
	setup_swap_space
	install_mysql_server
	install_mysql_client	
	install_java	
	install_apache
	install_openunderwriter
	configure_liferay
	configure_openunderwriter 
	configure_init_script
	start_service
}

main $*
