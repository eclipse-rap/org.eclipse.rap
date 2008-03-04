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

public final class QxImage implements QxType {

  private static final String NONE_INPUT = "none";

  public static final QxImage NONE = new QxImage( true, null, null );

  public final boolean none;

  public final String path;

  public final ResourceLoader loader;

  /**
   * Creates a new image from the given value.
   *
   * @param path the definition string to create the image from. Either
   *            <code>none</code> or a path to an image
   * @param loader a resource loader which is able to load the image from the
   *            given path
   */
  private QxImage( final boolean none,
                   final String path,
                   final ResourceLoader loader )
  {
    this.none = none;
    this.path = path;
    this.loader = loader;
  }

  public static QxImage valueOf( final String input, final ResourceLoader loader )
  {
    QxImage result;
    if( NONE_INPUT.equals( input ) ) {
      result = NONE;
    } else {
      if( input == null || loader == null ) {
        throw new NullPointerException( "null argument" );
      }
      if( input.length() == 0 ) {
        throw new IllegalArgumentException( "Empty image path" );
      }
      result = new QxImage( false, input, loader );
    }
    return result;
  }

  public String toDefaultString() {
    // returns an empty string, because the default resource path is only valid
    // for the bundle that specified it
    return none ? NONE_INPUT : "";
  }

  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxImage ) {
      QxImage other = ( QxImage )object;
      result = path != null
               && path.equals( other.path )
               && loader != null
               && loader.equals( other.loader );
    }
    return result;
  }

  public int hashCode() {
    return path.hashCode();
  }

  public String toString() {
    return "QxImage{ "
           + ( none ? NONE_INPUT : path )
           + " }";
  }
}
