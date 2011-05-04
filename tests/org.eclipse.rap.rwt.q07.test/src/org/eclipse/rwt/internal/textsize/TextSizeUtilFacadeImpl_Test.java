/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;


public class TextSizeUtilFacadeImpl_Test extends TestCase {
  private static final String TEXT_TO_MEASURE = " text \"to\" measure ";

  private Font[] fonts;
  private TextSizeUtilFacadeImpl facade;

  public void testCreateProbeParamFragment() {
    Probe probe = createProbe();

    String probeFragment = TextSizeUtilFacadeImpl.createProbeParamFragment( probe );
    
    checkProbeFragment( probeFragment, probe );
  }
  
  public void testCreateItemParamFragment() {
    MeasurementItem item = createMeasurementItem();
    
    String itemFragment = TextSizeUtilFacadeImpl.createItemParamFragment( item );

    checkItemFragment( itemFragment, item );
  }

  public void testWriteFontProbingInternal() throws IOException {
    prepareFontAndTextProbing();
    
    facade.writeFontProbingInternal();
    
    checkResponseContainsProbeCall();
  }
  
  public void testWriteStringMeasurementsInternal() throws IOException {
    prepareFontAndTextProbing();
    
    facade.writeStringMeasurementsInternal();

    checkResponseContainsMeasurementCall();
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    facade = new TextSizeUtilFacadeImpl();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void checkResponseContainsMeasurementCall() {
    String[] expected = getMeasurementCall();
    checkResponseContainsContent( expected );
  }

  private void checkResponseContainsProbeCall() {
    String[] expected = getProbeCall();
    checkResponseContainsContent( expected );
  }
  
  private void checkResponseContainsContent( String[] expected ) {
    String allMarkup = Fixture.getAllMarkup();
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( "Expected to contain '" + expected[ i ] + "', but was '" + allMarkup + "'",
                  contains( allMarkup, expected[ i ] ) );
    }
  }

  private void prepareFontAndTextProbing() {
    createFonts();
    askForTextSizes();
    Fixture.fakeResponseWriter();
  }

  private boolean contains( String allMarkup, String snippet ) {
    return allMarkup.indexOf( snippet ) != -1;
  }

  private void checkItemFragment( String itemFragment, MeasurementItem item ) {
    int key = item.hashCode();
    String escaped = "&nbsp;text \\\"to\\\" measure&nbsp;";
    String expected = "[ " + key + ", \"" + escaped + "\", [ \"fontName\" ], 1, false, false, 17 ]";
    assertEquals( expected, itemFragment );
  }

  private void checkProbeFragment( String probeFragment, Probe probe ) {
    int key = probe.getFontData().hashCode();
    String text = TEXT_TO_MEASURE;
    String expected = "[ " + key + ", \"" + text + "\", [ \"fontName\" ], 1, false, false ]";
    assertEquals( expected, probeFragment );
  }

  private Probe createProbe() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    return new Probe( TEXT_TO_MEASURE, fontData );
  }

  private MeasurementItem createMeasurementItem() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    int wrapWidth = 17;
    return new MeasurementItem( TEXT_TO_MEASURE, fontData, wrapWidth );
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // Note [fappel]: The literals used in createFonts() askForTextSizes(),
  //                getProbeCall() and getMeasurementCasll()
  //                belong together. I do this without constant extraction since I think this
  //                would make the statement array even more unreadable. If you find a more
  //                readable option feel free to change :-)
  private String[] getProbeCall() {
    String probe = Probe.DEFAULT_PROBE_STRING;
    return new String[] {
      "org.eclipse.swt.FontSizeCalculation.probe( [ [",
      ", \"" + probe + "\", [ \"arial\" ], 10, true, false ]",
      ", [ ",
      ", \"" + probe + "\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false ]",
      ", [ ",
      ", \"" + probe + "\", [ \"Bogus  Font  Name\" ], 12, true, false ]",
      " ] );"
    };
  }
  
  private String[] getMeasurementCall() {
    return new String[] {
      "org.eclipse.swt.FontSizeCalculation.measureStrings( [ [ ",
      ", \"FirstString\", [ \"arial\" ], 10, true, false, -1 ]",
      ", [ ",
      ", \"SecondString\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false, -1 ]", 
      ", [",
      ", \"Weird &quot; String \\\\\", [ \"Bogus  Font  Name\" ], 12, true, false, -1 ]",
      " ] );"
    };
  }
  
  private void createFonts() {
    fonts = new Font[] {
      Graphics.getFont( "arial", 10, SWT.BOLD ),
      Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD ),
      Graphics.getFont( "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD )
    };
  }
  
  private void askForTextSizes() {
    Graphics.stringExtent( fonts[ 0 ], "FirstString" );
    Graphics.stringExtent( fonts[ 1 ], "SecondString" );
    Graphics.stringExtent( fonts[ 2 ], "Weird \" String \\" );
  }
  // END NOTE
  //////////////////////////////////////////////////////////////////////////////////////////////////
}