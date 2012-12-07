Building RAP
============

We use tycho [1] to build RAP.

Prerequisites
-------------

You'll need:

* Git
* Maven 3.0
* network access

Preparation
-----------

Clone the RAP repository.

  git clone git://git.eclipse.org/gitroot/rap/org.eclipse.rap.git

This command will create a new directory `org.eclipse.rap` and create a local copy of the RAP
repository in this directory.

RAP Runtime
-----------

Run maven on the runtime directory in the releng project

  cd org.eclipse.rap/releng/org.eclipse.rap.build/runtime
  mvn clean package

The runtime repository will be created in runtime-repository/target

Note: this command creates the rap.runtime feature which contains only the RAP artifacts.
      The build for the basic target requirements resides in the project
      `org.eclipse.rap.target.build`.

RAP Tooling
-----------

Run maven on the tooling directory in the releng project

  cd org.eclipse.rap/releng/org.eclipse.rap.build/tooling
  mvn clean package

The tooling repository will be created in tooling-repository/target

References
----------

[1] http://eclipse.org/tycho/
