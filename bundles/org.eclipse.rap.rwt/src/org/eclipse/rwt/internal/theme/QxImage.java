/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.ResourceFactory;

public final class QxImage implements QxType {

  private static final String NONE_INPUT = "none";

  public static final QxImage NONE = new QxImage( true, null, null, null, null );

  public final boolean none;

  public final String path;

  public final ResourceLoader loader;

  public final String[] gradientColors;

  public final float[] gradientPercents;

  public final int width;

  public final int height;

  /**
   * Creates a new image from the given value.
   *
   * @param path the definition string to create the image from. Either
   *            <code>none</code> or a path to an image
   * @param loader a resource loader which is able to load the image from the
   *            given path
   * @param gradientColors an array with gradient colors
   * @param gradientPercents an array with gradient percents
   */
  private QxImage( final boolean none,
                   final String path,
                   final ResourceLoader loader,
                   final String[] gradientColors,
                   final float[] gradientPercents )
  {
    this.none = none;
    this.path = path;
    this.loader = loader;
    this.gradientColors = gradientColors;
    this.gradientPercents = gradientPercents;
    if( none ) {
      width = 0;
      height = 0;
    } else {
      try {
        Point size = readImageSize( path, loader );
        if( size == null ) {
          throw new IllegalArgumentException( "Failed to read image '"
                                              + path
                                              + "'" );
        }
        width = size.x;
        height = size.y;
      } catch( IOException e ) {
        throw new IllegalArgumentException( "Failed to read image "
                                            + path
                                            + ": "
                                            + e.getMessage() );
      }
    }
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
      result = new QxImage( false, input, loader, null, null );
    }
    return result;
  }

  public static QxImage createGradient( final String[] gradientColors,
                                        final float[] gradientPercents )
  {
    QxImage result;
    if( gradientColors == null || gradientPercents == null ) {
      throw new NullPointerException( "null argument" );
    }
    result = new QxImage( true, null, null, gradientColors, gradientPercents );
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
      result =    ( path == null
                    ? other.path == null
                    : path.equals( other.path ) )
               && ( loader == null
                    ? other.loader == null
                    : loader.equals( other.loader ) )
               && ( gradientColors == null
                    ? other.gradientColors == null
                    : Arrays.equals( gradientColors, other.gradientColors ) )
               && ( gradientPercents == null
                    ? other.gradientPercents == null
                    : Arrays.equals( gradientPercents, other.gradientPercents ) );
    }
    return result;
  }

  public int hashCode() {
    int result = -1;
    if( none ) {
      if( gradientColors != null && gradientPercents != null ) {
        result = 29;
        for( int i = 0; i < gradientColors.length; i++ ) {
          result += 31 * result + gradientColors[ i ].hashCode();
        }
        for( int i = 0; i < gradientPercents.length; i++ ) {
          result += 31 * result + Float.floatToIntBits( gradientPercents[ i ] );
        }
      }
    } else {
      result = path.hashCode();
    }
    return result;
  }

  public String toString() {
    return   "QxImage{ "
           + ( none ? NONE_INPUT : path )
           + " }";
  }

  private static Point readImageSize( final String path,
                                      final ResourceLoader loader )
    throws IOException
  {
    Point result = null;
    InputStream inputStream = loader.getResourceAsStream( path );
    if( inputStream != null ) {
      try {
        result = ResourceFactory.readImageSize( inputStream );
      } finally {
        inputStream.close();
      }
    }
    return result;
  }
}
