/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
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
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.METHOD_MEASURE_ITEMS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.METHOD_STORE_MEASUREMENTS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.PARAM_ITEMS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.PARAM_RESULTS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.TYPE;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
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
    operator = MeasurementUtil.getMeasurementOperator();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitStartupProbes() {
    removeRemoteObject( TYPE );
    createProbe( FONT_DATA_1 );

    MeasurementOperator measurementOperator = new MeasurementOperator();

    assertEquals( 1, measurementOperator.getProbeCount() );
  }

  @Test
  public void testOperationHandler_handleCall_onStartup() {
    removeRemoteObject( TYPE );
    LifeCycleUtil.setSessionDisplay( null );
    createProbe( FONT_DATA_1 );
    MeasurementOperator measurementOperator = new MeasurementOperator();

    JsonObject parameters = createMeasurementResult( FONT_DATA_1, null );
    getOperationHandler( TYPE ).handleCall( METHOD_STORE_MEASUREMENTS, parameters );

    assertEquals( 0, measurementOperator.getProbeCount() );
  }

  @Test
  public void testOperationHandler_handleCall() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    operator.addProbeToMeasure( FONT_DATA_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    JsonObject parameters = createMeasurementResult( FONT_DATA_1, MEASUREMENT_ITEM_1 );
    getOperationHandler( TYPE ).handleCall( METHOD_STORE_MEASUREMENTS, parameters );

    assertEquals( 0, operator.getProbeCount() );
    assertEquals( 0, operator.getItemCount() );
  }

  @Test
  public void testOperationHandler_handleCall_triggersTextSizeRecalculation() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    operator.addProbeToMeasure( FONT_DATA_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );
    Shell shell = new Shell( display );
    Listener listener = mock( Listener.class );
    shell.addListener( SWT.Resize, listener );

    JsonObject parameters = createMeasurementResult( FONT_DATA_1, MEASUREMENT_ITEM_1 );
    getOperationHandler( TYPE ).handleCall( METHOD_STORE_MEASUREMENTS, parameters );

    verify( listener, times( 2 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testAddItemToMeasure() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  @Test
  public void testAddItemToMeasure_isIdempotent() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );

    checkMeasurementItemBuffering( MEASUREMENT_ITEM_1 );
  }

  @Test
  public void testAddItemToMeasure_subsequent() {
    operator.addItemToMeasure( MEASUREMENT_ITEM_1 );
    operator.addItemToMeasure( MEASUREMENT_ITEM_2 );

    assertEquals( 2, operator.getItemCount() );
    assertSame( MEASUREMENT_ITEM_1, operator.getItems()[ 0 ] );
    assertSame( MEASUREMENT_ITEM_2, operator.getItems()[ 1 ] );
  }

  @Test
  public void testAddProbeToMeasure() {
    operator.addProbeToMeasure( FONT_DATA_1 );

    assertEquals( 1, operator.getProbeCount() );
    assertSame( FONT_DATA_1, operator.getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testAddProbeToMeasure_isIdempotent() {
    operator.addProbeToMeasure( FONT_DATA_1 );
    operator.addProbeToMeasure( FONT_DATA_1 );

    assertEquals( 1, operator.getProbeCount() );
    assertSame( FONT_DATA_1, operator.getProbes()[ 0 ].getFontData() );
  }

  @Test
  public void testAddProbeToMeasure_subsequent() {
    operator.addProbeToMeasure( FONT_DATA_1 );
    operator.addProbeToMeasure( FONT_DATA_2 );

    assertEquals( 2, operator.getProbeCount() );
    assertTrue( probesContainFontData( FONT_DATA_1 ) );
    assertTrue( probesContainFontData( FONT_DATA_2 ) );
  }

  @Test
  public void testGetItemsToMeasureWithEmptyResult() {
    MeasurementItem[] items = operator.getItems();

    assertEquals( 0, items.length );
  }

  @Test
  public void testRenderFontProbing() {
    prepareTextProbing();

    operator.renderMeasurementItems();

    checkResponseContainsProbeCall();
  }

  @Test
  public void testRenderStringMeasurements() {
    prepareTextProbing();

    operator.renderMeasurementItems();

    checkResponseContainsMeasurementCall();
  }

  private boolean probesContainFontData( FontData fontData ) {
    Probe[] probes = operator.getProbes();
    for( Probe probe : probes ) {
      if( probe.getFontData() == fontData ) {
        return true;
      }
    }
    return false;
  }

  private void removeRemoteObject( String type ) {
    RemoteObjectRegistry.getInstance().remove( ( RemoteObjectImpl )getRemoteObject( type ) );
  }

  private OperationHandler getOperationHandler( String type ) {
    return ( ( RemoteObjectImpl )getRemoteObject( type ) ).getHandler();
  }

  private void createProbe( FontData fontData ) {
    getApplicationContext().getProbeStore().createProbe( fontData );
  }

  private JsonObject createMeasurementResult( FontData fontData, MeasurementItem measurementItem ) {
    JsonObject results = new JsonObject();
    if( fontData != null ) {
      results.add( getId( fontData ), createJsonArray( 3, 4 ) );
    }
    if( measurementItem != null ) {
      results.add( getId( measurementItem ), createJsonArray( 12, 4 ) );
    }
    return new JsonObject().add( PARAM_RESULTS, results );
  }

  private void checkMeasurementItemBuffering( MeasurementItem item ) {
    assertEquals( 1, operator.getItemCount() );
    assertSame( item, operator.getItems()[ 0 ] );
  }

  private void checkResponseContainsMeasurementCall() {
    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS );
    JsonValue itemsProperty = operation.getParameters().get( PARAM_ITEMS );
    checkResponseContainsContent( getMeasurementCall(), itemsProperty.toString() );
  }

  private void checkResponseContainsProbeCall() {
    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS );
    JsonValue itemsProperty = operation.getParameters().get( PARAM_ITEMS );
    checkResponseContainsContent( getProbeCall(), itemsProperty.toString() );
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
