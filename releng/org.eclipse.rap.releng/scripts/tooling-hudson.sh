#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized
#

# Cleanup workspace dir
rm -rf "$WORKSPACE"/*

runtimeDir=/home/build/rap/build-runtime/eclipse-3.4.1
rapTargets="/home/build/.hudson/jobs/RAP Runtime/lastSuccessful/archive"

scriptsDir=`dirname $0`

# detect latest runtime target archive
latestTargetQualifier=`find "$rapTargets" -name '*runtime*.zip' -printf "%f\n" | cut -f5,6 -d"-" | sort | tail -n 1`
latestTarget=`find "$rapTargets" -name *${latestTargetQualifier}`

$scriptsDir/build-common.sh \
  --cvs-tag "$CVS_TAG" \
  --build-type "$BUILD_TYPE" \
  --work "$WORKSPACE" \
  --runtime "$runtimeDir" \
  --base-platform "$PLATFORM_DIR" \
  --builder "org.eclipse.rap/releng/org.eclipse.rap.releng/tooling" \
  --rap-target "$latestTarget"
