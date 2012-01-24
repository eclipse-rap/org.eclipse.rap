package org.eclipse.rap.rwt.jstest;

import java.io.IOException;
import java.io.InputStream;

public interface TestContribution {

  String getName();

  String[] getResources();

  InputStream getResourceAsStream( String resourceName ) throws IOException;

}
