'''
Created on 26 Oct 2012

@author: epstvxj

'''

from logging import Logger, NOTSET
import logging
import multiprocessing

TRACE  = 5

logging_queue = multiprocessing.Queue(-1)

class DatagenLogger(Logger):
    '''
    classdocs
    '''
    
    def __init__(self, name='datagen', level=logging.NOTSET):
        '''
        Constructor
        '''  
        Logger.__init__(self, name, level)
        logging.addLevelName(TRACE, 'TRACE')             
        
    def trace(self, msg, *args, **kwargs):
        self.log(TRACE, msg, *args, **kwargs)
        
    def isTraceEnabled(self):
        return self.isEnabledFor(TRACE)
        
class QueueHandler(logging.Handler):
    """
    This is a logging handler which sends events to a multiprocessing queue.
    """

    def __init__(self, queue):
        """
        Initialise an instance, using the passed queue.
        """
        logging.Handler.__init__(self)
        self.queue = queue
        
    def emit(self, record):
        """
        Emit a record.

        Writes the LogRecord to the queue.
        """
        try:
            ei = record.exc_info
            if ei:
                dummy = self.format(record) # just to get traceback text into record.exc_text
                record.exc_info = None  # not needed any more
            self.queue.put_nowait(record)
        except (KeyboardInterrupt, SystemExit):
            raise
        except:
            self.handleError(record)
            
