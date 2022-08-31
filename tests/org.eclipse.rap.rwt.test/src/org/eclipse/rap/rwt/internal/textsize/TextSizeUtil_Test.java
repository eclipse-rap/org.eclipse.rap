/*******************************************************************************
 * Copyright (c) 2002, 2022 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.textsize.TextSizeUtil.STRING_EXTENT;
import static org.eclipse.rap.rwt.internal.textsize.TextSizeUtil.TEXT_EXTENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TextSizeUtil_Test {

  private Display display;
  private static final String TEST_STRING = "test";
  private static final FontData FONT_DATA = new FontData( "arial", 10, SWT.NORMAL );

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    TextSizeUtil.loadTestsEnabled = false;
  }

  @Test
  public void testStringExtent_assignsUnknownStringsToTextSizeMeasuring() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 1, getMeasurementItems().length );
    assertEquals( TEST_STRING, getMeasurementItems()[ 0 ].getTextToMeasure() );
    assertEquals( FONT_DATA, getMeasurementItems()[ 0 ].getFontData() );
  }

  @Test
  public void testStringExtent_doesNotAssignsUnknownStringsToTextSizeMeasuringIfTemporaryResize() {
    markTemporaryResize();

    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 0, getMeasurementItems().length );
  }

  @Test
  public void testStringExtent_assignsUnknownFontToFontProbing() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testStringExtent_usesEstimationForUnknownStrings() {
    Point determined = TextSizeUtil.stringExtent( getFont(), TEST_STRING );
    Point estimated = TextSizeEstimation.stringExtent( getFont(), TEST_STRING );

    assertEquals( estimated, determined );
  }

  @Test
  public void testStringExtent_usesStoreageForKnowStrings() {
    Point storedSize = new Point( 100, 10 );
    fakeMeasurement( TEST_STRING, SWT.DEFAULT, TextSizeUtil.STRING_EXTENT, storedSize );

    Point determinedSize = TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( storedSize, determinedSize );
  }

  @Test
  public void testStringExtent_forEmptyString() {
    Point emptyStringSize = TextSizeUtil.stringExtent( getFont(), "" );

    assertEquals( new Point( 0, 10 ), emptyStringSize );
  }

  @Test
  public void testStringExtent_mustNotExpandLineBreaks() {
    Point singleLine = TextSizeUtil.stringExtent( getFont(), "First Line" );
    Point multiLine = TextSizeUtil.stringExtent( getFont(), "First Line\nSecond Line" );

    assertEquals( singleLine.y, multiLine.y );
  }

  @Test
  public void testStringExtent_consideresLeadingAndTrailingSpaces() {
    Point str = TextSizeUtil.stringExtent( getFont(), "  First Line    " );
    Point trimStr = TextSizeUtil.stringExtent( getFont(), "First Line" );

    assertTrue( str.x > trimStr.x );
  }

  @Test
  public void testStringExtend_withMarkup() {
    Point stringExtend = TextSizeUtil.stringExtent( getFont(), "<b>foo</b>", false );
    Point markupExtend = TextSizeUtil.stringExtent( getFont(), "<b>foo</b>", true );

    assertTrue( stringExtend.x > markupExtend.x );
  }

  @Test
  public void testStringExtend_withLoadTestsEnabled_doesNotAssignsStringsToTextSizeMeasurement() {
    TextSizeUtil.loadTestsEnabled = true;

    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 0, getMeasurementItems().length );
    assertEquals( 0, getProbes().length );
    assertNotNull( TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, -1, STRING_EXTENT ) );
    assertTrue( ProbeResultStore.getInstance().containsProbeResult( FONT_DATA ) );
  }

  @Test
  public void testTextExtent_expandLineBreaks() {
    Point singleLine = TextSizeUtil.textExtent( getFont(), "First Line", 0 );
    Point multiLine = TextSizeUtil.textExtent( getFont(), "First Line\nSecond Line", 0 );

    assertTrue( singleLine.y < multiLine.y );
  }

  @Test
  public void testTextExtent_returnsStoredDimensions() {
    String textToMeasure = "Foo bar";
    fakeMeasurement( textToMeasure, 10, TextSizeUtil.TEXT_EXTENT, new Point( 15, 30 ) );

    Point size = TextSizeUtil.textExtent( getFont(), textToMeasure, 10 );

    assertEquals( new Point( 15, 30 ), size );
  }

  @Test
  public void testTextExtent_withMarkup() {
    Point textExtend = TextSizeUtil.textExtent( getFont(), "<b>foo</b>", 0, false );
    Point markupExtend = TextSizeUtil.textExtent( getFont(), "<b>foo</b>", 0, true );

    assertTrue( textExtend.x > markupExtend.x );
  }

  @Test
  public void testTextExtent_withMarkupAndLineBreak() {
    Point textExtend = TextSizeUtil.textExtent( getFont(), "<b>foo</b></br>", 0, false );
    Point markupExtend = TextSizeUtil.textExtent( getFont(), "<b>foo</b></br>", 0, true );

    assertTrue( textExtend.y < markupExtend.y );
  }

  @Test
  public void testTextExtend_withLoadTestsEnabled_doesNotAssignsStringsToTextSizeMeasurement() {
    TextSizeUtil.loadTestsEnabled = true;

    TextSizeUtil.textExtent( getFont(), TEST_STRING, 0, false );

    assertEquals( 0, getMeasurementItems().length );
    assertEquals( 0, getProbes().length );
    assertNotNull( TextSizeStorageUtil.lookup( FONT_DATA, TEST_STRING, -1, TEXT_EXTENT ) );
    assertTrue( ProbeResultStore.getInstance().containsProbeResult( FONT_DATA ) );
  }

  @Test
  public void testGetCharHeightAssignsUnknownFontToFontProbing() {
    TextSizeUtil.getCharHeight( getFont() );

    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testGetCharHeightUsesEstimationForUnknownStrings() {
    int determined = TextSizeUtil.getCharHeight( getFont() );
    int estimated = TextSizeEstimation.getCharHeight( getFont() );

    assertEquals( estimated, determined, 0 );
  }

  @Test
  public void testGetCharHeightUsesStorageForUnknownStrings() {
    int charHeight = 13;
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 10, charHeight ) );

    int determined = TextSizeUtil.getCharHeight( getFont() );

    assertEquals( charHeight, determined );
  }

  @Test
  public void testGetCharHeight_withLoadTestsEnabled_doesNotAssignsStringsToTextSizeMeasurement() {
    TextSizeUtil.loadTestsEnabled = true;

    TextSizeUtil.getCharHeight( getFont() );

    assertEquals( 0, getProbes().length );
    assertTrue( ProbeResultStore.getInstance().containsProbeResult( FONT_DATA ) );
  }

  @Test
  public void testGetAvgCharWidthAssignsUnknownFontToFontProbing() {
    TextSizeUtil.getAvgCharWidth( getFont() );

    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testGetAvgCharWidthUsesEstimationForUnknownStrings() {
    float determined = TextSizeUtil.getAvgCharWidth( getFont() );
    float estimated = TextSizeEstimation.getAvgCharWidth( getFont() );

    assertEquals( estimated, determined, 0 );
  }

  @Test
  public void testGetAvgCharWidthUsesStorageForKnownStrings() {
    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    Point probeSize = new Point( Probe.DEFAULT_PROBE_STRING.length() * 4, 10 );
    probeStore.createProbeResult( new Probe( FONT_DATA ), probeSize );

    float determined = TextSizeUtil.getAvgCharWidth( getFont() );

    assertEquals( 4, determined, 0 );
  }

  @Test
  public void testGetAvgCharWidth_withLoadTestsEnabled_doesNotAssignsStringsToTextSizeMeasurement() {
    TextSizeUtil.loadTestsEnabled = true;

    TextSizeUtil.getAvgCharWidth( getFont() );

    assertEquals( 0, getProbes().length );
    assertTrue( ProbeResultStore.getInstance().containsProbeResult( FONT_DATA ) );
  }

  @Test
  public void testHeightAdjustmentInCaseOfWhitespaceText() {
    fakeMeasurement( " ", SWT.DEFAULT, TextSizeUtil.TEXT_EXTENT, new Point( 2, 0 ) );

    Point size = TextSizeUtil.textExtent( getFont(), " ", 0 );

    assertEquals( 10, size.y );
  }

  private void fakeMeasurement( String text, int wrapWidth, int mode, Point size ) {
    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 10, 10 ) );
    getApplicationContext().getTextSizeStorage().storeFont( FONT_DATA );
    TextSizeStorageUtil.store( FONT_DATA, text, wrapWidth, mode, size );
  }

  private MeasurementItem[] getMeasurementItems() {
    return MeasurementUtil.getMeasurementOperator().getItems();
  }

  private Probe[] getProbes() {
    return MeasurementUtil.getMeasurementOperator().getProbes();
  }

  private Font getFont() {
    return new Font( display, FONT_DATA );
  }

  private void markTemporaryResize() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String key = "org.eclipse.rap.rwt.internal.textsize.TextSizeRecalculation#temporaryResize";
    serviceStore.setAttribute( key, Boolean.TRUE );
  }

}
