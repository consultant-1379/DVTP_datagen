'''
Created on 15 Sep 2012

@author: epstvxj
'''
#!/usr/bin/env python

from distutils.core import setup

setup(packages=['datagen', 'datagen.sgeh_datagen', 'datagen.up_datagen',
                'datagen.ggsn_datagen', 'datagen.lib', 'datagen.validate', 'datagen.shared'],
      package_dir={'datagen':'py_script/datagen'},
      name='dvtp_tool',
      version='1.0',
      description='3G Session Browser Datagen Distribution',  
      maintainer='epstvxj',
      maintainer_email='jun.l.liu@ericsson.com',
     )