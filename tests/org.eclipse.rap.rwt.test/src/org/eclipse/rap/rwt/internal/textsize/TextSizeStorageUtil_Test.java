/*******************************************************************************
 * Copyright (c) 2011, 2014 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - bug 348056: Eliminate compiler warnings
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;

public class TextSizeStorageUtil_Test {

  private static final FontData FONT_DATA = new FontData( "arial", 10, SWT.NORMAL );
  private static final String TEST_STRING = "test";
  private static final int MODE = TextSizeUtil.STRING_EXTENT;

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    System.getProperties().remove( RWTProperties.TEXT_SIZE_STORE_SESSION_SCOPED );
  }

  @Test
  public void testLookupOfNotExistingText() {
    assertNull( TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE ) );
  }

  @Test
  public void testStoreWithUnprobedFont() {
    try {
      TextSizeStorageUtil.store( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE, new Point( 1, 1 ) );
      fail( "No probe available." );
    } catch( IllegalStateException ise ) {
    }
  }

  @Test
  public void testStoreAndLookup() {
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );
    Point storedSize = new Point( 10, 10 );

    TextSizeStorageUtil.store( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE, storedSize );
    Point lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE );

    assertEquals( storedSize, lookupSize );
  }

  @Test
  public void testLookup_withBiggerWrapWidth() {
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );
    Point notWrappedSize = new Point( 50, 10 );

    TextSizeStorageUtil.store( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE, notWrappedSize );
    Point lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, 100, MODE );

    assertEquals( notWrappedSize, lookupSize );
  }

  @Test
  public void testLookup_withSmallerWrapWidth() {
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );
    Point notWrappedSize = new Point( 50, 10 );

    TextSizeStorageUtil.store( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE, notWrappedSize );
    Point lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, 25, MODE );

    assertNull( lookupSize );
  }

  @Test
  public void testLookup_withMissingSize() {
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );

    Point lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, 25, MODE );

    assertNull( lookupSize );
  }

  @Test
  public void testGetKey() {
    Set<Integer> takenKeys = new HashSet<Integer>();
    StringBuilder generatedText = new StringBuilder();
    for( int i = 0; i < 100; i++ ) {
      generatedText.append( "a" );
      String text = generatedText.toString();
      Probe probe = new Probe( text, FONT_DATA );
      Point size = new Point( 1, 2 );
      ProbeResultStore.getInstance().createProbeResult( probe, size );
      Integer key = TextSizeStorageUtil.getKey( FONT_DATA, text, SWT.DEFAULT, MODE );
      assertFalse( takenKeys.contains( key ) );
      takenKeys.add( key );
    }
  }

  @Test
  public void testSessionScopedStore() {
    Point storedSize = new Point( 100, 10 );
    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );
    TextSizeStorageUtil.store( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE, storedSize );
    Point lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE );
    assertEquals(storedSize, lookupSize);

    System.setProperty( RWTProperties.TEXT_SIZE_STORE_SESSION_SCOPED , "true" );
    Fixture.disposeOfServiceContext();
    Fixture.createServiceContext();

    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 2, 10 ) );
    lookupSize = TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, SWT.DEFAULT, MODE );
    assertNull( lookupSize );
  }
}
