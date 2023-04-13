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
package org.eclipse.swt.internal.widgets.datetimekit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class DateTimeOperationHandler_Test {

  private DateTime dateTime;
  private DateTimeOperationHandler handler;

  @Before
  public void setUp() {
    dateTime = mock( DateTime.class );
    handler = new DateTimeOperationHandler( dateTime );
  }

  @Test
  public void testHandleSetDate() {
    handler.handleSet( new JsonObject().add( "year", 2013 ).add( "month", 0 ).add( "day", 1 ) );

    verify( dateTime ).setDate( eq( 2013 ), eq( 0 ), eq( 1 ) );
  }

  @Test
  public void testHandleSetTime() {
    handler.handleSet( new JsonObject().add( "hours", 1 ).add( "minutes", 2 ).add( "seconds", 3 ) );

    verify( dateTime ).setTime( eq( 1 ), eq( 2 ), eq( 3 ) );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( dateTime ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( dateTime ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

}
