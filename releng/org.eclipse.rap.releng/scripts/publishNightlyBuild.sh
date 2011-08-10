#!/bin/bash
#
#  Copyright (c) 2011 Innoopract Informationssysteme GmbH.
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
# 
#  Contributors:
#      Innoopract Informationssysteme GmbH - initial API and implementation
###############################################################################
#
# Usage: publishNightlyBuild.sh (tooling|runtime)

TMP_DIR=/shared/rt/rap/tmp
JOB_DIR=/shared/jobs
NIGHTLY_DIR=/home/data/httpd/download.eclipse.org/rt/rap/nightly
SCRIPTS_DIR=$(dirname $(readlink -nm $0))
# Set bash's internal file separator to \n to avoid problems with filenames that contain spaces
IFS="
"

# Read build type from command line
buildType=$1
if [ -z "$buildType" ]; then
  echo >&2 "Missing build type parameter"
  echo >&2 "Usage: $0 (tooling|runtime)"
  exit 1
fi

# Check runtime dir
if [ ! -d $RUNTIME_DIR/plugins ]; then
  echo >&2 "Missing or invalid runtime dir: $RUNTIME_DIR"
  exit 1
fi

# Check job dir
jobDir=$JOB_DIR/rap-$buildType
if [ ! -d $jobDir ]; then
  echo >&2 "Missing job dir: $jobDir"
  exit 1
fi

# Check target main dir
targetMainDir=$NIGHTLY_DIR/$buildType
if [ ! -d $targetMainDir ]; then
  echo >&2 "Missing target main dir: $targetMainDir"
  exit 1
fi

echo "Publishing nightly build"
echo "========================"

echo "Build type: $buildType"

latestStableBuild=`ls -1 $jobDir/lastStable/archive/*/*.zip`
echo "Latest build: $latestStableBuild"

zipFileName=`basename $latestStableBuild`

# Check that this is a nightly build
if [ "$zipFileName" == "${zipFileName##*-N-}" ]; then
  echo >&2 "Not a nightly build, exiting"
  exit 0
fi
timeStamp=${zipFileName##*-N-}
timeStamp=${timeStamp%.zip}

# Determine and check target directory
targetDir=$targetMainDir/$timeStamp
echo "Target directory: $targetDir"

if [ -e $targetDir ]; then
  echo "Target directory exists already. Nothing to do, exiting"
  exit 0
fi

# Determine and create working directory
workingDir=$TMP_DIR/nightly-$buildType-$timeStamp
echo "Working directory: $workingDir"

mkdir -p $workingDir || exit 1
cd $workingDir || exit 1

# Copy last stable build 
echo "Copy latest stable build"
cp $latestStableBuild . || exit 1

# Unzip artifact
echo "Uncompress latest stable build"
unzip -q $zipFileName || exit 1

mkdir -p $targetDir || exit 1

# Publish p2 repository
launcher=$RUNTIME_DIR/plugins/org.eclipse.equinox.launcher_*.jar
echo "Start to generate p2 repository"
java -jar $launcher \
   -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
   -metadataRepository file:$targetDir \
   -artifactRepository file:$targetDir \
   -source $workingDir/eclipse \
   -configs gtk.linux.x86 \
   -reusePackedFiles \
   -compress \
   -publishArtifacts || exit 1
echo "Finished generating a p2 repository"

# Generate category.xml
echo '<?xml version="1.0" encoding="UTF-8"?>' > category.xml
echo '<site>' >> category.xml
echo '<category-def name="org.eclipse.rap.category" label="Rich Ajax Platform (RAP)"/>' >> category.xml
ls -1 $targetDir/features/*.jar | sed 's/^.*\/\([^_]*\)_\(.*\)\.jar$/<feature url="features\/\1_\2.jar" id="\1" version="\2">\n<category name="org.eclipse.rap.category"\/>\n<\/feature>/' >> category.xml
echo '</site>' >> category.xml

# Add category to p2 directory
java -cp $launcher org.eclipse.core.launcher.Main \
   -consolelog \
   -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
   -metadataRepository file:$targetDir \
   -categoryDefinition file:$workingDir/category.xml \
   -compress || exit 1

# Add to composite repository
$SCRIPTS_DIR/repo-tool.sh $targetMainDir add $timeStamp || exit 1

# TODO Delete old directories
i=0
for dir in `ls -r -1 $targetMainDir`; do
  if [ $i -ge 3 -a -d $targetMainDir/$dir ]; then
    echo "Removing outdated $dir"
    $SCRIPTS_DIR/repo-tool.sh $targetMainDir remove $dir || exit 1
    rm -r $targetMainDir/$dir || exit 1
  fi;
  let i=i+1;
done

# Remove working directory
echo "Clean up work directory"
cd ..
rm -rf $workingDir

echo "done"
echo

