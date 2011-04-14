/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.q07.jstest.internal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class JsTestsHttpContext implements HttpContext {

  private Bundle bundle;

  public JsTestsHttpContext() {
    bundle = Platform.getBundle( "org.eclipse.rap.rwt" ); //$NON-NLS-1$
  }

  public boolean handleSecurity( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    return true;
  }

  public URL getResource( String name ) {
    return bundle.getResource( name );
  }

  public String getMimeType( String name ) {
    return null;
  }
}
