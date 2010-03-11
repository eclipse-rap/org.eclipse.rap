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

import org.eclipse.rwt.internal.theme.css.StyleSheet;

public final class Theme {

  private static final String JS_THEME_PREFIX = "org.eclipse.swt.theme.";

  private final String id;
  private final String jsId;
  private final String name;
  private ThemeCssValuesMap valuesMap;
  private StyleSheetBuilder styleSheetBuilder;

  public Theme( final String id, final String name, final StyleSheet styleSheet )
  {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    if( styleSheet == null ) {
      throw new NullPointerException( "stylesheet" );
    }
    this.id = id;
    this.name = name != null ? name : "Unnamed Theme";
    jsId = createUniqueJsId( id );
    valuesMap = null;
    styleSheetBuilder = new StyleSheetBuilder();
    styleSheetBuilder.addStyleSheet( styleSheet );
  }

  public String getId() {
    return id;
  }

  public String getJsId() {
    return jsId;
  }

  public String getName() {
    return name;
  }

  public void addStyleSheet( final StyleSheet styleSheet ) {
    if( valuesMap != null ) {
      throw new IllegalStateException( "Theme is already initialized" );
    }
    styleSheetBuilder.addStyleSheet( styleSheet );
  }

  public void initialize( final ThemeableWidget[] themeableWidgets ) {
    if( valuesMap != null ) {
      throw new IllegalStateException( "Theme is already initialized" );
    }
    StyleSheet styleSheet = styleSheetBuilder.getStyleSheet();
    valuesMap = new ThemeCssValuesMap( styleSheet, themeableWidgets );
    styleSheetBuilder = null;
  }

  public ThemeCssValuesMap getValuesMap() {
    if( valuesMap == null ) {
      throw new IllegalStateException( "Theme is not initialized" );
    }
    return valuesMap;
  }

  public static String createCssKey( final QxType value ) {
    String result;
    if( value instanceof QxIdentifier
        || value instanceof QxBoolean
        || value instanceof QxFloat )
    {
      // Identifiers, boolean and float values are written directly
      result = value.toDefaultString();
    } else {
      result = Integer.toHexString( value.hashCode() );
    }
    return result;
  }

  private static String createUniqueJsId( String id ) {
    String result;
    if( id.equals( ThemeManager.DEFAULT_THEME_ID ) ) {
      result = JS_THEME_PREFIX + "Default";
    } else {
      String hash = Integer.toHexString( id.hashCode() );
      result = JS_THEME_PREFIX + "Custom_" + hash;
    }
    return result;
  }
}
