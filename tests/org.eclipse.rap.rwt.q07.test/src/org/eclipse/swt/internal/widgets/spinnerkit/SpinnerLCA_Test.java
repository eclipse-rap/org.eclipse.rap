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

package org.eclipse.swt.internal.widgets.spinnerkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
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
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( spinner );
    Object selection = adapter.getPreserved( Props.SELECTION_INDICES );
    assertEquals( new Integer( 0 ), selection );
    Object minimum = adapter.getPreserved( SpinnerLCA.PROP_MINIMUM );
    assertEquals( new Integer( 0 ), minimum );
    Object maximum = adapter.getPreserved( SpinnerLCA.PROP_MAXIMUM );
    assertEquals( new Integer( 100 ), maximum );
    Object increment = adapter.getPreserved( SpinnerLCA.PROP_INCREMENT );
    assertEquals( new Integer( 1 ), increment );
    Object pageIncrement
     = adapter.getPreserved( SpinnerLCA.PROP_PAGE_INCREMENT );
    assertEquals( new Integer( 10 ), pageIncrement );
    hasListeners
     = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    spinner.setSelection( 5 );
    spinner.setMinimum( 3 );
    spinner.setMaximum( 200 );
    spinner.setIncrement( 2 );
    spinner.setPageIncrement( 9 );
    spinner.addModifyListener( new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    selection = adapter.getPreserved( Props.SELECTION_INDICES );
    minimum = adapter.getPreserved( SpinnerLCA.PROP_MINIMUM );
    maximum = adapter.getPreserved( SpinnerLCA.PROP_MAXIMUM );
    increment = adapter.getPreserved( SpinnerLCA.PROP_INCREMENT );
    pageIncrement = adapter.getPreserved( SpinnerLCA.PROP_PAGE_INCREMENT );
    assertEquals( new Integer( 5 ), selection );
    assertEquals( new Integer( 3 ), minimum );
    assertEquals( new Integer( 200 ), maximum );
    assertEquals( new Integer( 2 ), increment );
    assertEquals( new Integer( 9 ), pageIncrement );
    hasListeners
     = ( Boolean )adapter.getPreserved( SpinnerLCA.PROP_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    spinner.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    spinner.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( spinner );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    spinner.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    spinner.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    spinner.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    spinner.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    spinner.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    spinner.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    // tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( null, spinner.getToolTipText() );
    RWTFixture.clearPreserved();
    spinner.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    assertEquals( "some text", spinner.getToolTipText() );
    RWTFixture.clearPreserved();
    // activate_listeners Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    spinner.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( spinner, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( spinner );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testReadData() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    String spinnerId = WidgetUtil.getId( spinner );
    // simulate valid client-side selection
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "77" );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 77, spinner.getSelection() );
    // simulate invalid client-side selection
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "777" );
    spinner.setSelection( 1 );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( spinner.getMaximum(), spinner.getSelection() );
  }
  
  public void testModifyEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Spinner spinner = new Spinner( shell, SWT.NONE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String spinnerId = WidgetUtil.getId( spinner );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, spinnerId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "", log.toString() );
    log.setLength( 0 );
    spinner.addModifyListener( new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "2" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, spinnerId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "modifyText", log.toString() );
  }
  
  protected void setUp() {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
