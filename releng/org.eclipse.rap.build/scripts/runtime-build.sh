#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

######################################################################
# set up environment

export MVN=${MVN:-"/opt/public/common/apache-maven-3.2.1/bin/mvn"}

export MAVEN_LOCAL_REPO_PATH=${MAVEN_LOCAL_REPO_PATH:-"/shared/rt/rap/m2/repository"}
echo "Local Maven repository location: ${MAVEN_LOCAL_REPO_PATH}"

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  SIGNPROFILE="-Peclipse-sign -Dmaven.test.skip=true"
else
  SIGNPROFILE=""
fi

basedirectory="$WORKSPACE/org.eclipse.rap"
tempdirectory="$WORKSPACE/tmp"

######################################################################
# clean up local Maven repository to circumvent p2 cache problems

for II in .cache .meta p2 ; do
  echo "Remove directory ${MAVEN_LOCAL_REPO_PATH}/${II}" 
  rm -r ${MAVEN_LOCAL_REPO_PATH}/${II}
done

######################################################################
# build RAP Runtime

cd "$basedirectory"
echo "Running maven on $PWD, $SIGNPROFILE"
mkdir -p "$tempdirectory"
${MVN} -e clean package $SIGNPROFILE -Dmaven.repo.local=${MAVEN_LOCAL_REPO_PATH} -Djava.io.tmpdir="$tempdirectory"
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

######################################################################
# rename ZIP archive

repoDirectory="$basedirectory"/releng/org.eclipse.rap.build/repository.luna/target/repository
VERSION=$(ls "$repoDirectory"/features/org.eclipse.rap.sdk.feature_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
echo "Version is $VERSION"
test -n "$VERSION" || exit 1
TIMESTAMP=$(ls "$repoDirectory"/features/org.eclipse.rap.sdk.feature_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Timestamp is $TIMESTAMP"
test -n "$TIMESTAMP" || exit 1

# Example: rap-1.5.0-N-20110814-2110.zip
zipFileName=rap-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
zipFileNameMars=rap-mars-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip

mv "$basedirectory"/releng/org.eclipse.rap.build/repository.luna/target/*.zip "$WORKSPACE/$zipFileName" || exit 1
mv "$basedirectory"/releng/org.eclipse.rap.build/repository.mars/target/*.zip "$WORKSPACE/$zipFileNameMars" || exit 1
