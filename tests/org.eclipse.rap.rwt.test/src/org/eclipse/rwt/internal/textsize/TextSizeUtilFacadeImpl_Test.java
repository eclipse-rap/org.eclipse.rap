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

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.Message;
import org.eclipse.rwt.internal.protocol.Message.CallOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public class TextSizeUtilFacadeImpl_Test extends TestCase {
  private static final String TEXT_TO_MEASURE = " text \"to\" measure ";
  private static final String DISPLAY_ID = "w1";

  private Font[] fonts;
  private TextSizeUtilFacadeImpl facade;

  public void testCreateProbeParamObject() {
    Probe probe = createProbe();

    Object probeObject = TextSizeUtilFacadeImpl.createProbeParamObject( probe );

    checkProbeObject( probeObject, probe );
  }

  public void testCreateItemParamObject() {
    MeasurementItem item = createMeasurementItem();

    Object itemObject = TextSizeUtilFacadeImpl.createItemParamObject( item );

    checkItemObject( itemObject, item );
  }

  public void testWriteFontProbingInternal() {
    prepareFontAndTextProbing();

    facade.writeFontProbingInternal();

    checkResponseContainsProbeCall();
  }

  public void testWriteStringMeasurementsInternal() {
    prepareFontAndTextProbing();

    facade.writeStringMeasurementsInternal();

    checkResponseContainsMeasurementCall();
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    new Display();
    facade = new TextSizeUtilFacadeImpl();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void checkResponseContainsMeasurementCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation
      = message.findCallOperation( DISPLAY_ID, TextSizeUtilFacadeImpl.METHOD_MEASURE_STRINGS );
    Object stringsProperty = operation.getProperty( TextSizeUtilFacadeImpl.PROPERTY_STRINGS );
    String[] expected = getMeasurementCall();
    checkResponseContainsContent( expected, stringsProperty.toString() );
  }

  private void checkResponseContainsProbeCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation
      = message.findCallOperation( DISPLAY_ID, TextSizeUtilFacadeImpl.METHOD_PROBE );
    Object fontsProperty = operation.getProperty( TextSizeUtilFacadeImpl.PROPERTY_FONTS );
    String[] expected = getProbeCall();
    checkResponseContainsContent( expected, fontsProperty.toString() );
  }

  private void checkResponseContainsContent( String[] expected, String markup ) {
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( "Expected to contain '" + expected[ i ] + "', but was '" + markup + "'",
                  contains( markup, expected[ i ] ) );
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

  private void checkItemObject( Object itemObject, MeasurementItem item ) {
    assertTrue( itemObject instanceof Object[] );
    Object[] itemObjectArray = ( Object[] )itemObject;
    assertEquals( 7, itemObjectArray.length );
    assertEquals( new Integer( item.hashCode() ),  itemObjectArray[ 0 ] );
    String escaped = " text \"to\" measure ";
    assertEquals( escaped, itemObjectArray[ 1 ] );
    assertTrue( itemObjectArray[ 2 ] instanceof String[] );
    String[] fontNameArray = ( String[] )itemObjectArray[ 2 ];
    assertEquals( 1, fontNameArray.length );
    assertEquals( "fontName", fontNameArray[ 0 ] );
    assertEquals( new Integer( 1 ), itemObjectArray[ 3 ] );
    assertEquals( Boolean.FALSE, itemObjectArray[ 4 ] );
    assertEquals( Boolean.FALSE, itemObjectArray[ 5 ] );
    assertEquals( new Integer( 17 ), itemObjectArray[ 6 ] );
  }

  private void checkProbeObject( Object probeObject, Probe probe ) {
    assertTrue( probeObject instanceof Object[] );
    Object[] probeObjectArray = ( Object[] )probeObject;
    assertEquals( 6, probeObjectArray.length );
    assertEquals( new Integer( probe.getFontData().hashCode() ),  probeObjectArray[ 0 ] );
    assertEquals( TEXT_TO_MEASURE, probeObjectArray[ 1 ] );
    assertTrue( probeObjectArray[ 2 ] instanceof String[] );
    String[] fontNameArray = ( String[] )probeObjectArray[ 2 ];
    assertEquals( 1, fontNameArray.length );
    assertEquals( "fontName", fontNameArray[ 0 ] );
    assertEquals( new Integer( 1 ), probeObjectArray[ 3 ] );
    assertEquals( Boolean.FALSE, probeObjectArray[ 4 ] );
    assertEquals( Boolean.FALSE, probeObjectArray[ 5 ] );
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

  private String[] getProbeCall() {
    return new String[] {
      ",[\"arial\"],10,true,false]",
      ",[\"helvetia\",\"ms sans serif\"],12,true,false]",
      ",[\"Bogus  Font  Name\"],12,true,false]"
    };
  }

  private String[] getMeasurementCall() {
    return new String[] {
      ",\"FirstString\",[\"arial\"],10,true,false,-1]",
      ",\"SecondString\",[\"helvetia\",\"ms sans serif\"],12,true,false,-1]",
      ",\"Weird \\\" String \\\\\",[\"Bogus  Font  Name\"],12,true,false,-1]"
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