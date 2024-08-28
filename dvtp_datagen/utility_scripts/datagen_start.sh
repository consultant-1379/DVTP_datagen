#!/usr/bin/bash
OUTPUT_LOG="/var/log/dvtp_tool/datagen-output.log"
exec python -m datagen.__main__ --etc=/opt/ericsson/dvtp_tool/etc > ${OUTPUT_LOG} 2>&1 &