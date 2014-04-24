Eclipse RAP Runtime (Remote Application Platform)
=================================================

Thanks for your interest in this project. The [Eclipse RAP Runtime] [1]
provides a powerful widget toolkit and integrates well with proven
technologies such as OSGi and Java EE. You can write your application
entirely in Java, re-use code and benefit from first-class IDE tools.

Git Repository Structure
------------------------

| directory   | content                                                     |
|-------------|-------------------------------------------------------------|
| `bundles/`  | all bundle projects                                         |
| `tests/`    | unit test projects                                          |
| `features/` | feature projects                                            |
| `releng/`   | projects for release engineering                            |
| `examples/` | for bundles containing exemplary applications and demo code | 

Additional information regarding source code management, builds, coding
standards, and more can be found on the
[Getting involved with RAP development] [2] pages. For more information,
refer to the [RAP wiki pages] [3].

Building RAP Runtime
--------------------

The RAP project uses Tycho with Maven to build its bundles, features,
examples, and p2 repositories, and it's *easy* to run the build locally!
All you need is Maven installed on your computer, and then you need to
run the following command from the root of the Git repository:

    mvn clean verify

As a result, you'll get a p2 repository with all the RAP Runtime bundles and
features in

    releng/org.eclipse.rap.build/repository.luna/target/repository/

Official builds are available from the [RAP Download page] [4].

Contributions
-------------

Before your contribution can be accepted by the project, you need to create
and electronically sign the
[Eclipse Foundation Contributor License Agreement (CLA)] [5] and sign off 
on the Eclipse Foundation Certificate of Origin.

For more information, please visit [Contributing via Git] [6].

License
-------

[Eclipse Public License (EPL) v1.0] [7]

Contact
-------

Contact the project developers via the [RAP Forum] [8] or the project's
["dev" mailing list] [9].

Search for bugs
---------------

This project uses Bugzilla to track [ongoing development and issues] [10].

Create a new bug
----------------

Be sure to [search for existing bugs] [10] before you
[create a new RAP bug report] [11]. Remember that contributions are always
welcome!


[1]: http://eclipse.org/rap
[2]: http://www.eclipse.org/rap/getting-involved/
[3]: http://wiki.eclipse.org/RAP/
[4]: http://www.eclipse.org/rap/downloads/
[5]: http://www.eclipse.org/legal/CLA.php
[6]: http://wiki.eclipse.org/Development_Resources/Contributing_via_Git
[7]: http://wiki.eclipse.org/EPL
[8]: http://www.eclipse.org/forums/eclipse.technology.rap
[9]: https://dev.eclipse.org/mailman/listinfo/rap-dev
[10]: https://bugs.eclipse.org/bugs/buglist.cgi?product=RAP
[11]: https://bugs.eclipse.org/bugs/enter_bug.cgi?product=RAP
