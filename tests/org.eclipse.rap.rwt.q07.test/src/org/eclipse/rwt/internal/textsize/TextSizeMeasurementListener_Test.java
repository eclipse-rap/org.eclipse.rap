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

import org.eclipse.rwt.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.textsize.TextSizeProbeResults.ProbeResult;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class TextSizeMeasurementListener_Test extends TestCase {
  private static final int EXPAND_AND_RESTORE = 2;
  private static final FontData FONT_DATA = new FontData( "arial", 12, SWT.BOLD );
  private static final String RESPONSE_CALL = "org.eclipse.swt.FontSizeCalculation.measureStrings";
  private static final String PROBE_CALL = "org.eclipse.swt.FontSizeCalculation.probe";
  
  private TextSizeMeasurementListener listener;
  private int resizeCount;

  public void testGetPhaseId() {
    PhaseId phaseId = listener.getPhaseId();
    
    assertSame( PhaseId.ANY, phaseId );
  }
  
  public void testAfterPhaseWithoutMeasurementItemsOrProbes() {
    listener.afterPhase( PhaseListenerUtil.createRenderEvent() );
    
    assertFalse( responseContains( PROBE_CALL ) );
    assertFalse( responseContains( RESPONSE_CALL ) );
  }

  public void testAfterPhaseWithMeasurementItems() {
    MeasurementUtil.addItemToMeasure( new MeasurementItem( "text", FONT_DATA, 0 ) );
   
    listener.afterPhase( PhaseListenerUtil.createRenderEvent() );
    
    assertFalse( responseContains( PROBE_CALL ) );
    assertTrue( responseContains( RESPONSE_CALL ) );
  }

  public void testAfterPhaseWithProbes() {
    TextSizeProbeStore.addProbeToMeasure( FONT_DATA );
    
    listener.afterPhase( PhaseListenerUtil.createRenderEvent() );
    
    assertTrue( responseContains( PROBE_CALL ) );
    assertFalse( responseContains( RESPONSE_CALL ) );
  }

  public void testAfterPhaseWithMeasurementItemsButWrongPhaseId() {
    MeasurementUtil.addItemToMeasure( new MeasurementItem( "text", FONT_DATA, 0 ) );
    
    executeNonRenderPhases();
    
    assertFalse( responseContains( PROBE_CALL ) );
    assertFalse( responseContains( RESPONSE_CALL ) );
  }


  public void testAfterPhaseWithProbesButWrongPhaseId() {
    TextSizeProbeStore.addProbeToMeasure( FONT_DATA );
    
    executeNonRenderPhases();
    
    assertFalse( responseContains( PROBE_CALL ) );
    assertFalse( responseContains( RESPONSE_CALL ) );
  }
  
  public void testAfterPhaseWithMeasuredProbes() {
    fakeRequestWithProbeMeasurementResults();

    listener.afterPhase( PhaseListenerUtil.createProcessActionEvent() );
    
    checkProbeResultHasBeenStored();
  }
  
  public void testAfterPhaseWithMeasuredItems() {
    createShellWithResizeListener();
    fakeRequestWithItemMeasurementResults();
    
    listener.afterPhase( PhaseListenerUtil.createProcessActionEvent() );
    
    checkTextMeasurementResultHasBeenStored();
    checkShellHasBeenResized();
  }

  public void testAfterPhaseWithoutMeasuredItemsMustNotResizeShell() {
    createShellWithResizeListener();
    
    listener.afterPhase( PhaseListenerUtil.createProcessActionEvent() );
    
    checkShellHasNotBeenResized();
  }
  
  public void testBeforePhaseProbeMeasurementOfStartupProbes() {
    createProbe();
    fakeRequestWithProbeMeasurementResults();
    
    listener.beforePhase( PhaseListenerUtil.createPrepareUIRootEvent() );
    
    checkProbeResultWasStored();
  }

  protected void setUp() throws Exception {
    listener = new TextSizeMeasurementListener();

    Fixture.setUp();
    Fixture.fakeNewRequest( new Display() );
    initResizeCount();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void checkProbeResultWasStored() {
    assertNotNull( TextSizeProbeResults.getInstance().getProbeResult( FONT_DATA ) );
  }

  private void initResizeCount() {
    resizeCount = 0;
  }
  
  private void createProbe() {
    TextSizeProbeStore textSizeProbeStore = RWTFactory.getTextSizeProbeStore();
    textSizeProbeStore.createProbe( FONT_DATA, "probeText" );
  }

  private void checkShellHasBeenResized() {
    assertEquals( EXPAND_AND_RESTORE, resizeCount );
  }
  
  private void checkShellHasNotBeenResized() {
    assertEquals( 0, resizeCount );
  }
  
  private void checkProbeResultHasBeenStored() {
    ProbeResult probeResult = TextSizeProbeResults.getInstance().getProbeResult( FONT_DATA );
    assertEquals( new Point( 5, 10 ), probeResult.getSize() );
  }

  private void checkTextMeasurementResultHasBeenStored() {
    Font font = Graphics.getFont( FONT_DATA );
    assertEquals( new Point( 100, 10 ), Graphics.stringExtent( font, "text" ) );
  }
  
  private void fakeRequestWithProbeMeasurementResults() {
    TextSizeProbeStore.addProbeToMeasure( FONT_DATA );
    listener.afterPhase( PhaseListenerUtil.createRenderEvent() );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.addParameter( String.valueOf( FONT_DATA.hashCode() ), "5,10" );
  }
  
  private void fakeRequestWithItemMeasurementResults() {
    MeasurementItem itemToMeasure = new MeasurementItem( "text", FONT_DATA, -1 );
    MeasurementUtil.addItemToMeasure( itemToMeasure );
    fakeRequestWithProbeMeasurementResults();
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.addParameter( String.valueOf( itemToMeasure.hashCode() ), "100,10" );
  }
  
  private boolean responseContains( String probeCall ) {
    return Fixture.getAllMarkup().indexOf( probeCall ) != -1;
  }

  private void executeNonRenderPhases() {
    listener.afterPhase( PhaseListenerUtil.createPrepareUIRootEvent() );
    listener.afterPhase( PhaseListenerUtil.createReadDataEvent() );
    listener.afterPhase( PhaseListenerUtil.createProcessActionEvent() );
  }
  
  private void createShellWithResizeListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Shell shell = new Shell( LifeCycleUtil.getSessionDisplay() );
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( ControlEvent evt ) {
        resizeCount++;
      }
    } );
  }
}