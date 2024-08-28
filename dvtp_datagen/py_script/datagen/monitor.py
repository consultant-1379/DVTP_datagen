'''
Created on 12 Sep 2012

@author: epstvxj
'''

from ConfigParser import ConfigParser
from copy import deepcopy
from datagen.shared import metrics, datagen_logging
from datagen.shared.constants import STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING, \
    STAPLE_TCPTA_PARTIAL_FILENAME_EXT, CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING, \
    CAPTOOL_SUMMARY_FILENAME_EXT
from datagen.shared.metrics import log_classification_events_count, \
    log_tcpta_partial_events_count, log_ggsn_events_count, log_sgeh_events_count
from datagen.shared.utility import get_rop_session_through_system_time, \
    get_ggsn_filename_search_pattern, get_session_str_representation, \
    datetimeToString, get_sleep_time_in_sec, \
    get_rop_session_through_system_time_with_diff, get_sgeh_filename_search_pattern, \
    get_tcp_partial_search_pattern, get_classification_search_pattern, ensure_dir, \
    get_maps_from_monitor_props, check_full_rop_exists, \
    get_current_utc_time_in_seconds, get_timstamp_in_sec_of_datetime
from datetime import datetime, timedelta
from multiprocessing.process import Process
from settings import datagen_logger
from shutil import move, copy
from threading import Thread
import glob
import os
import subprocess
import sys
import time

def dest_folder_check(dest_directory_location):
    if not os.path.exists(dest_directory_location):
        datagen_logger.warning('folder: %s does not exist', dest_directory_location)
        return False
        
    if not os.path.isdir(dest_directory_location):
        datagen_logger.error('%s is not a directory', dest_directory_location)
        sys.exit()
    
    return True    

def job_handler(copy_job_list, move_job_list, target_session):
    
    number_of_copy_jobs = len(copy_job_list)
    datagen_logger.info('%d number of copy jobs created for session %s', 
                        number_of_copy_jobs, get_session_str_representation(target_session))
    
    number_of_move_jobs = len(move_job_list)
    datagen_logger.info('%d number of move jobs created for session %s', 
                        number_of_move_jobs, get_session_str_representation(target_session))  
             
    if number_of_copy_jobs > 0:
        start_time = get_current_utc_time_in_seconds()    
        for job in copy_job_list:
            job.start()
        for job in copy_job_list:
            job.join()
        end_time = get_current_utc_time_in_seconds()            
        datagen_logger.info('file copy finished with in %f minutes, %d copy jobs', 
                            (end_time-start_time)/60.0, number_of_copy_jobs)

     
    if number_of_move_jobs > 0:
        start_time = get_current_utc_time_in_seconds()  
        for job in move_job_list:
            job.start()
        for job in move_job_list:
            job.join()
        end_time = get_current_utc_time_in_seconds()            
        datagen_logger.info('file move finished with in %f minutes, %d move jobs', 
                            (end_time-start_time)/60.0, number_of_move_jobs)

def log_copy_status(has_rop_checker, session_in_string, is_full_rop, number_of_attempts, number_of_files_found, directory, number_of_files_expected=None):
    
    if number_of_files_expected is None:
        datagen_logger.trace('rop_checker [%r], session [%s], is_full_rop [%r], number of attempts [%d], %d files found in directory [%s]', 
                             has_rop_checker, session_in_string, is_full_rop, number_of_attempts, number_of_files_found, directory)
    else:
        datagen_logger.trace('rop_checker [%r], session [%s], is_full_rop [%r], number of attempts [%d], %d files found in directory [%s], %d required', 
                             has_rop_checker, session_in_string, is_full_rop, number_of_attempts, number_of_files_found, directory, number_of_files_expected)

def copy_or_move(executor, options, target_search_pattern, process_type, target_session, rop_time_period, 
                 time_diff=0, rop_checker_function_ref=None, event_count_function_ref=None):
    try:
        if options is not None and len(options) > 0:
            
            if datagen_logger.isTraceEnabled():                
                for option in options:
                    for item in option.items():
                        datagen_logger.trace('[%s] option: key [%s], value [%s]', process_type, item[0], item[1])
            
            for option in options:
                dest_directory = option['dest_directory']
  
                if not dest_directory.endswith(os.sep):
                    dest_directory = ''.join([dest_directory, os.sep])
                ensure_dir(dest_directory)
                
                name_prefix = option['name_prefix']
                generated_file_location = option['generated_file_location']
                
                generated_files_path = ''.join([generated_file_location, target_search_pattern])
                        
                datagen_logger.debug('search for %s in folder %s', generated_files_path, dest_directory)
                
                generated_files = glob.glob(generated_files_path)
                
                number_of_attempts = 1
                
                current_time = datetime.utcnow() + timedelta(hours=time_diff)
                
                is_full_rop = False
                
                number_of_files_found = len(generated_files)
                if rop_checker_function_ref is not None:
                    is_full_rop, number_of_files_required = rop_checker_function_ref(generated_files)
                    if datagen_logger.isTraceEnabled():
                        log_copy_status(rop_checker_function_ref != None, get_session_str_representation(target_session), is_full_rop, 
                                        number_of_attempts, number_of_files_found, generated_file_location, number_of_files_required)
                    
                else:
                    is_full_rop = number_of_files_found > 0
                    if datagen_logger.isTraceEnabled():
                        log_copy_status(rop_checker_function_ref != None, get_session_str_representation(target_session), is_full_rop, 
                                        number_of_attempts, number_of_files_found, generated_file_location)
                
                if (current_time - target_session[1]).seconds / 60 < 2 * rop_time_period:         
                    datagen_logger.trace('enable multiple attempts for %s option of session %s', 
                                         process_type, get_session_str_representation(target_session))
                    
                    # we need more wait time for classification files, roughly five rops time
                    if executor == ClassificationOutputMonitor.__class__.__name__:
                        total_wait_for = get_sleep_time_in_sec(datetime.utcnow(), rop_time_period) + 5 * rop_time_period * 60
                    else:
                    # wait for roughly two rops time
                        total_wait_for = get_sleep_time_in_sec(datetime.utcnow(), rop_time_period) + rop_time_period * 60               
                    
                    while total_wait_for > 0 and not is_full_rop:
                        time.sleep(10)                    
                        number_of_attempts += 1
                        total_wait_for -= 10                
                        start_time = get_current_utc_time_in_seconds()
                        generated_files = glob.glob(generated_files_path)
                        
                        number_of_files_found = len(generated_files)
                        
                        if datagen_logger.isTraceEnabled():
                            datagen_logger.trace('%d files found at %d attempts for session %s', 
                                                 number_of_files_found, number_of_attempts, get_session_str_representation(target_session))
                            
                        if rop_checker_function_ref is not None:                            
                            is_full_rop, number_of_files_required = rop_checker_function_ref(generated_files)
                            log_copy_status(rop_checker_function_ref != None, get_session_str_representation(target_session), is_full_rop, 
                                            number_of_attempts, number_of_files_found, generated_file_location, number_of_files_required)
                        else:
                            if len(generated_files) == 0:
                                is_full_rop = False
                            else:
                                is_full_rop = True
                            log_copy_status(rop_checker_function_ref != None, get_session_str_representation(target_session), is_full_rop, 
                                            number_of_attempts, number_of_files_found, generated_file_location)
                                
                        end_time = get_current_utc_time_in_seconds()
                        total_wait_for -= (end_time-start_time)

                else:
                    datagen_logger.trace('disable multiple attempts for %s option of session %s', 
                                         process_type, get_session_str_representation(target_session))  
                
                if not is_full_rop:
                    datagen_logger.warning('[%s] %d number of attempt(s) failed, no generated files found using the search pattern %s for session %s', 
                                           process_type, number_of_attempts, generated_files_path, get_session_str_representation(target_session))  
                else:
                    datagen_logger.info('[%s] succeed within %d attempt(s), find files using the search pattern %s for session %s', 
                                        process_type, number_of_attempts, generated_files_path, get_session_str_representation(target_session))     
                    datagen_logger.info('%d file(s) matching the search pattern %s in folder %s', len(generated_files), target_search_pattern, generated_file_location)  
                    
                    # generates event count reports, i.e., event count for each type of event as well as the total number
                        # of events in the current rop                         
                    if event_count_function_ref and datagen_logger.isTraceEnabled():
                        try:
                            event_count_function_ref(generated_files, datagen_logger, datagen_logging.TRACE)
                        except:
                            # ignore the error for the moment
                            pass
                                              
                    for generated_file in generated_files:    
                                            
                        index = generated_file.rfind(os.sep)+1              
                        dest = ''.join([dest_directory, name_prefix, generated_file[index:]])
                        datagen_logger.info('%s, file %s found in session %s ', executor, generated_file, 
                                         get_session_str_representation(target_session))
                                    
                        if datagen_logger.isTraceEnabled():
                            metrics.log_statistic_info(datagen_logger, datagen_logging.TRACE, 
                                                       'file %s, size %.4f MB' % (generated_file, os.path.getsize(generated_file)/1024.0/1024.0))
                                                                    
                        if 'copy' == process_type: 
                            datagen_logger.trace('copy file %s to %s', generated_file, dest) 
                            if os.path.exists(generated_file):                                
                                copy(generated_file, dest)                                    
                        else:
                            datagen_logger.trace('move file %s to %s', generated_file, dest)
                            if os.path.exists(generated_file):
                                move(generated_file, dest)
        else:
            datagen_logger.warning('%s option is none', process_type)
    except:
        datagen_logger.exception(sys.stderr) 

class MZSgehOutputMonitor(Process):
    
    def __init__(self, options):
        Process.__init__(self)
        configuration_filename = 'sgeh_monitor.props'
        
        if os.path.exists(options['etc']+os.sep+configuration_filename):
            sgeh_monitor_config = ConfigParser()
            sgeh_monitor_config.readfp(open(options['etc']+os.sep+configuration_filename))
            
            self.__options = dict()                        
            self.__options['rop_time_period']         = sgeh_monitor_config.getint('basic', 'rop_time_period')
            self.__options['ignore_session_time']     = sgeh_monitor_config.getboolean('basic', 'ignore_session_time')
            self.__options['nowait']                  = options['nowait']
            
            return_values = get_maps_from_monitor_props(sgeh_monitor_config)            
            self.__options['dest_directory_list_map']     = return_values[0]
            self.__options['generated_file_location_map'] = return_values[1]            
            self.__options['option_map']                  = return_values[2]            
            self.__options['name_prefix_map']             = return_values[3]
                                    
            datagen_logger.trace('%s options: %s', self.__class__.__name__, self.__options)

            self.__shutdown      = False
            
        else:
            datagen_logger.error('%s file does not exist in folder %s', configuration_filename, options['etc'])        
    
    def terminate(self):
        self.__shutdown = True                  
        subprocess.Popen(['pkill', '-P', str(self.pid)]).wait()
        subprocess.Popen(['kill', str(self.pid)]).wait()
                
    def run(self):
        
        '''
         The nowait attribute is used for testing purpose only.
         It is not defined in the property file.
         If one would like to use this attribute, then it has to be manually added into the option dictionary object passed
         to the __init__ constructor
        '''
        nowait = bool(self.__options['nowait']) if self.__options.has_key('nowait') else False
                
        self.__rop_time_period = int(self.__options['rop_time_period'])
        self.__ignore_session_time = bool(self.__options['ignore_session_time']) if self.__options.has_key('ignore_session_time') else False
        
        if not nowait:
            sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period) + self.__rop_time_period * 60
            datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
            time.sleep(sleeptime)
            
        #TODO 8 should be configurable
        self.__target_session = get_rop_session_through_system_time_with_diff(self.__rop_time_period, 8, 2) if not self.__ignore_session_time else None
        try: 
            while True and not self.__shutdown:                 
                start_time = get_current_utc_time_in_seconds()
                #TODO 8 should be configurable
                start_time_with_timediff = start_time + 8 * 3600                
                # always copies files that are 2xrop_time_period before, therefore need to calculate time delta between current time and 
                # session start time                  
                diff_in_minutes = (start_time_with_timediff-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%s starts at utc time %s', self.__class__.__name__, datetimeToString(datetime.utcfromtimestamp(start_time)))
                
                number_of_consecutive_copy_process = 0                
                while diff_in_minutes >= 2 * self.__rop_time_period:
                    datagen_logger.trace('>>>>>>> start_time_with_timediff: %s, session start time: %s', datetimeToString(datetime.utcfromtimestamp(start_time_with_timediff)), self.__target_session[0])
                    number_of_consecutive_copy_process += 1
                    self.__monitor_mz_output()
                    diff_in_minutes = (start_time_with_timediff-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%d consecutive copy and move process(es) executed', number_of_consecutive_copy_process)
                end_time = get_current_utc_time_in_seconds()
                
                minute_spend = (end_time - start_time) / 60
                if not nowait and minute_spend < self.__rop_time_period:
                    sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    if sleeptime == 60 * self.__rop_time_period:
                        time.sleep(5)
                        sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
                    time.sleep(sleeptime)
                else:
                    datagen_logger.debug('%s kick off without sleep', self.__class__.__name__)
                    
                if nowait:
                    break
                                
        except:
            datagen_logger.exception(sys.stderr)
            sys.exit()

    def __monitor_mz_output(self):        
        
        generated_file_location_map = self.__options['generated_file_location_map']
        dest_directory_list_map     = self.__options['dest_directory_list_map']
        name_prefix_map             = self.__options['name_prefix_map']
        option_map                  = self.__options['option_map']
    
        target_search_pattern = None
        
        if self.__target_session:
            target_search_pattern = get_sgeh_filename_search_pattern(self.__target_session)
        else:
            target_search_pattern = 'A*.*-*.*_*_ebs.*'
        
        datagen_logger.info('looking for generated sgeh files of session %s', get_session_str_representation(self.__target_session))
        
        generated_file_location_keys = generated_file_location_map.keys()
        generated_file_location_keys.sort()
        
        copy_job_list = []
        move_job_list = []
        
        for generated_file_location_key in generated_file_location_keys:
            generated_file_location = generated_file_location_map.get(generated_file_location_key)
            
            if not dest_folder_check(generated_file_location):                
                continue
            
            if not generated_file_location.endswith(os.sep):
                generated_file_location += os.sep
            
            dest_directory_map = dest_directory_list_map.get(generated_file_location_key)
            dest_directory_options = dest_directory_map.keys()
            for dest_directory_option in dest_directory_options:
                name_prefix = ''
                if name_prefix_map.has_key(dest_directory_option):  
                    name_prefix = name_prefix_map.get(dest_directory_option) 
                
                if option_map.has_key(dest_directory_option):
                    if 'copy' == option_map.get(dest_directory_option):
                        copy_job_list.append(Thread(target=copy_or_move, 
                                                    name='copy',
                                                    args=(self.__class__.__name__, 
                                                          ({'generated_file_location':generated_file_location, 
                                                            'name_prefix':name_prefix, 
                                                            'dest_directory':dest_directory_map.get(dest_directory_option)},), 
                                                          target_search_pattern, 'copy', deepcopy(self.__target_session),
                                                          self.__rop_time_period, 8, None, log_sgeh_events_count)))
                        
                    elif 'move' == option_map.get(dest_directory_option):
                        move_job_list.append(Thread(target=copy_or_move,
                                                    name='move', 
                                                    args=(self.__class__.__name__, 
                                                          ({'generated_file_location':generated_file_location, 
                                                            'name_prefix':name_prefix, 
                                                            'dest_directory':dest_directory_map.get(dest_directory_option)},), 
                                                          target_search_pattern, 'move', deepcopy(self.__target_session),
                                                          self.__rop_time_period, 8, None, log_sgeh_events_count)))                  
                
        p = Thread(target=job_handler, 
                    name='SgehCopyAndMoveHandlerProcess', 
                    args=(copy_job_list, move_job_list, 
                          deepcopy(self.__target_session)))
        p.start()
                
        if self.__target_session: 
            self.__target_session = [self.__target_session[0] + timedelta(minutes=self.__rop_time_period),
                                     self.__target_session[1] + timedelta(minutes=self.__rop_time_period)]
        
            
class MZGgsnOutputMonitor(Process):
    
    def __init__(self, options):
        Process.__init__(self)
        
        configuration_filename = 'ggsn_monitor.props'        
        if os.path.exists(options['etc']+os.sep+configuration_filename):
            ggsn_monitor_config = ConfigParser()
            ggsn_monitor_config.readfp(open(options['etc']+os.sep+configuration_filename))
            
            self.__options = dict()      
            self.__options['rop_time_period']         = ggsn_monitor_config.getint('basic', 'rop_time_period')
            self.__options['ignore_session_time']     = ggsn_monitor_config.getboolean('basic', 'ignore_session_time')
            self.__options['nowait']                  = options['nowait']
            
            return_values = get_maps_from_monitor_props(ggsn_monitor_config)            
            self.__options['dest_directory_list_map']     = return_values[0]
            self.__options['generated_file_location_map'] = return_values[1]            
            self.__options['option_map']                  = return_values[2]            
            self.__options['name_prefix_map']             = return_values[3]
            
            datagen_logger.trace('%s options: %s', self.__class__.__name__, self.__options)
            
            self.__shutdown      = False
            
        else:
            datagen_logger.error('%s file does not exist in folder %s', configuration_filename, options['etc'])

    def terminate(self):
        
        self.__shutdown = True
                    
        subprocess.Popen(['pkill', '-P', str(self.pid)]).wait()
        subprocess.Popen(['kill', str(self.pid)]).wait()
    
    def run(self):
        
        '''
         The nowait attribute is used for testing purpose only.
         It is not defined in the property file.
         If one would like to use this attribute, then it has to be manually added into the option dictionary object passed
         to the __init__ constructor
        '''
        nowait = bool(self.__options['nowait']) if self.__options.has_key('nowait') else False
                
        self.__rop_time_period = int(self.__options['rop_time_period'])
        self.__ignore_session_time = bool(self.__options['ignore_session_time']) if self.__options.has_key('ignore_session_time') else False
                
        if not nowait: 
            sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period) + self.__rop_time_period * 60
            datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
            time.sleep(sleeptime)

        self.__target_session = get_rop_session_through_system_time(self.__rop_time_period, 2) if not self.__ignore_session_time else None
        try:                    
            while True and not self.__shutdown:                
                start_time = get_current_utc_time_in_seconds()
                # always copies files that are 2xrop_time_period before, therefore need to calculate time delta between current time and 
                # session start time                  
                diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0                             
                datagen_logger.info('%s starts at utc time %s', self.__class__.__name__, datetimeToString(datetime.utcfromtimestamp(start_time)))
                number_of_consecutive_copy_process = 0
                while diff_in_minutes >= 2 * self.__rop_time_period:
                    datagen_logger.trace('>>>>>>> start_time: %s, session start time: %s', datetimeToString(datetime.utcfromtimestamp(start_time)), self.__target_session[0])
                    number_of_consecutive_copy_process += 1
                    self.__monitor_mz_output()
                    diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%d consecutive copy and move process(es) executed', number_of_consecutive_copy_process)
                end_time = get_current_utc_time_in_seconds()
                
                minute_spend = (end_time - start_time) / 60
                if not nowait and minute_spend < self.__rop_time_period:
                    sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    if sleeptime == 60 * self.__rop_time_period:
                        time.sleep(5)
                        sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
                    time.sleep(sleeptime)
                else:
                    datagen_logger.debug('%s kick off without sleep', self.__class__.__name__)
                if nowait:
                    break
                
                                
        except:
            datagen_logger.exception(sys.stderr)
            sys.exit()
           
    def __monitor_mz_output(self):        
        
        generated_file_location_map = self.__options['generated_file_location_map']
        dest_directory_list_map     = self.__options['dest_directory_list_map']
        name_prefix_map             = self.__options['name_prefix_map']
        option_map                  = self.__options['option_map']
        
        target_search_pattern = None
        
        if self.__target_session:
            target_search_pattern = get_ggsn_filename_search_pattern(self.__target_session)
        else:
            target_search_pattern = '*A*.*_SubNetwork=RNC*,MeContext=RNC*_rnc_ggsnfile_Mp*.bin.gz*'
        
        datagen_logger.info('looking for generated ggsn files of session %s', get_session_str_representation(self.__target_session))
        
        generated_file_location_keys = generated_file_location_map.keys()
        generated_file_location_keys.sort()
        
        copy_job_list = []
        move_job_list = []

        for generated_file_location_key in generated_file_location_keys:
            generated_file_location = generated_file_location_map.get(generated_file_location_key)
            
            if not dest_folder_check(generated_file_location):                
                continue
            
            if not generated_file_location.endswith(os.sep):
                generated_file_location += os.sep
            
            dest_directory_map = dest_directory_list_map.get(generated_file_location_key)
            dest_directory_options = dest_directory_map.keys()
            for dest_directory_option in dest_directory_options:
                name_prefix = ''
                if name_prefix_map.has_key(dest_directory_option):  
                    name_prefix = name_prefix_map.get(dest_directory_option) 
                if option_map.has_key(dest_directory_option):
                    if 'copy' == option_map.get(dest_directory_option):
                        copy_job_list.append(Thread(target=copy_or_move, 
                                                    args=(self.__class__.__name__, 
                                                          ({'generated_file_location':generated_file_location, 
                                                            'name_prefix':name_prefix, 
                                                            'dest_directory':dest_directory_map.get(dest_directory_option)},), 
                                                          target_search_pattern, 'copy', deepcopy(self.__target_session),
                                                          self.__rop_time_period, 0, None, log_ggsn_events_count)))
                        
                    if 'move' == option_map.get(dest_directory_option):
                        move_job_list.append(Thread(target=copy_or_move, 
                                                    args=(self.__class__.__name__, 
                                                          ({'generated_file_location':generated_file_location, 
                                                            'name_prefix':name_prefix, 
                                                            'dest_directory':dest_directory_map.get(dest_directory_option)},), 
                                                          target_search_pattern, 'move', deepcopy(self.__target_session),
                                                          self.__rop_time_period, 0, None, log_ggsn_events_count)))
        
        p = Thread(target=job_handler, 
                    name='GgsnCopyAndMoveHandlerProcess', 
                    args=(copy_job_list, move_job_list, deepcopy(self.__target_session)))
        
        p.start()
        
        if self.__target_session:
            self.__target_session = [self.__target_session[0] + timedelta(minutes=self.__rop_time_period),
                                     self.__target_session[1] + timedelta(minutes=self.__rop_time_period)]

class TcpPartialOutputMonitor(Process):
    
    def __init__(self, options):        
        Process.__init__(self)
        configuration_filename = 'tcp_partial_monitor.props'
        
        if os.path.exists(options['etc']+os.sep+configuration_filename):
            tcp_partial_monitor_config = ConfigParser()
            tcp_partial_monitor_config.readfp(open(options['etc']+os.sep+configuration_filename))
            
            self.__options = dict()
            self.__options['rop_time_period']         = tcp_partial_monitor_config.getint('basic', 'rop_time_period')
            self.__options['ignore_session_time']     = tcp_partial_monitor_config.getboolean('basic', 'ignore_session_time')
            
            rnc_mcc_mnc_lac_map = {}
            if not tcp_partial_monitor_config.has_section('rnc_mcc_mnc_lac_map'):
                datagen_logger.error('rnc_mcc_mnc_lac_map section is not defined in the props file')
                sys.exit()
            rncs = tcp_partial_monitor_config.options('rnc_mcc_mnc_lac_map')
            for rnc in rncs:
                rnc_mcc_mnc_lac_map[rnc] = tcp_partial_monitor_config.get('rnc_mcc_mnc_lac_map', rnc)
                        
            self.__options['rnc_mcc_mnc_lac_map']     = rnc_mcc_mnc_lac_map                                                                                                   
            self.__options['nowait']                  = options['nowait']
            
            return_values = get_maps_from_monitor_props(tcp_partial_monitor_config)            
            self.__options['dest_directory_list_map']     = return_values[0]
            self.__options['generated_file_location_map'] = return_values[1]            
            self.__options['option_map']             = return_values[2]            
            self.__options['name_prefix_map']             = return_values[3]
                        
            datagen_logger.trace('%s options: %s', self.__class__.__name__, self.__options)
            
            self.__copy_job_list = []
            self.__move_job_list = []
            self.__shutdown      = False
        else:
            datagen_logger.error('%s file does not exist in folder %s', configuration_filename, options['etc'])  
    
    def terminate(self):
        self.__shutdown = True
        for job in self.__copy_job_list:
            datagen_logger.debug('terminating copy job %s', job.name)
            job.join()
        for job in self.__move_job_list:
            datagen_logger.debug('terminating move job %s', job.name)
            job.join()
        subprocess.Popen(['kill', str(self.pid)]).wait()
            
    def run(self):
        
        '''
         The nowait attribute is used for testing purpose only.
         It is not defined in the property file.
         If one would like to use this attribute, then it has to be manually added into the option dictionary object passed
         to the __init__ constructor
        '''
        nowait = bool(self.__options['nowait']) if self.__options.has_key('nowait') else False
        
        self.__rop_time_period = int(self.__options['rop_time_period'])
        self.__ignore_session_time = bool(self.__options['ignore_session_time']) if self.__options.has_key('ignore_session_time') else False
        
        if not nowait:
            sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period) + self.__rop_time_period * 60
            datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
            time.sleep(sleeptime)

        self.__target_session = get_rop_session_through_system_time(self.__rop_time_period, 2) if not self.__ignore_session_time else None
        try:                    
            while True and not self.__shutdown:            
                start_time = get_current_utc_time_in_seconds()
                # always copies files that are 2xrop_time_period before, therefore need to calculate time delta between current time and 
                # session start time                  
                diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%s starts at utc time %s', self.__class__.__name__, datetimeToString(datetime.utcfromtimestamp(start_time)))
                number_of_consecutive_copy_process = 0                
                while diff_in_minutes >= 2 * self.__rop_time_period:
                    datagen_logger.trace('>>>>>>> start_time: %s, session start time: %s', datetimeToString(datetime.utcfromtimestamp(start_time)), self.__target_session[0])
                    number_of_consecutive_copy_process += 1
                    self.__monitor_mz_output()
                    diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%d consecutive copy and move process(es) executed', number_of_consecutive_copy_process)
                end_time = get_current_utc_time_in_seconds()
                
                minute_spend = (end_time - start_time) / 60
                if not nowait and minute_spend < self.__rop_time_period:
                    sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    if sleeptime == 60 * self.__rop_time_period:
                        time.sleep(5)
                        sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
                    time.sleep(sleeptime)
                else:
                    datagen_logger.debug('%s kick off without sleep', self.__class__.__name__)
                    
                if nowait:
                    break
                                
        except:
            datagen_logger.exception(sys.stderr)
            sys.exit()

                    
    def __monitor_mz_output(self):        

        generated_file_location_map = self.__options['generated_file_location_map']
        dest_directory_list_map     = self.__options['dest_directory_list_map']
        name_prefix_map             = self.__options['name_prefix_map']
        option_map                  = self.__options['option_map']
        rnc_mcc_mnc_lac_map         = self.__options['rnc_mcc_mnc_lac_map']
        
        datagen_logger.info('looking for generated tcp partial files of session %s', 
                            get_session_str_representation(self.__target_session))
        
        generated_file_location_keys = generated_file_location_map.keys()
        generated_file_location_keys.sort()
        
        self.__copy_job_list[:] = []
        self.__move_job_list[:] = []
        
        rnc_mcc_mnc_lac_map_keys = rnc_mcc_mnc_lac_map.keys()
        for rnc_number_in_string in rnc_mcc_mnc_lac_map_keys:

            target_search_pattern = None        
            if self.__target_session:
                target_search_pattern = get_tcp_partial_search_pattern(self.__target_session, rnc_mcc_mnc_lac_map[rnc_number_in_string])
            else:
                target_search_pattern = ''.join(['*', STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING,'*',
                                                 STAPLE_TCPTA_PARTIAL_FILENAME_EXT,'*'])
            
            for generated_file_location_key in generated_file_location_keys:
                
                generated_file_location = generated_file_location_map.get(generated_file_location_key)
                
                if not dest_folder_check(generated_file_location):
                    datagen_logger.error('destination folder %s does not exist', generated_file_location)                
                    continue
                
                if not generated_file_location.endswith(os.sep):
                    generated_file_location += os.sep
                
                dest_directory_map = dest_directory_list_map.get(generated_file_location_key)
                dest_directory_options = dest_directory_map.keys()
                
                for dest_directory_option in dest_directory_options:                
                    name_prefix = ''
                    if name_prefix_map.has_key(dest_directory_option):  
                        name_prefix = name_prefix_map.get(dest_directory_option) 
                        
                    dest_directory = dest_directory_map.get(dest_directory_option)
                    if not dest_directory.endswith(os.sep):
                        dest_directory += os.sep
                    
                    dest_directory += ''.join([rnc_mcc_mnc_lac_map.get(rnc_number_in_string),os.sep])   
                    generated_file_location += ''.join([rnc_mcc_mnc_lac_map.get(rnc_number_in_string),os.sep])
                    
                    if option_map.has_key(dest_directory_option):
                        if 'copy' == option_map.get(dest_directory_option):
                            self.__copy_job_list.append(Thread(target=copy_or_move, 
                                                        args=(self.__class__.__name__, 
                                                              ({'generated_file_location':generated_file_location, 
                                                                'name_prefix':name_prefix, 
                                                                'dest_directory':dest_directory},), 
                                                              target_search_pattern, 'copy', self.__target_session,
                                                              self.__rop_time_period, 0, None, log_tcpta_partial_events_count)))
                            
                        if 'move' == option_map.get(dest_directory_option):
                            self.__move_job_list.append(Thread(target=copy_or_move, 
                                                        args=(self.__class__.__name__, 
                                                              ({'generated_file_location':generated_file_location, 
                                                               'name_prefix':name_prefix, 
                                                               'dest_directory':dest_directory},), 
                                                               target_search_pattern, 'move', self.__target_session,
                                                               self.__rop_time_period, 0, None, log_tcpta_partial_events_count)))
        number_of_copy_jobs = len(self.__copy_job_list)        
        datagen_logger.info('%d number of copy jobs created for session %s', 
                            number_of_copy_jobs, get_session_str_representation(self.__target_session))
        
        number_of_move_jobs = len(self.__move_job_list)
        datagen_logger.info('%d number of move jobs created for session %s', 
                            number_of_move_jobs, get_session_str_representation(self.__target_session))
        
        if number_of_copy_jobs > 0:
            start_time = get_current_utc_time_in_seconds()
            for job in self.__copy_job_list:
                job.start()
            for job in self.__copy_job_list:
                job.join()
            end_time = get_current_utc_time_in_seconds()            
            datagen_logger.info('tcpta partial file copy finished with in %f minutes, %d copy jobs', 
                                (end_time-start_time)/60.0, number_of_copy_jobs)
        
        if number_of_move_jobs > 0:
            start_time = get_current_utc_time_in_seconds()
            for job in self.__move_job_list:
                job.start()
            for job in self.__move_job_list:
                job.join()
            end_time = get_current_utc_time_in_seconds()            
            datagen_logger.info('tcpta partial file move finished with in %f minutes, %d move jobs', 
                                (end_time-start_time)/60.0, number_of_move_jobs)       
        
        if self.__target_session: 
            self.__target_session = [self.__target_session[0] + timedelta(minutes=self.__rop_time_period),
                                     self.__target_session[1] + timedelta(minutes=self.__rop_time_period)]


class ClassificationOutputMonitor(Process):
    
    def __init__(self, options):
        Process.__init__(self)
        configuration_filename = 'classification_monitor.props'
        
        if os.path.exists(options['etc']+os.sep+configuration_filename):
            classification_monitor_config = ConfigParser()
            classification_monitor_config.readfp(open(options['etc']+os.sep+configuration_filename))
            
            self.__options = dict()
            self.__options['rop_time_period']         = classification_monitor_config.getint('basic', 'rop_time_period')
            self.__options['ignore_session_time']     = classification_monitor_config.getboolean('basic', 'ignore_session_time')         

            rnc_mcc_mnc_lac_map = {}
            if not classification_monitor_config.has_section('rnc_mcc_mnc_lac_map'):
                datagen_logger.error('rnc_mcc_mnc_lac_map section is not defined in the props file')
                sys.exit()

            rncs = classification_monitor_config.options('rnc_mcc_mnc_lac_map')
            for rnc in rncs:
                rnc_mcc_mnc_lac_map[rnc] = classification_monitor_config.get('rnc_mcc_mnc_lac_map', rnc)
            
                    
            self.__options['rnc_mcc_mnc_lac_map']     = rnc_mcc_mnc_lac_map          
            self.__options['nowait']                  = options['nowait']
            
            return_values = get_maps_from_monitor_props(classification_monitor_config)            
            self.__options['dest_directory_list_map']     = return_values[0]
            self.__options['generated_file_location_map'] = return_values[1]            
            self.__options['option_map']                  = return_values[2]            
            self.__options['name_prefix_map']             = return_values[3]
                            
            datagen_logger.trace('%s options: %s', self.__class__.__name__, self.__options)
            
            self.__copy_job_list = []
            self.__move_job_list = []
            self.__shutdown      = False
            
        else:
            datagen_logger.error('%s file does not exist in folder %s', configuration_filename, options['etc'])
    
    def terminate(self):              
        self.__shutdown = True
        for job in self.__copy_job_list:
            datagen_logger.debug('terminating copy job %s', job.name)
            job.join()
        for job in self.__move_job_list:
            datagen_logger.debug('terminating move job %s', job.name)
            job.join()
        
        subprocess.Popen(['kill', str(self.pid)]).wait()
                
    def run(self):
        
        '''
         The nowait attribute is used for testing purpose only.
         It is not defined in the property file.
         If one would like to use this attribute, then it has to be manually added into the option dictionary object passed
         to the __init__ constructor
        '''
        nowait = bool(self.__options['nowait']) if self.__options.has_key('nowait') else False
        
        self.__rop_time_period = int(self.__options['rop_time_period'])
        self.__ignore_session_time = bool(self.__options['ignore_session_time']) if self.__options.has_key('ignore_session_time') else False
        
        if not nowait:
            sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period) + self.__rop_time_period * 60
            datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
            time.sleep(sleeptime)

        self.__target_session = get_rop_session_through_system_time(self.__rop_time_period, 2) if not self.__ignore_session_time else None
        try:                  
            while True and not self.__shutdown:        
                start_time = get_current_utc_time_in_seconds()
                # always copies files that are 2xrop_time_period before, therefore need to calculate time delta between current time and 
                # session start time                  
                diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%s starts at utc time %s', self.__class__.__name__, datetimeToString(datetime.utcfromtimestamp(start_time)))
                number_of_consecutive_copy_process = 0                
                while diff_in_minutes >= 2 * self.__rop_time_period:
                    datagen_logger.trace('>>>>>>> start_time: %s, session start time: %s', datetimeToString(datetime.utcfromtimestamp(start_time)), self.__target_session[0])
                    number_of_consecutive_copy_process += 1
                    self.__monitor_mz_output()
                    diff_in_minutes = (start_time-get_timstamp_in_sec_of_datetime(self.__target_session[0]))/60.0
                datagen_logger.info('%d consecutive copy and move process(es) executed', number_of_consecutive_copy_process)
                end_time = get_current_utc_time_in_seconds()
                
                minute_spend = (end_time - start_time) / 60
                if not nowait and minute_spend < self.__rop_time_period:
                    sleeptime = get_sleep_time_in_sec(datetime.utcnow(), self.__rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                  
                    time.sleep(sleeptime)
                else:
                    datagen_logger.debug('%s kick off without sleep', self.__class__.__name__)
                if nowait:
                    break
                                                
        except:
            datagen_logger.exception(sys.stderr)
            sys.exit()

    def __monitor_mz_output(self):        
        
        generated_file_location_map = self.__options['generated_file_location_map']
        dest_directory_list_map     = self.__options['dest_directory_list_map']
        name_prefix_map             = self.__options['name_prefix_map']
        option_map                  = self.__options['option_map']
        rnc_mcc_mnc_lac_map         = self.__options['rnc_mcc_mnc_lac_map']

        datagen_logger.info('looking for generated classification files of session %s', get_session_str_representation(self.__target_session))
        
        generated_file_location_keys = generated_file_location_map.keys()
        generated_file_location_keys.sort()
        
        copy_job_list = []
        move_job_list = []
        
        rnc_mcc_mnc_lac_map_keys = rnc_mcc_mnc_lac_map.keys()
        for rnc_number_in_string in rnc_mcc_mnc_lac_map_keys:
            
            target_search_pattern = None        
            if self.__target_session:
                target_search_pattern = get_classification_search_pattern(self.__target_session, rnc_mcc_mnc_lac_map[rnc_number_in_string])
            else:
                target_search_pattern = ''.join(['*', CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING,'*',
                                                 CAPTOOL_SUMMARY_FILENAME_EXT,'*'])   
        
            for generated_file_location_key in generated_file_location_keys:
                generated_file_location = generated_file_location_map.get(generated_file_location_key)
                
                if not dest_folder_check(generated_file_location):   
                    datagen_logger.error('destination folder %s does not exist', generated_file_location)              
                    continue
                
                if not generated_file_location.endswith(os.sep):
                    generated_file_location += os.sep
                
                dest_directory_map = dest_directory_list_map.get(generated_file_location_key)
                dest_directory_options = dest_directory_map.keys()
                for dest_directory_option in dest_directory_options:
                    name_prefix = ''
                    if name_prefix_map.has_key(dest_directory_option):  
                        name_prefix = name_prefix_map.get(dest_directory_option) 
                        
                    dest_directory = dest_directory_map.get(dest_directory_option)
                    if not dest_directory.endswith(os.sep):
                        dest_directory += os.sep
                    
                    dest_directory += ''.join([rnc_mcc_mnc_lac_map.get(rnc_number_in_string),os.sep])   
                    generated_file_location += ''.join([rnc_mcc_mnc_lac_map.get(rnc_number_in_string),os.sep])
                    
                    if option_map.has_key(dest_directory_option):
                        if 'copy' == option_map.get(dest_directory_option):
                            copy_job_list.append(Thread(target=copy_or_move, 
                                                        args=(self.__class__.__name__, 
                                                              ({'generated_file_location':generated_file_location, 
                                                                'name_prefix':name_prefix, 
                                                                'dest_directory':dest_directory},), 
                                                              target_search_pattern, 'copy', deepcopy(self.__target_session),
                                                              self.__rop_time_period, 0, None, log_classification_events_count)))
                            
                        if 'move' == option_map.get(dest_directory_option):
                            move_job_list.append(Thread(target=copy_or_move, 
                                                        args=(self.__class__.__name__, 
                                                              ({'generated_file_location':generated_file_location, 
                                                               'name_prefix':name_prefix, 
                                                               'dest_directory':dest_directory},), 
                                                               target_search_pattern, 'move', deepcopy(self.__target_session),
                                                               self.__rop_time_period, 0, None, log_classification_events_count)))
                                    
        
        number_of_copy_jobs = len(copy_job_list)        
        datagen_logger.info('%d number of copy jobs created for session %s', 
                            number_of_copy_jobs, get_session_str_representation(self.__target_session))
        
        number_of_move_jobs = len(move_job_list)
        datagen_logger.info('%d number of move jobs created for session %s', 
                            number_of_move_jobs, get_session_str_representation(self.__target_session))
        
        p = Thread(target=job_handler, 
                    name='ClassificationCopyAndMoveHandlerProcess', 
                    args=(copy_job_list, move_job_list, 
                          deepcopy(self.__target_session)))
        p.start()    
            
        if self.__target_session: 
            self.__target_session = [self.__target_session[0] + timedelta(minutes=self.__rop_time_period),
                                     self.__target_session[1] + timedelta(minutes=self.__rop_time_period)]
