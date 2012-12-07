#!/bin/bash

# This script is run by rsternber's cron

DATE=`date +%Y%m%d-%H%M`
RUNTIME_LOG=/shared/rt/rap/log/nightly-runtime-$DATE.log
TOOLS_LOG=/shared/rt/rap/log/nightly-tools-$DATE.log

export ECLIPSE_DIR=/shared/rt/rap/build-runtimes/eclipse

/shared/rt/rap/scripts/publish-nightly-build.sh runtime > $RUNTIME_LOG
test $? -eq 0 || echo >&2 FAILED. See $RUNTIME_LOG

/shared/rt/rap/scripts/publish-nightly-build.sh tools > $TOOLS_LOG
test $? -eq 0 || echo >&2 FAILED. See $TOOLS_LOG

