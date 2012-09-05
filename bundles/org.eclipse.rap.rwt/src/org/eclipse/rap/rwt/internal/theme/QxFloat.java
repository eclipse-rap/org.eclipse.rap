/*******************************************************************************
 * Copyright (c) 2010, 2012 Innoopract Informationssysteme GmbH and others.
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

  public static QxFloat valueOf( String input ) {
    if( input == null ) {
      throw new NullPointerException( "input" );
    }
    return create( Float.parseFloat( input ) );
  }

  public final float value;

  private QxFloat( float value ) {
    this.value = value;
  }

  public String toDefaultString() {
    return String.valueOf( value );
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj ) {
      return true;
    }
    if( obj == null ) {
      return false;
    }
    if( getClass() != obj.getClass() ) {
      return false;
    }
    QxFloat other = ( QxFloat )obj;
    if( Float.floatToIntBits( value ) != Float.floatToIntBits( other.value ) ) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits( value );
    return result;
  }

  @Override
  public String toString() {
    return "QxFloat{ " + String.valueOf( value ) + " }";
  }

}
