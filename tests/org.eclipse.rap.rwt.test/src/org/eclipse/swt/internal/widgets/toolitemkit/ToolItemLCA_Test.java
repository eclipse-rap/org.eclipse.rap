/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolItemLCA_Test {

  private Display display;
  private Shell shell;
  private ToolBar toolbar;
  private ToolItem toolitem;
  private ToolItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    toolbar = new ToolBar( shell, SWT.NONE );
    toolitem = new ToolItem( toolbar, SWT.PUSH );
    lca = new ToolItemLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    final ToolItem item = new ToolItem( toolbar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertTrue( event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertTrue( item.getSelection() );
        assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
      }
    } );
    shell.open();

    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.TRUE );
    Map<String,Object> params = new HashMap<String,Object>();
    params.put( "altKey", "true" );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, params );
    Fixture.readDataAndProcessAction( display );

    assertTrue( wasEventFired[ 0 ] );
  }

  @Test
  public void testDropDownItemSelected() {
    final boolean[] wasEventFired = { false };
    final ToolItem item = new ToolItem( toolbar, SWT.DROP_DOWN );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertTrue( event.doit );
        assertEquals( SWT.ARROW, event.detail );
        Rectangle itemBounds = item.getBounds();
        assertEquals( itemBounds.x, event.x );
        assertEquals( itemBounds.y + itemBounds.height, event.y );
        assertEquals( itemBounds.width, event.width );
        assertEquals( itemBounds.height, event.height );
        assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
      }
    } );
    shell.open();

    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.TRUE );
    Map<String,Object> params = new HashMap<String,Object>();
    params.put( "altKey", "true" );
    params.put( ClientMessageConst.EVENT_PARAM_DETAIL, "arrow" );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, params );
    Fixture.readDataAndProcessAction( display );

    assertTrue( wasEventFired[ 0 ] );
  }

  @Test
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

  @Test
  public void testReadData() {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.TRUE );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, null );
    WidgetUtil.getLCA( item ).readData( item );

    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );

    Fixture.fakeNewRequest();
    Fixture.fakeSetParameter( getId( item ), "selection", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, null );
    WidgetUtil.getLCA( item ).readData( item );

    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getSelection() ) );
  }

  @Test
  public void testGetImage() throws IOException {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Image enabledImage = createImage( Fixture.IMAGE1 );
    Image disabledImage = createImage( Fixture.IMAGE2 );
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

  @Test
  public void testRenderCreatePush() throws IOException {
    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "PUSH" ) );
  }

  @Test
  public void testRenderCreateCheck() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.CHECK );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CHECK" ) );
  }

  @Test
  public void testRenderCreateRadio() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.RADIO );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "RADIO" ) );
  }

  @Test
  public void testRenderCreateDropDown() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.DROP_DOWN );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "DROP_DOWN" ) );
  }

  @Test
  public void testRenderCreateSeparator() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.SEPARATOR );

    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( "rwt.widgets.ToolItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SEPARATOR" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertEquals( WidgetUtil.getId( toolitem.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialEnabled() throws IOException {
    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "enabled" ) == -1 );
  }

  @Test
  public void testRenderEnabled() throws IOException {
    toolitem.setEnabled( false );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( toolitem, "enabled" ) );
  }

  @Test
  public void testRenderEnabledUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "enabled" ) );
  }

  @Test
  public void testRenderInitialToolTip() throws IOException {
    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "toolTip" ) == -1 );
  }

  @Test
  public void testRenderToolTip() throws IOException {
    toolitem.setToolTipText( "foo" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "toolTip" ) );
  }

  @Test
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "toolTip" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    toolitem.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( toolitem, "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "customVariant" ) );
  }

  @Test
  public void testRenderInitialVisible() throws IOException {
    toolbar.setSize( 20, 25 );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "visible" ) == -1 );
  }

  @Test
  public void testRenderVisible() throws IOException {
    toolbar.setSize( 20, 25 );

    toolitem.setText( "foo bar" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( toolitem, "visible" ) );
  }

  @Test
  public void testRenderVisibleUnchanged() throws IOException {
    toolbar.setSize( 20, 25 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "foo bar" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "visible" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    toolitem.setText( "foo" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "text" ) );
  }

  @Test
  public void testRenderText_WithMnemonic() throws IOException {
    toolitem.setText( "fo&o" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "text" ) );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException, JSONException {
    Image image = createImage( Fixture.IMAGE_100x50 );

    toolitem.setImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( toolitem, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = createImage( Fixture.IMAGE_100x50 );

    toolitem.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "image" ) );
  }

  @Test
  public void testRenderInitialHotImage() throws IOException {
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "hotImage" ) );
  }

  @Test
  public void testRenderHotImage() throws IOException, JSONException {
    Image image = createImage( Fixture.IMAGE_100x50 );

    toolitem.setHotImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( toolitem, "hotImage" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  @Test
  public void testRenderHotImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = createImage( Fixture.IMAGE_100x50 );

    toolitem.setHotImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "hotImage" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = createImage( Fixture.IMAGE_100x50 );
    toolitem.setImage( image );

    Fixture.preserveWidgets();
    toolitem.setImage( null );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( toolitem, "image" ) );
  }

  @Test
  public void testRenderInitialControl() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.SEPARATOR );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "control" ) == -1 );
  }

  @Test
  public void testRenderControl() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.SEPARATOR );
    Composite control = new Composite( toolbar, SWT.NONE );
    String controlId = WidgetUtil.getId( control );

    toolitem.setControl( control );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( controlId, message.findSetProperty( toolitem, "control" ) );
  }

  @Test
  public void testRenderControlUnchanged() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.SEPARATOR );
    Composite control = new Composite( toolbar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setControl( control );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "control" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.CHECK );

    lca.render( toolitem );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolitem );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.CHECK );

    toolitem.setSelection( true );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( toolitem, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    toolitem = new ToolItem( toolbar, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setSelection( true );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "selection" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( toolitem, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    toolitem.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.removeListener( SWT.Selection, listener );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( toolitem, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Fixture.preserveWidgets();

    toolitem.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( toolitem, "Selection" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    toolitem.setText( "te&st" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( toolitem, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonic_OnTextChange() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "te&st" );
    Fixture.preserveWidgets();
    toolitem.setText( "aa&bb" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( toolitem, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    toolitem.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( toolitem, "mnemonicIndex" ) );
  }

  private Image createImage( String imagePath ) throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( imagePath );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

}
