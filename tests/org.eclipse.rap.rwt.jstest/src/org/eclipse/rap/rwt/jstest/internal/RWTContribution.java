package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.swt.internal.widgets.displaykit.ClientResourcesAdapter;


public class RWTContribution implements TestContribution {

  private static final String JSON_PARSER_NAME = "json2.js";

  public String getName() {
    return "rwt";
  }

  public String[] getResources() {
    String[] clientResources = ClientResourcesAdapter.getRegisteredClientResources();
    String[] result = new String[ clientResources.length + 1 ];
    System.arraycopy( clientResources, 0, result, 0, clientResources.length );
    result[ result.length - 1 ] = JSON_PARSER_NAME;
    return result;
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return ClientResourcesAdapter.getResourceAsStream( resource );
  }

}
