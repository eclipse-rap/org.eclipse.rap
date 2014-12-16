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
package org.eclipse.swt.internal.widgets.displaykit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_RESIZE;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class DisplayOperationHandler_Test {

  private Display display;
  private DisplayOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    handler = new DisplayOperationHandler( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetBounds() {
    JsonObject properties = new JsonObject()
      .add( "bounds", new JsonArray().add( 10 ).add( 10 ).add( 100 ).add( 100 ) );

    handler.handleSet( properties );

    assertEquals( new Rectangle( 10, 10, 100, 100 ), display.getBounds() );
  }

  @Test
  public void testHandleSetCursorLocation() {
    JsonObject properties = new JsonObject()
      .add( "cursorLocation", new JsonArray().add( 1 ).add( 2 ) );

    handler.handleSet( properties );

    assertEquals( new Point( 1, 2 ), display.getCursorLocation() );
  }

  @Test
  public void testHandleSetFocusControl() {
    Shell shell = new Shell( display, SWT.NONE );
    new Button( shell, SWT.PUSH );
    Control control = new Button( shell, SWT.PUSH );
    shell.open();

    JsonObject properties = new JsonObject().add( "focusControl", getId( control ) );
    handler.handleSet( properties );

    assertEquals( control, display.getFocusControl() );
  }

  @Test
  public void testHandleSetFocusControl_nullValue() {
    Shell shell = new Shell( display, SWT.NONE );
    new Button( shell, SWT.PUSH );
    shell.open();
    Control previousFocusControl = display.getFocusControl();

    JsonObject properties = new JsonObject().add( "focusControl", JsonValue.NULL );
    handler.handleSet( properties );

    assertEquals( previousFocusControl, display.getFocusControl() );
  }

  @Test
  public void testHandleNotifyResize() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    getDisplayAdapter( display ).setBounds( new Rectangle( 1, 2, 3, 4 ) );
    Listener listener = mock( Listener.class );
    display.addListener( SWT.Resize, listener );

    handler.handleNotify( EVENT_RESIZE, new JsonObject() );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertSame( display, event.display );
    assertEquals( display.getBounds(), event.getBounds() );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return display.getAdapter( IDisplayAdapter.class );
  }

}
