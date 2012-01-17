/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class ControlLCAUtil_Test extends TestCase {

  private static final String WIDGET_DEFAULT_SELECTED = "widgetDefaultSelected";
  private static final String WIDGET_SELECTED = "widgetSelected";

  private Display display;
  private Shell shell;
  private Control control;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    control.setSize( 10, 10 ); // Would be rendered as invisible otherwise
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testWriteBounds() throws Exception {
    // Ensure that bounds for an uninitialized widget are rendered
    Composite composite = new Composite( shell , SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( composite );

    String expected = "w.setSpace( 0, 0, 0, 0 );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );

    // Ensure that unchanged bounds are not rendered
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( composite );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ControlLCAUtil.writeBounds( composite );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Ensure that bounds-changes on an already initialized widgets are rendered
    Fixture.fakeResponseWriter();
    composite.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    ControlLCAUtil.writeBounds( composite );

    expected = "w.setSpace( 1, 3, 2, 4 );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testWriteToolTip() throws IOException {
    // on a not yet initialized control: no tool tip -> no markup
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", ProtocolTestUtil.getMessageScript() );
    shell.setToolTipText( "" );
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", ProtocolTestUtil.getMessageScript() );
    // on a not yet initialized control: non-empty tool tip must be rendered
    Fixture.fakeResponseWriter();
    shell.setToolTipText( "abc" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "abc" ) );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( null );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "setToolTip" ) );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    shell.setToolTipText( "abc" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( "newTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "setToolTip" ) );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "newTooltip" ) );
    // on an initialized control: change non-empty tooltip text
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    shell.setToolTipText( "newToolTip" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( "anotherTooltip" );
    // test actual markup - the next two lines fake situation that there is
    // already a widget reference (w)
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.newWidget( "Window" );
    ControlLCAUtil.writeToolTip( shell );
    String expected = "wm.setToolTip( w, \"anotherTooltip\" );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testWriteActivateListener() throws IOException {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );

    // A non-initialized widget with no listener attached must not render
    // JavaScript code for adding activateListeners
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeActivateListener( label );
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertEquals( "", ProtocolTestUtil.getMessageScript() );

    // A non-initialized widget with a listener attached must render JavaScript
    // code for adding activateListeners
    ActivateEvent.addListener( label, listener );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeActivateListener( label );
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "addActivateListenerWidget" ) );

    // An initialized widget with unchanged activateListeners must not render
    // JavaScript code for adding activateListeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( label );
    ControlLCAUtil.preserveValues( label );
    ControlLCAUtil.writeActivateListener( label );
    assertEquals( "", ProtocolTestUtil.getMessageScript() );

    // Removing an ActivateListener from an initialized widget must render
    // JavaScript code for removing activateListeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( label );
    ControlLCAUtil.preserveValues( label );
    ActivateEvent.removeListener( label, listener );
    ControlLCAUtil.writeActivateListener( label );
    assertFalse( ProtocolTestUtil.getMessageScript().contains( "addActivateListenerWidget" ) );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "removeActivateListenerWidget" ) );

    // When the shell is disposed of, no removeActivateListener must be rendered
    // Important when disposing of a shell with ShellListener#shellClosed
    ControlLCAUtil.preserveValues( label );
    ActivateEvent.addListener( label, listener );
    shell.dispose();
    ControlLCAUtil.writeActivateListener( label );
    Fixture.fakeResponseWriter();
    assertFalse( ProtocolTestUtil.getMessageScript().contains( "removeActivateListenerWidget" ) );
  }

  public void testProcessSelection() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuilder log = new StringBuilder();
    SelectionListener listener = new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        log.append( WIDGET_SELECTED );
      }
      public void widgetDefaultSelected( SelectionEvent e ) {
        log.append( WIDGET_DEFAULT_SELECTED );
      }
    };
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
    assertEquals( WIDGET_SELECTED, log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );

    // Test that requestParams like '...events.widgetDefaultSelected=w3' cause
    // the event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( WIDGET_DEFAULT_SELECTED, log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );
  }

  public void testMaxZOrder() {
    for( int i = 0; i < ControlLCAUtil.MAX_STATIC_ZORDER; i++ ) {
      new Button( shell, SWT.PUSH );
    }
    Control control = new Button( shell, SWT.PUSH );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    ControlLCAUtil.preserveValues( control );
    assertEquals( new Integer( 1 ), adapter.getPreserved( Props.Z_INDEX ) );
  }

  public void testWriteCursor() throws Exception {
    final Control control = new Button( shell, SWT.PUSH );
    AbstractWidgetLCA controlLCA = WidgetUtil.getLCA( control );
    Cursor cursor = display.getSystemCursor( SWT.CURSOR_HAND );
    Fixture.markInitialized( control );
    Fixture.preserveWidgets();
    control.setCursor( cursor );
    ControlLCAUtil.writeCursor( control );
    String expected = "w.setCursor( \"pointer\" );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );

    Fixture.fakeResponseWriter();
    controlLCA.preserveValues( control );
    ControlLCAUtil.writeCursor( control );
    assertEquals( "", ProtocolTestUtil.getMessageScript() );

    Fixture.fakeResponseWriter();
    control.setCursor( null );
    ControlLCAUtil.writeCursor( control );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "w.resetCursor();" ) );
  }

  public void testWriteKeyEvents() throws IOException {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Fixture.fakeResponseWriter();

    ControlLCAUtil.writeKeyListener( shell );

    assertEquals( "", ProtocolTestUtil.getMessageScript() );

    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeKeyListener( shell );

    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setUserData( \"keyListener\", true );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testWriteTraverseEvents() throws IOException {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeTraverseListener( shell );

    assertEquals( "", ProtocolTestUtil.getMessageScript() );

    shell.addListener( SWT.Traverse, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeTraverseListener( shell );

    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setUserData( \"traverseListener\", true );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testProcessKeyEvents() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    // Simulate requests that carry information about a key-down event
    // - incomplete request
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
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
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( SWT.KeyDown, event.type );
    assertEquals( shell, event.widget );
    assertEquals( 0, event.character );
    assertEquals( 0, event.keyCode );
    assertTrue( event.doit );
  }

  public void testProcessKeyEventWithDisplayFilter() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    eventLog.clear();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "65" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "97" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( 97, event.character );
    assertEquals( 97, event.keyCode );
  }

  public void testProcessKeyEventWithLowerCaseCharacter() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    eventLog.clear();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "65" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "97" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( 97, event.character );
    assertEquals( 97, event.keyCode );
  }

  public void testProcessKeyEventWithUpperCaseCharacter() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    eventLog.clear();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "65" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "65" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( 65, event.character );
    assertEquals( 97, event.keyCode );
  }

  public void testProcessKeyEventWithDigitCharacter() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    eventLog.clear();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "49" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "49" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( 49, event.character );
    assertEquals( 49, event.keyCode );
  }

  public void testProcessKeyEventWithPunctuationCharacter() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    eventLog.clear();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "49" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "33" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( 33, event.character );
    assertEquals( 49, event.keyCode );
  }

  public void testKeyAndTraverseEvents() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    String shellId = WidgetUtil.getId( shell );

    // Ensure that if a key event that notifies about a traversal key is
    // canceled (doit=false) the following traverse event isn't fired at all
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    };
    shell.addListener( SWT.Traverse, listener );
    shell.addListener( SWT.KeyDown, listener );
    shell.addListener( SWT.KeyUp, listener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "27" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, eventLog.size() );
    Event traverseEvent = eventLog.get( 0 );
    assertEquals( SWT.Traverse, traverseEvent.type );
    assertEquals( SWT.TRAVERSE_ESCAPE, traverseEvent.detail );
    assertTrue( traverseEvent.doit );
    Event downEvent = eventLog.get( 1 );
    assertEquals( SWT.KeyDown, downEvent.type );
    Event upEvent = eventLog.get( 2 );
    assertEquals( SWT.KeyUp, upEvent.type );
  }

  public void testGetTraverseKey() {
    int traverseKey;
    traverseKey = ControlLCAUtil.getTraverseKey( 13, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_RETURN );
    traverseKey = ControlLCAUtil.getTraverseKey( 27, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_ESCAPE );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_NEXT );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, SWT.SHIFT );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_PREVIOUS );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, SWT.SHIFT | SWT.CTRL );
    assertEquals( traverseKey, SWT.TRAVERSE_NONE );
  }

  public void testTranslateKeyCode() {
    int keyCode;
    keyCode = ControlLCAUtil.translateKeyCode( 40 );
    assertEquals( SWT.ARROW_DOWN, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 37 );
    assertEquals( SWT.ARROW_LEFT, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 38 );
    assertEquals( SWT.ARROW_UP, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 39 );
    assertEquals( SWT.ARROW_RIGHT, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 20 );
    assertEquals( SWT.CAPS_LOCK, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 36 );
    assertEquals( SWT.HOME, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 115 );
    assertEquals( SWT.F4, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 123 );
    assertEquals( SWT.F12, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 18 );
    assertEquals( SWT.ALT, keyCode );
  }

  public void testWriteBackgroundImage() throws IOException {
    Control control = new Button( shell, SWT.PUSH );
    ControlLCAUtil.preserveBackgroundImage( control );
    Fixture.markInitialized( control );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    control.setBackgroundImage( image );
    ControlLCAUtil.writeBackgroundImage( control );
    String imageLocation = ImageFactory.getImagePath( image );

    String expected = "var w = wm.findWidgetById( \"w2\" );"
                      + "w.setUserData( \"backgroundImageSize\", [58,12 ] );"
                      + "w.setBackgroundImage( \""
                      + imageLocation
                      + "\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveBackgroundImage( control );
    control.setBackgroundImage( null );
    ControlLCAUtil.writeBackgroundImage( control );

    String expected2 = "w.setUserData( \"backgroundImageSize\", null );"
                       + "w.resetBackgroundImage();";
    assertEquals( expected2, ProtocolTestUtil.getMessageScript() );
  }

  public void testProcessHelpEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<HelpEvent> log = new ArrayList<HelpEvent>();
    shell.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent event ) {
        log.add( event );
      }
    } );
    Fixture.fakeRequestParam( JSConst.EVENT_HELP, WidgetUtil.getId( shell ) );
    WidgetLCAUtil.processHelp( shell );
    assertEquals( 1, log.size() );
    HelpEvent event = log.get( 0 );
    assertSame( shell, event.widget );
    assertSame( display, event.display );
  }

  public void testWriteMouseListener() throws IOException {
    Composite control = new Composite( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( control );
    Fixture.markInitialized( control );
    Fixture.markInitialized( display );

    control.addMouseListener( new MouseAdapter() {} );
    ControlLCAUtil.writeChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"mouse\", true );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testWriteFocusListener_FocusableControl() throws IOException {
    Button control = new Button( shell, SWT.PUSH );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( control );
    Fixture.markInitialized( control );
    Fixture.markInitialized( display );

    control.addFocusListener( new FocusAdapter() {} );
    ControlLCAUtil.writeChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"focus\", true );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testWriteFocusListener_NotFocusableControl() throws IOException {
    Label control = new Label( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( control );
    Fixture.markInitialized( control );
    Fixture.markInitialized( display );

    control.addFocusListener( new FocusAdapter() {} );
    ControlLCAUtil.writeChanges( control );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  public void testWriteMenuDetectListener() throws IOException {
    Composite control = new Composite( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( control );
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );

    control.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    } );
    ControlLCAUtil.writeChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"menuDetect\", true );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  //////////////////////////////////////////////
  // Tests for new render methods using protocol

  public void testRenderVisibilityIntiallyFalse() throws IOException {
    control.setVisible( false );
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "visibility" ) );
  }

  public void testRenderVisibilityInitiallyTrue() throws IOException {
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  public void testRenderVisibilityUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setVisible( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsIntiallyZero() throws IOException, JSONException {
    control = new Button( shell, SWT.PUSH );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 0, 0, 0, 0 ]", bounds ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsInitiallySet() throws IOException, JSONException {
    control.setBounds( 10, 20, 100, 200 );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 10, 20, 100, 200 ]", bounds ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBounds( 10, 20, 100, 200 );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  public void testRenderIntialZIndex() throws IOException {
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 300 ), message.findSetProperty( control, "zIndex" ) );
  }

  public void testRenderZIndex() throws IOException {
    control.moveBelow( new Button( shell, SWT.PUSH ) );
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 299 ), message.findSetProperty( control, "zIndex" ) );
  }

  public void testRenderZIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.moveBelow( new Button( shell, SWT.PUSH ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "zIndex" ) );
  }

  public void testRenderIntialTabIndex() throws IOException {
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( control, "tabIndex" ) );
  }

  public void testRenderTabIndex() throws IOException {
    shell.setTabList( new Control[]{ new Button( shell, SWT.PUSH ), control } );
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( control, "tabIndex" ) );
  }

  public void testRenderTabIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.moveBelow( new Button( shell, SWT.PUSH ) );
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();

    Fixture.preserveWidgets();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "tabIndex" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialToolTip() throws IOException {
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTip() throws IOException {
    control.setToolTipText( "foo" );
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setToolTipText( "foo" );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialMenu() throws IOException {
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenu() throws IOException {
    control.setMenu( new Menu( shell ) );
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    String expected = WidgetUtil.getId( control.getMenu() );
    assertEquals( expected, message.findSetProperty( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenuUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setMenu( new Menu( shell ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialEnabled() throws IOException {
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabled() throws IOException {
    control.setEnabled( false );
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabledUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setEnabled( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  public void testRenderIntialBackgroundImage() throws IOException {
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderBackgroundImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    control.setBackgroundImage( image );
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JSONArray args = ( JSONArray )message.findSetProperty( control, "backgroundImage" );
    String expected = "[ \"" + imageLocation + "\", 58, 12 ]";
    assertTrue( ProtocolTestUtil.jsonEquals( expected, args ) );
  }

  public void testRenderBackgroundImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBackgroundImage( Graphics.getImage( Fixture.IMAGE1 ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderInitialFont() throws IOException {
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  public void testRenderFont() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( 4, result.length() );
    assertEquals( "Arial", ( ( JSONArray )result.get( 0 ) ).getString( 0 ) );
    assertEquals( 12, result.getInt( 1 ) );
    assertEquals( false, result.getBoolean( 2 ) );
    assertEquals( false, result.getBoolean( 3 ) );
  }

  public void testRenderFontBold() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( false, result.getBoolean( 3 ) );
  }

  public void testRenderFontItalic() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( false, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }

  public void testRenderFontItalicAndBold() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC | SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }

  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  public void testResetFont() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    control.setFont( null );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( control, "font" ) );
  }

  public void testRenderInitialCursor() {
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "cursor" ) );
  }

  public void testRenderCursor() {
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "pointer", message.findSetProperty( control, "cursor" ) );
  }

  public void testRenderCursorUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "cursor" ) );
  }

  public void testResetCursor() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.preserveWidgets();
    control.setCursor( null );
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( control, "cursor" ) );
  }

  public void testRenderInitialListenActivate() {
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "activate" ) );
  }

  public void testRenderListenActivate() {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    ActivateEvent.addListener( control, listener );

    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "activate" ) );
  }

  public void testRenderListenActivateUnchanged() {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    ActivateEvent.addListener( control, listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "activate" ) );
  }

  public void testRenderListenActivateRemoved() {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    ActivateEvent.addListener( control, listener );
    Fixture.preserveWidgets();

    ActivateEvent.removeListener( control, listener );
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "activate" ) );
  }

  public void testRenderNoListenActivateOnDispose() {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    Fixture.preserveWidgets();
    ActivateEvent.addListener( control, listener );

    control.dispose();
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "activate" ) );
  }

  public void testRenderInitialListenFocus() {
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "focus" ) );
  }

  public void testRenderListenFocus() {
    FocusAdapter listener = new FocusAdapter() {
    };

    control.addFocusListener( listener );
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "focus" ) );
  }

  public void testRenderListenFocusUnchanged() {
    FocusAdapter listener = new FocusAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addFocusListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "focus" ) );
  }

  public void testRenderListenFocusRemoved() {
    FocusAdapter listener = new FocusAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addFocusListener( listener );
    Fixture.preserveWidgets();

    control.removeFocusListener( listener );
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "focus" ) );
  }

  public void testRenderInitialListenMouse() {
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "mouse" ) );
  }

  public void testRenderListenMouse() {
    MouseAdapter listener = new MouseAdapter() {
    };

    control.addMouseListener( listener );
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "mouse" ) );
  }

  public void testRenderListenMouseUnchanged() {
    MouseAdapter listener = new MouseAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addMouseListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "mouse" ) );
  }

  public void testRenderListenMouseRemoved() {
    MouseAdapter listener = new MouseAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addMouseListener( listener );
    Fixture.preserveWidgets();

    control.removeMouseListener( listener );
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "mouse" ) );
  }

  public void testRenderInitialListenKey() {
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "key" ) );
  }

  public void testRenderListenKey() {
    KeyAdapter listener = new KeyAdapter() {
    };

    control.addKeyListener( listener );
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "key" ) );
  }

  public void testRenderListenKeyUnchanged() {
    KeyAdapter listener = new KeyAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addKeyListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "key" ) );
  }

  public void testRenderListenKeyRemoved() {
    KeyAdapter listener = new KeyAdapter() {
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addKeyListener( listener );
    Fixture.preserveWidgets();

    control.removeKeyListener( listener );
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "key" ) );
  }

  public void testRenderInitialListenTraverse() {
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "traverse" ) );
  }

  public void testRenderListenTraverse() {
    TraverseListener listener = new TraverseListener() {
      public void keyTraversed( TraverseEvent e ) {
      }
    };
    control.addTraverseListener( listener );
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "traverse" ) );
  }

  public void testRenderListenTraverseUnchanged() {
    TraverseListener listener = new TraverseListener() {
      public void keyTraversed( TraverseEvent e ) {
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addTraverseListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "traverse" ) );
  }

  public void testRenderListenTraverseRemoved() {
    TraverseListener listener = new TraverseListener() {
      public void keyTraversed( TraverseEvent e ) {
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addTraverseListener( listener );
    Fixture.preserveWidgets();

    control.removeTraverseListener( listener );
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "traverse" ) );
  }

  public void testRenderInitialListenMenuDetect() {
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "menuDetect" ) );
  }

  public void testRenderListenMenuDetect() {
    MenuDetectListener listener = new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    };
    control.addMenuDetectListener( listener );
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "menuDetect" ) );
  }

  public void testRenderListenMenuDetectUnchanged() {
    MenuDetectListener listener = new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addMenuDetectListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "menuDetect" ) );
  }

  public void testRenderListenMenuDetectRemoved() {
    MenuDetectListener listener = new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addMenuDetectListener( listener );
    Fixture.preserveWidgets();

    control.removeMenuDetectListener( listener );
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "menuDetect" ) );
  }

}
