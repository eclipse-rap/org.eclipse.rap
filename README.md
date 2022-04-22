Eclipse RAP Runtime (Remote Application Platform)
=================================================

Welcome and thank you for your interest in this project. The [Eclipse RAP Runtime](https://www.eclipse.org/rap/) provides a powerful widget toolkit and integrates well with proven technologies such as Java EE and OSGi. You can write your application entirely in Java, reuse code and benefit from world-class IDE tools.

Git Repository Structure
------------------------

| directory               | content                                                     |
|-------------------------|-------------------------------------------------------------|
| [`bundles/`](bundles)   | all bundle projects                                         |
| [`tests/`](tests)       | unit test projects                                          |
| [`features/`](features) | feature projects                                            |
| [`releng/`](releng)     | projects for release engineering                            |
| [`examples/`](examples) | for bundles containing exemplary applications and demo code |

Additional information regarding source code management, builds, coding standards, and more can be found on the [Getting involved with RAP development](https://www.eclipse.org/rap/getting-involved/) pages. For more information, refer to the [RAP wiki pages](https://wiki.eclipse.org/RAP/).

Building RAP Runtime
--------------------

The RAP project uses Maven in combination with [Eclipse Tycho](https://github.com/eclipse/tycho) to build its bundles, features, examples and p2 repositories, and it's easy to run the build locally! All you need is [Maven installed](https://maven.apache.org/install.html) on your computer, and then you need to run the following command from the root [`pom.xml`](./pom.xml) of the RAP Runtime Git repository:

    mvn clean verify

As a result, you get a p2 repository with all RAP e3 Runtime bundles and features in

    releng/org.eclipse.rap.build/repository/target/repository/

and a second one with the RAP e4 Runtime bundles in

    releng/org.eclipse.rap.build/repository.e4/target/repository/

Official builds are available from the [RAP Download page](https://www.eclipse.org/rap/downloads/).

Compiling and building the RAP demo projects is explained [here](releng/org.eclipse.rap.examples.build/README.md).

Contributions
-------------

Before your contribution can be accepted by the project, you need to create and electronically sign the [Eclipse Foundation Contributor License Agreement (CLA)](https://www.eclipse.org/legal/ECA.php) and sign off on the Eclipse Foundation Certificate of Origin.

For more information, please visit the [project's contribution guide](CONTRIBUTING.md).

License
-------

[Eclipse Public License - v 1.0](https://www.eclipse.org/legal/epl-v10.html)

Contact
-------

Contact the project developers via the [RAP Forum](https://www.eclipse.org/forums/eclipse.technology.rap) or the project's ["dev" mailing list](https://dev.eclipse.org/mailman/listinfo/rap-dev).

Search for bugs
---------------

This project uses GitHub issues to track [ongoing development and bugs](https://github.com/eclipse-rap/org.eclipse.rap/issues).

Create a new bug
----------------

Be sure to [search for existing bugs](https://github.com/eclipse-rap/org.eclipse.rap/issues) before you [create a new RAP bug report](https://github.com/eclipse-rap/org.eclipse.rap/issues/new/choose). Remember that contributions are always welcome!
