/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TabItemLCA_Test extends TestCase {

  private static final String PROP_SELECTED = "selected";
  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );

    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, adapter.getPreserved( PROP_SELECTED ) );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "", adapter.getPreserved( "toolTipText" ) );
    Fixture.clearPreserved();
    tabFolder.setSelection( 1 );
    item.setText( "some text" );
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    item.setToolTipText( "tooltip text" );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, adapter.getPreserved( PROP_SELECTED ) );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Graphics.getImage( Fixture.IMAGE1 ),
                  adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "tooltip text", adapter.getPreserved( "toolTipText" ) );
  }

  public void testReadData() {
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    Fixture.readDataAndProcessAction( item );
    assertSame( item, tabFolder.getSelection()[ 0 ] );
  }

  public void testRenderChanges() throws IOException {
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    new TabItem( tabFolder, SWT.NONE );
    TabItem item = new TabItem( tabFolder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    AbstractWidgetLCA adapter = WidgetUtil.getLCA( item );
    item.setToolTipText( "tooltip text" );
    adapter.renderChanges( item );
    String expected = "setToolTip( ";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteControlJsParent() {
    TabFolder tabFolder = new TabFolder( shell, SWT.TOP );
    TabItem item = new TabItem( tabFolder, SWT.NONE );
    String itemId = WidgetUtil.getId( item );
    Control control = new Label( tabFolder, SWT.NONE );
    String controlId = WidgetUtil.getId( control );
    item.setControl( control );
    Fixture.fakeNewRequest( display );

    Fixture.executeLifeCycleFromServerThread();

    StringBuffer expected = new StringBuffer();
    expected.append( "wm.setParent( wm.findWidgetById( \"" );
    expected.append( controlId );
    expected.append( "\" ), \"" );
    expected.append( itemId );
    expected.append( "pg\" );" );
    assertTrue( Fixture.getAllMarkup().endsWith( expected.toString() ) );
  }
}
