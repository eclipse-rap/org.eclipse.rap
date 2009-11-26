/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
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

import java.util.*;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


/**
 * Contains the values defined in a CSS style sheet in an optimized structure
 * for providing quick access to the values for a given element and property.
 */
public final class ThemeCssValuesMap {

  private final Map map;

  public ThemeCssValuesMap( final StyleSheet styleSheet,
                            final ThemeableWidget[] themeableWidgets )
  {
    map = new HashMap();
    init( styleSheet, themeableWidgets );
  }

  public ConditionalValue[] getValues( final String elementName,
                                       final String propertyName )
  {
    ConditionalValue[] result
      = ( ConditionalValue[] )map.get( new Key( elementName, propertyName ) );
    // if element name is unknown, resort to * rules
    if( result == null ) {
      result = ( ConditionalValue[] )map.get( new Key( "*", propertyName ) );
    }
    return result;
  }

  public QxType[] getAllValues() {
    Set resultSet = new HashSet();
    Iterator iterator = map.values().iterator();
    while( iterator.hasNext() ) {
      ConditionalValue[] condValues = ( ConditionalValue[] )iterator.next();
      for( int i = 0; i < condValues.length; i++ ) {
        ConditionalValue condValue = condValues[ i ];
        resultSet.add( condValue.value );
      }
    }
    QxType[] result = new QxType[ resultSet.size() ];
    resultSet.toArray( result );
    return result;
  }

  private void init( final StyleSheet styleSheet,
                     final ThemeableWidget[] themeableWidgets )
  {
    for( int i = 0; i < themeableWidgets.length; i++ ) {
      ThemeableWidget themeableWidget = themeableWidgets[ i ];
      IThemeCssElement[] elements = themeableWidget.elements;
      if( elements != null ) {
        for( int j = 0; j < elements.length; j++ ) {
          IThemeCssElement element = elements[ j ];
          String elementName = element.getName();
          String[] properties = element.getProperties();
          for( int k = 0; k < properties.length; k++ ) {
            String propertyName = properties[ k ];
            ConditionalValue[] values = styleSheet.getValues( elementName,
                                                              propertyName );
            ConditionalValue[] filteredValues = filterValues( values, element );
            map.put( new Key( elementName, propertyName ), filteredValues );
          }
        }
      }
    }
  }

  private ConditionalValue[] filterValues( final ConditionalValue[] values,
                                           final IThemeCssElement element )
  {
    List resultList = new ArrayList();
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
    ConditionalValue[] result = new ConditionalValue[ resultList.size() ];
    resultList.toArray( result );
    return result;
  }

  private static boolean matches( final IThemeCssElement element,
                                  final String[] constraints )
  {
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

  private static boolean contains( final String[] elements, final String string )
  {
    boolean result = false;
    for( int i = 0; i < elements.length && !result; i++ ) {
      if( string.equals( elements[ i ] ) ) {
        result = true;
      }
    }
    return result;
  }

  private static class Key {

    private final String element;

    private final String property;

    private final int hashCode;

    public Key( final String element, final String property ) {
      this.element = element;
      this.property = property;
      hashCode = element.hashCode() ^ property.hashCode();
    }

    public boolean equals( final Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else if( obj != null && obj.getClass() == getClass() ) {
        Key other = ( Key )obj;
        result =    element.equals( other.element )
                 && property.equals( other.property );
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
