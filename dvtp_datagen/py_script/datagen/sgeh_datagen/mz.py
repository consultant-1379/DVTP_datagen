'''
Created on Aug 23, 2012

@author: epstvxj
'''
import subprocess
from datagen.settings import datagen_logger

        
def remove_wf_from_wg(mzsh, mz_account, workgroup, workflow):
    datagen_logger.debug('remove workflow [%s] from workgroup [%s]', workflow, workgroup)
    cmd = [mzsh, mz_account, 'wfgroupremovewf', workgroup, workflow]
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')
        
def add_wf_into_wg(mzsh, mz_account, workgroup, workflow):
    datagen_logger.debug('add workflow [%s] into workgroup [%s]', workflow, workgroup)
    cmd = [mzsh, mz_account, 'wfgroupaddwf', workgroup, workflow]
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')
    
def get_number_of_workflows(mzsh, mz_account, workflow_name_pattern_expr):
    cmd = [mzsh, mz_account, 'wflist', workflow_name_pattern_expr]
    datagen_logger.info('count number of workflows matching expression %s', workflow_name_pattern_expr)
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')
    return len(stdout_value.split('\n'))-1

def stopwf(mzsh, mz_account, workflow_name_pattern_expr):
    cmd = [mzsh, mz_account, 'wfstop', '-immediate', workflow_name_pattern_expr]
    datagen_logger.debug('stop workflow [%s]', workflow_name_pattern_expr)
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')

def enablewf(mzsh, mz_account, workflow_name):
    cmd = [mzsh, mz_account, 'wfenable', workflow_name]
    datagen_logger.debug('enable workflow [%s]', workflow_name)
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')
        
def disablewf(mzsh, mz_account, workflow_name):
    cmd = [mzsh, mz_account, 'wfdisable', workflow_name]
    datagen_logger.debug('disable workflow [%s]', workflow_name)
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')

def startwg(mzsh, mz_account, workgroup_name):
    
    if not is_wg_enabled(mzsh, mz_account, workgroup_name):
        datagen_logger.info('workgroup %s is not enabled', workgroup_name)
        datagen_logger.info('enable workgroup %s', workgroup_name)
        enable_wg(mzsh, mz_account, workgroup_name)
    
    cmd = [mzsh, mz_account, 'wfgroupstart', workgroup_name]
    datagen_logger.info('Attempting to Start WorkGroup %s', workgroup_name)
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value+'\n')
# end startwg()

def is_wg_active(mzsh, mz_account, workgroup_name):
    cmd = [mzsh, mz_account, 'wfgrouplist', workgroup_name, '-active']
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if workgroup_name in stdout_value:
        return True
    else:
        return False
# end is_wg_active()

def is_wg_enabled(mzsh, mz_account, workgroup_name):
    cmd = [mzsh, mz_account, 'wfgrouplist', workgroup_name, '-mode', 'E']
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if 'E' in stdout_value:
        return True
    else:
        return False
# end is_wg_enabled()
    
def disable_wg(mzsh, mz_account, workgroup_name):
    cmd = [mzsh, mz_account, 'wfgroupdisable', workgroup_name]
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    datagen_logger.trace(stdout_value)
# end disable_wg()
    
def enable_wg(mzsh, mz_account, workgroup_name):
    cmd = [mzsh, mz_account, 'wfgroupenable', workgroup_name]
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    datagen_logger.trace(stdout_value)
# end enable_wg()

def check_wg_status(mzsh, mz_account, workgroup_name):
    
    cmd = [mzsh, mz_account, "wfgrouplist", workgroup_name]
     
    datagen_logger.info("check workgroup status %s", workgroup_name)

    proc = subprocess.Popen(cmd, shell=False, stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    datagen_logger.trace(stdout_value)
        
        
def stop_wg(mzsh, mz_account, workgroup_name):
    datagen_logger.info("stop workgroup %s", workgroup_name)
    cmd = [mzsh, mz_account, 'wfgroupstop', workgroup_name]
    proc = subprocess.Popen(cmd,shell=False,stdout=subprocess.PIPE)
    stdout_value = proc.communicate()[0]
    if datagen_logger.isTraceEnabled():
        datagen_logger.trace(stdout_value)
    
            