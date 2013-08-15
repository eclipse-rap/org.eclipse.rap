#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

######################################################################
# set up environment

SCRIPTS_DIR=$(dirname $(readlink -nm $0))
. $SCRIPTS_DIR/build-environment.sh

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  sign=true
  SIGNPROFILE="-Peclipse-sign -Dmaven.test.skip=true"
else
  sign=false
  SIGNPROFILE=""
fi

basedirectory="$WORKSPACE/org.eclipse.rap/releng/org.eclipse.rap.build"

######################################################################
# clean up WORKSPACE
rm -r ${WORKSPACE}/rap*.zip

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
${MVN} -e clean package $SIGNPROFILE -Dmaven.repo.local=${MAVEN_LOCAL_REPO_PATH}
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

######################################################################
# rename ZIP archive

repoDirectory="$basedirectory"/repository/target/repository
VERSION=$(ls "$repoDirectory"/features/org.eclipse.rap.sdk.feature_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
echo "Version is $VERSION"
test -n "$VERSION" || exit 1
TIMESTAMP=$(ls "$repoDirectory"/features/org.eclipse.rap.sdk.feature_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Timestamp is $TIMESTAMP"
test -n "$TIMESTAMP" || exit 1

# Example: rap-1.5.0-N-20110814-2110.zip
zipFileName=rap-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
zipFileNameLuna=rap-luna-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip

mv repository.kepler/target/*.zip "$WORKSPACE/$zipFileName" || exit 1
mv repository.luna/target/*.zip "$WORKSPACE/$zipFileNameLuna" || exit 1

######################################################################
# include legal files in zip files

cd "$WORKSPACE"
cp -f org.eclipse.rap/releng/org.eclipse.rap.build/legal/notice.html .
cp -f org.eclipse.rap/releng/org.eclipse.rap.build/legal/epl-v10.html .
zip "$zipFileName" notice.html epl-v10.html
zip "$zipFileNameLuna" notice.html epl-v10.html
