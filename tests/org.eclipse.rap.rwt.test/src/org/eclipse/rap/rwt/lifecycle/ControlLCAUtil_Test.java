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
package org.eclipse.rap.rwt.lifecycle;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_WIDGET_DEFAULT_SELECTED;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.ArgumentCaptor;


@SuppressWarnings("deprecation")
public class ControlLCAUtil_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Button control;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    control.setSize( 10, 10 ); // Would be rendered as invisible otherwise
    Fixture.fakeNewRequest( display );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testProcessSelectionForControl() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
    
    fakeSelectionEvent( control, null, null );
    ControlLCAUtil.processSelection( control, null, true );
    
    verify( listener, never() ).widgetDefaultSelected( any( SelectionEvent.class ) );
    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( control, event.widget );
    assertEquals( control.getBounds(), 
                  new Rectangle( event.x, event.y, event.width, event.height ) );
  }

  public void testProcessSelectionForControlWithItem() {
    Item item = mock( Item.class );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Selection, listener );
    
    fakeSelectionEvent( control, null, null );
    ControlLCAUtil.processSelection( control, item, true );
    
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( control, event.widget );
    assertEquals( item, event.item );
    assertEquals( SWT.Selection, event.type );
    assertEquals( control.getBounds(), event.getBounds() );
  }
  
  public void testProcessSelectionWithStateMask() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
    
    fakeSelectionEvent( control, "altKey", "true" );
    ControlLCAUtil.processSelection( control, null, true );
    
    verify( listener, never() ).widgetDefaultSelected( any( SelectionEvent.class ) );
    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( control, event.widget );
    assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
    assertEquals( control.getBounds(), 
                  new Rectangle( event.x, event.y, event.width, event.height ) );
  }

  public void testProcessSelectionWithoutReadingBounds() {
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Selection, listener );
    
    fakeSelectionEvent( control, null, null );
    ControlLCAUtil.processSelection( control, null, false );
    
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( control, event.widget );
    assertEquals( SWT.Selection, event.type );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
  }
  
  public void testProcessSelectionWithDetailChecked() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
    
    fakeSelectionEvent( control, ClientMessageConst.EVENT_PARAM_DETAIL, "check" );
    ControlLCAUtil.processSelection( control, null, false );
    
    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( SWT.CHECK, event.detail );
  }

  public void testProcessSelectionWithDetailSearch() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
    
    fakeSelectionEvent( control, ClientMessageConst.EVENT_PARAM_DETAIL, "search" );
    ControlLCAUtil.processSelection( control, null, false );
    
    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( SWT.ICON_SEARCH, event.detail );
  }
  
  public void testProcessSelectionWithDetailCancel() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
    
    fakeSelectionEvent( control, ClientMessageConst.EVENT_PARAM_DETAIL, "cancel" );
    ControlLCAUtil.processSelection( control, null, false );
    
    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( SWT.CANCEL, event.detail );
  }
  
  public void testProcessDefaultSelection() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );
  
    Fixture.fakeNotifyOperation( getId( control ), EVENT_WIDGET_DEFAULT_SELECTED, null );
    ControlLCAUtil.processDefaultSelection( control, null );
  
    verify( listener, never() ).widgetSelected( any( SelectionEvent.class ) );
    verify( listener ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }

  public void testProcessKeyEventWithDisplayFilter() {
    shell.open();
    Listener listener = mock( Listener.class );
    display.addFilter( SWT.KeyDown, listener );

    fakeKeyDown( getId( shell ), 65, 97, "" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener, times( 1 ) ).handleEvent( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'a', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  public void testProcessKeyEventWithLowerCaseCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeKeyDown( getId( shell ), 65, 97, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'a', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  public void testProcessKeyEventWithUpperCaseCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeKeyDown( getId( shell ), 65, 65, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'A', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  public void testProcessKeyEventWithDigitCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeKeyDown( getId( shell ), 49, 49, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 49, captor.getValue().keyCode );
    assertEquals( 49, captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  public void testProcessKeyEventWithPunctuationCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeKeyDown( getId( shell ), 49, 33, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 49, captor.getValue().keyCode );
    assertEquals( 33, captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  public void testKeyAndTraverseEvents() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    };
    shell.addListener( SWT.Traverse, listener );
    shell.addListener( SWT.KeyDown, listener );
    shell.addListener( SWT.KeyUp, listener );

    fakeKeyDown( getId( shell ), 27, 0, "" );
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

  public void testProcessHelpEvent() {
    shell.open();
    HelpListener listener = mock( HelpListener.class );
    shell.addHelpListener( listener );

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_HELP, null );
    Fixture.readDataAndProcessAction( shell );

    verify( listener, times( 1 ) ).helpRequested( any( HelpEvent.class) );
  }

  public void testRenderFocusListener_NotFocusableControl() {
    Label control = new Label( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( control );
    Fixture.markInitialized( control );
    Fixture.markInitialized( display );

    control.addFocusListener( new FocusAdapter() {} );
    ControlLCAUtil.renderChanges( control );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  //////////////////////////////////////////////
  // Tests for new render methods using protocol

  public void testRenderVisibilityIntiallyFalse() {
    control.setVisible( false );
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "visibility" ) );
  }

  public void testRenderVisibilityInitiallyTrue() {
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  public void testRenderVisibilityUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setVisible( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsIntiallyZero() throws JSONException {
    control = new Button( shell, SWT.PUSH );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 0, 0, 0, 0 ]", bounds ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsInitiallySet() throws JSONException {
    control.setBounds( 10, 20, 100, 200 );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ 10, 20, 100, 200 ]", bounds ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBounds( 10, 20, 100, 200 );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  public void testRenderIntialChildren() throws JSONException {
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( shell, "children" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[" +  WidgetUtil.getId( control ) + "]", actual ) );
  }

  public void testRenderChildren() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    control.moveBelow( button );
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( shell, "children" );
    String expected = "[" + WidgetUtil.getId( button ) + "," + WidgetUtil.getId( control ) + "]";
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderChildrenOnNotComposite() {
    ControlLCAUtil.renderChildren( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "children" ) );
  }

  public void testRenderChildrenUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    control.moveBelow( new Button( shell, SWT.PUSH ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "children" ) );
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
  public void testRenderIntialToolTip() {
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTip() {
    control.setToolTipText( "foo" );
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTipUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setToolTipText( "foo" );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialMenu() {
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenu() {
    control.setMenu( new Menu( shell ) );
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    String expected = WidgetUtil.getId( control.getMenu() );
    assertEquals( expected, message.findSetProperty( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenuUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setMenu( new Menu( shell ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialEnabled() {
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabled() {
    control.setEnabled( false );
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabledUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setEnabled( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  public void testRenderIntialBackgroundImage() {
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderBackgroundImage() throws JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    control.setBackgroundImage( image );
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JSONArray args = ( JSONArray )message.findSetProperty( control, "backgroundImage" );
    String expected = "[ \"" + imageLocation + "\", 58, 12 ]";
    assertTrue( ProtocolTestUtil.jsonEquals( expected, args ) );
  }

  public void testRenderBackgroundImageUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBackgroundImage( Graphics.getImage( Fixture.IMAGE1 ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderInitialFont() {
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  public void testRenderFont() throws JSONException {
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

  public void testRenderFontBold() throws JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( false, result.getBoolean( 3 ) );
  }

  public void testRenderFontItalic() throws JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( false, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }

  public void testRenderFontItalicAndBold() throws JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC | SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }

  public void testRenderFontUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  public void testResetFont() {
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
    control.addListener( SWT.Activate, mock( Listener.class ) );

    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( control, "activate" ) );
  }

  public void testRenderListenActivateUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addListener( SWT.Activate, mock( Listener.class ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "activate" ) );
  }

  public void testRenderListenActivateRemoved() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Activate, listener );
    Fixture.preserveWidgets();

    control.removeListener( SWT.Activate, listener );
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( control, "activate" ) );
  }

  public void testRenderNoListenActivateOnDispose() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    Fixture.preserveWidgets();
    control.addListener( SWT.Activate, mock( Listener.class ) );

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


  private void fakeKeyDown( String target, int keyCode, int charCode, String modifier ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_KEY_CODE, Integer.valueOf( keyCode ) );
    properties.put( ClientMessageConst.EVENT_PARAM_CHAR_CODE, Integer.valueOf( charCode ) );
    properties.put( ClientMessageConst.EVENT_PARAM_MODIFIER, modifier );
    Fixture.fakeNotifyOperation( target, ClientMessageConst.EVENT_KEY_DOWN, properties  );
  }

  private static void fakeSelectionEvent( Control control, String key, String value ) {
    String id = getId( control );
    Map<String, Object> parameters = new HashMap<String,Object>();
    if( key != null ) {
      parameters.put( key, value );
    }
    Fixture.fakeNotifyOperation( id, ClientMessageConst.EVENT_WIDGET_SELECTED, parameters );
  }

}
