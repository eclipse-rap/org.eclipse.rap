/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

public class QxImage implements QxType {

  private static final String NONE = "none";

  public final boolean none;

  public final String path;

  public final ResourceLoader loader;

  /**
   * Creates a new image from the given value.
   * 
   * @param value the definition string to create the image from. Either
   *            <code>none</code> or a path to an image
   * @param loader a resource loader which is able to load the image from the
   *            given path
   */
  public QxImage( final String value,
                  final ResourceLoader loader )
  {
    if( value == null || loader == null ) {
      throw new NullPointerException( "null argument" );
    }
    none = NONE.equals( value );
    path = none ? null : value;
    this.loader = loader;
  }

  public String toDefaultString() {
    // returns an empty string, because the default resource path is not to be
    // displayed to the user
    return none ? NONE : "";
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
    return "QxImage{ "
           + ( none ? NONE : "path=" + path )
           + " }";
  }
}
