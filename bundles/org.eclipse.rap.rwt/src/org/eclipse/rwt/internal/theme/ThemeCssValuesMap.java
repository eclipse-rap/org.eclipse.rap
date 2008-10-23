/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


/**
 * Contains the values defined in a CSS style sheet in an optimized structure
 * for providing quick access to the values for a given element and property.
 */
public class ThemeCssValuesMap {

  private final Map map;

  public ThemeCssValuesMap() {
    map = new HashMap();
  }

  public void init( final IThemeCssElement element, final StyleSheet styleSheet )
  {
    String elementName = element.getName();
    IThemeCssProperty[] properties = element.getProperties();
    for( int i = 0; i < properties.length; i++ ) {
      IThemeCssProperty property = properties[ i ];
      String propertyName = property.getName();
      ConditionalValue[] values = styleSheet.getValues( elementName,
                                                        propertyName );
      // TODO [rst] Enable filtering as soon as theme.xml files contain styles
      //            and states
//      ConditionalValue[] filteredValues = filterValues( values, element );
//      add( elementName, propertyName, filteredValues );
      add( elementName, propertyName, values );
    }
  }

  public ConditionalValue[] getValues( final String elementName,
                                       final String propertyName )
  {
    return ( ConditionalValue[] )map.get( getKey( elementName, propertyName ) );
  }

  private ConditionalValue[] filterValues( final ConditionalValue[] values,
                                           final IThemeCssElement element )
  {
    // TODO [rst] Optimize: filter out conditional values whith doubled constraints
    List resultList = new ArrayList();
    for( int j = 0; j < values.length; j++ ) {
      ConditionalValue value = values[ j ];
      boolean passed = true;
      for( int k = 0; k < value.constraints.length && passed; k++ ) {
        String constraint = value.constraints[ k ];
        if( constraint.charAt( 0 ) == ':' ) {
          passed &= contains( element.getStates(), constraint.substring( 1 ) );
        } else if( constraint.charAt( 0 ) == '[' ) {
          passed &= contains( element.getStyles(), constraint.substring( 1 ) );
        }
      }
      if( passed ) {
        resultList.add( value );
      }
    }
    ConditionalValue[] result = new ConditionalValue[ resultList.size() ];
    resultList.toArray( result );
    return result;
  }

  private boolean contains( final IThemeCssAttribute[] attributes,
                            final String name )
  {
    boolean result = false;
    for( int i = 0; i < attributes.length && !result; i++ ) {
      IThemeCssAttribute themeCssAttribute = attributes[ i ];
      if( name.equals( themeCssAttribute.getName() ) ) {
        result = true;
      }
    }
    return result;
  }

  private void add( final String elementName,
                    final String propertyName,
                    final ConditionalValue[] values )
  {
    map.put( getKey( elementName, propertyName ), values );
  }

  private String getKey( final String elementName, final String propertyName ) {
    // TODO [rst] Improve
    return elementName + "/" + propertyName;
  }
}
