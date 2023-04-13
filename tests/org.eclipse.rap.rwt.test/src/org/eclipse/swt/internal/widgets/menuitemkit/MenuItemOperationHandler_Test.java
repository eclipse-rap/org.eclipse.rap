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
package org.eclipse.swt.internal.widgets.menuitemkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_HELP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class MenuItemOperationHandler_Test {

  private MenuItem item;
  private MenuItemOperationHandler handler;

  @Before
  public void setUp() {
    item = mock( MenuItem.class );
    handler = new MenuItemOperationHandler( item );
  }

  @Test
  public void testHandleSetSelection() {
    handler.handleSet( new JsonObject().add( "selection", true ) );

    verify( item ).setSelection( true );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( item ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifySelection_timeFieldOnDeselectedRadio() {
    doReturn( Integer.valueOf( SWT.RADIO ) ).when( item ).getStyle();
    doReturn( Boolean.FALSE ).when( item ).getSelection();

    handler.handleNotify( EVENT_SELECTION, new JsonObject() );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( item ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( -1, captor.getValue().time );
  }

  @Test
  public void testHandleNotifyHelp() {
    handler.handleNotify( EVENT_HELP, new JsonObject() );

    verify( item ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

}
