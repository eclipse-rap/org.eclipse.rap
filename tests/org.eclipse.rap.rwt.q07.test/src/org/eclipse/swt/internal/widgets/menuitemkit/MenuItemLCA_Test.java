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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class MenuItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testBarPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Menu menu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( menu, SWT.BAR );
    Fixture.markInitialized( display );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
    display.dispose();
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
    Fixture.markInitialized( display );
    // Selection_Listener
    testPreserveSelectionListener( menuItem );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
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
    Fixture.markInitialized( display );
    // Selection_Listener
    testPreserveSelectionListener( menuItem );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
    //selection
    menuItem.setSelection( true );
    menuItem.setText( "menu item" );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.TRUE, Boolean.valueOf( menuItem.getSelection() ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.SELECTION_INDICES ) );
    Fixture.clearPreserved();
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
    Fixture.markInitialized( display );
    // Selection_Listener
    testPreserveSelectionListener( menuItem );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
    //selection
    menuItem.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.SELECTION_INDICES ) );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testWidgetSelected() {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menu = new Menu( shell, SWT.POP_UP );
    shell.setMenu( menu );
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
    Fixture.executeLifeCycleFromServerThread( );
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
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRadioSelectionEvent() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, SWT.PUSH );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem3 = new MenuItem( menu, SWT.RADIO );
    new MenuItem( menu, SWT.CHECK );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.add( event );
      }
    };
    radioItem1.addSelectionListener( listener );
    radioItem2.addSelectionListener( listener );
    radioItem3.addSelectionListener( listener );
    String displayId = DisplayUtil.getId( display );
    String radio1Id = WidgetUtil.getId( radioItem1 );
    String radio2Id = WidgetUtil.getId( radioItem2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( radio1Id + ".selection", "true" );
    Fixture.executeLifeCycleFromServerThread();
    SelectionEvent event = ( SelectionEvent )log.get( 0 );
    assertSame( radioItem1, event.widget );
    assertTrue( radioItem1.getSelection() );
    assertEquals( 1, log.size() );
    log.clear();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( radio1Id + ".selection", "false" );
    Fixture.fakeRequestParam( radio2Id + ".selection", "true" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    event = ( SelectionEvent )log.get( 0 );
    assertSame( radioItem1, event.widget );
    assertFalse( radioItem1.getSelection() );
    event = ( SelectionEvent )log.get( 1 );
    assertSame( radioItem2, event.widget );
    assertTrue( radioItem2.getSelection() );
  }

  public void testRadioTypedSelectionEventOrder() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    radioItem1.setText( "1" );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    radioItem2.setText( "2" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.add( event );
      }
    };
    radioItem1.addSelectionListener( listener );
    radioItem2.addSelectionListener( listener );
    radioItem2.setSelection( true );
    String displayId = DisplayUtil.getId( display );
    String item1Id = WidgetUtil.getId( radioItem1 );
    String item2Id = WidgetUtil.getId( radioItem2);
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( item2Id + ".selection", "false" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    SelectionEvent event = ( SelectionEvent )log.get( 0 );
    assertSame( radioItem2, event.widget );
    event = ( SelectionEvent )log.get( 1 );
    assertSame( radioItem1, event.widget );
  }

  public void testRadioUntypedSelectionEventOrder() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    radioItem1.setText( "1" );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    radioItem2.setText( "2" );
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    radioItem1.addListener( SWT.Selection, listener );
    radioItem2.addListener( SWT.Selection, listener );
    radioItem2.setSelection( true );
    String displayId = DisplayUtil.getId( display );
    String item1Id = WidgetUtil.getId( radioItem1 );
    String item2Id = WidgetUtil.getId( radioItem2);
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( item2Id + ".selection", "false" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    Event event = ( Event )log.get( 0 );
    assertSame( radioItem2, event.widget );
    event = ( Event )log.get( 1 );
    assertSame( radioItem1, event.widget );
  }

  public void testRemoveKeyShortcutText() throws IOException {
    String shortcut = "Some shortcut";
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "Some item\t" + shortcut );
    MenuItemLCA lca = new MenuItemLCA();
    lca.renderChanges( menuItem );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( shortcut ) );
  }

  public void testArmEvent() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem pushItem = new MenuItem( menu, SWT.PUSH );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem3 = new MenuItem( menu, SWT.RADIO );
    final MenuItem checkItem = new MenuItem( menu, SWT.CHECK );
    ArmListener listener = new ArmListener() {
      public void widgetArmed( final ArmEvent event ) {
        log.add( event.widget );
      }
    };
    pushItem.addArmListener( listener );
    radioItem1.addArmListener( listener );
    radioItem2.addArmListener( listener );
    radioItem3.addArmListener( listener );
    checkItem.addArmListener( listener );
    String displayId = DisplayUtil.getId( display );
    String menuId = WidgetUtil.getId( menu );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_SHOWN, menuId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 5, log.size() );
    assertTrue( log.contains( pushItem ) );
    assertTrue( log.contains( radioItem1 ) );
    assertTrue( log.contains( radioItem2 ) );
    assertTrue( log.contains( radioItem3 ) );
    assertTrue( log.contains( checkItem ) );
  }

  private void testPreserveSelectionListener( final MenuItem menuItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.SELECTION_LISTENERS ) );
    Fixture.clearPreserved();
    menuItem.addSelectionListener( new SelectionAdapter() {} );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.SELECTION_LISTENERS ) );
    Fixture.clearPreserved();
  }

  private void testPreserveText( final MenuItem menuItem ) {
    IWidgetAdapter adapter;
    adapter = WidgetUtil.getAdapter( menuItem );
    Fixture.preserveWidgets();
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    menuItem.setText( "some text" );
    Fixture.preserveWidgets();
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
  }

  private void testPreserveEnabled( final MenuItem menuItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    menuItem.setEnabled( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    menuItem.setEnabled( true );
    menuItem.getParent().setEnabled( false );
    Fixture.preserveWidgets();
    // even if parent is disabled
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
  }
}
