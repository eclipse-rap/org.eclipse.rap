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
package org.eclipse.swt.internal.custom.ccombokit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MODIFY;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class CComboOperationHandler_Test {

  private CCombo ccombo;
  private CComboOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ccombo = new CCombo( shell, SWT.NONE );
    handler = new CComboOperationHandler( ccombo );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetSelectionIndex() {
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );

    handler.handleSet( new JsonObject().add( "selectionIndex", 1 ) );

    assertEquals( 1, ccombo.getSelectionIndex() );
  }

  @Test
  public void testHandleSetListVisible() {
    handler.handleSet( new JsonObject().add( "listVisible", true ) );

    assertTrue( ccombo.getListVisible() );
  }

  @Test
  public void testHandleText() {
    handler.handleSet( new JsonObject().add( "text", "abc" ) );

    assertEquals( "abc", ccombo.getText() );
  }

  @Test
  public void testHandleText_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ccombo.setText( "some text" );
    ccombo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "text", "verify me" ) );

    assertEquals( "verify me", ccombo.getText() );
  }

  @Test
  public void testHandleSelection() {
    ccombo.setText( "text" );

    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleSelection_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ccombo.setText( "abc" );
    ccombo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inSameOperation() {
    ccombo.setText( "original text" );
    JsonObject properties = new JsonObject()
      .add( "text", "abc" )
      .add( "selectionStart", 1 )
      .add( "selectionLength", 1 );

    handler.handleSet( properties );

    assertEquals( "abc", ccombo.getText() );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inDifferentOperation() {
    ccombo.setText( "original text" );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( "abc", ccombo.getText() );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_changeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ccombo.addListener( SWT.Verify, new Listener() {
      public void handleEvent( Event event ) {
        event.text = "verified";
      }
    } );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( "verified", ccombo.getText() );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_doesNotChangeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ccombo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( "abc", ccombo.getText() );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  @Test
  public void testHandleNotifySelection() {
    ccombo = mock( CCombo.class );
    handler = new CComboOperationHandler( ccombo );
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( ccombo ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    ccombo = mock( CCombo.class );
    handler = new CComboOperationHandler( ccombo );
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( ccombo ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyModify() {
    ccombo = mock( CCombo.class );
    handler = new CComboOperationHandler( ccombo );

    handler.handleNotify( EVENT_MODIFY, new JsonObject() );

    verify( ccombo, never() ).notifyListeners( eq( SWT.Modify ), any( Event.class ) );
  }

}
