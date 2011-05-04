/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


public class TextSizeStorage_Test extends TestCase {
  private static final FontData FONT_DATA_1 = new FontData( "arial", 10, SWT.NORMAL );
  private static final FontData FONT_DATA_2 = new FontData( "helvetia", 12, SWT.NORMAL );
  private static final Integer KEY_FIRST = new Integer( 0 );
  private static final Integer KEY_OVERFLOW = new Integer( Integer.MAX_VALUE );
  private static final Point SIZE_FIRST = new Point( 0, 0 );
  private static final Point SIZE_OVERFLOW = new Point( -1, -1 );
  
  private TextSizeStorage storage;

  public void testFontStorage() {
    storage.storeFont( FONT_DATA_1 );
    storage.storeFont( FONT_DATA_2 );
    FontData[] fontList = storage.getFontList();
    
    assertEquals( 2, fontList.length );
    assertTrue( Arrays.asList( fontList ).contains( fontList[ 0 ] ) );
    assertTrue( Arrays.asList( fontList ).contains( fontList[ 1 ] ) );
  }
  
  public void testTextSizeStorage() {
    Integer key = new Integer( 1 );
    Point size = new Point( 1, 4 );
    storage.storeTextSize( key, size );
    
    Point foundSize = storage.lookupTextSize( key );
    
    assertEquals( size, foundSize );
    assertNotSame( size, foundSize );
  }
  
  public void testStorageOverflowHandling() {
    populateUntilOverflowThresholdIsReached();
    updateTimestampOnFirstEntry();

    forceOverflow();

    checkTimestampOrdering();
    checkLatestEntriesExist();
    checkRangeCleanup();
  }
  
  public void testMaximumStoreSize() {
    int maximumStoreSize = 2000;
    
    storage.setMaximumStoreSize( maximumStoreSize );
    
    assertEquals( maximumStoreSize, storage.getMaximumStoreSize() );
  }
  
  public void testSetTooSmallMaximumStoreSize() {
    try {
      storage.setMaximumStoreSize( 3 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    storage = new TextSizeStorage();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  private void populateUntilOverflowThresholdIsReached() {
    storage.setMaximumStoreSize( TextSizeStorage.MIN_STORE_SIZE );
    for( int i = 0; i < TextSizeStorage.MIN_STORE_SIZE - 1; i++ ) {
      Integer key = new Integer( i );
      Point point = new Point( i, i );
      storage.storeTextSize( key, point );
    }
  }
  
  private Point updateTimestampOnFirstEntry() {
    return storage.lookupTextSize( KEY_FIRST );
  }

  private void checkRangeCleanup() {
    assertNull( storage.lookupTextSize( new Integer( 99 ) ) );
  }

  private void checkLatestEntriesExist() {
    assertEquals( SIZE_OVERFLOW, storage.lookupTextSize( KEY_OVERFLOW ) );
    assertEquals( new Point( 101, 101 ), storage.lookupTextSize( new Integer( 101 ) ) );
  }

  private void checkTimestampOrdering() {
    assertEquals( SIZE_FIRST, storage.lookupTextSize( KEY_FIRST ) );
  }

  private void forceOverflow() {
    storage.storeTextSize( KEY_OVERFLOW, SIZE_OVERFLOW );
  }
}