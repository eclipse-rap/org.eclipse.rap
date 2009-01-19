/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.swt.widgets.Widget;


/**
 * Selects values based on constraints, without comparing to the given widget.
 */
public final class SimpleSelector implements ValueSelector {

  private final String[] requiredConstraints;

  public static final SimpleSelector DEFAULT
    = new SimpleSelector( new String[ 0 ] );

  public static final SimpleSelector SELECTED
    = new SimpleSelector( new String[] { ":selected" } );

  public static final SimpleSelector INACTIVE
    = new SimpleSelector( new String[] { ":inactive" } );

  public SimpleSelector( final String[] constraints ) {
    this.requiredConstraints = constraints;
  }

  public QxType select( final ConditionalValue[] values, final Widget widget ) {
    QxType result = null;
    for( int i = 0; i < values.length && result == null; i++ ) {
      ConditionalValue condValue = values[ i ];
      String[] constraints = condValue.constraints;
      if( matches( constraints ) ) {
        result = condValue.value;
      }
    }
    return result;
  }

  private boolean matches( final String[] constraints ) {
    boolean result = true;
    for( int i = 0; i < constraints.length && result; i++ ) {
      result = contains( requiredConstraints, constraints[ i ] );
    }
    return result;
  }

  private static boolean contains( final String[] array, final String string ) {
    boolean result = false;
    for( int i = 0; i < array.length && !result; i++ ) {
      result = array[ i ].equals( string );
    }
    return result;
  }
}
