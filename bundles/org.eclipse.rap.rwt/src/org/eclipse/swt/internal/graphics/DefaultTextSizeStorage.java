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

package org.eclipse.swt.internal.graphics;

import java.io.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.*;


public class DefaultTextSizeStorage implements ITextSizeStorage {

  public static final int MIN_STORE_SIZE = 1000;
  public static final int DEFAULT_STORE_SIZE = 10000;

  static final String COMMENT = "RAP DefaultFontSizeStorage";
  static final String PREFIX_FONT_KEY = "FONT_";
  private static Set fonts = new HashSet();
  private static Map data = new HashMap();
  private static Object lock = new Object();
  private static int storeSize ;
  private static int clearRange;
  private static int clock;
  static {
    setStoreSize( DEFAULT_STORE_SIZE );
  }

  private static class Entry {
    private Point point;
    private long timeStamp;
  }


  /////////////////////////////
  // interface IFontSizeStorage

  public Font[] getFontList() {
    Font[] result;
    synchronized( lock ) {
      result = new Font[ fonts.size() ];
      fonts.toArray( result );
    }
    return result;
  }

  public void storeFont( final Font font ) {
    synchronized( lock ) {
      fonts.add( font );
    }
  }

  public Point lookupTextSize( final Integer key ) {
    Point result = null;
    synchronized( lock ) {
      Entry entry = ( Entry )data.get( key );
      if( entry != null ) {
        entry.timeStamp = clock++;
        result = entry.point;
      }
    }
    result = defensiveCopy( result );
    return result;
  }

  public void storeTextSize( final Integer key,
                               final Point size )
  {
    Point clone = defensiveCopy( size );
    Entry entry = new Entry();
    entry.point = clone;
    entry.timeStamp = clock++;
    synchronized( lock ) {
      data.put( key , entry );
      handleOverFlow();
    }
  }

  //////////////
  // persistance

  public void save( final OutputStream out ) throws IOException {
    Properties properties = new Properties();
    Font[] fontList;
    synchronized( lock ) {
      fontList = getFontList();
      Iterator iterator = data.keySet().iterator();
      while( iterator.hasNext() ) {
        Integer key = ( Integer )iterator.next();
        Point size = ( ( Entry )data.get( key ) ).point;
        StringBuffer value = new StringBuffer();
        value.append( size.x );
        value.append( "," );
        value.append(  size.y );
        properties.setProperty( key.toString(), value.toString() );
      }
    }
    for( int i = 0; i < fontList.length; i++ ) {
      StringBuffer key = new StringBuffer();
      key.append( PREFIX_FONT_KEY );
      key.append( i );
      String value = fontList[ i ].getFontData()[ 0 ].toString();
      properties.setProperty( key.toString(), value );
    }
    properties.store( out, COMMENT );
  }

  public void read( final InputStream in ) throws IOException {
    Properties properties = new Properties();
    properties.load( in );
    synchronized( lock ) {
      Enumeration keys = properties.keys();
      while( keys.hasMoreElements() ) {
        String key = ( String )keys.nextElement();
        String value = properties.getProperty( key );
        if( key.startsWith( PREFIX_FONT_KEY ) ) {
          FontData fontData = new FontData( value );
          storeFont( Graphics.getFont( fontData ) );
        } else {
          storeTextSize( new Integer( key ), parsePoint( value ) );
        }
      }
    }
  }


  ////////////////////
  // overflow handling

  public static void setStoreSize( final int storeSize ) {
    if( storeSize < MIN_STORE_SIZE ) {
      String txt = "Store size must be >= {0}.";
      Object[] param = new Object[] { new Integer( MIN_STORE_SIZE ) };
      String msg = MessageFormat.format( txt, param );
      throw new IllegalArgumentException( msg );
    }
    // TODO [rh] the buid server seems to compile againts JDK >= 1.5
    //      this causes NoSuchMethodError: java.math.BigDecimal.<init>(I)V
    //      when running with JDK 1.4. See
    //      http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6226858
    //      The fix is to explicitly call BigDecimal(double)
    BigDecimal ten = new BigDecimal( 10.0 );
    BigDecimal bdStoreSize = new BigDecimal( storeSize );
    int rounding = BigDecimal.ROUND_HALF_UP;
    clearRange = bdStoreSize.divide( ten, 0, rounding ).intValue();
    DefaultTextSizeStorage.storeSize = storeSize;
  }

  public static int getStoreSize() {
    return storeSize;
  }

  private void handleOverFlow() {
    if( data.size() >= DefaultTextSizeStorage.storeSize ) {
      Entry[] entries = new Entry[ data.size() ];
      data.values().toArray( entries );
      Arrays.sort( entries, new Comparator() {
        public int compare( final Object obj1, final Object obj2 ) {
          Entry e1 = ( Entry )obj1;
          Entry e2 = ( Entry )obj2;
          int result = 0;
          if( e1.timeStamp > e2.timeStamp ) {
            result = 1;
          } else if( e1.timeStamp < e2.timeStamp ) {
            result = -1;
          }
          return result;
        }
      } );
      for( int i = 0; i < clearRange; i++ ) {
        data.values().remove( entries[ i ] );
      }
    }
  }


  //////////////////
  // helping methods

  private Point parsePoint( final String value ) {
    String[] values = value.split( "," );
    int x = Integer.valueOf( values[ 0 ] ).intValue();
    int y = Integer.valueOf( values[ 1 ] ).intValue();
    return new Point( x, y );
  }

  // for test purposes only
  void resetFontList() {
    synchronized( fonts ) {
      fonts.clear();
    }
  }

  // for test purposes only
  void resetStringSizes() {
    synchronized( data ) {
      data.clear();
    }
  }

  private static Point defensiveCopy( final Point point ) {
    Point result = null;
    if( point != null ) {
      result = new Point( point.x, point.y );
    }
    return result;
  }
}
