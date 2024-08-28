'''
Created on Sep 27, 2012

@author: epstvxj
'''
from ConfigParser import ConfigParser
from datagen.settings import datagen_logger
from datagen.sgeh_datagen.mz import startwg, is_wg_active, check_wg_status, \
    stop_wg, get_number_of_workflows, enable_wg, stopwf, disablewf, \
    remove_wf_from_wg, add_wf_into_wg, enablewf
from datagen.shared import metrics, datagen_logging
from datagen.shared.file_lookup_service import GgsnFileLookupService
from datagen.shared.utility import clean_dir, ensure_dir, \
    get_rop_session_through_system_time, get_rop_sessions, increase_session, \
    get_sleep_time_in_sec, get_current_utc_time_in_seconds
from datagen.validate.compare_ggsn import get_number_of_rop_files
from datetime import datetime
from multiprocessing.process import Process
from shutil import copy
from threading import Thread
import glob
import os
import stat
import sys
import time

class GgsnGenerator(Process):
    
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
            props_filename = 'ggsn.props'
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
            nowait   = options['nowait'] if options.has_key('nowait') else False
            noenrich = options['noenrich'] if options.has_key('noenrich') else False
            clean    = options['clean'] if options.has_key('clean') else False
            copy     = options['copy'] if options.has_key('copy') else True
            
            props_filename = 'ggsn.props'
            if not os.path.exists(options['etc']+os.sep+props_filename):
                datagen_logger.error('configuration file %s does not exist', props_filename)
                sys.exit(1)
                
            config = ConfigParser()                       
            config.readfp(open(options['etc'] + os.sep + props_filename))
            
            lookup_timerange_start = config.get('basic', 'lookup_timerange_start')
            if not lookup_timerange_start:
                datagen_logger.error('lookup start time required')
                sys.exit()        
            
            lookup_timerange_end = config.get('basic', 'lookup_timerange_end')
            if not lookup_timerange_end:
                datagen_logger.error('lookup end time required')
                sys.exit()
            
            realtime                   = options['realtime']
            rop_start                  = options['rop_start']
            rop_end                    = options['rop_end']
            
            encoder_version            = config.get('basic', 'encoder_version')            
            if encoder_version is None or len(encoder_version) == 0:
                datagen_logger.error('ggsn encoder is not specified')
                        
            rop_time_period            = config.getint('basic', 'rop_time_period') 
            number_of_rncs             = config.getint('basic', 'number_of_rncs')
            
            mz_property_filename       = config.get('basic', 'mz_property_filename')
            mz_property_file_directory = config.get('basic', 'mz_property_file_directory')
            
            mzsh                       = config.get('basic', 'mzsh')
            mz_account                 = config.get('basic', 'mz_account')
            workgroup_name             = config.get('basic', 'workgroup_name')
            workflow_name_prefix       = config.get('basic', 'workflow_name_prefix')

            if not config.has_section('mz_output_directory'):
                datagen_logger.error('no mz_output_directory section specified in the ggsn configuration file')
                sys.exit()
            if not config.has_section('mz_input_directory'):
                datagen_logger.error('no mz_input_directory section specified in the ggsn configuration file')
                sys.exit()
            if not config.has_section('datetime_enrichment_options'):
                datagen_logger.error('no datetime_enrichment_options section specified in the ggsn configuration file')
                sys.exit()
            if not config.has_section('imsi_enrichment_options'):
                datagen_logger.error('no imsi_enrichment_options section specified in the ggsn configuration file')
                sys.exit()
	    if not config.has_section('ggsnip_enrichment_options'):
                datagen_logger.error('no ggsnip_enrichment_options section specified in the ggsn configuration file')
                sys.exit()
            if not config.has_section('workflows'):
                datagen_logger.error('no workflows section specified in the ggsn configuration file')
                sys.exit()
                                         
            input_directory = config.get('basic', 'input_directory')
            if not input_directory.endswith(os.sep):
                input_directory = ''.join([input_directory, os.sep])
                             
        except: 
            datagen_logger.exception(sys.stderr)
                   
        max_num_of_workflow = get_number_of_workflows(mzsh, mz_account, ''.join((workflow_name_prefix,'*'))) 
        datagen_logger.info('%d workflows in %s', max_num_of_workflow, workgroup_name)
        '''
            generate configuration.properties file that is used by GPEH Enrichment Workflow
        '''
        ensure_dir(mz_property_file_directory)
        if not mz_property_file_directory.endswith(os.sep):
            mz_property_file_directory = ''.join([mz_property_file_directory, os.sep])

        '''
            Need to automatically generate ggsn workflow configuration.properties file
        '''
        ensure_dir(mz_property_file_directory)
        if not mz_property_file_directory.endswith(os.sep):
            mz_property_file_directory = ''.join([mz_property_file_directory, os.sep])

        mz_input_directories = []
        with open(''.join([mz_property_file_directory,mz_property_filename]), 'w+') as f:                                
            f.write('ENRICH_IMSI_ENABLED='+str(options['enrich_imsi'])+'\n')            
            f.write('ENRICH_DATETIME_ENABLED='+str(options['enrich_datetime'])+'\n')
	    f.write('ENRICH_GGSNIP_ENABLED='+str(options['enrich_ggsnip'])+'\n')
            f.write('NUMBER_OF_GGSNS='+str(len(config.options('workflows')))+'\n')
            
            for index in range(1,max_num_of_workflow+1):
                sgsn_identity = 'GGSN0' + str(index) if index < 10 else 'GGSN' + str(index)
                if config.has_option('mz_input_directory', sgsn_identity):
                    mz_input_directory = config.get('mz_input_directory', sgsn_identity)
                    if not mz_input_directory.endswith(os.sep):
                        mz_input_directory = ''.join((mz_input_directory,os.sep))
                    ensure_dir(mz_input_directory)
                    mz_input_directories.append(mz_input_directory)
                    datagen_logger.trace('clean mz input directory hitesh : %s', mz_input_directory)
                    datagen_logger.trace('clean mz input directories deepika : %s', mz_input_directories[0])
					
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
		    f.write('GGSNIP_ENRICHMENT_OPTION_'+sgsn_identity+'='+config.get('ggsnip_enrichment_options', sgsn_identity)+'\n')
                else:
                    f.write('OUTPUT_'+sgsn_identity+'=\n')
                    f.write('IMSI_ENRICHMENT_OPTION_'+sgsn_identity+'=False\n')
                    f.write('DATETIME_ENRICHMENT_OPTION_'+sgsn_identity+'=False\n')
		    f.write('GGSNIP_ENRICHMENT_OPTION_'+sgsn_identity+'=False\n')
       
        
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
            
            if config.has_option('workflows', 'GGSN'+workflow_id):
                enable_workflows.append(config.get('workflows', 'GGSN'+workflow_id))
            else:
                disable_workflows.append(workflow_id)
        
        disablewf(mzsh, mz_account, workflow_name_prefix+'('+'|'.join(disable_workflows)+')')
        remove_wf_from_wg(mzsh, mz_account, workgroup_name, workflow_name_prefix+'('+'|'.join(disable_workflows)+')')
        
        add_wf_into_wg(mzsh, mz_account, workgroup_name, '('+'|'.join(enable_workflows)+')')
        enablewf(mzsh, mz_account, '('+'|'.join(enable_workflows)+')')     
        
        '''
            copy file from file repository to the output directory and rename it correspondingly
        '''
        ggsn_lookup = GgsnFileLookupService((lookup_timerange_start, lookup_timerange_end))                
        
        
        try:
            if realtime:
                if not nowait:                    
                    sleeptime = get_sleep_time_in_sec(datetime.now(), rop_time_period)
                    datagen_logger.debug('%s sleep for %d', self.__class__.__name__, sleeptime)                
                    time.sleep(sleeptime)
                    
                session = get_rop_session_through_system_time(rop_time_period)
                        
                while True and not self.__shutdown:
                    #diya
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
                        
                    datagen_logger.info('realtime ggsn file generation, current session: %s, %s', str(session[0]), str(session[1]))
                        
                    filename_search_pattern = ggsn_lookup.get_ggsn_filename_search_pattern(session[0])
                    
                    datagen_logger.info('ggsn partial lookup service search pattern %s', filename_search_pattern)
                    
                    filematches = ggsn_lookup.find_ggsn_files(input_directory, filename_search_pattern)
                    #is_full_rop = self.__check_full_rop_exists(filematches, session)
                    if filematches:
                        datagen_logger.debug('find [%d] ggsn files starting with prefix %s', len(filematches), filename_search_pattern)
                        
                        if datagen_logger.isTraceEnabled():
                            for filematch in filematches:
                                metrics.log_statistic_info(datagen_logger, datagen_logging.TRACE, 
                                                           'file %s, size %.4f MB' % (filematch, os.path.getsize(filematch)/1024.0/1024.0))
                            
                        '''
                          ggsn workflow takes input from one location only
                        '''
                        self.__copy_job_list = []
                        for sgsn_dir in mz_input_directories:
                            datagen_logger.trace('copy job created, destination folder %s', sgsn_dir)
                            self.__copy_job_list.append(Thread(target=self.__copy, args=(filematches, session, input_directory, sgsn_dir)))
                         
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
                                datagen_logger.info('ggsn file regeneration finished with in %f minutes for session %s, %s', 
                                                (end_time - start_time) / 60.0, str(session[0]), str(session[1]))                            
                            else:
                                datagen_logger.error('%s workflow is still processing previous rop', workgroup_name)
                                            
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
                        
                        filename_search_pattern = ggsn_lookup.get_ggsn_filename_search_pattern(session[0], session[1])
                        datagen_logger.debug('ggsn partial lookup service search pattern %s', filename_search_pattern)
                        
                        filematches = ggsn_lookup.find_ggsn_files(input_directory, filename_search_pattern)
                        
                        is_full_rop = self.__check_full_rop_exists(filematches, session)
                        if filematches and is_full_rop:
                            datagen_logger.debug('find [%d] ggsn files starting with prefix %s', len(filematches), filename_search_pattern)
                            
                            if datagen_logger.isTraceEnabled():
                                for filematch in filematches:
                                    metrics.log_statistic_info(datagen_logger, datagen_logging.TRACE, 
                                                               'file %s, size %.4f MB' % (filematch, os.path.getsize(filematch)/1024.0/1024.0))
                                    
                            if copy:     
                                clean_dir(mz_input_directory)
                                datagen_logger.trace('copy to %s', mz_input_directory)
                                self.__copy(filematches, session, input_directory, mz_input_directory)
                                datagen_logger.trace('%d ggsn files in the MZ input directory %s', 
                                                     len(glob.glob(''.join([mz_input_directory, '*']))), mz_input_directory)  
                            else:
                                datagen_logger.warning('copy is not enabled')
                                   
                        if (not noenrich) and is_full_rop:
                            if not is_wg_active(mzsh, mz_account, workgroup_name):
                                start_time = get_current_utc_time_in_seconds()
                                startwg(mzsh, mz_account, workgroup_name)
                                time.sleep(10) # wait for 10 seconds to make sure that workgroup starts
                                timeout = 60 * rop_time_period - 10
                                while is_wg_active(mzsh, mz_account, workgroup_name) and timeout != 0:
                                    check_wg_status(mzsh, mz_account, workgroup_name)
                                    time.sleep(10)
                                    timeout -= 10
                                
                                if timeout == 0 and is_wg_active(mzsh, mz_account, workgroup_name):
                                    datagen_logger.error('ggsn file regeneration lasts more than %d minutes for session %s, %s',  rop_time_period, str(session[0]), str(session[1]))
                                    stop_wg(mzsh, mz_account, workgroup_name)
                                else:                                                        
                                    end_time = get_current_utc_time_in_seconds()
                                    datagen_logger.info('ggsn file regeneration finished with in %f minutes for session %s, %s', (end_time - start_time) / 60.0, str(session[0]), str(session[1]))
        except:
            datagen_logger.exception(sys.stderr)
    
    def __check_full_rop_exists(self, file_list, session):       
        
        if file_list is None or len(file_list) == 0:
            return False
         
        file_list.sort()        
        number_of_mp_files = get_number_of_rop_files(file_list[0])
        
        if number_of_mp_files != len(file_list):
            datagen_logger.error('cannot find all rop files for session [%s,%s], number of files required %d, number of files found %d', session[0], session[1], number_of_mp_files, len(file_list))
            return False
        else:
            datagen_logger.debug('find all rop files required for session [%s,%s]', session[0], session[1])
            return True
    
    '''
     session, a tuple contains session start and end time
     file_list, a sequence of input files
    '''
    def __copy(self, file_list, session, input_directory, output_directory):
                              
        ensure_dir(output_directory)
        
        '''
            The code block commented out below changes the ggsn files name
            before they are copied to the MZ input directory. Currently names are changed
            by MZ workflow, we could consider to move this logic into Python.
        '''
        
#        component = [str(session[0].year)]
#        if session[0].month < 10:
#            component.append('0')
#        component.append(str(session[0].month))
#        if session[0].day < 10:
#            component.append('0')
#        component.append(str(session[0].day))
#        new_start_date_str = ''.join(component)

        for raw_file in file_list:
            subpaths = raw_file.split(os.sep)
            filename_index = len(subpaths) - 1
            filename = subpaths[filename_index]
#            old_start_date_str = filename[1:9]                        
#            new_filename = filename.replace(old_start_date_str, new_start_date_str)
            new_filename = filename
            # need to set the permission, otherwise, MZ cannot delete processed files
            os.chmod(output_directory, stat.S_IRWXO | stat.S_IRWXU | stat.S_IRWXG)            
            new_file = output_directory + new_filename if output_directory.endswith(os.sep) else output_directory + os.sep + new_filename
            
            try:
                if os.path.exists(new_file):
                    os.remove(new_file)          
                
                copy(raw_file, new_file)
                
                os.chmod(new_file, stat.S_IRWXO | stat.S_IRWXU | stat.S_IRWXG)
            except:
                datagen_logger.exception(sys.stderr)
                datagen_logger.error('error when trying to copy ggsn raw files to the output directory: %s', output_directory)
    


