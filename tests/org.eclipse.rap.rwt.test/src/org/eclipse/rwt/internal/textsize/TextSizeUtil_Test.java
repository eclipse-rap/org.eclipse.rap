/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontUtil;


public class TextSizeUtil_Test extends TestCase {
  private static final String TEST_STRING = "test";

  private ProbeStore textSizeProbeStore;

  public void testStringExtent() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();
    assertEquals( 0, items.length );

    Font font = Graphics.getFont( "arial", 10, SWT.NORMAL );
    Point calculated = TextSizeUtil.stringExtent( font, TEST_STRING );
    Point estimated = TextSizeEstimation.stringExtent( font, TEST_STRING );
    assertEquals( estimated, calculated );

    items = MeasurementOperator.getInstance().getItems();
    assertEquals( 1, items.length );
    items = MeasurementOperator.getInstance().getItems();
    assertEquals( 1, items.length );

    TextSizeUtil.stringExtent( font, TEST_STRING );
    assertEquals( 1, items.length );
    items = MeasurementOperator.getInstance().getItems();
    assertEquals( 1, items.length );

    Point storedSize = new Point( 100, 10 );
    Probe[] probeRequests = MeasurementOperator.getInstance().getProbes();
    assertEquals( 1, probeRequests.length );
    assertEquals( FontUtil.getData( font ), probeRequests[ 0 ].getFontData() );

    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    probeStore.createProbeResult( probeRequests[ 0 ], new Point( 10, 10 ) );
    TextSizeStorageUtil.store( FontUtil.getData( font ), TEST_STRING, SWT.DEFAULT,  storedSize );
    calculated = TextSizeUtil.stringExtent( font, TEST_STRING );
    assertEquals( storedSize, calculated );

    Point emptyStringSize = TextSizeUtil.stringExtent( font, "" );
    assertEquals( new Point( 0, 10 ), emptyStringSize );

    // make sure string extent does not expand line breaks
    Point singleLine = TextSizeUtil.stringExtent( font, "First Line" );
    Point multiLine = TextSizeUtil.stringExtent( font, "First Line\nSecond Line" );
    assertEquals( singleLine.y, multiLine.y );

    // make sure that leading and trailing space are calculated
    Point str = TextSizeUtil.stringExtent( font, "  First Line    " );
    Point trimStr = TextSizeUtil.stringExtent( font, "First Line" );
    assertTrue( str.x > trimStr.x );
  }

  public void testTextExtent() {
    Font font = Graphics.getFont( "Helvetica", 10, SWT.NORMAL );
    // make sure text extent does expand line breaks
    Point singleLine
      = TextSizeUtil.textExtent( font, "First Line", 0 );
    Point multiLine
      = TextSizeUtil.textExtent( font, "First Line\nSecond Line", 0 );
    assertTrue( singleLine.y < multiLine.y );
  }

  public void testCharHeight() {
    Probe[] probeRequests = MeasurementOperator.getInstance().getProbes();
    assertEquals( 0, probeRequests.length );

    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    int calculated = TextSizeUtil.getCharHeight( font0 );
    int estimated = TextSizeEstimation.getCharHeight( font0 );
    assertEquals( estimated, calculated, 0 );

    probeRequests = MeasurementOperator.getInstance().getProbes();
    assertEquals( 1, probeRequests.length );
    assertEquals( font0.getFontData()[ 0 ], probeRequests[ 0 ].getFontData() );

    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    Point probeSize = new Point( 10, 13 );
    probeStore.createProbeResult( probeRequests[ 0 ], probeSize );
    calculated = TextSizeUtil.getCharHeight( font0 );
    assertEquals( 13, calculated );
  }

  public void testAvgCharWidth() {
    Probe[] probeRequests = MeasurementOperator.getInstance().getProbes();
    assertEquals( 0, probeRequests.length );

    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    float calculated = TextSizeUtil.getAvgCharWidth( font0 );
    float estimated = TextSizeEstimation.getAvgCharWidth( font0 );
    assertEquals( estimated, calculated, 0 );

    probeRequests = MeasurementOperator.getInstance().getProbes();
    assertEquals( 1, probeRequests.length );
    assertEquals( font0.getFontData()[ 0 ], probeRequests[ 0 ].getFontData() );

    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    Point probeSize = new Point( Probe.DEFAULT_PROBE_STRING.length() * 4, 10 );
    probeStore.createProbeResult( probeRequests[ 0 ], probeSize );
    calculated = TextSizeUtil.getAvgCharWidth( font0 );
    assertEquals( 4, calculated, 0 );
  }

  public void testFontSizeDataBase() {
    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    FontData fontData0 = font0.getFontData()[ 0 ];
    Font font1 = Graphics.getFont( "helvetia", 12, SWT.NORMAL );
    FontData fontData1 = font1.getFontData()[ 0 ];

    Point textSize;
    textSize = TextSizeStorageUtil.lookup( fontData0, TEST_STRING, SWT.DEFAULT );
    assertNull( textSize );
    textSize = TextSizeStorageUtil.lookup( fontData1, TEST_STRING, SWT.DEFAULT );
    assertNull( textSize );

    try {
      Point point = new Point( 1, 1 );
      TextSizeStorageUtil.store( fontData1, TEST_STRING, SWT.DEFAULT, point );
      fail( "No probe available." );
    } catch( final IllegalStateException ise ) {
    }

    // simulate clientside probing...
    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    Point probeSize0 = new Point( 10, 10 );
    probeStore.createProbeResult( findRequestedProbe( 0 ), probeSize0 );
    Point probeSize1 = new Point( 12, 12 );
    probeStore.createProbeResult( findRequestedProbe( 1 ), probeSize1 );

    Point calculatedTextSize0 = new Point( 100, 10 );
    TextSizeStorageUtil.store( fontData0,
                            TEST_STRING,
                            SWT.DEFAULT,
                            calculatedTextSize0 );
    Point calculatedTextSize1 = new Point( 100, 12 );
    TextSizeStorageUtil.store( fontData1,
                            TEST_STRING,
                            SWT.DEFAULT,
                            calculatedTextSize1 );
    textSize = TextSizeStorageUtil.lookup( fontData0, TEST_STRING, SWT.DEFAULT );
    assertEquals( calculatedTextSize0, textSize );
    textSize = TextSizeStorageUtil.lookup( fontData1, TEST_STRING, SWT.DEFAULT );
    assertEquals( calculatedTextSize1, textSize );
  }

  private Probe findRequestedProbe( int i ) {
    Probe[] probeRequests = MeasurementOperator.getInstance().getProbes();
    return textSizeProbeStore.getProbe( probeRequests[ i ].getFontData() );
  }

  public void testProbeStorage() {
    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    FontData fontData0 = font0.getFontData()[ 0 ];
    Probe[] probeList = textSizeProbeStore.getProbes();
    assertEquals( 0, probeList.length );
    Probe probe0 = textSizeProbeStore.getProbe( fontData0 );
    assertNull( probe0 );

    probe0 = textSizeProbeStore.createProbe( fontData0 );
    probeList = textSizeProbeStore.getProbes();
    assertEquals( 1, probeList.length );
    assertSame( probe0, probeList[ 0 ] );
    assertSame( probe0, textSizeProbeStore.getProbe( fontData0 ) );
    assertTrue( textSizeProbeStore.getProbe( fontData0 ) != null );
    assertSame( probe0.getFontData(), fontData0 );
    assertSame( probe0.getText(), Probe.DEFAULT_PROBE_STRING );

    Font font1 = Graphics.getFont( "arial", 12, SWT.NORMAL );
    FontData fontData1 = font1.getFontData()[ 0 ];
    assertNull( textSizeProbeStore.getProbe( fontData1 ) );

    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    ProbeResult probeResult0 = probeStore.getProbeResult( fontData0 );
    assertNull( probeResult0 );

    Point probeSize0 = new Point( 10, 10 );
    probeResult0 = probeStore.createProbeResult( probe0, probeSize0 );
    assertSame( probeResult0.getProbe(), probe0 );
    assertSame( probeResult0.getSize(), probeSize0 );
    assertTrue( probeStore.containsProbeResult( fontData0 ) );
    assertFalse( probeStore.containsProbeResult( fontData1 ) );
  }

  public void testTextSizeDatabaseKey() {
    Font font = Graphics.getFont( "name", 10, SWT.NORMAL );
    FontData fontData = FontUtil.getData( font );
    Set takenKeys = new HashSet();
    StringBuffer generatedText = new StringBuffer();
    for( int i = 0; i < 100; i++ ) {
      generatedText.append( "a" );
      String text = generatedText.toString();
      Probe probe = new Probe( text, fontData );
      Point size = new Point( 1, 2 );
      ProbeResultStore.getInstance().createProbeResult( probe, size );
      Integer key = TextSizeStorageUtil.getKey( fontData, text, -1 );
      assertFalse( takenKeys.contains( key ) );
      takenKeys.add( key );
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    textSizeProbeStore = RWTFactory.getProbeStore();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}