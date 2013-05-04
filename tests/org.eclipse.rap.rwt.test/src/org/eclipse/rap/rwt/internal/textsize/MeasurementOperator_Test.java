/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
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

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.METHOD_MEASURE_ITEMS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.METHOD_STORE_MEASUREMENTS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.PROPERTY_ITEMS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.PROPERTY_RESULTS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MeasurementOperator_Test {

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

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    operator = MeasurementOperator.getInstance();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleMeasurementRequest() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();

    operator.handleMeasurementRequests();

    assertEquals( 1, operator.getProbeCount() );
    assertEquals( 1, operator.getItemCount() );
  }

  @Test
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

  @Test
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

  @Test
  public void testHandleMeasurementResults() {
    requestProbingOfFont1();
    requestMeasurementOfItem1();
    operator.handleMeasurementRequests();
    fakeMessageWithMeasurementResult( FONT_DATA_1, MEASUREMENT_ITEM_1 );

    operator.handleMeasurementResults();

    assertEquals( 0, operator.getProbeCount() );
    assertEquals( 0, operator.getItemCount() );
  }

  @Test
  public void testInitStartupProbes() {
    createProbeOfFont1();

    MeasurementOperator measurementOperator = new MeasurementOperator();

    assertEquals( 1, measurementOperator.getProbeCount() );
  }

  @Test
  public void testHandleStartupProbeMeasurementResults() {
    createProbeOfFont1();
    fakeMessageWithMeasurementResult( FONT_DATA_1, null );
    MeasurementOperator measurementOperator = new MeasurementOperator();

    measurementOperator.handleStartupProbeMeasurementResults();

    assertEquals( 0, measurementOperator.getProbeCount() );
  }

  @Test
  public void testHandleStartupProbeMeasurementResultsExecutedOnce() {
    requestProbing( FONT_DATA_1 );
    fakeMessageWithMeasurementResult( FONT_DATA_1, null );
    operator.handleStartupProbeMeasurementResults();
    requestProbing( FONT_DATA_2 );
    fakeMessageWithMeasurementResult( FONT_DATA_2, null );

    operator.handleStartupProbeMeasurementResults();

    assertEquals( 1, operator.getProbeCount() );
  }

  @Test
  public void testAddItemToMeasure() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  @Test
  public void testAddItemToMeasureIsIdempotent() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  @Test
  public void testGetItemsToMeasureWithEmptyResult() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();

    assertEquals( 0, items.length );
  }

  @Test
  public void testRenderFontProbing() {
    prepareTextProbing();

    operator.handleMeasurementRequests();

    checkResponseContainsProbeCall();
  }

  @Test
  public void testRenderStringMeasurements() {
    prepareTextProbing();

    operator.handleMeasurementRequests();

    checkResponseContainsMeasurementCall();
  }

  @Test
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
    ProbeStore textSizeProbeStore = getApplicationContext().getProbeStore();
    textSizeProbeStore.createProbe( fontData );
  }

  private void fakeMessageWithMeasurementResult( FontData fontData,
                                                 MeasurementItem measurementItem )
  {
    Fixture.fakeNewRequest();
    JsonObject results = new JsonObject();
    if( fontData != null ) {
      results.add( MeasurementUtil.getId( fontData ), createJsonArray( 3, 4 ) );
    }
    if( measurementItem != null ) {
      results.add( MeasurementUtil.getId( measurementItem ), createJsonArray( 12, 4 ) );
    }
    JsonObject parameters = new JsonObject().add( PROPERTY_RESULTS, results );
    Fixture.fakeCallOperation( TYPE, METHOD_STORE_MEASUREMENTS, parameters  );
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
    Fixture.fakeNewRequest();
    operator.addProbeToMeasure( fontData1 );
  }

  private void checkMeasurementItemBuffering( MeasurementItem item ) {
    assertEquals( 1, MeasurementOperator.getInstance().getItems().length );
    assertSame( item, MeasurementOperator.getInstance().getItems() [ 0 ] );
  }

  private void checkResponseContainsMeasurementCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS );
    JsonValue itemsProperty = operation.getProperty( PROPERTY_ITEMS );
    String[] expected = getMeasurementCall();
    checkResponseContainsContent( expected, itemsProperty.toString() );
  }

  private void checkResponseContainsProbeCall() {
    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS );
    JsonValue itemsProperty = operation.getProperty( PROPERTY_ITEMS );
    String[] expected = getProbeCall();
    checkResponseContainsContent( expected, itemsProperty.toString() );
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
      ",[\"arial\"],10,true,false,-1,true]",
      ",[\"helvetia\",\"ms sans serif\"],12,true,false,-1,true]",
      ",[\"Bogus  Font  Name\"],12,true,false,-1,true]"
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
      new Font( display, "arial", 10, SWT.BOLD ),
      new Font( display, "helvetia, ms sans serif", 12, SWT.BOLD ),
      new Font( display, "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD )
    };
    TextSizeUtil.stringExtent( fonts[ 0 ], "FirstString" );
    TextSizeUtil.stringExtent( fonts[ 1 ], "SecondString" );
    TextSizeUtil.stringExtent( fonts[ 2 ], "Weird \" String \\" );
  }

}
