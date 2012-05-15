#!/bin/bash
#
# This script is used to trigger the tooling build with parameters passed by Hudson.
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
# Generate reference documentation

cd "$WORKSPACE/source"
echo "Generating reference documentation"
$SCRIPTS_DIR/ant-runner.sh releng/org.eclipse.rap.releng/tooling/reference/build.xml \
  -DsourceDir="$WORKSPACE/source" || exit 1

######################################################################
# Build RAP Tooling

cd "$WORKSPACE/source/releng/org.eclipse.rap.releng/tooling" || exit 1
echo "Running maven on $PWD, sign=$sign"
$MVN clean package -Dsign=$sign || exit 1

VERSION=$(ls tooling-repository/target/repository/features/org.eclipse.rap.tooling_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls tooling-repository/target/repository/features/org.eclipse.rap.tooling_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Version is '$VERSION'"
echo "Timestamp is '$TIMESTAMP'"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

# Example: rap-tooling-1.5.0-N-20110814-2110.zip
zipFileName=rap-tooling-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
if [ -d tooling-repository/target/fixedSigned ]; then
  cd tooling-repository/target/fixedSigned
  zip -r "$WORKSPACE/$zipFileName" .
  zip -d "$WORKSPACE/$zipFileName" "META-INF/*"
  cd -
else
  mv tooling-repository/target/*.zip "$WORKSPACE/$zipFileName"
fi

repoZipFileName=rap-tooling-repo-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
if [ -d tooling-repository/target/fixedPacked ]; then
  cd tooling-repository/target/fixedPacked
  zip -r "$WORKSPACE/$repoZipFileName" .
  zip -d "$WORKSPACE/$repoZipFileName" "META-INF/*"
  cd -
fi

######################################################################
# Include legal files in zip

cd "$WORKSPACE"
cp -f source/releng/org.eclipse.rap.releng/legal/notice.html .
cp -f source/releng/org.eclipse.rap.releng/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html
