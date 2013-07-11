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
package org.eclipse.swt.internal.widgets.combokit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_IN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_OUT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_HELP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MENU_DETECT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_UP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_TRAVERSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ComboOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Combo combo;
  private Combo mockedCombo;
  private ComboOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    combo = new Combo( shell, SWT.NONE );
    combo.setBounds( 0, 0, 100, 20 );
    mockedCombo = mock( Combo.class );
    handler = new ComboOperationHandler( combo );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetSelectionIndex() {
    combo.add( "item 1" );
    combo.add( "item 2" );

    handler.handleSet( new JsonObject().add( "selectionIndex", 1 ) );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testHandleSetListVisible() {
    handler.handleSet( new JsonObject().add( "listVisible", true ) );

    assertTrue( combo.getListVisible() );
  }

  @Test
  public void testHandleText() {
    handler.handleSet( new JsonObject().add( "text", "abc" ) );

    assertEquals( "abc", combo.getText() );
  }

  @Test
  public void testHandleText_withVerifyListener() {
    combo.setText( "some text" );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.Verify, listener );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "text", "verify me" ) );

    assertEquals( "verify me", combo.getText() );
  }

  @Test
  public void testHandleSelection() {
    combo.setText( "text" );

    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inSameOperation() {
    combo.setText( "original text" );

    JsonObject properties = new JsonObject()
      .add( "text", "abc" )
      .add( "selectionStart", 1 )
      .add( "selectionLength", 1 );
    handler.handleSet( properties );

    assertEquals( "abc", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inDifferentOperation() {
    combo.setText( "original text" );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selectionStart", 1 ).add( "selectionLength", 1 ) );

    assertEquals( "abc", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleNotifySelection() {
    when( mockedCombo.getBounds() ).thenReturn( new Rectangle( 1, 2, 3, 4) );
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyModify() {
    handler = new ComboOperationHandler( mockedCombo );

    handler.handleNotify( ClientMessageConst.EVENT_MODIFY, null );

    verify( mockedCombo, times( 0 ) ).notifyListeners( eq( SWT.Modify ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusIn() {
    handler = new ComboOperationHandler( mockedCombo );

    handler.handleNotify( EVENT_FOCUS_IN, null );

    verify( mockedCombo ).notifyListeners( eq( SWT.FocusIn ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusOut() {
    Combo comboSpy = spy( combo );
    handler = new ComboOperationHandler( comboSpy );

    handler.handleNotify( EVENT_FOCUS_OUT, null );

    verify( comboSpy ).notifyListeners( eq( SWT.FocusOut ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDown() {
    Combo spyCombo = spy( combo );
    handler = new ComboOperationHandler( spyCombo );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyCombo ).notifyListeners( eq( SWT.MouseDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyMouseDown_coordinatesOutOfClientArea() {
    Combo spyCombo = spy( combo );
    handler = new ComboOperationHandler( spyCombo );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 110 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( spyCombo, times( 0 ) ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDoubleClick() {
    Combo spyCombo = spy( combo );
    handler = new ComboOperationHandler( spyCombo );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOUBLE_CLICK, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyCombo ).notifyListeners( eq( SWT.MouseDoubleClick ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
    assertEquals( 4, event.time );
    assertEquals( 2, event.count );
  }

  @Test
  public void testHandleNotifyMouseUp() {
    Combo spyCombo = spy( combo );
    handler = new ComboOperationHandler( spyCombo );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_UP, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyCombo ).notifyListeners( eq( SWT.MouseUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyTraverse() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );
    handler.handleNotify( EVENT_TRAVERSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.Traverse ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 9, event.keyCode );
    assertEquals( 9, event.character );
  }

  @Test
  public void testHandleNotifyTraverse_wrongModifier() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );
    handler.handleNotify( EVENT_TRAVERSE, properties );

    verify( mockedCombo, times( 0 ) ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyKeyDown() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 97 )
      .add( "charCode", 65 );
    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.KeyDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 65, event.character );
  }

  @Test
  public void testHandleNotifyKeyUp() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 97 )
      .add( "charCode", 65 );
    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.KeyUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 65, event.character );
  }

  @Test
  public void testHandleNotifyMenuDetect() {
    handler = new ComboOperationHandler( mockedCombo );

    JsonObject properties = new JsonObject().add( "x", 1 ).add( "y", 2 );
    handler.handleNotify( EVENT_MENU_DETECT, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.MenuDetect ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
  }

  @Test
  public void testHandleNotifyHelp() {
    handler = new ComboOperationHandler( mockedCombo );

    handler.handleNotify( EVENT_HELP, null );

    verify( mockedCombo ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

  @Test( expected=UnsupportedOperationException.class )
  public void testHandleNotify_unknownOperation() {
    handler.handleNotify( "Unknown", null );
  }

}
