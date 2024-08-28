'''
Created on 22 Nov 2012

@author: epstvxj
'''
import sys
import os
import glob

def uninstall():
    package_dir = ''.join((sys.prefix, os.sep, 'lib', os.sep, 'python',str(sys.version_info[0]), '.', str(sys.version_info[1]), os.sep, 'site-packages'))
    print('python site-packages directory location %s' % package_dir)
    info_file = glob.glob(package_dir+'3G_Session_Browser_Datagen-*.egg-info')
    if info_file:
        print('deleting info file %s' % info_file)
        os.remove(info_file)
    
    datagen_dir = ''.join((package_dir, os.sep, 'datagen'))
    if os.path.exists(datagen_dir):
        print('remove datagen package from %s' % datagen_dir)
        os.remove(datagen_dir)
        
if __name__ == '__main__':
    uninstall()
    