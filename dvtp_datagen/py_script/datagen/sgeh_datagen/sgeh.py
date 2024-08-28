'''
Created on Aug 22, 2012

@author: epstvxj
'''

from ConfigParser import ConfigParser
from datagen.settings import datagen_logger
from datagen.sgeh_datagen.mz import startwg, is_wg_active, check_wg_status, \
    stop_wg, stopwf, enablewf, get_number_of_workflows, enable_wg, remove_wf_from_wg, \
    add_wf_into_wg, disablewf
from datagen.shared import metrics, datagen_logging
from datagen.shared.file_lookup_service import SgehFileLookupService
from datagen.shared.utility import clean_dir, ensure_dir, \
    get_rop_session_through_system_time, get_rop_sessions, increase_session, \
    get_sleep_time_in_sec, get_current_utc_time_in_seconds
from datetime import datetime, timedelta
from multiprocessing.process import Process
from shutil import copy
from threading import Thread
import glob
import os
import stat
import sys
import time

'''
  The SgehGenerator copies files from master file repository to feed a MZ SGEH_SESSION workflow.
  Files are renamed and copied to the input directory of the MZ SGEH_SESSION workflow. 
  The SgehGenerator also dynamically generates the configuration.properties file used by MZ SGEH_SESSION workflows
  
  To initialise a SgehGenerator object, a dictionary object containing the setting information 
  should be provided.
  
  The dictionary object may contain the following key/value pairs:
  nowait [True|False] (optional), default to False
         used for debug purpose in the realtime processing mode; this attribute indicates whether the running thread 
         should wait to process next rop
  noenrich [True|False] (optional), default to False,
         determine whether to enrich sgeh data or not; 
  clean [True|False] (optional), default to False,
         whether to clean the output directory first         
  mz_property_file_directory
         indicates where the generated configuration.properties file used by MZ should be stored
  mz_property_filename
         the name of the property file used by MZ SGEH_SESSION workflow
  realtime
         whether to generated data in realtime mode or dump mode
  rop_time_period
         indicates how long does the generator have to wait (minute based) in the realtime mode, 
         e.g., for sgeh data, rop_time_period is 1 minute
  rop_start
         used in dump mode only, indicates the rop start time, e.g., 201205161600 (2012-05-16 16:00)
  rop_end
         used in dump mode only, indicates the rop end time, e.g., 201205171600 (2012-05-17 16:00)
  rop_start_timediff_sign
         used to rename the sgeh file name, e.g., +
  rop_start_timediff
         used to rename the sgeh file name, e.g., 0800
  rop_end_timediff_sign
         used to rename the sgeh file name, e.g., +
  rop_end_timediff
         used to rename the sgeh file name, e.g., 0800
  mzsh
         the location where mz executable file stored
  mz_account
         MediationZone account, i.e., username/password
  workgroup_name
         workgroup name to invoke
  number_of_sgsns
         number of sgsn replications
  input_directory
         the location where master files are stored
  datetime_enrichment_options
         datetime enrichment option for each SGSN
  imsi_enrichment_options
         imsi enrichment option for each SGSN
  mz input directory
         input directories of MZ SGEH_SESSION workflows
  mz output directory
         output directories of MZ SGEH_SESSION workflows 
'''
class SgehGenerator(Process):

    def __init__(self, options):
        Process.__init__(self)
        self.__options       = options
        self.__shutdown      = False
        self.__copy_job_list = []
        datagen_logger.trace('%s options: %s', self.__class__.__name__, self.__options)
        
    def run(self):
        self.generate(self.__options)
    
    def terminate(self):
        
        for __copy_job in self.__copy_job_list:
            __copy_job.exit()
        
        self.__shutdown = True
        self.stop_workflows()
        Process.terminate(self)
        
    def stop_workflows(self):
        try:
            props_filename = 'gpeh.props'
            if not os.path.exists(self.__options['etc']+os.sep+props_filename):
                datagen_logger.error('configuration file %s does not exist', props_filename)
                return
                
            config = ConfigParser()                       
            config.readfp(open(self.__options['etc'] + os.sep + props_filename))
            
            mzsh           = config.get('basic', 'mzsh')
            mz_account     = config.get('basic', 'mz_account')
            workgroup_name = config.get('basic', 'workgroup_name')    
            if mzsh is not None and mz_account is not None and workgroup_name is not None:
                while is_wg_active(mzsh, mz_account, workgroup_name):
                    datagen_logger.info('terminating %s workgroup', workgroup_name)
                    stop_wg(mzsh, mz_account, workgroup_name)
                    time.sleep(2)
            else:
                datagen_logger.error('mzsh path: %s, mz account: %s, workgroup name: %s', mzsh, mz_account, workgroup_name)
        except:
            datagen_logger.exception(sys.stderr)
                
    def generate(self, options):
        try:            
            # [debug only] wait to sleep in realtime mode
            nowait   = options['nowait']   if options.has_key('nowait')   else False
            # [debug only] whether to perform the enrichment process
            noenrich = options['noenrich'] if options.has_key('noenrich') else False
            # [debug only] whether to clean the mz input directory
            clean    = options['clean']    if options.has_key('clean')    else False
            # [debug only] whether to copy files from master repository into the mz input directory
            copy     = options['copy']     if options.has_key('copy')     else True
            
            props_filename = 'sgeh.props'
            if not os.path.exists(options['etc']+os.sep+props_filename):
                datagen_logger.error('configuration file %s does not exist', props_filename)
                sys.exit(1)
                
            config = ConfigParser()                       
            config.readfp(open(options['etc'] + os.sep + props_filename))
            
            lookup_timerange_start = config.get('basic', 'lookup_timerange_start')
            if not lookup_timerange_start:
                datagen_logger.error('lookup start time required')
                sys.exit()        
            
            lookup_timerange_end   = config.get('basic', 'lookup_timerange_end')
            if not lookup_timerange_end:
                datagen_logger.error('lookup end time required')
                sys.exit()
            
            realtime                   = options['realtime']
            rop_start                  = options['rop_start']
            rop_end                    = options['rop_end']
            
            rop_time_period            = config.getint('basic', 'rop_time_period') 
            rop_start_timediff_sign    = config.get('basic', 'rop_start_timediff_sign')
            rop_start_timediff         = config.get('basic', 'rop_start_timediff')
            rop_end_timediff_sign      = config.get('basic', 'rop_end_timediff_sign')
            rop_end_timediff           = config.get('basic', 'rop_start_timediff')
            
            mz_property_filename       = config.get('basic', 'mz_property_filename')
            mz_property_file_directory = config.get('basic', 'mz_property_file_directory')
            mzsh                       = config.get('basic', 'mzsh')
            mz_account                 = config.get('basic', 'mz_account')
            workgroup_name             = config.get('basic', 'workgroup_name')                        
            workflow_name_prefix       = config.get('basic', 'workflow_name_prefix')
                
            if not config.has_section('mz_output_directory'):
                datagen_logger.error('no mz_output_directory section specified in the sgeh configuration file')
                sys.exit()
            if not config.has_section('mz_input_directory'):
                datagen_logger.error('no mz_input_directory section specified in the sgeh configuration file')
                sys.exit()
            if not config.has_section('datetime_enrichment_options'):
                datagen_logger.error('no datetime_enrichment_options section specified in the sgeh configuration file')
                sys.exit()
            if not config.has_section('imsi_enrichment_options'):
                datagen_logger.error('no imsi_enrichment_options section specified in the sgeh configuration file')
                sys.exit()
            if not config.has_section('workflows'):
                datagen_logger.error('no workflows section specified in the sgeh configuration file')
                sys.exit()
                              
            input_directory = config.get('basic', 'input_directory')
            if not input_directory.endswith(os.sep):
                input_directory = ''.join([input_directory, os.sep])
             
        except: 
            datagen_logger.exception(sys.stderr)
            
        max_num_of_workflow = get_number_of_workflows(mzsh, mz_account, ''.join((workflow_name_prefix,'*')))    
        datagen_logger.info('%d workflows in %s', max_num_of_workflow, workgroup_name) 
        '''
            generate configuration.properties file that is used by SGEH Enrichment Workflow
        '''
        ensure_dir(mz_property_file_directory)
        if not mz_property_file_directory.endswith(os.sep):
            mz_property_file_directory = ''.join([mz_property_file_directory,os.sep])
        
        mz_input_directories = []
        with open(''.join([mz_property_file_directory,mz_property_filename]), 'w+') as f:                                
            f.write('ENRICH_IMSI_ENABLED='+str(options['enrich_imsi'])+'\n')            
            f.write('ENRICH_DATETIME_ENABLED='+str(options['enrich_datetime'])+'\n')
            f.write('NUMBER_OF_SGSNS='+str(len(config.options('workflows')))+'\n')
            
            for index in range(1,max_num_of_workflow+1):
                sgsn_identity = 'SGSN0' + str(index) if index < 10 else 'SGSN' + str(index)
                if config.has_option('mz_input_directory', sgsn_identity):
                    mz_input_directory = config.get('mz_input_directory', sgsn_identity)
                    if not mz_input_directory.endswith(os.sep):
                        mz_input_directory = ''.join((mz_input_directory,os.sep))
                    ensure_dir(mz_input_directory)
                    mz_input_directories.append(mz_input_directory)
                    if clean:
                        datagen_logger.trace('clean mz input directory: %s', mz_input_directory)
                        clean_dir(mz_input_directory)  
                    f.write('INPUT_'+sgsn_identity+'='+mz_input_directory+'\n')
                else:
                    f.write('INPUT_'+sgsn_identity+'=\n')
                if config.has_option('mz_input_directory', sgsn_identity):
                    mz_output_directory = config.get('mz_output_directory', sgsn_identity)
                    if not mz_output_directory.endswith(os.sep):
                        mz_output_directory = ''.join((mz_output_directory,os.sep))
                    ensure_dir(mz_output_directory)                    
                    f.write('OUTPUT_'+sgsn_identity+'='+mz_output_directory+'\n')
                    f.write('IMSI_ENRICHMENT_OPTION_'+sgsn_identity+'='+config.get('imsi_enrichment_options', sgsn_identity)+'\n')
                    f.write('DATETIME_ENRICHMENT_OPTION_'+sgsn_identity+'='+config.get('datetime_enrichment_options', sgsn_identity)+'\n')
                else:
                    f.write('OUTPUT_'+sgsn_identity+'=\n')
                    f.write('IMSI_ENRICHMENT_OPTION_'+sgsn_identity+'=False\n')
                    f.write('DATETIME_ENRICHMENT_OPTION_'+sgsn_identity+'=False\n')
       
        
        if workgroup_name:
            enable_wg(mzsh, mz_account, workgroup_name)
        # stop all workflows 
        if workflow_name_prefix:
            stopwf(mzsh, mz_account, ''.join((workflow_name_prefix,'*')))
        # enable only the workflow specified        
        enable_workflows = []
        disable_workflows = []
        for index in range(1,max_num_of_workflow+1):
            workflow_id = '0' + str(index) if index < 10 else str(index)
            
            if config.has_option('workflows', 'SGSN'+workflow_id):
                enable_workflows.append(config.get('workflows', 'SGSN'+workflow_id))
            else:
                disable_workflows.append(workflow_id)
        
        disablewf(mzsh, mz_account, workflow_name_prefix+'('+'|'.join(disable_workflows)+')')
        remove_wf_from_wg(mzsh, mz_account, workgroup_name, workflow_name_prefix+'('+'|'.join(disable_workflows)+')')
        
        add_wf_into_wg(mzsh, mz_account, workgroup_name, '('+'|'.join(enable_workflows)+')')
        enablewf(mzsh, mz_account, '('+'|'.join(enable_workflows)+')')

        '''
            copy file from file repository to the output directory and rename it correspondingly
        '''
        sgeh_lookup = SgehFileLookupService((lookup_timerange_start, lookup_timerange_end,
                                             rop_start_timediff, rop_start_timediff_sign,
                                             rop_end_timediff, rop_end_timediff_sign))
        try:                          
            if realtime:
                
                if not nowait:                    
                    sleeptime = get_sleep_time_in_sec(datetime.utcnow(), rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                
                    time.sleep(sleeptime)
                    
                session = get_rop_session_through_system_time(rop_time_period)
                
                while True and not self.__shutdown:
                    
                    process_start_datetime = datetime.utcnow()
                    
                    '''
                        if last generation process spent more than two rop_period_time, then we 
                        need to reset the session time 
                    '''
                    diff_in_minutes = (process_start_datetime-session[1]).seconds / 60 - rop_time_period;
                    if diff_in_minutes >= 2*rop_time_period:
                        datagen_logger.error('last %s process took place %d seconds ago, skip %d rop(s)', 
                                      self.__class__.__name__, (process_start_datetime-session[1]).seconds, 
                                      diff_in_minutes/rop_time_period-1)
                        new_session = get_rop_session_through_system_time(rop_time_period);
                        datagen_logger.error('set session from %s, %s -->> %s, %s', str(session[0]), str(session[1]), 
                                                                            str(new_session[0]), str(new_session[1]))
                        session = new_session
                    
                    datagen_logger.info('realtime sgeh file generation, current session: %s, %s', str(session[0]), str(session[1]))
                        
                    filename_prefix = sgeh_lookup.get_sgeh_filename_prefix(session[0], session[1])
                    datagen_logger.debug('sgeh lookup service search pattern %s', filename_prefix)
                                    
                    filematches = sgeh_lookup.find_sgeh_file(input_directory, filename_prefix)
                    if filematches:
                        datagen_logger.debug('find sgeh files starting with prefix: %s, %s', filename_prefix, ''.join(filematches))
                        
                        if datagen_logger.isTraceEnabled():
                            for filematch in filematches:
                                metrics.log_statistic_info(datagen_logger, datagen_logging.TRACE, 
                                                           'file %s, size %.4f MB' % (filematch, os.path.getsize(filematch)/1024.0/1024.0))
                                
                        session_start = None
                        session_end = None
                        if rop_start_timediff_sign == '+':
                            session_start = session[0] + timedelta(minutes=int(rop_start_timediff[2:4])+60*int(rop_start_timediff[0:2]))
                        else:
                            session_start = session[0] - timedelta(minutes=int(rop_start_timediff[2:4])+60*int(rop_start_timediff[0:2]))
                        datagen_logger.info('session start in UTC: %s, session start in UTC+8 %s', str(session[0]), str(session_start))
                            
                        if rop_end_timediff_sign == '+':
                            session_end = session[1] + timedelta(minutes=int(rop_end_timediff[2:4])+60*int(rop_end_timediff[0:2]))
                        else:
                            session_end = session[1] - timedelta(minutes=int(rop_end_timediff[2:4])+60*int(rop_end_timediff[0:2]))
                        datagen_logger.info('session end in UTC: %s, session end in UTC+8 %s', str(session[1]), str(session_end))
    
                        self.__copy_job_list = []
                        for sgsn_dir in mz_input_directories:
                            datagen_logger.trace('copy job created, destination folder %s', sgsn_dir)
                            self.__copy_job_list.append(Thread(target=self.__copy, args=(filematches, (session_start,session_end), input_directory, sgsn_dir)))
                         
                        start_time = get_current_utc_time_in_seconds()
                        for job in self.__copy_job_list:
                            job.start()
                        for job in self.__copy_job_list:
                            job.join()
                        self.__copy_job_list[:] = []
                        end_time = get_current_utc_time_in_seconds()            
                        datagen_logger.info('copy sgeh files to MZ input directories finished with in %f seconds, %s, %s', 
                                     (end_time-start_time), str(session[0]), str(session[1])) 
                            
                        if not noenrich:                                                                        
                            if not is_wg_active(mzsh, mz_account, workgroup_name):
                                start_time = get_current_utc_time_in_seconds()
                                startwg(mzsh, mz_account, workgroup_name)
                                time.sleep(10) # wait for 10 seconds to make sure that workgroup starts
                                process_current_datetime = datetime.utcnow()
                                # maximum time to wait is 2 rop time - 10 seconds
                                timeout = 60 * 2 * rop_time_period - ((process_current_datetime-process_start_datetime).seconds) - 10
                                datagen_logger.debug('set %s timeout --> %d', self.__class__.__name__, timeout)
                                while is_wg_active(mzsh, mz_account, workgroup_name) and timeout > 0:
                                    check_status_start_time = get_current_utc_time_in_seconds()
                                    check_wg_status(mzsh, mz_account, workgroup_name)
                                    check_status_end_time = get_current_utc_time_in_seconds()
                                    time.sleep(10)
                                    timeout -= (10 + (check_status_end_time-check_status_start_time))
                                
                                if is_wg_active(mzsh, mz_account, workgroup_name):
                                    stop_wg(mzsh, mz_account, workgroup_name)
                                                            
                                end_time = get_current_utc_time_in_seconds()
                                datagen_logger.info('sgeh file regeneration finished with in %f seconds for session %s, %s', (end_time - start_time), str(session[0]), str(session[1]))                            
                            else:
                                datagen_logger.error('%s workflow is still processing previous rop', workgroup_name)
                    else:
                        datagen_logger.warning('no sgeh files matching search pattern %s', filename_prefix)      
                                                      
                    session = (increase_session(session[0], rop_time_period),
                               increase_session(session[1], rop_time_period))
                        
                    process_end_datetime = datetime.utcnow()
                    
                    total_time_spend_in_minute = float((process_end_datetime-process_start_datetime).seconds)/60.0
                    
                    if total_time_spend_in_minute >= rop_time_period:
                        if total_time_spend_in_minute >= 2*rop_time_period:
                            datagen_logger.error('system is too busy, total time spend %f', total_time_spend_in_minute)
                        # no need to sleep
                        datagen_logger.debug('%s continue without sleep', self.__class__.__name__)
                        continue
                    
                    if not nowait:                    
                        sleeptime = get_sleep_time_in_sec(datetime.utcnow(), rop_time_period)
                        if sleeptime == 60 * rop_time_period:
                            time.sleep(5)
                            sleeptime = get_sleep_time_in_sec(datetime.utcnow(), rop_time_period)
                        datagen_logger.debug('%s sleep for %d', __name__, sleeptime)           
                        time.sleep(sleeptime)
            else:
                sessions = get_rop_sessions(rop_start, rop_end, rop_time_period)
                
                if sessions is not None:
                    for session in sessions:
                        filename_prefix = sgeh_lookup.get_sgeh_filename_prefix(session[0], session[1])
                        datagen_logger.debug('sgeh lookup service search pattern %s', filename_prefix)
                        filematches = sgeh_lookup.find_sgeh_file(input_directory, filename_prefix)
                        if filematches:
                            datagen_logger.debug('find sgeh files starting with prefix: %s, %s', filename_prefix, filematches)
                            
                            if datagen_logger.isTraceEnabled():
                                for filematch in filematches:
                                    metrics.log_statistic_info(datagen_logger, datagen_logging.TRACE, 
                                                               'file %s, size %.4f MB' % (filematch, os.path.getsize(filematch)/1024.0/1024.0))
                            
                            session_start = None
                            session_end = None
                            if rop_start_timediff_sign == '+':
                                session_start = session[0] + timedelta(minutes=int(rop_start_timediff[2:4])+60*int(rop_start_timediff[0:2]))
                            else:
                                session_start = session[0] - timedelta(minutes=int(rop_start_timediff[2:4])+60*int(rop_start_timediff[0:2]))
                            datagen_logger.info('session start in UTC: %s, session start in UTC+8 %s', str(session[0]), str(session_start))
                                
                            if rop_end_timediff_sign == '+':
                                session_end = session[1] + timedelta(minutes=int(rop_end_timediff[2:4])+60*int(rop_end_timediff[0:2]))
                            else:
                                session_end = session[1] - timedelta(minutes=int(rop_end_timediff[2:4])+60*int(rop_end_timediff[0:2]))
                            datagen_logger.info('session end in UTC: %s, session end in UTC+8 %s', str(session[1]), str(session_end))
                            
                            if copy:    
                                copy_job_list = []
                                for sgsn_dir in mz_input_directories:
                                    datagen_logger.trace('copy to %s', sgsn_dir)
                                    datagen_logger.trace('%d sgeh files in the MZ input directory %s', len(glob.glob(''.join([sgsn_dir,'*']))), sgsn_dir)  
                                    copy_job_list.append(Process=self.__copy, args=(filematches, (session_start,session_end), input_directory, sgsn_dir))  
                                 
                                start_time = get_current_utc_time_in_seconds()
                                for job in copy_job_list:
                                    job.start()
                                for job in copy_job_list:
                                    job.join()
                                end_time = get_current_utc_time_in_seconds()            
                                datagen_logger.info('copy sgeh files to MZ input directories finished with in %f seconds', (end_time-start_time))   
                                                           
                            else:
                                datagen_logger.warning('copy is not enabled')
                                                            
                        if not noenrich:
                            if not is_wg_active(mzsh, mz_account, workgroup_name):
                                start_time = get_current_utc_time_in_seconds()
                                startwg(mzsh, mz_account, workgroup_name)
                                time.sleep(10)
                                timeout = 60 * rop_time_period - 10
                                while is_wg_active(mzsh, mz_account, workgroup_name) and timeout != 0:
                                    check_wg_status(mzsh, mz_account, workgroup_name)
                                    time.sleep(10)
                                    timeout -= 10
                                
                                if timeout == 0 and is_wg_active(mzsh, mz_account, workgroup_name):
                                    datagen_logger.error('sgeh file regeneration lasts more than %d minutes for session %s, %s',  rop_time_period, str(session[0]), str(session[1]))
                                    stop_wg(mzsh, mz_account, workgroup_name)
                                else:                                                        
                                    end_time = get_current_utc_time_in_seconds()
                                    datagen_logger.info('sgeh file regeneration finished with in %f seconds for session %s, %s', (end_time - start_time), str(session[0]), str(session[1]))
        except:
            datagen_logger.exception(sys.stderr)
            
    '''
     session, a tuple contains session start and end time
     file_list, a sequence of input files
    '''
    def __copy(self, file_list, session, input_directory, output_directory):
                              
        ensure_dir(output_directory)
        
        component = [str(session[0].year)]
        if session[0].month < 10:
            component.append('0')
        component.append(str(session[0].month))
        if session[0].day < 10:
            component.append('0')
        component.append(str(session[0].day))
        new_start_date_str = ''.join(component)
        
        component = [str(session[1].year)]
        if session[1].month < 10:
            component.append('0')
        component.append(str(session[1].month))
        if session[1].day < 10:
            component.append('0')
        component.append(str(session[1].day))
        new_end_date_str = ''.join(component)
        
        for raw_file in file_list:
            subpaths = raw_file.split(os.sep)
            filename_index = len(subpaths) - 1
            filename = subpaths[filename_index]
            old_start_date_str = filename[1:9]
            old_end_date_str = filename[20:28]
            
            new_filename = filename.replace(old_start_date_str, new_start_date_str)
            new_filename = new_filename.replace(old_end_date_str, new_end_date_str)
            
            # need to set the permission, otherwise, MZ cannot delete processed files
            os.chmod(output_directory, stat.S_IRWXO | stat.S_IRWXU | stat.S_IRWXG)            
            new_file = output_directory + new_filename if output_directory.endswith(os.sep) else output_directory + os.sep + new_filename
            
            try:
                if os.path.exists(new_file):
                    os.remove(new_file)          
                datagen_logger.debug('%s => %s', raw_file, new_file)
                copy(raw_file, new_file)          
                
                os.chmod(new_file, stat.S_IRWXO | stat.S_IRWXU | stat.S_IRWXG)
            except:
                datagen_logger.exception(sys.stderr)
                datagen_logger.error('error when trying to copy sgeh raw files to the output directory: %s', output_directory)
        
