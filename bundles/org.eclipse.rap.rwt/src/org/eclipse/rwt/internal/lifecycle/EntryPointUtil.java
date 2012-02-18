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
    String name = getCurrentEntryPointName();
    return getEntryPointByName( name );
  }

  static String getCurrentEntryPointName() {
    String result = readCurrentEntryPointName();
    if( result == null ) {
      result = determineCurrentEntryPointName();
      storeCurrentEntryPointName( result );
    }
    return result;
  }

  static IEntryPoint getEntryPointByName( String name ) {
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getFactoryByName( name );
    if( factory == null ) {
      throw new IllegalArgumentException( "Entry point not found: " + name );
    }
    return factory.create();
  }

  private static String determineCurrentEntryPointName() {
    String result;
    result = readNameFromStartupParameter();
    if( result == null ) {
      result = readNameFromBranding();
      if( result == null ) {
        result = EntryPointUtil.DEFAULT;
      }
    }
    return result;
  }

  private static String readNameFromStartupParameter() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    return "".equals( result ) ? null : result;
  }

  private static String readNameFromBranding() {
    AbstractBranding branding = BrandingUtil.determineBranding();
    String result = branding.getDefaultEntryPoint();
    return "".equals( result ) ? null : result;
  }

  private static void storeCurrentEntryPointName( String name ) {
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( ATTR_CURRENT_ENTRY_POINT_NAME, name );
  }

  private static String readCurrentEntryPointName() {
    ISessionStore session = ContextProvider.getSessionStore();
    return ( String )session.getAttribute( ATTR_CURRENT_ENTRY_POINT_NAME );
  }

}
