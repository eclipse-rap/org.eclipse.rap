/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public class MeasurementOperator_Test extends TestCase {
  private static final FontData FONT_DATA_1 = new FontData( "arial", 12, SWT.NONE );
  private static final FontData FONT_DATA_2 = new FontData( "courier", 14, SWT.BOLD );
  private static final String TEXT_TO_MEASURE = "textToMeasure";
  private static final MeasurementItem MEASUREMENT_ITEM_1
    = new MeasurementItem( TEXT_TO_MEASURE, FONT_DATA_1, -1 );
  private static final MeasurementItem MEASUREMENT_ITEM_2
    = new MeasurementItem( TEXT_TO_MEASURE, FONT_DATA_2, -1 );

  private MeasurementOperator operator;

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
    fakeRequestParamWithMeasurementResultOfProbe( FONT_DATA_1 );
    fakeRequestParamWithMeasurementResultOfItem( MEASUREMENT_ITEM_1 );

    operator.handleMeasurementResults();

    assertEquals( 0, operator.getProbeCount() );
    assertEquals( 0, operator.getItemCount() );
  }

  public void testIgnoreMeasurementResultsOfAlreadyMeasuredItem() {
    requestProbingOfFont1();
    fakeRequestParamWithMeasurementResultOfItem( MEASUREMENT_ITEM_1 );
    requestMeasurementOfItem1();
    
    operator.handleMeasurementRequests();

    assertEquals( 0, operator.getItemCount() );
  }
  
  public void testInitStartupProbes() {
    createProbeOfFont1();
    
    MeasurementOperator measurementOperator = new MeasurementOperator();
    
    assertEquals( 1, measurementOperator.getProbeCount() );
  }
  
  public void testHandleStartupProbeMeasurementResults() {
    createProbeOfFont1();
    fakeRequestParamWithMeasurementResultOfProbe( FONT_DATA_1 );
    MeasurementOperator measurementOperator = new MeasurementOperator();
    
    measurementOperator.handleStartupProbeMeasurementResults();
    
    assertEquals( 0, measurementOperator.getProbeCount() );
  }

  public void testAddItemToMeasure() {
    initializeSessionWithDisplay();
    MeasurementItem item = createItem();
    
    operator.addItemToMeasure( item );

    checkMeasurementItemBuffering( item );
  }

  public void testAddItemToMeasureIsIdempotent() {
    MeasurementItem item = createItem();
    
    operator.addItemToMeasure( item );
    operator.addItemToMeasure( item );

    checkMeasurementItemBuffering( item );
  }
    
  public void testGetItemsToMeasureWithEmptyResult() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();
    
    assertEquals( 0, items.length );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    operator = MeasurementOperator.getInstance();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void createProbeOfFont1() {
    createProbe( FONT_DATA_1 );
  }

  private void createProbe( FontData fontData ) {
    ProbeStore textSizeProbeStore = RWTFactory.getTextSizeProbeStore();
    textSizeProbeStore.createProbe( fontData );
  }

  private void fakeRequestParamWithMeasurementResultOfItem( MeasurementItem measurementItem ) {
    Fixture.fakeRequestParam( String.valueOf( measurementItem.hashCode() ), "12,4" );
  }

  private void fakeRequestParamWithMeasurementResultOfProbe( FontData fontData ) {
    Fixture.fakeRequestParam( String.valueOf( fontData.hashCode() ), "3,4" );
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
  
  private MeasurementItem createItem() {
    FontData fontData = new FontData( "arial", 13, SWT.BOLD );
    String textToMeasure = "textToMeasure";
    int wrapWidth = 2;
    return createItem( fontData, textToMeasure, wrapWidth );
  }

  private MeasurementItem createItem( FontData fontData, String textToMeasure, int wrapWidth ) {
    return new MeasurementItem( textToMeasure, fontData, wrapWidth );
  }
  
  private Display initializeSessionWithDisplay() {
    return new Display();
  }
  
  private void checkMeasurementItemBuffering( MeasurementItem item ) {
    assertEquals( 1, MeasurementOperator.getInstance().getItems().length );
    assertSame( item, MeasurementOperator.getInstance().getItems() [ 0 ] );
  }
}