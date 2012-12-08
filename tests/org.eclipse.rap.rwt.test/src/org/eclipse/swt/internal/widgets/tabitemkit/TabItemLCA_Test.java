/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class TabItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private TabFolder folder;
  private TabItem item;
  private TabItemLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    folder = new TabFolder( shell, SWT.NONE );
    item = new TabItem( folder, SWT.NONE );
    lca = new TabItemLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    new TabItem( folder, SWT.NONE );

    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Fixture.preserveWidgets();
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "", adapter.getPreserved( "toolTip" ) );
    Fixture.clearPreserved();
    folder.setSelection( 1 );
    item.setText( "some text" );
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    item.setToolTipText( "tooltip text" );
    Fixture.preserveWidgets();
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Graphics.getImage( Fixture.IMAGE1 ), adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "tooltip text", adapter.getPreserved( "toolTip" ) );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.TabItem", operation.getType() );
    assertEquals( WidgetUtil.getId( item ), operation.getProperty( "id" ) );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( item.getParent() ), operation.getParent() );
  }

  public void testRenderInitialToolTip() throws IOException {
    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "toolTip" ) == -1 );
  }

  public void testRenderToolTip() throws IOException {
    item.setToolTipText( "foo" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( item, "toolTip" ) );
  }

  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "toolTip" ) );
  }

  public void testRenderInitialText() throws IOException {
    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    item.setText( "foo" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( item, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    item.setImage( image );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    item.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    item.setImage( image );

    Fixture.preserveWidgets();
    item.setImage( null );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( item, "image" ) );
  }

  public void testRenderInitialControl() throws IOException {
    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "control" ) == -1 );
  }

  public void testRenderControl() throws IOException {
    Composite content = new Composite( folder, SWT.NONE );
    String contentId = WidgetUtil.getId( content );

    item.setControl( content );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( contentId, message.findSetProperty( item, "control" ) );
  }

  public void testRenderControlUnchanged() throws IOException {
    Composite content = new Composite( folder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setControl( content );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "control" ) );
  }

}
