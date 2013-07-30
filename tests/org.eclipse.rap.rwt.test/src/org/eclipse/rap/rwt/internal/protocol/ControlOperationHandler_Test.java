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
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createKeyEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createMenuDetectEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createMouseEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.createSelectionEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.getTraverseKey;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.processMouseEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.processTraverseEvent;
import static org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler.translateKeyCode;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
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

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.NONE );
    control.setBounds( 10, 10, 100, 20 );
    mockedControl = mock( Control.class );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreateSelectionEvent_withoutProperties() {
    Event event = createSelectionEvent( SWT.Selection, new JsonObject() );

    assertEquals( SWT.Selection, event.type );
  }

  @Test
  public void testCreateSelectionEvent_withStateMask() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "ctrlKey", true );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.ALT | SWT.CTRL, event.stateMask );
  }

  @Test
  public void testCreateSelectionEvent_withDetail() {
    JsonObject properties = new JsonObject().add( "detail", "check" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.CHECK, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withBounds() {
    JsonObject properties = new JsonObject()
      .add( "x", 1 )
      .add( "y", 2 )
      .add( "width", 3 )
      .add( "height", 4 );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), event.getBounds() );
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

    Event event = createMouseEvent( SWT.MouseDoubleClick, control, properties );

    assertEquals( SWT.MouseDoubleClick, event.type );
    assertEquals( control, event.widget );
    assertEquals( SWT.ALT | SWT.CTRL | SWT.BUTTON1, event.stateMask );
    assertEquals( 1, event.button );
    assertEquals( 4, event.x );
    assertEquals( 9, event.y );
    assertEquals( 4, event.time );
    assertEquals( 2, event.count );
  }

  @Test
  public void testProcessMouseEvent_onControl_valid() {
    Control spyControl = spy( control );
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 15 )
      .add( "y", 20 )
      .add( "time", 4 );

    processMouseEvent( SWT.MouseDown, spyControl, properties );

    verify( spyControl ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testProcessMouseEvent_onControl_invalid() {
    Control spyControl = spy( control );
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", -10 )
      .add( "y", 3 )
      .add( "time", 4 );

    processMouseEvent( SWT.MouseDown, spyControl, properties );

    verify( spyControl, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testProcessMouseEvent_onScrollable_valid() {
    Control spyControl = spy( shell );
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 20 )
      .add( "y", 60 )
      .add( "time", 4 );

    processMouseEvent( SWT.MouseDown, spyControl, properties );

    verify( spyControl ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testProcessMouseEvent_onScrollable_invalid() {
    Control spyControl = spy( shell );
    JsonObject properties = new JsonObject()
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 3 )
      .add( "time", 4 );

    processMouseEvent( SWT.MouseDown, spyControl, properties );

    verify( spyControl, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testProcessTraverseEvent() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    processTraverseEvent( mockedControl, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedControl ).notifyListeners( eq( SWT.Traverse ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.SHIFT, event.stateMask );
    assertEquals( 9, event.keyCode );
    assertEquals( 9, event.character );
    assertEquals( SWT.TRAVERSE_TAB_PREVIOUS, event.detail );
  }

  @Test
  public void testProcessTraverseEvent_wrongKeyModifier() {
    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    processTraverseEvent( mockedControl, properties );

    verify( mockedControl, never() ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
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

}
