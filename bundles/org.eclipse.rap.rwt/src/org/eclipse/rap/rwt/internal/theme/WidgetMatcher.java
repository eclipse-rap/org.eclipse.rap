/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.swt.widgets.Widget;


public final class WidgetMatcher implements ValueSelector {

  public static interface Constraint {
    boolean matches( Widget widget );
  }

  // TODO [rst] Optimize, linear search might be faster than hashmap overhead
  private final Map<String,Constraint> constraintMap;

  public WidgetMatcher() {
    constraintMap = new HashMap<String,Constraint>();
  }

  public void addStyle( String string, int style ) {
    constraintMap.put( "[" + string, createStyleConstraint( style ) );
  }

  public void addState( String string, Constraint constraint ) {
    constraintMap.put( ":" + string, constraint );
  }

  public CssType select( ConditionalValue[] values, Widget widget ) {
    CssType result = null;
    for( int i = 0; i < values.length && result == null; i++ ) {
      ConditionalValue condValue = values[ i ];
      String[] constraints = condValue.constraints;
      if( matches( widget, constraints ) ) {
        result = condValue.value;
      }
    }
    return result;
  }

  public static Constraint createStyleConstraint( final int style ) {
    Constraint matcher = new Constraint() {
      public boolean matches( Widget widget ) {
        return ( widget.getStyle() & style ) != 0;
      }
    };
    return matcher;
  }

  private boolean matches( Widget widget, String[] constraints ) {
    boolean result = true;
    for( int i = 0; i < constraints.length && result; i++ ) {
      String string = constraints[ i ];
      if( string.startsWith( "." ) ) {
        String variant = string.substring( 1 );
        result &= hasVariant( widget, variant );
      } else {
        Constraint constraint = constraintMap.get( string );
        result &= constraint != null && constraint.matches( widget );
      }
    }
    return result;
  }

  private static boolean hasVariant( Widget widget, String variant ) {
    String actualVariant = WidgetUtil.getVariant( widget );
    return actualVariant != null && actualVariant.equals( variant );
  }
}
