#!/usr/bin/bash
STARTSTOP_LOG="/var/log/dvtp_tool/datagen-startstop.log"
python -m datagen.__main__ stop >> ${STARTSTOP_LOG} 2>&1