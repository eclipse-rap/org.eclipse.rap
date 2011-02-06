/*******************************************************************************
 * Copyright (c) 2010 CAS Software AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CAS Software AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.jettycustomizer.internal;

import java.util.Dictionary;

import org.eclipse.equinox.http.jetty.JettyCustomizer;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.SessionHandler;

/**
 * Configure Jetty to not use cookies for session management. This allows RAP
 * applications to run in multiple tabs of the same browser instance.
 */
public final class SessionCookieCustomizer extends JettyCustomizer {

  public Object customizeContext( final Object context, 
                                  final Dictionary settings )
  {
    Object result = super.customizeContext( context, settings );
    customizeSessionManager( result );
    return result;
  }

  private static void customizeSessionManager( Object context ) {
    if( context instanceof Context ) {
      Context jettyContext = ( Context )context;
      SessionHandler sessionHandler = jettyContext.getSessionHandler();
      if( sessionHandler != null ) {
        SessionManager sessionManager = sessionHandler.getSessionManager();
        if( sessionManager instanceof AbstractSessionManager ) {
          ( ( AbstractSessionManager )sessionManager ).setUsingCookies( false );
        }
      }
    }
  }
}
