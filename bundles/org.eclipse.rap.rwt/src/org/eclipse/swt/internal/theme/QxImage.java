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

public class QxImage implements QxType {
  
  private final String path;
  
  public QxImage( final String value ) {
    this.path = value;
  }
  
  public String getPath() {
    return path;
  }
  
  public String toDefaultString() {
    // returns an empty string, because the default resource path is not to be
    // displayed to the user
    return "";
  }
  
  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxImage ) {
      QxImage other = (QxImage)object;
      result = ( other.path.equals( this.path ) );
    }
    return result;
  }

  public int hashCode () {
    return path.hashCode();
  }

  public String toString () {
    return "QxImage {"
           + path
           + "}";
  }
}
