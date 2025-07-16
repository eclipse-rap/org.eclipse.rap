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

import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.fileupload.FileUploadReceiver;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Camera control that shows a video stream from the client camera. *
 *
 * @since 4.4
 */
@SuppressWarnings("serial")
public class Camera extends Composite {
  
  private static final Pattern SERVICE_URL_PATTERN = Pattern.compile(".*/([^/.]*\\?.*)"); //$NON-NLS-1$
  
  public static final String PROPERTY_PARENT = "parent";
  public static final String PROPERTY_UPLOAD_PATH = "uploadPath";
  public static final String PROPERTY_RESOLUTION = "resolution";
  public static final String PROPERTY_COMPRESSON_QUALITY = "compressionQuality";
  public static final String METHOD_TAKE_PICTURE = "takePicture";
  
  private static final String JS_PATH = "org/eclipse/rap/rwt/addons/camera/";
  private static final String REGISTER_PATH = "camera/";

  private static final String[] JS_FILES = { "Camera.js" };
  private static final String REMOTE_TYPE = "rwt.widgets.Camera";
  
  private final RemoteObject remoteObject;
  private final List<CameraListener> cameraListeners;
  private final ServerPushSession serverPush;

  public Camera( Composite parent ) {
    super( parent, SWT.NONE );
    registerResources();
    loadJavaScript();
    Connection connection = RWT.getUISession().getConnection();
    remoteObject = connection.createRemoteObject( REMOTE_TYPE );
    remoteObject.set( PROPERTY_PARENT, getId( this ) );
    cameraListeners = new ArrayList<CameraListener>();
    serverPush = new ServerPushSession();
    String uploadPath = registerFileUploadServiceHandler();
    remoteObject.set( PROPERTY_UPLOAD_PATH, stripContextPath( uploadPath ) );
  }
  
  /**
   * <p>
   * Instructs the client to take a picture from the camera. The added {@link CameraListener}s will be called when the
   * user has taken a picture..
   * </p>
   *
   * @see CameraListener
   */
  public void takePicture( CameraOptions options ) {
    checkWidget();
    if( options == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    remoteObject.call( METHOD_TAKE_PICTURE, createProperties( options ) );
    serverPush.start();
  }
  
  /**
   * <p>
   * Adds a {@link CameraListener} to get notified about image events.
   * </p>
   */
  public void addCameraListener( CameraListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    cameraListeners.add( listener );
  }

  /**
   * <p>
   * Removes a {@link CameraListener}.
   * </p>
   */
  public void removeCameraListener( CameraListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    cameraListeners.remove( listener );
  }
  
  @Override
  public void dispose() {
    if( !isDisposed() ) {
      remoteObject.destroy();
    }
    super.dispose();
  }
  
  RemoteObject getRemoteObject() {
    return remoteObject;
  }
  
  private static void registerResources() {
    ResourceManager resourceManager = RWT.getResourceManager();
    for( String fileName : JS_FILES ) {
      registerFileIfNeeded( resourceManager, fileName );
    }
  }

  private static void registerFileIfNeeded( ResourceManager resourceManager, String fileName ) {
    boolean isRegistered = resourceManager.isRegistered( REGISTER_PATH + fileName );
    if( !isRegistered ) {
      try {
        register( resourceManager, fileName );
      } catch( IOException ioe ) {
        throw new IllegalArgumentException( "Failed to load resources", ioe );
      }
    }
  }
  
  private static void loadJavaScript() {
    ClientFileLoader loader = RWT.getClient().getService( ClientFileLoader.class );
    ResourceManager resourceManager = RWT.getResourceManager();
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "Camera.js" ) );
  }
  
  private static void register( ResourceManager resourceManager, String fileName ) throws IOException {
    ClassLoader classLoader = Camera.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( JS_PATH + fileName );
    try {
      resourceManager.register( REGISTER_PATH + fileName, inputStream );
    } finally {
      inputStream.close();
    }
  }
  
  private static JsonObject createProperties( CameraOptions options ) {
    JsonObject properties = new JsonObject();
    addResolution( properties, options );
    addCompressionQuality( properties, options );
    return properties;
  }
  
  private static void addResolution( JsonObject properties, CameraOptions options ) {
    Point resolution = options.getResolution();
    if( resolution != null ) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add( resolution.x );
      jsonArray.add( resolution.y );
      properties.add( PROPERTY_RESOLUTION, jsonArray );
    }
  }

  private static void addCompressionQuality( JsonObject properties, CameraOptions options ) {
    properties.add( PROPERTY_COMPRESSON_QUALITY, options.getCompressionQuality() );
  }
  
  void handleUploadFailed( Display display, final ImageUploadReceiver receiver ) {
    if( display != null && !display.isDisposed() ) {
      display.asyncExec( new Runnable() {
        @Override
        public void run() {
          notifyListenersWithoutPicture();
          receiver.reset();
          serverPush.stop();
        }
      } );
    }
  }

  void handleUploadFinished( Display display, final ImageUploadReceiver receiver ) {
    if( display != null && !display.isDisposed() ) {
      display.asyncExec( new Runnable() {
        @Override
        public void run() {
          notifyListenersWithImage( receiver.getImage() );
          receiver.reset();
          serverPush.stop();
        }
      } );
    }
  }

  private void notifyListenersWithImage( Image image ) {
    List<CameraListener> listeners = new ArrayList<CameraListener>( cameraListeners );
    for( CameraListener listener : listeners ) {
      listener.receivedPicture( image );
    }
  }

  private void notifyListenersWithoutPicture() {
    List<CameraListener> listeners = new ArrayList<CameraListener>( cameraListeners );
    for( CameraListener listener : listeners ) {
      listener.receivedPicture( null );
    }
  }

  private String registerFileUploadServiceHandler() {
    Display display = getDisplay();
    ImageUploadReceiver receiver = new ImageUploadReceiver( display );
    FileUploadHandler uploadHandler = new FileUploadHandler( receiver );
    uploadHandler.addUploadListener( new FileUploadListener() {
      @Override
      public void uploadProgress( FileUploadEvent event ) {
      }
      @Override
      public void uploadFailed( FileUploadEvent event ) {
        handleUploadFailed( display, receiver );
      }
      @Override
      public void uploadFinished( FileUploadEvent event ) {
        handleUploadFinished( display, receiver );
      }
    } );
    RWT.getUISession().addUISessionListener( new UISessionListener() {
      @Override
      public void beforeDestroy( UISessionEvent event ) {
        uploadHandler.dispose();
      }
    } );
    return uploadHandler.getUploadUrl();
  }

  static String stripContextPath( String serviceHandlerUrl ) {
    Matcher matcher = SERVICE_URL_PATTERN.matcher( serviceHandlerUrl );
    if( matcher.matches() ) {
      return matcher.group( 1 );
    }
    return serviceHandlerUrl;
  }
  
  public class ImageUploadReceiver extends FileUploadReceiver {

    private final Display display;
    private Image image;

    public ImageUploadReceiver( Display display ) {
      this.display = display;
    }

    @Override
    public void receive( InputStream stream, FileDetails details ) throws IOException {
      image = new Image( display, stream );
    }

    public Image getImage() {
      return image;
    }

    public void reset() {
      image = null;
    }

  }
  
}
