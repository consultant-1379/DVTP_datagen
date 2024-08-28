#!/usr/bin/bash
STARTSTOP_LOG="/var/log/dvtp_tool/datagen-startstop.log"
python -m datagen.__main__ status >> ${STARTSTOP_LOG} 2>&1 