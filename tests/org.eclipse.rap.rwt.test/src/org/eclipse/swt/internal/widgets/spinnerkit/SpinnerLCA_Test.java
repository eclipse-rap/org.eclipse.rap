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
package org.eclipse.swt.internal.widgets.spinnerkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SpinnerLCA_Test {

  private Display display;
  private Shell shell;
  private SpinnerLCA lca;
  private Spinner spinner;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    spinner = new Spinner( shell, SWT.NONE );
    lca = new SpinnerLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( spinner );
    ControlLCATestUtil.testFocusListener( spinner );
    ControlLCATestUtil.testMouseListener( spinner );
    ControlLCATestUtil.testKeyListener( spinner );
    ControlLCATestUtil.testTraverseListener( spinner );
    ControlLCATestUtil.testMenuDetectListener( spinner );
    ControlLCATestUtil.testHelpListener( spinner );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( spinner );
    Object selection = adapter.getPreserved( SpinnerLCA.PROP_SELECTION );
    assertEquals( new Integer( 0 ), selection );
    Object minimum = adapter.getPreserved( SpinnerLCA.PROP_MINIMUM );
    assertEquals( new Integer( 0 ), minimum );
    Object maximum = adapter.getPreserved( SpinnerLCA.PROP_MAXIMUM );
    assertEquals( new Integer( 100 ), maximum );
    Object digits = adapter.getPreserved( SpinnerLCA.PROP_DIGITS );
    assertEquals( new Integer( 0 ), digits );
    Object increment = adapter.getPreserved( SpinnerLCA.PROP_INCREMENT );
    assertEquals( new Integer( 1 ), increment );
    Object pageIncrement = adapter.getPreserved( SpinnerLCA.PROP_PAGE_INCREMENT );
    assertEquals( new Integer( 10 ), pageIncrement );
    Object textLimit = adapter.getPreserved( SpinnerLCA.PROP_TEXT_LIMIT );
    assertNull( textLimit );
    Fixture.clearPreserved();
    spinner.setSelection( 5 );
    spinner.setMinimum( 3 );
    spinner.setMaximum( 200 );
    spinner.setDigits( 2 );
    spinner.setIncrement( 2 );
    spinner.setPageIncrement( 9 );
    spinner.setTextLimit( 10 );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    spinner.addSelectionListener( new SelectionAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    selection = adapter.getPreserved( SpinnerLCA.PROP_SELECTION );
    minimum = adapter.getPreserved( SpinnerLCA.PROP_MINIMUM );
    maximum = adapter.getPreserved( SpinnerLCA.PROP_MAXIMUM );
    digits = adapter.getPreserved( SpinnerLCA.PROP_DIGITS );
    increment = adapter.getPreserved( SpinnerLCA.PROP_INCREMENT );
    pageIncrement = adapter.getPreserved( SpinnerLCA.PROP_PAGE_INCREMENT );
    textLimit = adapter.getPreserved( SpinnerLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( 5 ), selection );
    assertEquals( new Integer( 3 ), minimum );
    assertEquals( new Integer( 200 ), maximum );
    assertEquals( new Integer( 2 ), digits );
    assertEquals( new Integer( 2 ), increment );
    assertEquals( new Integer( 9 ), pageIncrement );
    assertEquals( new Integer( 10 ), textLimit );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    spinner.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    spinner.setEnabled( true );
    // visible
    spinner.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    spinner.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( spinner );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    spinner.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    spinner.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    spinner.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    spinner.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    spinner.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( null, spinner.getToolTipText() );
    Fixture.clearPreserved();
    spinner.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( "some text", spinner.getToolTipText() );
  }

  @Test
  public void testReadSelection() {
    spinner.setMaximum( 100 );

    Fixture.fakeSetProperty( getId( spinner ), "selection", 77 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 77, spinner.getSelection() );
  }

  @Test
  public void testReadSelection_Invalid() {
    spinner.setMaximum( 100 );

    Fixture.fakeSetProperty( getId( spinner ), "selection", 777 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( spinner.getMaximum(), spinner.getSelection() );
  }

  @Test
  public void testSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    spinner.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( spinner ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( spinner );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testDefaultSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    spinner.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( spinner ), ClientMessageConst.EVENT_DEFAULT_SELECTION, null );
    Fixture.readDataAndProcessAction( spinner );

    verify( listener, times( 1 ) ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testModifyEvent() {
    ModifyListener listener = mock( ModifyListener.class );
    spinner.addModifyListener( listener );

    Fixture.fakeSetProperty( getId( spinner ), "selection", 2 );
    Fixture.readDataAndProcessAction( spinner );

    verify( listener, times( 1 ) ).modifyText( any( ModifyEvent.class ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertEquals( "rwt.widgets.Spinner", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertEquals( WidgetUtil.getId( spinner.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderCreateWithWrapAndReadOnly() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.WRAP | SWT.READ_ONLY );

    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
    assertTrue( Arrays.asList( styles ).contains( "READ_ONLY" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    spinner.setMinimum( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( spinner, "minimum" ).asInt() );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    spinner.setMaximum( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( spinner, "maximum" ).asInt() );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    spinner.setSelection( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( spinner, "selection" ).asInt() );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "selection" ) );
  }

  @Test
  public void testRenderInitialDigits() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "digits" ) == -1 );
  }

  @Test
  public void testRenderDigits() throws IOException {
    spinner.setDigits( 2 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( spinner, "digits" ).asInt() );
  }

  @Test
  public void testRenderDigitsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setDigits( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "digits" ) );
  }

  @Test
  public void testRenderInitialIncrement() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  @Test
  public void testRenderIncrement() throws IOException {
    spinner.setIncrement( 2 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( spinner, "increment" ).asInt() );
  }

  @Test
  public void testRenderIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "increment" ) );
  }

  @Test
  public void testRenderInitialPageIncrement() throws IOException {
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  @Test
  public void testRenderPageIncrement() throws IOException {
    spinner.setPageIncrement( 20 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( spinner, "pageIncrement" ).asInt() );
  }

  @Test
  public void testRenderPageIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "pageIncrement" ) );
  }

  @Test
  public void testRenderInitialTextLimit() throws IOException {
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimit() throws IOException {
    spinner.setTextLimit( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( spinner, "textLimit" ).asInt() );
  }

  @Test
  public void testRenderTextLimitUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    spinner.setTextLimit( Text.LIMIT );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( spinner, "textLimit" ) );
  }

  @Test
  public void testRenderTextLimitResetWithNegative() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    spinner.setTextLimit( -5 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( spinner, "textLimit" ) );
  }

  @Test
  public void testRenderInitialDecimalSeparator() throws IOException {
    RWT.setLocale( Locale.US );
    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "decimalSeparator" ) == -1 );
  }

  @Test
  public void testRenderDecimalSeparator() throws IOException {
    RWT.setLocale( Locale.GERMANY );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( ",", message.findSetProperty( spinner, "decimalSeparator" ).asString() );
  }

  @Test
  public void testRenderDecimalSeparatorUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    RWT.setLocale( Locale.GERMANY );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "decimalSeparator" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( spinner, "Selection" ) );
    assertNull( message.findListenOperation( spinner, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    spinner.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.removeListener( SWT.Selection, listener );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( spinner, "Selection" ) );
    assertNull( message.findListenOperation( spinner, "DefaultSelection" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( spinner, "DefaultSelection" ) );
    assertNull( message.findListenOperation( spinner, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    spinner.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( spinner, "DefaultSelection" ) );
    assertNull( message.findListenOperation( spinner, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( spinner, "selection" ) );
  }

  @Test
  public void testRenderAddModifyListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( spinner, "Selection" ) );
  }

  @Test
  public void testRenderRemoveModifyListener() throws Exception {
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    };
    spinner.addModifyListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.removeModifyListener( listener );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( spinner, "Selection" ) );
  }

  @Test
  public void testRenderModifyListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( spinner, "Modify" ) );
  }
}
