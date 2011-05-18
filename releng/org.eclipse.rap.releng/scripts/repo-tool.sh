
runtimeDir=/opt/public/rt/rap/build-runtimes/eclipse-3.6.1
publishDir=/opt/public/rt/rap/publish

mode=
repoDir=
repoName=
repoChild=

fail() {
  echo Composite Repository Tool
  if [ $# -gt 0 ]; then
    echo "Error: $1"
  fi
  echo Usage:
  echo "  $0 create -d /path/to/repo -n \"Repo Name\""
  echo "  $0 addChild -d /path/to/repo -c child"
  echo "  $0 removeChild -d /path/to/repo -c child"
  exit 1
}

# Check command line
while [ $# -gt 0 ]; do
  arg=$1
  shift
  case $arg in
    create|addChild|removeChild)
      mode=$arg;;
    -d)
      repoDir=$1
      shift;;
    -n)
      repoName=$1
      shift;;
    -c)
      repoChild=$1
      shift;;
    *)
      fail "illegal parameter $arg";;
  esac
done

if [ -z "$mode" ]; then
  fail "mode not specified"
fi

if [ -z "$repoDir" ]; then
  fail "repository base directory not specified"
fi

if [ "$mode" == "create" -a -z "$repoName" ]; then
  fail "repository name not specified"
fi

if [ "$mode" == "add" -a -z "$repoChild" ]; then
  fail "child to add not specified"
fi

if [ "$mode" == "remove" -a -z "$repoChild" ]; then
  fail "child to remove not specified"
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

