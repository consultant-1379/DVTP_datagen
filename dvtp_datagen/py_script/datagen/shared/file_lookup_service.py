'''
Created on 15 Aug 2012

@author: epstvxj

This module contains Sgeh, Classification, Flv Partial, Tcp Partial file lookup service classes.
These service classes search for rop files in the master repository based a given session period.
The result of lookup service are rop files that are used as base data sets for imsi/datetime enrichment.
The lookup time range is defined by the lookup_timerange_start and lookup_timerange_end
specified in the .props configuration files, 

The file lookup service only supports 24 hour rop-file-lookup.
The lookup service determines matches based on the value of year, month and day 
reading from the rop_start_datetime and rop_end_datetime variables and hour and minute 
from the current_rop_start_datatime and current_rop_end_datetime variables. 
'''

from datagen.settings import datagen_logger
from datagen.shared.constants import CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING
from datetime import datetime, timedelta
import glob
import os
import sys
    
class GgsnFileLookupService():
    
    def __init__(self, options):
        
        try:
            self.__rop_start_datetime = datetime.strptime(options[0], "%Y%m%d%H%M%S")
            self.__rop_end_datetime = datetime.strptime(options[1], "%Y%m%d%H%M%S")
            datagen_logger.info('ggsn file lookup %s, %s', self.__rop_start_datetime, self.__rop_end_datetime)
        except:
            datagen_logger.exception(sys.stderr)
            datagen_logger.error(options)
            sys.exit()    

    def get_ggsn_filename_search_pattern(self, current_rop_start_datetime):
        
        if self.__rop_start_datetime is None or self.__rop_end_datetime is None:
            return None;
        
        filename_search_pattern_components = ['APGGSN*']
        filename_search_pattern_components.append(str(self.__rop_start_datetime.year))

        day = None
        
        if (current_rop_start_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        
        if month < 10:
            filename_search_pattern_components.append('0')
        filename_search_pattern_components.append(str(month))
        
        if day < 10:
            filename_search_pattern_components.append('0')
        filename_search_pattern_components.append(str(day))

        hour = current_rop_start_datetime.hour
        if hour < 10:
            filename_search_pattern_components.append('0')
        filename_search_pattern_components.append(str(hour))
        
        minute = current_rop_start_datetime.minute
        if minute < 10:
            filename_search_pattern_components.append('0')
        filename_search_pattern_components.append(str(minute))
		
		
        filename_search_pattern_components.append('*_*')

        filename_search_pattern = ''.join(filename_search_pattern_components)        
                
        return filename_search_pattern
    #end get_ggsn_filename_search_pattern
        
    def find_ggsn_files(self, input_directory, filename_search_pattern):
        
        if input_directory.startswith("\""):
            input_directory = input_directory[1:len(input_directory)-1]    
                    
        if not input_directory.endswith(os.sep):
            input_directory = ''.join([input_directory,os.sep])
            
        datagen_logger.debug('search for pattern %s',input_directory+filename_search_pattern)
        ggsn_file_list = glob.glob(input_directory+filename_search_pattern)
                
        return ggsn_file_list

class SgehFileLookupService():
    
    def __init__(self, options, ):
                
        try:
            self.__rop_start_datetime = datetime.strptime(options[0], "%Y%m%d%H%M")
            self.__rop_end_datetime = datetime.strptime(options[1], "%Y%m%d%H%M")        
            self.__rop_start_timediff = datetime.strptime(options[2], "%H%M")
            self.__rop_start_timediff_sign = options[3]
            self.__rop_end_timediff = datetime.strptime(options[4], "%H%M")
            self.__rop_end_timediff_sign = options[5]
            datagen_logger.info('sgeh file lookup %s, %s', self.__rop_start_datetime, self.__rop_end_datetime)
        except:
            datagen_logger.exception(sys.stderr)
            datagen_logger.error(options)
            sys.exit()
        
    def find_sgeh_file(self, input_directory, filename_prefix):
        
        if input_directory.startswith("\""):
            input_directory = input_directory[1:len(input_directory)-1]    
            
        sgeh_file_list = []
        
        if not input_directory.endswith(os.sep):
            input_directory = ''.join([input_directory,os.sep])
        input_files = glob.glob(input_directory+"A*_ebs*")
        
        #TODO should use search instead, see GPEH
                
        for input_file in input_files:           
            if filename_prefix in input_file:
                sgeh_file_list.append(input_file)
        
        return sgeh_file_list 
        
    
    def get_sgeh_filename_prefix(self, current_rop_start_datetime, current_rop_end_datetime):
        
        if self.__rop_start_datetime is None or self.__rop_end_datetime is None:
            return None;
        
        if self.__rop_start_timediff_sign == '+':
            current_rop_start_datetime += timedelta(hours=self.__rop_start_timediff.hour,minutes=self.__rop_start_timediff.minute)
        else:
            current_rop_start_datetime -= timedelta(hours=self.__rop_start_timediff.hour,minutes=self.__rop_start_timediff.minute)
        
        if self.__rop_end_timediff_sign == '+':
            current_rop_end_datetime += timedelta(hours=self.__rop_end_timediff.hour,minutes=self.__rop_end_timediff.minute)
        else:
            current_rop_end_datetime -= timedelta(hours=self.__rop_end_timediff.hour,minutes=self.__rop_end_timediff.minute)
                
        filename_prefix_components = ['A']
        filename_prefix_components.append(str(self.__rop_start_datetime.year))
        
        day = None
        
        if (current_rop_start_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        if month < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(month))
        
        if day < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(day))
        
        filename_prefix_components.append('.')
        
        hour = current_rop_start_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_start_datetime.minute - current_rop_start_datetime.minute % 1
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append(self.__rop_start_timediff_sign)
        
        timediff_hour = self.__rop_start_timediff.hour
        if timediff_hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(timediff_hour))
        
        timediff_minute = self.__rop_start_timediff.minute
        if timediff_minute < 10:
            filename_prefix_components.append('0')#
        filename_prefix_components.append(str(timediff_minute))
        
        filename_prefix_components.append('-')
        
        filename_prefix_components.append(str(self.__rop_end_datetime.year))
        
        rop_day_delta = current_rop_end_datetime.day - current_rop_start_datetime.day                
        rop_month_delta = current_rop_end_datetime.month - current_rop_start_datetime.month
        
        day = None
        
        if (current_rop_end_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        if month < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(month))
                
        if rop_day_delta > 0 or rop_month_delta > 0:
            day += 1
        
        if day < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(day))
        
        filename_prefix_components.append('.')
        
        hour = current_rop_end_datetime.hour 
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_end_datetime.minute - current_rop_end_datetime.minute % 1
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append(self.__rop_end_timediff_sign)
        
        timediff_hour = self.__rop_end_timediff.hour
        if timediff_hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(timediff_hour))
        
        timediff_minute = self.__rop_end_timediff.minute
        if timediff_minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(timediff_minute))
        
        filename_prefix_components.append('_')
        
        filename_prefix = ''.join(filename_prefix_components)
        
        datagen_logger.debug('sgeh file prefix %s', filename_prefix)
        
        return filename_prefix
    # end get_sgeh_filename_prefix

class CaptoolClassificationFileLookupService():
    
    def __init__(self, options):
                       
        self.__rop_start_datetime = datetime.strptime(options[0], "%Y%m%d%H%M")
        self.__rop_end_datetime = datetime.strptime(options[1], "%Y%m%d%H%M")
        datagen_logger.info('classification file lookup %s %s', self.__rop_start_datetime, self.__rop_end_datetime)
    
    def find_captool_classification_file(self, input_directory, filename_prefix):
        
        if input_directory.startswith("\""):
            input_directory = input_directory[1:len(input_directory)-1]    

        if not input_directory.endswith(os.sep):
            input_directory = ''.join([input_directory,os.sep])
        
        input_files = glob.glob(''.join([input_directory,'*',filename_prefix,'*']))        
                
        return input_files

    def get_captool_classification_filename_prefix(self, current_rop_start_datetime, current_rop_end_datetime):
        
        if self.__rop_start_datetime is None or self.__rop_end_datetime is None:
            return None;
        
        filename_prefix_components = ['A']
        filename_prefix_components.append(str(self.__rop_start_datetime.year))
 
        day = None
        
        if (current_rop_start_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        if month < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(month))
        
        if day < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(day))
        
        filename_prefix_components.append('.')
        
        hour = current_rop_start_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_start_datetime.minute - current_rop_start_datetime.minute % 1
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append('-')
        
        hour = current_rop_end_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_end_datetime.minute - current_rop_end_datetime.minute % 1
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append(CAPTOOL_SUMMARY_FILENAME_SEARCH_STRING)
       
        filename_prefix = ''.join(filename_prefix_components)
    
        return filename_prefix
    # end get_captool_classification_filename_prefix    
    
class StapleFlvPartialFileLookupService():
    
    def __init__(self, options):

        try:
            self.__rop_start_datetime = datetime.strptime(options[0], "%Y%m%d%H%M")
            self.__rop_end_datetime = datetime.strptime(options[1], "%Y%m%d%H%M")
            self.__captool_lookup_table = None
            datagen_logger.info('flv partial file lookup %s, %s', self.__rop_start_datetime, self.__rop_end_datetime)
        except:
            datagen_logger.exception(sys.stderr)
            datagen_logger.error(options)
            sys.exit()
        
    def find_staple_flv_partial_file(self, input_directory, filename_prefix):
            
        if input_directory.startswith("\""):
            input_directory = input_directory[1:len(input_directory)-1]    

        flv_partial_file_list = []

        if not input_directory.endswith(os.sep):
            input_directory = ''.join([input_directory,os.sep])
        input_files = glob.glob(input_directory+"*_staple_flv-partial_*.log")        
        for input_file in input_files:                  
            if filename_prefix in input_file:
                flv_partial_file_list.append(input_file)
        
        return flv_partial_file_list   
    
    def get_staple_flv_partial_filename_prefix(self, current_rop_start_datetime, current_rop_end_datetime):
        
        if self.__rop_start_datetime is None or self.__rop_end_datetime is None:
            return None;
        
        filename_prefix_components = ['A']
        filename_prefix_components.append(str(self.__rop_start_datetime.year))
        
        day = None
        
        if (current_rop_start_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        if month < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(month))
        
        if day < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(day))
        
        filename_prefix_components.append('.')
        
        hour = current_rop_start_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_start_datetime.minute - current_rop_start_datetime.minute % 5
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append('-')
        
        hour = current_rop_end_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_end_datetime.minute - current_rop_end_datetime.minute % 5
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append('_staple_flv-partial_')
       
        filename_prefix = ''.join(filename_prefix_components)
    
        return filename_prefix
    # end get_staple_flv_partial_filename_prefix

class StapleTcpPartialFileLookupService():
    
    def __init__(self, options):
                       
        self.__rop_start_datetime = datetime.strptime(options[0], "%Y%m%d%H%M")
        self.__rop_end_datetime = datetime.strptime(options[1], "%Y%m%d%H%M")
        datagen_logger.info('tcpta partial file lookup %s %s', self.__rop_start_datetime, self.__rop_end_datetime)
    
    def find_staple_tcp_partial_file(self, input_directory, filename_prefix):
        
        if input_directory.startswith("\""):
            input_directory = input_directory[1:len(input_directory)-1]    

        if not input_directory.endswith(os.sep):
            input_directory = ''.join([input_directory,os.sep])
        
        input_files = glob.glob(''.join([input_directory,'*',filename_prefix,'*']))        
                
        return input_files

    def get_staple_tcp_partial_filename_prefix(self, current_rop_start_datetime, current_rop_end_datetime):
        
        if self.__rop_start_datetime is None or self.__rop_end_datetime is None:
            return None;
        
        filename_prefix_components = ['A']
        filename_prefix_components.append(str(self.__rop_start_datetime.year))
 
        day = None
        
        if (current_rop_start_datetime.hour >= self.__rop_start_datetime.hour):
            day = self.__rop_start_datetime.day
        else:
            day = self.__rop_end_datetime.day
                   
        month = self.__rop_start_datetime.month if day == self.__rop_start_datetime.day else self.__rop_end_datetime.month
        if month < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(month))
        
        if day < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(day))
        
        filename_prefix_components.append('.')
        
        hour = current_rop_start_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_start_datetime.minute - current_rop_start_datetime.minute % 5
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append('-')
        
        hour = current_rop_end_datetime.hour
        if hour < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(hour))
        
        minute = current_rop_end_datetime.minute - current_rop_end_datetime.minute % 5
        if minute < 10:
            filename_prefix_components.append('0')
        filename_prefix_components.append(str(minute))
        
        filename_prefix_components.append('_staple_tcpta-partial_')
       
        filename_prefix = ''.join(filename_prefix_components)
    
        return filename_prefix
    # end get_staple_tcp_partial_filename_prefix    
