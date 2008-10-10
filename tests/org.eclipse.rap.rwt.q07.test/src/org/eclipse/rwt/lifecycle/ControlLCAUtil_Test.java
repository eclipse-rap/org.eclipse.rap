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
package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class ControlLCAUtil_Test extends TestCase {

  public void testWriteBounds() throws Exception {
    // Ensure that bounds for an uninitialized widget are rendered
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell , SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( composite );
    // TODO [fappel]: check whether minWidth and minHeight is still needed -
    //                causes problems on FF with caching
    //String expected
    //  = w.setSpace( 0, 0, 0, 0 );w.setMinWidth( 0 );w.setMinHeight( 0 );";
    String expected = "w.setSpace( 0, 0, 0, 0 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Ensure that unchanged bound do not lead to markup
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( composite );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ControlLCAUtil.writeBounds( composite );
    assertEquals( "", Fixture.getAllMarkup() );
    // Ensure that bounds-changes on an already initialized widgets are rendered
    Fixture.fakeResponseWriter();
    composite.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    ControlLCAUtil.writeBounds( composite );
    expected = "w.setSpace( 1, 3, 2, 4 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
  }

  public void testWriteTooolTip() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    // on a not yet initialized control: no tool tip -> no markup
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.setToolTipText( "" );
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // on a not yet initialized control: non-empty tool tip must be rendered
    Fixture.fakeResponseWriter();
    shell.setToolTipText( "abc" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "abc" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( null );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "abc" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "newTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "newTooltip" ) != -1 );
    // on an initialized control: change non-empty tooltip text
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "newToolTip" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "anotherTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "anotherTooltip" ) != -1 );
    // test actual markup - the next two lines fake situation that there is
    // already a widget reference (w)
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.newWidget( "Window" );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    String expected = "wm.setToolTip( w, \"anotherTooltip\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteActivateListener() throws IOException {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );

    // A non-initialized widget with no listener attached must not render
    // JavaScript code for adding activateListeners
    AbstractWidgetLCA labelLCA = WidgetUtil.getLCA( label );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    String markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.length() > 0 );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // A non-initialized widget with a listener attached must render JavaScript
    // code for adding activateListeners
    ActivateEvent.addListener( label, listener );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) != -1 );

    // An initialized widget with unchanged activateListeners must not render
    // JavaScript code for adding activateListeners
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( label );
    labelLCA.preserveValues( label );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // Removing an ActivateListener from an initialized widget must render
    // JavaScript code for removing activateListeners
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( label );
    labelLCA.preserveValues( label );
    ActivateEvent.removeListener( label, listener );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );
    assertTrue( markup.indexOf( "removeActivateListenerWidget" ) != -1 );

    // When the shell is disposed of, no removeActivateListener must be rendered
    // Important when disposing of a shell with ShellListener#shellClosed
    labelLCA.preserveValues( label );
    ActivateEvent.addListener( label, listener );
    shell.dispose();
    ControlLCAUtil.writeActivateListener( label );
    Fixture.fakeResponseWriter();
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "removeActivateListenerWidget" ) );
  }

  public void testProcessSelection() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected" );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( listener );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Test that requestParams like '...events.widgetSelected=w3' cause the
    // event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "widgetSelected", log.toString() );

    // Test that requestParams like '...events.widgetSelected=w3,0' cause the
    // event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "widgetSelected", log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );
  }

  public void testMaxZOrder() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    for( int i = 0; i < ControlLCAUtil.MAX_STATIC_ZORDER; i++ ) {
      new Button( shell, SWT.PUSH );
    }
    Control control = new Button( shell, SWT.PUSH );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    ControlLCAUtil.preserveValues( control );
    assertEquals( new Integer( 1 ), adapter.getPreserved( Props.Z_INDEX ) );
  }

  public void testWriteCursor() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Control control = new Button( shell, SWT.PUSH );
    AbstractWidgetLCA controlLCA = WidgetUtil.getLCA( control );
    Cursor cursor = Graphics.getCursor( SWT.CURSOR_HAND );
    RWTFixture.markInitialized( control );
    RWTFixture.preserveWidgets();
    control.setCursor( cursor );
    ControlLCAUtil.writeCursor( control );
    String expected = "w.setCursor( \"pointer\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    controlLCA.preserveValues( control );
    ControlLCAUtil.writeCursor( control );
    assertEquals( "", Fixture.getAllMarkup() );

    control.setCursor( null );
    ControlLCAUtil.writeCursor( control );
    assertTrue( Fixture.getAllMarkup().indexOf( "w.resetCursor();" ) != -1 );
  }

  public void testWritekeyEvents() throws IOException {
    final java.util.List eventLog = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeKeyListener( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeKeyListener( shell );
    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setUserData( \"keyListener\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testProcessKeyEvents() {
    final java.util.List eventLog = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    // Simulate requests that carry information about a key-down event
    // - incomplete request
    RWTFixture.fakeNewRequest();
    RWTFixture.fakePhase( PhaseId.READ_DATA );
    eventLog.clear();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    try {
      ControlLCAUtil.processKeyEvents( shell );
      fail( "Attempting to process incomplete key-event-request must fail" );
    } catch( RuntimeException e ) {
      // expected
    }
    assertTrue( eventLog.isEmpty() );
    // - key-event without meaningful information (e.g. Shift-key only)
    RWTFixture.fakeNewRequest();
    RWTFixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    ControlLCAUtil.processKeyEvents( shell );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = ( Event )eventLog.get( 0 );
    assertEquals( SWT.KeyDown, event.type );
    assertEquals( shell, event.widget );
    assertEquals( 0, event.character );
    assertEquals( 0, event.keyCode );
    assertTrue( event.doit );
  }

  public void testProcessKeyEventsWithDoItFlag() {
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    final java.util.List eventLog = new ArrayList();
    Listener doitTrueListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    Listener doitFalseListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
        event.doit = false;
      }
    };
    Listener keyUpListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    String shellId = WidgetUtil.getId( shell );
    String displayId = DisplayUtil.getId( display );

    // Simulate KeyEvent request, listener leaves doit untouched (doit==true)
    shell.addListener( SWT.KeyDown, doitTrueListener );
    shell.addListener( SWT.KeyUp, keyUpListener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, eventLog.size() );
    assertEquals( SWT.KeyDown, ( ( Event )eventLog.get( 0 ) ).type );
    assertTrue( ( ( Event )eventLog.get( 0 ) ).doit );
    assertEquals( SWT.KeyUp, ( ( Event )eventLog.get( 1 ) ).type );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) == -1 );
    shell.removeListener( SWT.KeyDown, doitTrueListener );
    
    // Simulate KeyEvent request, listener sets doit = false
    eventLog.clear();
    shell.addListener( SWT.KeyDown, doitFalseListener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "65" );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 1, eventLog.size() );
    assertFalse( ( ( Event )eventLog.get( 0 ) ).doit );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
