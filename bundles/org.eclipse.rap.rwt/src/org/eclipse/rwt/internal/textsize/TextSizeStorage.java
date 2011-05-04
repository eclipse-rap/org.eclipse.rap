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

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


public final class TextSizeStorage {
  public static final int MIN_STORE_SIZE = 1000;
  public static final int DEFAULT_STORE_SIZE = 10000;

  private final Object lock;
  // access is guarded by 'lock'
  private final Set fontDatas;
  // access is guarded by 'lock'
  private final Map data;
  private int maximumStoreSize;
  private int clearRange;
  private long clock;


  private static class Entry {
    private Point point;
    private long timeStamp;
  }
  
  private static class EntryComparator implements Comparator {
    
    public int compare( Object obj1, Object obj2 ) {
      Entry entry1 = ( Entry )obj1;
      Entry entry2 = ( Entry )obj2;
      int result = 0;
      if( entry1.timeStamp > entry2.timeStamp ) {
        result = 1;
      } else if( entry1.timeStamp < entry2.timeStamp ) {
        result = -1;
      }
      return result;
    }
  }


  public TextSizeStorage() {
    lock = new Object();
    data = new HashMap();
    fontDatas = new HashSet();
    setMaximumStoreSize( DEFAULT_STORE_SIZE );
  }

  FontData[] getFontList() {
    FontData[] result;
    synchronized( lock ) {
      result = new FontData[ fontDatas.size() ];
      fontDatas.toArray( result );
    }
    return result;
  }

  void storeFont( FontData fontData ) {
    synchronized( lock ) {
      fontDatas.add( fontData );
    }
  }

  Point lookupTextSize( Integer key ) {
    Point result = null;
    synchronized( lock ) {
      Entry entry = ( Entry )data.get( key );
      if( entry != null ) {
        updateTimestamp( entry );
        result = entry.point;
      }
    }
    result = defensiveCopy( result );
    return result;
  }

  void storeTextSize( Integer key, Point size ) {
    Entry entry = new Entry();
    entry.point = defensiveCopy( size );
    updateTimestamp( entry );
    synchronized( lock ) {
      data.put( key , entry );
      handleOverFlow();
    }
  }

  ////////////////////
  // overflow handling

  void setMaximumStoreSize( int maximumStoreSize ) {
    checkLowerStoreSizeBoundary( maximumStoreSize );
    calculateClearRange( maximumStoreSize );
    this.maximumStoreSize = maximumStoreSize;
  }

  int getMaximumStoreSize() {
    return maximumStoreSize;
  }

  private void handleOverFlow() {
    if( data.size() >= maximumStoreSize ) {
      Entry[] entries = sortEntries();
      for( int i = 0; i < clearRange; i++ ) {
        data.values().remove( entries[ i ] );
      }
    }
  }

  private Entry[] sortEntries() {
    Entry[] result = new Entry[ data.size() ];
    data.values().toArray( result );
    Arrays.sort( result, new EntryComparator() );
    return result;
  }


  //////////////////
  // helping methods

  private void checkLowerStoreSizeBoundary( int maximumStoreSize ) {
    if( maximumStoreSize < MIN_STORE_SIZE ) {
      Object[] param = new Object[] { new Integer( MIN_STORE_SIZE ) };
      String msg = MessageFormat.format( "Store size must be >= {0}.", param );
      throw new IllegalArgumentException( msg );
    }
  }

  private static Point defensiveCopy( Point point ) {
    Point result = null;
    if( point != null ) {
      result = new Point( point.x, point.y );
    }
    return result;
  }
  
  private void updateTimestamp( Entry entry ) {
    entry.timeStamp = clock++;
  }

  private void calculateClearRange( int maximumStoreSize ) {
    BigDecimal ten = new BigDecimal( 10 );
    BigDecimal bdStoreSize = new BigDecimal( maximumStoreSize );
    clearRange = bdStoreSize.divide( ten, 0, BigDecimal.ROUND_HALF_UP ).intValue();
  }
}