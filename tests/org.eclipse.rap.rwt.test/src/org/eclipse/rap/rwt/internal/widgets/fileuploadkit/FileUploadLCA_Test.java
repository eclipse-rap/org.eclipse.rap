/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
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
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( fileUpload );
    ControlLCATestUtil.testFocusListener( fileUpload );
    ControlLCATestUtil.testMouseListener( fileUpload );
    ControlLCATestUtil.testKeyListener( fileUpload );
    ControlLCATestUtil.testTraverseListener( fileUpload );
    ControlLCATestUtil.testMenuDetectListener( fileUpload );
    ControlLCATestUtil.testHelpListener( fileUpload );
  }

  @Test
  public void testPreserveBounds() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = WidgetUtil.getAdapter( fileUpload );
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    fileUpload.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( fileUpload );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
  }

  @Test
  public void testReadFileName() {
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( fileUpload ), "fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( "foo", fileUpload.getFileName() );

    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( "foo", fileUpload.getFileName() );
  }

  @Test
  public void testReadEmptyFileName() {
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( fileUpload ), "fileName", "" );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( null, fileUpload.getFileName() );
  }

  @Test
  public void testFireSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    fileUpload.addSelectionListener( listener );

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( fileUpload ), "fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( "foo", fileUpload.getFileName() );
    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( "rwt.widgets.FileUpload", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( WidgetUtil.getId( fileUpload.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( fileUpload );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( fileUpload ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    fileUpload.setText( "test" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( fileUpload, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    fileUpload.setImage( image );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
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

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( fileUpload, "image" ) );
  }

  @Test
  public void testSubmit() throws IOException {
    IFileUploadAdapter adapter = getFileUploadAdapter( fileUpload );
    adapter.setFileName( "foo" );

    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( fileUpload, "submit" );
    assertEquals( "bar", operation.getProperty( "url" ).asString() );
  }

  @Test
  public void testSubmitWithoutFileName() throws IOException {
    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( fileUpload, "submit" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    fileUpload.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( fileUpload, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "customVariant" ) );
  }

  private IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return upload.getAdapter( IFileUploadAdapter.class );
  }

}
