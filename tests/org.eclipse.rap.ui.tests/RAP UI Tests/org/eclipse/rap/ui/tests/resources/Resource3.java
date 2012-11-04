package org.eclipse.rap.ui.tests.resources;

import org.eclipse.rap.ui.resources.IResource;


public final class Resource3 implements IResource {

  public String getCharset() {
    return "UTF-8"; //$NON-NLS-1$
  }

  public ClassLoader getLoader() {
    return Resource3.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/rap/ui/tests/resources/Resource3.js"; //$NON-NLS-1$
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }
}
