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
REPOSITORY_PATH=

DOWNLOAD_LOCATION=/home/data/httpd/download.eclipse.org/rt/rap
SIGNING_LOCATION=/opt/public/download-staging.priv/rt/rap

function printUsage() {
  cat <<EOT
Usage: $0 [options]"
  -i, --input-archive       input zip file to publish
  -r, --repository-path     path to remote p2 repository to publish (relative to rap home)
  -z, --zip-download-path   path to remote zip download directory (relative to rap home)
  -u, --build-user          username to log into build and download server (overrides \$BUILD_USER)
  -e, --eclipse-home        path to eclipse runtime installation (overrides \$ECLIPSE_HOME)
  -j, --java-home           path to java home (overrides \$JAVA_HOME)

Examples:
  $0 -i rap-tooling-1.3.0-N-20090921-1835.zip -r "1.3/tooling" -e /opt/Eclipse-N-Builds/M7/
  $0 -i rap-runtime-1.3.0-N-20090921-1741.zip -r "1.3/runtime" -z "1.3" -e /opt/Eclipse-N-Builds/M7/
EOT
}

function parseArguments() {
  while [ $# -gt 0 ]; do
    case "$1" in
      -i|--input-archive) shift; INPUT_ARCHIVE=$1 ;;
      -r|--repository-path) shift; REPOSITORY_PATH=$1 ;;
      -z|--zip-download-path) shift; ZIP_DOWNLOAD_PATH=$1 ;;
      -u|--build-user) shift; BUILD_USER=$1 ;;
      -e|--eclipse-home) shift; ECLIPSE_HOME=$1 ;;
      -j|--java-home) shift; JAVA_HOME=$1 ;;
      -h|--help) printUsage; exit 0 ;;
      *) echo "invalid parameter: $1"; exit 1 ;;
    esac
    shift
  done
  if [ -z "$INPUT_ARCHIVE" ]; then
    echo "Input zip archive missing (provide parameter -i)"
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
  if [ -z "$BUILD_USER" ]; then
    echo "Username for build and download server missing, set BUILD_USER or specify -u"
    printUsage
    exit 1
  fi
  if [ -z "$ZIP_DOWNLOAD_PATH" -a -z "$REPOSITORY_PATH" ]; then
    echo "Nothing to do. Specify -r or -z."
    printUsage
    exit 0
  fi
}

################################################################################
# Utility functions for building sub-tasks.

# Packs all directories into jars and unpacks jarred directories later.
#
# Parameters: (pack|unpack|rename) srcFile dstFile
#
# pack:
#   all plugin/feature directories are packed to a file named *.unpack.jar
# unpack:
#   all plugin/feature jars that match *.unpack.jar are unpacked
# rename:
#   all plugin/feature jars that match *.unpack.jar are renamed to *.jar
#
function jarDirs() {
  # parameter checking
  if [ $# -ne 3 ]; then
    echo "Usage: jarDirs (pack|unpack|rename) srcFile dstFile"
    return 1
  fi
  local mode=$1
  local srcFile=`readlink -nm "$2"`
  local dstFile=`readlink -nm "$3"`
  local srcFileName="${srcFile##*/}"
  if [ ! -e "$srcFile" ]; then
    echo "Source file does not exist: $srcFile"
    return 1
  fi
  if [ -e "$dstFile" ]; then
    echo "Target file exists already: $dstFile, skipping"
    return 0
  fi
  mkdir -p .unzipped && rm -rf .unzipped/* || return 1
  unzip -q $srcFile -d .unzipped || return 1
  if [ "$mode" == "pack" ]; then
    find .unzipped/eclipse/plugins -mindepth 1 -maxdepth 1 -type d \
      -exec echo jar {} \; \
      -exec jar cMf {}.unpack.jar -C {} . \; \
      -exec rm -r {} \; || return 1
    find .unzipped/eclipse/features -mindepth 1 -maxdepth 1 -type d \
      -exec echo jar {} \; \
      -exec jar cMf {}.unpack.jar -C {} . \; \
      -exec rm -r {} \; || return 1
  elif [ "$mode" == "unpack" ]; then
    for f in .unzipped/eclipse/plugins/*.unpack.jar; do
      unzip -q $f -d ${f/.unpack.jar/} && rm $f || return 1
    done
    for f in .unzipped/eclipse/features/*.unpack.jar; do
      unzip -q $f -d ${f/.unpack.jar/} && rm $f || return 1
    done
  elif [ "$mode" == "rename" ]; then
    # Note: *.unpack.jar.pack.gz files need to be renamed too
    for f in .unzipped/eclipse/plugins/*.unpack.jar*; do
      mv $f ${f/.unpack.jar/.jar} || return 1
    done
    for f in .unzipped/eclipse/features/*.unpack.jar*; do
      mv $f ${f/.unpack.jar/.jar} || return 1
    done
  else
    echo "Invalid mode: $mode, use pack or normalize"
    return 1
  fi
  cd .unzipped && zip -q -r "$dstFile" . && cd .. && rm -rf .unzipped || return 1
  echo ok
}

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
  rm -rf .unzipped && unzip -q -d .unzipped "$dstFile" && rm "$dstFile" \
    && cd .unzipped && zip -q -r "$dstFile" . && cd .. && rm -rf .unzipped || return 1
  echo ok
}

# Calls pack200 on the given srcFile and copies the result to dstFile.
#
# Parameters: (normalize|pack) srcFile dstFile
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
  mkdir -p .packed && rm -rf .packed/* || return 1
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
  $JAVA_HOME/bin/java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
    -consolelog -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
    -metadataRepository file:$inputDir \
    -artifactRepository file:$inputDir \
    -metadataRepositoryName "RAP Runtime SDK Repository" \
    -artifactRepositoryName "RAP Runtime SDK Repository" \
    -source $inputDir \
    -configs gtk.linux.x86 \
    -reusePackedFiles \
    -compress \
    -publishArtifacts || return 1
  echo ok
}

# Generates p2 category metadata for a given directory.
#
# Parameter: inputDir
#
function generateCategory() {
  if [ $# -ne 1 ]; then
    echo "Usage: generateCategory <directory>"
    return 1
  fi
  # Canonicalize path, the p2 generator needs absolute paths to work correctly.
  local inputDir=`readlink -mn "$1"`
  if [ ! -d "$inputDir" ]; then
    echo "No such directory: $inputDir"
    return 1
  fi
  # generate category.xml
  echo '<?xml version="1.0" encoding="UTF-8"?>' > category.xml
  echo '<site>' >> category.xml
  echo '<category-def name="org.eclipse.rap.category" label="Rich Ajax Platform (RAP)"/>' >> category.xml
  ls -1 $inputDir/features/*.jar | sed 's/^.*\/\([^_]*\)_\(.*\)\.jar$/<feature url="features\/\1_\2.jar" id="\1" version="\2">\n<category name="org.eclipse.rap.category"\/>\n<\/feature>/' >> category.xml
  echo '</site>' >> category.xml
  local categoryXml=`readlink -mn category.xml`
  $JAVA_HOME/bin/java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
   -consolelog -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
   -metadataRepository file:$inputDir \
   -categoryDefinition file:$categoryXml \
   -compress || return 1
  rm category.xml
  echo ok
}

function findLauncher() {
  # search equinox launcher
  echo "Using this Eclipse runtime: $ECLIPSE_HOME"
  ECLIPSE_LAUNCHER=$ECLIPSE_HOME/plugins/`ls -1 $ECLIPSE_HOME/plugins | grep launcher_ | tail -n 1`
  echo "Using this Equinox launcher: $ECLIPSE_LAUNCHER"
}

################################################################################
# MAIN

parseArguments "$@"
findLauncher

# pack all directories as jars (signing process doesn't handle directories correctly)
echo "=== jar all dirs in $INPUT_ARCHIVE"
jarDirs pack "$INPUT_ARCHIVE" jarred-$INPUT_ARCHIVE_NAME || exit 1
 
# exclude icu.base bundles from packing and signing
EXCLUDE_BUNDLES=`zipinfo -1 jarred-$INPUT_ARCHIVE_NAME | grep -E 'com.ibm.icu|org.junit_3'`
echo "pack.excludes: `echo $EXCLUDE_BUNDLES | sed 's/ /, /g'`" > pack.properties
echo "sign.excludes: `echo $EXCLUDE_BUNDLES | sed 's/ /, /g'`" >> pack.properties
zip "$INPUT_ARCHIVE" pack.properties && rm pack.properties || exit 1

# pack200 - normalize
echo "=== normalize (pack200) $INPUT_ARCHIVE"
packBuild normalize jarred-$INPUT_ARCHIVE normalized-$INPUT_ARCHIVE_NAME || exit 1

# sign
echo "=== sign normalized $INPUT_ARCHIVE"
signBuild normalized-$INPUT_ARCHIVE_NAME signed-$INPUT_ARCHIVE_NAME || exit 1

if [ -n "$ZIP_DOWNLOAD_PATH" ]; then
  # create a copy without pack.properties
  jarDirs unpack signed-$INPUT_ARCHIVE_NAME upload-$INPUT_ARCHIVE_NAME
  zip -d upload-$INPUT_ARCHIVE_NAME pack.properties
  # upload zip
  echo "=== upload zip file to $ZIP_DOWNLOAD_PATH/$INPUT_ARCHIVE_NAME"
  echo check local file before uploading: upload-$INPUT_ARCHIVE_NAME
  echo -n "press ok to upload "
  read c
  rsync -v --progress \
    upload-$INPUT_ARCHIVE_NAME \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$ZIP_DOWNLOAD_PATH/$INPUT_ARCHIVE_NAME
fi

if [ -n "$REPOSITORY_PATH" ]; then
  # pack200 - pack
  echo "=== pack200 signed $INPUT_ARCHIVE_NAME"
  packBuild pack signed-$INPUT_ARCHIVE_NAME packed-$INPUT_ARCHIVE_NAME || exit 1

  # download old repo
  echo "=== merge repository dev.eclipse.org:$DOWNLOAD_LOCATION/$REPOSITORY_PATH/"
  echo "update local copy of repository..."
  mkdir -p mirror
  localCopy=mirror/${REPOSITORY_PATH/\//_}
  rsync -av --delete --progress \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$REPOSITORY_PATH/ \
    $localCopy/ || return 1

  echo "merge new content into local copy of repository"
  jarDirs rename packed-$INPUT_ARCHIVE_NAME renamed-$INPUT_ARCHIVE_NAME
  rm -rf newSite && unzip -q renamed-$INPUT_ARCHIVE_NAME -d newSite || exit 1
  rm renamed-$INPUT_ARCHIVE_NAME
  rsync -r newSite/eclipse/ $localCopy/ || exit 1
  rm -rf newSite

  # metadata
  generateMetadata $localCopy || exit 1
  generateCategory $localCopy || exit 1

  # upload
  echo "=== upload repository $REPOSITORY_PATH"
  echo check local repository before uploading: $localCopy
  echo -n "press ok to upload "
  read c
  rsync -av --progress \
    $localCopy/ \
    $BUILD_USER@dev.eclipse.org:$DOWNLOAD_LOCATION/$REPOSITORY_PATH/ || exit 1
fi

