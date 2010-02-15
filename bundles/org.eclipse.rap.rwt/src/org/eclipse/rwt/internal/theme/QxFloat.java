/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

public class QxFloat implements QxType {

  private static final QxFloat ZERO = new QxFloat( 0 );
  private static final QxFloat ONE = new QxFloat( 1 );

  public static QxFloat create( float value ) {
    QxFloat result;
    if( value == 0 ) {
      result = ZERO;
    } else if( value == 1 ) {
      result = ONE;
    } else {
      result = new QxFloat( value );
    }
    return result;
  }
  
  public static QxFloat valueOf( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "input" );
    }    
    return create( Float.parseFloat( input ) );
  }
  
  public final float value;

  private QxFloat( final float value ) {
    this.value = value;
  }

  public String toDefaultString() {
    return String.valueOf( value );
  }

  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxFloat ) {
      QxFloat other = ( QxFloat )object;
      result = other.value == value;
    }
    return result;
  }

  public String toString() {
    return "QxFloat{ " + String.valueOf( value ) + " }";
  }
}
