The pom.xml in this project helps to assemble the .war files of the
RAP demo projects. As a default it uses the p2 repositories that are
created from the nightly RAP and RAP incubator builds (see below).

    mvn -e clean verify

At the moment it generates three .war files that can be deployed in
any web container: controls.war, rapdemo.war, workbench.war.

The RAP Examples Demo (rapdemo.war) requires bundles from additional
projects. These bundles need to be compiled externally and added to
the .war file manually.

* Google Maps:
  https://github.com/eclipsesource/rap-gmap

* Complex Data:
  https://github.com/ralfstx/rap-demo-additions

* d3 Chart
  https://github.com/ralfstx/rap-d3charts

* AutoSuggest
  http://git.eclipse.org/c/rap/incubator/org.eclipse.rap.incubator.dropdown.git/


Changing the default p2 repositories
------------------------------------

The default build uses the nightly build from the RAP Runtime project and the
nightly build of the RAP Incubator projects. This default behavior can be
changed by setting the properties `rap-repo.url` and `rap-incubator-repo.url`.

Example with RAP 3.0 and RAP Incubator builds for 3.0:

    mvn -e \
      -Drap-repo.url=http://download.eclipse.org/rt/rap/3.0/ \
      -Drap-incubator-repo.url=http://download.eclipse.org/rt/rap/incubator/3.0/ \
      clean verify
