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
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    operator = new MeasurementOperator();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void createProbeOfFont1() {
    TextSizeProbeStore textSizeProbeStore = RWTFactory.getTextSizeProbeStore();
    textSizeProbeStore.createProbe( FONT_DATA_1, TEXT_TO_MEASURE );
  }
  
  private void fakeRequestParamWithMeasurementResultOfItem( MeasurementItem measurementItem ) {
    Fixture.fakeRequestParam( String.valueOf( measurementItem.hashCode() ), "12,4" );
  }

  private void fakeRequestParamWithMeasurementResultOfProbe( FontData fontData ) {
    Fixture.fakeRequestParam( String.valueOf( fontData.hashCode() ), "3,4" );
  }

  private void requestMeasurementOfItem1() {
    MeasurementUtil.addItemToMeasure( MEASUREMENT_ITEM_1 );
  }
  
  private void requestMeasurementOfItem2() {
    MeasurementUtil.addItemToMeasure( MEASUREMENT_ITEM_2 );
  }

  private void requestProbingOfFont1() {
    requestProbing( FONT_DATA_1 );
  }

  private void requestProbingOfFont2() {
    requestProbing( FONT_DATA_2 );
  }
  
  private void requestProbing( FontData fontData1 ) {
    Fixture.fakeNewRequest();
    TextSizeProbeStore.addProbeToMeasure( fontData1 );
  }
}