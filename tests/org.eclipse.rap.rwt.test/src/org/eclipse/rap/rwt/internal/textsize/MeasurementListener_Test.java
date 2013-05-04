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
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.PROPERTY_RESULTS;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MeasurementListener_Test {
  private static final int EXPAND_AND_RESTORE = 2;
  private static final FontData FONT_DATA = new FontData( "arial", 12, SWT.BOLD );

  private Display display;
  private MeasurementListener listener;
  private int resizeCount;

  @Before
  public void setUp() {
    listener = new MeasurementListener();

    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest();
    initResizeCount();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPhaseId() {
    PhaseId phaseId = listener.getPhaseId();

    assertSame( PhaseId.ANY, phaseId );
  }

  @Test
  public void testAfterPhaseWithoutMeasurementItemsOrProbes() {
    listener.afterPhase( createPhaseEvent( PhaseId.RENDER ) );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS ) );
  }

  @Test
  public void testAfterPhaseWithMeasurementItems() {
    MeasurementOperator.getInstance().addItemToMeasure( createItem() );

    listener.afterPhase( createPhaseEvent( PhaseId.RENDER ) );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS ) );
  }

  @Test
  public void testAfterPhaseWithProbes() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );

    listener.afterPhase( createPhaseEvent( PhaseId.RENDER ) );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS ) );
  }

  @Test
  public void testAfterPhaseWithMeasurementItemsButWrongPhaseId() {
    MeasurementOperator.getInstance().addItemToMeasure( createItem() );

    executeNonRenderPhases();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS ) );
  }

  @Test
  public void testAfterPhaseWithProbesButWrongPhaseId() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );

    executeNonRenderPhases();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, METHOD_MEASURE_ITEMS ) );
  }

  @Test
  public void testBeforePhaseWithMeasuredProbes() {
    fakeRequestWithProbeMeasurementResults();

    listener.beforePhase( createPhaseEvent( PhaseId.PROCESS_ACTION ) );

    checkProbeResultHasBeenStored();
  }

  @Test
  public void testBeforePhaseWithMeasuredItems() {
    createShellWithResizeListener();
    fakeRequestWithItemMeasurementResults();

    listener.beforePhase( createPhaseEvent( PhaseId.PROCESS_ACTION ) );

    checkTextMeasurementResultHasBeenStored();
    checkShellHasBeenResized();
  }

  @Test
  public void testBeforePhaseWithoutMeasuredItemsMustNotResizeShell() {
    createShellWithResizeListener();

    listener.beforePhase( createPhaseEvent( PhaseId.PROCESS_ACTION ) );

    checkShellHasNotBeenResized();
  }

  @Test
  public void testBeforePhaseProbeMeasurementOfStartupProbes() {
    createProbe();
    fakeRequestWithProbeMeasurementResults();

    listener.beforePhase( createPhaseEvent( PhaseId.PREPARE_UI_ROOT ) );

    checkProbeResultWasStored();
  }

  private void checkProbeResultWasStored() {
    assertNotNull( ProbeResultStore.getInstance().getProbeResult( FONT_DATA ) );
  }

  private void initResizeCount() {
    resizeCount = 0;
  }

  private void createProbe() {
    ProbeStore textSizeProbeStore = getApplicationContext().getProbeStore();
    textSizeProbeStore.createProbe( FONT_DATA );
  }

  private void checkShellHasBeenResized() {
    assertEquals( EXPAND_AND_RESTORE, resizeCount );
  }

  private void checkShellHasNotBeenResized() {
    assertEquals( 0, resizeCount );
  }

  private void checkProbeResultHasBeenStored() {
    ProbeResult probeResult = ProbeResultStore.getInstance().getProbeResult( FONT_DATA );
    assertEquals( new Point( 5, 10 ), probeResult.getSize() );
  }

  private void checkTextMeasurementResultHasBeenStored() {
    Font font = new Font( display, FONT_DATA );
    assertEquals( new Point( 100, 10 ), TextSizeUtil.stringExtent( font, "text" ) );
  }

  private void fakeRequestWithProbeMeasurementResults() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );
    listener.afterPhase( createPhaseEvent( PhaseId.RENDER ) );
    JsonObject results = new JsonObject()
      .add( MeasurementUtil.getId( FONT_DATA ), createJsonArray( 5, 10 ) );
    JsonObject parameters = new JsonObject().add( PROPERTY_RESULTS, results );
    Fixture.fakeCallOperation( TYPE, METHOD_STORE_MEASUREMENTS, parameters  );
  }

  private void fakeRequestWithItemMeasurementResults() {
    MeasurementItem itemToMeasure = createItem();
    MeasurementOperator.getInstance().addItemToMeasure( itemToMeasure );
    fakeRequestWithProbeMeasurementResults();
    JsonObject results = new JsonObject()
      .add( MeasurementUtil.getId( itemToMeasure ), createJsonArray( 100, 10 ) );
    JsonObject parameters = new JsonObject().add( PROPERTY_RESULTS, results );
    Fixture.fakeCallOperation( TYPE, METHOD_STORE_MEASUREMENTS, parameters  );
  }

  private void executeNonRenderPhases() {
    listener.afterPhase( createPhaseEvent( PhaseId.PREPARE_UI_ROOT ) );
    listener.afterPhase( createPhaseEvent( PhaseId.READ_DATA ) );
    listener.afterPhase( createPhaseEvent( PhaseId.PROCESS_ACTION ) );
  }

  private void createShellWithResizeListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Shell shell = new Shell( LifeCycleUtil.getSessionDisplay() );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent evt ) {
        resizeCount++;
      }
    } );
  }

  private static MeasurementItem createItem() {
    return new MeasurementItem( "text", FONT_DATA, SWT.DEFAULT, TextSizeUtil.STRING_EXTENT );
  }

  private static PhaseEvent createPhaseEvent( PhaseId phaseId ) {
    return new PhaseEvent( mock( LifeCycle.class ), phaseId );
  }

}
