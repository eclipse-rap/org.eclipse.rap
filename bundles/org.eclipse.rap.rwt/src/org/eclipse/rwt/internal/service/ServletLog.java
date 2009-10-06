/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.RWT;


public final class ServletLog {

  public static void log( final String message, final Throwable throwable ) {
    ServletContext servletContext;
    try {
      HttpSession session = RWT.getSessionStore().getHttpSession();
      servletContext = session.getServletContext();
    } catch( Throwable e ) {
      servletContext = null;
    }
    if( servletContext == null ) {
      System.err.println( message );
      if( throwable != null ) {
        throwable.printStackTrace( System.err );
      }
    } else {
      servletContext.log( message, throwable );
    }
  }
}
