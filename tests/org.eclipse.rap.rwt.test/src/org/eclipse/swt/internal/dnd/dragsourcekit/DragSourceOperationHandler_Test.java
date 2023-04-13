/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import static org.eclipse.swt.internal.dnd.DNDUtil.hasDataTypeChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.hasDetailChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.hasFeedbackChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.isCanceled;
import static org.eclipse.swt.internal.dnd.DNDUtil.setDataTypeChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.setDetailChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.setFeedbackChanged;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class DragSourceOperationHandler_Test {

  private Control sourceControl;
  private DragSource dragSource;
  private DragSourceOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    sourceControl = spy( new Label( shell, SWT.NONE ) );
    sourceControl.setBounds( 5, 5, 100, 20 );
    dragSource = spy( new DragSource( sourceControl, DND.DROP_NONE ) );
    handler = new DragSourceOperationHandler( dragSource );
    reset( sourceControl );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifyDragStart_notifiesListeners() {
    JsonObject properties = new JsonObject().add( "x", 10 ).add( "y", 20 ).add( "time", 3 );

    handler.handleNotify( "DragStart", properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( dragSource ).notifyListeners( eq( DND.DragStart ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 5, event.x );
    assertEquals( 15, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_NONE, event.detail );
  }

  @Test
  public void testHandleNotifyDragStart_notifiesDragDetectListeners() {
    JsonObject properties = new JsonObject().add( "x", 10 ).add( "y", 20 ).add( "time", 3 );

    handler.handleNotify( "DragStart", properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( sourceControl ).notifyListeners( eq( SWT.DragDetect ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 5, event.x );
    assertEquals( 15, event.y );
    assertEquals( 3, event.time );
    assertEquals( 1, event.button );
  }

  @Test
  public void testHandleNotifyDragStart_setsCancelFlag() {
    dragSource.addListener( DND.DragStart, new Listener() {
      public void handleEvent( Event event ) {
        event.doit = false;
      }
    } );
    JsonObject properties = new JsonObject().add( "x", 10 ).add( "y", 20 ).add( "time", 3 );

    handler.handleNotify( "DragStart", properties );

    assertTrue( isCanceled() );
  }

  @Test
  public void testHandleNotifyDragEnd_notifiesListeners() {
    setDetailChanged( mock( Control.class ), DND.DROP_COPY );
    JsonObject properties = new JsonObject().add( "x", 10 ).add( "y", 20 ).add( "time", 3 );

    handler.handleNotify( "DragEnd", properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( dragSource ).notifyListeners( eq( DND.DragEnd ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 5, event.x );
    assertEquals( 15, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_COPY, event.detail );
  }

  @Test
  public void testHandleNotifyDragEnd_cancelsChangedEventFields() {
    Control dropControl = mock( Control.class );
    setDetailChanged( dropControl, DND.DROP_COPY );
    setFeedbackChanged( dropControl, DND.FEEDBACK_INSERT_AFTER );
    setDataTypeChanged( dropControl, mock( TransferData.class ) );
    JsonObject properties = new JsonObject().add( "x", 10 ).add( "y", 20 ).add( "time", 3 );

    handler.handleNotify( "DragEnd", properties );

    assertFalse( hasDetailChanged() );
    assertFalse( hasFeedbackChanged() );
    assertFalse( hasDataTypeChanged() );
  }

}
