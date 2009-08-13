/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ToolItemLCA_Test extends TestCase {

  public void testCheckPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.CHECK );
    RWTFixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    item.setSelection( true );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testDropDownPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.DROP_DOWN );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testPushPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.PUSH );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testRadioPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.RADIO );
    RWTFixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    RWTFixture.clearPreserved();
    item.setSelection( true );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.SELECTION_INDICES ) );
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testSeparatorPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.SEPARATOR );
    Button button = new Button( tb, SWT.PUSH );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( null, adapter.getPreserved( Props.CONTROL ) );
    item.setControl( button );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    assertEquals( button, adapter.getPreserved( Props.CONTROL ) );
    RWTFixture.clearPreserved();
    testPreserveValues( display, item );
    display.dispose();
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, item.getSelection() );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String toolItemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( toolItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, toolItemId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, wasEventFired[ 0 ] );
  }
  
  public void testRadioItemSelected() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem item0 = new ToolItem( toolBar, SWT.RADIO );
    item0.setSelection( true );
    ToolItem item1 = new ToolItem( toolBar, SWT.RADIO );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String item1Id = WidgetUtil.getId( item1 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, item1Id );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertFalse( item0.getSelection() );
    assertTrue( item1.getSelection() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( tb, SWT.CHECK );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( item );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ToolItemLCA itemLCA = new ToolItemLCA();
    item.setText( "benny" );
    itemLCA.renderChanges( item );
    String expected = "setLabel( \"benny\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    item.setSelection( true );
    itemLCA.renderChanges( item );
    assertTrue( Fixture.getAllMarkup().endsWith( "setChecked( true );" ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    itemLCA.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "false" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getSelection() ) );
  }

  public void testGetImage() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );

    Image enabledImage = Graphics.getImage( RWTFixture.IMAGE1 );
    Image disabledImage = Graphics.getImage( RWTFixture.IMAGE2 );
    assertNull( ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    item.setDisabledImage( disabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setEnabled( false );
    assertSame( disabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setDisabledImage( null );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );
  }
  
  public void testIndexOnInitialize() throws Exception {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.NONE );
    final ToolItem item1 = new ToolItem( tb, SWT.PUSH );
    final ToolItem item2 = new ToolItem( tb, SWT.PUSH );
    shell.open();
    ToolItemLCA itemLCA = new ToolItemLCA();
    itemLCA.renderInitialization( item1 );
    itemLCA.renderInitialization( item2 );
    String parent = "wm.findWidgetById( \"" + WidgetUtil.getId( tb )+ "\" )";
    String expected = "createPush( \""
                      + WidgetUtil.getId( item1 )
                      + "\", "
                      + parent
                      + ", 0, false );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    expected = "createPush( \""
               + WidgetUtil.getId( item2 )
               + "\", "
               + parent
               + ", 1, false );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    final ToolItem item0 = new ToolItem( tb, SWT.PUSH, 0 );
    itemLCA.renderInitialization( item0 );
    expected = "createPush( \""
               + WidgetUtil.getId( item0 )
               + "\", "
               + parent
               + ", 0, false );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testRadioGroupWithIndex() throws Exception {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.NONE );
    final ToolItem item = new ToolItem( tb, SWT.RADIO );
    shell.open();
    ToolItemLCA itemLCA = new ToolItemLCA();
    itemLCA.renderInitialization( item );
    String parent = "wm.findWidgetById( \"" + WidgetUtil.getId( tb ) + "\" )";
    String expected = "org.eclipse.swt.ToolItemUtil.createRadio( \""
                      + WidgetUtil.getId( item )
                      + "\", "
                      + parent
                      + ", 0, null, null );";    
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // second radio item, should be in the group with first item
    RWTFixture.fakeNewRequest();
    ToolItem item2 = new ToolItem( tb, SWT.RADIO );
    itemLCA.renderInitialization( item2 );
    String neighbour = "wm.findWidgetById( \""
                       + WidgetUtil.getId( item )
                       + "\" )";
    expected = "org.eclipse.swt.ToolItemUtil.createRadio( \""
               + WidgetUtil.getId( item2 )
               + "\", "
               + parent
               + ", 1, null, "
               + neighbour
               + " );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // add third item to the left, should be in same radio group
    RWTFixture.fakeNewRequest();
    ToolItem item3 = new ToolItem( tb, SWT.RADIO, 0 );
    itemLCA.renderInitialization( item3 );
    neighbour = "wm.findWidgetById( \"" + WidgetUtil.getId( item ) + "\" )";
    expected = "org.eclipse.swt.ToolItemUtil.createRadio( \""
               + WidgetUtil.getId( item3 )
               + "\", "
               + parent
               + ", 0, null, "
               + neighbour
               + " );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // add forth item to the right, should also be in the same group
    RWTFixture.fakeNewRequest();
    ToolItem item4 = new ToolItem( tb, SWT.RADIO, 3 );
    itemLCA.renderInitialization( item4 );
    neighbour = "wm.findWidgetById( \"" + WidgetUtil.getId( item2 ) + "\" )";
    expected = "org.eclipse.swt.ToolItemUtil.createRadio( \""
               + WidgetUtil.getId( item4 )
               + "\", "
               + parent
               + ", 3, null, "
               + neighbour
               + " );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // add spacer item to check both directions
    new ToolItem( tb, SWT.PUSH, 0 );
    
    // add fifth item, after push but left of button group, part of radio group
    RWTFixture.fakeNewRequest();
    ToolItem item5 = new ToolItem( tb, SWT.RADIO, 1 );
    itemLCA.renderInitialization( item5 );
    neighbour = "wm.findWidgetById( \"" + WidgetUtil.getId( item3 ) + "\" )";
    expected = "org.eclipse.swt.ToolItemUtil.createRadio( \""
               + WidgetUtil.getId( item5 )
               + "\", "
               + parent
               + ", 1, null, "
               + neighbour
               + " );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  // TODO [bm]: test for workaround for bug 286306
  //            we need to count the DROP_DOWN twice for a proper index
  //            as it consists of two
  //            widgets on the client-side. This needs to be removed once we
  //            a single-widget DROP_DOWN ToolItem in place
  public void testIndexOnInitializeWithDropDown() throws Exception {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.NONE );
    final ToolItem item1 = new ToolItem( tb, SWT.DROP_DOWN );
    final ToolItem item2 = new ToolItem( tb, SWT.PUSH );
    final ToolItem item3 = new ToolItem( tb, SWT.DROP_DOWN );
    final ToolItem item4 = new ToolItem( tb, SWT.PUSH );
    shell.open();
    ToolItemLCA itemLCA = new ToolItemLCA();
    itemLCA.renderInitialization( item1 );
    itemLCA.renderInitialization( item2 );
    itemLCA.renderInitialization( item3 );
    itemLCA.renderInitialization( item4 );
    String parent = "wm.findWidgetById( \"" + WidgetUtil.getId( tb )+ "\" )";
    String expected1 = "org.eclipse.swt.ToolItemUtil.createDropDown( \""
                      + WidgetUtil.getId( item1 )
                      + "\", "
                      + parent
                      + ", 0, false );";
    String expected2 = "org.eclipse.swt.ToolItemUtil.createPush( \""
                       + WidgetUtil.getId( item2 )
                       + "\", "
                       + parent
                       + ", 2, false );";
    String expected3 = "org.eclipse.swt.ToolItemUtil.createDropDown( \""
                       + WidgetUtil.getId( item3 )
                       + "\", "
                       + parent
                       + ", 3, false );";
    String expected4 = "org.eclipse.swt.ToolItemUtil.createPush( \""
                       + WidgetUtil.getId( item4 )
                       + "\", "
                       + parent
                       + ", 5, false );";
    assertEquals( expected1 + expected2 + expected3 + expected4,
                  Fixture.getAllMarkup() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private void testPreserveValues( final Display display, final ToolItem item )
  {
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    assertEquals( "", adapter.getPreserved( Props.TOOLTIP ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    item.addSelectionListener( selectionListener );
    item.setText( "some text" );
    item.setEnabled( false );
    item.setToolTipText( "tooltip text" );
    ToolBar toolbar = item.getParent();
    Menu contextMenu = new Menu( toolbar );
    toolbar.setMenu( contextMenu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    if( ( item.getStyle() & SWT.SEPARATOR ) == 0 ) {
      assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    }
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( "tooltip text", adapter.getPreserved( Props.TOOLTIP ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    assertEquals( contextMenu, adapter.getPreserved( Props.MENU ) );
  }
}
