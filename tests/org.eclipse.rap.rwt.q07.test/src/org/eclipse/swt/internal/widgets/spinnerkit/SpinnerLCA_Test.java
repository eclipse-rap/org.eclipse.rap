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
package org.eclipse.swt.internal.widgets.spinnerkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
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


public class SpinnerLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    Boolean hasListeners;
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
    Object pageIncrement
      = adapter.getPreserved( SpinnerLCA.PROP_PAGE_INCREMENT );
    assertEquals( new Integer( 10 ), pageIncrement );
    Object textLimit = adapter.getPreserved( SpinnerLCA.PROP_TEXT_LIMIT );
    assertEquals( new Integer( Spinner.LIMIT ), textLimit );
    hasListeners
      = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    hasListeners
      = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    spinner.setSelection( 5 );
    spinner.setMinimum( 3 );
    spinner.setMaximum( 200 );
    spinner.setDigits( 2 );
    spinner.setIncrement( 2 );
    spinner.setPageIncrement( 9 );
    spinner.setTextLimit( 10 );
    spinner.addModifyListener( new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
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
    hasListeners
      = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    hasListeners
      = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
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
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    spinner.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
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
    spinner.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
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
    ActivateEvent.addListener( spinner, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadData() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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

      public void modifyText( final ModifyEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( ".modifyText" );
      }
    } );
    spinner.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( ".widgetSelected" );
      }

      public void widgetDefaultSelected( final SelectionEvent event ) {
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

  protected void setUp() {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
