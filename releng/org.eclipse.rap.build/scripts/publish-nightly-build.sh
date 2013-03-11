#!/bin/bash
#
#  Copyright (c) 2011, 2013 Innoopract Informationssysteme GmbH and others.
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
# 
#  Contributors:
#     Innoopract Informationssysteme GmbH - initial API and implementation
#     EclipseSource - ongoing development
###############################################################################
#
# Usage: publishNightlyBuild.sh (tooling|runtime)

RAP_STREAM=head

TMP_DIR=/shared/rt/rap/tmp
JOBS_DIR=/shared/jobs
NIGHTLY_DIR=/home/data/httpd/download.eclipse.org/rt/rap/nightly
SCRIPTS_DIR=$(dirname $(readlink -nm $0))
# Set bash's internal file separator to \n to avoid problems with filenames that contain spaces
IFS="
"

# Read build type from command line
buildType=$1
if [ -z "$buildType" ]; then
  echo >&2 "Missing build type parameter"
  echo >&2 "Usage: $0 (runtime|tools)"
  exit 1
fi

# Check runtime dir
if [ ! -d $ECLIPSE_DIR/plugins ]; then
  echo >&2 "Missing or invalid ECLIPSE_DIR: $ECLIPSE_DIR"
  exit 1
fi

# Check job archive dir
archiveDir=$JOBS_DIR/rap-$RAP_STREAM-$buildType/lastStable/archive
if [ ! -d $archiveDir ]; then
  echo >&2 "Missing archive dir: $archiveDir"
  exit 1
fi

# Check target main dir
targetMainDir=$NIGHTLY_DIR/${buildType/tools/tooling}
if [ ! -d $targetMainDir ]; then
  echo >&2 "Missing target main dir: $targetMainDir"
  exit 1
fi

echo "Publishing nightly build"
echo "========================"

echo "Build type: $buildType"

latestStableBuild=`ls -1 $archiveDir/*.zip | head -n 1`
if [ -z "$latestStableBuild" ]; then
  echo >&2 "No latest stable build found, exiting"
  exit 0
fi
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

# ensure structure of uncompressed repository
if [ ! -d $workingDir/features ]; then
  echo >&2 "Missing features directory in $workingDir"
  exit 1
fi
if [ ! -d $workingDir/plugins ]; then
  echo >&2 "Missing plugins directory in $workingDir"
  exit 1
fi

mkdir -p $targetDir || exit 1

# Publish p2 repository from zip archive
echo "Copy p2 repository"
rm $workingDir/$zipFileName
cp -r $workingDir/* $targetDir || exit 1

# Add to composite repository
$SCRIPTS_DIR/comp-repo.sh $targetMainDir add $timeStamp || exit 1

# Delete old directories
i=0
for dir in `ls -r -1 $targetMainDir`; do
  if [ -d $targetMainDir/$dir ]; then
    if [ $i -ge 3 ]; then
      echo "Removing outdated $dir"
      $SCRIPTS_DIR/comp-repo.sh $targetMainDir remove $dir || exit 1
      rm -r $targetMainDir/$dir || exit 1
    fi
    let i=i+1;
  fi
done

# Remove working directory
echo "Clean up work directory"
cd ..
rm -rf $workingDir

echo "done"
echo

