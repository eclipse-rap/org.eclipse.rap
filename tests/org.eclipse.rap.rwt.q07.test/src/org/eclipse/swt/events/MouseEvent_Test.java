/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.*;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


public class MouseEvent_Test extends TestCase {

  private static class LoggingListener implements Listener {
    private final List events;
    private LoggingListener( List events ) {
      this.events = events;
    }
    public void handleEvent( Event event ) {
      events.add( event );
    }
  }

  private static class LoggingMouseListener implements MouseListener {
    private final List events;
    private LoggingMouseListener( List events ) {
      this.events = events;
    }
    public void mouseDoubleClick( final MouseEvent event ) {
      events.add( event );
    }
    public void mouseDown( final MouseEvent event ) {
      events.add( event );
    }
    public void mouseUp( final MouseEvent event ) {
      events.add( event );
    }
  }

  private Display display;
  private Shell shell;
  private List events;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    events = new LinkedList();
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    Button button = new Button( shell, SWT.PUSH );
    button.addMouseListener( new LoggingMouseListener( events ) );
    Object data = new Object();
    Event event = new Event();
    event.data = data;
    event.button = 2;
    event.x = 10;
    event.y = 20;
    event.stateMask = 23;
    event.time = 4711;
    button.notifyListeners( SWT.MouseDown, event );
    MouseEvent mouseEvent = ( MouseEvent )events.get( 0 );
    assertSame( button, mouseEvent.getSource() );
    assertSame( button, mouseEvent.widget );
    assertSame( display, mouseEvent.display );
    assertSame( data, mouseEvent.data );
    assertEquals( 10, mouseEvent.x );
    assertEquals( 20, mouseEvent.y );
    assertEquals( 2, mouseEvent.button );
    assertEquals( 23, mouseEvent.stateMask );
    assertEquals( 4711, mouseEvent.time );
    assertEquals( SWT.MouseDown, mouseEvent.getID() );
  }

  public void testAddRemoveListener() {
    MouseListener listener = new LoggingMouseListener( events );
    MouseEvent.addListener( shell, listener );
    MouseEvent downEvent = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    downEvent.processEvent();
    MouseEvent upEvent = new MouseEvent( shell, MouseEvent.MOUSE_UP );
    upEvent.processEvent();
    MouseEvent doubleClickEvent = new MouseEvent( shell, MouseEvent.MOUSE_DOUBLE_CLICK );
    doubleClickEvent.processEvent();
    assertSame( downEvent, events.get( 0 ) );
    assertSame( upEvent, events.get( 1 ) );
    assertSame( doubleClickEvent, events.get( 2 ) );
  }
  
  public void testRemoveListener() {
    MouseListener listener = new LoggingMouseListener( events );
    MouseEvent.addListener( shell, listener );
    MouseEvent.removeListener( shell, listener );
    MouseEvent event = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    event.processEvent();
    assertEquals( 0, events.size() );
  }

  public void testAddRemoveUntypedListener() {
    Listener listener = new LoggingListener( events );
    // MouseDown
    shell.addListener( SWT.MouseDown, listener );
    MouseEvent event;
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    event.processEvent();
    Event firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseDown, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseDown, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    event.processEvent();
    assertEquals( 0, events.size() );
    // MouseUp
    shell.addListener( SWT.MouseUp, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_UP );
    event.processEvent();
    firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseUp, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseUp, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_UP );
    event.processEvent();
    assertEquals( 0, events.size() );
    // MouseDoubleCLick
    shell.addListener( SWT.MouseDoubleClick, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOUBLE_CLICK );
    event.processEvent();
    firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseDoubleClick, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseDoubleClick, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOUBLE_CLICK );
    event.processEvent();
    assertEquals( 0, events.size() );
  }

  public void testTypedMouseEventOrderWithClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseUpRequest( shell, shellX + 30, shellY + 30 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, events.size() );
    MouseEvent mouseDown = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( MouseEvent.MOUSE_DOWN, mouseDown.getID() );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 28, mouseDown.x );
    assertEquals( 28, mouseDown.y );
    MouseEvent mouseUp = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( MouseEvent.MOUSE_UP, mouseUp.getID() );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 28, mouseUp.x );
    assertEquals( 28, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
  }
  
  public void testTypedMouseEventOrderWithDoubleClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseUpRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseDoubleClickRequest( shell, shellX + 30, shellY + 30 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    MouseEvent mouseDown = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( MouseEvent.MOUSE_DOWN, mouseDown.getID() );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 28, mouseDown.x );
    assertEquals( 28, mouseDown.y );
    assertTrue( ( mouseDown.stateMask & SWT.BUTTON1 ) != 0 );
    MouseEvent mouseDoubleClick = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( MouseEvent.MOUSE_DOUBLE_CLICK, mouseDoubleClick.getID() );
    assertSame( shell, mouseDoubleClick.widget );
    assertEquals( 1, mouseDoubleClick.button );
    assertEquals( 28, mouseDoubleClick.x );
    assertEquals( 28, mouseDoubleClick.y );
    assertTrue( ( mouseDoubleClick.stateMask & SWT.BUTTON1 ) != 0 );
    MouseEvent mouseUp = ( ( MouseEvent )events.get( 2 ) );
    assertEquals( MouseEvent.MOUSE_UP, mouseUp.getID() );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 28, mouseUp.x );
    assertEquals( 28, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
  }

  public void testUntypedMouseEventOrderWithClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseUpRequest( shell, shellX + 30, shellY + 30 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, events.size() );
    Event mouseEvent = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 28, mouseEvent.x );
    assertEquals( 28, mouseEvent.y );
    mouseEvent = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseUp, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 28, mouseEvent.x );
    assertEquals( 28, mouseEvent.y );
  }
  
  public void testUntypedMouseEventOrderWithDoubleClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseUpRequest( shell, shellX + 30, shellY + 30 );
    fakeMouseDoubleClickRequest( shell, shellX + 30, shellY + 30 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    Event mouseDown = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseDown.type );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 28, mouseDown.x );
    assertEquals( 28, mouseDown.y );
    Event mouseDoubleClick = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseDoubleClick, mouseDoubleClick.type );
    assertSame( shell, mouseDoubleClick.widget );
    assertEquals( 1, mouseDoubleClick.button );
    assertEquals( 28, mouseDoubleClick.x );
    assertEquals( 28, mouseDoubleClick.y );
    Event mouseUp = ( ( Event )events.get( 2 ) );
    assertEquals( SWT.MouseUp, mouseUp.type );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 28, mouseUp.x );
    assertEquals( 28, mouseUp.y );
  }

  public void testNoMouseEventOutsideClientArea() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp on shell border
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 1, shellY + 1 );
    fakeMouseUpRequest( shell, shellX + 1, shellY + 1 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, shell.getBorderWidth() );
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell titlebar
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 10, shellY + 10 );
    fakeMouseUpRequest( shell, shellX + 10, shellY + 10 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell menubar
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( shell, shellX + 24, shellY + 24 );
    fakeMouseUpRequest( shell, shellX + 24, shellY + 24 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 0, events.size() );
  }

  public void testNoMouseEventOnScrollBars() {
    Table table = new Table( shell, SWT.NONE );
    table.setSize( 100, 100 );
    for( int i = 0; i < 50; i++ ) {
      new TableItem( table, SWT.NONE);
    }
    table.addMouseListener( new LoggingMouseListener( events ) );
    assertEquals( new Rectangle( 0, 0, 85, 100 ), table.getClientArea() );
    // Simulate request that sends a mouseDown + mouseUp on scrollbar
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( );
    fakeMouseDownRequest( table, 90, 10 );
    fakeMouseUpRequest( table, 90, 10 );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 0, events.size() );
  }

  private void fakeUIRootRequestParam() {
    Fixture.fakeRequestParam( RequestParams.UIROOT, DisplayUtil.getId( display ) );
  }

  private static void fakeMouseDoubleClickRequest( Widget widget, int x, int y ) {
    String widgetId = WidgetUtil.getId( widget );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK, widgetId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_X,
                              String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_Y,
                              String.valueOf( y ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_TIME, "0" );
  }

  private static void fakeMouseUpRequest( Widget widget, int x, int y ) {
    String widgetId = WidgetUtil.getId( widget );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP, widgetId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_X, String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_Y, String.valueOf( y ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_TIME, "0" );
  }

  private static void fakeMouseDownRequest( Widget widget, int x, int y ) {
    String widgetId = WidgetUtil.getId( widget );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN, widgetId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_X, String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_Y, String.valueOf( y ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_TIME, "0" );
  }
}
