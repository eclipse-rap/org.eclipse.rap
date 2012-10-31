package org.eclipse.rap.ui.tests.resources;

import org.eclipse.rap.ui.resources.IResource;
import org.eclipse.rap.ui.resources.RegisterOptions;


public final class Resource5 implements IResource {

  public String getCharset() {
    return "UTF-8"; //$NON-NLS-1$
  }

  public ClassLoader getLoader() {
    return Resource5.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/rap/ui/tests/resources/Resource5.js"; //$NON-NLS-1$
  }

  public RegisterOptions getOptions() {
    return RegisterOptions.VERSION_AND_COMPRESS;
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }
}
