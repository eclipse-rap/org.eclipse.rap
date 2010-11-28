#!/bin/bash
#
# This script is used to trigger the war build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized
#

runtimeDir=/home/build/rap/build-runtimes/eclipse-3.4.1
tomcatDir=/home/build/rap/apache-tomcat-6.0.18/

scriptsDir=`dirname $0`

$scriptsDir/build-common.sh \
  --cvs-tag "$CVS_TAG" \
  --build-type "$BUILD_TYPE" \
  --work "$WORKSPACE" \
  --runtime "$runtimeDir" \
  --base-platform "$PLATFORM_DIR" \
  --builder "org.eclipse.rap/releng/org.eclipse.rap.releng/warbuild"

# start build-internal tomcat
$tomcatDir/bin/startup.sh

# give tomcat a chance to start
sleep 1m

if [ $? = 0 ]; then
  oldDeployment=`stat -c %Y $tomcatDir/work/Catalina/localhost/rapdemo/`

  echo "Deploy new war archive"
  cp "`echo $WORKSPACE`/output/rapdemo.war" $tomcatDir/webapps

  # give tomcat a chance to deploy
  sleep 1m
 
  # check if the deployment is newer then the last one
  newDeployment=`stat -c %Y $tomcatDir/work/Catalina/localhost/rapdemo/`
  result=0;
  if [ $oldDeployment -ge $newDeployment ]; then
    echo "Demo War archive was not successfully deployed to Tomcat."
    result=42 
  fi
  
  exit $result
fi
