INSTALLATION_FOLDER="/opt/ericsson/dvtp_tool"
UTILITY_FOLDER=${INSTALLATION_FOLDER}"/utility_scripts"
ETC_FOLDER=${INSTALLATION_FOLDER}/etc

if [ -f "${INSTALLATION_FOLDER}/setup.py" ]; then
	echo "install python datagen package"	
	SETUP_SCRIPT=${INSTALLATION_FOLDER}"/setup.py"
	python ${SETUP_SCRIPT} install
	
	echo "clean installation files"
	
	BUILD_DIR="${INSTALLATION_FOLDER}/build"
	echo "remove $BUILD_DIR folder"
	rm -fr $BUILD_DIR
	
	PACKAGE_DIR="${INSTALLATION_FOLDER}/py_script"
	echo "remove $PACKAGE_DIR folder"
	rm -fr $PACKAGE_DIR	

	echo "remove setup.py file"
	rm -fr "${INSTALLATION_FOLDER}/setup.py"
			
	echo "check whether cron job already exists"
	CRONTAB_JOB=`crontab -l | grep delete_old_files.py`
	echo "cron job does not exist"
	if [ -f '/tmp/cron_job.backup' ]; then
		echo "remove old cron_job.backup file"
		rm -f '/tmp/cron_job.backup'
	fi		
	echo "create a backup file for the existing cron job"
	crontab -l > /tmp/cron_job.backup
	echo "create a temp file for the existing cron job"
	crontab -l > /tmp/cron_job.tmp
	
	sed -e '/delete_old_files.py/d' /tmp/cron_job.tmp > /tmp/cron_job.tmp.1
	rm -rf /tmp/cron_job.tmp
	
	echo "0,30 * * * * python ${UTILITY_FOLDER}/delete_old_files.py 1 ${ETC_FOLDER}/clean_list >> /var/log/dvtp_tool/delete_old_files.log 2>&1" >> /tmp/cron_job.tmp.1
	chmod 755 /tmp/cron_job.tmp.1
	crontab /tmp/cron_job.tmp.1
	# remove temp file
	rm -f /tmp/cron_job.tmp.1
	crontab -l
	
	if [ -e "/usr/bin/delete_old_files" -a -L "/usr/bin/delete_old_files" ]; then
		echo "remove existing symlink to delete_old_files.py"
		unlink /usr/bin/delete_old_files
	fi
	
	# Create a symlink to delete_old_files.py in /usr/bin
	echo "create a symlink /etc/init.d/datagen"
	ln -s ${UTILITY_FOLDER}/delete_old_files.py /usr/bin/delete_old_files
	
	if [ -d /var/log/dvtp_tool ]; then
		rm -rf /var/log/dvtp_tool
	fi
	mkdir /var/log/dvtp_tool
	chmod -R 755 /var/log/dvtp_tool
	chown -R dcuser:dc5000 /var/log/dvtp_tool
	
	if [ -e /etc/init.d/datagen -a -L /etc/init.d/datagen ]; then
		echo "remove existing symlink"
		unlink /etc/init.d/datagen
	fi
	
	# Add soft link for the datagen start-stop init.d script
	echo "create a symlink /etc/init.d/datagen"
	ln -s ${UTILITY_FOLDER}/datagen /etc/init.d
else
	echo "missing steup.py file"
fi