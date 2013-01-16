#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

SCRIPTS_DIR=$(dirname $(readlink -nm $0))
. $SCRIPTS_DIR/build-environment.sh

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  sign=true
  SIGNPROFILE="-Peclipse-sign -Dmaven.test.skip=true"
else
  sign=false
  SIGNPROFILE=""
fi

######################################################################
# Cleanup left-overs from previous run
test -d "$WORKSPACE" || exit 1
rm -rf "$WORKSPACE"/runtimeRepo "$WORKSPACE"/*.zip

######################################################################
# clean up local Maven repository to circumvent p2 cache problems
for II in .cache .meta p2 ; do
  echo "Remove directory ${MAVEN_LOCAL_REPO_PATH}/${II}" 
  rm -r ${MAVEN_LOCAL_REPO_PATH}/${II}
done

######################################################################
# Build RAP Runtime

cd "$WORKSPACE/org.eclipse.rap/releng/org.eclipse.rap.build"
echo "Running maven on $PWD, $SIGNPROFILE"
${MVN} -e clean package $SIGNPROFILE -Dmaven.repo.local=${MAVEN_LOCAL_REPO_PATH}
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

if [ -d repository/target/fixedPacked ]; then
  mv repository/target/fixedPacked "$WORKSPACE/runtimeRepo" || exit 1
else
  mv repository/target/repository "$WORKSPACE/runtimeRepo" || exit 1
fi

VERSION=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.feature_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls "$WORKSPACE"/runtimeRepo/features/org.eclipse.rap.feature_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Version is $VERSION"
echo "Timestamp is $TIMESTAMP"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

######################################################################
# Build Aggregation Repository

cd "$WORKSPACE/org.eclipse.rap/releng/org.eclipse.rap.target.build"
echo "Running maven on $PWD, sign=$sign"
$MVN -e clean package -DruntimeRepo="file://$WORKSPACE/runtimeRepo" -Dmaven.repo.local=${MAVEN_LOCAL_REPO_PATH} -Dsign=$sign || exit 1

# Example: rap-1.5.0-N-20110814-2110.zip
zipFileName=rap-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
junoZipFileName=rap-juno-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip

if [ -d repository.juno/target/fixedSigned ]; then
  mv repository.juno/target/fixedSigned/*.zip "$WORKSPACE/$junoZipFileName" || exit 1
else
  mv repository.juno/target/*.zip "$WORKSPACE/$junoZipFileName" || exit 1
fi

if [ -d repository.kepler/target/fixedSigned ]; then
  mv repository.kepler/target/fixedSigned/*.zip "$WORKSPACE/$zipFileName" || exit 1
else
  mv repository.kepler/target/*.zip "$WORKSPACE/$zipFileName" || exit 1
fi

######################################################################
# Include legal files in zip files

cd "$WORKSPACE"
cp -f org.eclipse.rap/releng/org.eclipse.rap.build/legal/notice.html .
cp -f org.eclipse.rap/releng/org.eclipse.rap.build/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html
zip "$junoZipFileName" notice.html epl-v10.html
