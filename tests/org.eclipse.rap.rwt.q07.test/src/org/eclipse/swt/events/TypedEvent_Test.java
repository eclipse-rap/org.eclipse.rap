/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.widgets.*;


public class TypedEvent_Test extends TestCase {
  
  private static final String EVENT_FIRED 
    = "eventFired|";
  private static final String AFTER_RENDER 
    = "after" + PhaseId.RENDER + "|";
  private static final String BEFORE_RENDER 
    = "before" + PhaseId.RENDER + "|";
  private static final String AFTER_PROCESS_ACTION 
    = "after" + PhaseId.PROCESS_ACTION + "|";
  private static final String BEFORE_PROCESS_ACTION 
    = "before" + PhaseId.PROCESS_ACTION + "|";
  private static final String AFTER_READ_DATA 
    = "after" + PhaseId.READ_DATA + "|";
  private static final String BEFORE_READ_DATA 
    = "before" + PhaseId.READ_DATA + "|";
  private static final String AFTER_PREPARE_UI_ROOT 
    = "after" + PhaseId.PREPARE_UI_ROOT + "|";
  private static final String BEFORE_PREPARE_UI_ROOT 
    = "before" + PhaseId.PREPARE_UI_ROOT + "|";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testPhase() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( EVENT_FIRED );
      }
    } );

    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( final PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() + "|" );
      }
      public void afterPhase( final PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() + "|" );
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );
    RWTFixture.executeLifeCycleFromServerThread( );
    String expected 
      = BEFORE_PREPARE_UI_ROOT
      + AFTER_PREPARE_UI_ROOT
      + BEFORE_READ_DATA
      + AFTER_READ_DATA
      + BEFORE_PROCESS_ACTION
      + EVENT_FIRED
      + AFTER_PROCESS_ACTION
      + BEFORE_RENDER
      + AFTER_RENDER;
    assertEquals( expected, log.toString() );
  }
  
  public void testMultipleEventsInOneRequest() {
    // Ensure that two events get fired in the order as it is specified in
    // TypedEvent
    final java.util.List eventLog = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        eventLog.add( event );
      }
    } );
    ActivateEvent.addListener( button, new ActivateAdapter() {
      public void activated( ActivateEvent event ) {
        eventLog.add( event );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );
    
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( ActivateEvent.class, eventLog.get( 0 ).getClass() );
    assertEquals( SelectionEvent.class, eventLog.get( 1 ).getClass() );
  }
}
