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

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class MenuLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDropDownPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setText( "shell" );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem item = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( shell, SWT.DROP_DOWN );
    item.setMenu( menu );
    shell.setMenuBar( menuBar );
    Fixture.markInitialized( display );
    //menubar
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuBar );
    assertSame( shell, adapter.getPreserved( MenuBarLCA.PROP_SHELL ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Boolean hasMenuListener
     = ( Boolean )adapter.getPreserved( MenuLCAUtil.PROP_MENU_LISTENER );
    assertEquals( Boolean.FALSE, hasMenuListener );
    Fixture.clearPreserved();
    menuBar.setEnabled( false );
    menuBar.addMenuListener( new MenuListener() {

      public void menuHidden( final MenuEvent e ) {
      }

      public void menuShown( final MenuEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( menuBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    hasMenuListener
     = ( Boolean )adapter.getPreserved( MenuLCAUtil.PROP_MENU_LISTENER );
    assertEquals( Boolean.TRUE, hasMenuListener );
    Fixture.clearPreserved();
    testPreserveMenuListener( menu );
    testPreserveWidth( menu );
    testPreserveEnabled( menu );
    display.dispose();
  }

  public void testPopUpPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setText( "shell" );
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    testPreserveMenuListener( menu );
    testPreserveWidth( menu );
    testPreserveEnabled( menu );
    display.dispose();
  }

  public void testUnassignedMenuBar() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    String shellId = WidgetUtil.getId( shell );
    Menu menuBar = new Menu( shell, SWT.BAR );
    // Ensure that a menuBar that is not assigned to any shell (via setMenuBar)
    // is rendered but without settings its parent
    Fixture.fakeResponseWriter();
    MenuLCA lca = new MenuLCA();
    Fixture.markInitialized( display );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setParent" ) == -1 );
    // The contrary: an assigned menuBar has to be rendered with setParent
    Fixture.fakeResponseWriter();
    shell.setMenuBar( menuBar );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    String expected = "setParent( wm.findWidgetById( \"" + shellId + "\" ) )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Un-assigning a menuBar must result in setParent( null ) being rendered
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( menuBar );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setMenuBar( null );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    expected = "setParent( null )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteBoundsForMenuBar() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuLCA menuLCA = new MenuLCA();
    // initial unassigned rendering -> no setSpace
    Fixture.fakeResponseWriter();
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) == -1 );
    // initial assigned rendering -> no setSpace
    Fixture.fakeResponseWriter();
    shell.setMenuBar( menuBar );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
    //
    Fixture.markInitialized( shell );
    Fixture.markInitialized( menuBar );
    // changing bounds of shell -> an assigned menuBar must adjust its size
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
    // changing bounds of shell -> an unassigned menuBar does nothing
    Fixture.fakeResponseWriter();
    shell.setMenuBar( null );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setBounds( new Rectangle( 5, 6, 7, 8 ) );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) == -1 );
    // Simulate client-side size-change of shell: menuBar must render new size
    Fixture.clearPreserved();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    PreserveWidgetsPhaseListener preserveListener
      = new PreserveWidgetsPhaseListener();
    lifeCycle.addPhaseListener( preserveListener );
    shell.setMenuBar( menuBar );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    String menuId = WidgetUtil.getId( menuBar );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread( );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( shellId + ".bounds.x", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.y", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.width", "1234" );
    Fixture.fakeRequestParam( shellId + ".bounds.height", "4321" );
    Fixture.executeLifeCycleFromServerThread( );
    String expected = "wm.findWidgetById( \"" + menuId + "\" );w.setSpace";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    lifeCycle.removePhaseListener( preserveListener );
  }

  private void testPreserveMenuListener( final Menu menu ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( MenuLCAUtil.PROP_MENU_LISTENER ) );
    Fixture.clearPreserved();
    menu.addMenuListener( new MenuAdapter() {} );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( MenuLCAUtil.PROP_MENU_LISTENER ) );
    Fixture.clearPreserved();
  }
  
  private void testPreserveWidth( final Menu menu ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    Fixture.preserveWidgets();
    assertEquals( new Integer( MenuLCAUtil.computeWidth( menu ) ),
                  adapter.getPreserved( MenuLCAUtil.PROP_WIDTH ) );
    Fixture.clearPreserved();
  }

  private void testPreserveEnabled( final Menu menu ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    menu.setEnabled( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
  }
}
