#!/usr/bin/python
'''
Created on 22 Oct 2012

@author: epstvxj

delete_old_files.py : delete files that are older than <threshold> hour(s)
                      in folders listed in the file clean_list 

A cron job is set to call delete_old_files.py script every 30 minutes.

On Linux (RPM),
The cron job is set through the post-installation.sh script 
and removed by post-remove.sh during un-installation.

On Solaris (tar)
The cron job is set through the install.sh script 
and removed by uninstall.sh during un-installation.

'''

import os
from datetime import datetime
import logging
import traceback
import time

logging.basicConfig(format='%(asctime)-8s %(message)s', level=logging.DEBUG, datefmt='%m-%d %H:%M:%S')

def get_current_utc_time_in_seconds():
    return time.mktime(datetime.utcnow().timetuple())

def delete_old_files(threshold=1, clean_list=r'/opt/ericsson/dvtp_tool/etc/clean_list'):
    
    if clean_list is None or not os.path.exists(clean_list):
        logging.error('clean_list does not exist')
        return
    
    target_directory_list = []
    with open(clean_list) as clean_list_reader:
        for clean_dir in clean_list_reader:
            target_directory_list.append(clean_dir.strip())
    
    logging.info('delete files older than %.2f hour(s) in folders %s', threshold, target_directory_list)
    try:
        current_time = get_current_utc_time_in_seconds()
        logging.info('Started at %s', datetime.utcfromtimestamp(current_time))
        for target_directory in target_directory_list:
            if not os.path.exists(target_directory):
                continue
            if not target_directory.endswith(os.sep):
                target_directory += os.sep        
            number_of_file_to_process_in_current_directory = count_number_of_files(target_directory)
            if number_of_file_to_process_in_current_directory == 0:
                continue
            number_of_file_deleted_in_current_directory = 0
            for root, dirs, files in os.walk(target_directory, topdown=False):
                for name in files:
                    if 'bin.gz' in name or 'ebs' in name or '_staple_tcpta-partial_' in name or '_summary_' in name:
                        filepath = os.path.join(root, name)
                        file_creation_time = os.path.getctime(filepath)
                        seconds_passed = current_time-file_creation_time
                        if seconds_passed / 3600.0 > threshold:    
                            #logging.info('deleting file %s', os.path.join(root, name))   
                            number_of_file_deleted_in_current_directory += 1
                            os.remove(os.path.join(root, name))
            logging.info('%d file(s) to process in folder %s, %d file(s) deleted', 
                         number_of_file_to_process_in_current_directory, target_directory, number_of_file_deleted_in_current_directory)
        logging.info('Finished at %s', datetime.utcnow().strftime('%A, %d. %B %Y %H:%M:%S'))
        logging.info('---------------------------------------------------------------------------')
    except Exception as ex:
        logging.error('>>>>> %s', str(ex))
#        logging.exception(traceback.print_stack())
    
def count_number_of_files(target_directory):    
    total_count = 0
    for root, dirs, files in os.walk(target_directory, topdown=False):
        total_count += len(files)
    return total_count
    
if __name__ == '__main__':
    import sys
    if len(sys.argv) < 3:
        print('invalid number of command line arguments')
        sys.exit()
    try:
        delete_old_files(float(sys.argv[1]), sys.argv[2])
    except:
        traceback.print_exc(sys.stderr)