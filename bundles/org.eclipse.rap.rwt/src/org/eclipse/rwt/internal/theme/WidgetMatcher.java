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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.theme.css.StyleSheet.ConditionalValue;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public final class WidgetMatcher {

  public static interface Constraint {
  
    boolean matches( Widget widget );
  }

  public static final Constraint CONTROL_ENABLED = new Constraint() {

    public boolean matches( final Widget widget ) {
      return ( ( Control )widget ).isEnabled();
    }
  };

  // TODO [rst] Optimize, linear search might be faster than hashmap overhead
  private final Map constraintMap = new HashMap();

  public void addStyle( final String string, final int style )
  {
    constraintMap.put( "[" + string, createStyleConstraint( style ) );
  }

  public void addState( final String string, final Constraint constraint )
  {
    constraintMap.put( ":" + string, constraint );
  }

  public QxType select( final ConditionalValue[] values, final Widget widget )
  {
    QxType result = null;
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

      public boolean matches( final Widget widget ) {
        return ( widget.getStyle() & style ) != 0;
      }
    };
    return matcher;
  }

  private boolean matches( final Widget widget, final String[] constraints ) {
    boolean result = true;
    for( int i = 0; i < constraints.length && result; i++ ) {
      String string = constraints[ i ];
      if( string.startsWith( "." ) ) {
        String variant = string.substring( 1 );
        result &= hasVariant( widget, variant );
      } else {
        Constraint constraint = ( Constraint )constraintMap.get( string );
        result &= constraint != null && constraint.matches( widget );
      }
    }
    return result;
  }

  private static boolean hasVariant( final Widget widget, final String variant )
  {
    String actualVariant = WidgetUtil.getVariant( widget );
    return actualVariant != null && variant.equals( variant );
  }
}
