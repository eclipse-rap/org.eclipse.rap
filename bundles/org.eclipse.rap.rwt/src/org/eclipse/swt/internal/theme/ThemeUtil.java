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

package org.eclipse.swt.internal.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.w4t.engine.service.ContextProvider;

/**
 * Used to switch between themes at runtime.
 */
public class ThemeUtil {

  private static final String THEME_URL_PARM = "theme";
  private static final String CURR_THEME_ATTR
    = "org.eclipse.rap.swt.theme.current";

  public static String[] getAvailableThemeIds() {
    ThemeManager themeMgr = ThemeManager.getInstance();
    return themeMgr.getAvailableThemeIds();
  }

  public static String getCurrentThemeId() {
    // 1) try URL parameter
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( THEME_URL_PARM );
    ThemeManager manager = ThemeManager.getInstance();
    HttpSession session = ContextProvider.getSession();
    if( result != null && manager.hasTheme( result ) ) {
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

  public static void setCurrentTheme( final String themeId ) {
    HttpSession session = ContextProvider.getSession();
    session.setAttribute( CURR_THEME_ATTR, themeId );
  }

  public static Theme getTheme() {
    String themeName = getCurrentThemeId();
    ThemeManager themeMgr = ThemeManager.getInstance();
    return themeMgr.getTheme( themeName );
  }
}
