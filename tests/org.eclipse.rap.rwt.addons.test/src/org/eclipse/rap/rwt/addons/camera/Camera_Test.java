/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.addons.camera.Camera.ImageUploadReceiver;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@SuppressWarnings("restriction")
public class Camera_Test {

  private Display display;
  private Shell shell;
  private Connection connection;
  private RemoteObject remoteObject;
  private Camera camera;

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    context.replaceConnection( connection );
    camera = new Camera( shell );
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( Camera.class ) );
  }

  @Test
  public void testCameraListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( CameraListener.class ) );
  }

  @Test
  public void testSetsNoInitialCameraOptionsWithDefaultOptions() {
    verify( remoteObject, never() ).set( eq( "resolution" ), any( JsonValue.class ) );
    verify( remoteObject, never() ).set( eq( "compressionQuality" ), any( JsonValue.class ) );
  }

  @Test
  public void testSendsOpenWithTakePhotoCall() {
    CameraListener listener = mock( CameraListener.class );
    camera.addCameraListener( listener );

    camera.takePicture( createOptions() );

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "takePicture" ), captor.capture() );
    JsonArray resolution = captor.getValue().get( "resolution" ).asArray();
    assertEquals( 100, resolution.get( 0 ).asInt() );
    assertEquals( 100, resolution.get( 1 ).asInt() );
    assertEquals( captor.getValue().get( "compressionQuality" ).asFloat(), 0.5F, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullOptions() {
    camera.takePicture( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddFailsWithNullListener() {
    camera.addCameraListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveFailsWithNullListener() {
    camera.removeCameraListener( null );
  }

  @Test
  public void testDelegatesImage_activatesServerPush() {
    camera.takePicture( createOptions() );

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testDelegatesImage_withFailedUpload() {
    CameraListener listener = mock( CameraListener.class );
    camera.addCameraListener( listener );
    camera.takePicture( createOptions() );
    ImageUploadReceiver receiver = mock( ImageUploadReceiver.class );

    camera.handleUploadFailed( display, receiver );
    display.readAndDispatch();

    verify( listener ).receivedPicture( null );
    verify( receiver ).reset();
    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testDelegatesImage_withSuccessfulUpload() {
    Image image = getImage();
    CameraListener listener = mock( CameraListener.class );
    camera.addCameraListener( listener );
    camera.takePicture( createOptions() );
    ImageUploadReceiver receiver = mock( ImageUploadReceiver.class );
    when( receiver.getImage() ).thenReturn( image );

    camera.handleUploadFinished( display, receiver );
    display.readAndDispatch();

    verify( listener ).receivedPicture( image );
    verify( receiver ).reset();
    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStripContextPath_withLeadingSlash() throws Exception {
    String serviceHandlerUrl = "/entry?servicehandler=handler";

    assertEquals( "entry?servicehandler=handler", Camera.stripContextPath( serviceHandlerUrl ) );
  }

  @Test
  public void testStripContextPath_withContextPath() throws Exception {
    String serviceHandlerUrl = "foo/bar/entry?servicehandler=handler";

    assertEquals( "entry?servicehandler=handler", Camera.stripContextPath( serviceHandlerUrl ) );
  }

  @Test
  public void testStripContextPath_withoutContextPath() throws Exception {
    String serviceHandlerUrl = "entry?servicehandler=handler";

    assertEquals( "entry?servicehandler=handler", Camera.stripContextPath( serviceHandlerUrl ) );
  }

  @Test
  public void testStripContextPath_notServiceHandleUrl() throws Exception {
    String serviceHandlerUrl = "foo.bar";

    assertEquals( "foo.bar", Camera.stripContextPath( serviceHandlerUrl ) );
  }

  private static CameraOptions createOptions() {
    CameraOptions options = new CameraOptions();
    options.setResolution( 100, 100 );
    options.setCompressionQuality( 0.5F );
    return options;
  }

  private Image getImage() {
    InputStream resourceStream = getClass().getResourceAsStream( "ok.png" );
    return new Image( display, resourceStream );
  }

}
