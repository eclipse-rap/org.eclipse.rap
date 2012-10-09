/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;


public class MouseEvent_Test extends TestCase {

  private static class LoggingListener implements Listener {
    private final List<Object> events;
    private LoggingListener( List<Object> events ) {
      this.events = events;
    }
    public void handleEvent( Event event ) {
      events.add( event );
    }
  }

  private static class LoggingMouseListener implements MouseListener {
    private final List<Object> events;
    private LoggingMouseListener( List<Object> events ) {
      this.events = events;
    }
    public void mouseDoubleClick( MouseEvent event ) {
      events.add( event );
    }
    public void mouseDown( MouseEvent event ) {
      events.add( event );
    }
    public void mouseUp( MouseEvent event ) {
      events.add( event );
    }
  }

  private Display display;
  private Shell shell;
  private List<Object> events;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    events = new LinkedList<Object>();
  }

  @Override
  protected void tearDown() throws Exception {
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

  public void testAddListener() {
    MouseListener listener = mock( MouseListener.class );
    
    shell.addMouseListener( listener );
    shell.notifyListeners( SWT.MouseDown, new Event() );
    shell.notifyListeners( SWT.MouseUp, new Event() );
    shell.notifyListeners( SWT.MouseDoubleClick, new Event() );
    
    verify( listener ).mouseDown( any( MouseEvent.class ) );
    verify( listener ).mouseUp( any( MouseEvent.class ) );
    verify( listener ).mouseDoubleClick( any( MouseEvent.class ) );
  }

  public void testRemoveListener() {
    MouseListener listener = mock( MouseListener.class );
    shell.addMouseListener( listener );
    shell.removeMouseListener( listener );

    shell.notifyListeners( SWT.MouseDown, new Event() );
    
    verify( listener, never() ).mouseDown( any( MouseEvent.class ) );
  }

  public void testAddRemoveUntypedListener() {
    Listener listener = new LoggingListener( events );
    // MouseDown
    shell.addListener( SWT.MouseDown, listener );
    shell.notifyListeners( SWT.MouseDown, new Event() );
    Event firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseDown, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseDown, listener );
    shell.notifyListeners( SWT.MouseDown, new Event() );
    assertEquals( 0, events.size() );
    // MouseUp
    shell.addListener( SWT.MouseUp, listener );
    shell.notifyListeners( SWT.MouseUp, new Event() );
    firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseUp, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseUp, listener );
    shell.notifyListeners( SWT.MouseUp, new Event() );
    assertEquals( 0, events.size() );
    // MouseDoubleCLick
    shell.addListener( SWT.MouseDoubleClick, listener );
    shell.notifyListeners( SWT.MouseDoubleClick, new Event() );
    firedEvent = ( Event )events.get( 0 );
    assertEquals( SWT.MouseDoubleClick, firedEvent.type );
    events.clear();
    shell.removeListener( SWT.MouseDoubleClick, listener );
    shell.notifyListeners( SWT.MouseDoubleClick, new Event() );
    assertEquals( 0, events.size() );
  }

  public void testTypedMouseEventOrderWithClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, events.size() );
    MouseEvent mouseDown = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseDown.getID() );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 15, mouseDown.x );
    assertEquals( 53, mouseDown.y );
    MouseEvent mouseUp = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( SWT.MouseUp, mouseUp.getID() );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 15, mouseUp.x );
    assertEquals( 53, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
  }

  public void testTypedMouseEventOrderWithDoubleClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    fakeMouseDoubleClickRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, events.size() );
    MouseEvent mouseDown = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseDown.getID() );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 15, mouseDown.x );
    assertEquals( 53, mouseDown.y );
    assertTrue( ( mouseDown.stateMask & SWT.BUTTON1 ) != 0 );
    MouseEvent mouseDoubleClick = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( SWT.MouseDoubleClick, mouseDoubleClick.getID() );
    assertSame( shell, mouseDoubleClick.widget );
    assertEquals( 1, mouseDoubleClick.button );
    assertEquals( 15, mouseDoubleClick.x );
    assertEquals( 53, mouseDoubleClick.y );
    assertTrue( ( mouseDoubleClick.stateMask & SWT.BUTTON1 ) != 0 );
    MouseEvent mouseUp = ( ( MouseEvent )events.get( 2 ) );
    assertEquals( SWT.MouseUp, mouseUp.getID() );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 15, mouseUp.x );
    assertEquals( 53, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
  }

  public void testUntypedMouseEventOrderWithClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, events.size() );
    Event mouseEvent = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 15, mouseEvent.x );
    assertEquals( 53, mouseEvent.y );
    mouseEvent = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseUp, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 15, mouseEvent.x );
    assertEquals( 53, mouseEvent.y );
  }

  public void testUntypedMouseEventOrderWithDoubleClick() {
    shell.setBounds( 100, 100, 200, 200 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    fakeMouseDoubleClickRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, events.size() );
    Event mouseDown = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseDown.type );
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 15, mouseDown.x );
    assertEquals( 53, mouseDown.y );
    Event mouseDoubleClick = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseDoubleClick, mouseDoubleClick.type );
    assertSame( shell, mouseDoubleClick.widget );
    assertEquals( 1, mouseDoubleClick.button );
    assertEquals( 15, mouseDoubleClick.x );
    assertEquals( 53, mouseDoubleClick.y );
    Event mouseUp = ( ( Event )events.get( 2 ) );
    assertEquals( SWT.MouseUp, mouseUp.type );
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 15, mouseUp.x );
    assertEquals( 53, mouseUp.y );
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
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, shellX + 1, shellY + 1 );
    fakeMouseUpRequest( shell, shellX + 1, shellY + 1 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, shell.getBorderWidth() );
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell titlebar
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, shellX + 10, shellY + 10 );
    fakeMouseUpRequest( shell, shellX + 10, shellY + 10 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell menubar
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( shell, shellX + 24, shellY + 24 );
    fakeMouseUpRequest( shell, shellX + 24, shellY + 24 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
  }

  public void testNoMouseEventOnScrollBars() {
    Table table = createTableWithMouseListener();
    assertEquals( new Rectangle( 0, 0, 90, 100 ), table.getClientArea() );
    // Simulate request that sends a mouseDown + mouseUp on scrollbar
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( table, 93, 50 );
    fakeMouseUpRequest( table, 93, 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
  }

  public void testMouseSelectionEventsOrder() {
    Table table = createTableWithMouseListener();
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        events.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( table, 30, 50 );
    fakeMouseDoubleClickRequest( table, 30, 50 );
    fakeMouseUpRequest( table, 30, 50 );
    fakeSelectionRequest( table, table.getItem( 1 ) );

    events.clear();
    Fixture.readDataAndProcessAction( display );

    assertEquals( 4, events.size() );
    assertEquals( SWT.MouseDown, ( ( TypedEvent )events.get( 0 ) ).getID() );
    assertEquals( SWT.MouseDoubleClick, ( ( TypedEvent )events.get( 1 ) ).getID() );
    assertEquals( SWT.Selection, ( ( TypedEvent )events.get( 2 ) ).getID() );
    assertEquals( SWT.MouseUp, ( ( TypedEvent )events.get( 3 ) ).getID() );
  }

  public void testMouseMenuDetectEventsOrder() {
    Table table = createTableWithMouseListener();
    table.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent event ) {
        events.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    fakeMouseDownRequest( table, 30, 50 );
    fakeMouseUpRequest( table, 30, 50 );
    fakeMenuDetectRequest( table, 30, 50 );

    events.clear();
    Fixture.readDataAndProcessAction( display );

    assertEquals( 3, events.size() );
    assertEquals( SWT.MouseDown, ( ( TypedEvent )events.get( 0 ) ).getID() );
    assertEquals( SWT.MenuDetect, ( ( TypedEvent )events.get( 1 ) ).getID() );
    assertEquals( SWT.MouseUp, ( ( TypedEvent )events.get( 2 ) ).getID() );
  }

  private Table createTableWithMouseListener() {
    Table result = new Table( shell, SWT.NONE );
    result.setSize( 100, 100 );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( result, SWT.NONE);
    }
    result.addMouseListener( new LoggingMouseListener( events ) );
    return result;
  }

  private static void fakeSelectionRequest( Widget widget, Widget item ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_WIDGET_SELECTED,
                                 parameters );
  }

  private static void fakeMenuDetectRequest( Widget widget, int x, int y ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_X, Integer.valueOf( x ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_Y, Integer.valueOf( y ) );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_MENU_DETECT,
                                 parameters );
  }

  private static void fakeMouseDoubleClickRequest( Widget widget, int x, int y ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_BUTTON, Integer.valueOf( 1 ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_X, Integer.valueOf( x ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_Y, Integer.valueOf( y ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_TIME, Integer.valueOf( 0 ) );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK,
                                 parameters );
  }

  private static void fakeMouseUpRequest( Widget widget, int x, int y ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_BUTTON, Integer.valueOf( 1 ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_X, Integer.valueOf( x ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_Y, Integer.valueOf( y ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_TIME, Integer.valueOf( 0 ) );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_UP, parameters );
  }

  private static void fakeMouseDownRequest( Widget widget, int x, int y ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_BUTTON, Integer.valueOf( 1 ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_X, Integer.valueOf( x ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_Y, Integer.valueOf( y ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_TIME, Integer.valueOf( 0 ) );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_DOWN, parameters );
  }
}
