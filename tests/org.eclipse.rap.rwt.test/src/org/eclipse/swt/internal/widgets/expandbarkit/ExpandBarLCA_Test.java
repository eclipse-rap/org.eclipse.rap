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
package org.eclipse.swt.internal.widgets.expandbarkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ExpandBarLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    ExpandItem expandItem = createExpandItems( expandBar );
    expandBar.setSize( expandBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    Fixture.markInitialized( display );
    // Show Vertical Scrollbar
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( expandBar );
    Boolean showVScrollbar
      = ( Boolean )adapter.getPreserved( ExpandBarLCA.PROP_SHOW_VSCROLLBAR );
    assertEquals( Boolean.FALSE, showVScrollbar );
    Fixture.clearPreserved();
    expandItem.setExpanded( true );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    showVScrollbar = ( Boolean )adapter.getPreserved( ExpandBarLCA.PROP_SHOW_VSCROLLBAR );
    assertEquals( Boolean.TRUE, showVScrollbar );
    Fixture.clearPreserved();
    // Bottom Spacing Bounds
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    Rectangle bottomSpacingBounds = ( Rectangle )adapter.getPreserved( ExpandBarLCA.PROP_BOTTOM_SPACING_BOUNDS );
    assertEquals( new Rectangle( 4, 209, 10, 4 ), bottomSpacingBounds );
    Fixture.clearPreserved();
    expandItem.setExpanded( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    bottomSpacingBounds = ( Rectangle )adapter.getPreserved( ExpandBarLCA.PROP_BOTTOM_SPACING_BOUNDS );
    assertEquals( new Rectangle( 4, 56, 10, 4 ), bottomSpacingBounds );
    Fixture.clearPreserved();
    expandBar.setSpacing( 8 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    bottomSpacingBounds = ( Rectangle )adapter.getPreserved( ExpandBarLCA.PROP_BOTTOM_SPACING_BOUNDS );
    assertEquals( new Rectangle( 8, 64, 10, 8 ), bottomSpacingBounds );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    expandBar.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( expandBar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    expandBar.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    expandBar.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    expandBar.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    expandBar.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
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
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
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
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setExpanded( false );
    return item;
  }
}
