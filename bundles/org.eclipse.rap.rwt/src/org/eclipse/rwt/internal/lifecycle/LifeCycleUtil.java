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
package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class LifeCycleUtil {
  private static final String ATTR_SESSION_DISPLAY 
    = LifeCycleUtil.class.getName() + "#sessionDisplay";
  private static final String ATTR_UI_THREAD = LifeCycleUtil.class.getName() + "#uiThread";

  public static Display getSessionDisplay() {
    Display result = null;
    if( ContextProvider.hasContext() ) {
      ISessionStore sessionStore = ContextProvider.getSession();
      result = ( Display )sessionStore.getAttribute( ATTR_SESSION_DISPLAY );
    }
    return result;
  }

  public static void setSessionDisplay( Display display ) {
    ContextProvider.getSession().setAttribute( ATTR_SESSION_DISPLAY, display );
  }
  
  public static void setUIThread( ISessionStore sessionStore, IUIThreadHolder threadHolder ) {
    sessionStore.setAttribute( ATTR_UI_THREAD, threadHolder );
  }
  
  public static IUIThreadHolder getUIThread( ISessionStore sessionStore ) {
    return ( IUIThreadHolder )sessionStore.getAttribute( ATTR_UI_THREAD );
  }

  static String getEntryPoint() {
    String result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String startup = request.getParameter( RequestParams.STARTUP );
    if( startup != null ) {
      result = startup;
    } else if( getSessionDisplay() == null ) {
      result = EntryPointManager.DEFAULT;
    }
    return result;
  }

  private LifeCycleUtil() {
    // prevent instantiation
  }
}
