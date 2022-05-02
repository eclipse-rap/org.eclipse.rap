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

echo "### DEPLOYMENT CONFIGURATION"
echo "User:           ${RAP_UID}"
echo "Base Directory: ${RAP_BASE_DIRECTORY}"
echo "Release:        ${RAP_RELEASE}"
echo ""

function copyToRemote {
    LOCAL_FILE_OR_DIRECTORY="$1"
    REMOTE_DIRECTORY="$2"
    echo "Mirroring ${LOCAL_FILE_OR_DIRECTORY} to remote directory ${REMOTE_DIRECTORY}"
    rsync -a -e ssh "${LOCAL_FILE_OR_DIRECTORY}" "${RAP_UID}:${REMOTE_DIRECTORY}"
}

echo "### CHMOD TARGET FILES"
chmod ${VERBOSE:+"--verbose"} 664 org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build/targets/rap-*${RAP_RELEASE}*.target

echo "### PUBLISH RAP TARGET FILES"
TARGET_DIRECTORY="${RAP_BASE_DIRECTORY}/targets"

copyToRemote "org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build/targets/rap-${RAP_RELEASE}.target"    "${TARGET_DIRECTORY}"
copyToRemote "org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build/targets/rap-${RAP_RELEASE}-e4.target" "${TARGET_DIRECTORY}"

echo "### PUBLISHING FINISHED."
