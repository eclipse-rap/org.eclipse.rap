/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.*;

import org.eclipse.rwt.internal.theme.css.*;


/**
 * An instance of this class represents all the information provided by an RWT
 * theme, i.e. values from the theme properties file, derived values from the
 * default theme, and the name given to the theme.
 */
public final class Theme {

  private final String name;

  private StyleSheet styleSheet;

  private QxType[] values;

  private ThemeCssValuesMap valuesMap;

  private String jsId;

  public Theme( String name, StyleSheet styleSheet ) {
    checkName( name );
    this.name = name;
    this.styleSheet = styleSheet;
    readValuesFromStyleSheet();
  }

  public void initValuesMap( final ThemeableWidget[] themeableWidgets ) {
    valuesMap = new ThemeCssValuesMap();
    for( int i = 0; i < themeableWidgets.length; i++ ) {
      ThemeableWidget themeableWidget = themeableWidgets[ i ];
      IThemeCssElement[] elements = themeableWidget.elements;
      if( themeableWidget.elements != null ) {
        for( int j = 0; j < elements.length; j++ ) {
          valuesMap.initElement( elements[ j ], styleSheet );
        }
      }
    }
  }
  
  public void setJsId( final String jsId ) {
    this.jsId = jsId;
  }

  public String getName() {
    return name;
  }

  public String getJsId() {
    return jsId;
  }

  public StyleSheet getStyleSheet() {
    return styleSheet;
  }

  public QxType[] getValues() {
    return values;
  }

  public ThemeCssValuesMap getValuesMap() {
    return valuesMap;
  }

  public static String createCssKey( final QxType value ) {
    return Integer.toHexString( value.hashCode() );
  }

  private void checkName( final String name ) {
    if( name == null ) {
      throw new NullPointerException( "name" );
    }
    if( name.length() == 0 ) {
      throw new IllegalArgumentException( "empty argument" );
    }
  }

  private void readValuesFromStyleSheet() {
    // For each value in the style sheet, create a dummy property that will be
    // registered with the qx themes. These dummy props are needed to fill the
    // qx color/border/etc. themes.
    HashSet valueSet = new HashSet();
    StyleRule[] styleRules = styleSheet.getStyleRules();
    for( int i = 0; i < styleRules.length; i++ ) {
      StyleRule styleRule = styleRules[ i ];
      IStylePropertyMap propertyMap = styleRule.getProperties();
      String[] propertyNames = propertyMap.getProperties();
      for( int j = 0; j < propertyNames.length; j++ ) {
        String propertyName = propertyNames[ j ];
        QxType value = propertyMap.getValue( propertyName );
        // TODO [rst] Quick fix for NPE, revise
        if( value != null ) {
          valueSet.add( value );
        }
      }
    }
    values = ( QxType[] )valueSet.toArray( new QxType[ valueSet.size() ] );
//    Arrays.sort( values, new Comparator() {
//
//      public int compare( Object o1, Object o2 ) {
//        String hash1 = createCssKey( ( QxType )o1 );
//        String hash2 = createCssKey( ( QxType )o2 );
//        return hash1.compareTo( hash2 );
//      }
//    });
  }
}
