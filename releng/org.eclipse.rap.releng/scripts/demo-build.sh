#!/bin/bash
#
# This script is used to trigger the demo build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

SCRIPTS_DIR=$(dirname $(readlink -nm $0))
. $SCRIPTS_DIR/build-environment.sh

if [ -z "$CVS_TAG" ]; then
  echo CVS_TAG is not set
  exit 1
fi

# Cleanup workspace dir
test -n "$WORKSPACE" -a -d "$WORKSPACE" && rm -rf "$WORKSPACE"/*

######################################################################
# Checkout Repository

cd "$WORKSPACE"
echo "checking out $CVS_TAG"
cvs -Q -d :local:/cvsroot/rt co -P -d source -r $CVS_TAG org.eclipse.rap || exit 1

######################################################################
# Build RAP Demo WAR

cd "$WORKSPACE/source/releng/org.eclipse.rap.releng/demo-war" || exit 1
echo "Running maven on $PWD"
$MVN clean image || exit 1

######################################################################
# Include legal files in zip

cd "$WORKSPACE/"
cp -f source/releng/org.eclipse.rap.releng/legal/notice.html .
cp -f source/releng/org.eclipse.rap.releng/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html

######################################################################
# Copy build artifacts

TARGET_DIR=/shared/rt/rap/last-stable/rap-tooling

mkdir -p "$TARGET_DIR"
rm -f "$TARGET_DIR"/*.zip
test -e "$WORKSPACE/$zipFileName" && cp "$WORKSPACE/$zipFileName" "$TARGET_DIR"
true
