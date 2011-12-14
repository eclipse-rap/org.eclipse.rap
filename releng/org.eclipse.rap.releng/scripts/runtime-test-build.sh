#!/bin/bash
#

if [ "${BUILD_TYPE:0:1}" == "I" -o "${BUILD_TYPE:0:1}" == "M" -o "${BUILD_TYPE:0:1}" == "R" ]; then
  sign=true
else
  sign=false
fi

cd "$WORKSPACE/source/releng/org.eclipse.rap.releng/runtime"
echo "Running maven on $PWD, sign=$sign"
$MVN -e clean package -Dsign=$sign
exitcode=$?
if [ "$exitcode" != "0" ]; then
  echo "Maven exited with error code " + $exitcode
fi

VERSION=$(ls runtime-repository/target/repository/features/org.eclipse.rap.runtime_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls runtime-repository/target/repository/features/org.eclipse.rap.runtime_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')

echo "Version is $VERSION"
echo "Timestamp is $TIMESTAMP"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

# Example: rap-runtime-1.5.0-N-20110814-2110.zip
zipFileName=rap-runtime-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
if [ -d runtime-repository/target/fixedSigned ]; then
  cd runtime-repository/target/fixedSigned
  zip -r "$WORKSPACE/$zipFileName" .
  zip -d "$WORKSPACE/$zipFileName" "META-INF/*"
  cd -
else
  mv runtime-repository/target/*.zip "$WORKSPACE/$zipFileName"
fi

repoZipFileName=rap-runtime-repo-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip
if [ -d runtime-repository/target/fixedPacked ]; then
  cd runtime-repository/target/fixedPacked
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

######################################################################
# Copy build artifacts

saveArtifacts "$WORKSPACE/$zipFileName" "$WORKSPACE/$repoZipFileName"
