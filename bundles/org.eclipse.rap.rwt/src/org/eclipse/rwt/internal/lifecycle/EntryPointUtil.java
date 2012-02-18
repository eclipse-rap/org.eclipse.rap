/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.service.ISessionStore;


public class EntryPointUtil {

  private static final String ATTR_CURRENT_ENTRY_POINT_NAME
    = EntryPointUtil.class.getName() + "#currentEntryPoint";

  public static final String DEFAULT = "default";

  private EntryPointUtil() {
    // prevent instantiation
  }

  public static IEntryPoint getCurrentEntryPoint() {
    // TODO [rst] Is caching still needed here?
    IEntryPoint result = readCurrentEntryPoint();
    if( result == null ) {
      result = determineCurrentEntryPoint();
      storeCurrentEntryPoint( result );
    }
    return result;
  }

  private static IEntryPoint determineCurrentEntryPoint() {
    IEntryPoint result;
    result = findByStartupParameter();
    if( result == null ) {
      result = findByServletName();
      if( result == null ) {
        result = findByBranding();
        if( result == null ) {
          result = getEntryPointByName( DEFAULT );
        }
      }
    }
    return result;
  }

  private static IEntryPoint findByStartupParameter() {
    IEntryPoint result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String name = request.getParameter( RequestParams.STARTUP );
    if( name != null && name.length() > 0 ) {
      result = getEntryPointByName( name );
    }
    return result;
  }

  private static IEntryPoint findByServletName() {
    IEntryPoint result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String path = request.getServletPath();
    if( path != null && path.length() > 0 ) {
      result = getEntryPointByPath( path );
    }
    return result;
  }

  private static IEntryPoint findByBranding() {
    IEntryPoint result = null;
    AbstractBranding branding = BrandingUtil.determineBranding();
    String name = branding.getDefaultEntryPoint();
    if( name != null && name.length() > 0 ) {
      result = getEntryPointByName( name );
    }
    return result;
  }

  private static IEntryPoint getEntryPointByPath( String path ) {
    IEntryPoint result = null;
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getFactoryByPath( path );
    if( factory != null ) {
      result = factory.create();
    }
    return result;
  }

  private static IEntryPoint getEntryPointByName( String name ) {
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getFactoryByName( name );
    if( factory == null ) {
      throw new IllegalArgumentException( "Entry point not found: " + name );
    }
    return factory.create();
  }

  private static void storeCurrentEntryPoint( IEntryPoint name ) {
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( ATTR_CURRENT_ENTRY_POINT_NAME, name );
  }

  private static IEntryPoint readCurrentEntryPoint() {
    ISessionStore session = ContextProvider.getSessionStore();
    return ( IEntryPoint )session.getAttribute( ATTR_CURRENT_ENTRY_POINT_NAME );
  }

}
