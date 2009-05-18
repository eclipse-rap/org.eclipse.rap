#!/bin/bash
#
# This script is used to publish a new build to the existing update site / repository.
#
# * Run this script like this:
#
# ./publish.sh -a rap-tooling-1.2.0-M-qualifier.zip -u "1.2/update" -r /opt/Eclipse-N-Builds/M7/

clear
# Initialize variables that represent command line args with default values
UPDATE_SITE_PATH="1.2/update"
REPOSITORY_ARCHIVE=
RUNTIME_DIR=
DOWNLOAD_LOCATION=/home/data/httpd/download.eclipse.org/rt/rap
SIGNING_LOCATION=/opt/public/download-staging.priv/rt/rap
TARGET=all

# print usage info
function printUsage() {
  echo "Usage: $0 [options] [target]"
  echo "  -u, --update-site-path    path to remote update site to merge with" 
  echo "  -a, --tooling-archive     zip file with rap tooling"
  echo "  -r, --runtime             path to runtime eclipse install"
  echo
  echo "Available targets:"
  echo "  * toolingZip"
  echo "  * toolingRepo"
  echo "  * runtimeZip"
  echo "  * runtimeRepo"
  echo
  echo "Example:"
  echo "  $0 -a rap-tooling-1.2.0-M-qualifier.zip -u \"1.2/update\" -r /opt/Eclipse-N-Builds/M7/"
  echo ""
}

function parseArguments() {
  while getopts u:a:r:h opt; do
    case $opt in
      a) REPOSITORY_ARCHIVE=$OPTARG ;;
      u) UPDATE_SITE_PATH=$OPTARG ;;
      r) RUNTIME_DIR=$OPTARG ;;
      ?) printUsage; exit 2 ;;
    esac
  done
  shift `expr $OPTIND - 1`
  TARGET=$1

  if [ -z "$REPOSITORY_ARCHIVE" ]; then
  	echo "Reposity missing"
    printUsage
    exit 1
  fi
  if [ -z "$RUNTIME_DIR" ]; then
    echo "runtime missing"
    printUsage
    exit 1
  fi
  ensureArchive $REPOSITORY_ARCHIVE
  ensureRuntime $RUNTIME_DIR
  ensureRemoteRepository $UPDATE_SITE_PATH
  
  # get absolute path and filename for archive
  REPOSITORY_ARCHIVE=`readlink -f $REPOSITORY_ARCHIVE`
  REPOSITORY_FILE_NAME=${REPOSITORY_ARCHIVE##*/}
}

# print failure notice and exit
function fail() {
  echo "Publishing failed."
  exit 1
}

# make sure the zip file exists
function ensureArchive() {
  if [ ! -f "$1" ]; then
    echo "Please provide a correct archive to process. '$1' not found"
    fail
  fi
  echo "Using $1 as repository archive."
}

# make sure the runtime exists
function ensureRuntime() {
  if [ ! -d "$1/plugins" ]; then
    echo "Please provide a path to an eclipse runtime."
    fail
  fi
  echo "Using $1 as runtime."
}

# make sure the remote repository exists
function ensureRemoteRepository() {
  if [ -z $1 ]; then
    echo "Please provide a path to the remote update site (eg. '1.2/update')."
    fail
  fi
  echo "Using $DOWNLOAD_LOCATION/$1 as remote update site."
}

# make sure the remote repository exists
function ensurePack200() {
  packVersion=`pack200 -V`
  correctVersion=`grep -q 1.23 <<< $packVersion`
  if [ $? != 0 ]; then
    echo "We need pack200 v1.23. Installed is the following:"
    echo $packVersion
    fail
  else 
    echo "Correct version of pack200 found."
  fi
}

# upload build for signing
function signBuild() {
  scp $REPOSITORY_ARCHIVE $username@build.eclipse.org:$SIGNING_LOCATION

  echo "Initiating signing process..."

  ssh -T $username@build.eclipse.org << EOI
  cd $SIGNING_LOCATION
  sign $REPOSITORY_FILE_NAME mail ./signedOutput
  cd ./signedOutput
  echo "Waiting for $REPOSITORY_FILE_NAME"
  while [ ! -f $REPOSITORY_FILE_NAME ]
  do
    sleep 20
    echo -n "."
  done
EOI
  
  mkdir -p newSite
  mkdir -p signed

  echo ""
  echo "Downloading signed jar again..."
  scp $username@build.eclipse.org:$SIGNING_LOCATION/signedOutput/$REPOSITORY_FILE_NAME ./signed
  ssh -T $username@build.eclipse.org << EOI
  cd $SIGNING_LOCATION
  rm $REPOSITORY_FILE_NAME
EOI
}

function packBuild() {
  outputDir=./packed
  mkdir -p $outputDir
  # check correct version of pack200
  ensurePack200
  echo "Packing the archive..."
  # TODO [bm] ensure that we really use the correct pack200
  java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
    -application org.eclipse.update.core.siteOptimizer \
    -jarProcessor -processAll -repack -pack \
    -digestBuilder -digestOutputDir=$outputDir \
    -outputDir $outputDir $REPOSITORY_ARCHIVE
}

function generateMetadata() {
  java -Xmx512m -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
    -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator \
    -updateSite $1 \
    -site file:$1/site.xml \
    -metadataRepository file:$1 \
    -metadataRepositoryName "Rich Ajax Platform (RAP)" \
    -artifactRepository file:$1 \
    -artifactRepositoryName "Rich Ajax Platform (RAP)" \
    -compress \
    -reusePack200Files \
    -noDefaultIUs
}

function getUsername() {
  echo -n "Please enter your username for build.eclipse.org: "
  read username
  echo ""
}

function findLauncher() {
  # search equinox launcher
  ECLIPSE_LAUNCHER=$RUNTIME_DIR/plugins/`ls -1 $RUNTIME_DIR/plugins | grep launcher_ | tail -n 1`
  echo "Using the following Equinox launcher: $ECLIPSE_LAUNCHER"
}

function mergeUpdateSite() {
  mkdir -p oldSite
  rsync -avz --progress $username@dev.eclipse.org:$DOWNLOAD_LOCATION/$UPDATE_SITE_PATH/ ./oldSite
  unzip ./signed/$REPOSITORY_FILE_NAME -d newSite
  java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
  -application org.eclipse.update.core.standaloneUpdate \
  -command mirror \
  -from file://`readlink -f ./oldSite` \
  -to ./newSite
}

function upload() {
  rsync -avz --progress ./newSite/ $username@dev.eclipse.org:$DOWNLOAD_LOCATION/$UPDATE_SITE_PATH/
}

function buildToolingZip() {
  getUsername
  # sign
  # metadata
  # upload
}

function buildToolingRepo() {
  getUsername
  # pack200
  # sign
  # download
  # merge (update manager)
  # metadata
  # pack artifacts
  # upload
}

function buildRuntimeZip() {
  getUsername
  # sign
  # upload
}

function buildRuntimeRepo() {
  getUsername
  # pack200
  # sign
  # merge (copy)
  # metadata
  # pack artifacts
  # upload
}
############################### MAIN ###########################################

parseArguments "$@"
findLauncher
case $TARGET in
  all)
    buildToolingZip
    buildToolingRepo
    buildRuntimeZip
    buildRuntimeRepo
  ;;
  toolingZip)
    buildToolingZip
  ;;
  toolingRepo)
    buildToolingRepo
  ;;
  runtimeZip)
    buildRuntimeZip
  ;;
  runtimeRepo)
    buildRuntimeRepo
  ;;
  ?)
    echo "Invalid target: $TARGET"
  ;;
esac