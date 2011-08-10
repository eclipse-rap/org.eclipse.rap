#!/bin/bash

# This script is run by rsternber's cron

DATE=`date +%Y%m%d-%H%M`
RUNTIME_LOG=/shared/rt/rap/log/nightly-runtime-$DATE.log
TOOLING_LOG=/shared/rt/rap/log/nightly-tooling-$DATE.log

export RUNTIME_DIR=/shared/rt/rap/build-runtimes/eclipse-3.6.1

/shared/rt/rap/scripts/1.5/publishNightlyBuild.sh runtime > $RUNTIME_LOG
test $? -eq 0 || echo >&2 FAILED. See $RUNTIME_LOG

/shared/rt/rap/scripts/1.5/publishNightlyBuild.sh tooling > $TOOLING_LOG
test $? -eq 0 || echo >&2 FAILED. See $TOOLING_LOG

