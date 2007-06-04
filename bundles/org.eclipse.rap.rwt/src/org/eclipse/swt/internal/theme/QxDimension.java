/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

public class QxDimension implements QxType {
  
  private final int value;
  
  public QxDimension( final String value ) {
    int intValue = Integer.parseInt( value );
    this.value = intValue;
  }
  
  public QxDimension( final int value ) {
    this.value = value;
  }
  
  public int getInt() {
    return value;
  }
  
  public String toDefaultString() {
    return String.valueOf( value );
  }
  
  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxDimension ) {
      QxDimension other = (QxDimension)object;
      result = ( other.value == this.value );
    }
    return result;
}

  public int hashCode () {
    return value;
  }

  public String toString () {
    return "QxDimension {"
           + value
           + "}";
  }
}
