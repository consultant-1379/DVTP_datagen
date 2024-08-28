#!/usr/bin/bash
if [[ $EUID -ne 0 ]]; then
	echo "This script must be run as root" 1>&2
	exit 1
fi

INSTALLATION_FOLDER="/opt/ericsson/dvtp_tool"
ETC_BACKUP_LOCATION="/tmp/etc"
ETC_FOLDER=${INSTALLATION_FOLDER}"/etc"
UTILITY_FOLDER=${INSTALLATION_FOLDER}"/utility_scripts"
LOG_FOLDER="/var/log/dvtp_tool"
UNINSTALL_SCRIPT=${UTILITY_FOLDER}"/uninstall.py"

if [ -d ${INSTALLATION_FOLDER} ]; then
	
	echo "uninstall dvtp_tool"
	
	if [ -d ${ETC_BACKUP_LOCATION} ]; then
		echo "removing old etc backup files"
		rm -rf ${ETC_BACKUP_LOCATION}
	fi
	
	if [ -d ${INSTALLATION_FOLDER} ]; then
		echo "backup configuration files in ${INSTALLATION_FOLDER}/etc"
		cp -fr ${ETC_FOLDER} /tmp/			
	fi
	
	echo "remove datagen package"
	python ${UNINSTALL_SCRIPT}
	
	echo "create a temp file for the existing cron job"
	crontab -l > /tmp/cron_job.tmp
	
	echo "remove cron job from cron job list"
	sed -e '/delete_old_files.py/d' /tmp/cron_job.tmp > /tmp/cron_job.tmp.1
	rm -rf /tmp/cron_job.tmp
	chmod 755 /tmp/cron_job.tmp.1
	crontab /tmp/cron_job.tmp.1
	# remove temp file
	rm -f /tmp/cron_job.tmp.1
	crontab -l
	
	if [ -e /etc/init.d/datagen -a -L /etc/init.d/datagen ]; then
		echo "remove symlink"
		unlink /etc/init.d/datagen
	fi
	
	rm -rf ${LOG_FOLDER}
	cd ~
	rm -rf ${INSTALLATION_FOLDER}

fi