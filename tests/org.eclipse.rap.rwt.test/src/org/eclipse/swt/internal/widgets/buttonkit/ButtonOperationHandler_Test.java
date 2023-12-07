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
package org.eclipse.swt.internal.widgets.buttonkit;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ButtonOperationHandler_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Button button;
  private Button mockedButton;
  private IControlAdapter mockedControlAdapter;
  private ButtonOperationHandler handler;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    button = new Button( shell, SWT.PUSH );
    button.setBounds( 0, 0, 100, 20 );
    mockedButton = mock( Button.class );
    mockedControlAdapter = mock( IControlAdapter.class );
    when( mockedButton.getAdapter( IControlAdapter.class ) ).thenReturn( mockedControlAdapter );
    handler = new ButtonOperationHandler( mockedButton );
  }

  @Test
  public void testHandleSetSelection() {
    button = new Button( shell, SWT.CHECK );
    handler = new ButtonOperationHandler( button );

    handler.handleSet( new JsonObject().add( "selection", true ) );

    assertTrue( button.getSelection() );
  }

  @Test
  public void testHandleSetText() {
    handler.handleSet( new JsonObject().add( "text", "foo" ) );

    verify( mockedButton ).setText( "foo" );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifySelection_timeFieldOnDeselectedRadio() {
    button = new Button( shell, SWT.RADIO );
    button.setSelection( false );
    Button spyButton = spy( button );
    handler = new ButtonOperationHandler( spyButton );

    handler.handleNotify( EVENT_SELECTION, new JsonObject() );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyButton ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( -1, captor.getValue().time );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyFocusIn() {
    handler.handleNotify( EVENT_FOCUS_IN, new JsonObject() );

    verify( mockedButton ).notifyListeners( eq( SWT.FocusIn ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusOut() {
    handler.handleNotify( EVENT_FOCUS_OUT, new JsonObject() );

    verify( mockedButton ).notifyListeners( eq( SWT.FocusOut ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDown() {
    Button spyButton = spy( button );
    handler = new ButtonOperationHandler( spyButton );
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );

    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyButton ).notifyListeners( eq( SWT.MouseDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyMouseDoubleClick() {
    Button spyButton = spy( button );
    handler = new ButtonOperationHandler( spyButton );
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );

    handler.handleNotify( EVENT_MOUSE_DOUBLE_CLICK, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyButton ).notifyListeners( eq( SWT.MouseDoubleClick ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 2, event.count );
  }

  @Test
  public void testHandleNotifyMouseUp() {
    Button spyButton = spy( button );
    handler = new ButtonOperationHandler( spyButton );
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );

    handler.handleNotify( EVENT_MOUSE_UP, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyButton ).notifyListeners( eq( SWT.MouseUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 2, event.x );
    assertEquals( 3, event.y );
    assertEquals( 4, event.time );
    assertEquals( 1, event.count );
  }

  @Test
  public void testHandleNotifyTraverse() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    handler.handleNotify( EVENT_TRAVERSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.Traverse ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 9, event.keyCode );
    assertEquals( 9, event.character );
  }

  @Test
  public void testHandleNotifyTraverse_wrongModifier() {
    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    handler.handleNotify( EVENT_TRAVERSE, properties );

    verify( mockedButton, times( 0 ) ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyKeyDown() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );

    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.KeyDown ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotifyKeyUp() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );

    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.KeyUp ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 97, event.keyCode );
    assertEquals( 'a', event.character );
  }

  @Test
  public void testHandleNotifyMenuDetect() {
    JsonObject properties = new JsonObject().add( "x", 1 ).add( "y", 2 );

    handler.handleNotify( EVENT_MENU_DETECT, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedButton ).notifyListeners( eq( SWT.MenuDetect ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
  }

  @Test
  public void testHandleNotifyHelp() {
    handler.handleNotify( EVENT_HELP, new JsonObject() );

    verify( mockedButton ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

  @Test( expected=UnsupportedOperationException.class )
  public void testHandleNotify_unknownOperation() {
    handler.handleNotify( "Unknown", new JsonObject() );
  }

  @Test
  public void testHandleSetForeground() {
    JsonArray foreground = new JsonArray().add( 1 ).add( 2 ).add( 3 );
    JsonObject properties = new JsonObject().add( "foreground", foreground );

    handler.handleSet( mockedButton, properties );

    ArgumentCaptor<Color> captor = ArgumentCaptor.forClass( Color.class );
    verify( mockedControlAdapter ).setForeground( captor.capture() );
    Color foregroundColor = captor.getValue();
    assertEquals( 1, foregroundColor.getRed() );
    assertEquals( 2, foregroundColor.getGreen() );
    assertEquals( 3, foregroundColor.getBlue() );
  }

  @Test
  public void testHandleSetBackground() {
    JsonArray background = new JsonArray().add( 1 ).add( 2 ).add( 3 );
    JsonObject properties = new JsonObject().add( "background", background );

    handler.handleSet( mockedButton, properties );

    ArgumentCaptor<Color> captor = ArgumentCaptor.forClass( Color.class );
    verify( mockedControlAdapter ).setBackground( captor.capture() );
    Color backgroundColor = captor.getValue();
    assertEquals( 1, backgroundColor.getRed() );
    assertEquals( 2, backgroundColor.getGreen() );
    assertEquals( 3, backgroundColor.getBlue() );
  }

}
