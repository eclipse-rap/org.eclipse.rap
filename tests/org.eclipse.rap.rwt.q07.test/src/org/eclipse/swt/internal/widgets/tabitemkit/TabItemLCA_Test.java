/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TabItemLCA_Test extends TestCase {

  private static final String PROP_SELECTED = "selected";
  
  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );

    RWTFixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.FALSE, adapter.getPreserved( PROP_SELECTED ) );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "", adapter.getPreserved( Props.TOOLTIP ) );
    RWTFixture.clearPreserved();
    tabFolder.setSelection( 1 );
    item.setText( "some text" );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    item.setToolTipText( "tooltip text" );
    RWTFixture.preserveWidgets();
    assertEquals( Boolean.TRUE, adapter.getPreserved( PROP_SELECTED ) );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Graphics.getImage( RWTFixture.IMAGE1 ),
                  adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "tooltip text", adapter.getPreserved( Props.TOOLTIP ) );
    display.dispose();
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    RWTFixture.readDataAndProcessAction( item );
    assertSame( item, tabFolder.getSelection()[ 0 ] );
  }
  
  public void testRenderChanges() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    AbstractWidgetLCA adapter = WidgetUtil.getLCA( item );
    item.setToolTipText( "tooltip text" );
    adapter.renderChanges( item );
    String expected = "setToolTip( ";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
