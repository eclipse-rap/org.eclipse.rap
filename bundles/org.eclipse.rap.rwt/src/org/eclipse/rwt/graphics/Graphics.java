/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.graphics;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public class Graphics {

  private final static Map colorPalette = new HashMap();

  private static final Map fonts = new HashMap();

  // === COLORS ===

  public static Color getColor( final RGB rgb ) {
    if( rgb == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return getColor( rgb.red, rgb.green, rgb.blue );
  }

  public static Color getColor( final int red,
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
    int colorNr = red | ( green << 8 ) | ( blue << 16 );
    return getColor( colorNr );
  }

  public static synchronized Color getColor( final int color ) {
    Color result;
    Integer key = new Integer( color );
    if( colorPalette.containsKey( key ) ) {
      result = ( Color )colorPalette.get( key );
    } else {
      result = createColorInstance( color );
      colorPalette.put( key, result );
    }
    return result;
  }

  // === FONTS ===

  /**
   * TODO [fappel]: comment
   */
  public static Font getFont( final FontData data ) {
    return getFont( data.getName(), data.getHeight(), data.getStyle() );
  }

  /**
   * TODO [fappel]: comment
   */
  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    validateFontParams( name, height );
    int checkedStyle = checkFontStyle( style );
    Font result;
    FontData fontData = new FontData( name, height, checkedStyle );
    synchronized( Font.class ) {
      result = ( Font )fonts.get( fontData );
      if( result == null ) {
        result = createFontInstance( fontData );
        fonts.put( fontData, result );
      }
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static void validateFontParams( final String name, final int height ) {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( height < 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
  }

  private static int checkFontStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }

  ////////////////////
  // Instance creation

  private static Color createColorInstance( final int colorNr ) {
    Color result = null;
    try {
      Class colorClass = Color.class;
      Class[] classes = colorClass.getDeclaredClasses();
      Class colorExtClass = classes[ 0 ];
      Class[] paramList = new Class[] { int.class };
      Constructor constr = colorExtClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      Object[] param = new Object[] { new Integer( colorNr ) };
      result = ( Color )constr.newInstance( param );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Color", e );
    }
    return result;
  }

  private static Font createFontInstance( final FontData fontData ) {
    Font result = null;
    try {
      Class fontClass = Font.class;
      Class[] paramList = new Class[] { FontData.class };
      Constructor constr = fontClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Font )constr.newInstance( new Object[] { fontData } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Font", e );
    }
    return result;
  }
}
