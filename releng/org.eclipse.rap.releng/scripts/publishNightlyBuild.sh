#!/bin/bash
#
#  Copyright (c) 2011 Innoopract Informationssysteme GmbH.
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
# 
#  Contributors:
#      Innoopract Informationssysteme GmbH - initial API and implementation
###############################################################################

nightlyDir=$DOWNLOAD_DIR
jobDir=$JOB_DIR

# Clean up last nightly repository
echo "Clean up last nightly repository"
rm -rf $nightlyDir/*

# Copy last stable build 
echo "Copy last stable build"
nightlyTmp=$nightlyDir/tmp
mkdir $nightlyTmp
cd $nightlyTmp
cp $jobDir/lastStable/archive/*/*.zip .

# Unzip artifact
echo "Uncompress last stable build"
unzip *.zip

# Publish p2 repository
launcher=$RUNTIME_DIR/plugins/org.eclipse.equinox.launcher_*.jar
echo "Start to generate p2 repository"
java -jar $launcher \
   -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
   -metadataRepository file:$nightlyDir \
   -artifactRepository file:$nightlyDir \
   -source $nightlyTmp/eclipse \
   -configs gtk.linux.x86 \
   -reusePackedFiles \
   -compress \
   -publishArtifacts    
echo "Finished generating a p2 repository"

# Generate category
echo "Create category.xml"
echo '<?xml version="1.0" encoding="UTF-8"?>' > category.xml
echo '<site>' >> category.xml
echo '<category-def name="org.eclipse.rap.category" label="Rich Ajax Platform (RAP)"/>' >> category.xml
ls -1 $nightlyDir/features/*.jar | sed 's/^.*\/\([^_]*\)_\(.*\)\.jar$/<feature url="features\/\1_\2.jar" id="\1" version="\2">\n<category name="org.eclipse.rap.category"\/>\n<\/feature>/' >> category.xml
echo '</site>' >> category.xml
categoryXml=$nightlyTmp/category.xml
java -cp $launcher org.eclipse.core.launcher.Main \
   -consolelog \
   -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
   -metadataRepository file:$nightlyDir \
   -categoryDefinition file:$categoryXml \
   -compress


# Remove tmp
echo "Clean up work directory"
rm -rf $nightlyTmp