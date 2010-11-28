#!/bin/sh
# update build scripts from CVS

CVS_PATH=org.eclipse.rap/releng/org.eclipse.rap.releng/scripts
CVS_TAG=HEAD

cd /home/build/rap
cvs -Q -f -d:pserver:anonymous@dev.eclipse.org:/cvsroot/rt checkout -d ./scripts2 -r "$CVS_TAG" "$CVS_PATH"
cd -
