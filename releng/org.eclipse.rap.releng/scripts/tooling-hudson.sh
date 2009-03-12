#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized
#

# Cleanup workspace dir
rm -rf "$WORKSPACE"/*

runtimeDir=/projects/rapbuild/build-runtime/3.4.1/

# detect latest runtime target archive
rapTargets="/home/benny/build-env/"
latestTarget=$rapTargets/`find $rapTargets -iname '*target*.zip' -printf "%A@;%f\n" | sort | tail -n 1 | cut -f2 -d";"`

./build-common.sh \
  --cvs-tag "$CVS_TAG" \
  --build-type "$BUILD_TYPE" \
  --work "$WORKSPACE" \
  --runtime "$runtimeDir" \
  --base-platform "$PLATFORM_DIR" \
  --builder "org.eclipse.rap/releng/org.eclipse.rap.releng.tooling" \
  --rap-target "$latestTarget"