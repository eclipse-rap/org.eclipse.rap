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
package org.eclipse.swt.internal.widgets.buttonkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// TODO [rst] Split into different test classes for button types
@SuppressWarnings("deprecation")
public class ButtonLCA_Test extends TestCase {

  private static final String PROP_SELECTION_LISTENER = "listener_Selection";

  private Display display;
  private Shell shell;
  private ButtonLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ButtonLCA();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Button button = new Button( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( button );
    ControlLCATestUtil.testFocusListener( button );
    ControlLCATestUtil.testMouseListener( button );
    ControlLCATestUtil.testKeyListener( button );
    ControlLCATestUtil.testTraverseListener( button );
    ControlLCATestUtil.testMenuDetectListener( button );
    ControlLCATestUtil.testHelpListener( button );
  }

  public void testRadioPreserveValues() {
    Button button = new Button( shell, SWT.RADIO );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
  }

  public void testCheckPreserveValues() {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    button.setGrayed( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_GRAYED ) );
  }

  public void testTogglePreserveValues() {
    Button button = new Button( shell, SWT.TOGGLE );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
  }

  private void testPreserveValues( Display display, Button button ) {
    Boolean hasListeners;
    // Text,Image
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      button.setText( "abc" );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      Object object = adapter.getPreserved( Props.TEXT );
      assertEquals( "abc", ( String )object );
      Fixture.clearPreserved();
      Image image = Graphics.getImage( Fixture.IMAGE1 );
      button.setImage( image );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      assertSame( image, adapter.getPreserved( Props.IMAGE ) );
      Fixture.clearPreserved();
    }
    //Selection_Listener
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( PROP_SELECTION_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    button.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( PROP_SELECTION_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    button.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( button );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    button.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    button.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( true );
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    button.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    button.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    button.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, button.getToolTipText() );
    Fixture.clearPreserved();
    button.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( "some text", button.getToolTipText() );
    Fixture.clearPreserved();
  }

  public void testDisabledButtonSelection() {
    final Button button = new Button( shell, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    button.addListener( SWT.Activate, new Listener() {
      public void handleEvent( Event event ) {
        button.setEnabled( false );
      }
    } );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );
    shell.getAdapter( IShellAdapter.class ).setActiveControl( label );
    Fixture.fakeNewRequest();
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    fakeActiveControl( button );

    Fixture.readDataAndProcessAction( display );

    assertFalse( button.getEnabled() );
    verify( listener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testSelectionEvent() {
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( button );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testRadioSelectionEvent() {
    Button button = new Button( shell, SWT.RADIO );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );

    Fixture.fakeSetParameter( getId( button ), "selection", Boolean.TRUE );
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( button );

    assertTrue( button.getSelection() );
    verify( listener ).widgetSelected( any( SelectionEvent.class ) );
  }

  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=224872
  public void testRadioDeselectionEvent() {
    Button button = new Button( shell, SWT.RADIO );
    button.setSelection( true );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );

    Fixture.fakeSetParameter( getId( button ), "selection", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( button );

    assertFalse( button.getSelection() );
    verify( listener ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testRadioTypedSelectionEventOrder_TypedListener() {
    final List<Widget> log = new ArrayList<Widget>();
    Button button1 = new Button( shell, SWT.RADIO );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setSelection( true );
    SelectionAdapter listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log.add( event.widget );
      }
    };
    button1.addSelectionListener( listener );
    button2.addSelectionListener( listener );

    Fixture.fakeSetParameter( getId( button1 ), "selection", Boolean.TRUE );
    Fixture.fakeSetParameter( getId( button2 ), "selection", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( button1 ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.fakeNotifyOperation( getId( button2 ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( display );

    assertTrue( Arrays.equals( new Widget[]{ button2, button1 }, log.toArray() ) );
  }

  public void testRadioTypedSelectionEventOrder_UntypedListener() {
    final List<Widget> log = new ArrayList<Widget>();
    Button button1 = new Button( shell, SWT.RADIO );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setSelection( true );
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        log.add( event.widget );
      }
    };
    button1.addListener( SWT.Selection, listener );
    button2.addListener( SWT.Selection, listener );

    Fixture.fakeSetParameter( getId( button1 ), "selection", Boolean.TRUE );
    Fixture.fakeSetParameter( getId( button2 ), "selection", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( button1 ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.fakeNotifyOperation( getId( button2 ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( display );

    assertTrue( Arrays.equals( new Widget[]{ button2, button1 }, log.toArray() ) );
  }

  public void testRenderWrap() throws Exception {
    Button button = new Button( shell, SWT.PUSH | SWT.WRAP );
    Fixture.fakeResponseWriter();
    PushButtonDelegateLCA lca = new PushButtonDelegateLCA();

    lca.renderInitialization( button );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( button );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

  public void testRenderCreate() throws IOException {
    Button pushButton = new Button( shell, SWT.PUSH );

    lca.renderInitialization( pushButton );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( pushButton );
    assertEquals( "rwt.widgets.Button", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "PUSH" ) );
  }

  public void testRenderCreateArrow() throws IOException {
    Button pushButton = new Button( shell, SWT.ARROW );

    lca.renderInitialization( pushButton );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( pushButton );
    assertEquals( "rwt.widgets.Button", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "ARROW" ) );
  }

  public void testRenderParent() throws IOException {
    Button pushButton = new Button( shell, SWT.PUSH );

    lca.renderInitialization( pushButton );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( pushButton );
    assertEquals( WidgetUtil.getId( pushButton.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "text" ) );
  }

  public void testRenderText() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "test" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextWithQuotationMarks() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "te\"s't" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextWithNewlines() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "\ntes\r\nt\n" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "text" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "alignment" ) );
  }

  public void testRenderInitialAlignment_Arrow() throws IOException {
    Button button = new Button( shell, SWT.ARROW );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( button, "alignment" ) );
  }

  public void testRenderAlignment() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setAlignment( SWT.RIGHT );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( button, "alignment" ) );
  }

  public void testRenderAlignment_Arrow() throws IOException {
    Button button = new Button( shell, SWT.ARROW | SWT.DOWN );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "down", message.findSetProperty( button, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "alignment" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( button, "Selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = new SelectionAdapter() { };
    button.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.removeSelectionListener( listener );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( button, "Selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( button, "selection" ) );
  }

  public void testRenderInitialImage() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Button button = new Button( shell, SWT.PUSH );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    button.setImage( image );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( button, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    button.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    button.setImage( image );

    Fixture.preserveWidgets();
    button.setImage( null );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( button, "image" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "selection" ) );
  }

  public void testRenderSelection() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    button.setSelection( true );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( button, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setSelection( true );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "selection" ) );
  }

  public void testRenderInitialGrayed() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "grayed" ) );
  }

  public void testRenderGrayed() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    button.setGrayed( true );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( button, "grayed" ) );
  }

  public void testRenderGrayedUnchanged() throws IOException {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setGrayed( true );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "grayed" ) );
  }

  private void fakeActiveControl( Control control ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "activeControl", getId( control ) );
    Fixture.fakeSetOperation( getId( control.getShell() ), properties );
  }
}
