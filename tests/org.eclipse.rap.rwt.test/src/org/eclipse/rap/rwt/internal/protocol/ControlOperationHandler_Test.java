/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createKeyEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createMenuDetectEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createMouseEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.getTraverseKey;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.translateKeyCode;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ControlOperationHandler_Test {

  private Shell shell;
  private Button control;
  private Control mockedControl;
  private IControlAdapter mockedControlAdapter;
  private ControlOperationHandler<Control> handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.BORDER );
    control.setBounds( 10, 10, 100, 20 );
    mockedControl = mock( Control.class );
    mockedControlAdapter = mock( IControlAdapter.class );
    when( mockedControl.getDisplay() ).thenReturn( display );
    when( mockedControl.getAdapter( IControlAdapter.class ) ).thenReturn( mockedControlAdapter );
    handler = new ControlOperationHandler<Control>( mockedControl ) {};
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreateMouseEvent() {
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 15 )
      .add( "y", 20 )
      .add( "time", 4 );

    Event event = createMouseEvent( SWT.MouseDown, control, properties );

    assertEquals( SWT.MouseDown, event.type );
    assertEquals( control, event.widget );
    assertEquals( SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 4, event.x );
    assertEquals( 9, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testCreateMouseEvent_withKeyModifiers() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "ctrlKey", true )
      .add( "button", 1 )
      .add( "x", 15 )
      .add( "y", 20 )
      .add( "time", 4 );

    Event event = createMouseEvent( SWT.MouseUp, control, properties );

    assertEquals( SWT.MouseUp, event.type );
    assertEquals( control, event.widget );
    assertEquals( SWT.ALT | SWT.CTRL | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 4, event.x );
    assertEquals( 9, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testCreateMouseEvent_eventCount() {
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 15 )
      .add( "y", 20 )
      .add( "time", 4 );

    Event event = createMouseEvent( SWT.MouseDoubleClick, control, properties );

    assertEquals( SWT.MouseDoubleClick, event.type );
    assertEquals( control, event.widget );
    assertEquals( 1, event.button );
    assertEquals( 4, event.x );
    assertEquals( 9, event.y );
    assertEquals( 4, event.time );
    assertEquals( 2, event.count );
  }

  @Test
  public void testGetTraverseKey() {
    int traverseKey;
    traverseKey = getTraverseKey( 13, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_RETURN );
    traverseKey = getTraverseKey( 27, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_ESCAPE );
    traverseKey = getTraverseKey( 9, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_NEXT );
    traverseKey = getTraverseKey( 9, SWT.SHIFT );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_PREVIOUS );
    traverseKey = getTraverseKey( 9, SWT.SHIFT | SWT.CTRL );
    assertEquals( traverseKey, SWT.TRAVERSE_NONE );
  }

  @Test
  public void testCreateKeyEvent_withLowerCaseCharacter() {
    JsonObject properties = new JsonObject().add( "keyCode", 65 ).add( "charCode", 97 );

    Event event = createKeyEvent( properties );

    assertEquals( SWT.None, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testCreateKeyEvent_withUpperCaseCharacter() {
    JsonObject properties = new JsonObject().add( "keyCode", 65 ).add( "charCode", 65 );

    Event event = createKeyEvent( properties );

    assertEquals( SWT.None, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'A', event.character );
  }

  @Test
  public void testCreateKeyEvent_withDigitCharacter() {
    JsonObject properties = new JsonObject().add( "keyCode", 49 ).add( "charCode", 49 );

    Event event = createKeyEvent( properties );

    assertEquals( SWT.None, event.stateMask );
    assertEquals( 49, event.keyCode );
    assertEquals( '1', event.character );
  }

  @Test
  public void testCreateKeyEvent_withPunctuationCharacter() {
    JsonObject properties = new JsonObject().add( "keyCode", 49 ).add( "charCode", 33 );

    Event event = createKeyEvent( properties );

    assertEquals( SWT.None, event.stateMask );
    assertEquals( 49, event.keyCode );
    assertEquals( '!', event.character );
  }

  @Test
  public void testCreateKeyEvent_withKeyModifier() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 65 );

    Event event = createKeyEvent( properties );

    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'A', event.character );
  }

  @Test
  public void testTranslateKeyCode() {
    int keyCode;
    keyCode = translateKeyCode( 40 );
    assertEquals( SWT.ARROW_DOWN, keyCode );
    keyCode = translateKeyCode( 37 );
    assertEquals( SWT.ARROW_LEFT, keyCode );
    keyCode = translateKeyCode( 38 );
    assertEquals( SWT.ARROW_UP, keyCode );
    keyCode = translateKeyCode( 39 );
    assertEquals( SWT.ARROW_RIGHT, keyCode );
    keyCode = translateKeyCode( 20 );
    assertEquals( SWT.CAPS_LOCK, keyCode );
    keyCode = translateKeyCode( 36 );
    assertEquals( SWT.HOME, keyCode );
    keyCode = translateKeyCode( 115 );
    assertEquals( SWT.F4, keyCode );
    keyCode = translateKeyCode( 123 );
    assertEquals( SWT.F12, keyCode );
    keyCode = translateKeyCode( 18 );
    assertEquals( SWT.ALT, keyCode );
  }

  @Test
  public void testCreateMenuDetectEvent() {
    JsonObject properties = new JsonObject()
      .add( "x", 1 )
      .add( "y", 2 );

    Event event = createMenuDetectEvent( properties );

    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
  }

  @Test
  public void testHandleNotify_processesFocusIn() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "FocusIn", properties );

    verify( mockedControl ).notifyListeners( eq( SWT.FocusIn ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesFocusOut() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "FocusOut", properties );

    verify( mockedControl ).notifyListeners( eq( SWT.FocusOut ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesMouseDown_onControl_valid() {
    Control spyControl = spy( control );
    handler = new ControlOperationHandler<Control>( spyControl ) {};
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 15 )
      .add( "y", 20 )
      .add( "time", 4 );

    handler.handleNotifyMouseDown( spyControl, properties );

    verify( spyControl ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesMouseDown_onControl_onBorder() {
    Control spyControl = spy( control );
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 10 )
      .add( "y", 13 )
      .add( "time", 4 );

    handler.handleNotifyMouseDown( spyControl, properties );

    verify( spyControl, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesKeyDown() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );

    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedControl ).notifyListeners( eq( SWT.KeyDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotify_processesKeyUp() {
    JsonObject properties = new JsonObject()
    .add( "shiftKey", true )
    .add( "keyCode", 65 )
    .add( "charCode", 97 );

    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedControl ).notifyListeners( eq( SWT.KeyUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotify_processesTraverse() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    handler.handleNotify( "Traverse", properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedControl ).notifyListeners( eq( SWT.Traverse ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 9, event.keyCode );
    assertEquals( 9, event.character );
    assertEquals( SWT.TRAVERSE_TAB_PREVIOUS, event.detail );
  }

  @Test
  public void testHandleNotify_processesTraverse_wrongKeyModifier() {
    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    handler.handleNotify( "Traverse", properties );

    verify( mockedControl, never() ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesMenuDetect() {
    JsonObject properties = new JsonObject().add( "x", 1 ).add( "y", 2 );

    handler.handleNotify( "MenuDetect", properties );

    verify( mockedControl ).notifyListeners( eq( SWT.MenuDetect ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesHelp() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Help", properties );

    verify( mockedControl ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesActivate() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Activate", properties );

    verify( mockedControl, never() ).notifyListeners( eq( SWT.Activate ), any( Event.class ) );
  }

  @Test
  public void testHandleNotify_processesDeactivate() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Deactivate", properties );

    verify( mockedControl, never() ).notifyListeners( eq( SWT.Deactivate ), any( Event.class ) );
  }

  @Test
  public void testHandleSetForeground() {
    JsonArray foreground = new JsonArray().add( 1 ).add( 2 ).add( 3 );
    JsonObject properties = new JsonObject().add( "foreground", foreground );

    handler.handleSet( mockedControl, properties );

    ArgumentCaptor<Color> captor = ArgumentCaptor.forClass( Color.class );
    verify( mockedControlAdapter ).setForeground( captor.capture() );
    Color foregroundColor = captor.getValue();
    assertEquals( 1, foregroundColor.getRed() );
    assertEquals( 2, foregroundColor.getGreen() );
    assertEquals( 3, foregroundColor.getBlue() );
  }

  @Test
  public void testHandleSetForeground_toNull() {
    JsonObject properties = new JsonObject().add( "foreground", JsonObject.NULL );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setForeground( eq( ( Color )null ) );
  }

  @Test
  public void testHandleSetBackground() {
    JsonArray background = new JsonArray().add( 1 ).add( 2 ).add( 3 );
    JsonObject properties = new JsonObject().add( "background", background );

    handler.handleSet( mockedControl, properties );

    ArgumentCaptor<Color> captor = ArgumentCaptor.forClass( Color.class );
    verify( mockedControlAdapter ).setBackground( captor.capture() );
    Color backgroundColor = captor.getValue();
    assertEquals( 1, backgroundColor.getRed() );
    assertEquals( 2, backgroundColor.getGreen() );
    assertEquals( 3, backgroundColor.getBlue() );
  }

  @Test
  public void testHandleSetBackground_toNull() {
    JsonObject properties = new JsonObject().add( "background", JsonObject.NULL );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setBackground( eq( ( Color )null ) );
  }

  @Test
  public void testHandleSetVisibility() {
    JsonObject properties = new JsonObject().add( "visibility", false );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setVisible( false );
  }

  @Test
  public void testHandleSetEnabled() {
    JsonObject properties = new JsonObject().add( "enabled", false );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setEnabled( false );
  }

  @Test
  public void testHandleSetToolTip() {
    JsonObject properties = new JsonObject().add( "toolTip", "foo" );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setToolTipText( "foo" );
  }

  @Test
  public void testHandleSetToolTip_toNull() {
    JsonObject properties = new JsonObject().add( "toolTip", JsonObject.NULL );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setToolTipText( eq( ( String )null ) );
  }

  @Test
  public void testHandleSetCursor() {
    JsonObject properties = new JsonObject().add( "cursor", "help" );

    handler.handleSet( mockedControl, properties );

    Cursor expected = new Cursor( mockedControl.getDisplay(), SWT.CURSOR_HELP );
    verify( mockedControlAdapter ).setCursor( eq( expected ) );
  }

  @Test
  public void testHandleSetCursor_toNull() {
    JsonObject properties = new JsonObject().add( "cursor", JsonObject.NULL );

    handler.handleSet( mockedControl, properties );

    verify( mockedControlAdapter ).setCursor( eq( ( Cursor )null ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testHandleSetCursor_invalidCursor() {
    JsonObject properties = new JsonObject().add( "cursor", "foo" );

    handler.handleSet( mockedControl, properties );
  }

}
