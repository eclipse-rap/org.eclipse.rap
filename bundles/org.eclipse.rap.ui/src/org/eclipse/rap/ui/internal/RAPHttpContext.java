/*******************************************************************************
 * Copyright (c) 2008, 2018 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

// NOTE: This was added to org.eclipse.rap.ui to avoid starting
//       the workbench bundle before the context is established. The latter
//       uses the context already on startup
public final class RAPHttpContext implements HttpContext {

  private final Bundle bundle;

  public RAPHttpContext() {
    bundle = Platform.getBundle( "org.eclipse.rap.ui" ); //$NON-NLS-1$
  }

  @Override
  public boolean handleSecurity( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    // default behaviour assumes the container has already performed
    // authentication
    return true;
  }

  @Override
  public URL getResource( String name ) {
    URL result = null;
    try {
      File file = new File( name );
      if( file.exists() && !file.isDirectory() ) {
        result = file.toURI().toURL();
      } else {
        result = bundle.getResource( name );
      }
    } catch( MalformedURLException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    return result;
  }

  @Override
  public String getMimeType( String name ) {
    return null;
  }

}
