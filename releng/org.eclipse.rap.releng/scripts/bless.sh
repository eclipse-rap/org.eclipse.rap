#!/bin/sh
#
# Tool to turn eclipse directories into p2 repositories

fail() {
  echo $1
  exit 1
}

# Check command line
if [ $# -ne 1 ]; then
  fail "Wrong # of paramters"
fi

# Canonicalize path, the p2 generator needs absolute paths to work correctly.
inputDir=`readlink -mn "$1"`
if [ ! -d "$inputDir" ]; then
  echo "No such directory: $inputDir"
  exit 1
fi

# Determine runtime
if [ -z "$RUNTIME_DIR" ]; then
  RUNTIME_DIR=/shared/rt/rap/build-runtimes/eclipse-3.6.2
fi

# Find Equinox launcher
ECLIPSE_LAUNCHER=$RUNTIME_DIR/plugins/`ls -1 $RUNTIME_DIR/plugins | grep launcher_ | tail -n 1`
echo "Using Equinox launcher: $ECLIPSE_LAUNCHER"

# Remove existing metadata
rm -f "$inputDir/artifacts.jar" && rm -f "$inputDir/content.jar" || exit 1
rm -f "$inputDir/artifacts.xml" && rm -f "$inputDir/content.xml" || exit 1

echo "Input directory: $inputDir"

java -cp $ECLIPSE_LAUNCHER org.eclipse.core.launcher.Main \
    -consolelog -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
    -metadataRepository file:$inputDir \
    -artifactRepository file:$inputDir \
    -metadataRepositoryName "Generated Repository" \
    -artifactRepositoryName "Generated Repository" \
    -source $inputDir \
    -reusePackedFiles \
    -compress \
    -publishArtifacts \
    || exit 1
