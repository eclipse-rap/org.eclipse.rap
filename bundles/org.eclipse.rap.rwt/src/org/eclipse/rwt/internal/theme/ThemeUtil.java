/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Widget;


/**
 * Used to switch between themes at runtime.
 */
public final class ThemeUtil {

  private static final String CURR_THEME_ATTR
    = "org.eclipse.rap.theme.current";

  /**
   * Returns the ids of all themes that are currently registered.
   *
   * @return an array of the theme ids, never <code>null</code>
   */
  public static String[] getAvailableThemeIds() {
    return ThemeManager.getInstance().getRegisteredThemeIds();
  }

  /**
   * Returns the id of the currently active theme.
   *
   * @return the id of the current theme, never <code>null</code>
   */
  public static String getCurrentThemeId() {
    ISessionStore session = ContextProvider.getSession();
    String result = ( String )session.getAttribute( CURR_THEME_ATTR );
    if( result == null ) {
      result = ThemeManager.DEFAULT_THEME_ID;
    }
    return result;
  }

  /**
   * Sets the current theme to the theme identified by the given id.
   *
   * @param themeId the id of the theme to activate
   * @throws IllegalArgumentException if no theme with the given id is
   *             registered
   */
  public static void setCurrentThemeId( final String themeId ) {
    if( !ThemeManager.getInstance().hasTheme( themeId ) ) {
      throw new IllegalArgumentException( "Illegal theme id: " + themeId );
    }
    ContextProvider.getSession().setAttribute( CURR_THEME_ATTR, themeId );
  }

  public static Theme getTheme() {
    return ThemeManager.getInstance().getTheme( getCurrentThemeId() );
  }

  public static Theme getDefaultTheme() {
    ThemeManager themeManager = ThemeManager.getInstance();
    return themeManager.getTheme( ThemeManager.DEFAULT_THEME_ID );
  }

  //////////////////////////////////////
  // Methods for accessing themed values

  public static QxType getCssValue( final String cssElement,
                                    final String cssProperty,
                                    final SimpleSelector selector )
  {
    return getCssValue( cssElement, cssProperty, selector, null );
  }

  public static QxType getCssValue( final String cssElement,
                                    final String cssProperty,
                                    final ValueSelector selector,
                                    final Widget widget )
  {
    Theme theme = getTheme();
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    ConditionalValue[] values = valuesMap.getValues( cssElement, cssProperty );
    QxType result = selector.select( values, widget );
    if( result == null ) {
      // resort to default theme
      theme = getDefaultTheme();
      valuesMap = theme.getValuesMap();
      values = valuesMap.getValues( cssElement, cssProperty );
      result = selector.select( values, widget );
    }
    return result;
  }

  private ThemeUtil() {
    // prevent instantiation
  }
}
