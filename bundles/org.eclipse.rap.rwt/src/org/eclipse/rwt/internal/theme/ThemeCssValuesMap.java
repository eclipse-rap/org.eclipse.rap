/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.*;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


/**
 * Contains the values defined in a CSS style sheet in an optimized structure
 * for providing quick access to the values for a given element and property.
 */
public final class ThemeCssValuesMap {

  private final Map<PropertyKey,ConditionalValue[]> valuesMap;

  public ThemeCssValuesMap( StyleSheet styleSheet, ThemeableWidget[] themeableWidgets ) {
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

  public QxType[] getAllValues() {
    Set<QxType> resultSet = new LinkedHashSet<QxType>();
    Iterator iterator = valuesMap.values().iterator();
    while( iterator.hasNext() ) {
      ConditionalValue[] condValues = ( ConditionalValue[] )iterator.next();
      for( int i = 0; i < condValues.length; i++ ) {
        ConditionalValue condValue = condValues[ i ];
        resultSet.add( condValue.value );
      }
    }
    return resultSet.toArray( new QxType[ resultSet.size() ] );
  }

  private void extractValues( StyleSheet styleSheet, ThemeableWidget[] themeableWidgets ) {
    for( int i = 0; i < themeableWidgets.length; i++ ) {
      ThemeableWidget themeableWidget = themeableWidgets[ i ];
      extractValuesForWidget( styleSheet, themeableWidget );
    }
  }

  private void extractValuesForWidget( StyleSheet styleSheet, ThemeableWidget themeableWidget ) {
    IThemeCssElement[] elements = themeableWidget.elements;
    if( elements != null ) {
      for( int i = 0; i < elements.length; i++ ) {
        IThemeCssElement element = elements[ i ];
        String elementName = element.getName();
        String[] properties = element.getProperties();
        for( int j = 0; j < properties.length; j++ ) {
          String propertyName = properties[ j ];
          PropertyKey key = new PropertyKey( elementName, propertyName );
          ConditionalValue[] values = styleSheet.getValues( elementName, propertyName );
          ConditionalValue[] filteredValues = filterValues( values, element );
          valuesMap.put( key, filteredValues );
        }
      }
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

    public int hashCode() {
      return hashCode;
    }
  }
}
