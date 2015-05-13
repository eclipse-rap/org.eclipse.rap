/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileUploadLCA_Test {

  private Display display;
  private Shell shell;
  private FileUpload fileUpload;
  private FileUploadLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    fileUpload = new FileUpload( shell, SWT.NONE );
    lca = new FileUploadLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( fileUpload );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( "rwt.widgets.FileUpload", operation.getType() );
  }

  @Test
  public void testRenderCreate_withMULTI() throws IOException {
    fileUpload = new FileUpload( shell, SWT.MULTI );
    lca.renderInitialization( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( new JsonArray().add( "MULTI" ), operation.getProperties().get( "style" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( fileUpload );
    lca.renderInitialization( fileUpload );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof FileUploadOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    FileUploadOperationHandler handler = spy( new FileUploadOperationHandler( fileUpload ) );
    getRemoteObject( getId( fileUpload ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( fileUpload ), "Help", new JsonObject() );
    lca.readData( fileUpload );

    verify( handler ).handleNotifyHelp( fileUpload, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( getId( fileUpload.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( fileUpload ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    fileUpload.setText( "test" );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( fileUpload, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    fileUpload.setImage( image );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( fileUpload, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    fileUpload.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    fileUpload.setImage( image );

    Fixture.preserveWidgets();
    fileUpload.setImage( null );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( fileUpload, "image" ) );
  }

  @Test
  public void testSubmit() throws IOException {
    IFileUploadAdapter adapter = getFileUploadAdapter( fileUpload );
    adapter.setFileNames( new String[] { "foo" } );

    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( fileUpload, "submit" );
    assertEquals( "bar", operation.getParameters().get( "url" ).asString() );
  }

  @Test
  public void testSubmitWithoutFileName() throws IOException {
    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( fileUpload, "submit" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertFalse( operation.getProperties().names().contains( "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    fileUpload.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( fileUpload, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "customVariant" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Fixture.preserveWidgets();

    fileUpload.addSelectionListener( mock( SelectionListener.class ) );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( fileUpload, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    SelectionListener listener = mock( SelectionListener.class );
    fileUpload.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Fixture.preserveWidgets();

    fileUpload.removeSelectionListener( listener );
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( fileUpload, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Fixture.preserveWidgets();

    fileUpload.addSelectionListener( mock( SelectionListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( fileUpload, "Selection" ) );
  }

  private IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return upload.getAdapter( IFileUploadAdapter.class );
  }

}
