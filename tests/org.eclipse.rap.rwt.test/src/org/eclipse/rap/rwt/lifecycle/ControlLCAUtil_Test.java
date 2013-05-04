/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ControlLCAUtil_Test {

  private Display display;
  private Shell shell;
  private Button control;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    control.setSize( 10, 10 ); // Would be rendered as invisible otherwise
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.tearDown();
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testProcessSelectionWithText() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );

    fakeSelectionEvent( control, ClientMessageConst.EVENT_PARAM_TEXT, "foo" );
    ControlLCAUtil.processSelection( control, null, false );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( event.text, "foo" );
  }

  @Test
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

  @Test
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

  @Test
  public void testProcessSelectionWithDetailHyperlink() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );

    fakeSelectionEvent( control, ClientMessageConst.EVENT_PARAM_DETAIL, "hyperlink" );
    ControlLCAUtil.processSelection( control, null, false );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( RWT.HYPERLINK, event.detail );
  }

  @Test
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

  @Test
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

  @Test
  public void testProcessDefaultSelection() {
    SelectionListener listener = mock( SelectionListener.class );
    control.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( control ), EVENT_DEFAULT_SELECTION, null );
    ControlLCAUtil.processDefaultSelection( control, null );

    verify( listener, never() ).widgetSelected( any( SelectionEvent.class ) );
    verify( listener ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
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

    fakeTraverse( getId( shell ), 27, 0, "" );
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

  @Test
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

  @Test
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

  @Test
  public void testProcessHelpEvent() {
    shell.open();
    HelpListener listener = mock( HelpListener.class );
    shell.addHelpListener( listener );

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_HELP, null );
    Fixture.readDataAndProcessAction( shell );

    verify( listener, times( 1 ) ).helpRequested( any( HelpEvent.class) );
  }

  @Test
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

  @Test
  public void testRenderVisibilityIntiallyFalse() {
    control.setVisible( false );
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( control, "visibility" ) );
  }

  @Test
  public void testRenderVisibilityInitiallyTrue() {
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  @Test
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
  @Test
  public void testRenderBoundsIntiallyZero() {
    control = new Button( shell, SWT.PUSH );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 0, 0, 0, 0 ]" );
    assertEquals( expected, message.findSetProperty( control, "bounds" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderBoundsInitiallySet() {
    control.setBounds( 10, 20, 100, 200 );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[ 10, 20, 100, 200 ]" );
    assertEquals( expected, message.findSetProperty( control, "bounds" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderBoundsUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBounds( 10, 20, 100, 200 );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  @Test
  public void testRenderIntialChildren() {
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( getId( control ) );
    assertEquals( expected, message.findSetProperty( shell, "children" ) );
  }

  @Test
  public void testRenderChildren() {
    Button button = new Button( shell, SWT.PUSH );
    control.moveBelow( button );
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( getId( button ) ).add( getId( control ) );
    assertEquals( expected, message.findSetProperty( shell, "children" ) );
  }

  @Test
  public void testRenderChildrenOnNotComposite() {
    ControlLCAUtil.renderChildren( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "children" ) );
  }

  @Test
  public void testRenderChildrenUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    control.moveBelow( new Button( shell, SWT.PUSH ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderChildren( shell );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( shell, "children" ) );
  }

  @Test
  public void testRenderIntialTabIndex() throws IOException {
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( control, "tabIndex" ).asInt() );
  }

  @Test
  public void testRenderTabIndex() throws IOException {
    shell.setTabList( new Control[]{ new Button( shell, SWT.PUSH ), control } );
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( control, "tabIndex" ).asInt() );
  }

  @Test
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
  @Test
  public void testRenderIntialToolTip() {
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderToolTip() {
    control.setToolTipText( "foo" );
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( control, "toolTip" ).asString() );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
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
  @Test
  public void testRenderIntialMenu() {
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderMenu() {
    control.setMenu( new Menu( shell ) );
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    String expected = getId( control.getMenu() );
    assertEquals( expected, message.findSetProperty( control, "menu" ).asString() );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
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
  @Test
  public void testRenderIntialEnabled() {
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderEnabled() {
    control.setEnabled( false );
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  @Test
  public void testRenderEnabledUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setEnabled( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  @Test
  public void testRenderIntialBackgroundImage() {
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderBackgroundImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );

    control.setBackgroundImage( image );
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 58 ).add( 12 );
    assertEquals( expected, message.findSetProperty( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderBackgroundImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBackgroundImage( createImage( display, Fixture.IMAGE1 ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderInitialFont() {
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  @Test
  public void testRenderFont() {
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, false, false]" );
    assertEquals( expected, message.findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFontBold() {
    control.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, true, false]" );
    assertEquals( expected, message.findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFontItalic() {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, false, true]" );
    assertEquals( expected, message.findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFontItalicAndBold() {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC | SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, true, true]" );
    assertEquals( expected, message.findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFontUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  @Test
  public void testResetFont() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    control.setFont( null );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderInitialCursor() {
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "cursor" ) );
  }

  @Test
  public void testRenderCursor() {
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "pointer", message.findSetProperty( control, "cursor" ).asString() );
  }

  @Test
  public void testRenderCursorUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "cursor" ) );
  }

  @Test
  public void testResetCursor() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.preserveWidgets();
    control.setCursor( null );
    ControlLCAUtil.renderCursor( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( control, "cursor" ) );
  }

  @Test
  public void testRenderInitialListenActivate() {
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "Activate" ) );
    assertNull( message.findListenOperation( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivate() {
    control.addListener( SWT.Activate, mock( Listener.class ) );

    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "Activate" ) );
  }

  @Test
  public void testRenderListenDeactivate() {
    control.addListener( SWT.Deactivate, mock( Listener.class ) );

    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivateUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addListener( SWT.Activate, mock( Listener.class ) );
    control.addListener( SWT.Deactivate, mock( Listener.class ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "Activate" ) );
    assertNull( message.findListenOperation( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivateRemoved() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Activate, listener );
    control.addListener( SWT.Deactivate, listener );
    Fixture.preserveWidgets();

    control.removeListener( SWT.Activate, listener );
    control.removeListener( SWT.Deactivate, listener );
    ControlLCAUtil.renderListenActivate( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "Activate" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "Deactivate" ) );
  }

  @Test
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

  @Test
  public void testRenderInitialListenFocus() {
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "FocusIn" ) );
    assertNull( message.findListenOperation( control, "FocusIn" ) );
  }

  @Test
  public void testRenderListenFocus() {
    FocusAdapter listener = new FocusAdapter() {};

    control.addFocusListener( listener );
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "FocusIn" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenFocusUnchanged() {
    FocusAdapter listener = new FocusAdapter() {};
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addFocusListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "FocusIn" ) );
    assertNull( message.findListenOperation( control, "FocusIn" ) );
  }

  @Test
  public void testRenderListenFocusRemoved() {
    FocusAdapter listener = new FocusAdapter() {};
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addFocusListener( listener );
    Fixture.preserveWidgets();

    control.removeFocusListener( listener );
    ControlLCAUtil.renderListenFocus( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "FocusIn" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "FocusOut" ) );
  }

  @Test
  public void testRenderInitialListenMouse() {
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "MouseDown" ) );
    assertNull( message.findListenOperation( control, "MouseDoubleClick" ) );
    assertNull( message.findListenOperation( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenMouse() {
    MouseAdapter listener = new MouseAdapter() {};

    control.addMouseListener( listener );
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseDown" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseDoubleClick" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenMouseUnchanged() {
    MouseAdapter listener = new MouseAdapter() {};
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addMouseListener( listener );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenMouse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "MouseDown" ) );
    assertNull( message.findListenOperation( control, "MouseDoubleClick" ) );
    assertNull( message.findListenOperation( control, "MouseUp" ) );
  }

  @Test
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
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseDown" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseDoubleClick" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseUp" ) );
  }

  @Test
  public void testRenderInitialListenKey() {
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "KeyDown" ) );
  }

  @Test
  public void testRenderListenKey() {
    control.addKeyListener( mock( KeyListener.class ) );
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "KeyDown" ) );
  }

  @Test
  public void testRenderListenKeyUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addKeyListener( mock( KeyListener.class ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "KeyDown" ) );
  }

  @Test
  public void testRenderListenKeyRemoved() {
    KeyListener listener = mock( KeyListener.class );
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addKeyListener( listener );
    Fixture.preserveWidgets();

    control.removeKeyListener( listener );
    ControlLCAUtil.renderListenKey( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "KeyDown" ) );
  }

  @Test
  public void testRenderInitialListenTraverse() {
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverse() {
    control.addTraverseListener( mock( TraverseListener.class ) );
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverseUnchanged() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addTraverseListener( mock( TraverseListener.class ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverseRemoved() {
    TraverseListener listener = mock( TraverseListener.class );
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.addTraverseListener( listener );
    Fixture.preserveWidgets();

    control.removeTraverseListener( listener );
    ControlLCAUtil.renderListenTraverse( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "Traverse" ) );
  }

  @Test
  public void testRenderInitialListenMenuDetect() {
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( control, "MenuDetect" ) );
  }

  @Test
  public void testRenderListenMenuDetect() {
    MenuDetectListener listener = new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    };
    control.addMenuDetectListener( listener );
    ControlLCAUtil.renderListenMenuDetect( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MenuDetect" ) );
  }

  @Test
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
    assertNull( message.findListenOperation( control, "MenuDetect" ) );
  }

  @Test
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
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MenuDetect" ) );
  }

  private void fakeKeyDown( String target, int keyCode, int charCode, String modifier ) {
    JsonObject properties = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_KEY_CODE, keyCode )
      .add( ClientMessageConst.EVENT_PARAM_CHAR_CODE, charCode )
      .add( ClientMessageConst.EVENT_PARAM_MODIFIER, modifier );
    Fixture.fakeNotifyOperation( target, ClientMessageConst.EVENT_KEY_DOWN, properties  );
  }

  private void fakeTraverse( String target, int keyCode, int charCode, String modifier ) {
    JsonObject properties = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_KEY_CODE, keyCode )
      .add( ClientMessageConst.EVENT_PARAM_CHAR_CODE, charCode )
      .add( ClientMessageConst.EVENT_PARAM_MODIFIER, modifier );
    Fixture.fakeNotifyOperation( target, ClientMessageConst.EVENT_KEY_DOWN, properties  );
    Fixture.fakeNotifyOperation( target, ClientMessageConst.EVENT_TRAVERSE, properties  );
  }

  private static void fakeSelectionEvent( Control control, String key, String value ) {
    String id = getId( control );
    JsonObject parameters = new JsonObject();
    if( key != null ) {
      parameters.add( key, value );
    }
    Fixture.fakeNotifyOperation( id, ClientMessageConst.EVENT_SELECTION, parameters );
  }

}
