/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.listkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ListOperationHandler_Test {

  private Display display;
  private Shell shell;
  private List mockedList;
  private ListOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    mockedList = mock( List.class );
    handler = new ListOperationHandler( mockedList );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetTopIndex() {
    handler.handleSet( new JsonObject().add( "topIndex", 1 ) );

    verify( mockedList ).setTopIndex( 1 );
  }

  @Test
  public void testHandleSetSelection_single() {
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 2 ) ) );

    verify( mockedList ).setSelection( eq( new int[] { 2 } ) );
  }

  @Test
  public void testHandleSetSelection_multi() {
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 2 ).add( 4 ) ) );

    verify( mockedList ).setSelection( eq( new int[] { 2, 4 } ) );
  }

  @Test
  public void testHandleSetSelection_empty() {
    handler.handleSet( new JsonObject().add( "selection", new JsonArray() ) );

    verify( mockedList ).setSelection( eq( new int[ 0 ] ) );
  }

  @Test
  public void testHandleSetFocusIndex() {
    List list = createList();
    handler = new ListOperationHandler( list );

    handler.handleSet( new JsonObject().add( "focusIndex", 1 ) );

    assertEquals( 1, list.getFocusIndex() );
  }

  @Test
  public void testHandleSetFocusIndex_reset() {
    List list = createList();
    handler = new ListOperationHandler( list );

    handler.handleSet( new JsonObject().add( "focusIndex", -1 ) );

    assertEquals( -1, list.getFocusIndex() );
  }

  @Test
  public void testHandleSetFocusIndex_outOfRange() {
    List list = createList();
    handler = new ListOperationHandler( list );

    handler.handleSet( new JsonObject().add( "focusIndex", 55 ) );

    assertEquals( 0, list.getFocusIndex() );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedList ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedList ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
  }

  private List createList() {
    List list = new List( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    list.setBounds( 0, 0, 100, 100 );
    for( int i = 0; i < 10; i++ ) {
      list.add( "Item " + i );
    }
    list.getAdapter( IListAdapter.class ).setFocusIndex( 0 );
    return list;
  }

}
