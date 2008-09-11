/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expanditemkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ExpandItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ExpandItem expandItem = createExpandItems( expandBar );
    expandBar.setSize( expandBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    Image image1 = Graphics.getImage( RWTFixture.IMAGE1 );
    Image image2 = Graphics.getImage( RWTFixture.IMAGE2 );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( expandItem );
    Object text = adapter.getPreserved( ExpandItemLCA.PROP_TEXT );
    assertEquals( "What is your favorite icon?", text );
    Object image = adapter.getPreserved( ExpandItemLCA.PROP_IMAGE );
    assertEquals( image1, image );
    Object expanded = adapter.getPreserved( ExpandItemLCA.PROP_EXPANDED );
    assertEquals( Boolean.FALSE, expanded );
    Object headerHeight
      = adapter.getPreserved( ExpandItemLCA.PROP_HEADER_HEIGHT );
    assertEquals( ExpandItemLCA.DEFAULT_HEADER_HEIGHT, headerHeight );
    RWTFixture.clearPreserved();
    expandItem.setText( "Item text" );
    expandItem.setImage( Graphics.getImage( RWTFixture.IMAGE2 ) );
    expandItem.setExpanded( true );
    Font font = Graphics.getFont( "font", 30, SWT.BOLD );
    expandBar.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandItem );
    text = adapter.getPreserved( ExpandItemLCA.PROP_TEXT );
    assertEquals( "Item text", text );
    image = adapter.getPreserved( ExpandItemLCA.PROP_IMAGE );
    assertEquals( image2, image );
    expanded = adapter.getPreserved( ExpandItemLCA.PROP_EXPANDED );
    assertEquals( Boolean.TRUE, expanded );
    headerHeight = adapter.getPreserved( ExpandItemLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( 34 ), headerHeight );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testExpandEvent() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final ExpandBar expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    final ExpandItem expandItem = createExpandItems( expandBar );
    final StringBuffer log = new StringBuffer();
    ExpandListener listener = new ExpandListener() {

      public void itemCollapsed( final ExpandEvent event ) {
        assertEquals( expandBar, event.getSource() );
        assertEquals( expandItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "collapsed" );
      }

      public void itemExpanded( final ExpandEvent event ) {
        assertEquals( expandBar, event.getSource() );
        assertEquals( expandItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "expanded" );
      }
    };
    expandBar.addExpandListener( listener );
    String expandItemId = WidgetUtil.getId( expandItem );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, expandItemId );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( "expanded", log.toString() );
    log.setLength( 0 );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, null );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_COLLAPSED, expandItemId );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( "collapsed", log.toString() );
  }

  public void testExpandCollapse() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final ExpandBar expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    final ExpandItem expandItem = createExpandItems( expandBar );
    String expandItemId = WidgetUtil.getId( expandItem );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_EXPANDED, expandItemId );
    RWTFixture.readDataAndProcessAction( expandItem );
    assertEquals( true, expandItem.getExpanded() );
    Fixture.fakeRequestParam( ExpandItemLCA.EVENT_ITEM_COLLAPSED, expandItemId );
    RWTFixture.readDataAndProcessAction( expandItem );
    assertEquals( false, expandItem.getExpanded() );
  }

  private ExpandItem createExpandItems( final ExpandBar expandBar ) {
    Display display = expandBar.getDisplay();
    // First item
    Composite composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout() );
    new Button( composite, SWT.PUSH ).setText( "SWT.PUSH" );
    new Button( composite, SWT.RADIO ).setText( "SWT.RADIO" );
    new Button( composite, SWT.CHECK ).setText( "SWT.CHECK" );
    new Button( composite, SWT.TOGGLE ).setText( "SWT.TOGGLE" );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE, 0 );
    item.setText( "What is your favorite button?" );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setExpanded( false );
    // Second item
    composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Image img = display.getSystemImage( SWT.ICON_ERROR );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_ERROR" );
    img = display.getSystemImage( SWT.ICON_INFORMATION );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_INFORMATION" );
    img = display.getSystemImage( SWT.ICON_WARNING );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_WARNING" );
    img = display.getSystemImage( SWT.ICON_QUESTION );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_QUESTION" );
    item = new ExpandItem( expandBar, SWT.NONE, 1 );
    item.setText( "What is your favorite icon?" );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setExpanded( false );
    return item;
  }
}
