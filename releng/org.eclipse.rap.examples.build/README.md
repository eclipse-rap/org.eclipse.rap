The pom.xml in this project helps to assemble the .war files of the
RAP demo projects. As a default it uses the p2 repositories that are
created from the nightly RAP and RAP incubator builds (see below).

    mvn -e clean install

At the moment it generates three .war files that can be deployed in
any web container: controlsdemo.war, rapdemo.war, workbenchdemo.war.

The RAP Examples Demo requires bundles from additional projects. These
bundles need to be compiled externally and added to the .war file
manually.

* Google Maps:
  https://github.com/eclipsesource/rap-gmap

* Complex Data:
  https://github.com/ralfstx/rap-demo-additions

* d3 Chart
  https://github.com/ralfstx/rap-d3charts

* Nebula Grid
  http://git.eclipse.org/c/rap/incubator/org.eclipse.rap.incubator.nebula-grid.git/

* FileUpload
  http://git.eclipse.org/c/rap/incubator/org.eclipse.rap.incubator.fileupload.git/

* AutoSuggest
  http://git.eclipse.org/c/rap/incubator/org.eclipse.rap.incubator.dropdown.git/

* CkEditor
  http://git.eclipse.org/c/rap/incubator/org.eclipse.rap.incubator.richtext.git/


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
