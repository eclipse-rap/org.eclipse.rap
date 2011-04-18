/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.io.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


public final class DefaultTextSizeStorage implements ITextSizeStorage {

  public static final int MIN_STORE_SIZE = 1000;
  public static final int DEFAULT_STORE_SIZE = 10000;

  static final String COMMENT = "RAP DefaultFontSizeStorage";
  static final String PREFIX_FONT_KEY = "FONT_";
  
  private static class Entry {
    private Point point;
    private long timeStamp;
  }
  
  private final Object lock;
  // access is guarded by 'lock'
  private final Set fontDatas;
  // access is guarded by 'lock'
  private final Map data;
  private int storeSize;
  private int clearRange;
  private int clock;

  public DefaultTextSizeStorage() {
    lock = new Object();
    data = new HashMap();
    fontDatas = new HashSet();
    setStoreSize( DEFAULT_STORE_SIZE );
  }

  /////////////////////////////
  // interface IFontSizeStorage

  public FontData[] getFontList() {
    FontData[] result;
    synchronized( lock ) {
      result = new FontData[ fontDatas.size() ];
      fontDatas.toArray( result );
    }
    return result;
  }

  public void storeFont( final FontData fontData ) {
    synchronized( lock ) {
      fontDatas.add( fontData );
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

  public void storeTextSize( final Integer key, final Point size ) {
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

  public void save( final OutputStream stream ) throws IOException {
    Properties properties = new Properties();
    FontData[] fontDataList;
    synchronized( lock ) {
      fontDataList = getFontList();
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
    for( int i = 0; i < fontDataList.length; i++ ) {
      StringBuffer key = new StringBuffer();
      key.append( PREFIX_FONT_KEY );
      key.append( i );
      String value = fontDataList[ i ].toString();
      properties.setProperty( key.toString(), value );
    }
    properties.store( stream, COMMENT );
  }

  public void read( final InputStream stream ) throws IOException {
    Properties properties = new Properties();
    properties.load( stream );
    synchronized( lock ) {
      Enumeration keys = properties.keys();
      while( keys.hasMoreElements() ) {
        String key = ( String )keys.nextElement();
        String value = properties.getProperty( key );
        if( key.startsWith( PREFIX_FONT_KEY ) ) {
          FontData fontData = new FontData( value );
          storeFont( fontData );
        } else {
          storeTextSize( new Integer( key ), parsePoint( value ) );
        }
      }
    }
  }


  ////////////////////
  // overflow handling

  public void setStoreSize( final int storeSize ) {
    if( storeSize < MIN_STORE_SIZE ) {
      String txt = "Store size must be >= {0}.";
      Object[] param = new Object[] { new Integer( MIN_STORE_SIZE ) };
      String msg = MessageFormat.format( txt, param );
      throw new IllegalArgumentException( msg );
    }
    BigDecimal ten = new BigDecimal( 10 );
    BigDecimal bdStoreSize = new BigDecimal( storeSize );
    int rounding = BigDecimal.ROUND_HALF_UP;
    clearRange = bdStoreSize.divide( ten, 0, rounding ).intValue();
    this.storeSize = storeSize;
  }

  public int getStoreSize() {
    return storeSize;
  }

  private void handleOverFlow() {
    if( data.size() >= storeSize ) {
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

  private static Point parsePoint( final String value ) {
    String[] values = value.split( "," );
    int x = Integer.valueOf( values[ 0 ] ).intValue();
    int y = Integer.valueOf( values[ 1 ] ).intValue();
    return new Point( x, y );
  }

  private static Point defensiveCopy( final Point point ) {
    Point result = null;
    if( point != null ) {
      result = new Point( point.x, point.y );
    }
    return result;
  }

  // for test purposes only
  void resetFontList() {
    synchronized( fontDatas ) {
      fontDatas.clear();
    }
  }
  
  // for test purposes only
  void resetStringSizes() {
    synchronized( data ) {
      data.clear();
    }
  }
  
}
