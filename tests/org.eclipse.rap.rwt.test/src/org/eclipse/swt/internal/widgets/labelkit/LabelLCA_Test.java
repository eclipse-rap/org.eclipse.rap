/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LabelLCA_Test {

  private Display display;
  private Shell shell;
  private Label label;
  private LabelLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    label = new Label( shell, SWT.NONE );
    lca = new LabelLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( new Label( shell, SWT.NONE ) );
    ControlLCATestUtil.testCommonControlProperties( new Label( shell, SWT.SEPARATOR ) );
  }

  @Test
  public void testStandardPreserveValues() throws IOException {
    Fixture.markInitialized( display );
    //Text
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    label.setText( "xyz" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    //Image
    Image image = TestUtil.createImage( display, Fixture.IMAGE1 );
    label.setImage( image );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    //aligment
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    String alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "left", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "right", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "center", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "left", alignment );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    label.setText( "test" );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithQuotationMarks() throws IOException {
    label.setText( "te\"s't" );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithMnemonic() throws IOException {
    label.setText( "te&st" );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithNewlines() throws IOException {
    label.setText( "\ntes\r\nt\n" );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setText( "foo" );

    Fixture.preserveWidgets();
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    label.dispose();
    LabelLCA labelLCA = new LabelLCA();

    labelLCA.renderDispose( label );

    TestMessage message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( label ), operation.getTarget() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    label = new Label( shell, SWT.WRAP );

    lca.renderInitialization( label );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Label", operation.getType() );
    assertTrue( getStyles( operation ).contains( "WRAP" ) );
    assertFalse( operation.getProperties().names().contains( "markupEnabled" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( label );
    lca.renderInitialization( label );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof LabelOperationHandler );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler_onSeparator() throws IOException {
    label = new Label( shell, SWT.SEPARATOR );
    String id = getId( label );
    lca.renderInitialization( label );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof LabelOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    LabelOperationHandler handler = spy( new LabelOperationHandler( label ) );
    getRemoteObject( getId( label ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( label ), "Help", new JsonObject() );
    lca.readData( label );

    verify( handler ).handleNotifyHelp( label, new JsonObject() );
  }

  @Test
  public void testRenderCreateWithMarkupEnabled() throws IOException {
    label = new Label( shell, SWT.WRAP );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.renderInitialization( label );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "markupEnabled" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    label.setImage( image );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( label, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    label.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    label.setImage( image );

    Fixture.preserveWidgets();
    label.setImage( null );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( label, "image" ) );
  }

  @Test
  public void testRenderInitialAlignment() throws IOException {
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  @Test
  public void testRenderAlignment() throws IOException {
    label.setAlignment( SWT.RIGHT );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( label, "alignment" ).asString() );
  }

  @Test
  public void testRenderAlignmentUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  @Test
  public void testRenderCreateSeparator() throws IOException {
    label = new Label( shell, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.VERTICAL );

    lca.renderInitialization( label );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Separator", operation.getType() );
    List<String> styles = getStyles( operation );
    assertTrue( styles.contains( "SHADOW_IN" ) );
    assertTrue( styles.contains( "VERTICAL" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    label.setText( "te&st" );
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( label, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderChanges_rendersClientListener() throws IOException {
    label.addListener( SWT.MouseEnter, new ClientListener( "" ) );

    lca.renderChanges( label );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( label, "addListener" ) );
  }

}
