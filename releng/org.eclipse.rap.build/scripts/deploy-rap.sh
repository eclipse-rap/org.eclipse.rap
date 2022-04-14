#!/usr/bin/env bash
set -e

if [[ "${VERBOSE}" = true || ${VERBOSE} -eq 1 ]]; then
    echo "Be very verbose..."
	# set -x
else
    VERBOSE=false
fi

RAP_UID="${RAP_UID:-genie.rap@projects-storage.eclipse.org}"
RAP_BASE_DIRECTORY="${RAP_BASE_DIRECTORY:-/home/data/httpd/download.eclipse.org/rt/rap}"
RAP_RELEASE="${RAP_RELEASE:-3.21}"
RAP_VERSION="${RAP_VERSION:-${RAP_RELEASE}.0}"
RAP_MILESTONE="${RAP_MILESTONE:-M0}"
RAP_RUNTIME_BUILD_NUMBER="${RAP_RUNTIME_BUILD_NUMBER:-0}"
RAP_RUNTIME_BUILD_TIMESTAMP="${RAP_RUNTIME_BUILD_TIMESTAMP:-19990101-0000}"
RAP_TOOLS_BUILD_NUMBER="${RAP_TOOLS_BUILD_NUMBER:-0}"
RAP_TOOLS_BUILD_TIMESTAMP="${RAP_TOOLS_BUILD_TIMESTAMP:-19990101-0000}"

echo "### DEPLOYMENT CONFIGURATION"
echo "User:           ${RAP_UID}"
echo "Base Directory: ${RAP_BASE_DIRECTORY}"
echo "Release:        ${RAP_RELEASE}"
echo "Version:        ${RAP_VERSION}"
echo "Milestone:      ${RAP_MILESTONE}"
echo "RAP Runtime:    Build #${RAP_RUNTIME_BUILD_NUMBER} (${RAP_RUNTIME_BUILD_TIMESTAMP})"
echo "RAP Tools:      Build #${RAP_TOOLS_BUILD_NUMBER} (${RAP_TOOLS_BUILD_TIMESTAMP})"
echo ""

function createRemoteDirectory {
    REMOTE_DIRECTORY="$1"
    echo "Creating remote directory ${REMOTE_DIRECTORY}"
    ssh ${RAP_UID} mkdir -p "${REMOTE_DIRECTORY}"
}

function removeRemoteDirectory {
    REMOTE_DIRECTORY="$1"
    echo "Remove remote directory ${REMOTE_DIRECTORY}"
    ssh ${RAP_UID} rm -rf ${VERBOSE:+"--verbose"} "${REMOTE_DIRECTORY}"
}

function moveToRemote {
    LOCAL_FILE_OR_DIRECTORY="$1"
    REMOTE_DIRECTORY="$2"
    echo "Mirroring ${LOCAL_FILE_OR_DIRECTORY} to remote directory ${REMOTE_DIRECTORY}"
    rsync -a -e ssh "${LOCAL_FILE_OR_DIRECTORY}" "${RAP_UID}:${REMOTE_DIRECTORY}"
    echo "Removing local ${LOCAL_FILE_OR_DIRECTORY}"
    rm -rf ${VERBOSE:+"--verbose"} "${LOCAL_FILE_OR_DIRECTORY}"
}

echo "### DOWNLOAD ARTIFACTS FROM BUILD"
wget -nv https://ci.eclipse.org/rap/job/rap-head-runtime-signed/${RAP_RUNTIME_BUILD_NUMBER}/artifact/org.eclipse.rap/releng/org.eclipse.rap.build/repository/target/rap-${RAP_VERSION}-S-${RAP_RUNTIME_BUILD_TIMESTAMP}.zip
wget -nv https://ci.eclipse.org/rap/job/rap-head-runtime-signed/${RAP_RUNTIME_BUILD_NUMBER}/artifact/org.eclipse.rap/releng/org.eclipse.rap.build/repository.e4/target/rap-e4-${RAP_VERSION}-S-${RAP_RUNTIME_BUILD_TIMESTAMP}.zip
wget -nv https://ci.eclipse.org/rap/job/rap-head-tools/${RAP_TOOLS_BUILD_NUMBER}/artifact/org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build/repository/target/rap-tools-${RAP_VERSION}-S-${RAP_TOOLS_BUILD_TIMESTAMP}.zip

echo "### RENAME ARCHIVE FILES"
for II in rap-*${RAP_RELEASE}*.zip; do mv ${VERBOSE:+"--verbose"} ${II} ${II/\-S\-/\-${RAP_MILESTONE}\-}; done

echo "### CHMOD ARCHIVE FILES"
chmod ${VERBOSE:+"--verbose"} 664 rap-*${RAP_RELEASE}*.zip

echo "### PUBLISH RAP RUNTIME e3"
ZIP_FILE="rap-${RAP_VERSION}-${RAP_MILESTONE}-${RAP_RUNTIME_BUILD_TIMESTAMP}.zip"
REPO_DIRECTORY="${RAP_MILESTONE}-${RAP_RUNTIME_BUILD_TIMESTAMP}"
TARGET_DIRECTORY="${RAP_BASE_DIRECTORY}/${RAP_RELEASE}"

unzip "${ZIP_FILE}" -d "${REPO_DIRECTORY}"
createRemoteDirectory "${TARGET_DIRECTORY}"
moveToRemote "${ZIP_FILE}" "${TARGET_DIRECTORY}"
moveToRemote "${REPO_DIRECTORY}" "${TARGET_DIRECTORY}"

echo "### PUBLISH RAP RUNTIME e4"
ZIP_FILE="rap-e4-${RAP_VERSION}-${RAP_MILESTONE}-${RAP_RUNTIME_BUILD_TIMESTAMP}.zip"
REPO_DIRECTORY="${RAP_MILESTONE}-${RAP_RUNTIME_BUILD_TIMESTAMP}"
TARGET_DIRECTORY="${RAP_BASE_DIRECTORY}/${RAP_RELEASE}/e4"

unzip "${ZIP_FILE}" -d "${REPO_DIRECTORY}"
createRemoteDirectory "${TARGET_DIRECTORY}"
moveToRemote "${ZIP_FILE}" "${TARGET_DIRECTORY}"
moveToRemote "${REPO_DIRECTORY}" "${TARGET_DIRECTORY}"

echo "### PUBLISH RAP TOOLS WITH DOCS"
ZIP_FILE="rap-tools-${RAP_VERSION}-${RAP_MILESTONE}-${RAP_TOOLS_BUILD_TIMESTAMP}.zip"
REPO_DIRECTORY="${RAP_MILESTONE}-${RAP_TOOLS_BUILD_TIMESTAMP}"
TARGET_DIRECTORY="${RAP_BASE_DIRECTORY}/tools/${RAP_RELEASE}"
DOCS_DIRECTORY="${RAP_BASE_DIRECTORY}/doc/${RAP_RELEASE}"

unzip "${ZIP_FILE}" -d "${REPO_DIRECTORY}"
cp -a ${VERBOSE:+"--verbose"} "${REPO_DIRECTORY}"/plugins/org.eclipse.rap.doc_${RAP_VERSION}*.jar .
createRemoteDirectory "${TARGET_DIRECTORY}"
moveToRemote "${ZIP_FILE}" "${TARGET_DIRECTORY}"
moveToRemote "${REPO_DIRECTORY}" "${TARGET_DIRECTORY}"

echo "### PUBLISH DOCS"
unzip "org.eclipse.rap.doc_${RAP_VERSION}*.jar" -d doc
removeRemoteDirectory "${DOCS_DIRECTORY}"
createRemoteDirectory "${DOCS_DIRECTORY}"
moveToRemote doc/guide "${DOCS_DIRECTORY}"
rm -rf ${VERBOSE:+"--verbose"} doc org.eclipse.rap.doc_${RAP_VERSION}*.jar doc

echo "### PUBLISHING FINISHED."
