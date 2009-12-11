/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabfolderkit;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class TabFolderLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    TabFolder tabfolder = new TabFolder( shell, SWT.NONE );
    Boolean hasListeners;
    Fixture.markInitialized( display );
    //control: enabled
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    tabfolder.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    tabfolder.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( tabfolder );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    tabfolder.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    tabfolder.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    tabfolder.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    tabfolder.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    tabfolder.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    tabfolder.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( null, tabfolder.getToolTipText() );
    Fixture.clearPreserved();
    tabfolder.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    assertEquals( "some text", tabfolder.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners   Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    tabfolder.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( tabfolder, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tabfolder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testSelectionWithoutListener() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    Control control0 = new Button( folder, SWT.PUSH );
    item0.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    shell.open();

    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String folderId = WidgetUtil.getId( folder );
    String item1Id = WidgetUtil.getId( item1 );
    // Run life cycle once to reduce markup that is written for the actual
    // request under test
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread( );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread( );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item1Id );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, folder.getSelectionIndex() );
    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
  }
  
  public void testSelectionWithListener() {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    Control control0 = new Button( folder, SWT.PUSH );
    item0.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    shell.open();
    folder.addSelectionListener( new SelectionListener() {

      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
      }

      public void widgetDefaultSelected( final SelectionEvent event ) {
        events.add( event );
      }
    } );
    
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String item1Id = WidgetUtil.getId( item1 );
    String folderId = WidgetUtil.getId( folder );
    // Run life cycle once to reduce markup that is written for the actual
    // request under test
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread( );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread( );
    
    events.clear();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item1Id );
    
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, folder.getSelectionIndex() );
    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
    assertEquals( 1, events.size() );
    SelectionEvent event = ( SelectionEvent )events.get( 0 );
    assertSame( item1, event.item );
    assertSame( folder, event.widget );
    assertTrue( event.doit );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertEquals( 0, event.detail );
    assertNull( event.text );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
