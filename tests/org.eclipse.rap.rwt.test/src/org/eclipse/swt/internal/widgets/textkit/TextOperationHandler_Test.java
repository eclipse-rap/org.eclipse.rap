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
package org.eclipse.swt.internal.widgets.textkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;



public class TextOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Text text;
  private Text mockedText;
  private TextOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    text = new Text( shell, SWT.NONE );
    text.setBounds( 0, 0, 100, 20 );
    mockedText = mock( Text.class );
    handler = new TextOperationHandler( mockedText );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetText() {
    handler = new TextOperationHandler( text );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );

    assertEquals( "abc", text.getText() );
  }

  @Test
  public void testHandleSetText_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new TextOperationHandler( text );
    text.setText( "some text" );
    text.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "text", "verify me" ) );

    assertEquals( "verify me", text.getText() );
  }

  /*
   * See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=449350
   */
  @Test
  public void testHandleSetText_withVerifyListener_onDisposedWidget() {
    handler = new TextOperationHandler( text );
    text.addListener( SWT.Verify, mock( Listener.class ) );
    handler.handleSet( new JsonObject().add( "text", "verify me" ) );
    text.dispose();

    try {
      ProcessActionRunner.execute();
    } catch( SWTException exception ) {
      fail();
    }
  }

  @Test
  public void testHandleSetText_doesNotResetSelection() {
    handler = new TextOperationHandler( text );
    text.setText( "some text" );
    text.setSelection( new Point( 2, 4 ) );

    handler.handleSet( new JsonObject().add( "text", "other text" ) );

    assertEquals( new Point( 2, 4 ), text.getSelection() );
  }

  @Test
  public void testHandleSetSelection() {
    handler = new TextOperationHandler( text );
    text.setText( "text" );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  @Test
  public void testHandleSetSelection_withVerifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new TextOperationHandler( text );
    text.setText( "abc" );
    text.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  /*
   * See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=449350
   */
  @Test
  public void testHandleSetSelection_withVerifyListener_onDisposedWidget() {
    handler = new TextOperationHandler( text );
    text.setText( "abc" );
    text.addListener( SWT.Verify, mock( Listener.class ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );
    text.dispose();

    try {
      ProcessActionRunner.execute();
    } catch( SWTException exception ) {
      fail();
    }
  }

  @Test
  public void testHandleSetSelection_withVerifyListener_preservesAdjustedSelection() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new TextOperationHandler( text );
    text.setText( "abc" );
    text.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 10 ).add( 12 ) ) );

    assertEquals( new Point( 3, 3 ), getPreservedSelection( text ) );
  }

  @Test
  public void testHandleSetTextAndSelection_inSameOperation() {
    handler = new TextOperationHandler( text );
    text.setText( "original text" );
    JsonObject properties = new JsonObject()
      .add( "text", "abc" )
      .add( "selection", new JsonArray().add( 1 ).add( 2 ) );

    handler.handleSet( properties );

    assertEquals( "abc", text.getText() );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_inDifferentOperation() {
    handler = new TextOperationHandler( text );
    text.setText( "original text" );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "abc", text.getText() );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_changeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new TextOperationHandler( text );
    text.addListener( SWT.Verify, new Listener() {
      public void handleEvent( Event event ) {
        event.text = "verified";
      }
    } );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "verified", text.getText() );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  @Test
  public void testHandleSetTextAndSelection_withVerifyListener_doesNotChangeText() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    handler = new TextOperationHandler( text );
    text.addListener( SWT.Verify, mock( Listener.class ) );

    handler.handleSet( new JsonObject().add( "text", "abc" ) );
    handler.handleSet( new JsonObject().add( "selection", new JsonArray().add( 1 ).add( 2 ) ) );

    assertEquals( "abc", text.getText() );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedText ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifySelection_onMulti() {
    mockedText = spy( new Text( shell, SWT.MULTI ) );
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    verify( mockedText, never() ).notifyListeners( eq( SWT.Selection ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedText ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection_onMulti() {
    mockedText = spy( new Text( shell, SWT.MULTI ) );
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    verify( mockedText, never() ).notifyListeners( eq( SWT.Selection ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyDefaultSelection_withDetailSearch() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "detail", "search" );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedText ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ICON_SEARCH, captor.getValue().detail );
  }

  @Test
  public void testHandleNotifyDefaultSelection_withDetailCancel() {
    JsonObject properties = new JsonObject()
    .add( "altKey", true )
    .add( "shiftKey", true )
    .add( "detail", "cancel" );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( mockedText ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    assertEquals( SWT.ICON_CANCEL, captor.getValue().detail );
  }

  @Test
  public void testHandleNotifyModify() {
    handler.handleNotify( ClientMessageConst.EVENT_MODIFY, new JsonObject() );

    verify( mockedText, never() ).notifyListeners( eq( SWT.Modify ), any( Event.class ) );
  }

  private static Point getPreservedSelection( Text text ) {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )WidgetUtil.getAdapter( text );
    return ( Point )adapter.getPreserved( "selection" );
  }

}
