#!/bin/bash
#
# This script is used to build RAP runtime, tooling and war using PDE build.

# Initialize variables that represent command line args with default values
cvsTag=
buildType=N
runtimeDir=
basePlatformDir=
workDir=./work
outputDir=./output
builderCvsPath=

# Print usage info
print_usage() {
  echo "Usage: $0 [args]"
  echo "  --cvs-tag  CVS tag to checkout from"
  echo "  --builder  CVS repository location of builder"
  echo "  --build-type  N/I/M/R, defaults to $buildType"
  echo "  --runtime  path to runtime eclipse install"
  echo "  --base-platform  path to base platform to build against"
  echo "  --work  temporary working directory, defaults to $workDir"
  echo "  --output  path to place output in, defaults to $outputDir"
  echo "  --rap-target  path to a zipped rap target platform, only applicable in tooling build"
}

# Print failure notice and exit
fail() {
  echo "Failed"
  exit 1
}

# Parse command line args and set according variables
swallowOutput=`getopt -l "cvs-tag,build-type,runtime:,base-platform:,work,output,help,builder:,rap-target" cbr:p:wohdt $*`
if test $? != 0
then
  print_usage
  exit 1
fi
for i
do
  case "$i" in
    --cvs-tag|-c) shift; cvsTag=$1; shift;;
    --builder|-d) shift; builderCvsPath=$1; shift;;
    --build-type|-b) shift; buildType=$1; shift;;
    --runtime|-r) shift; runtimeDir=$1; shift;;
    --base-platform|-p) shift; basePlatformDir=$1; shift;;
    --work|-w) shift; workDir=$1; shift;;
    --output|-o) shift; outputDir=$1; shift;;
    --help|-h) shift; print_usage; exit 1; shift;;
  esac
done

# Check CVS tag
if [ -z "$cvsTag" ]; then
  echo "No CVS tag given"
  print_usage
  exit 1
fi

# resolve relative paths
runtimeDir=`readlink -f $runtimeDir`
basePlatformDir=`readlink -f $basePlatformDir`
workDir=`readlink -f "$workDir"`
outputDir=`readlink -f "$outputDir"`

# Create base working directory
if [ ! -d "$workDir" ]; then
  mkdir "$workDir" || fail
fi

# Create output directory
if [ ! -d "$outputDir" ]; then
  mkdir "$outputDir" || fail
fi

# Show informations
echo "Starting build with the following settings:"
echo "  CVS-Tag:            $cvsTag"
echo "  Builder CVS Path:   $builderCvsPath"
echo "  Build Type:         $buildType"
echo "  Runtime Inst.:      $runtimeDir"
echo "  Platform Inst.:     $basePlatformDir"
echo "  Working Dir:        $workDir"
echo "  Output Dir:         $outputDir"
echo ""

# Checkout releng project
builderDir="$workDir/builder/"
echo "Checking out builder from CVS $cvsTag ..."
cd "$workDir"
cvs -Q -f -d:pserver:anonymous@dev.eclipse.org:/cvsroot/rt checkout \
    -d ./builder -r $cvsTag $builderCvsPath \
  || fail
cd -

# Find PDE build
pdeBuild=`ls -1 $runtimeDir/plugins | grep pde.build_ | tail -n 1`
echo "Using PDE Build: $pdeBuild"

# Find Equinox launcher
launcher=$runtimeDir/plugins/`ls -1 $runtimeDir/plugins | grep launcher_ | tail -n 1`
echo "Using Equinox launcher: $launcher"

java -cp $launcher org.eclipse.core.launcher.Main \
    -application org.eclipse.ant.core.antRunner \
    -buildfile "$runtimeDir/plugins/$pdeBuild/scripts/build.xml" \
    -Dbuilder="$builderDir" \
    -DbuildId=`date +%Y%m%d-%H%M` \
    -DbuildDirectory="$workDir/build" \
    -DoutputDirectory="$outputDir" \
    -DbuildType=$buildType \
    -DmapsCheckoutTag=$cvsTag \
    -DfetchTag=$cvsTag \
    -DbaseLocation="$basePlatformDir" \
    -Dfile.encoding=UTF-8 \
  || fail

echo "Cleaning up workspace"
test -d "$builderDir" && rm -rf "$builderDir"

