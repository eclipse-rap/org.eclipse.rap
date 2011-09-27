/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONObject;


public class SpinnerLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private SpinnerLCA lca;

  protected void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new SpinnerLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( spinner );
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
    // control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    spinner.addControlListener( new ControlAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    spinner.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    spinner.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    spinner.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
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
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    spinner.addFocusListener( new FocusAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( spinner, new ActivateAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadData() {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    spinner.setMaximum( 100 );
    String spinnerId = WidgetUtil.getId( spinner );
    // simulate valid client-side selection
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( spinnerId + ".selection", "77" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 77, spinner.getSelection() );
    // simulate invalid client-side selection
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( spinnerId + ".selection", "777" );
    spinner.setSelection( 1 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( spinner.getMaximum(), spinner.getSelection() );
  }

  public void testModifyAndSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Spinner spinner = new Spinner( shell, SWT.NONE );
    shell.open();
    String spinnerId = WidgetUtil.getId( spinner );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, spinnerId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, spinnerId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, spinnerId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "", log.toString() );
    log.setLength( 0 );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( ".modifyText" );
      }
    } );
    spinner.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( ".widgetSelected" );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( ".widgetDefaultSelected" );
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( spinnerId + ".selection", "2" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, spinnerId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( ".modifyText.widgetSelected", log.toString() );
    log.setLength( 0 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, spinnerId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( ".widgetDefaultSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertEquals( "rwt.widgets.Spinner", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertEquals( WidgetUtil.getId( spinner.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithWrapAndReadOnly() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.WRAP | SWT.READ_ONLY );

    lca.renderInitialization( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
    assertTrue( Arrays.asList( styles ).contains( "READ_ONLY" ) );
  }

  public void testRenderInitialMinimum() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  public void testRenderMinimum() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setMinimum( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( spinner, "minimum" ) );
  }

  public void testRenderMinimumUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "minimum" ) );
  }

  public void testRenderInitialMaxmum() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  public void testRenderMaxmum() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setMaximum( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( spinner, "maximum" ) );
  }

  public void testRenderMaxmumUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "maximum" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setSelection( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( spinner, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "selection" ) );
  }

  public void testRenderInitialDigits() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "digits" ) == -1 );
  }

  public void testRenderDigits() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setDigits( 2 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( spinner, "digits" ) );
  }

  public void testRenderDigitsUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setDigits( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "digits" ) );
  }

  public void testRenderInitialIncrement() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  public void testRenderIncrement() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setIncrement( 2 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( spinner, "increment" ) );
  }

  public void testRenderIncrementUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "increment" ) );
  }

  public void testRenderInitialPageIncrement() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  public void testRenderPageIncrement() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setPageIncrement( 20 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( spinner, "pageIncrement" ) );
  }

  public void testRenderPageIncrementUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "pageIncrement" ) );
  }

  public void testRenderInitialTextLimit() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "textLimit" ) );
  }

  public void testRenderTextLimit() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    spinner.setTextLimit( 10 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( spinner, "textLimit" ) );
  }

  public void testRenderTextLimitUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "textLimit" ) );
  }

  public void testRenderTextLimitReset() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    spinner.setTextLimit( Text.LIMIT );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( spinner, "textLimit" ) );
  }

  public void testRenderTextLimitResetWithNegative() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    spinner.setTextLimit( 10 );
    Fixture.preserveWidgets();
    spinner.setTextLimit( -5 );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( spinner, "textLimit" ) );
  }

  public void testRenderInitialDecimalSeparator() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    lca.render( spinner );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( spinner );
    assertTrue( operation.getPropertyNames().indexOf( "decimalSeparator" ) == -1 );
  }

  public void testRenderDecimalSeparator() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );

    RWT.setLocale( Locale.GERMANY );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( ",", message.findSetProperty( spinner, "decimalSeparator" ) );
  }

  public void testRenderDecimalSeparatorUnchanged() throws IOException {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );

    RWT.setLocale( Locale.GERMANY );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( spinner, "decimalSeparator" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( spinner, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    spinner.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.removeSelectionListener( listener );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( spinner, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( spinner, "selection" ) );
  }

  public void testRenderAddModifyListener() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( spinner );
    Fixture.preserveWidgets();

    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
      }
    } );
    lca.renderChanges( spinner );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( spinner, "modify" ) );
  }

  public void testRenderRemoveModifyListener() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
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
    assertEquals( Boolean.FALSE, message.findListenProperty( spinner, "modify" ) );
  }

  public void testRenderModifyListenerUnchanged() throws Exception {
    Spinner spinner = new Spinner( shell, SWT.NONE );
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
    assertNull( message.findListenOperation( spinner, "modify" ) );
  }
}
