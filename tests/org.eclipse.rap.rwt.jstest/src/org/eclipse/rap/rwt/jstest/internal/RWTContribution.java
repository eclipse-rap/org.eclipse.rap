package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.swt.internal.widgets.displaykit.ClientResourcesAdapter;


public class RWTContribution implements TestContribution {

  public String getName() {
    return "rwt";
  }

  public String[] getResources() {
    return ClientResourcesAdapter.getRegisteredClientResources();
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return ClientResourcesAdapter.getResourceAsStream( resource );
  }

}
