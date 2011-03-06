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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.*;


public class ResourceFactoryInstance {
  private final Map colors;
  private final Map fonts;
  private final Map cursors;
  
  
  private ResourceFactoryInstance() {
    colors = new HashMap();
    fonts = new HashMap();
    cursors = new HashMap();
  }
  
  Color getColor( final int value ) {
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

  Font getFont( final FontData fontData ) {
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

  Cursor getCursor( final int style ) {
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

  void clear() {
    colors.clear();
    fonts.clear();
    cursors.clear();
  }

  int getColorsCount() {
    return colors.size();
  }

  int getFontsCount() {
    return fonts.size();
  }

  int getCursorsCount() {
    return cursors.size();
  }

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
}
