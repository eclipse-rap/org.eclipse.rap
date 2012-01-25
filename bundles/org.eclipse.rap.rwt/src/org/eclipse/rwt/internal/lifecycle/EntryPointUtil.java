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
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.service.ISessionStore;


public class EntryPointUtil {

  private static final String ATTR_CURRENT_ENTRY_POINT
    = EntryPointUtil.class.getName() + "#currentEntryPoint";

  private EntryPointUtil() {
    // prevent instantiation
  }

  public static String getCurrentEntryPoint() {
    ISessionStore session = ContextProvider.getSessionStore();
    return ( String )session.getAttribute( ATTR_CURRENT_ENTRY_POINT );
  }

  public static String findEntryPoint() {
    String result = readFromStartupParameter();
    if( result == null ) {
      result = readFromBranding();
      if( result == null ) {
        result = EntryPointManager.DEFAULT;
      }
    }
    return result;
  }

  public static int createUI( String name ) {
    ParamCheck.notNull( name, "name" );

    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getEntryPointFactory( name );
    IEntryPoint entryPoint = factory.create();
    setCurrentEntryPoint( name );
    return entryPoint.createUI();
  }

  private static String readFromStartupParameter() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( "".equals( result ) ) {
      result = null;
    }
    return result;
  }

  private static String readFromBranding() {
    AbstractBranding branding = BrandingUtil.determineBranding();
    String result = branding.getDefaultEntryPoint();
    if( "".equals( result ) ) {
      result = null;
    }
    return result;
  }

  private static void setCurrentEntryPoint( String name ) {
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( ATTR_CURRENT_ENTRY_POINT, name );
  }

}
