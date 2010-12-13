/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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



public class UntypedEvents_Test extends TestCase {

  private static final String WIDGET_SELECTED = "widgetSelected";
  private static final String WIDGET_DEFAULT_SELECTED = "widgetSelected";
  private String log = "";

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testUntypedEventInvocation() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    final Widget widget = new Button( shell, SWT.PUSH );

    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( widget, event.widget );
        assertNull( event.item );
        assertEquals( 10, event.x );
        assertEquals( 20, event.y );
        assertEquals( 30, event.width );
        assertEquals( 40, event.height );
        assertEquals( 3, event.stateMask );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    };
    widget.addListener( SWT.Selection, listener );
    SelectionEvent event = new SelectionEvent( widget,
                                               null,
                                               SelectionEvent.WIDGET_SELECTED,
                                               new Rectangle( 10, 20, 30, 40 ),
                                               3,
                                               null,
                                               true,
                                               SWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_SELECTED, log );
    widget.removeListener( SWT.Selection, listener );

    log = "";
    listener = new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( widget, event.widget );
        assertNull( event.item );
        assertEquals( 10, event.x );
        assertEquals( 20, event.y );
        assertEquals( 30, event.width );
        assertEquals( 40, event.height );
        assertEquals( 3, event.stateMask );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    };
    widget.addListener( SWT.DefaultSelection, listener );
    event = new SelectionEvent( widget,
                                null,
                                SelectionEvent.WIDGET_DEFAULT_SELECTED,
                                new Rectangle( 10, 20, 30, 40 ),
                                3,
                                null,
                                true,
                                SWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_DEFAULT_SELECTED, log );
    widget.removeListener( SWT.DefaultSelection, listener );
  }

  public void testFilter() {
    final boolean[] executed = new boolean[ 1 ];
    Display display = new Display();
    display.addFilter( SWT.Selection, new Listener() {
      public void handleEvent( final Event event ) {
        event.type = SWT.None;
        executed[ 0 ] = true;
      }
    } );
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        throw new RuntimeException( "This should never be called." );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );

    Fixture.executeLifeCycleFromServerThread( );
    assertTrue( executed[ 0 ] );
  }

  public void testUntypedEvent_DataFiled() {
    final Object[] data = new Object[ 1 ];
    final Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    final Button button = new Button( shell, SWT.PUSH );
    button.addMouseListener( new MouseAdapter() {
      public void mouseDown( final MouseEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    Event event = new Event();
    event.data = "data";
    button.notifyListeners( SWT.MouseDown, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addFocusListener( new FocusAdapter() {
      public void focusGained( final FocusEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.FocusIn, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addControlListener( new ControlAdapter() {
      public void controlMoved( final ControlEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.Move, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addDragDetectListener( new DragDetectListener() {
      public void dragDetected( final DragDetectEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.DragDetect, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addHelpListener( new HelpListener() {
      public void helpRequested( final HelpEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.Help, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addKeyListener( new KeyAdapter() {
      public void keyPressed( final KeyEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.KeyDown, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
    button.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( final MenuDetectEvent event ) {
        data[ 0 ] = event.data;
      }
    } );
    button.notifyListeners( SWT.MenuDetect, event );
    assertEquals( "data", data[ 0 ] );
    data[ 0 ] = null;
  }
}
