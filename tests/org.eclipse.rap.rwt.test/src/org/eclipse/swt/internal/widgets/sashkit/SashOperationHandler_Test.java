/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.sashkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Sash;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class SashOperationHandler_Test {

  private Sash sash;
  private SashOperationHandler handler;

  @Before
  public void setUp() {
    sash = mock( Sash.class );
    handler = new SashOperationHandler( sash );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "detail", "drag" )
      .add( "x", 1 )
      .add( "y", 2 )
      .add( "width", 3 )
      .add( "height", 4 );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( sash ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( SWT.DRAG, event.detail );
    assertEquals( new Rectangle( 1, 2, 3, 4 ), event.getBounds() );
  }

}
