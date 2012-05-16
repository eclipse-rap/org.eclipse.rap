#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

SCRIPTS_DIR=$(dirname $(readlink -nm $0))
. $SCRIPTS_DIR/build-environment.sh

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  sign=true
else
  sign=false
fi


######################################################################
# Cleanup left-overs from previous run

test -d "$WORKSPACE" || exit 1
rm -rf "$WORKSPACE/runtimeRepo" "$WORKSPACE/*.zip"

######################################################################
# Build RAP Runtime

cd "$WORKSPACE/org.eclipse.rap/releng/org.eclipse.rap.releng"
echo "Running maven on $PWD, sign=$sign"
$MVN -e clean package -Dsign=$sign
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

if [ -d repository/target/fixedPacked ]; then
  mv repository/target/fixedPacked "$WORKSPACE/runtimeRepo"
else
  mv repository/target/repository "$WORKSPACE/runtimeRepo"
fi

VERSION=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.runtime_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.runtime_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Version is $VERSION"
echo "Timestamp is $TIMESTAMP"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

######################################################################
# Build Aggregation Repository

cd "$WORKSPACE/org.eclipse.rap/releng/org.eclipse.rap.target.releng"
echo "Running maven on $PWD, sign=$sign"
$MVN -e clean package -DruntimeRepo="file://$WORKSPACE/runtimeRepo" || exit 1

# Example: rap-runtime-1.5.0-N-20110814-2110.zip
zipFileName=rap-runtime-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
compatZipFileName=rap-runtime-compatibility-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip

mv repository/target/*.zip "$WORKSPACE/$zipFileName" || exit 1

if [ "$sign" == "true" -a -d compatibility-repository/target ]; then
  mv compatibility-repository/target/*.zip "$WORKSPACE/$compatZipFileName" || exit 1
fi

######################################################################
# Include legal files in zip

cd "$WORKSPACE"
cp -f org.eclipse.rap/releng/org.eclipse.rap.releng/legal/notice.html .
cp -f org.eclipse.rap/releng/org.eclipse.rap.releng/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html
