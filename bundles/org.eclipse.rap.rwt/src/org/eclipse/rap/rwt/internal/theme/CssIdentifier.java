/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;


public class CssIdentifier implements CssValue {

  public final String value;

  public CssIdentifier( String value ) {
    this.value = value;
  }

  @Override
  public String toDefaultString() {
    return value;
  }

  @Override
  public String toString() {
    return "CssIdentifier{ " + value + " }";
  }

  @Override
  public boolean equals( Object object ) {
    if( object == this ) {
      return true;
    }
    if( object instanceof CssIdentifier ) {
      CssIdentifier other = ( CssIdentifier )object;
      return value.equals( other.value );
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

}
