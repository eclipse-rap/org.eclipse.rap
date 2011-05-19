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

import org.eclipse.rwt.internal.util.*;
import org.eclipse.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.swt.graphics.*;


public class ResourceFactory {
  
  private final SharedInstanceBuffer colors;
  private final SharedInstanceBuffer fonts;
  private final SharedInstanceBuffer cursors;
  
  public ResourceFactory() {
    colors = new SharedInstanceBuffer();
    fonts = new SharedInstanceBuffer();
    cursors = new SharedInstanceBuffer();
  }
  
  public Color getColor( int red, int green, int blue ) {
    int colorNr = ColorUtil.computeColorNr( red, green, blue );
    return getColor( colorNr );
  }

  private Color getColor( final int value ) {
    Integer key = new Integer( value );
    return ( Color )colors.get( key, new IInstanceCreator() {
      public Object createInstance() {
        return createColorInstance( value );
      }
    } );
  }

  public Font getFont( final FontData fontData ) {
    Integer key = new Integer( fontData.hashCode() );
    return ( Font )fonts.get( key, new IInstanceCreator() {
      public Object createInstance() {
        return createFontInstance( fontData );
      }
    } );
  }

  public Cursor getCursor( final int style ) {
    Integer key = new Integer( style );
    return ( Cursor )cursors.get( key, new IInstanceCreator() {
        public Object createInstance() {
          return createCursorInstance( style );
        }
      } );
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
