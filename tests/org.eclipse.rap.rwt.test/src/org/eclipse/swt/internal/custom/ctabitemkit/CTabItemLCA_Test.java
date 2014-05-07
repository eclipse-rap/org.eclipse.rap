/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ctabitemkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.testfixture.Fixture.getProtocolMessage;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CTabItemLCA_Test {

  private Display display;
  private Shell shell;
  private CTabFolder folder;
  private CTabItem item;
  private CTabItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    folder = new CTabFolder( shell, SWT.NONE );
    folder.setSize( 150, 150 );
    item = new CTabItem( folder, SWT.NONE );
    lca = new CTabItemLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.CTabItem", operation.getType() );
    assertEquals( 0, operation.getProperties().get( "index" ).asInt() );
  }

  @Test
  public void testRenderCreateWithClose() throws IOException {
    CTabItem item = new CTabItem( folder, SWT.CLOSE );

    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( getStyles( operation ).contains( "CLOSE" ) );
  }

  @Test
  public void testRenderIndex() throws IOException {
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE, 1 );

    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( 1, operation.getProperties().get( "index" ).asInt() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( getId( item.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( item );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( item ), operation.getTarget() );
  }

  @Test
  public void testRenderIntialToolTipMarkupEnabled() throws IOException {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    lca.renderChanges( item );

    TestMessage message = getProtocolMessage();
    assertTrue( "foo", message.findSetProperty( item, "toolTipMarkupEnabled" ).asBoolean() );
  }

  @Test
  public void testRenderToolTipMarkupEnabled() throws IOException {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    Fixture.markInitialized( item );

    lca.renderChanges( item );

    TestMessage message = getProtocolMessage();
    assertNull( message.findSetOperation( item, "toolTipMarkupEnabled" ) );
  }

  @Test
  public void testRenderInitialToolTip() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "toolTip" ) );
  }

  @Test
  public void testRenderToolTip() throws IOException {
    item.setToolTipText( "foo" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( item, "toolTip" ).asString() );
  }

  @Test
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "toolTip" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "customVariant" ) );
  }

  @Test
  public void testRenderInitialBounds() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findCreateProperty( item, "bounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBounds() throws IOException {
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray bounds = ( JsonArray )message.findSetProperty( item, "bounds" );
    assertTrue( bounds.get( 2 ).asInt() > 0 );
    assertTrue( bounds.get( 3 ).asInt() > 0 );
  }

  @Test
  public void testRenderBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "bounds" ) );
  }

  @Test
  public void testRenderInitialFont() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "font" ) );
  }

  @Test
  public void testRenderFont() throws IOException {
    item.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 20, true, false ]" );
    assertEquals( expected, message.findSetProperty( item, "font" ) );
  }

  @Test
  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "font" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    item.setText( "foo" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( item, "text" ).asString() );
  }

  @Test
  public void testRenderText_WithMnemonic() throws IOException {
    item.setText( "foo&bar" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foobar", message.findSetProperty( item, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    item.setImage( image );
    folder.setSelection( item );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( item, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    item.setImage( image );
    folder.setSelection( item );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    item.setImage( image );
    folder.setSelection( item );

    Fixture.preserveWidgets();
    item.setImage( null );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( item, "image" ) );
  }

  @Test
  public void testRenderInitialShowing() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "showing" ) );
  }

  @Test
  public void testRenderShowing() throws IOException {
    CTabItem lastItem = new CTabItem( folder, SWT.NONE );

    item.setText( "foo bar foo bar" );
    lca.renderChanges( lastItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( lastItem, "showing" ) );
  }

  @Test
  public void testRenderShowingUnchanged() throws IOException {
    CTabItem lastItem = new CTabItem( folder, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( lastItem );

    item.setText( "foo bar foo bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( lastItem );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( lastItem, "showing" ) );
  }

  @Test
  public void testRenderInitialShowClose() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "showClose" ) );
  }

  @Test
  public void testRenderShowClose() throws IOException {
    item.setShowClose( true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( item, "showClose" ) );
  }

  @Test
  public void testRenderShowCloseUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setShowClose( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "showClose" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    item.setText( "te&st" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( item, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndex_OnTextChange() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( "te&st" );
    Fixture.preserveWidgets();
    item.setText( "aa&bb" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( item, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderData() throws IOException {
    registerDataKeys( new String[]{ "foo", "bar" } );
    item.setData( "foo", "string" );
    item.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( item, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    registerDataKeys( new String[]{ "foo" } );
    item.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

}
