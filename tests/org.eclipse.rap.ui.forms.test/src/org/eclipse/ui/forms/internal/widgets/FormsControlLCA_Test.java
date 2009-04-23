/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class FormsControlLCA_Test extends TestCase {

  protected void testPreserveControlProperties( final Control control ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    control.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    control.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    control.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( control );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    control.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    control.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    control.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    control.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
