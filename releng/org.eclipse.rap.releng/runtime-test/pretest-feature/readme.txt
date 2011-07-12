This feature was introduced to work around the classpath problems of PDE.
We have in RWT several fragments with inter-fragment dependencies due to the
split of RWT and it's flavors. To work around these dependencies this feature
helps the releng project to build the correct dependencies before building
the tests itself. See org.eclipse.rap.releng/allElements.xml for the order of
building the features.