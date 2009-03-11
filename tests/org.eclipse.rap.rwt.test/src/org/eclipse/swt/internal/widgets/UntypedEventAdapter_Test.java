/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.events.SetDataEvent;
import org.eclipse.swt.widgets.*;

public class UntypedEventAdapter_Test extends TestCase {

  private static final String EVENT_FIRED = "fired|";
  private static int eventType;
  private static String log;
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    eventType = 0;
    log = "";
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testListenerTypes() throws Exception {
    Display display = new Display();
    Control widget = new Shell( display );
    UntypedEventAdapter adapter = new UntypedEventAdapter();
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        eventType = event.type;
      }
    };
    adapter.addListener( SWT.Move, listener );
    adapter.controlMoved( new ControlEvent( widget, 0 ) );
    assertEquals( SWT.Move, eventType );
    adapter.addListener( SWT.Resize, listener );
    adapter.controlResized( new ControlEvent( widget, 0 ) );
    assertEquals( SWT.Resize, eventType );
    adapter.addListener( SWT.Dispose, listener );
    adapter.widgetDisposed( new DisposeEvent( widget ) );
    assertEquals( SWT.Dispose, eventType );
    adapter.addListener( SWT.Selection, listener );
    adapter.widgetSelected( new SelectionEvent( widget, null, 0 ) );
    assertEquals( SWT.Selection, eventType );
    adapter.addListener( SWT.DefaultSelection, listener );
    adapter.widgetDefaultSelected( new SelectionEvent( widget, null, 0 ) );
    assertEquals( SWT.DefaultSelection, eventType );
    adapter.addListener( SWT.FocusIn, listener );
    adapter.focusGained( new FocusEvent( widget, FocusEvent.FOCUS_GAINED ) );
    assertEquals( SWT.FocusIn, eventType );
    adapter.addListener( SWT.FocusOut, listener );
    adapter.focusLost( new FocusEvent( widget, FocusEvent.FOCUS_LOST ) );
    assertEquals( SWT.FocusOut, eventType );
    adapter.addListener( SWT.Expand, listener );
    adapter.treeExpanded( new TreeEvent( widget, null, 0 ) );
    assertEquals( SWT.Expand, eventType );
    adapter.addListener( SWT.Collapse, listener );
    adapter.treeCollapsed( new TreeEvent( widget, null, 0 ) );
    assertEquals( SWT.Collapse, eventType );
    adapter.addListener( SWT.Activate, listener );
    adapter.shellActivated( new ShellEvent( widget, 0 ) );
    assertEquals( SWT.Activate, eventType );
    adapter.addListener( SWT.Deactivate, listener );
    adapter.shellDeactivated( new ShellEvent( widget, 0 ) );
    assertEquals( SWT.Deactivate, eventType );
    adapter.addListener( SWT.Close, listener );
    adapter.shellClosed( new ShellEvent( widget, 0 ) );
    assertEquals( SWT.Close, eventType );
    adapter.addListener( SWT.Hide, listener );
    adapter.menuHidden( new MenuEvent( widget, 0 ) );
    assertEquals( SWT.Hide, eventType );
    adapter.addListener( SWT.Show, listener );
    adapter.menuShown( new MenuEvent( widget, 0 ) );
    assertEquals( SWT.Show, eventType );
    adapter.addListener( SWT.Modify, listener );
    adapter.modifyText( new ModifyEvent( widget ) );
    assertEquals( SWT.Modify, eventType );
    adapter.addListener( SWT.SetData, listener );
    adapter.update( new SetDataEvent( widget, null, 0 ) );
    assertEquals( SWT.SetData, eventType );
    adapter.addListener( SWT.MouseDown, listener );
    adapter.mouseDown( new MouseEvent( widget, 0 ) );
    assertEquals( SWT.MouseDown, eventType );
    adapter.addListener( SWT.MouseUp, listener );
    adapter.mouseUp( new MouseEvent( widget, 0 ) );
    assertEquals( SWT.MouseUp, eventType );
    adapter.addListener( SWT.MouseDoubleClick, listener );
    adapter.mouseDoubleClick( new MouseEvent( widget, 0 ) );
    assertEquals( SWT.MouseDoubleClick, eventType );
    adapter.addListener( SWT.KeyDown, listener );
    adapter.keyPressed( new KeyEvent( widget, 0 ) );
    assertEquals( SWT.KeyDown, eventType );
  }

  public void testAdditionAndRemovalOfListener() throws Exception {
    Display display = new Display();
    Control widget = new Shell( display );
    UntypedEventAdapter adapter = new UntypedEventAdapter();
    final Set eventBuffer = new HashSet();
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        eventType = event.type;
        log += EVENT_FIRED;
        eventBuffer.add(  event );
      }
    };
    adapter.addListener( SWT.Move, listener );
    adapter.addListener( SWT.Move, listener );
    adapter.controlMoved( new ControlEvent( widget, 0 ) );
    assertEquals( SWT.Move, eventType );
    assertEquals( EVENT_FIRED + EVENT_FIRED, log );
    assertEquals( 1, eventBuffer.size() );
    
    log = "";
    eventType = 0;
    eventBuffer.clear();
    adapter.removeListener( SWT.Move, listener );
    adapter.controlMoved( new ControlEvent( widget, 0 ) );
    assertEquals( SWT.Move, eventType );
    assertEquals( EVENT_FIRED, log );
    assertEquals( 1, eventBuffer.size() );

    log = "";
    eventType = 0;
    eventBuffer.clear();
    adapter.removeListener( SWT.Move, listener );
    adapter.controlMoved( new ControlEvent( widget, 0 ) );
    assertEquals( 0, eventType );
    assertEquals( "", log );
    assertEquals( 0, eventBuffer.size() );
  }
  
  public void testExecutionOrder() throws Exception {
    Display display = new Display();
    Control widget = new Shell( display );
    UntypedEventAdapter adapter = new UntypedEventAdapter();
    Listener listener1 = new Listener() {
      public void handleEvent( final Event event ) {
        log += "L1|";
      }
    };
    Listener listener2 = new Listener() {
      public void handleEvent( final Event event ) {
        log += "L2|";
      }
    };
    adapter.addListener( SWT.Move, listener1 );
    adapter.addListener( SWT.Move, listener2 );
    adapter.controlMoved( new ControlEvent( widget, 0 ) );
    assertEquals( "L1|L2|", log );
  }
  
  public void testEventFields() {
    final Event[] eventLog = { null };
    final Display display = new Display();
    final Shell shell = new Shell( display );
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog[ 0 ] = event;
      }
    };
    // Move event
    UntypedEventAdapter adapter = new UntypedEventAdapter();
    adapter.addListener( SWT.Move, listener );
    ControlEvent event = new ControlEvent( shell, ControlEvent.CONTROL_MOVED );
    adapter.controlMoved( event );
    assertNotNull( eventLog[ 0 ] );
    assertSame( display, eventLog [ 0 ].display );
    assertSame( shell, eventLog [ 0 ].widget );
    // Selection event
    adapter = new UntypedEventAdapter();
    adapter.addListener( SWT.Selection, listener );
    SelectionEvent selEvent
      = new SelectionEvent( shell, null, SelectionEvent.WIDGET_SELECTED );
    selEvent.x = 1;
    selEvent.y = 2;
    selEvent.width = 3;
    selEvent.height = 4;
    selEvent.text = "some text";
    selEvent.detail = 123;
    adapter.widgetSelected( selEvent );
    assertEquals( selEvent.text, eventLog[ 0 ].text );
    assertEquals( selEvent.x, eventLog[ 0 ].x );
    assertEquals( selEvent.y, eventLog[ 0 ].y );
    assertEquals( selEvent.height, eventLog[ 0 ].height );
    assertEquals( selEvent.width, eventLog[ 0 ].width );
    assertEquals( selEvent.detail, eventLog[ 0 ].detail );
    // Key event
    adapter = new UntypedEventAdapter();
    adapter.addListener( SWT.KeyDown, listener );
    KeyEvent keyEvent = new KeyEvent( shell, KeyEvent.KEY_PRESSED );
    keyEvent.character = 'x';
    keyEvent.keyCode = 123;
    keyEvent.stateMask = 321;
    keyEvent.data = new Object();
    keyEvent.doit = false;
    adapter.keyPressed( keyEvent );
    assertEquals( keyEvent.character, eventLog[ 0 ].character );
    assertEquals( keyEvent.keyCode, eventLog[ 0 ].keyCode );
    assertEquals( keyEvent.stateMask, eventLog[ 0 ].stateMask );
    assertEquals( keyEvent.data, eventLog[ 0 ].data );
    assertEquals( keyEvent.doit, eventLog[ 0 ].doit );
  }
  
  public void testInvalidEventType() {
    final Event[] eventLog = { null };
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog[ 0 ] = event;
      }
    };
    Display display = new Display();
    Widget widget = new Shell( display );
    // Ensure that adding an unknown/invalid event type is silently ignored
    widget.addListener( 505, listener );
    assertNull( eventLog[ 0 ] );
    widget.removeListener( 505, listener );
  }
}
