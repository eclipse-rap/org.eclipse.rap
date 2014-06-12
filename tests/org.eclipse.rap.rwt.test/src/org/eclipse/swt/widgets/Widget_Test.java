/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.internal.scripting.ClientListenerUtil.getClientListenerOperations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtilAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.internal.events.EventList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


@SuppressWarnings( "deprecation" )
public class Widget_Test {

  private Display display;
  private Shell shell;
  private Widget widget;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    widget = new Widget( shell, SWT.NONE ) {};
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    UITestUtilAdapter.setUITestEnabled( false );
  }

  @Test
  public void testGetAdapter_forWidgetAdapter() {
    Object adapter = widget.getAdapter( WidgetAdapter.class );

    assertTrue( adapter instanceof WidgetAdapter );
  }

  @Test
  public void testGetAdapter_forWidgetAdapter_returnsSameInstance() {
    Object adapter1 = widget.getAdapter( WidgetAdapter.class );
    Object adapter2 = widget.getAdapter( WidgetAdapter.class );

    assertSame( adapter1, adapter2 );
  }

  @Test
  public void testGetAdapter_forWidgetAdapter_returnsDifferentInstances() {
    Object adapter1 = widget.getAdapter( WidgetAdapter.class );
    Widget anotherWidget = new Widget( shell, SWT.NONE ) {};

    Object adapter2 = anotherWidget.getAdapter( WidgetAdapter.class );

    assertNotSame( adapter1, adapter2 );
  }

  @Test
  public void testGetAdapter_succeedsForDisposedWidget() {
    widget.dispose();

    Object adapter = widget.getAdapter( WidgetAdapter.class );

    assertNotNull( adapter );
  }

  @Test
  public void testSetsParentOnAdapter() {
    WidgetAdapter adapter = widget.getAdapter( WidgetAdapter.class );

    assertSame( shell, adapter.getParent() );
  }

  @Test
  public void testCheckWidget() throws Throwable {
    Runnable target = new Runnable() {
      public void run() {
        widget.checkWidget();
      }
    };
    try {
      Fixture.runInThread( target );
      fail( "Illegal thread access expected." );
    } catch( SWTException swte ) {
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, swte.code );
    }
  }

  @Test
  public void testCheckBits() {
    int style = SWT.VERTICAL | SWT.HORIZONTAL;
    int result = Widget.checkBits( style, SWT.VERTICAL, SWT.HORIZONTAL, 0, 0, 0, 0 );
    assertTrue( ( result & SWT.VERTICAL ) != 0 );
    assertFalse( ( result & SWT.HORIZONTAL ) != 0 );
  }

  @Test
  public void testGetData_initiallyNull() {
    assertNull( widget.getData() );
  }

  @Test
  public void testGetData_nullForNonExistingKey() {
    assertNull( widget.getData( "non-existing-key" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetData_failsWithNullKey() {
    widget.getData( null );
  }

  @Test( expected = SWTException.class )
  public void testGetData_failsWhenDisposed() {
    widget.setData( "foo", "bar" );
    widget.dispose();

    widget.getData( "foo" );
  }

  @Test
  public void testSetData_singleData() {
    Object data = new Object();

    widget.setData( data );
    Object result = widget.getData();

    assertSame( data, result );
  }

  @Test
  public void testSetData_keyedData() {
    Object data1 = new Object();
    Object data2 = new Object();

    widget.setData( "key1", data1 );
    widget.setData( "key2", data2 );

    assertSame( data1, widget.getData( "key1" ) );
    assertSame( data2, widget.getData( "key2" ) );
  }

  @Test
  public void testSetData_keyedDataDoesNotChangeSingleData() {
    Object singleData = new Object();
    Object keyedData = new Object();

    widget.setData( singleData );
    widget.setData( "key", keyedData );

    assertSame( singleData, widget.getData() );
    assertSame( keyedData, widget.getData( "key" ) );
  }

  @Test
  public void testSetData_nullRemovesSingleData() {
    widget.setData( new Object() );

    widget.setData( null );

    assertNull( widget.getData() );
  }

  @Test
  public void testSetData_nullRemovesKeyedData() {
    widget.setData( "key", new Object() );

    widget.setData( "key", null );

    assertNull( widget.getData( "key" ) );
  }

  @Test
  public void testSetData_singleNullDoesNotRemoteKeyedData() {
    widget.setData( "key", new Object() );

    widget.setData( null );

    assertNotNull( widget.getData( "key" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_failsWithNullKey() {
    widget.setData( null, new Object() );
  }

  @Test( expected = SWTException.class )
  public void testSetData_failsWhenDisposed() {
    widget.setData( "foo", "bar" );
    widget.dispose();

    widget.getData( "foo" );
  }

  @Test
  public void testSetData_handlesCustomId() {
    UITestUtilAdapter.setUITestEnabled( true );

    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, "custom-id" );

    assertEquals( "custom-id", WidgetUtil.getId( widget ) );
  }

  @Test
  public void testGetCustomIdOnDisposedWidget() {
    UITestUtilAdapter.setUITestEnabled( true );

    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, "custom-id" );
    widget.dispose();

    assertEquals( "custom-id", WidgetUtil.getId( widget ) );
  }

  @Test
  public void testIsDisposed_initiallyFalse() {
    assertFalse( widget.isDisposed() );
  }

  @Test
  public void testIsDisposed_trueAfterDispose() {
    widget.dispose();

    assertTrue( widget.isDisposed() );
  }

  @Test
  public void testDispose_calledTwiceDoesNotHurt() {
    widget.dispose();
    widget.dispose();

    assertTrue( widget.isDisposed() );
  }

  @Test( expected = SWTException.class )
  public void testDispose_failsOnIllegalThread() throws Throwable {
    Fixture.runInThread( new Runnable() {
      public void run() {
        widget.dispose();
      }
    } );
  }

  @Test
  public void testDispose_withException() {
    widget.addListener( SWT.Dispose, new Listener() {
      public void handleEvent( Event event ) {
        throw new RuntimeException();
      }
    } );

    try {
      widget.dispose();
      fail( "Wrong test setup: dispose listener must throw exception" );
    } catch( Exception exception ) {
      // expected
    }

    assertFalse( widget.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );
  }

  @Test
  public void testDisposeParentWhileInDispose() {
    // This test leads to a stack overflow or, if line "item[ 0 ].dispose();"
    // is activated to a NPE
    final Composite composite = new Composite( shell, SWT.NONE );
    ToolBar toolbar = new ToolBar( composite, SWT.NONE );
    final ToolItem[] item = { null };
    toolbar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        item[ 0 ].dispose();
      }
    } );
    toolbar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        composite.dispose();
      }
    } );
    item[ 0 ] = new ToolItem( toolbar, SWT.PUSH );
    widget.dispose();
    // no assert: this test ensures that no StackOverflowError occurs
  }

  @Test
  public void testDisposeSelfWhileInDispose() {
    widget.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        widget.dispose();
      }
    } );
    widget.dispose();
    // no assert: this test ensures that no exception occurs
  }

  @Test
  public void testDisposeSelfWhileInDispose_RenderOnce() {
    Fixture.markInitialized( widget );
    widget.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        widget.dispose();
      }
    } );
    widget.dispose();
    int counter = 0;
    Widget[] disposedWidgets = DisposedWidgets.getAll();
    for( int i = 0; i < disposedWidgets.length; i++ ) {
      if( disposedWidgets[ i ] == widget ) {
        counter++;
      }
    }
    assertEquals( 1, counter );
  }

  @Test
  public void testAddDisposeListener() {
    widget.addDisposeListener( mock( DisposeListener.class ) );

    assertTrue( widget.isListening( SWT.Dispose ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddDisposeListener_failsWithNullArgument() {
    widget.addDisposeListener( null );
  }

  @Test
  public void testRemoveDisposeListenerWithRegisteredListener() {
    DisposeListener listener = mock( DisposeListener.class );
    widget.addDisposeListener( listener );

    widget.removeDisposeListener( listener );

    assertFalse( widget.isListening( SWT.Dispose ) );
  }

  @Test
  public void testRemoveDisposeListenerWithUnregisteredListener() {
    DisposeListener listener = mock( DisposeListener.class );

    widget.removeDisposeListener( listener );

    assertFalse( widget.isListening( SWT.Dispose ) );
  }

  // bug 328043
  @Test
  public void testUntypedDisposeListener() {
    DisposeListener listener = mock( DisposeListener.class );
    widget.addDisposeListener( listener );

    widget.notifyListeners( SWT.Dispose, new Event() );

    verify( listener ).widgetDisposed( any( DisposeEvent.class ) );
  }

  @Test
  public void testAddListener() {
    Listener listener = mock( Listener.class );

    widget.addListener( SWT.Selection, listener );

    Listener[] listeners = widget.getListeners( SWT.Selection );
    assertEquals( 1, listeners.length );
    assertSame( listener, listeners[ 0 ] );
  }

  @Test
  public void testAddListener_addsSameListenerTwice() {
    Listener listener = mock( Listener.class );

    widget.addListener( SWT.Selection, listener );
    widget.addListener( SWT.Selection, listener );

    Listener[] listeners = widget.getListeners( SWT.Selection );
    assertEquals( 2, listeners.length );
  }

  @Test
  public void testAddListener_handlesClientListeners() {
    ClientListener listener = mock( ClientListener.class );

    widget.addListener( SWT.Selection, listener );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 1, operations.size() );
    assertEquals( listener, operations.get( 0 ).getListener() );
    assertEquals( SWT.Selection, operations.get( 0 ).getEventType() );
  }

  @Test
  public void testAddListener_handlesMultipleClientListeners() {
    ClientListener listener1 = mock( ClientListener.class );
    ClientListener listener2 = mock( ClientListener.class );

    widget.addListener( SWT.Selection, listener1 );
    widget.addListener( SWT.Selection, listener2 );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 2, operations.size() );
    assertEquals( listener1, operations.get( 0 ).getListener() );
    assertEquals( listener2, operations.get( 1 ).getListener() );
  }

  @Test
  public void testAddListener_handlesDuplicateClientListeners() {
    ClientListener listener = mock( ClientListener.class );

    widget.addListener( SWT.Selection, listener );
    widget.addListener( SWT.Selection, listener );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 2, operations.size() );
    assertEquals( listener, operations.get( 0 ).getListener() );
    assertEquals( listener, operations.get( 1 ).getListener() );
  }

  @Test
  public void testRemoveListener() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Selection, listener );

    widget.removeListener( SWT.Selection, listener );

    assertEquals( 0, widget.getListeners( SWT.Selection ).length );
  }

  @Test
  public void testRemoveListener_removesOnlyOneListener() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Selection, listener );
    widget.addListener( SWT.Selection, listener );

    widget.removeListener( SWT.Selection, listener );

    assertEquals( 1, widget.getListeners( SWT.Selection ).length );
  }

  @Test
  public void testRemoveListener_doesNotFailIfNotAdded() {
    // Ensure that removing a listener that was never added is ignored
    // silently see https://bugs.eclipse.org/251816
    widget.removeListener( SWT.Activate, mock( Listener.class ) );
  }

  @Test
  public void testRemoveListener_handlesClientListeners() {
    ClientListener listener = mock( ClientListener.class );

    widget.removeListener( SWT.Selection, listener );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 1, operations.size() );
    assertEquals( listener, operations.get( 0 ).getListener() );
    assertEquals( SWT.Selection, operations.get( 0 ).getEventType() );
  }

  @Test
  public void testRemoveListener_handlesMultipleClientListeners() {
    ClientListener listener1 = mock( ClientListener.class );
    ClientListener listener2 = mock( ClientListener.class );

    widget.removeListener( SWT.Selection, listener1 );
    widget.removeListener( SWT.Selection, listener2 );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 2, operations.size() );
    assertEquals( listener1, operations.get( 0 ).getListener() );
    assertEquals( listener2, operations.get( 1 ).getListener() );
  }

  @Test
  public void testRemoveListener_handlesDuplicateClientListeners() {
    ClientListener listener = mock( ClientListener.class );

    widget.removeListener( SWT.Selection, listener );
    widget.removeListener( SWT.Selection, listener );

    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    assertEquals( 2, operations.size() );
    assertEquals( listener, operations.get( 0 ).getListener() );
    assertEquals( listener, operations.get( 1 ).getListener() );
  }

  @Test
  public void testNotifyListeners() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );
    Event event = mock( Event.class );

    widget.notifyListeners( SWT.Resize, event );

    verify( listener ).handleEvent( same( event ) );
  }

  @Test
  public void testNotifyListeners_notifiesTypedListeners() {
    ControlListener listener = mock( ControlListener.class );
    TypedListener typedListener = new TypedListener( listener );
    widget.addListener( SWT.Move, typedListener );
    widget.addListener( SWT.Resize, typedListener );

    widget.notifyListeners( SWT.Resize, new Event() );

    verify( listener ).controlResized( any( ControlEvent.class ) );
    verify( listener, never() ).controlMoved( any( ControlEvent.class ) );
  }

  @Test
  public void testNotifyListeners_notifiesDisplayFilters() {
    Listener filter = mock( Listener.class );
    display.addFilter( SWT.Resize, filter );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.notifyListeners( SWT.Resize, new Event() );

    InOrder inOrder = inOrder( filter, listener );
    inOrder.verify( filter ).handleEvent( any( Event.class ) );
    inOrder.verify( listener ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testNotifyListeners_withDenyingFilter() {
    Listener filter = spy( new Listener() {
      public void handleEvent( Event event ) {
        event.type = SWT.None;
      }
    } );
    display.addFilter( SWT.Resize, filter );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.notifyListeners( SWT.Resize, new Event() );

    verify( filter ).handleEvent( any( Event.class ) );
    verify( listener, never() ).handleEvent( any( Event.class ) );
  }

  // SWT always overrides e.type, e.display and e.widget
  @Test
  public void testNotifyListeners_eventFields() {
    final AtomicReference<Event> eventCaptor = new AtomicReference<Event>();
    display.addFilter( SWT.Resize, new Listener() {
      public void handleEvent( Event event ) {
        eventCaptor.set( event );
      }
    });
    Event event = new Event();
    event.button = 2;
    event.character = 'a';
    event.count = 4;
    event.data = new Object();
    event.detail = 6;
    event.display = null;
    event.doit = false;
    event.end = 8;
    event.height = 10;
    event.index = 12;
    event.item = widget;
    event.keyCode = 14;
    event.start = 16;
    event.stateMask = 18;
    event.text = "foo";
    event.type = SWT.MouseDoubleClick;
    event.widget = widget;
    event.width = 20;
    event.x = 22;
    event.y = 24;

    widget.notifyListeners( SWT.Resize, event );

    Event capturedEvent = eventCaptor.get();
    assertEquals( 2, capturedEvent.button );
    assertEquals( 'a', capturedEvent.character );
    assertEquals( 4, capturedEvent.count );
    assertNotNull( capturedEvent.data );
    assertEquals( 6, capturedEvent.detail );
    assertSame( display, capturedEvent.display );
    assertFalse( capturedEvent.doit );
    assertEquals( 8, capturedEvent.end );
    assertEquals( 10, capturedEvent.height );
    assertEquals( 12, capturedEvent.index );
    assertEquals( widget, capturedEvent.item );
    assertEquals( 14, capturedEvent.keyCode );
    assertEquals( 16, capturedEvent.start );
    assertEquals( 18, capturedEvent.stateMask );
    assertEquals( "foo", capturedEvent.text );
    assertEquals( 20, capturedEvent.width );
    assertEquals( 22, capturedEvent.x );
    assertEquals( 24, capturedEvent.y );
    assertEquals( SWT.Resize, capturedEvent.type );
    assertEquals( widget, capturedEvent.widget );
    assertTrue( capturedEvent.time > 0 );
  }

  @Test
  public void testNotifyListeners_withEmptyEvent() {
    Event event = new Event();
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.notifyListeners( SWT.Resize, event );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertSame( event, captor.getValue() );
    assertEquals( widget.getDisplay(), event.display );
    assertEquals( widget, event.widget );
    assertEquals( SWT.Resize, event.type );
    assertTrue( event.time > 0 );
  }

  @Test
  public void testNotifyListeners_withNullEvent() {
    final AtomicReference<ControlEvent> eventCaptor = new AtomicReference<ControlEvent>();
    widget.addListener( SWT.Resize, new TypedListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        eventCaptor.set( event );
      }
    } ) );

    widget.notifyListeners( SWT.Resize, null );

    assertSame( widget, eventCaptor.get().widget );
    assertSame( display, eventCaptor.get().display );
  }

  @Test
  public void testNotifyListeners_withInvalidEventType() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.notifyListeners( 4711, new Event() );

    verify( listener, never() ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testNotifyListeners_inReadDataPhase() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    Event event = new Event();
    widget.notifyListeners( SWT.Resize, event );

    verify( listener, never() ).handleEvent( any( Event.class ) );
    assertEquals( 1, EventList.getInstance().getAll().length );
    assertEquals( event, EventList.getInstance().getAll()[ 0 ] );
  }

  @Test
  public void testNotifyListeners_withNullPhase() {
    Fixture.fakePhase( null );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.notifyListeners( SWT.Resize, new Event() );

    verify( listener, never() ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testNotifyListeners_withPreInitializedTime() {
    int predefinedTime = 12345;
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    Event event = new Event();
    event.time = predefinedTime;
    widget.notifyListeners( SWT.Resize, event );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertEquals( predefinedTime, captor.getValue().time );
  }

  // bug 286039
  @Test
  public void testRemoveUntypedListenerLeavesNeighbourListenerIntact() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Move, listener );

    widget.addListener( SWT.Resize, listener );
    widget.removeListener( SWT.Resize, listener );
    widget.notifyListeners( SWT.Move, new Event() );

    verify( listener ).handleEvent( any( Event.class ) );
  }

  // bug 332511
  @Test
  public void testRemoveTypedListenerWithUntypedRemoveListener() {
    widget.addDisposeListener( mock( DisposeListener.class ) );

    Listener[] listeners = widget.getListeners( SWT.Dispose );
    for( Listener listener : listeners ) {
      widget.removeListener( SWT.Dispose, listener );
    }

    assertFalse( widget.isListening( SWT.Dispose ) );
  }

  @Test
  public void testGetListeners_initiallyEmpty() {
    Listener[] listeners = widget.getListeners( 0 );

    assertNotNull( listeners );
    assertEquals( 0, listeners.length );
  }

  @Test
  public void testGetListeners_returnsAddedListener() {
    Listener listener = mock( Listener.class );

    widget.addListener( SWT.Resize, listener );

    assertEquals( 1, widget.getListeners( SWT.Resize ).length );
    assertSame( listener, widget.getListeners( SWT.Resize )[0] );
  }

  @Test
  public void testGetListeners_returnsAllListenersForType() {
    widget.addListener( SWT.Resize, mock( Listener.class ) );
    widget.addListener( SWT.Resize, mock( Listener.class ) );

    assertEquals( 2, widget.getListeners( SWT.Resize ).length );
  }

  @Test
  public void testGetListeners_doesNotReturnListenersForOtherTypes() {
    Listener listener = mock( Listener.class );

    widget.addListener( SWT.Move, listener );

    assertEquals( 0, widget.getListeners( SWT.Resize ).length );
  }

  @Test
  public void testIsListening_falseWithoutRegisteredListeners() {
    boolean listening = widget.isListening( SWT.Dispose );

    assertFalse( listening );
  }

  @Test
  public void testIsListening_trueAfterAddListener() {
    Listener listener = mock( Listener.class );

    widget.addListener( SWT.Resize, listener );

    assertTrue( widget.isListening( SWT.Resize ) );
  }

  @Test
  public void testIsListening_falseAfterRemoveListener() {
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Resize, listener );

    widget.removeListener( SWT.Resize, listener );

    assertFalse( widget.isListening( SWT.Resize ) );
  }

  @Test
  public void testIsListening_forTypedEvent() {
    widget.addListener( SWT.Help, new TypedListener( mock( HelpListener.class ) ) );

    assertTrue( widget.isListening( SWT.Help ) );
  }

  @Test
  public void testGetDisplay() {
    assertSame( display, widget.getDisplay() );
  }

  @Test(expected = SWTException.class)
  public void testGetDisplay_failsWhenDisposed() {
    widget.dispose();

    widget.getDisplay();
  }

  @Test
  public void testGetDisplay_worksFromNonUIThread() throws Throwable {
    final AtomicReference<Display> displayCaptor = new AtomicReference<Display>();

    Fixture.runInThread( new Runnable() {
      public void run() {
        displayCaptor.set( widget.getDisplay() );
      }
    } );

    assertSame( display, displayCaptor.get() );
  }

  @Test
  public void testReskin() {
    final java.util.List<Widget> log = new ArrayList<Widget>();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        if( event.type == SWT.Skin ) {
          log.add( event.widget );
        }
      }
    };
    widget.dispose();
    display.addListener( SWT.Skin, listener );
    Composite child1 = new Composite( shell, SWT.NONE );
    Label subchild1 = new Label( child1, SWT.NONE );
    Composite child2 = new Composite( shell, SWT.NONE );
    Label subchild2 = new Label( child2, SWT.NONE );
    Composite child3 = new Composite( shell, SWT.NONE );
    Label subchild3 = new Label( child3, SWT.NONE );
    shell.reskin( SWT.ALL );
    display.readAndDispatch();
    assertEquals( 7, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( child1, log.get( 1 ) );
    assertSame( subchild1, log.get( 2 ) );
    assertSame( child2, log.get( 3 ) );
    assertSame( subchild2, log.get( 4 ) );
    assertSame( child3, log.get( 5 ) );
    assertSame( subchild3, log.get( 6 ) );
    log.clear();
    shell.setData( SWT.SKIN_CLASS, "skin" );
    display.readAndDispatch();
    assertEquals( 7, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( child1, log.get( 1 ) );
    assertSame( subchild1, log.get( 2 ) );
    assertSame( child2, log.get( 3 ) );
    assertSame( subchild2, log.get( 4 ) );
    assertSame( child3, log.get( 5 ) );
    assertSame( subchild3, log.get( 6 ) );
    log.clear();
    shell.setData( SWT.SKIN_ID, "skin" );
    display.readAndDispatch();
    assertEquals( 7, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( child1, log.get( 1 ) );
    assertSame( subchild1, log.get( 2 ) );
    assertSame( child2, log.get( 3 ) );
    assertSame( subchild2, log.get( 4 ) );
    assertSame( child3, log.get( 5 ) );
    assertSame( subchild3, log.get( 6 ) );
    log.clear();
    child3.reskin( SWT.ALL );
    display.readAndDispatch();
    assertEquals( 2, log.size() );
    assertSame( child3, log.get( 0 ) );
    assertSame( subchild3, log.get( 1 ) );
    log.clear();
    child2.reskin( SWT.NONE );
    display.readAndDispatch();
    assertEquals( 1, log.size() );
    assertSame( child2, log.get( 0 ) );
    log.clear();
    display.removeListener( SWT.Skin, listener );
    shell.reskin( SWT.ALL );
    display.readAndDispatch();
    assertEquals( 0, log.size() );
  }

}
