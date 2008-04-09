/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class MenuItemLCA_Test extends TestCase {

  public void testBarPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Menu menu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( menu, SWT.BAR );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, menuItem );
    display.dispose();
  }

  private void testPreserveValues( final Display display, final MenuItem menuItem )
  {
    // Selection_Listener
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    menuItem.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuItem );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    menuItem.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //text
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    menuItem.setText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
  }

  public void testPushPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem fileItem = new MenuItem( menu, SWT.CASCADE );
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( fileMenu, SWT.PUSH );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, menuItem );
    display.dispose();
  }

  public void testRadioPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem fileItem = new MenuItem( menu, SWT.CASCADE );
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( fileMenu, SWT.RADIO );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, menuItem );
    //selection
    menuItem.setSelection( true );
    menuItem.setText( "menu item" );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.TRUE, Boolean.valueOf( menuItem.getSelection() ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem fileItem = new MenuItem( menu, SWT.CASCADE );
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( fileMenu, SWT.CHECK );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, menuItem );
    //selection
    menuItem.setSelection( true );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testWidgetSelected() {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
    menuItem.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( menuItem, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Menu menu = new Menu( menuBar );
    final MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
    menuItem.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( menuItem, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, menuItem.getSelection() );
      }
    } );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( menuItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRadioItemSelected() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, SWT.PUSH );
    MenuItem radioItem1Group1 = new MenuItem( menu, SWT.RADIO );
    MenuItem radioItem2Group1 = new MenuItem( menu, SWT.RADIO );
    new MenuItem( menu, SWT.CHECK );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String radioItem1Group1Id = WidgetUtil.getId( radioItem1Group1 );
    radioItem2Group1.setSelection( true );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( radioItem1Group1Id + ".selection", "true" );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, radioItem1Group1.getSelection() );
    assertEquals( false, radioItem2Group1.getSelection() );
  }

  public void testRadioManagerReference() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, SWT.PUSH );
    new MenuItem( menu, SWT.CHECK );
    MenuItem radio1 = new MenuItem( menu, SWT.RADIO );
    MenuItem radio2 = new MenuItem( menu, SWT.RADIO );
    Fixture.fakeResponseWriter();
    MenuItemLCA lca = new MenuItemLCA();
    lca.renderInitialization( radio2 );
    String id = WidgetUtil.getId( radio1 );
    String expected = "assignRadioManager( wm.findWidgetById( \"" + id + "\" )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
