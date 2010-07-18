/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public final class ResourceFactory {

  private static final Map colors = new HashMap();
  private static final Map fonts = new HashMap();
  private static final Map cursors = new HashMap();

  /////////
  // Colors

  public static Color getColor( final int red,
                                final int green,
                                final int blue )
  {
    int colorNr = computeColorNr( red, green, blue );
    return getColor( colorNr );
  }

  public static int computeColorNr( final int red,
                                    final int green,
                                    final int blue )
  {
    if(    red > 255
        || red < 0
        || green > 255
        || green < 0
        || blue > 255
        || blue < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int colorNr = red | green << 8 | blue << 16;
    return colorNr;
  }

  private static Color getColor( final int value ) {
    Color result;
    Integer key = new Integer( value );
    synchronized( colors ) {
      if( colors.containsKey( key ) ) {
        result = ( Color )colors.get( key );
      } else {
        result = createColorInstance( value );
        colors.put( key, result );
      }
    }
    return result;
  }


  ////////
  // Fonts

  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    int checkedStyle = checkFontStyle( style );
    Font result;
    Integer key = new Integer( fontHashCode( name, height, checkedStyle ) );
    synchronized( fonts ) {
      result = ( Font )fonts.get( key );
      if( result == null ) {
        FontData fontData = new FontData( name, height, checkedStyle );
        result = createFontInstance( fontData );
        fonts.put( key, result );
      }
    }
    return result;
  }

  public static int fontHashCode( final String name,
                                  final int height,
                                  final int style )
  {
    int nameHashCode = name == null ? 0 : name.hashCode();
    return nameHashCode ^ height << 2 ^ style;
  }

  public static int checkFontStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }

  public static String getImagePath( final Image image ) {
    return ImageFactory.getImagePath( image );
  }

  public static Cursor getCursor( final int style ) {
    Cursor result;
    Integer key = new Integer( style );
    synchronized( Cursor.class ) {
      result = ( Cursor )cursors.get( key );
      if( result == null ) {
        result = createCursorInstance( style );
        cursors.put( key, result );
      }
    }
    return result;
  }

  ///////////////
  // Test helpers

  public static void clear() {
    colors.clear();
    fonts.clear();
    cursors.clear();
    ImageFactory.clear();
    InternalImageFactory.clear();
    ImageDataFactory.clear();
  }

  static int colorsCount() {
    return colors.size();
  }

  static int fontsCount() {
    return fonts.size();
  }

  static int cursorsCount() {
    return cursors.size();
  }


  //////////////////
  // Helping methods

  private static Color createColorInstance( final int colorNr ) {
    Color result = null;
    try {
      Class[] paramList = new Class[] { int.class };
      Constructor constr = Color.class.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      Object[] args = new Object[] { new Integer( colorNr ) };
      result = ( Color )constr.newInstance( args );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Color", e );
    }
    return result;
  }

  private static Font createFontInstance( final FontData fontData ) {
    Font result = null;
    try {
      Class[] paramList = new Class[] { FontData.class };
      Constructor constr = Font.class.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Font )constr.newInstance( new Object[] { fontData } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Font", e );
    }
    return result;
  }

  private static Cursor createCursorInstance( final int style ) {
    Cursor result = null;
    try {
      Class cursorClass = Cursor.class;
      Class[] paramList = new Class[] { int.class };
      Constructor constr = cursorClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Cursor )constr.newInstance( new Object[] {
        new Integer( style )
      } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Cursor", e );
    }
    return result;
  }

  private ResourceFactory() {
    // prevent instantiation
  }
}
