/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.ISessionStore;


/**
 * Used to switch between themes at runtime.
 */
public final class ThemeUtil {

  private static final String THEME_URL_PARM = "theme";
  private static final String CURR_THEME_ATTR
    = "org.eclipse.rap.theme.current";

  public static String[] getAvailableThemeIds() {
    return ThemeManager.getInstance().getRegisteredThemeIds();
  }

  public static String getCurrentThemeId() {
    ThemeManager manager = ThemeManager.getInstance();
    ISessionStore session = ContextProvider.getSession();
    // 1) try URL parameter
    String result = ContextProvider.getRequest().getParameter( THEME_URL_PARM );
    if( result != null && manager.hasTheme( result ) ) {
      // TODO [rh] a method named get... should be constant, i.e. shouldn't 
      //      have side-effects like altering session attributes 
      session.setAttribute( CURR_THEME_ATTR, result );
    }
    // 2) try session attribute
    else {
      result = ( String )session.getAttribute( CURR_THEME_ATTR );
    }
    // 3) use default
    if( result == null ) {
      result = manager.getDefaultThemeId();
    }
    return result;
  }

  public static void setCurrentThemeId( final String themeId ) {
    // TODO [rh] consider throwing exception if themeId does not exist.
    //      currently leads to exceptions in theme adapters that are difficult
    //      to assign to the actual reason
    ContextProvider.getSession().setAttribute( CURR_THEME_ATTR, themeId );
  }

  public static Theme getTheme() {
    return ThemeManager.getInstance().getTheme( getCurrentThemeId() );
  }
  
  private ThemeUtil() {
    // prevent instantiation
  }
}
