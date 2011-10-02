Building RAP
============

We use tycho [1] to build RAP.

Prerequisites
-------------

You'll need:

* A CVS client
* Maven 3.0
* network access

Preparation
-----------

Checkout the RAP repository.  You cannot build directly from your Eclipse workspace, because
projects from CVS are stored in a directory structure that differs from the repository.
Once RAP has switched to git, you will be able to build directly from your local repository.

  cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/rt co -P -r HEAD org.eclipse.rap

This command creates a new directory "org.eclipse.rap" and checks out the latest version of the
RAP repository into this directory.

RAP Runtime
-----------

Run maven on the runtime directory in the releng project

  cd org.eclipse.rap/releng/org.eclipse.rap.releng/runtime
  mvn clean package

The runtime repository will be created in runtime-repository/target

Note: this command creates the rap.runtime feature which contains only the RAP artifacts.
      The basic target requirements have to be copied from the platform repositories.
      We use the pom-aggregate.xml to aggregate this feature on the build server, but this won't
      run locally.

RAP Tooling
-----------

Run maven on the tooling directory in the releng project

  cd org.eclipse.rap/releng/org.eclipse.rap.releng/tooling
  mvn clean package

The tooling repository will be created in tooling-repository/target

References
----------

[1] http://eclipse.org/tycho/
