/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import static org.eclipse.rap.rwt.internal.resources.TestUtil.assertArrayEquals;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;


public class CoolItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolItem item;
  private CoolItemLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    item = new CoolItem( bar, SWT.NONE );
    lca = new CoolItemLCA();
    Fixture.fakeNewRequest( display );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Button button = new Button( bar, SWT.NONE );
    Fixture.markInitialized( item );
    item.setControl( button );
    item.setSize( 30, 20 );
    Rectangle rectangle = new Rectangle( 0, 0, item.getSize().x, item.getSize().y );
    lca.preserveValues( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( button, adapter.getPreserved( CoolItemLCA.PROP_CONTROL ) );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    item.setControl( null );
    lca.preserveValues( item );
    assertNull( adapter.getPreserved( CoolItemLCA.PROP_CONTROL ) );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.CoolItem", operation.getType() );
    assertArrayEquals( new String[] { "NONE" }, operation.getStyles() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( bar ), operation.getParent() );
  }

  public void testRenderVertical() throws IOException {
    item = new CoolItem( bar, SWT.VERTICAL );
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertArrayEquals( new String[] { "VERTICAL" }, operation.getStyles() );
  }

  public void testRenderBounds() throws IOException, JSONException {
    item.setSize( 10, 20 );
    lca.renderInitialization( item );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    String bounds = ( ( JSONArray )operation.getProperty( "bounds" ) ).join( "," );
    assertEquals( "0,0,0,20", bounds );
  }

  public void testRenderControl() throws Exception {
    Button button = new Button( bar, SWT.NONE );
    Fixture.markInitialized( item );

    item.setControl( button );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( item, "control" );
    assertEquals( WidgetUtil.getId( button ), operation.getProperty( "control" ) );
  }

}
