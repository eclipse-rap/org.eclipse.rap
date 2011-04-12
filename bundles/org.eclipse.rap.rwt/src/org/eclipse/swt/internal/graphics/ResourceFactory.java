/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.swt.graphics.*;


public class ResourceFactory {
  
  private final Map colors;
  private final Map fonts;
  private final Map cursors;
  
  public ResourceFactory() {
    colors = new HashMap();
    fonts = new HashMap();
    cursors = new HashMap();
  }
  
  public Color getColor( int red, int green, int blue ) {
    int colorNr = ColorUtil.computeColorNr( red, green, blue );
    return getColor( colorNr );
  }

  private Color getColor( int value ) {
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

  public Font getFont( FontData fontData ) {
    Font result;
    Integer key = new Integer( fontData.hashCode() );
    synchronized( fonts ) {
      result = ( Font )fonts.get( key );
      if( result == null ) {
        result = createFontInstance( fontData );
        fonts.put( key, result );
      }
    }
    return result;
  }

  public Cursor getCursor( int style ) {
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

  private static Color createColorInstance( int colorNr ) {
    Class[] paramTypes = new Class[] { int.class };
    Object[] paramValues = new Object[] { new Integer( colorNr ) };
    return ( Color )ClassUtil.newInstance( Color.class, paramTypes, paramValues );
  }

  private static Font createFontInstance( FontData fontData ) {
    Class[] paramTypes = new Class[] { FontData.class };
    Object[] paramValues = new Object[] { fontData };
    return ( Font )ClassUtil.newInstance( Font.class, paramTypes, paramValues );
  }

  private static Cursor createCursorInstance( int style ) {
    Class[] paramTypes = new Class[] { int.class };
    Object[] paramValues = new Object[] { new Integer( style ) };
    return ( Cursor )ClassUtil.newInstance( Cursor.class, paramTypes, paramValues );
  }
}
