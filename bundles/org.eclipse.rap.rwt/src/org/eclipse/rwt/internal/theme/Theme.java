/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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


/**
 * An instance of this class represents all the information provided by an RWT
 * theme, i.e. values from the theme properties file, derived values from the
 * default theme, and the name given to the theme.
 */
public final class Theme {

  private final String jsId;

  private final String name;

  private ThemeCssValuesMap valuesMap;

  public Theme( final String jsId,
                final String name,
                final StyleSheet styleSheet,
                final ThemeableWidget[] themeableWidgets )
  {
    if( jsId == null ) {
      throw new NullPointerException( "jsId" );
    }
    if( name == null ) {
      throw new NullPointerException( "name" );
    }
    if( styleSheet == null ) {
      throw new NullPointerException( "stylesheet" );
    }
    if( themeableWidgets == null ) {
      throw new NullPointerException( "themeableWidgets" );
    }
    this.jsId = jsId;
    this.name = name;
    valuesMap = new ThemeCssValuesMap( styleSheet, themeableWidgets );
  }

  public String getJsId() {
    return jsId;
  }

  public String getName() {
    return name;
  }

  public ThemeCssValuesMap getValuesMap() {
    return valuesMap;
  }

  public static String createCssKey( final QxType value ) {
    String result;
    if(    value instanceof QxIdentifier 
        || value instanceof QxBoolean 
        || value instanceof QxFloat ) {
      // Identifiers, boolean and float values are written directly
      result = value.toDefaultString();
    } else {
      result = Integer.toHexString( value.hashCode() );
    }
    return result;
  }
}
