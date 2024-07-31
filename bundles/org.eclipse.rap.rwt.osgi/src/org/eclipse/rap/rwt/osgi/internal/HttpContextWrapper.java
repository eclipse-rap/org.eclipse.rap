/*******************************************************************************
 * Copyright (c) 2011, 2024 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.rap.service.http.HttpContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


class HttpContextWrapper implements HttpContext {
  private final HttpContext context;

  HttpContextWrapper( HttpContext context ) {
    this.context = context;
  }

  @Override
  public String getMimeType( String name ) {
    return context.getMimeType( name );
  }

  @Override
  public URL getResource( String name ) {
    URL result = null;
    try {
      // Preliminary fix for bug 268759
      // 268759: ResourceManager handles non-existing resources incorrectly
      File file = new File( name );
      if( file.exists() && !file.isDirectory() ) {
        result = file.toURI().toURL();
      }
    } catch( MalformedURLException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    return result;
  }

  @Override
  public boolean handleSecurity( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    return context.handleSecurity( request, response );
  }

}
