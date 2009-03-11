/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;



public class Widget_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetAdapter() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Widget shell = new Shell( display );
    // ensure that Widget#getAdapter can be called after widget was disposed of
    shell.dispose();
    assertNotNull( shell.getAdapter( IWidgetAdapter.class ) );
  }

  public void testCheckWidget() throws InterruptedException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Widget widget = new Text( shell, SWT.NONE );

    final Throwable[] throwable = new Throwable[ 1 ];
    final String[] message = new String[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          widget.checkWidget();
          fail( "Illegal thread access expected." );
        } catch( final SWTException swte ) {
          message[ 0 ] = swte.getMessage();
        } catch( final Throwable thr ) {
          throwable[ 0 ] = thr;
        }
      }
    });
    thread.start();
    thread.join();
    assertEquals( message[ 0 ], "Invalid thread access" );
    assertNull( throwable[ 0 ] );
  }

  public void testData() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new Text( shell, SWT.NONE );

    // Test initial state
    assertEquals( null, widget.getData() );

    Object singleData = new Object();
    // Set/get some single data
    widget.setData( singleData );
    assertSame( singleData, widget.getData() );

    // Set/get some keyed data, ensure that single data remains unchanged
    Object keyedData = new Object();
    widget.setData( "key", keyedData );
    widget.setData( "null-key", null );
    assertSame( singleData, widget.getData() );
    assertSame( keyedData, widget.getData( "key" ) );
    assertSame( null, widget.getData( "null-key" ) );

    // Test 'deleting' a key
    widget.setData( "key", null );
    assertNull( widget.getData( "key" ) );

    // Test keyed data with non-existing key
    assertNull( widget.getData( "non-existing-key" ) );

    // Test keyed data with illegal arguments
    try {
      widget.setData( null, new Object() );
      fail( "Must not allow to set data with null key" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      widget.getData( null );
      fail( "Must not allow to get data for null key" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testCheckBits() {
    int style = SWT.VERTICAL | SWT.HORIZONTAL;
    int result = Widget.checkBits( style,
                                   SWT.VERTICAL,
                                   SWT.HORIZONTAL,
                                   0,
                                   0,
                                   0,
                                   0 );
    assertTrue( ( result & SWT.VERTICAL ) != 0 );
    assertFalse( ( result & SWT.HORIZONTAL ) != 0 );
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new Button( shell, SWT.NONE );

    // Ensure initial state
    assertEquals( false, widget.isDisposed() );

    // Test dispose the first time
    widget.dispose();
    assertEquals( true, widget.isDisposed() );

    // Disposing of an already disposed of widget does nothing
    widget.dispose();
    assertEquals( true, widget.isDisposed() );
  }

  public void testDisposeFromIllegalThread() throws InterruptedException {
    Display display = new Display();
    Shell shell = new Shell( display );
    final Widget widget = new Button( shell, SWT.NONE );

    final AssertionFailedError[] failure = new AssertionFailedError[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          widget.dispose();
          fail( "Must not allow to dispose of a widget from a non-UI-thread" );
        } catch( SWTException e ) {
          // expected
        } catch( final AssertionFailedError afa ) {
          failure[ 0 ] = afa;
        }
      }
    } );
    thread.start();
    thread.join();

    if( failure[ 0 ] != null ) {
      throw failure[ 0 ];
    }
  }

  public void testDisposeWithException() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Widget widget = new Shell( display );
    widget.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        throw new RuntimeException();
      }
    } );
    try {
      widget.dispose();
      fail( "Wrong test setup: dispose listener must throw exception" );
    } catch( Exception e ) {
      // expected
    }
    assertFalse( widget.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );
  }

  public void testRemoveListener() {
    // Ensure that removing a listener that was never added is ignored
    // silently see https://bugs.eclipse.org/251816
    Display display = new Display();
    Widget widget = new Shell( display );
    widget.removeListener( SWT.Activate, new Listener() {
      public void handleEvent( final Event event ) {
      }
    } );
  }

  public void testNotifyListeners() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Widget widget = new Shell( display );
    final StringBuffer log = new StringBuffer();
    widget.addListener( SWT.Resize, new Listener() {
      public void handleEvent( final Event event ) {
        log.append( "untyped" );
      }
    } );
    widget.notifyListeners( SWT.Resize, new Event() );
    assertEquals( "untyped", log.toString() );
  }

  public void testNotifyListenersTyped() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    final StringBuffer log = new StringBuffer();
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
        log.append( "typed" );
      }
    } );
    shell.notifyListeners( SWT.Resize, new Event() );
    assertEquals( "typed", log.toString() );
  }

  public void testNotifyListenersDisplayFilter() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    final StringBuffer log = new StringBuffer();
    display.addFilter( SWT.Resize, new Listener() {
      public void handleEvent( final Event event ) {
        log.append( "filter" );
      }
    });
    shell.notifyListeners( SWT.Resize, new Event() );
    assertEquals( "filter", log.toString() );
  }

  // SWT always overrides e.type, e.display and e.widget
  public void testNotifyListenersEventFields() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Display display = new Display();
    final Shell shell = new Shell( display );
    final StringBuffer log = new StringBuffer();
    display.addFilter( SWT.Resize, new Listener() {
      public void handleEvent( final Event event ) {
        assertEquals( SWT.Resize, event.type );
        assertEquals( shell, event.widget );
        log.append( "filter" );
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
    event.item = shell;
    event.keyCode = 14;
    event.start = 16;
    event.stateMask = 18;
    event.text = "foo";
    event.type = SWT.MouseDoubleClick;
    event.widget = shell;
    event.width = 20;
    event.x = 22;
    event.y = 24;

    shell.notifyListeners( SWT.Resize, event );
    assertEquals( "filter", log.toString() );
  }

  public void testNotifyListenersSetData() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Display display = new Display();
    final Widget widget = new Shell( display );
    final StringBuffer log = new StringBuffer();
    widget.addListener( SWT.SetData, new Listener(){
      public void handleEvent( final Event event ) {
        assertSame( widget, event.widget );
        assertSame( widget, event.item );
        assertEquals( 3, event.index );
        assertSame( display, event.display );
        log.append( "setdata" );
      }
    });
    Event event = new Event();
    event.item = widget;
    event.index = 3;
    widget.notifyListeners( SWT.SetData, event );
    assertEquals( "setdata", log.toString() );
  }

  public void testNotifyListenersNullEvent() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Display display = new Display();
    final Control control = new Shell( display );
    final StringBuffer log = new StringBuffer();
    control.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        assertSame( control, event.widget );
        assertSame( display, event.display );
        log.append( "typed" );
      }
    } );
    control.notifyListeners( SWT.Resize, null );
    assertEquals( "typed", log.toString() );
  }
  
  public void testNotifyListenersInvalidEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Display display = new Display();
    final Widget widget = new Shell( display );
    widget.notifyListeners( 4711, new Event() );
    // no assertion: this test ensures that invalid event types are silently 
    // ignored 
  }
}
