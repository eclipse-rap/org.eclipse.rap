/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;


public class Widget_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetAdapter() {
    shell.dispose();
    Object adapterOfDisposedWidget = shell.getAdapter( IWidgetAdapter.class );
    assertNotNull( adapterOfDisposedWidget );
  }
  
  public void testCheckWidget() throws Throwable {
    final Widget widget = new Text( shell, SWT.NONE );
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
  
  public void testData() {
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
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDisposeParentWhileInDispose() {
    // This test leads to a stack overflow or, if line "item[ 0 ].dispose();"
    // is activated to a NPE
    final Composite composite = new Composite( shell, SWT.NONE );
    ToolBar toolbar = new ToolBar( composite, SWT.NONE );
    final ToolItem[] item = { null };
    toolbar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        item[ 0 ].dispose();
      }
    } );
    toolbar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        composite.dispose();
      }
    } );
    item[ 0 ] = new ToolItem( toolbar, SWT.PUSH );
    shell.dispose();
    // no assert: this test ensures that no StackOverflowError occurs
  }

  public void testDisposeSelfWhileInDispose() {
    shell.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        shell.dispose();
      }
    } );
    shell.dispose();
    // no assert: this test ensures that no exception occurs
  }

  public void testDisposeSelfWhileInDispose_RenderOnce() {
    Fixture.markInitialized( shell );
    shell.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        shell.dispose();
      }
    } );
    shell.dispose();
    int counter = 0;
    Widget[] disposedWidgets = DisposedWidgets.getAll();
    for( int i = 0; i < disposedWidgets.length; i++ ) {
      if( disposedWidgets[ i ] == shell ) {
        counter++;
      }
    }
    assertEquals( 1, counter );
  }

  public void testCheckBits() {
    int style = SWT.VERTICAL | SWT.HORIZONTAL;
    int result = Widget.checkBits( style, SWT.VERTICAL, SWT.HORIZONTAL, 0, 0, 0, 0 );
    assertTrue( ( result & SWT.VERTICAL ) != 0 );
    assertFalse( ( result & SWT.HORIZONTAL ) != 0 );
  }

  public void testDispose() {
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

  public void testDisposeFromIllegalThread() throws Throwable {
    final Widget widget = new Button( shell, SWT.NONE );
    Runnable runnable = new Runnable() {
      public void run() {
        widget.dispose();
      }
    };
    try {
      Fixture.runInThread( runnable );
      fail( "Must not allow to dispose of a widget from a non-UI-thread" );
    } catch( SWTException expected ) {
    }
  }
  
  public void testDisposeWithException() {
    shell.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        throw new RuntimeException();
      }
    } );
    try {
      shell.dispose();
      fail( "Wrong test setup: dispose listener must throw exception" );
    } catch( Exception e ) {
      // expected
    }
    assertFalse( shell.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );
  }

  public void testRemoveListener() {
    // Ensure that removing a listener that was never added is ignored
    // silently see https://bugs.eclipse.org/251816
    shell.removeListener( SWT.Activate, new Listener() {
      public void handleEvent( final Event event ) {
      }
    } );
  }

  public void testNotifyListeners() {
    final StringBuffer log = new StringBuffer();
    shell.addListener( SWT.Resize, new Listener() {
      public void handleEvent( final Event event ) {
        log.append( "untyped" );
      }
    } );
    shell.notifyListeners( SWT.Resize, new Event() );
    assertEquals( "untyped", log.toString() );
  }

  public void testNotifyListenersTyped() {
    final StringBuffer log = new StringBuffer();
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
        log.append( "typed" );
      }
    } );
    shell.notifyListeners( SWT.Resize, new Event() );
    assertEquals( "typed", log.toString() );
  }

  public void testNotifyListenersDisplayFilter() {
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
  public void testNotifyListenersEventFields() {
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

  public void testNotifyListenersSetData() {
    final StringBuffer log = new StringBuffer();
    shell.addListener( SWT.SetData, new Listener(){
      public void handleEvent( final Event event ) {
        assertSame( shell, event.widget );
        assertSame( shell, event.item );
        assertEquals( 3, event.index );
        assertSame( display, event.display );
        log.append( "setdata" );
      }
    });
    Event event = new Event();
    event.item = shell;
    event.index = 3;
    shell.notifyListeners( SWT.SetData, event );
    assertEquals( "setdata", log.toString() );
  }

  public void testNotifyListenersNullEvent() {
    final StringBuffer log = new StringBuffer();
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        assertSame( shell, event.widget );
        assertSame( display, event.display );
        log.append( "typed" );
      }
    } );
    shell.notifyListeners( SWT.Resize, null );
    assertEquals( "typed", log.toString() );
  }

  public void testNotifyListenersInvalidEvent() {
    shell.notifyListeners( 4711, new Event() );
    // no assertion: this test ensures that invalid event types are silently
    // ignored
  }

  public void testGetListeners() {
    Listener[] listeners = shell.getListeners( 0 );
    assertNotNull( listeners );
    assertEquals( 0, listeners.length );
    Listener dummyListener = new Listener() {
      public void handleEvent( final Event event ) {
      }
    };
    Listener dummyListener2 = new Listener() {
      public void handleEvent( final Event event ) {
      }
    };
    shell.addListener( SWT.Resize, dummyListener );
    assertEquals( 0, shell.getListeners( SWT.Move ).length );
    assertEquals( 1, shell.getListeners( SWT.Resize ).length );
    assertSame( dummyListener, shell.getListeners( SWT.Resize )[0] );
    shell.addListener( SWT.Resize, dummyListener2 );
    assertEquals( 2, shell.getListeners( SWT.Resize ).length );
  }

  public void testIsListening() {
    final Listener dummyListener = new Listener() {
      public void handleEvent( final Event event ) {
      }
    };
    assertFalse( shell.isListening( SWT.Resize ) );
    shell.addListener( SWT.Resize, dummyListener );
    assertTrue( shell.isListening( SWT.Resize ) );
    shell.removeListener( SWT.Resize, dummyListener );
    assertFalse( shell.isListening( SWT.Resize ) );
  }

  public void testIsListeningForTypedEvent() {
    shell.addHelpListener( new HelpListener() {
      public void helpRequested( final HelpEvent event ) {
      }
    } );
    assertTrue( shell.isListening( SWT.Help ) );
  }
  
  public void testGetDisplay() {
    assertSame( display, shell.getDisplay() );
  }

  public void testGetDisplayFromNonUIThread() throws Exception {
    final Display[] widgetDisplay = { null };
    Thread thread = new Thread( new Runnable() {
      public void run() {
        widgetDisplay[ 0 ] = shell.getDisplay();
      }
    } );
    thread.start();
    thread.join();
    assertSame( display, widgetDisplay[ 0 ] );
  }
  
  public void testReskin() {
    final java.util.List log = new ArrayList();
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        if( event.type == SWT.Skin ) {
          log.add( event.widget );
        }
      }
    };
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
