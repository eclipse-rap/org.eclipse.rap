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
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ControlDecoratorOperationHandler_Test {

  private ControlDecorator decorator;
  private ControlDecoratorOperationHandler handler;

  @Before
  public void setUp() {
    decorator = mock( ControlDecorator.class );
    handler = new ControlDecoratorOperationHandler( decorator );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( decorator ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( decorator ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

}
