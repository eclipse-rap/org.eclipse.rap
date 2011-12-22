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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("deprecation")
public class MenuItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Menu menuBar;
  private Menu menu;
  private MenuItemLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    menuBar = new Menu( shell, SWT.BAR );
    menu = new Menu( shell, SWT.POP_UP );
    lca = new MenuItemLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testBarPreserveValues() {
    shell.setMenuBar( menuBar );
    final MenuItem menuItem = new MenuItem( menuBar, SWT.BAR );
    Fixture.markInitialized( display );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
  }

  public void testPushPreserveValues() {
    MenuItem fileItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    shell.setMenuBar( menuBar );
    final MenuItem menuItem = new MenuItem( fileMenu, SWT.PUSH );
    Fixture.markInitialized( display );
    testPreserveEnabled( menuItem );
    testPreserveText( menuItem );
  }

  public void testWidgetSelected() {
    final boolean[] wasEventFired = { false };
    shell.setMenu( menu );
    final MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
    menuItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
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
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    shell.setMenuBar( menuBar );
    Menu menu = new Menu( menuBar );
    final MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
    menuItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
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
    String displayId = DisplayUtil.getId( display );
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( menuItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRadioSelectionEvent() {
    final java.util.List<SelectionEvent> log = new ArrayList<SelectionEvent>();
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, SWT.PUSH );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem3 = new MenuItem( menu, SWT.RADIO );
    new MenuItem( menu, SWT.CHECK );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event );
      }
    };
    radioItem1.addSelectionListener( listener );
    radioItem2.addSelectionListener( listener );
    radioItem3.addSelectionListener( listener );
    String radio1Id = WidgetUtil.getId( radioItem1 );
    String radio2Id = WidgetUtil.getId( radioItem2 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( radio1Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    SelectionEvent event = log.get( 0 );
    assertSame( radioItem1, event.widget );
    assertTrue( radioItem1.getSelection() );
    assertEquals( 1, log.size() );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( radio1Id + ".selection", "false" );
    Fixture.fakeRequestParam( radio2Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    event = log.get( 0 );
    assertSame( radioItem1, event.widget );
    assertFalse( radioItem1.getSelection() );
    event = log.get( 1 );
    assertSame( radioItem2, event.widget );
    assertTrue( radioItem2.getSelection() );
  }

  public void testRadioTypedSelectionEventOrder() {
    final java.util.List<SelectionEvent> log = new ArrayList<SelectionEvent>();
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    radioItem1.setText( "1" );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    radioItem2.setText( "2" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event );
      }
    };
    radioItem1.addSelectionListener( listener );
    radioItem2.addSelectionListener( listener );
    radioItem2.setSelection( true );
    String item1Id = WidgetUtil.getId( radioItem1 );
    String item2Id = WidgetUtil.getId( radioItem2);
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( item2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    SelectionEvent event = log.get( 0 );
    assertSame( radioItem2, event.widget );
    event = log.get( 1 );
    assertSame( radioItem1, event.widget );
  }

  public void testRadioUntypedSelectionEventOrder() {
    final java.util.List<Event> log = new ArrayList<Event>();
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    radioItem1.setText( "1" );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    radioItem2.setText( "2" );
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        log.add( event );
      }
    };
    radioItem1.addListener( SWT.Selection, listener );
    radioItem2.addListener( SWT.Selection, listener );
    radioItem2.setSelection( true );
    String item1Id = WidgetUtil.getId( radioItem1 );
    String item2Id = WidgetUtil.getId( radioItem2);
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( item2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    Event event = log.get( 0 );
    assertSame( radioItem2, event.widget );
    event = log.get( 1 );
    assertSame( radioItem1, event.widget );
  }

  public void testArmEvent() {
    final java.util.List<Widget> log = new ArrayList<Widget>();
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    final MenuItem pushItem = new MenuItem( menu, SWT.PUSH );
    final MenuItem radioItem1 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem2 = new MenuItem( menu, SWT.RADIO );
    final MenuItem radioItem3 = new MenuItem( menu, SWT.RADIO );
    final MenuItem checkItem = new MenuItem( menu, SWT.CHECK );
    ArmListener listener = new ArmListener() {
      public void widgetArmed( ArmEvent event ) {
        log.add( event.widget );
      }
    };
    pushItem.addArmListener( listener );
    radioItem1.addArmListener( listener );
    radioItem2.addArmListener( listener );
    radioItem3.addArmListener( listener );
    checkItem.addArmListener( listener );
    String menuId = WidgetUtil.getId( menu );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_SHOWN, menuId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 5, log.size() );
    assertTrue( log.contains( pushItem ) );
    assertTrue( log.contains( radioItem1 ) );
    assertTrue( log.contains( radioItem2 ) );
    assertTrue( log.contains( radioItem3 ) );
    assertTrue( log.contains( checkItem ) );
  }

  private void testPreserveText( MenuItem menuItem ) {
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

  private void testPreserveEnabled( MenuItem menuItem ) {
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

  public void testRenderCreatePush() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.MenuItem", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "PUSH" ) );
  }

  public void testRenderCreateCheck() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.MenuItem", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "CHECK" ) );
  }

  public void testRenderCreateRadio() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.RADIO );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.MenuItem", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "RADIO" ) );
  }

  public void testRenderCreateCascade() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CASCADE );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.MenuItem", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "CASCADE" ) );
  }

  public void testRenderParent() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( item.getParent() ), operation.getParent() );
  }

  public void testRenderIndex() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 0 ), message.findCreateProperty( item, "index" ) );
  }

  public void testRenderDispose() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    lca.renderDispose( item );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( item ), operation.getTarget() );
  }

  public void testRenderInitialMenu() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CASCADE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "menu" ) == -1 );
  }

  public void testRenderMenu() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    Menu subMenu = new Menu( item );

    item.setMenu( subMenu );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( subMenu ), message.findSetProperty( item, "menu" ) );
  }

  public void testRenderMenuUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CASCADE );
    Menu subMenu = new Menu( item );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setMenu( subMenu );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "menu" ) );
  }

  public void testRenderInitialEnabled() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "enabled" ) == -1 );
  }

  public void testRenderEnabled() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );

    item.setEnabled( false );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( item, "enabled" ) );
  }

  public void testRenderEnabledUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "enabled" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    item.setSelection( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setSelection( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "selection" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    item.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "customVariant" ) );
  }

  public void testRenderInitialTexts() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderTexts() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    item.setText( "foo" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( item, "text" ) );
  }

  public void testRenderTextsUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "text" ) );
  }

  public void testRenderInitialImages() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "image" ) == -1 );
  }

  public void testRenderImages() throws IOException, JSONException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( image );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "image" );
    String expected = "[\"rwt-resources/generated/90fb0bfe\",58,12]";
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImagesUnchanged() throws IOException {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "image" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( item, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    SelectionListener listener = new SelectionAdapter() { };
    item.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.removeSelectionListener( listener );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( item, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( item, "selection" ) );
  }

  public void testRenderAddHelpListener() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( item, "help" ) );
  }

  public void testRenderRemoveHelpListener() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    HelpListener listener = new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    };
    item.addHelpListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.removeHelpListener( listener );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( item, "help" ) );
  }

  public void testRenderHelpListenerUnchanged() throws Exception {
    MenuItem item = new MenuItem( menu, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    item.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( item, "help" ) );
  }
}
