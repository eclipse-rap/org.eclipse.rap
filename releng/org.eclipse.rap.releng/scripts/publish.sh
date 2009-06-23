#!/bin/bash
#
# This script is used to publish a new RAP build to the download server.
#
# Links:
# http://wiki.eclipse.org/JarProcessor_Options
# http://java.sun.com/j2se/1.5.0/docs/api/java/util/jar/Pack200.Packer.html
# http://wiki.eclipse.org/Pack200
# http://wiki.eclipse.org/Equinox/p2/Publisher

INPUT_ARCHIVE=
ZIP_DOWNLOAD_PATH=
UPDATE_SITE_PATH=

DOWNLOAD_LOCATION=/home/data/httpd/download.eclipse.org/rt/rap
SIGNING_LOCATION=/opt/public/download-staging.priv/rt/rap

function printUsage() {
  cat <<EOT
Usage: $0 [options]"
  -a, --input-archive       input zip file
  -u, --update-site-path    path to remote update site to merge with (relative)
  -d, --download-location   path to zip download directory (relative)
  -e, --eclipse-home        path to eclipse runtime installation
  -j, --java-home           path to java home (overrides \$JAVA_HOME)

Example:
  $0 -a rap-tooling-1.2.0-M-qualifier.zip -u "1.2/update" -e /opt/Eclipse-N-Builds/M7/
EOT
}

function parseArguments() {
  while [ $# -gt 0 ]; do
    case "$1" in
      -a|--input-archive) shift; INPUT_ARCHIVE=$1 ;;
      -u|--update-site-path) shift; UPDATE_SITE_PATH=$1 ;;
      -d|--download-location) shift; ZIP_DOWNLOAD_PATH=$1 ;;
      -e|--eclipse-home) shift; ECLIPSE_HOME=$1 ;;
      -j|--java-home) shift; JAVA_HOME=$1 ;;
      -h|--help) printUsage; exit 0 ;;
      *) echo "invalid parameter: $1"; exit 1 ;;
    esac
    shift
  done
  if [ -z "$INPUT_ARCHIVE" ]; then
    echo "Input zip archive missing (provide parameter -a)"
    printUsage
    exit 1
  fi
  INPUT_ARCHIVE_NAME=`basename "$INPUT_ARCHIVE"`
  if echo "$INPUT_ARCHIVE" | grep -q " "; then
    echo "Input zip file name must not contain spaces"
    exit 1
  fi
  if [ -z "$JAVA_HOME" ]; then
    echo "Java home missing, set JAVA_HOME or specify parameter -j"
    printUsage
    exit 1
  fi
  if [ ! -e "$JAVA_HOME/bin/java" ]; then
    echo "Invalid Java home, set JAVA_HOME or specify parameter -j"
    printUsage
    exit 1
  fi
  if [ -z "$ECLIPSE_HOME" ]; then
    echo "Eclipse runtime missing, set ECLIPSE_HOME or specify -e"
    printUsage
    exit 1
  fi
  if [ ! -d "$ECLIPSE_HOME/plugins" ]; then
    echo "Invalid Eclipse home, set ECLIPSE_HOME or specify -e"
    printUsage
    exit 1
  fi
}

################################################################################
# Utility function for building sub-tasks.

# Uploads given srcFile to build.eclipse.org and signs it. When signing is
# finished, the signed file is downloaded to dstFile.
#
# Parameter: srcFile dstFile
#
function signBuild() {
  local srcFile=`readlink -nm "$1"`
  local dstFile=`readlink -nm "$2"`
  local srcFileName="${srcFile##*/}"
  # parameter checking
  if [ $# -ne 2 ]; then
    echo "Usage: signBuild srcFile dstFile"
    return 1
  fi
  if [ ! -f "$srcFile" ]; then
    echo "Source file does not exist: $srcFile"
    return 1
  fi
  if [ -e "$dstFile" ]; then
    echo "Target file exists already: $dstFile, skipping"
    return 0
  fi
  # upload file
  echo "Upload file $srcFile for signing..."
  echo rsync -v --progress "$srcFile" $BUILD_USER@build.eclipse.org:$SIGNING_LOCATION/
  rsync -v --progress "$srcFile" $BUILD_USER@build.eclipse.org:$SIGNING_LOCATION/
  test $? -eq 0 || return 1
  # initiate signing process
  echo "Signing $srcFileName..."
  ssh -T $BUILD_USER@build.eclipse.org << EOI
  cd "$SIGNING_LOCATION"
  if [ -e "signedOutput/$srcFileName" ]; then
    echo "signed file signedOutput/$srcFileName exists already, skipping"
  else
    chmod g+w "$srcFileName"
    mkdir -p signedOutput
    sign "$srcFileName" nomail signedOutput
    echo "Waiting for $srcFileName..."
    while [ ! -f "signedOutput/$srcFileName" ]; do
      sleep 20
      echo -n "."
    done
  fi
  echo
EOI
  test $? -eq 0 || return 1
  # download signed file
  test $? -eq 0 || return 1
  echo "Downloading signed file to $dstFile..."
  rsync -v --progress "$BUILD_USER@build.eclipse.org:$SIGNING_LOCATION/signedOutput/$srcFileName" "$dstFile"
  test $? -eq 0 || return 1
  # delete file on server
  echo "Deleting remote files..."
  ssh -T $BUILD_USER@build.eclipse.org << EOI
  rm "$SIGNING_LOCATION/$srcFileName"
  rm "$SIGNING_LOCATION/signedOutput/$srcFileName"
EOI
  test $? -eq 0 || return 1
  # repack zip file since the signing process yields zip files that do not work on Windows Vista
  echo "repacking..."
  rm -rf .unzipped && unzip -d .unzipped "$dstFile" && rm "$dstFile" \
    && cd .unzipped && zip -r "$dstFile" . && cd .. && rm -rf .unzipped || return 1
  echo ok
}

# Calls pack200 on the given srcFile and copies the result to dstFile.
#
# Parameters: srcFile dstFile
#
function packBuild() {
  # parameter checking
  if [ $# -ne 3 ]; then
    echo "Usage: packBuild (normalize|pack) srcFile dstFile"
    return 1
  fi
  local mode=$1
  local srcFile=`readlink -nm "$2"`
  local dstFile=`readlink -nm "$3"`
  local srcFileName="${srcFile##*/}"
  if [ "$mode" != "pack" -a "$mode" != "normalize" ]; then
    echo "Invalid mode: $mode, use pack or normalize"
    return 1
  fi
  if [ ! -e "$srcFile" ]; then
    echo "Source file does not exist: $srcFile"
    return 1
  fi
  if [ -e "$dstFile" ]; then
    echo "Target file exists already: $dstFile, skipping"
    return 0
  fi
  # Ensure that we use Java 1.5 for pack200
  $JAVA_HOME/bin/java -version 2>&1 | grep -q "1.5.0"
  if [ $? -ne 0 ]; then
    echo "Incorrect Java version. 1.5.0 needed for pack200."
    return 1
  fi
  mkdir -p .packed && rm -f .packed/* || return 1
  if [ "$mode" == "normalize" ]; then
    $JAVA_HOME/bin/java \
      -Dorg.eclipse.update.jarprocessor.pack200=@jre \
      -cp "$ECLIPSE_LAUNCHER" org.eclipse.core.launcher.Main \
      -application org.eclipse.update.core.siteOptimizer \
      -jarProcessor -processAll -repack -outputDir .packed "$srcFile"
  else
    $JAVA_HOME/bin/java \
      -Dorg.eclipse.update.jarprocessor.pack200=@jre \
      -cp "$ECLIPSE_LAUNCHER" org.eclipse.core.launcher.Main \
      -application org.eclipse.update.core.siteOptimizer \
      -jarProcessor -pack -outputDir .packed "$srcFile"
#      -digestBuilder -digestOutputDir=.packed \
  fi
  test $? -eq 0 || return 1
  mv ".packed/$srcFileName" "$dstFile" || return 1
  rm -rf .packed || return 1
  echo ok
}

# Generates p2 metadata for a given directory.
#
# Parameter: inputDir
#
function generateMetadata() {
  if [ $# -ne 1 ]; then
    echo "Usage: generateMetadata <directory>"
    return 1
  fi
  # Canonicalize path, the p2 generator needs absolute paths to work correctly.
  local inputDir=`readlink -mn "$1"`
  if [ ! -d "$inputDir" ]; then
    echo "No such directory: $inputDir"
    return 1
  fi
  # remove exiting metadata
  rm -f "$inputDir/artifacts.jar" && rm -f "$inputDir/content.jar" || return 1
  # create new metadata
  if [ -e "$inputDir/site.xml" ]; then
    $JAVA_HOME/bin/java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
      -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher \
      -metadataRepository file://$inputDir \
      -artifactRepository file://$inputDir \
      -metadataRepositoryName "RAP Update Site" \
      -artifactRepositoryName "RAP Artifacts" \
      -source $inputDir \
      -configs gtk.linux.x86 \
      -reusePackedFiles \
      -compress \
      -publishArtifacts
  else
    $JAVA_HOME/bin/java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
      -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
      -metadataRepository file://$inputDir \
      -artifactRepository file://$inputDir \
      -metadataRepositoryName "RAP Runtime SDK Repository" \
      -artifactRepositoryName "RAP Runtime SDK Repository" \
      -source $inputDir \
      -configs gtk.linux.x86 \
      -reusePackedFiles \
      -compress \
      -publishArtifacts
  fi
  test $? -eq 0 || return 1
  echo ok
}

function getUsername() {
  if [ -z "$BUILD_USER" ]; then
    echo -n "Enter username for build.eclipse.org: "
    read BUILD_USER
    echo
  fi
  test "$BUILD_USER" || return 1
}

function findLauncher() {
  # search equinox launcher
  echo "Using this Eclipse runtime: $ECLIPSE_HOME"
  ECLIPSE_LAUNCHER=$ECLIPSE_HOME/plugins/`ls -1 $ECLIPSE_HOME/plugins | grep launcher_ | tail -n 1`
  echo "Using this Equinox launcher: $ECLIPSE_LAUNCHER"
}

################################################################################
# MAIN

# allow sourcing this script
if [ -n "$PUBLISH_SCRIPT_INCLUDE" ]; then
  findLauncher
  return
fi

parseArguments "$@"
findLauncher
getUsername || exit 1

if [ -n "$ZIP_DOWNLOAD_PATH" -o -n "$UPDATE_SITE_PATH" ]; then

  # pack200 - normalize
  echo "=== normalize (pack200) $INPUT_ARCHIVE"
  packBuild normalize "$INPUT_ARCHIVE" normalized-$INPUT_ARCHIVE_NAME || exit 1

  # sign
  echo "=== sign normalized $INPUT_ARCHIVE"
  signBuild normalized-$INPUT_ARCHIVE_NAME signed-normalized-$INPUT_ARCHIVE_NAME || exit 1

fi

if [ -n "$ZIP_DOWNLOAD_PATH" ]; then

  # upload zip
  echo "=== upload zip file to $DOWNLOAD_LOCATION/$ZIP_DOWNLOAD_PATH/$INPUT_ARCHIVE_NAME"
  rsync -v --progress \
    signed-normalized-$INPUT_ARCHIVE_NAME \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$ZIP_DOWNLOAD_PATH/$INPUT_ARCHIVE_NAME

fi

if [ -n "$UPDATE_SITE_PATH" ]; then

  # pack200 - pack
  echo "=== pack200 signed $INPUT_ARCHIVE_NAME"
  packBuild pack signed-normalized-$INPUT_ARCHIVE_NAME packed-signed-normalized-$INPUT_ARCHIVE_NAME || exit 1
  rm -rf newSite && unzip packed-signed-normalized-$INPUT_ARCHIVE_NAME -d newSite || exit 1
  if [ -d newSite/eclipse ]; then
    mv newSite/eclipse _eclipse_ && rm -rf newSite && mv _eclipse_ newSite || exit 1
  fi

  # TODO manual processing necessary here:
  if [ "${INPUT_ARCHIVE_NAME:0:11}" == "rap-runtime" ]; then
    echo "--- manual processing needed here ---"
    echo "replace folders with jars: both features and org.junit plug-in"
    echo -n "press ok when finished "
    read c
  fi

  # download old site
  echo "=== merge repository dev.eclipse.org:$DOWNLOAD_LOCATION/$UPDATE_SITE_PATH/"
  echo "update local copy of repository..."
  mkdir -p sites
  copySite=sites/${UPDATE_SITE_PATH/\//_}
  rsync -av --delete --progress \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$UPDATE_SITE_PATH/ \
    $copySite/ || return 1

  # merge (update manager)
  echo "merge repository..."
  rsync -av --exclude site.xml newSite/ $copySite/ || exit 1
  if [ -e newSite/site.xml ]; then
    cat $copySite/site.xml >> newSite/site.xml \
      && mv newSite/site.xml $copySite/site.xml \
      && vi $copySite/site.xml || exit 1
  fi

  # metadata
  generateMetadata $copySite || exit 1

  # TODO generate category metadata
  echo update category.xml
  echo generate category.xml like this:
  echo $JAVA_HOME/bin/java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
   -console -consolelog -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
   -metadataRepository file:///home/ralf/proj/RAP/build/sites/1.2_runtime-update \
   -categoryDefinition file:///home/ralf/proj/RAP/build/category.xml \
   -categoryQualifier \
   -compress
  echo -n "press ok to proceed "
  read c

  # upload
  echo "=== upload repository"
  echo check local repository before uploading: $copySite
  echo -n "press ok to upload "
  read c
  rsync -av --progress \
    $copySite/ \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$UPDATE_SITE_PATH/ || exit 1

fi
