#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

SCRIPTS_DIR=$(dirname $(readlink -nm $0))
. $SCRIPTS_DIR/build-environment.sh

if [ -z "$CVS_TAG" ]; then
  echo CVS_TAG is not set
  exit 1
fi

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  sign=true
else
  sign=false
fi

# Cleanup workspace dir
test -n "$WORKSPACE" -a -d "$WORKSPACE" && rm -rf "$WORKSPACE"/*

######################################################################
# Checkout Repository

cd "$WORKSPACE"
echo "checking out $CVS_TAG"
cvs -Q -d :local:/cvsroot/rt co -P -d source -r $CVS_TAG org.eclipse.rap || exit 1

######################################################################
# Build RAP Runtime

cd "$WORKSPACE/source/releng/org.eclipse.rap.releng/runtime"
echo "Running maven on $PWD, sign=$sign"
$MVN -e clean package -Dsign=$sign
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

if [ -d runtime-repository/target/fixedPacked ]; then
  mv runtime-repository/target/fixedPacked "$WORKSPACE/runtimeRepo"
else
  mv runtime-repository/target/repository "$WORKSPACE/runtimeRepo"
fi

VERSION=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.runtime_1.5.0.*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.runtime_1.5.0.*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Version is $VERSION"
echo "Timestamp is $TIMESTAMP"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

######################################################################
# Build Aggregation Repository

cd "$WORKSPACE/source/releng/org.eclipse.rap.releng/runtime"
echo "Running maven with pom-aggregation.xml on $PWD, sign=$sign"
$MVN -e -f pom-aggregation.xml clean package -DruntimeRepo="file://$WORKSPACE/runtimeRepo" || exit 1

# Example: rap-runtime-1.5.0-N-20110814-2110.zip
zipFileName=rap-runtime-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
compatZipFileName=rap-runtime-compatibility-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip

mv runtime-aggregation-repository/target/*.zip "$WORKSPACE/$zipFileName" || exit 1

if [ "$sign" == "true" -a -d runtime-compatibility-repository/target ]; then
  mv runtime-compatibility-repository/target/*.zip "$WORKSPACE/$compatZipFileName" || exit 1
fi

######################################################################
# Include legal files in zip

cd "$WORKSPACE"
cp -f source/releng/org.eclipse.rap.releng/legal/notice.html .
cp -f source/releng/org.eclipse.rap.releng/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html
