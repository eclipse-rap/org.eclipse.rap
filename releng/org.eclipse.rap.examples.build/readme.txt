The pom.xml in this project helps to assemble the .war files of the 
RAP demo projects. It uses the p2 repositories that are created from 
the nightly RAP and RAP incubator builds.

At the moment it generates three .war files that can be deployed in 
any web container: controlsdemo.war, rapdemo.war, workbenchdemo.war.

The RAP Examples Demo requires bundles from additional projects. These
are not included in the build.

* Google Maps:
  https://github.com/eclipsesource/rap-gmap

* Rich Text Editor:
  https://github.com/eclipsesource/rap-ckeditor

* Complex Data:
  https://github.com/ralfstx/rap-demo-additions
