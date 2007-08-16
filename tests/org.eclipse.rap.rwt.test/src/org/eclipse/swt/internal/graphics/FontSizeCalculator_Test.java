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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.FontSizeCalculator.ICalculationItem;
import org.eclipse.swt.internal.graphics.FontSizeProbeStore.IProbe;
import org.eclipse.swt.internal.graphics.FontSizeProbeStore.IProbeResult;
import org.eclipse.swt.widgets.Display;


public class FontSizeCalculator_Test extends TestCase {

  private static final String TEST_STRING = "test";

  public void testRequestCycle() throws IOException {
    Display display = new Display();
    String displayId = DisplayUtil.getId( display );

    // Let pass one startup request to init the 'system'
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    ISessionStore session = ContextProvider.getSession();
    String id = LifeCycle.class.getName();
    session.setAttribute( id, lifeCycle );
    lifeCycle.execute();

    // The actual test request
    Fixture.fakeResponseWriter();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
      }
      public void beforePhase( final PhaseEvent event ) {
        Font font = Graphics.getFont( "arial", 10, SWT.BOLD );
        FontSizeCalculator.stringExtent( font, "FirstString" );
        font = Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD );
        FontSizeCalculator.stringExtent( font, "SecondString" );
      }
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    lifeCycle.execute();

    String probe = FontSizeProbeStore.DEFAULT_PROBE;
    String[] expected = new String[] {
      "org.eclipse.swt.FontSizeCalculation.probe( [ [",
      ", \"" + probe + "\", [ \"arial\" ], 10, true, false ]",
      ", [ ",
      ", \"" + probe + "\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false ]",
      " ] );",
      "org.eclipse.swt.FontSizeCalculation.measureStrings( [ [ ",
      ", \"FirstString\", [ \"arial\" ], 10, true, false, -1 ], [ ",
      ", \"SecondString\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false, -1 ] ] );"
    };
    String allMarkup = Fixture.getAllMarkup();
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( allMarkup.indexOf( expected[ i ] ) != -1 );
    }
  }

  public void testStringExtent() {
    ICalculationItem[] items = FontSizeCalculator.getCalculationItems();
    assertEquals( 0, items.length );

    Font font = Graphics.getFont( "arial", 10, SWT.NORMAL );
    Point calculated = FontSizeCalculator.stringExtent( font , TEST_STRING );
    Point estimated = FontSizeEstimation.stringExtent( font, TEST_STRING );
    assertEquals( estimated, calculated );

    items = FontSizeCalculator.getCalculationItems();
    assertEquals( 1, items.length );
    items = FontSizeCalculator.getCalculationItems();
    assertEquals( 1, items.length );

    FontSizeCalculator.stringExtent( font , TEST_STRING );
    assertEquals( 1, items.length );
    items = FontSizeCalculator.getCalculationItems();
    assertEquals( 1, items.length );

    Point storedSize = new Point( 100, 10 );
    IProbe[] probeRequests = FontSizeProbeStore.getProbeRequests();
    assertEquals( 1, probeRequests.length );
    assertSame( font, probeRequests[ 0 ].getFont() );

    FontSizeProbeStore probeStore = FontSizeProbeStore.getInstance();
    probeStore.createProbeResult( probeRequests[ 0 ], new Point( 10, 10 ) );
    FontSizeDataBase.store( font, TEST_STRING, SWT.DEFAULT, storedSize );
    calculated = FontSizeCalculator.stringExtent( font, TEST_STRING );
    assertEquals( storedSize, calculated );

    Point emptyStringSize = FontSizeCalculator.stringExtent( font, "" );
    assertEquals( new Point( 0, 10 ), emptyStringSize );
  }

  public void testCharHeight() {
    IProbe[] probeRequests = FontSizeProbeStore.getProbeRequests();
    assertEquals( 0, probeRequests.length );

    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    int calculated = FontSizeCalculator.getCharHeight( font0 );
    int estimated = FontSizeEstimation.getCharHeight( font0 );
    assertEquals( estimated, calculated, 0 );

    probeRequests = FontSizeProbeStore.getProbeRequests();
    assertEquals( 1, probeRequests.length );
    assertSame( font0, probeRequests[ 0 ].getFont() );

    FontSizeProbeStore probeStore = FontSizeProbeStore.getInstance();
    Point probeSize = new Point( 10, 13 );
    probeStore.createProbeResult( probeRequests[ 0 ], probeSize );
    calculated = FontSizeCalculator.getCharHeight( font0 );
    assertEquals( 13, calculated );
  }

  public void testAvgCharWidth() {
    IProbe[] probeRequests = FontSizeProbeStore.getProbeRequests();
    assertEquals( 0, probeRequests.length );

    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    float calculated = FontSizeCalculator.getAvgCharWidth( font0 );
    float estimated = FontSizeEstimation.getAvgCharWidth( font0 );
    assertEquals( estimated, calculated, 0 );

    probeRequests = FontSizeProbeStore.getProbeRequests();
    assertEquals( 1, probeRequests.length );
    assertSame( font0, probeRequests[ 0 ].getFont() );

    FontSizeProbeStore probeStore = FontSizeProbeStore.getInstance();
    Point probeSize = new Point( FontSizeProbeStore.DEFAULT_PROBE.length() * 4, 10 );
    probeStore.createProbeResult( probeRequests[ 0 ], probeSize );
    calculated = FontSizeCalculator.getAvgCharWidth( font0 );
    assertEquals( 4, calculated, 0 );
  }

  public void testFontSizeDataBase() {
    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    Font font1 = Graphics.getFont( "helvetia", 12, SWT.NORMAL );

    Point textSize = FontSizeDataBase.lookup( font0, TEST_STRING, SWT.DEFAULT );
    assertNull( textSize );
    textSize = FontSizeDataBase.lookup( font1, TEST_STRING, SWT.DEFAULT );
    assertNull( textSize );

    try {
      Point point = new Point( 1, 1 );
      FontSizeDataBase.store( font1, TEST_STRING, SWT.DEFAULT, point );
      fail( "No probe available." );
    } catch( final IllegalStateException ise ) {
    }

    // simulate clientside probing...
    FontSizeProbeStore probeStore = FontSizeProbeStore.getInstance();
    Point probeSize0 = new Point( 10, 10 );
    probeStore.createProbeResult( findRequestedProbe( 0 ), probeSize0 );
    Point probeSize1 = new Point( 12, 12 );
    probeStore.createProbeResult( findRequestedProbe( 1 ), probeSize1 );

    Point calculatedTextSize0 = new Point( 100, 10 );
    FontSizeDataBase.store( font0,
                            TEST_STRING,
                            SWT.DEFAULT,
                            calculatedTextSize0 );
    Point calculatedTextSize1 = new Point( 100, 12 );
    FontSizeDataBase.store( font1,
                            TEST_STRING,
                            SWT.DEFAULT,
                            calculatedTextSize1 );
    textSize = FontSizeDataBase.lookup( font0, TEST_STRING, SWT.DEFAULT );
    assertEquals( calculatedTextSize0, textSize );
    textSize = FontSizeDataBase.lookup( font1, TEST_STRING, SWT.DEFAULT );
    assertEquals( calculatedTextSize1, textSize );
  }

  private IProbe findRequestedProbe( int i ) {
    IProbe[] probeRequests = FontSizeProbeStore.getProbeRequests();
    return FontSizeProbeStore.getProbe( probeRequests[ i ].getFont() );
  }

  public void testProbeStorage() {
    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    IProbe[] probeList = FontSizeProbeStore.getProbeList();
    assertEquals( 0, probeList.length );
    IProbe probe0 = FontSizeProbeStore.getProbe( font0 );
    assertNull( probe0 );

    String probeText0 = "ProbeText0";
    probe0 = FontSizeProbeStore.createProbe( font0, probeText0 );
    probeList = FontSizeProbeStore.getProbeList();
    assertEquals( 1, probeList.length );
    assertSame( probe0, probeList[ 0 ] );
    assertSame( probe0, FontSizeProbeStore.getProbe( font0 ) );
    assertTrue( FontSizeProbeStore.containsProbe( font0 ) );
    assertSame( probe0.getFont(), font0 );
    assertSame( probe0.getString(), probeText0 );

    Font font1 = Graphics.getFont( "arial", 12, SWT.NORMAL );
    assertFalse( FontSizeProbeStore.containsProbe( font1 ) );

    FontSizeProbeStore probeStore = FontSizeProbeStore.getInstance();
    IProbeResult probeResult0 = probeStore.getProbeResult( font0 );
    assertNull( probeResult0 );

    Point probeSize0 = new Point( 10, 10 );
    probeResult0 = probeStore.createProbeResult( probe0, probeSize0 );
    assertSame( probeResult0.getProbe(), probe0 );
    assertSame( probeResult0.getSize(), probeSize0 );
    assertTrue( probeStore.containsProbeResult( font0 ) );
    assertFalse( probeStore.containsProbeResult( font1 ) );
  }

  public void testDefaultFontSizeStorage() throws IOException {
    DefaultFontSizeStorage storage = new DefaultFontSizeStorage();
    Font font0 = Graphics.getFont( "arial", 10, SWT.NORMAL );
    Font font1 = Graphics.getFont( "helvetia", 12, SWT.NORMAL );
    storage.storeFont( font0 );
    storage.storeFont( font1 );

    Point point0 = new Point( 9, 10 );
    Integer key0 = new Integer( 0 );
    storage.storeStringSize( key0, point0 );
    Point point1 = new Point( 11, 12 );
    Integer key1 = new Integer( 1 );
    storage.storeStringSize( key1, point1 );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    storage.save( out );
    String[] expected = new String[] {
//      "#" + DefaultFontSizeStorage.COMMENT + "\n",
      DefaultFontSizeStorage.PREFIX_FONT_KEY + "0=1|arial|10|0|\n",
      DefaultFontSizeStorage.PREFIX_FONT_KEY + "=1|helvetia|12|0|\n",
      "0=9,10\n",
      "1=11,12\n"
    };
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( out.toString().indexOf( expected[ i ]  ) != 0 );
    }

    storage.resetFontList();
    storage.resetStringSizes();

    ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
    storage.read( in );

    Point actual = storage.lookupStringSize( key0 );
    assertEquals( point0, actual );
    actual = storage.lookupStringSize( key1 );
    assertEquals( point1, actual );
    Font[] fontList = storage.getFontList();
    List fonts = Arrays.asList( fontList );
    assertEquals( 2, fonts.size() );
    assertTrue( fonts.contains( font0 ) );
    assertTrue( fonts.contains( font1 ) );
  }

  public void testStorageOverflow() {
    DefaultFontSizeStorage storage = new DefaultFontSizeStorage();
    int storeSize = DefaultFontSizeStorage.MIN_STORE_SIZE;
    DefaultFontSizeStorage.setStoreSize( storeSize );

    for( int i = 0; i < storeSize - 1; i++ ) {
      Integer key = new Integer( i );
      Point point = new Point( i, i );
      storage.storeStringSize( key, point );
    }
    Integer firstKey = new Integer( 0 );
    // Attention: timestamp update!
    Point firstPoint = storage.lookupStringSize( firstKey );
    assertEquals( firstPoint, new Point( 0, 0 ) );

    Point overflowPoint = new Point( -1, -1 );
    Integer overFlowKey = new Integer( Integer.MAX_VALUE );
    storage.storeStringSize( overFlowKey, overflowPoint );

    assertEquals( firstPoint, storage.lookupStringSize( firstKey ) );
    assertEquals( overflowPoint, storage.lookupStringSize( overFlowKey ) );
    assertNull( storage.lookupStringSize( new Integer( 99 ) ) );
    assertEquals( new Point( 101, 101 ),
                  storage.lookupStringSize( new Integer( 101 ) ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    FontSizeDataBase.reset();
    FontSizeProbeStore.reset();
  }

  protected void tearDown() throws Exception {
    FontSizeProbeStore.reset();
    FontSizeDataBase.reset();
    RWTFixture.tearDown();
  }
}