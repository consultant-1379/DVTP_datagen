'''
Created on Aug 16, 2012

@author: epstvxj

This is the entry point of the datagen tool.
Before using the tool, corresponding .props files should be set.
The path to the .props files should be set through the command line arguments, 
i.e., --etc=<.props files location>.

After installation, type the following command to see what arguments are required:

    python -m datagen.__main__ --help
    
'''
from ConfigParser import ConfigParser
from datagen import monitor
from datagen.ggsn_datagen.ggsn import GgsnGenerator
from datagen.lib import argparse
from datagen.settings import datagen_logger, listener_configurer, \
    logger_configurer, listener_process
from datagen.sgeh_datagen.sgeh import SgehGenerator
from datagen.shared.datagen_logging import logging_queue
from datagen.up_datagen.classification import ClassificationGenerator
from datagen.up_datagen.tcp_partial import TcpPartialGenerator
from datagen.validate.validator_monitor import SgehValidatorMointor, \
    GgsnValidatorMonitor, ClassificationValidatorMointor, TcpPartialValidatorMointor
from datetime import datetime
from multiprocessing.managers import BaseManager, Server, State
from multiprocessing.process import Process, current_process

import logging
import os
import socket
import sys
import threading
from datagen.shared import datagen_logging
import traceback
import subprocess
from threading import Thread
    
class DatagenServer(Server):

    def serve_until_stop(self):
        current_process()._manager_server = self
        try:
            try:
                while self.running:
                    try:
                        c = self.listener.accept()
                    except (OSError, IOError):
                        continue
                    t = threading.Thread(target=self.handle_request, args=(c,))
                    t.daemon = True
                    t.start()
            except (KeyboardInterrupt, SystemExit):
                pass
        finally:
            self.stop = 999
            self.listener.close()     
            
class DatagenManager(BaseManager):
    def get_server(self):
        assert self._state.value == State.INITIAL
        return DatagenServer(self._registry, self._address,
                      self._authkey, self._serializer)

def get_setup_options():
    parser = init_arg_parser()
               
    args = None    
    if len(sys.argv) < 2:
        parser.print_help() 
        sys.exit()
    else:                    
        args = parser.parse_args()
    
    options = {}
    options['enrich_imsi']      = args.enrich_imsi
    options['imsi_prefix']      = args.imsi_prefix
    options['enrich_datetime']  = args.enrich_datetime
    options['rop_start']        = args.rop_start
    options['rop_end']          = args.rop_end
    options['noenrich']         = args.noenrich    
    options['clean']            = args.clean
    options['nowait']           = args.nowait
    options['realtime']         = args.realtime
    options['logger_filename']  = args.logger_filename
    options['etc']              = args.etc[0:-1] if args.etc.endswith(os.sep) else args.etc
    options['logger_directory'] = args.logger_directory
    options['enrich_ggsnip']    = args.enrich_ggsnip
    
    if 'FATAL' == args.logger_level.upper():
        options['logger_level'] = logging.FATAL
    elif 'ERROR' == args.logger_level.upper():
        options['logger_level'] = logging.ERROR
    elif 'WARNING' == args.logger_level.upper():
        options['logger_level'] = logging.WARNING
    elif 'INFO' == args.logger_level.upper():
        options['logger_level'] = logging.INFO
    elif 'DEBUG' == args.logger_level.upper():
        options['logger_level'] = logging.DEBUG
    elif 'TRACE' == args.logger_level.upper():
        options['logger_level'] = datagen_logging.TRACE
        
    return options

def init_arg_parser():
    usage = 'datagen script <enrichment options>'
    description = 'Enrich data by modifying imsi/datetime'
    
    parser = argparse.ArgumentParser(usage=usage, description=description, formatter_class=argparse.ArgumentDefaultsHelpFormatter)
            
    parser.add_argument('--clean', dest='clean',  help='clean the output directory before generating data', action='store_true', default=True)    
    parser.add_argument('--realtime', dest='realtime', help='whether to generate data in realtime or not', action='store_true', default=True)
    parser.add_argument('--etc', metavar='', dest='etc', help='configuration file location', default='/opt/ericsson/dvtp_tool/etc')
    parser.add_argument('--logger_directory', metavar='', dest='logger_directory', help='log file location', default='/var/log/dvtp_tool')
    parser.add_argument('--logger_level', metavar='', dest='logger_level', help='log level', default='INFO', choices=('FATAL', 'ERROR', 'WARNING', 'INFO', 'DEBUG', 'TRACE'))
    parser.add_argument('--logger_filename', metavar='', dest='logger_filename', help='log filename', default='datagen.log')
        
    imsi_group = parser.add_argument_group(title='IMSI Enrichment Options')
    imsi_group.add_argument('--enrich-imsi', dest='enrich_imsi', action='store_true', default=True, help='set to enable imsi enrichment')
    imsi_group.add_argument('--imsi-prefix', dest='imsi_prefix', default='45406', help='target imsi to enrich, e.g., imsi starts with 45406', metavar='')
    
    datetime_group = parser.add_argument_group(title='Datetime Enrichment Options')
    datetime_group.add_argument('--enrich-datetime', dest='enrich_datetime', action='store_true', default=True, help='set to enable datetime enrichment')
    datetime_group.add_argument('--rop-start', dest='rop_start', help='the datetime of the first rop, this has to follow the format yyyymmddHHMM, e.g., 201205161600]', metavar='')
    datetime_group.add_argument('--rop-end', dest='rop_end', help='the datetime of the last rop, this has to follow the format yyyymmddHHMM, e.g., 201205161600]', metavar='')
	
    ggsnip_group = parser.add_argument_group(title='GGSNIP Enrichment Options')
    ggsnip_group.add_argument('--enrich-ggsnip', dest='enrich_ggsnip', action='store_true', default=True, help='set to enable ggsnip enrichment')
    
    debug_group = parser.add_argument_group(title='Debug Options')
    debug_group.add_argument('--noenrich', dest='noenrich', help='turn this flag on to disable data enrichment and generation', action='store_true', default=False)
    debug_group.add_argument('--nowait', dest='nowait', help='disable waiting time in realtime mode', action='store_true', default=False)
        
    return parser;

class Datagen():
    
    def __init__(self, config_file_path, options):            
        
        self.__running_processes = []
                
        try:
            config = ConfigParser()
            config.readfp(open(config_file_path))
                        
            self.__enable_classification                   = config.getboolean('common', 'enable_classification')
            self.__enable_classification_monitor           = config.getboolean('common', 'enable_classification_monitor')
            self.__enable_classification_validator_monitor = config.getboolean('common', 'enable_classification_validator_monitor')
            self.__enable_tcp_partial                      = config.getboolean('common', 'enable_tcp_partial')
            self.__enable_tcp_partial_monitor              = config.getboolean('common', 'enable_tcp_partial_monitor')
            self.__enable_tcp_partial_validator_monitor    = config.getboolean('common', 'enable_tcp_partial_validator_monitor')
            self.__enable_sgeh                             = config.getboolean('common', 'enable_sgeh')
            self.__enable_sgeh_monitor                     = config.getboolean('common', 'enable_mz_sgeh_monitor')
            self.__enable_sgeh_validator_monitor           = config.getboolean('common', 'enable_sgeh_validator_monitor')
            self.__enable_ggsn                             = config.getboolean('common', 'enable_ggsn')
            self.__enable_ggsn_monitor                     = config.getboolean('common', 'enable_mz_ggsn_monitor')
            self.__enable_ggsn_validator_monitor           = config.getboolean('common', 'enable_ggsn_validator_monitor')
            
            options['logger_external_config']              = config.get('common', 'logger_external_config')
                        
            ''' 
            only the logging_delegate will write logs into an external log file;
            all processes share the same logging delegate
            '''
            self.__logging_delegate = Process(name='LoggingDelegate',
                                              target=listener_process,
                                              args=(logging_queue, listener_configurer, options))
            self.__logging_delegate.start()
            
            logger_configurer(logging_queue)    
                
            datagen_logger.debug(options)
            
            self.__options = dict(options)   
            
                                        
            if config.has_option('common', 'enrich_imsi'):                
                new_value = config.getboolean('common', 'enrich_imsi')
                datagen_logger.info('find logger_directory in common.props file, replace existing value [%s] with [%s]', options['enrich_imsi'], new_value)                
                options['enrich_imsi'] = new_value
                
            if config.has_option('common', 'imsi_prefix'):
                new_value = config.get('common', 'imsi_prefix')
                datagen_logger.info('find imsi_prefix in common.props file, replace existing value [%s] with [%s]', options['imsi_prefix'], new_value)
                options['imsi_prefix'] = new_value
                
            if config.has_option('common', 'enrich_datetime'):
                new_value = config.getboolean('common', 'enrich_datetime')
                datagen_logger.info('find enrich_datetime in common.props file, replace existing value [%s] with [%s]', options['enrich_datetime'], new_value)
                options['enrich_datetime'] = new_value
                
            if config.has_option('common', 'clean'):
                new_value = config.getboolean('common', 'clean')
                datagen_logger.info('find clean in common.props file, replace existing value [%s] with [%s]', options['clean'], new_value)
                options['clean'] = new_value
                
            if config.has_option('common', 'logger_filename'):
                new_value = config.get('common', 'logger_filename')
                datagen_logger.info('find logger_filename in common.props file, replace existing value [%s] with [%s]', options['logger_filename'], new_value)
                options['logger_filename'] = new_value
                
            if config.has_option('common', 'logger_directory'):
                new_value = config.get('common', 'logger_directory')
                datagen_logger.info('find logger_directory in common.props file, replace existing value [%s] with [%s]', options['logger_directory'], new_value)
                options['logger_directory'] = new_value
                            
        except: 
            datagen_logger.exception(sys.stderr)
            sys.exit()
                
    def stop(self):                
        try:                 
            datagen_logger.info('%d running process(es)', len(self.__running_processes))
            while len(self.__running_processes) > 0:
                running_process = self.__running_processes.pop()            
                datagen_logger.info('terminating %s [%d]', running_process.name, running_process.pid)
                running_process.terminate()
                                            
            datagen_logger.info('terminating %s', self.__logging_delegate.name)
            subprocess.Popen(['kill', str(self.__logging_delegate.pid)]).wait()
        except:
            datagen_logger.exception(sys.stderr)
        finally:
            self.__datagen_server.running = False     
            datagen_logger.info('terminating %s', current_process().name)      
            subprocess.Popen(['kill', str(current_process().pid)]).wait()
                                                    
        
    def start(self):
        
        if self.__enable_tcp_partial:
            
            datagen_logger.info('tcp partial data generator enabled')            
            tcp_partial_generator = TcpPartialGenerator(self.__options)
            self.__running_processes.append(tcp_partial_generator)
            datagen_logger.info('start tcp partial generator %s', tcp_partial_generator.name)
            tcp_partial_generator.start()
                    
        if self.__enable_classification:
            
            datagen_logger.debug('classification data generator enabled')
            classification_generator = ClassificationGenerator(self.__options)
            self.__running_processes.append(classification_generator)
            datagen_logger.info('start classification generator %s', classification_generator.name)
            classification_generator.start()
        
        if self.__enable_sgeh:
            
            datagen_logger.info('sgeh data generator enabled')
            sgeh_generator = SgehGenerator(self.__options)
            self.__running_processes.append(sgeh_generator)
            datagen_logger.info('start sgeh generator %s', sgeh_generator.name)
            sgeh_generator.start()
            
        if self.__enable_ggsn:
            
            datagen_logger.info('ggsn data generator enabled')
            ggsn_generator = GgsnGenerator(self.__options)
            self.__running_processes.append(ggsn_generator)
            datagen_logger.info('start ggsn generator %s', ggsn_generator.name)
            ggsn_generator.start()
           
        if self.__enable_sgeh_monitor and self.__options['realtime']:
            datagen_logger.info('sgeh data monitor enabled')                           
            mz_sgeh_output_monitor = monitor.MZSgehOutputMonitor(self.__options)
            self.__running_processes.append(mz_sgeh_output_monitor)
            datagen_logger.info('start mz output monitor process %s', mz_sgeh_output_monitor.name)
            mz_sgeh_output_monitor.start()

        if self.__enable_ggsn_monitor and self.__options['realtime']:
            datagen_logger.info('ggsn data monitor enabled')
            mz_ggsn_output_monitor = monitor.MZGgsnOutputMonitor(self.__options)
            self.__running_processes.append(mz_ggsn_output_monitor)
            datagen_logger.info('start mz output monitor process %s', mz_ggsn_output_monitor.name)
            mz_ggsn_output_monitor.start()
        
        if self.__enable_tcp_partial_monitor:
            datagen_logger.info('tcp partial monitor enabled')            
            tcp_partial_output_monitor = monitor.TcpPartialOutputMonitor(self.__options)
            self.__running_processes.append(tcp_partial_output_monitor)
            datagen_logger.info('start tcp partial output monitor process %s', tcp_partial_output_monitor.name)
            tcp_partial_output_monitor.start()
            
        if self.__enable_classification_monitor:
            datagen_logger.info('classification monitor enabled')
            classification_output_monitor = monitor.ClassificationOutputMonitor(self.__options)
            self.__running_processes.append(classification_output_monitor)
            datagen_logger.info('start classification output monitor process %s', classification_output_monitor.name)
            classification_output_monitor.start()
            
        if self.__enable_sgeh_validator_monitor:
            datagen_logger.info('sgeh validator monitor enabled')
            sgeh_validator_monitor = SgehValidatorMointor(self.__options)
            self.__running_processes.append(sgeh_validator_monitor)
            datagen_logger.info('start sgeh validator monitor process %s', sgeh_validator_monitor.name)            
            sgeh_validator_monitor.start()
            
            
        if self.__enable_classification_validator_monitor:
            datagen_logger.info('classification validator monitor enabled')                
            classification_validator_monitor = ClassificationValidatorMointor(self.__options)
            self.__running_processes.append(classification_validator_monitor)
            datagen_logger.info('start classification validator monitor process %s', classification_validator_monitor.name)            
            classification_validator_monitor.start()
            
        if self.__enable_tcp_partial_validator_monitor:
            datagen_logger.info('tcp partial validator monitor enabled')                           
            tcp_partial_validator_monitor = TcpPartialValidatorMointor(self.__options)
            self.__running_processes.append(tcp_partial_validator_monitor)
            datagen_logger.info('start tcp partial validator monitor process %s', tcp_partial_validator_monitor.name)            
            tcp_partial_validator_monitor.start()            
            
        if self.__enable_ggsn_validator_monitor:
            datagen_logger.info('ggsn validator monitor enabled')            
            ggsn_validator_monitor = GgsnValidatorMonitor(self.__options)
            self.__running_processes.append(ggsn_validator_monitor)
            datagen_logger.info('start ggsn validator monitor process %s', ggsn_validator_monitor.name)            
            ggsn_validator_monitor.start()
        
        if self.__options['realtime']:
            # get ip address of the machine
            server_ip = socket.gethostbyname(socket.gethostname())
            self.__datagen_manager = DatagenManager(address=(server_ip, 39999), authkey='')
            DatagenManager.register('stop_datagen', self.stop)
            DatagenManager.register('status', callable=lambda:True)
            self.__datagen_server = self.__datagen_manager.get_server()
            self.__datagen_server.running = True
            self.__datagen_server.serve_until_stop()
            
def start_datagen():
    
    '''
     According to,
     http://stackoverflow.com/questions/2427240/thread-safe-equivalent-to-pythons-time-strptime,
     the following line is required, otherwise,
     the application will occasionally throw AttributeError exception     
    '''
    datetime.strptime('20121015', '%Y%m%d')
              
    if len(sys.argv) >= 2 and sys.argv[1] == 'stop':
        try:
            server_ip = None
            if len(sys.argv) > 2 and sys.argv[2] != None:
                server_ip = socket.gethostbyname(sys.argv[2])                
            else:
                server_ip = socket.gethostbyname(socket.gethostname())
            datagen_manager = DatagenManager(address=(server_ip, 39999), authkey='')
            DatagenManager.register('stop_datagen')
            try:
                t = Thread(target=datagen_manager.connect)
                t.start()
                t.join(2)
                if t.is_alive():
                    raise Exception()
            except:
                print('datagen is already stopped, executed at UTC time %s' % datetime.utcnow())
                sys.exit()
                  
            try:
                print('stopping datagen')
                datagen_manager.stop_datagen()
            except:
                # client will not receive any response, therefore, ignore the exception
                pass
            finally:
                print('datagen stopped, executed at UTC time %s' % datetime.utcnow())
        finally:            
            sys.exit()
            
    if len(sys.argv) >= 2 and sys.argv[1] == 'status':
        try:
            server_ip = None
            if len(sys.argv) > 2 and sys.argv[2] != None:
                server_ip = socket.gethostbyname(sys.argv[2])                
            else:
                server_ip = socket.gethostbyname(socket.gethostname())
            datagen_manager = DatagenManager(address=(server_ip, 39999), authkey='')
            
            DatagenManager.register('status')
            try:
                t = Thread(target=datagen_manager.connect)
                t.start()
                t.join(2)
                if t.is_alive():
                    raise Exception()
            except:
                print(sys.stderr)
                print(traceback.print_exc(sys.stderr))
                sys.exit()
                
            try:
                print('checking datagen status')
                status = datagen_manager.status()
                if status:
                    print('datagen is running, executed at UTC time %s' % datetime.utcnow())                 
            except:
                print(sys.stderr)
                print(traceback.print_exc(sys.stderr))
        finally:
            sys.exit()
            
    options = get_setup_options()
    
        
    # TODO the value should be set dynamically
    options['enriched_imsi_prefix'] = 11101
        
    datagen = Datagen(options['etc']+os.sep+'common.props', options)
    datagen.start()

if __name__ == '__main__':
    start_datagen()
    

