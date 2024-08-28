'''
Created on Sep 10, 2012

@author: epstvxj
'''
from datagen.shared.datagen_logging import DatagenLogger, QueueHandler, TRACE
from logging import StreamHandler
import logging
from logging.config import fileConfig

datagen_logger = DatagenLogger()

def listener_process(queue, configurer, options):
    logger = configurer(options)
    while True:
        try:
            record = queue.get()
            if logger.isEnabledFor(record.levelno):
                logger.handle(record) 
        except (KeyboardInterrupt, SystemExit):
            raise
        except:
            import sys, traceback
            print(sys.stderr)            
            traceback.print_exc(file=sys.stderr)
            
def logger_configurer(queue):  
    handler = QueueHandler(queue)    
    datagen_logger.addHandler(handler)

def listener_configurer(options):
    logging.setLoggerClass(DatagenLogger)
    fileConfig(options['logger_external_config'])
    return logging.getLogger("DatagenFileLogger")    
        
def init_console(debug_level):  
    formatter = logging.Formatter(fmt='%(asctime)-8s, %(threadName)s, %(processName)s, %(module)s, <Line %(lineno)s in %(module)s> [%(levelname)-4s] %(message)s',
                                  datefmt='%m-%d %H:%M:%S')
    handler = StreamHandler()
    handler.setFormatter(formatter)
    datagen_logger.addHandler(handler)
    datagen_logger.setLevel(debug_level)
    
def deprecated(func):
    """This is a decorator which can be used to mark functions
    as deprecated. It will result in a warning being emmitted
    when the function is used."""
    def newFunc(*args, **kwargs):
        datagen_logger.warn("Call to deprecated function %s." % func.__name__,
                      category=DeprecationWarning)
        return func(*args, **kwargs)
    newFunc.__name__ = func.__name__
    newFunc.__doc__ = func.__doc__
    newFunc.__dict__.update(func.__dict__)
    return newFunc


