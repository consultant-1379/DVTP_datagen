'''
Created on 21 Nov 2012

@author: epstvxj
'''
import time
from datagen.settings import datagen_logger

def timer(f):
    def record_time(*args):
        start_time = time.time()
        f(*args)
        end_time = time.time()      
        
        if end_time-start_time > 60:                
            datagen_logger.trace('function %s spent %.2f minute(s)' % (f.__name__, (end_time-start_time)/60.0))
        else:
            datagen_logger.trace('function %s spent %.2f seconds(s)' % (f.__name__, (end_time-start_time)))            
    return record_time
    