/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;


/**
 * Contains the values defined in a CSS style sheet in an optimized structure
 * for providing quick access to the values for a given element and property.
 */
public final class ThemeCssValuesMap {

  private final Map<PropertyKey,ConditionalValue[]> valuesMap;
  private final Theme theme;

  public ThemeCssValuesMap( Theme theme, StyleSheet styleSheet, ThemeableWidget[] themeableWidgets )
  {
    this.theme = theme;
    valuesMap = new LinkedHashMap<PropertyKey,ConditionalValue[]>();
    extractValues( styleSheet, themeableWidgets );
  }

  public ConditionalValue[] getValues( String elementName, String propertyName ) {
    ConditionalValue[] result;
    PropertyKey propertyKey = new PropertyKey( elementName, propertyName );
    result = valuesMap.get( propertyKey );
    // if element name is unknown, resort to * rules
    if( result == null ) {
      PropertyKey wildcardKey = new PropertyKey( "*", propertyName );
      result = valuesMap.get( wildcardKey );
    }
    return result;
  }

  public CssType[] getAllValues() {
    Set<CssType> resultSet = new LinkedHashSet<CssType>();
    Collection<ConditionalValue[]> values = valuesMap.values();
    for( ConditionalValue[] condValues : values ) {
      for( ConditionalValue condValue : condValues ) {
        resultSet.add( condValue.value );
      }
    }
    return resultSet.toArray( new CssType[ resultSet.size() ] );
  }

  private void extractValues( StyleSheet styleSheet, ThemeableWidget[] themeableWidgets ) {
    for( ThemeableWidget themeableWidget : themeableWidgets ) {
      extractValuesForWidget( styleSheet, themeableWidget );
    }
  }

  private void extractValuesForWidget( StyleSheet styleSheet, ThemeableWidget themeableWidget ) {
    if( themeableWidget.elements != null ) {
      for( IThemeCssElement element : themeableWidget.elements ) {
        String elementName = element.getName();
        String[] properties = element.getProperties();
        for( String propertyName : properties ) {
          PropertyKey key = new PropertyKey( elementName, propertyName );
          ConditionalValue[] values = styleSheet.getValues( elementName, propertyName );
          if( values.length == 0 ) {
            reportMissingProperty( elementName, propertyName );
          }
          ConditionalValue[] filteredValues = filterValues( values, element );
          valuesMap.put( key, filteredValues );
        }
      }
    }
  }

  private void reportMissingProperty( String elementName, String propertyName ) {
    if( RWTProperties.isDevelopmentMode() && RWTProperties.enableThemeDebugOutput() ) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append( "Missing value for element: '" );
      stringBuilder.append( elementName );
      stringBuilder.append( "' property: '" );
      stringBuilder.append( propertyName );
      stringBuilder.append( "' in theme: '" );
      stringBuilder.append( theme.getId() );
      stringBuilder.append( "'" );
      System.err.println( stringBuilder.toString() );
    }
  }

  private ConditionalValue[] filterValues( ConditionalValue[] values, IThemeCssElement element ) {
    Collection<ConditionalValue> resultList = new ArrayList<ConditionalValue>();
    String[] latestConstraint = null;
    for( int j = 0; j < values.length; j++ ) {
      ConditionalValue value = values[ j ];
      if( !Arrays.equals( latestConstraint, value.constraints ) ) {
        if( matches( element, value.constraints ) ) {
          resultList.add( value );
          latestConstraint = value.constraints;
        }
      }
    }
    return resultList.toArray( new ConditionalValue[ resultList.size() ] );
  }

  private static boolean matches( IThemeCssElement element, String[] constraints ) {
    boolean passed = true;
    // TODO [rst] Revise: no restrictions for * rules
    if( !"*".equals( element.getName() ) ) {
      for( int k = 0; k < constraints.length && passed; k++ ) {
        String constraint = constraints[ k ];
        if( constraint.charAt( 0 ) == ':' ) {
          passed &= contains( element.getStates(), constraint.substring( 1 ) );
        } else if( constraint.charAt( 0 ) == '[' ) {
          passed &= contains( element.getStyles(), constraint.substring( 1 ) );
        }
      }
    }
    return passed;
  }

  private static boolean contains( String[] elements, String string ) {
    boolean result = false;
    for( int i = 0; i < elements.length && !result; i++ ) {
      if( string.equals( elements[ i ] ) ) {
        result = true;
      }
    }
    return result;
  }

  private static class PropertyKey {

    private final String element;

    private final String property;

    private final int hashCode;

    public PropertyKey( String element, String property ) {
      this.element = element;
      this.property = property;
      hashCode = element.hashCode() ^ property.hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else if( obj != null && obj.getClass() == getClass() ) {
        PropertyKey other = ( PropertyKey )obj;
        result = element.equals( other.element ) && property.equals( other.property );
      } else {
        result = false;
      }
      return result;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }
  }
}
