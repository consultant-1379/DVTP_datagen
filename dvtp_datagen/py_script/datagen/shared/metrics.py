'''
Created on 18 Nov 2012

@author: epstvxj
'''
from datagen.shared.data_utility import get_ggsn_events_count_map,\
    get_sgeh_events_count_map
from datagen.shared.utility import check_full_rop_exists, \
    get_ggsn_rop_identity_from_filename, get_sgeh_rop_identity_from_filename, \
    get_tcpta_partial_rop_identity_from_filename, \
    get_summary_rop_identity_from_filenam
from datagen.validate.events import ggsn_events_mapping, sgeh_events_mapping
import re
import sys
import traceback
from datagen.shared.decorators import timer
import os

def log_statistic_info(logger, level, msg):
    if logger is not None:
        logger.log(level, msg)
    else:
        print(msg)

@timer
def log_classification_events_count(summary_files, logger=None, level=0):
    
    if summary_files is None or len(summary_files) == 0:
        return
    
    for summary_file in summary_files:
        try:
            with open(summary_file, 'rb') as reader:            
                event_count = 0
                for _line in reader:
                    event_count += 1
                log_statistic_info(logger, level, 'file %s [%.4f] has [%d] event(s) in total for rop %s' 
                                    % (summary_file, os.path.getsize(summary_file), 
                                       event_count, get_summary_rop_identity_from_filenam(summary_file)))
        except:
            if logger != None:
                logger.exception(sys.stderr)
            else:
                print(sys.stderr)
                traceback.print_exc(file=sys.stderr)
            continue

@timer
def log_tcpta_partial_events_count(tcpta_partial_files, logger=None, level=0):
    
    if tcpta_partial_files is None or len(tcpta_partial_files) == 0:
        return
    
    for tcpta_partial_file in tcpta_partial_files:
        try:
            with open(tcpta_partial_file, 'rb') as reader:
                event_count = 0
                for _line in reader:
                    event_count += 1
                log_statistic_info(logger, level, 'file %s [%.4f] has [%d] event(s) in total for rop %s' 
                                  % (tcpta_partial_file, os.path.getsize(tcpta_partial_file), event_count, 
                                     get_tcpta_partial_rop_identity_from_filename(tcpta_partial_file)))
        except:
            continue

@timer
def log_sgeh_events_count(sgeh_files, logger=None, level=0):
    
    for sgeh_file in sgeh_files:
        rop_event_count = 0
        
        event_count_map = get_sgeh_events_count_map(sgeh_file)   
            
        if event_count_map is None:
            continue        
            
        for item in event_count_map.items():
            event_name = sgeh_events_mapping[item[0]]
            event_count = item[1]
            rop_event_count += event_count
            
            log_statistic_info(logger, level, 'file %s has [%d] %s event' % (sgeh_file, event_count, event_name))
        
        log_statistic_info(logger, level, 'file %s [%.4f] has [%d] event(s) in total for rop %s' 
            % (sgeh_file, os.path.getsize(sgeh_file), rop_event_count, get_sgeh_rop_identity_from_filename(sgeh_file)))

@timer
def log_ggsn_events_count(ggsn_files, logger=None, level=0):
    
    if ggsn_files is None or len(ggsn_files) == 0:
        return
    
    ggsn_filemap = {}
    
    is_full_rop, number_of_mp_files = check_full_rop_exists(ggsn_files)
    if not is_full_rop:
        if logger != None:
            log_statistic_info(logger, level, 'do not have all files for the rop')
        else:
            print('do not have all files for the rop')
        return
    
    pattern = re.compile('(Mp\d*.bin.gz)')      
    for ggsn_file in ggsn_files:      
        key = pattern.search(ggsn_file).group(1)
        ggsn_filemap[key] = ggsn_file
        
    mp0 = ggsn_filemap.pop('Mp0.bin.gz')
    
    log_statistic_info(logger, level, '[%d] link record(s) in mp0 file %s, %.4f' 
                       % (number_of_mp_files-1, mp0, os.path.getsize(mp0)))
    
    rop_identity = get_ggsn_rop_identity_from_filename(mp0)
    rop_event_count = 0    
    for ggsn_file in ggsn_files:
        
        if ggsn_file.endswith('Mp0.bin.gz'):
            continue
               
        event_count_map = get_ggsn_events_count_map(ggsn_file)
        
        if event_count_map is None:
            continue
        
        mpN_event_count = 0
        for item in event_count_map.items():
            event_name = ggsn_events_mapping[item[0]]
            event_count = item[1]
            mpN_event_count += event_count
            
            log_statistic_info(logger, level, 'file %s has [%d] %s event' 
                               % (ggsn_file, event_count, event_name))
                
        log_statistic_info(logger, level, 'file %s [%.4f] has [%d] event(s) in total' 
                           % (ggsn_file, os.path.getsize(ggsn_file), mpN_event_count))
              
        rop_event_count += mpN_event_count
    
    log_statistic_info(logger, level, 'rop %s has [%d] event(s) in total' % (rop_identity, rop_event_count))
        