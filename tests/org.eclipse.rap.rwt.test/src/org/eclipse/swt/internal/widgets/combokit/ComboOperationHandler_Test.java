/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
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
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.WidgetRemoteAdapter;
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
    handler = new ComboOperationHandler( mockedCombo );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetSelectionIndex() {
    handler = new ComboOperationHandler( combo );
    combo.add( "item 1" );
    combo.add( "item 2" );

    handler.handleSet( new JsonObject().add( "selectionIndex", 1 ) );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testHandleSetListVisible() {
    handler = new ComboOperationHandler( combo );

    handler.handleSet( new JsonObject().add( "listVisible", true ) );

    assertTrue( combo.getListVisible() );
  }

  @Test
  public void testHandleSetText() {
    handler = new ComboOperationHandler( combo );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );

    assertEquals( "abc", combo.getText() );
  }

  @Test
  public void testHandleSetText_doesNotResetSelection() {
    handler = new ComboOperationHandler( combo );
    combo.setText( "some text" );
    combo.setSelection( new Point( 2, 4 ) );

    handler.handleSet( new JsonObject().add( "text", "other text" ) );

    assertEquals( new Point( 2, 4 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetText_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    combo.setText( "some text" );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.Verify, listener );

    handler.handleSet( new JsonObject().add( "text", "verify me" ) );

    assertEquals( "verify me", combo.getText() );
  }

  /*
   * See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=449350
   */
  @Test
  public void testHandleSetText_withVerifyListener_onDisposedWidget() {
    handler = new ComboOperationHandler( combo );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.Verify, listener );
    handler.handleSet( new JsonObject().add( "text", "verify me" ) );
    combo.dispose();

    try {
      ProcessActionRunner.execute();
    } catch( SWTException exception ) {
      fail();
    }
  }

  @Test
  public void testHandleSetSelection() {
    handler = new ComboOperationHandler( combo );
    combo.setText( "text" );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetSelection_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    combo.setText( "abc" );
    combo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  /*
   * See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=449350
   */
  @Test
  public void testHandleSetSelection_withVerifyListener_onDisposedWidget() {
    handler = new ComboOperationHandler( combo );
    combo.setText( "abc" );
    combo.addListener( SWT.Verify, mock( Listener.class ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );
    combo.dispose();

    try {
      ProcessActionRunner.execute();
    } catch( SWTException exception ) {
      fail();
    }
  }

  @Test
  public void testHandleSetSelection_withVerifyListener_preservesAdjustedSelection() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    combo.setText( "abc" );
    combo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 10 ).add( 12 ) ) );

    assertEquals( new Point( 3, 3 ), getPreservedSelection( combo ) );
  }

  @Test
  public void testHandleSetTextAndSelection_inSameOperation() {
    handler = new ComboOperationHandler( combo );
    combo.setText( "original text" );
    JsonObject properties = new JsonObject()
      .add( "text", "abc" )
      .add( "selection", new JsonArray().add( 1 ).add( 2 ) );

    handler.handleSet( properties );

    assertEquals( "abc", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inDifferentOperation() {
    handler = new ComboOperationHandler( combo );
    combo.setText( "original text" );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "abc", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_changeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    combo.addListener( SWT.Verify, new Listener() {
      public void handleEvent( Event event ) {
        event.text = "verified";
      }
    } );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "verified", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_doesNotChangeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    combo.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "abc", combo.getText() );
    assertEquals( new Point( 1, 2 ), combo.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelectionIndex_fireModifyEventOnce() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new ComboOperationHandler( combo );
    Listener listener = mock( Listener.class );
    combo.addListener( SWT.Modify, listener );
    combo.setItems( new String[] { "a", "b", "c" } );

    handler.handleSet( new JsonObject().add( "text", "b" ).add( "selectionIndex", 1 ) );

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyModify() {
    handler.handleNotify( ClientMessageConst.EVENT_MODIFY, new JsonObject() );

    verify( mockedCombo, never() ).notifyListeners( eq( SWT.Modify ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusIn() {
    handler.handleNotify( EVENT_FOCUS_IN, new JsonObject() );

    verify( mockedCombo ).notifyListeners( eq( SWT.FocusIn ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyFocusOut() {
    handler.handleNotify( EVENT_FOCUS_OUT, new JsonObject() );

    verify( mockedCombo ).notifyListeners( eq( SWT.FocusOut ), any( Event.class ) );
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
    JsonObject properties = new JsonObject()
      .add( "ctrlKey", true )
      .add( "keyCode", 9 )
      .add( "charCode", 0 );

    handler.handleNotify( EVENT_TRAVERSE, properties );

    verify( mockedCombo, times( 0 ) ).notifyListeners( eq( SWT.Traverse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyKeyDown() {
    JsonObject properties = new JsonObject()
      .add( "shiftKey", true )
      .add( "keyCode", 65 )
      .add( "charCode", 97 );

    handler.handleNotify( EVENT_KEY_DOWN, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedCombo ).notifyListeners( eq( SWT.KeyDown ), captor.capture() );
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
    verify( mockedCombo ).notifyListeners( eq( SWT.KeyUp ), captor.capture() );
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
    verify( mockedCombo ).notifyListeners( eq( SWT.MenuDetect ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
  }

  @Test
  public void testHandleNotifyHelp() {
    handler.handleNotify( EVENT_HELP, new JsonObject() );

    verify( mockedCombo ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

  @Test( expected=UnsupportedOperationException.class )
  public void testHandleNotify_unknownOperation() {
    handler.handleNotify( "Unknown", new JsonObject() );
  }

  private static Point getPreservedSelection( Combo combo ) {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )WidgetUtil.getAdapter( combo );
    return ( Point )adapter.getPreserved( "selection" );
  }

}
