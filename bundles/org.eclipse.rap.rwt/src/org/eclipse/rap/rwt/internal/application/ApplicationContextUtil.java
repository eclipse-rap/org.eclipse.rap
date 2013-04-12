/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import javax.servlet.ServletContext;


public class ApplicationContextUtil {

  private final static String ATTR_APPLICATION_CONTEXT
    = ApplicationContextImpl.class.getName() + "#instance";

  public static void set( ServletContext servletContext, ApplicationContextImpl applicationContext )
  {
    servletContext.setAttribute( ATTR_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContextImpl get( ServletContext servletContext ) {
    return ( ApplicationContextImpl )servletContext.getAttribute( ATTR_APPLICATION_CONTEXT );
  }

  public static void remove( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTR_APPLICATION_CONTEXT );
  }

}
