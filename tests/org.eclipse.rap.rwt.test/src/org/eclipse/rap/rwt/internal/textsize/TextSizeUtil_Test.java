/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TextSizeUtil_Test {

  private static final String TEST_STRING = "test";
  private static final FontData FONT_DATA = new FontData( "arial", 10, SWT.NORMAL );

  @Before
  public void setUp() {
    Fixture.setUp();
    new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testStringExtentAssignsUnknownStringsToTextSizeMeasuring() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 1, getMeasurementItems().length );
    assertEquals( TEST_STRING, getMeasurementItems()[ 0 ].getTextToMeasure() );
    assertEquals( FONT_DATA, getMeasurementItems()[ 0 ].getFontData() );
  }

  @Test
  public void testStringExtentAssignsUnknownFontToFontProbing() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testStringExtentUsesEstimationForUnknownStrings() {
    Point determined = TextSizeUtil.stringExtent( getFont(), TEST_STRING );
    Point estimated = TextSizeEstimation.stringExtent( getFont(), TEST_STRING );

    assertEquals( estimated, determined );
  }

  @Test
  public void testStringExtentUsesStoreageForKnowStrings() {
    Point storedSize = new Point( 100, 10 );
    fakeMeasurement( TEST_STRING, SWT.DEFAULT, TextSizeUtil.STRING_EXTENT, storedSize );

    Point determinedSize = TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( storedSize, determinedSize );
  }

  @Test
  public void testStringExtentForEmptyString() {
    Point emptyStringSize = TextSizeUtil.stringExtent( getFont(), "" );

    assertEquals( new Point( 0, 10 ), emptyStringSize );
  }

  @Test
  public void testStringExtentMustNotExpandLineBreaks() {
    Point singleLine = TextSizeUtil.stringExtent( getFont(), "First Line" );
    Point multiLine = TextSizeUtil.stringExtent( getFont(), "First Line\nSecond Line" );

    assertEquals( singleLine.y, multiLine.y );
  }

  @Test
  public void testStringExtentConsideresLeadingAndTrailingSpaces() {
    Point str = TextSizeUtil.stringExtent( getFont(), "  First Line    " );
    Point trimStr = TextSizeUtil.stringExtent( getFont(), "First Line" );

    assertTrue( str.x > trimStr.x );
  }

  @Test
  public void testTextExtentExpandLineBreaks() {
    Point singleLine = TextSizeUtil.textExtent( getFont(), "First Line", 0 );
    Point multiLine = TextSizeUtil.textExtent( getFont(), "First Line\nSecond Line", 0 );

    assertTrue( singleLine.y < multiLine.y );
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
  public void testHeightAdjustmentInCaseOfWhitespaceText() {
    fakeMeasurement( " ", SWT.DEFAULT, TextSizeUtil.TEXT_EXTENT, new Point( 2, 0 ) );

    Point size = TextSizeUtil.textExtent( getFont(), " ", 0 );

    assertEquals( 10, size.y );
  }

  @Test
  public void testHeightAdjustmentInCaseOfMultiLineLengthGreaterThanWrapWidth() {
    String textToMeasure = "multi\nline\ntext";
    fakeMeasurement( textToMeasure, 2, TextSizeUtil.TEXT_EXTENT, new Point( 6, 10 ) );

    Point size = TextSizeUtil.textExtent( Graphics.getFont( FONT_DATA ), textToMeasure, 2 );

    assertEquals( 40, size.y );
  }

  private void fakeMeasurement( String text, int wrapWidth, int mode, Point size ) {
    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 10, 10 ) );
    getApplicationContext().getTextSizeStorage().storeFont( FONT_DATA );
    TextSizeStorageUtil.store( FONT_DATA, text, wrapWidth, mode, size );
  }

  private MeasurementItem[] getMeasurementItems() {
    return MeasurementOperator.getInstance().getItems();
  }

  private Probe[] getProbes() {
    return MeasurementOperator.getInstance().getProbes();
  }

  private Font getFont() {
    return Graphics.getFont( FONT_DATA );
  }

}
