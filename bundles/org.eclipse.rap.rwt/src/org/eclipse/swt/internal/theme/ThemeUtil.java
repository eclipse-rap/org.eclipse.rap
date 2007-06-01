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

import javax.servlet.http.HttpSession;
import com.w4t.engine.service.ContextProvider;

/**
 * Used to switch between themes at runtime.
 */
public class ThemeUtil {

  private static final String ATTR_CURR_THEME
    = "org.eclipse.rap.swt.theme.current";

  public static String getCurrentThemeId() {
    HttpSession session = ContextProvider.getSession();
    String result = ( String )session.getAttribute( ATTR_CURR_THEME );
    if( result == null ) {
      result = ThemeManager.getInstance().getDefaultThemeId();
    }
    return result;
  }

  public static void setCurrentTheme( final String themeId ) {
    HttpSession session = ContextProvider.getSession();
    session.setAttribute( ATTR_CURR_THEME, themeId );
  }

  public static Theme getTheme() {
    String themeName = getCurrentThemeId();
    ThemeManager themeMgr = ThemeManager.getInstance();
    return themeMgr.getTheme( themeName );
  }
}
