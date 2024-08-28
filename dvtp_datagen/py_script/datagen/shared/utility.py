'''
Created on Aug 16, 2012

@author: epstvxj
'''

from datagen.settings import datagen_logger, deprecated
from datagen.shared.constants import CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING, \
    STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING, STAPLE_TCPTA_PARTIAL_FILENAME_EXT, \
    CAPTOOL_SUMMARY_FILENAME_EXT
from datagen.validate.compare_ggsn import get_number_of_rop_files
from datetime import datetime, timedelta
import errno
import glob
import os
import re
import shutil
import stat
import threading
import time
    
def get_tcpta_partial_rop_identity_from_filename(tcpta_partial_file):
    
    if tcpta_partial_file is None:
        return None
    
    file_startindex = tcpta_partial_file.rfind(os.sep) + 1
    file_endindex   = tcpta_partial_file.find(STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING)
    assert(file_endindex >= file_startindex)
    
    return tcpta_partial_file[file_startindex:file_endindex]

def get_summary_rop_identity_from_filenam(summary_file):
    
    if summary_file is None:
        return None
    
    file_startindex = summary_file.rfind(os.sep) + 1
    file_endindex   = summary_file.find(CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING)
    assert(file_endindex >= file_startindex)
    
    return summary_file[file_startindex:file_endindex]
    

def get_sgeh_rop_identity_from_filename(sgeh_file):
    
    if sgeh_file is None:
        return None
    
    file_startindex = sgeh_file.find('A')
    assert(file_startindex > 0)
    file_endindex = file_startindex+38
    
    return sgeh_file[file_startindex:file_endindex]

def get_ggsn_rop_identity_from_filename(mpN):
    
    if mpN is None:
        return None
    
    file_startindex = mpN.find('A')
    file_endindex   = mpN.find('_SubNetwork=RNC')
    return mpN[file_startindex:file_endindex]

def get_timstamp_in_sec_of_datetime(datetime_obj):
    return time.mktime(datetime_obj.timetuple())

def get_current_utc_time_in_seconds():
    return time.mktime(datetime.utcnow().timetuple())



'''
    This function decodes mp0, find number of expected mpX files
    according to the number of link records in the mp0 file
'''
def check_full_rop_exists(file_list):       
    
    if file_list is None or len(file_list) == 0:
        return (False,0)
     
    file_list.sort()        
    assert('Mp0' in file_list[0])
    number_of_mp_files = get_number_of_rop_files(file_list[0])
    
    if number_of_mp_files != len(file_list):
        return (False,number_of_mp_files)
    else:
        return (True,number_of_mp_files)
        
def get_maps_from_monitor_props(monitor_config):
    dest_directory_list_map     = {}                        
    generated_file_location_map = {}
    move_option_map             = {}
    name_prefix_map             = {}
    
    generated_file_location_options = monitor_config.options('generated_file_locations')
    
    for generated_file_location_option in generated_file_location_options:
        directory = monitor_config.get('generated_file_locations', generated_file_location_option)
        generated_file_location_map[generated_file_location_option] = directory.strip()
        
        dest_directory_options = monitor_config.options(generated_file_location_option)
        dest_directory_list_map[generated_file_location_option] = list
        if dest_directory_options:
            dest_directory_list_map[generated_file_location_option] = {}                    
            for dest_directory_option in dest_directory_options:
                dest_directory_option = dest_directory_option.strip()
                move_option_map[dest_directory_option] = 'copy'
                if monitor_config.has_option('move', dest_directory_option):
                    is_move = monitor_config.getboolean('move', dest_directory_option)
                    if is_move:
                        move_option_map[dest_directory_option] = 'move'

                name_prefix = ''                        
                if monitor_config.has_option('name prefixes', dest_directory_option):
                    name_prefix = monitor_config.get('name prefixes', dest_directory_option)                        
                    
                name_prefix_map[dest_directory_option] = name_prefix
                dest_directory_list_map[generated_file_location_option][dest_directory_option] = monitor_config.get(generated_file_location_option, dest_directory_option).strip()
    datagen_logger.trace("dest_directory_list_map: %s", dest_directory_list_map)
    datagen_logger.trace("generated_file_location_map: %s", generated_file_location_map)
    datagen_logger.trace("move_option_map: %s", move_option_map)
    datagen_logger.trace("name_prefix_map: %s", name_prefix_map)
    return (dest_directory_list_map,generated_file_location_map,move_option_map,name_prefix_map)

def replace(search_string, pattern_string, replacements, append_0=True):
    pattern = re.compile(pattern_string)
    for replacement in replacements: 
        match_object = pattern.search(search_string)
        if match_object is not None:
            match = match_object.group(0)
            if int(replacement) < 10 and append_0:
                search_string = search_string.replace(match, ''.join(['0',str(replacement)]))
            else:
                search_string = search_string.replace(match, str(replacement))
            
    return search_string

def increase_session(session, increment):
    return session + timedelta(minutes=increment)

def get_rop_sessions_before_current_datetime(start_rop_datetime_str, end_rop_datetime_str, rop_time_period):
    
    start_rop_datetime = datetime.strptime(start_rop_datetime_str, "%Y%m%d%H%M%S")
    end_rop_datetime = datetime.strptime(end_rop_datetime_str, "%Y%m%d%H%M%S")
    inc = timedelta(minutes=rop_time_period)
    
    current_rop_session_start = get_rop_session_through_system_time()[0]
    
    end_rop_datetime = end_rop_datetime if end_rop_datetime < current_rop_session_start else current_rop_session_start 
    
    sessions = []
    
    while start_rop_datetime < end_rop_datetime:
        sessions.append((start_rop_datetime, start_rop_datetime+rop_time_period))
        start_rop_datetime += inc
    
    return sessions

def get_rop_sessions(start_rop_datetime_str, end_rop_datetime_str, rop_time_period_str):
    
    start_rop_datetime = datetime.strptime(start_rop_datetime_str, "%Y%m%d%H%M")
    end_rop_datetime = datetime.strptime(end_rop_datetime_str, "%Y%m%d%H%M")    
    inc = timedelta(minutes=int(rop_time_period_str))
        
    sessions = []
    
    while start_rop_datetime < end_rop_datetime:
        sessions.append((start_rop_datetime, start_rop_datetime+inc))
        start_rop_datetime += inc
        
    if len(sessions) == 0:
        return None
        
    return sessions

'''
    return a session that is one rop before the current system time
'''
def get_rop_session_through_system_time(rop_time_period, number_of_rop_ahead=1):
    
    current_datetime = datetime.utcnow() - timedelta(minutes=rop_time_period*number_of_rop_ahead)
    
    session_starttime = datetime(year        = current_datetime.year,
                                 month       = current_datetime.month,
                                 day         = current_datetime.day,
                                 hour        = current_datetime.hour,
                                 minute      = current_datetime.minute - current_datetime.minute % rop_time_period,
                                 second      = 0,
                                 microsecond = 0)
    
    increament = timedelta(minutes=rop_time_period)
    
    current_datetime += increament
    
    session_endtime = datetime(year        = current_datetime.year,
                               month       = current_datetime.month,
                               day         = current_datetime.day,
                               hour        = current_datetime.hour,
                               minute      = current_datetime.minute - current_datetime.minute % rop_time_period,
                               second      = 0,
                               microsecond = 0)
        
    return (session_starttime,session_endtime)

def get_tcp_partial_filename_prefix(session):
        
    session_start_datetime = session[0]
    session_end_datetime = session[1]
    
    datetime_list = ['A']
    datetime_list.append(str(session_start_datetime.year))
    
    if session_start_datetime.month < 10:
        datetime_list.append('0'+str(session_start_datetime.month))
    else:
        datetime_list.append(str(session_start_datetime.month))
    
    if session_start_datetime.day < 10:    
        datetime_list.append('0'+str(session_start_datetime.day))
    else:
        datetime_list.append(str(session_start_datetime.day))
    datetime_list.append('.')    
    # start hour
    if session_start_datetime.hour < 10:
        datetime_list.append('0'+str(session_start_datetime.hour))
    else:
        datetime_list.append(str(session_start_datetime.hour))
    
    # start minute
    minute_start = session_start_datetime.minute - session_start_datetime.minute % 5
    if minute_start < 10:
        datetime_list.append('0'+str(minute_start))
    else:
        datetime_list.append(str(minute_start))
    datetime_list.append('-')
    
    # end hour
    if session_end_datetime.hour < 10:
        datetime_list.append('0'+str(session_end_datetime.hour))
    else:
        datetime_list.append(str(session_end_datetime.hour))
    
    # end minute    
    minute_end = session_end_datetime.minute - session_end_datetime.minute % 5
    if minute_end < 10:
        datetime_list.append('0'+str(minute_end))
    else:
        datetime_list.append(str(minute_end))
    datetime_list.append(STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING)
       
    tcp_partial_filename_prefix = ''.join(datetime_list)

    return tcp_partial_filename_prefix

@deprecated
def get_flv_partial_filename_prefix(session):
    
    session_start_datetime = session[0]
    session_end_datetime = session[1]
    
    datetime_list = ['A']
    datetime_list.append(str(session_start_datetime.year))
    
    if session_start_datetime.month < 10:
        datetime_list.append('0'+str(session_start_datetime.month))
    else:
        datetime_list.append(str(session_start_datetime.month))
    
    if session_start_datetime.day < 10:    
        datetime_list.append('0'+str(session_start_datetime.day))
    else:
        datetime_list.append(str(session_start_datetime.day))
    datetime_list.append('.')    
    # start hour
    if session_start_datetime.hour < 10:
        datetime_list.append('0'+str(session_start_datetime.hour))
    else:
        datetime_list.append(str(session_start_datetime.hour))
    
    # start minute
    minute_start = session_start_datetime.minute - session_start_datetime.minute % 5
    if minute_start < 10:
        datetime_list.append('0'+str(minute_start))
    else:
        datetime_list.append(str(minute_start))
    datetime_list.append('-')
    
    
    # end hour
    if session_end_datetime.hour < 10:
        datetime_list.append('0'+str(session_end_datetime.hour))
    else:
        datetime_list.append(str(session_end_datetime.hour))
    
    # end minute    
    minute_end = session_end_datetime.minute - session_end_datetime.minute % 5
    if minute_end < 10:
        datetime_list.append('0'+str(minute_end))
    else:
        datetime_list.append(str(minute_end))
    datetime_list.append('_staple_flv-partial_')
       
    flv_partial_filename_prefix = ''.join(datetime_list)

    return flv_partial_filename_prefix

@deprecated
def get_classification_filename_prefix(session):
    session_start_datetime = session[0]
    
    filename_prefix_components = ['summary-']
    filename_prefix_components.append(str(session_start_datetime.year))
    
    month = session_start_datetime.month
    if month < 10:
        filename_prefix_components.append('0')
    filename_prefix_components.append(str(month))
        
    day = session_start_datetime.day
    if day < 10:
        filename_prefix_components.append('0')
    filename_prefix_components.append(str(day))
    
    filename_prefix = ''.join(filename_prefix_components)

    return filename_prefix


def ensure_dir(directory):
    
    if directory == None:
        return
    
    lock = threading.Lock()
    lock.acquire()
    try:
        if not os.path.exists(directory):
            os.makedirs(directory)
            os.chmod(directory, stat.S_IRWXO | stat.S_IRWXU | stat.S_IRWXG)
    except OSError as exception:
        if exception.errno != errno.EEXIST:
            raise
    finally:
        lock.release()
    
def clean_dir(directory, pattern = ''):
       
    if pattern == None:
        pattern = ''
    
    lock = threading.Lock()
    lock.acquire()
    try:
        if os.path.exists(directory):            
            for something in os.listdir(directory):
                path = get_path(directory,something)
                if os.path.isdir(path):
                    if pattern in something:
                        datagen_logger.info('delete directory: %s', path)
                        shutil.rmtree(path)
                else:
                    if pattern in something:
                        datagen_logger.info('delete file: %s', path)
                        os.remove(path)
    finally:
        lock.release()    
        
def get_path(directory, filename):
    if directory.endswith(os.sep):
        return directory + filename
    else:
        return directory + os.sep + filename

def find_sgeh_match_file(generated_file, source_directory):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'A']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('A')
    else:
        filename_start_index += 1
    
    date_str = generated_file[filename_start_index+1:filename_start_index+9]
    generated_file=generated_file[filename_start_index:]
    pattern=generated_file.replace(date_str, '*')
    if not source_directory.endswith(os.sep):
        source_directory = ''.join([source_directory,os.sep])    
    matches = glob.glob(''.join([source_directory,pattern]))
    if matches is None or len(matches) == 0:
        return None
    else: 
        return matches[0]

#summary-20121004165021-000408-029.blk
@deprecated
def find_classification_file(generated_file, source_directory):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'summary-']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('summary-')
    else:
        filename_start_index += 1
    
    date_str = generated_file[filename_start_index+8:filename_start_index+16]
    generated_file=generated_file[filename_start_index:]
    pattern=generated_file.replace(date_str, '*')

    if not source_directory.endswith(os.sep):
        source_directory = ''.join([source_directory,os.sep])    
    matches = glob.glob(''.join([source_directory,pattern]))
    if matches is None or len(matches) == 0:
        return None
    else: 
        return matches[0]

#454_06_8900_A20120515.2350-2355_staple_tcpta-partial_1337122500_1-282.log
def find_staple_and_captool_file(generated_file, source_directory):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'A']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('A')
    else:
        filename_start_index += 1
    
    date_str = generated_file[filename_start_index+1:filename_start_index+9]
    
    generated_file=generated_file[filename_start_index:]
    pattern=generated_file.replace(date_str, '*')

    if not source_directory.endswith(os.sep):
        source_directory = ''.join([source_directory,os.sep])    
    matches = glob.glob(''.join([source_directory, '*', pattern]))
    
    if matches is None or len(matches) == 0:
        return None
    else: 
        return matches[0]
    
def get_expected_datetime(generated_file):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'A']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('A')
    else:
        filename_start_index += 1
    
    date_str = generated_file[filename_start_index+1:filename_start_index+9]
    year = int(date_str[0:4])
    month = int(date_str[4:6])
    day = int(date_str[-2:])
    
    return datetime(year=year,month=month,day=day)

#A20121004.0638+0800-20121004.0639+0800_1_ebs.23
@deprecated
def get_sgeh_expected_datetime(generated_file):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'A']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('A')
    else:
        filename_start_index += 1
    
    start_date_str = generated_file[filename_start_index+1:filename_start_index+9]
    start_year = int(start_date_str[0:4])
    start_month = int(start_date_str[4:6])
    start_day = int(start_date_str[-2:]) 
    
    start_time_str = generated_file[filename_start_index+10:filename_start_index+14]
    start_hour = int(start_time_str[0:2])
    start_minute = int(start_time_str[2:4])
    start_timediff_sign = generated_file[filename_start_index+14:filename_start_index+15]
    start_timediff_hour = int(generated_file[filename_start_index+15:filename_start_index+17])
    start_timediff_minute = int(generated_file[filename_start_index+17:filename_start_index+19])
    start_datetime = datetime(year=start_year,month=start_month,day=start_day,hour=start_hour,minute=start_minute)
    
    if start_timediff_sign == '+':
        start_datetime = start_datetime - timedelta(minutes=start_timediff_hour*60+start_timediff_minute)
    else:
        start_datetime = start_datetime + timedelta(minutes=start_timediff_hour*60+start_timediff_minute)
    
    return start_datetime

@deprecated
def get_classification_datetime(generated_file):
    filename_start_index = generated_file.rfind(''.join([os.sep, 'summary-']))
    if filename_start_index < 0:
        filename_start_index = generated_file.rfind('summary-')
    else:
        filename_start_index += 1
    
    date_str = generated_file[filename_start_index+8:filename_start_index+20]
    year = int(date_str[0:4])
    month = int(date_str[4:6])
    day = int(date_str[6:8])
    hour = int(date_str[8:10])
    minute = int(date_str[10:12])
    
    return datetime(year=year,month=month,day=day,hour=hour,minute=minute)

def get_boolean(bool_str):
    try:    
        getattr(object, 'lower')    
        if bool_str.lower() == 'true':
            return True
        else:
            return False
    except:
        return False

def get_ggsn_filename_search_pattern(session):
    
    target_prefix_components = ['A']
    
    target_prefix_components.append(str(session[0].year))
            
    if session[0].month < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].month))
    
    if session[0].day < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].day))
    
    target_prefix_components.append('.')
    
    if session[0].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].hour))
    
    if session[0].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].minute))
    
    target_prefix_components.append('-')
    
    if session[1].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].hour))
    
    if session[1].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].minute))
    
    target_prefix_components.append('_SubNetwork=RNC*,MeContext=RNC*_rnc_ggsnfile_Mp*.bin.gz*')
    
    return ''.join(target_prefix_components)

# A20120517.0131+0800-20120517.0132+0800_1_ebs.226
def get_sgeh_filename_search_pattern(session):
    
    target_prefix_components = ['A']
    
    target_prefix_components.append(str(session[0].year))
            
    if session[0].month < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].month))
    
    if session[0].day < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].day))
    
    target_prefix_components.append('.')
    
    if session[0].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].hour))
    
    if session[0].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].minute))
    
    target_prefix_components.append('*-')
    
    target_prefix_components.append(str(session[1].year))
            
    if session[1].month < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].month))
    
    if session[1].day < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].day))
    
    target_prefix_components.append('.')
    
    if session[1].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].hour))
    
    if session[1].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].minute))
    
    
    target_prefix_components.append('*_ebs.*')
    
    return ''.join(target_prefix_components)

# A20120518.0325-0330_staple_tcpta-partial_1337308200_1-282.log
def get_tcp_partial_search_pattern(session, rnc_mcc_mnc_lac):
    
    target_prefix_components = [rnc_mcc_mnc_lac,'-A']
    
    target_prefix_components.append(str(session[0].year))
            
    if session[0].month < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].month))
    
    if session[0].day < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].day))
    
    target_prefix_components.append('.')
    
    if session[0].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].hour))
    
    if session[0].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].minute))
    
    target_prefix_components.append('*-')
    
    if session[1].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].hour))
    
    if session[1].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].minute))
    
    target_prefix_components.append(''.join(['*',STAPLE_TCPTA_PARTIAL_FILENAME_SEARCH_STRING,'*',
                                             STAPLE_TCPTA_PARTIAL_FILENAME_EXT,'*']))
    
    return ''.join(target_prefix_components)

#summary-20120516165021-001679-030.blk
def get_classification_search_pattern(session, rnc_mcc_mnc_lac):
    
    target_prefix_components = [rnc_mcc_mnc_lac,'-A']
    
    target_prefix_components.append(str(session[0].year))
            
    if session[0].month < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].month))
    
    if session[0].day < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].day))
    
    target_prefix_components.append('.')
    
    if session[0].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].hour))
    
    if session[0].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[0].minute))
    
    target_prefix_components.append('*-')
    
    if session[1].hour < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].hour))
    
    if session[1].minute < 10:
        target_prefix_components.append('0')
    target_prefix_components.append(str(session[1].minute))
    
    target_prefix_components.append(''.join(['*',CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING,'*',
                                             CAPTOOL_SUMMARY_FILENAME_EXT,'*']))
    
    return ''.join(target_prefix_components)

def get_session_str_representation(session):
    
    if session is None or len(session) == 0:
        return None
    
    session_str_components = []
    
    session_str_components.append(str(session[0].year))
    session_str_components.append('-')
    session_str_components.append(str(session[0].month))
    session_str_components.append('-')
    session_str_components.append(str(session[0].day))
    session_str_components.append(' ')
    session_str_components.append(str(session[0].hour))
    session_str_components.append(':')
    session_str_components.append(str(session[0].minute))
    
    session_str_components.append(' ')
    
    session_str_components.append(str(session[1].year))
    session_str_components.append('-')
    session_str_components.append(str(session[1].month))
    session_str_components.append('-')
    session_str_components.append(str(session[1].day))
    session_str_components.append(' ')
    session_str_components.append(str(session[1].hour))
    session_str_components.append(':')
    session_str_components.append(str(session[1].minute))
    
    return ''.join(session_str_components)
    
def datetimeToString(from_datetime):
    
    string_list = []
    
    string_list.append(str(from_datetime.year))
    string_list.append('-')
    
    if from_datetime.month < 10:
        string_list.append('0')
    string_list.append(str(from_datetime.month))
    string_list.append('-')
    
    if from_datetime.day < 10:
        string_list.append('0')
    string_list.append(str(from_datetime.day))
    
    string_list.append(' ')
    
    if from_datetime.hour < 10:
        string_list.append('0')
    string_list.append(str(from_datetime.hour))
    string_list.append(':')
    
    if from_datetime.minute < 10:
        string_list.append('0')
    string_list.append(str(from_datetime.minute))
    string_list.append(':')
    
    if from_datetime.second < 10:
        string_list.append('0')
    string_list.append(str(from_datetime.second))
    
    return ''.join(string_list)
    
def get_sleep_time_in_sec(current_datetime, rop_time_period):
    
    minute = current_datetime.minute
    second = current_datetime.second
    
    return (rop_time_period - minute % rop_time_period) * 60 - second
    
def get_rop_session_through_system_time_with_diff(rop_time_period, diff, number_of_rops=1):
    
    current_datetime = datetime.utcnow() - timedelta(minutes=rop_time_period*number_of_rops-diff*60)

    session_starttime = datetime(year        = current_datetime.year,
                                 month       = current_datetime.month,
                                 day         = current_datetime.day,
                                 hour        = current_datetime.hour,
                                 minute      = current_datetime.minute - current_datetime.minute % rop_time_period,
                                 second      = 0,
                                 microsecond = 0)
    
    increament = timedelta(minutes=rop_time_period)
    
    current_datetime += increament

    
    session_endtime = datetime(year        = current_datetime.year,
                               month       = current_datetime.month,
                               day         = current_datetime.day,
                               hour        = current_datetime.hour,
                               minute      = current_datetime.minute - current_datetime.minute % rop_time_period,
                               second      = 0,
                               microsecond = 0)
        
    return (session_starttime,session_endtime)
        