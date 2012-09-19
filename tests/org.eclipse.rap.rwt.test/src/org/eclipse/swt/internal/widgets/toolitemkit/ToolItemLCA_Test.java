/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class ToolItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ToolBar toolbar;
  private ToolItemLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    toolbar = new ToolBar( shell, SWT.NONE );
    lca = new ToolItemLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    final ToolItem item = new ToolItem( toolbar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
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

    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.TRUE );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.readDataAndProcessAction( display );

    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRadioItemSelected() {
    ToolItem item0 = new ToolItem( toolbar, SWT.RADIO );
    item0.setSelection( true );
    ToolItem item1 = new ToolItem( toolbar, SWT.RADIO );

    Fixture.fakeSetParameter( getId( item1 ), "selection", Boolean.TRUE );
    Fixture.fakeSetParameter( getId( item0 ), "selection", Boolean.FALSE );
    Fixture.readDataAndProcessAction( display );

    assertFalse( item0.getSelection() );
    assertTrue( item1.getSelection() );
  }

  public void testReadData() {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.TRUE );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    WidgetUtil.getLCA( item ).readData( item );

    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    WidgetUtil.getLCA( item ).readData( item );

    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getSelection() ) );
  }

  public void testGetImage() {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Image enabledImage = Graphics.getImage( Fixture.IMAGE1 );
    Image disabledImage = Graphics.getImage( Fixture.IMAGE2 );
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

  public void testRenderCreatePush() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "PUSH" ) );
  }

  public void testRenderCreateCheck() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.CHECK );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CHECK" ) );
  }

  public void testRenderCreateRadio() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.RADIO );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "RADIO" ) );
  }

  public void testRenderCreateDropDown() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.DROP_DOWN );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "DROP_DOWN" ) );
  }

  public void testRenderCreateSeparator() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.SEPARATOR );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SEPARATOR" ) );
  }

  public void testRenderParent() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( WidgetUtil.getId( toolitem.getParent() ), operation.getParent() );
  }

  public void testRenderInitialEnabled() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "enabled" ) == -1 );
  }

  public void testRenderEnabled() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    toolitem.setEnabled( false );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( toolitem, "enabled" ) );
  }

  public void testRenderEnabledUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "enabled" ) );
  }

  public void testRenderInitialToolTip() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "toolTip" ) == -1 );
  }

  public void testRenderToolTip() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    toolitem.setToolTipText( "foo" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "toolTip" ) );
  }

  public void testRenderToolTipUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "toolTip" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    toolitem.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( toolitem, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "customVariant" ) );
  }

  public void testRenderInitialVisible() throws IOException {
    toolbar.setSize( 20, 25 );
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "visible" ) == -1 );
  }

  public void testRenderVisible() throws IOException {
    toolbar.setSize( 20, 25 );
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    toolitem.setText( "foo bar" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( toolitem, "visible" ) );
  }

  public void testRenderVisibleUnchanged() throws IOException {
    toolbar.setSize( 20, 25 );
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "foo bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "visible" ) );
  }

  public void testRenderInitialText() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    toolitem.setText( "foo" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    toolitem.setImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( toolitem, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    toolitem.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "image" ) );
  }

  public void testRenderInitialHotImage() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );

    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "hotImage" ) );
  }

  public void testRenderHotImage() throws IOException, JSONException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    toolitem.setHotImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( toolitem, "hotImage" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderHotImageUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    toolitem.setHotImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "hotImage" ) );
  }

  public void testRenderImageReset() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    toolitem.setImage( image );

    Fixture.preserveWidgets();
    toolitem.setImage( null );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( toolitem, "image" ) );
  }

  public void testRenderInitialControl() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.SEPARATOR );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "control" ) == -1 );
  }

  public void testRenderControl() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.SEPARATOR );
    Composite control = new Composite( toolbar, SWT.NONE );
    String controlId = WidgetUtil.getId( control );

    toolitem.setControl( control );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( controlId, message.findSetProperty( toolitem, "control" ) );
  }

  public void testRenderControlUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.SEPARATOR );
    Composite control = new Composite( toolbar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setControl( control );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "control" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.CHECK );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.CHECK );

    toolitem.setSelection( true );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( toolitem, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    ToolItem toolitem = new ToolItem( toolbar, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setSelection( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "selection" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( toolitem, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    SelectionListener listener = new SelectionAdapter() { };
    toolitem.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.removeSelectionListener( listener );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( toolitem, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    ToolItem toolitem = new ToolItem( toolbar, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( toolitem, "selection" ) );
  }
}
