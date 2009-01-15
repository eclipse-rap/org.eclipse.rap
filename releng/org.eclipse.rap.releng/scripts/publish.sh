#!/bin/bash
clear
# Initialize variables that represent command line args with default values
updateSite=
toolingArchive=
runtimeDir=
# activate!
rapDownloadLocation=/home/data/httpd/download.eclipse.org/rt/rap
rapRemoteLocation=/opt/public/download-staging.priv/rt/rap

# print usage info
print_usage() {
  echo "Usage: $0 [args]"
  echo "  --update-site-path - path to remote update site to merge with" 
  echo "  --tooling-archive - zip file with rap tooling"
  echo "  --runtime - path to runtime eclipse install"
}

# print failure notice and exit
fail() {
  echo "failed"
  exit 1
}


# Parse command line args and set according variables
swallowOutput=`getopt -l "update-site:,tooling-archive:,runtime:" aur $*`
if test $? != 0
then
  print_usage
  exit 1
fi
for i
do
  case "$i" in
    --tooling-archive|-a) shift; toolingArchive=$1; shift;;
    --update-site-path|-u) shift; updateSitePath=$1; shift;;
    --runtime|-r) shift; runtimeDir=$1; shift;;
    --help|-h) shift; print_usage; exit 1; shift;;
  esac
done

toolingArchive=`readlink -f $toolingArchive`
fileName=${toolingArchive##*/}

echo ""
echo "Publishing build to eclipse.org"
echo ""
echo "  1/4 - Signing the build"
echo ""
echo -n "Please enter your username for build.eclipse.org: "
read username
echo ""
echo "Uploading zip to sign it..."

scp $toolingArchive $username@build.eclipse.org:$rapRemoteLocation

echo "Initiating signing process..."

ssh -T $username@build.eclipse.org << EOI
cd $rapRemoteLocation
sign $fileName nomail ./signedOutput
cd ./signedOutput
echo "Waiting for $fileName"
while [ ! -f $fileName ]
do
        sleep 20
        echo -n "."
done
rm ../$fileName
EOI

mkdir -p newSite
mkdir -p signed

echo ""
echo "Downloading signed jar again..."
scp $username@build.eclipse.org:$rapRemoteLocation/signedOutput/$fileName ./signed
ssh -T $username@build.eclipse.org << EOI
cd $rapRemoteLocation
rm $fileName
EOI

echo ""
echo "  2/4 - Downloading old site content"
mkdir oldSite
scp -r $username@dev.eclipse.org:$rapDownloadLocation/$updateSitePath/* ./oldSite

echo ""
echo "  3/4 - Merging update site with new build"

# search equinox launcher
launcher=$runtimeDir/plugins/`ls -1 $runtimeDir/plugins | grep launcher_ | tail -n 1`
echo "Using the following Equinox launcher: $launcher"

echo "Unzipping current build"
unzip ./signed/$fileName -d newSite

java -cp $launcher org.eclipse.core.launcher.Main \
  -application org.eclipse.update.core.standaloneUpdate \
  -command mirror \
  -from ./oldSite \
  -to ./newSite

mkdir output
zip output/$fileName -r newSite/*

echo "  4/4 - Uploading data to download.eclipse.org"
echo -n "do it! it enter "
read 
scp -r ./newSite/* $username@dev.eclipse.org:$rapDownloadLocation/$updateSitePath/*

echo "Done."
exit
