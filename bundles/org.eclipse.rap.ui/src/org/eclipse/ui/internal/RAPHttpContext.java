/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.ui.internal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

// NOTE: This was added to org.eclipse.rap.ui to avoid starting
//       the workbench bundle before the context is established. The latter
//       uses the context already on startup
public class RAPHttpContext implements HttpContext {
  private final Bundle bundle;

  public RAPHttpContext() {
    bundle = Platform.getBundle( "org.eclipse.ui" ); //$NON-NLS-1$
  }

  public boolean handleSecurity( final HttpServletRequest request,
                                 final HttpServletResponse response )
    throws IOException
  {
    // default behaviour assumes the container has already performed
    // authentication
    return true;
  }

  public URL getResource( final String name ) {
    return bundle.getResource( name );
  }

  public String getMimeType( final String name ) {
    return null;
  }
}
