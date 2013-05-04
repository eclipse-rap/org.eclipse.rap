/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class MouseEvent_Test {

  private Display display;
  private Shell shell;
  private List<Object> events;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    events = new LinkedList<Object>();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.time = 4711;
    event.data = new Object();
    event.button = 2;
    event.x = 10;
    event.y = 20;
    event.stateMask = 23;
    event.count = 8;

    MouseEvent mouseEvent = new MouseEvent( event );

    EventTestHelper.assertFieldsEqual( mouseEvent, event );
  }

  @Test
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

  @Test
  public void testRemoveListener() {
    MouseListener listener = mock( MouseListener.class );
    shell.addMouseListener( listener );
    shell.removeMouseListener( listener );

    shell.notifyListeners( SWT.MouseDown, new Event() );

    verify( listener, never() ).mouseDown( any( MouseEvent.class ) );
  }

  @Test
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

  @Test
  public void testTypedMouseEventOrderWithClick() {
    MouseListener mouseListener = mock( MouseListener.class );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( mouseListener );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;

    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( mouseListener );
    ArgumentCaptor<MouseEvent> downCaptor = ArgumentCaptor.forClass( MouseEvent.class );
    inOrder.verify( mouseListener ).mouseDown( downCaptor.capture() );
    MouseEvent mouseDown = downCaptor.getValue();
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 15, mouseDown.x );
    assertEquals( 53, mouseDown.y );
    assertEquals( 1, mouseDown.count );
    ArgumentCaptor<MouseEvent> upCaptor = ArgumentCaptor.forClass( MouseEvent.class );
    inOrder.verify( mouseListener ).mouseUp( upCaptor.capture() );
    MouseEvent mouseUp = upCaptor.getValue();
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 15, mouseUp.x );
    assertEquals( 53, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
    assertEquals( 1, mouseUp.count );
  }

  @Test
  public void testTypedMouseEventOrderWithDoubleClick() {
    MouseListener mouseListener = mock( MouseListener.class );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( mouseListener );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;

    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( shell, eventX, eventY );
    fakeMouseUpRequest( shell, eventX, eventY );
    fakeMouseDoubleClickRequest( shell, eventX, eventY );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( mouseListener );
    ArgumentCaptor<MouseEvent> downCaptor = ArgumentCaptor.forClass( MouseEvent.class );
    inOrder.verify( mouseListener ).mouseDown( downCaptor.capture() );
    MouseEvent mouseDown = downCaptor.getValue();
    assertSame( shell, mouseDown.widget );
    assertEquals( 1, mouseDown.button );
    assertEquals( 15, mouseDown.x );
    assertEquals( 53, mouseDown.y );
    assertEquals( 2, mouseDown.count );
    ArgumentCaptor<MouseEvent> doubleClickCaptor = ArgumentCaptor.forClass( MouseEvent.class );
    inOrder.verify( mouseListener ).mouseDoubleClick( doubleClickCaptor.capture() );
    MouseEvent mouseDoubleClick = doubleClickCaptor.getValue();
    assertSame( shell, mouseDoubleClick.widget );
    assertEquals( 1, mouseDoubleClick.button );
    assertEquals( 15, mouseDoubleClick.x );
    assertEquals( 53, mouseDoubleClick.y );
    assertTrue( ( mouseDoubleClick.stateMask & SWT.BUTTON1 ) != 0 );
    assertEquals( 2, mouseDoubleClick.count );
    ArgumentCaptor<MouseEvent> upCaptor = ArgumentCaptor.forClass( MouseEvent.class );
    inOrder.verify( mouseListener ).mouseUp( upCaptor.capture() );
    MouseEvent mouseUp = upCaptor.getValue();
    assertSame( shell, mouseUp.widget );
    assertEquals( 1, mouseUp.button );
    assertEquals( 15, mouseUp.x );
    assertEquals( 53, mouseUp.y );
    assertTrue( ( mouseUp.stateMask & SWT.BUTTON1 ) != 0 );
    assertEquals( 2, mouseUp.count );
  }

  @Test
  public void testUntypedMouseEventOrderWithClick() {
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeNewRequest();
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

  @Test
  public void testUntypedMouseEventOrderWithDoubleClick() {
    shell.setBounds( 100, 100, 200, 200 );
    shell.open();
    shell.addListener( SWT.MouseDown, new LoggingListener( events ) );
    shell.addListener( SWT.MouseUp, new LoggingListener( events ) );
    shell.addListener( SWT.MouseDoubleClick, new LoggingListener( events ) );
    int eventX = shell.getLocation().x + shell.getClientArea().x + 1;
    int eventY = shell.getLocation().y + shell.getClientArea().y + 1;
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    Fixture.fakeNewRequest();
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

  @Test
  public void testNoMouseEventOutsideClientArea() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new LoggingMouseListener( events ) );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp on shell border
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( shell, shellX + 1, shellY + 1 );
    fakeMouseUpRequest( shell, shellX + 1, shellY + 1 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, shell.getBorderWidth() );
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell titlebar
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( shell, shellX + 10, shellY + 10 );
    fakeMouseUpRequest( shell, shellX + 10, shellY + 10 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
    events.clear();
    // Simulate request that sends a mouseDown + mouseUp on shell menubar
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( shell, shellX + 24, shellY + 24 );
    fakeMouseUpRequest( shell, shellX + 24, shellY + 24 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
  }

  @Test
  public void testNoMouseEventOnScrollBars() {
    Table table = createTableWithMouseListener();
    assertEquals( new Rectangle( 0, 0, 90, 100 ), table.getClientArea() );
    // Simulate request that sends a mouseDown + mouseUp on scrollbar
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( table, 93, 50 );
    fakeMouseUpRequest( table, 93, 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, events.size() );
  }

  @Test
  public void testMouseSelectionEventsOrder() {
    MouseListener mouseListener = mock( MouseListener.class );
    SelectionListener selectionListener = mock( SelectionListener.class );
    Table table = createTableWithMouseListener();
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( table, 30, 50 );
    fakeMouseDoubleClickRequest( table, 30, 50 );
    fakeMouseUpRequest( table, 30, 50 );
    fakeSelectionRequest( table, table.getItem( 1 ) );

    table.addMouseListener( mouseListener );
    table.addSelectionListener( selectionListener );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( selectionListener, mouseListener );
    inOrder.verify( mouseListener ).mouseDown( any( MouseEvent.class ) );
    inOrder.verify( mouseListener ).mouseDoubleClick( any( MouseEvent.class ) );
    inOrder.verify( selectionListener ).widgetSelected( any( SelectionEvent.class ) );
    inOrder.verify( mouseListener ).mouseUp( any( MouseEvent.class ) );
  }

  @Test
  public void testMouseMenuDetectEventsOrder() {
    MouseListener mouseListener = mock( MouseListener.class );
    MenuDetectListener menuDetectListener = mock( MenuDetectListener.class );
    Table table = createTableWithMouseListener();
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( table, 30, 50 );
    fakeMouseUpRequest( table, 30, 50 );
    fakeMenuDetectRequest( table, 30, 50 );

    table.addMouseListener( mouseListener );
    table.addMenuDetectListener( menuDetectListener );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( menuDetectListener, mouseListener );
    inOrder.verify( mouseListener ).mouseDown( any( MouseEvent.class ) );
    inOrder.verify( menuDetectListener ).menuDetected( any( MenuDetectEvent.class ) );
    inOrder.verify( mouseListener ).mouseUp( any( MouseEvent.class ) );
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
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 parameters );
  }

  private static void fakeMenuDetectRequest( Widget widget, int x, int y ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_X, x )
      .add( ClientMessageConst.EVENT_PARAM_Y, y );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_MENU_DETECT,
                                 parameters );
  }

  private static void fakeMouseDoubleClickRequest( Widget widget, int x, int y ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_BUTTON, 1 )
      .add( ClientMessageConst.EVENT_PARAM_X, x )
      .add( ClientMessageConst.EVENT_PARAM_Y, y )
      .add( ClientMessageConst.EVENT_PARAM_TIME, 0 );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK,
                                 parameters );
  }

  private static void fakeMouseUpRequest( Widget widget, int x, int y ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_BUTTON, 1 )
      .add( ClientMessageConst.EVENT_PARAM_X, x )
      .add( ClientMessageConst.EVENT_PARAM_Y, y )
      .add( ClientMessageConst.EVENT_PARAM_TIME, 0 );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_UP, parameters );
  }

  private static void fakeMouseDownRequest( Widget widget, int x, int y ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_BUTTON, 1 )
      .add( ClientMessageConst.EVENT_PARAM_X, x )
      .add( ClientMessageConst.EVENT_PARAM_Y, y )
      .add( ClientMessageConst.EVENT_PARAM_TIME, 0 );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_DOWN, parameters );
  }

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

}
