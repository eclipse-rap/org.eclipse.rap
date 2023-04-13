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
package org.eclipse.swt.internal.widgets.scalekit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Scale;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ScaleOperationHandler_Test {

  private Scale scale;
  private ScaleOperationHandler handler;

  @Before
  public void setUp() {
    scale = mock( Scale.class );
    handler = new ScaleOperationHandler( scale );
  }

  @Test
  public void testHandleSetSelection() {
    handler.handleSet( new JsonObject().add( "selection", 1 ) );

    verify( scale ).setSelection( 1 );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( scale ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

}
