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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public class TextSizeUtil_Test extends TestCase {
  private static final String TEST_STRING = "test";
  private static final FontData FONT_DATA = new FontData( "arial", 10, SWT.NORMAL );
  
  public void testStringExtentAssignsUnknownStringsToTextSizeMeasuring() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );
    
    assertEquals( 1, getMeasurementItems().length );
    assertEquals( TEST_STRING, getMeasurementItems()[ 0 ].getTextToMeasure() );
    assertEquals( FONT_DATA, getMeasurementItems()[ 0 ].getFontData() );
  }
  
  public void testStringExtentAssignsUnknownFontToFontProbing() {
    TextSizeUtil.stringExtent( getFont(), TEST_STRING );
    
    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }
  
  public void testStringExtentUsesEstimationForUnknownStrings() {
    Point determined = TextSizeUtil.stringExtent( getFont(), TEST_STRING );
    Point estimated = TextSizeEstimation.stringExtent( getFont(), TEST_STRING );
    
    assertEquals( estimated, determined );
  }

  public void testStringExtentUsesStoreageForKnowStrings() {
    Point storedSize = new Point( 100, 10 );
    fakeMeasurement( TEST_STRING, SWT.DEFAULT, storedSize );
    
    Point determinedSize = TextSizeUtil.stringExtent( getFont(), TEST_STRING );

    assertEquals( storedSize, determinedSize );
  }
  
  public void testStringExtentForEmptyString() {
    Point emptyStringSize = TextSizeUtil.stringExtent( getFont(), "" );
    
    assertEquals( new Point( 0, 10 ), emptyStringSize );
  }
  
  public void testStringExtentMustNotExpandLineBreaks() {
    Point singleLine = TextSizeUtil.stringExtent( getFont(), "First Line" );
    Point multiLine = TextSizeUtil.stringExtent( getFont(), "First Line\nSecond Line" );
    
    assertEquals( singleLine.y, multiLine.y );
  }
  
  public void testStringExtentConsideresLeadingAndTrailingSpaces() {
    Point str = TextSizeUtil.stringExtent( getFont(), "  First Line    " );
    Point trimStr = TextSizeUtil.stringExtent( getFont(), "First Line" );
    
    assertTrue( str.x > trimStr.x );
  }

  public void testTextExtentExpandLineBreaks() {
    Point singleLine = TextSizeUtil.textExtent( getFont(), "First Line", 0 );
    Point multiLine = TextSizeUtil.textExtent( getFont(), "First Line\nSecond Line", 0 );

    assertTrue( singleLine.y < multiLine.y );
  }

  public void testGetCharHeightAssignsUnknownFontToFontProbing() {
    TextSizeUtil.getCharHeight( getFont() );
    
    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }
  
  public void testGetCharHeightUsesEstimationForUnknownStrings() {
    int determined = TextSizeUtil.getCharHeight( getFont() );
    int estimated = TextSizeEstimation.getCharHeight( getFont() );
    
    assertEquals( estimated, determined, 0 );
  }

  public void testGetCharHeightUsesStorageForUnknownStrings() {
    int charHeight = 13;
    ProbeResultStore probeResultStore = ProbeResultStore.getInstance();
    probeResultStore.createProbeResult( new Probe( FONT_DATA ), new Point( 10, charHeight ) );

    int determined = TextSizeUtil.getCharHeight( getFont() );
    
    assertEquals( charHeight, determined );
  }
  
  public void testGetAvgCharWidthAssignsUnknownFontToFontProbing() {
    TextSizeUtil.getAvgCharWidth( getFont() );
    
    assertEquals( 1, getProbes().length );
    assertEquals( FONT_DATA, getProbes()[ 0 ].getFontData() );
  }
  
  public void testGetAvgCharWidthUsesEstimationForUnknownStrings() {
    float determined = TextSizeUtil.getAvgCharWidth( getFont() );
    float estimated = TextSizeEstimation.getAvgCharWidth( getFont() );
    
    assertEquals( estimated, determined, 0 );
  }

  public void testGetAvgCharWidthUsesStorageForKnownStrings() {
    ProbeResultStore probeStore = ProbeResultStore.getInstance();
    Point probeSize = new Point( Probe.DEFAULT_PROBE_STRING.length() * 4, 10 );
    probeStore.createProbeResult( new Probe( FONT_DATA ), probeSize );
    
    float determined = TextSizeUtil.getAvgCharWidth( getFont() );

    assertEquals( 4, determined, 0 );
  }
  
  public void testHeightAdjustmentInCaseOfWhitespaceText() {
    fakeMeasurement( " ", 0, new Point( 2, 0 ) );
    
    Point size = TextSizeUtil.textExtent( getFont(), " ", 0 );

    assertEquals( 10, size.y );
  }

  public void testHeightAdjustmentInCaseOfMultiLineLengthGreaterThanWrapWidth() {
    String textToMeasure = "multi\nline\ntext";
    fakeMeasurement( textToMeasure, 2, new Point( 6, 10 ) );
    
    Point size = TextSizeUtil.textExtent( Graphics.getFont( FONT_DATA ), textToMeasure, 2 );
    
    assertEquals( 40, size.y );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void fakeMeasurement( String text, int wrapWidth, Point size ) {
    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 10, 10 ) );
    RWTFactory.getTextSizeStorage().storeFont( FONT_DATA );
    String expanded = TextSizeUtilFacade.createMeasurementString( text, true );
    TextSizeStorageUtil.store( FONT_DATA, expanded, wrapWidth, size );
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