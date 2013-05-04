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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.WidgetDataUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


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
  public void testReadSelection_Check() {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Fixture.fakeSetProperty( getId( item ), "selection", true );
    lca.readData( item );

    assertTrue( item.getSelection() );
  }

  @Test
  public void testFireSelectionEvent_Check() {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );
    Listener listener = mock( Listener.class );
    item.addListener( SWT.Selection, listener );

    JsonObject params = new JsonObject().add( "altKey", "true" );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, params );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( null, event.item );
    assertSame( item, event.widget );
    assertTrue( event.doit );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
  }

  @Test
  public void testReadSelection_Radio() {
    ToolItem item = new ToolItem( toolbar, SWT.RADIO );

    Fixture.fakeSetProperty( getId( item ), "selection", true );
    lca.readData( item );

    assertTrue( item.getSelection() );
  }

  @Test
  public void testFireSelectionEvent_Radio() {
    ToolItem item = new ToolItem( toolbar, SWT.RADIO );
    Listener listener = mock( Listener.class );
    item.addListener( SWT.Selection, listener );

    JsonObject params = new JsonObject().add( "altKey", "true" );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, params );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( null, event.item );
    assertSame( item, event.widget );
    assertTrue( event.doit );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
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

    Fixture.fakeSetProperty( getId( item ), "selection", true );
    JsonObject params = new JsonObject()
      .add( "altKey", "true" )
      .add( ClientMessageConst.EVENT_PARAM_DETAIL, "arrow" );
    Fixture.fakeNotifyOperation( getId( item ), ClientMessageConst.EVENT_SELECTION, params );
    Fixture.readDataAndProcessAction( display );

    assertTrue( wasEventFired[ 0 ] );
  }

  @Test
  public void testGetImage() throws IOException {
    ToolItem item = new ToolItem( toolbar, SWT.CHECK );

    Image enabledImage = TestUtil.createImage( display, Fixture.IMAGE1 );
    Image disabledImage = TestUtil.createImage( display, Fixture.IMAGE2 );
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
    assertEquals( 0, operation.getProperty( "index" ).asInt() );
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
    assertEquals( 1, operation.getProperty( "index" ).asInt() );
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
    assertEquals( 1, operation.getProperty( "index" ).asInt() );
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
    assertEquals( 1, operation.getProperty( "index" ).asInt() );
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
    assertEquals( 1, operation.getProperty( "index" ).asInt() );
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
    assertEquals( JsonValue.FALSE, message.findSetProperty( toolitem, "enabled" ) );
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
    assertEquals( "foo", message.findSetProperty( toolitem, "toolTip" ).asString() );
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
    assertEquals( "variant_blue", message.findSetProperty( toolitem, "customVariant" ).asString() );
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
    assertEquals( JsonValue.FALSE, message.findSetProperty( toolitem, "visible" ) );
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
    assertEquals( "foo", message.findSetProperty( toolitem, "text" ).asString() );
  }

  @Test
  public void testRenderText_WithMnemonic() throws IOException {
    toolitem.setText( "fo&o" );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( toolitem, "text" ).asString() );
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
  public void testRenderImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    toolitem.setImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( toolitem, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

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
  public void testRenderHotImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    toolitem.setHotImage( image );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( toolitem, "hotImage" ) );
  }

  @Test
  public void testRenderHotImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

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
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    toolitem.setImage( image );

    Fixture.preserveWidgets();
    toolitem.setImage( null );
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( toolitem, "image" ) );
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
    assertEquals( controlId, message.findSetProperty( toolitem, "control" ).asString() );
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
    assertEquals( JsonValue.TRUE, message.findSetProperty( toolitem, "selection" ) );
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
    assertEquals( JsonValue.TRUE, message.findListenProperty( toolitem, "Selection" ) );
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
    assertEquals( JsonValue.FALSE, message.findListenProperty( toolitem, "Selection" ) );
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
    assertEquals( 2, message.findSetProperty( toolitem, "mnemonicIndex" ).asInt() );
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
    assertEquals( 2, message.findSetProperty( toolitem, "mnemonicIndex" ).asInt() );
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

  @Test
  public void testRenderData() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo", "bar" } );
    toolitem.setData( "foo", "string" );
    toolitem.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( toolitem, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    WidgetDataUtil.fakeWidgetDataWhiteList( new String[]{ "foo" } );
    toolitem.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( toolitem );

    Fixture.preserveWidgets();
    lca.renderChanges( toolitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

}
