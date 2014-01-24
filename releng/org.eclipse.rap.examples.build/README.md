The pom.xml in this project helps to assemble the .war files of the
RAP demo projects. As a default it uses the p2 repositories that are
created from the nightly RAP and RAP incubator builds (see below).

    mvn -e clean install

At the moment it generates three .war files that can be deployed in 
any web container: controlsdemo.war, rapdemo.war, workbenchdemo.war.

The RAP Examples Demo requires bundles from additional projects. See
below for instructions on how to add them to the .war files.

* Google Maps:
  https://github.com/eclipsesource/rap-gmap

* Complex Data:
  https://github.com/ralfstx/rap-demo-additions

* d3 Chart


Changing the default p2 repositories
------------------------------------

The default build uses the nightly build from the RAP Runtime project and the
nightly build of the RAP Incubator projects. This default behavior can be
changed by setting the properties `rap-repository` and `rap-incubator-repository`.

Example with RAP 2.2 and RAP Incubator builds for 2.2:

    mvn -e \
      -Drap-repository=http://download.eclipse.org/rt/rap/2.2/ \
      -Drap-incubator-repository=http://download.eclipse.org/rt/rap/incubator/2.2/ \
      clean install

Adding additional pre-built bundles to .war files
-------------------------------------------------

(1) Copy the bundles into the project directory
    /org.eclipse.rap.examples.build/localrepo/plugins
(2) Run the Ant build "RAP Examples - local p2 repo build" from within
    Eclipse (/org.eclipse.rap.examples.build/localrepo/build.xml)
(3) Add the new bundles to the .product file of your war product and
    ensure that the start-levels are set appropriately
(4) Run the standard Tycho/Maven build in project root
