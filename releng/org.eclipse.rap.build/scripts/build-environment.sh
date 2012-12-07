# This script sets up environment variables for the RAP builds
#

echo "***********************************************************************"

export MVN=${MVN:-"/opt/public/common/apache-maven-3.0.4/bin/mvn"}
echo "Maven path: ${MVN}"

export ECLIPSE_HOME=${ECLIPSE_HOME:-"/shared/rt/rap/build-runtimes/eclipse"}
echo "Eclipse location: ${ECLIPSE_HOME}"

export SIGNING_LOCATION=${SIGNING_LOCATION:-"/opt/public/download-staging.priv/rt/rap"}
echo "Signing location: ${SIGNING_LOCATION}"

export MAVEN_LOCAL_REPO_PATH=${MAVEN_LOCAL_REPO_PATH:-"/shared/rt/rap/m2/repository"}
echo "Local Maven repository location: ${MAVEN_LOCAL_REPO_PATH}"
