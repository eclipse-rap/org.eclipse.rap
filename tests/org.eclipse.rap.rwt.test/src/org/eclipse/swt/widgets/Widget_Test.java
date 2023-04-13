/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.events.EventList;
import org.eclipse.swt.internal.widgets.WidgetRemoteAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


@SuppressWarnings( "deprecation" )
public class Widget_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Widget widget;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    widget = new Widget( shell, SWT.NONE ) {};
  }

  @Test
  public void testGetAdapter_forRemoteAdapter() {
    Object adapter = widget.getAdapter( RemoteAdapter.class );

    assertTrue( adapter instanceof RemoteAdapter );
  }

  @Test
  public void testGetAdapter_forRemoteAdapter_returnsSameInstance() {
    Object adapter1 = widget.getAdapter( RemoteAdapter.class );
    Object adapter2 = widget.getAdapter( RemoteAdapter.class );

    assertSame( adapter1, adapter2 );
  }

  @Test
  public void testGetAdapter_forRemoteAdapter_returnsDifferentInstances() {
    Object adapter1 = widget.getAdapter( RemoteAdapter.class );
    Widget anotherWidget = new Widget( shell, SWT.NONE ) {};

    Object adapter2 = anotherWidget.getAdapter( RemoteAdapter.class );

    assertNotSame( adapter1, adapter2 );
  }

  @Test
  public void testGetAdapter_succeedsForDisposedWidget() {
    widget.dispose();

    Object adapter = widget.getAdapter( RemoteAdapter.class );

    assertNotNull( adapter );
  }

  @Test
  public void testSetsParentOnAdapter() {
    RemoteAdapter adapter = widget.getAdapter( RemoteAdapter.class );

    assertSame( shell, adapter.getParent() );
  }

  @Test
  public void testCheckWidget() throws Throwable {
    Runnable target = new Runnable() {
      @Override
      public void run() {
        widget.checkWidget();
      }
    };
    try {
      runInThread( target );
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
  public void testCheckOrientation_initial() {
    widget = new Label( shell, SWT.NONE );

    assertTrue( ( widget.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertFalse( ( widget.getStyle() & SWT.RIGHT_TO_LEFT ) != 0 );
  }

  @Test
  public void testCheckOrientation_withParentOrientation_LTR() {
    shell = new Shell( display, SWT.LEFT_TO_RIGHT );
    widget = new Label( shell, SWT.NONE );

    assertTrue( ( widget.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertFalse( ( widget.getStyle() & SWT.RIGHT_TO_LEFT ) != 0 );
  }

  @Test
  public void testCheckOrientation_withParentOrientation_RTL() {
    shell = new Shell( display, SWT.RIGHT_TO_LEFT );
    widget = new Label( shell, SWT.NONE );

    assertFalse( ( widget.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertTrue( ( widget.getStyle() & SWT.RIGHT_TO_LEFT ) != 0 );
  }

  @Test
  public void testCheckOrientation_LTR() {
    widget = new Label( shell, SWT.LEFT_TO_RIGHT );

    assertTrue( ( widget.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertFalse( ( widget.getStyle() & SWT.RIGHT_TO_LEFT ) != 0 );
  }

  @Test
  public void testCheckOrientation_RTL() {
    widget = new Label( shell, SWT.RIGHT_TO_LEFT );

    assertFalse( ( widget.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertTrue( ( widget.getStyle() & SWT.RIGHT_TO_LEFT ) != 0 );
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
  public void testSetData_forVariant() {
    widget.setData( RWT.CUSTOM_VARIANT, "foo" );

    assertEquals( "foo", widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test
  public void testSetData_forVariant_canBeReset() {
    widget.setData( RWT.CUSTOM_VARIANT, "foo" );

    widget.setData( RWT.CUSTOM_VARIANT, null );

    assertNull( widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test
  public void testSetData_forVariant_acceptsUnderscore() {
    widget.setData( RWT.CUSTOM_VARIANT, "Foo_Bar_23_42" );

    assertNotNull( widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test
  public void testSetData_forVariant_acceptsDash() {
    widget.setData( RWT.CUSTOM_VARIANT, "Foo-Bar-23-42" );

    assertNotNull( widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test
  public void testSetData_forVariant_acceptsLeadingDash() {
    widget.setData( RWT.CUSTOM_VARIANT, "-Foo-Bar-23-42" );

    assertNotNull( widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test
  public void testSetData_forVariant_acceptsNonAscii() {
    widget.setData( RWT.CUSTOM_VARIANT, "Foo-üäöæ-23-42" );

    assertNotNull( widget.getData( RWT.CUSTOM_VARIANT ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_forVariant_rejectsNonStringValue() {
    widget.setData( RWT.CUSTOM_VARIANT, new Object() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_forVariant_rejectsEmptyString() {
    widget.setData( RWT.CUSTOM_VARIANT, "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_forVariant_rejectsSpaces() {
    widget.setData( RWT.CUSTOM_VARIANT, "Foo Bar 23 42 " );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_forVariant_rejectsColon() {
    widget.setData( RWT.CUSTOM_VARIANT, "Foo:Bar" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_forVariant_rejectsLeadingNumber() {
    widget.setData( RWT.CUSTOM_VARIANT, "1-Foo-Bar" );
  }

  @Test
  public void testSetData_forVariant_preservesVariant() {
    widget.setData( RWT.CUSTOM_VARIANT, "foo" );

    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )widget.getAdapter( RemoteAdapter.class );
    assertTrue( adapter.hasPreservedVariant() );
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
    runInThread( new Runnable() {
      @Override
      public void run() {
        widget.dispose();
      }
    } );
  }

  @Test
  public void testDispose_withException() {
    widget.addListener( SWT.Dispose, new Listener() {
      @Override
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
    assertEquals( 0, DisposedWidgets.getAll().size() );
  }

  @Test
  public void testDisposeParentWhileInDispose() {
    // This test leads to a stack overflow or, if line "item[ 0 ].dispose();"
    // is activated to a NPE
    final Composite composite = new Composite( shell, SWT.NONE );
    ToolBar toolbar = new ToolBar( composite, SWT.NONE );
    final ToolItem[] item = { null };
    toolbar.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        item[ 0 ].dispose();
      }
    } );
    toolbar.addDisposeListener( new DisposeListener() {
      @Override
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
      @Override
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
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        widget.dispose();
      }
    } );
    widget.dispose();
    int counter = 0;
    for( Widget disposedWidget : DisposedWidgets.getAll() ) {
      if( disposedWidget == widget ) {
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
  public void testAddListener_preservesListeners() {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )widget.getAdapter( RemoteAdapter.class );
    adapter.clearPreserved();

    widget.addListener( SWT.Selection, mock( Listener.class ) );

    assertTrue( adapter.hasPreservedListeners() );
    assertEquals( 0, adapter.getPreservedListeners(), SWT.Selection );
  }

  @Test
  public void testRemoveListener_preservesListeners() {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )widget.getAdapter( RemoteAdapter.class );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Selection, listener );
    adapter.clearPreserved();

    widget.removeListener( SWT.Selection, listener );

    assertTrue( adapter.hasPreservedListeners() );
    assertTrue( EventLCAUtil.containsEvent( adapter.getPreservedListeners(), SWT.Selection ) );
  }

  @Test
  public void testRemoveListener_typed_preservesListeners() {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )widget.getAdapter( RemoteAdapter.class );
    SWTEventListener listener = mock( SWTEventListener.class );
    widget.addListener( SWT.Selection, new TypedListener( listener ) );
    adapter.clearPreserved();

    widget.removeListener( SWT.Selection, listener );

    assertTrue( adapter.hasPreservedListeners() );
    assertTrue( EventLCAUtil.containsEvent( adapter.getPreservedListeners(), SWT.Selection ) );
  }

  @Test
  public void testRemoveDisposeListener_typed_preservesListeners() {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )widget.getAdapter( RemoteAdapter.class );
    DisposeListener listener = mock( DisposeListener.class );
    widget.addDisposeListener( listener );
    adapter.clearPreserved();

    widget.removeDisposeListener( listener );

    assertTrue( adapter.hasPreservedListeners() );
    assertTrue( EventLCAUtil.containsEvent( adapter.getPreservedListeners(), SWT.Dispose ) );
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
      @Override
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
      @Override
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

    runInThread( new Runnable() {
      @Override
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
      @Override
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

  @Test
  public void testAddState() {
    widget.addState( 1 << 2 );

    assertFalse( widget.hasState( 1 << 0 ) );
    assertFalse( widget.hasState( 1 << 1 ) );
    assertTrue( widget.hasState( 1 << 2 ) );
    assertFalse( widget.hasState( 1 << 3 ) );
  }

  @Test
  public void testRemoveState() {
    widget.addState( 1 << 23 );
    widget.removeState( 1 << 23 );

    assertFalse( widget.hasState( 1 << 0 ) );
    assertFalse( widget.hasState( 1 << 1 ) );
    assertFalse( widget.hasState( 1 << 2 ) );
    assertFalse( widget.hasState( 1 << 3 ) );
  }

}
