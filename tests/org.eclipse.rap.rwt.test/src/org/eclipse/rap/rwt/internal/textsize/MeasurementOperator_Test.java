/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.textsize.MeasurementItem;
import org.eclipse.rap.rwt.internal.textsize.MeasurementOperator;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public class MeasurementOperator_Test extends TestCase {

  private static final String TSD_ID = "rwt.client.TextSizeMeasurement";
  private static final FontData FONT_DATA_1 = new FontData( "arial", 12, SWT.NONE );
  private static final FontData FONT_DATA_2 = new FontData( "courier", 14, SWT.BOLD );
  private static final String TEXT_TO_MEASURE = "textToMeasure";
  private static final int MODE = TextSizeUtil.STRING_EXTENT;
  private static final MeasurementItem MEASUREMENT_ITEM_1
    = new MeasurementItem( TEXT_TO_MEASURE, FONT_DATA_1, SWT.DEFAULT, MODE );
  private static final MeasurementItem MEASUREMENT_ITEM_2
    = new MeasurementItem( TEXT_TO_MEASURE, FONT_DATA_2, SWT.DEFAULT, MODE );

  private Display display;
  private MeasurementOperator operator;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    operator = MeasurementOperator.getInstance();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testHandleMeasurementRequest() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();

    operator.handleMeasurementRequests();

    assertEquals( 1, operator.getProbeCount() );
    assertEquals( 1, operator.getItemCount() );
  }

  public void testSubsequentCallsToHandleMeasurementRequest() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();
    operator.handleMeasurementRequests();
    requestProbingOfFont2();
    requestMeasurementOfItem2();

    operator.handleMeasurementRequests();

    assertEquals( 2, operator.getProbeCount() );
    assertEquals( 2, operator.getItemCount() );
  }

  public void testSubsequentCallsToHandleMeasurementRequestAreIdempotent() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();
    operator.handleMeasurementRequests();
    requestProbingOfFont1();
    requestMeasurementOfItem1();

    operator.handleMeasurementRequests();

    assertEquals( 1, operator.getProbeCount() );
    assertEquals( 1, operator.getItemCount() );
  }

  public void testHandleMeasurementResults() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();
    operator.handleMeasurementRequests();
    fakeMessageWithMeasurementResultOfProbe( FONT_DATA_1 );
    fakeMessageWithMeasurementResultOfItem( MEASUREMENT_ITEM_1 );

    operator.handleMeasurementResults();

    assertEquals( 0, operator.getProbeCount() );
    assertEquals( 0, operator.getItemCount() );
  }

  public void testInitStartupProbes() {
    createProbeOfFont1();

    MeasurementOperator measurementOperator = new MeasurementOperator();

    assertEquals( 1, measurementOperator.getProbeCount() );
  }

  public void testHandleStartupProbeMeasurementResults() {
    createProbeOfFont1();
    fakeMessageWithMeasurementResultOfProbe( FONT_DATA_1 );
    MeasurementOperator measurementOperator = new MeasurementOperator();

    measurementOperator.handleStartupProbeMeasurementResults();

    assertEquals( 0, measurementOperator.getProbeCount() );
  }

  public void testHandleStartupProbeMeasurementResultsExecutedOnce() {
    requestProbing( FONT_DATA_1 );
    fakeMessageWithMeasurementResultOfProbe( FONT_DATA_1 );
    operator.handleStartupProbeMeasurementResults();
    requestProbing( FONT_DATA_2 );
    fakeMessageWithMeasurementResultOfProbe( FONT_DATA_2 );

    operator.handleStartupProbeMeasurementResults();

    assertEquals( 1, operator.getProbeCount() );
  }

  public void testAddItemToMeasure() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  public void testAddItemToMeasureIsIdempotent() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  public void testGetItemsToMeasureWithEmptyResult() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();

    assertEquals( 0, items.length );
  }

  public void testRenderFontProbing() {
    prepareTextProbing();

    operator.handleMeasurementRequests();

    checkResponseContainsProbeCall();
  }

  public void testRenderStringMeasurements() {
    prepareTextProbing();

    operator.handleMeasurementRequests();

    checkResponseContainsMeasurementCall();
  }

  public void testRenderStringMeasurementsWithDisposedDisplay() {
    prepareTextProbing();
    display.dispose();

    // Ensures that no exception is thrown.
    operator.handleMeasurementRequests();
  }

  private void createProbeOfFont1() {
    createProbe( FONT_DATA_1 );
  }

  private void createProbe( FontData fontData ) {
    ProbeStore textSizeProbeStore = RWTFactory.getProbeStore();
    textSizeProbeStore.createProbe( fontData );
  }

  private void fakeMessageWithMeasurementResultOfItem( MeasurementItem measurementItem ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    Map<String, Object> results = new HashMap<String, Object>();
    results.put( String.valueOf( measurementItem.hashCode() ), new int[] { 12, 4 } );
    parameters.put( "results", results );
    Fixture.fakeCallOperation( TSD_ID, "storeMeasurements", parameters  );
  }

  private void fakeMessageWithMeasurementResultOfProbe( FontData fontData ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    Map<String, Object> results = new HashMap<String, Object>();
    results.put( String.valueOf( fontData.hashCode() ), new int[] { 3, 4 } );
    parameters.put( "results", results );
    Fixture.fakeCallOperation( TSD_ID, "storeProbes", parameters  );
  }

  private void requestMeasurementOfItem1() {
    MeasurementOperator.getInstance().addItemToMeasure( MEASUREMENT_ITEM_1 );
  }

  private void requestMeasurementOfItem2() {
    MeasurementOperator.getInstance().addItemToMeasure( MEASUREMENT_ITEM_2 );
  }

  private void requestProbingOfFont1() {
    requestProbing( FONT_DATA_1 );
  }

  private void requestProbingOfFont2() {
    requestProbing( FONT_DATA_2 );
  }

  private void requestProbing( FontData fontData1 ) {
    Fixture.fakeNewRequest( display );
    operator.addProbeToMeasure( fontData1 );
  }

  private void checkMeasurementItemBuffering( MeasurementItem item ) {
    assertEquals( 1, MeasurementOperator.getInstance().getItems().length );
    assertSame( item, MeasurementOperator.getInstance().getItems() [ 0 ] );
  }

  private void checkResponseContainsMeasurementCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TSD_ID, "measureItems" );
    Object stringsProperty = operation.getProperty( "strings" );
    String[] expected = getMeasurementCall();
    checkResponseContainsContent( expected, stringsProperty.toString() );
  }

  private void checkResponseContainsProbeCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TSD_ID, "probe" );
    Object fontsProperty = operation.getProperty( "fonts" );
    String[] expected = getProbeCall();
    checkResponseContainsContent( expected, fontsProperty.toString() );
  }

  private void checkResponseContainsContent( String[] expected, String markup ) {
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( "Expected to contain '" + expected[ i ] + "', but was '" + markup + "'",
                  markup.contains( expected[ i ] ) );
    }
  }

  private void prepareTextProbing() {
    askForTextSizes();
    Fixture.fakeResponseWriter();
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
      ",\"FirstString\",[\"arial\"],10,true,false,-1,false]",
      ",\"SecondString\",[\"helvetia\",\"ms sans serif\"],12,true,false,-1,false]",
      ",\"Weird \\\" String \\\\\",[\"Bogus  Font  Name\"],12,true,false,-1,false]"
    };
  }

  private void askForTextSizes() {
    Font[] fonts = new Font[] {
      Graphics.getFont( "arial", 10, SWT.BOLD ),
      Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD ),
      Graphics.getFont( "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD )
    };
    Graphics.stringExtent( fonts[ 0 ], "FirstString" );
    Graphics.stringExtent( fonts[ 1 ], "SecondString" );
    Graphics.stringExtent( fonts[ 2 ], "Weird \" String \\" );
  }
}