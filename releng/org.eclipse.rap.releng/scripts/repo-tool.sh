#!/bin/sh
#
# Tool to maintain composite repositories

runtimeDir=/opt/public/rt/rap/build-runtimes/eclipse-3.6.1
publishDir=/opt/public/rt/rap/publish
downloadDir=/home/data/httpd/download.eclipse.org/rt/rap

mode=
repoDir=
repoName=
repoChild=

fail() {
  echo Composite Repository Tool
  if [ $# -gt 0 ]; then
    echo "Error: $1"
  fi
  echo "Usage:"
  echo "  $0 repo-dir create <repo name>"
  echo "  $0 repo-dir add <child>"
  echo "  $0 repo-dir remove <child>"
  echo
  echo "Example:"
  echo "  $0 1.4/runtime create \"RAP 1.4 Runtime Repository\""
  echo "  $0 1.4/runtime add M1"
  exit 1
}

# Check command line
if [ $# -ne 3 ]; then
  fail "Wrong # of paramters"
fi

repoDir="$downloadDir/$1"
if [ ! -d "$repoDir" ]; then
  fail "directory does not exist: $repoDir"
fi

mode=$2
if [ "$mode" == "create" ]; then
  repoName=$3
elif [ "$mode" == "add" -o "$mode" == "remove" ]; then
  repoChild=$3
  if [ ! -d "$repoDir/$repoChild" ]; then
    fail "child to add/remove does not exist: $repoDir/$repoChild"
  fi
else
  fail "Illegal mode: $mode"
fi

# Find PDE build
pdeBuild=`ls -1 $runtimeDir/plugins | grep pde.build_ | tail -n 1`
echo "Using PDE Build: $pdeBuild"

# Find Equinox launcher
launcher=$runtimeDir/plugins/`ls -1 $runtimeDir/plugins | grep launcher_ | tail -n 1`
echo "Using Equinox launcher: $launcher"

java -cp $launcher org.eclipse.core.launcher.Main \
    -application org.eclipse.ant.core.antRunner \
    -buildfile "$publishDir/comp-repo.xml" \
    -DrepoDir="$repoDir" \
    -DrepoName="$repoName" \
    -DrepoChild="$repoChild" \
    $mode \
  || fail
