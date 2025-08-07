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
package org.eclipse.rap.rwt.addons.scanner;

import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.addons.camera.Camera;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.widgets.compositekit.CompositeOperationHandler;
import org.eclipse.swt.widgets.Composite;


/**
 * <p>
 * The {@link BarcodeScanner} widget can be used to to scan various types of barcodes.
 * </p>
 * <p>
 * <b>Please Note:</b> Scanning is an asynchronous operation. For this reason you need to attach a {@link ScanListener}
 * to the {@link BarcodeScanner} widget to receive scan results.
 * </p>
 *
 * @since 4.4
 */
@SuppressWarnings({
  "restriction",
  "serial"
})
public class BarcodeScanner extends Composite {

  public static enum Formats {
    /** UPC-A 1D format. */
    UPC_A,
    /** UPC-E 1D format. */
    UPC_E,
    /** Code 39 1D format. */
    CODE_39,
    /** Code 93 1D format. */
    CODE_93,
    /** Code 128 1D format. */
    CODE_128,
    /** EAN-8 1D format. */
    EAN_8,
    /** EAN-13 1D format. */
    EAN_13,
    /** PDF417 format. */
    PDF_417,
    /** QR Code 2D barcode format. */
    QR_CODE,
    /** Aztec 2D barcode format. */
    AZTEC,
    /** ITF (Interleaved Two of Five) 1D format. */
    ITF,
    /** Data Matrix 2D barcode format. */
    DATA_MATRIX,
    /** CODABAR 1D format. */
    CODABAR
  }

  private static final String TYPE_BARCODE_SCANNER = "rwt.widgets.BarcodeScanner";
  private static final String PROPERTY_PARENT = "parent";
  private static final String PROPERTY_FORMATS = "formats";
  private static final String PROPERTY_FORMAT = "format";
  private static final String PROPERTY_RUNNING = "running";
  private static final String PROPERTY_DATA = "data";
  private static final String PROPERTY_RAW_DATA = "rawData";
  private static final String PROPERTY_ERROR_MESSAGE = "errorMessage";
  private static final String METHOD_START = "start";
  private static final String METHOD_STOP = "stop";
  private static final String EVENT_SUCCESS = "Success";
  private static final String EVENT_ERROR = "Error";

  private static final String RESOURCES_PATH = "org/eclipse/rap/rwt/addons/scanner/";
  private static final String REGISTER_PATH = "scanner/";

  private static final String[] RESOURCES_FILES = { "BarcodeScanner.js", "camera-flip-32.png" };
  private static final String PROP_ZXING_JS_URL = "org.eclipse.rap.rwt.addons.scanner.zxingJsUrl";
  private static final String DEF_ZXING_JS_URL = "https://unpkg.com/@zxing/browser@latest";

  private final RemoteObject remoteObject;
  private final List<ScanListener> scanListeners = new ArrayList<ScanListener>();
  private boolean running;

  public BarcodeScanner( Composite parent ) {
    this( parent, SWT.NONE );
  }

  public BarcodeScanner( Composite parent, int style ) {
    super( parent, style );
    registerResources();
    loadJavaScript();
    Connection connection = RWT.getUISession().getConnection();
    remoteObject = connection.createRemoteObject( TYPE_BARCODE_SCANNER );
    remoteObject.setHandler( new BarcodeScannerOperationHandler( this ) );
    remoteObject.set( PROPERTY_PARENT, getId( this ) );
  }

  /**
   * <p>
   * Enables the camera and starts scanning for barcodes. When started, the <code>BarcodeScanner</code>
   * continuously notifies the <code>ScanListener</code> as soon as it finds a barcode in its view.
   * </p>
   *
   * @param formats specifies barcode formats to be recognized
   *
   * @exception SWTException
   * <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void start( Formats[] formats ) {
    checkWidget();
    if( !running ) {
      JsonObject props = new JsonObject();
      JsonArray jsonFormats = new JsonArray();
      for( Formats format : formats ) {
        jsonFormats.add( format.toString() );
      }
      props.add( PROPERTY_FORMATS, jsonFormats );
      remoteObject.call( METHOD_START, props );
      running = true;
    }
  }

  /**
   * <p>
   * Stops the barcode scanning and disables the camera.
   * </p>
   *
   * @exception SWTException
   * <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void stop() {
    checkWidget();
    if( running ) {
      remoteObject.call( METHOD_STOP, null );
      running = false;
    }
  }

  /**
   * <p>
   * Returns true if barcode scanning is running, false otherwise.
   * </p>
   *
   * @exception SWTException
   * <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean isRunning() {
    checkWidget();
    return running;
  }

  /**
   * Adds a {@link ScanListener} to receive notifications of barcode scanning results.
   *
   * @param listener the listener to add
   *
   * @exception SWTException
   * <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void addScanListener( ScanListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    scanListeners.add( listener );
  }

  /**
   * Removes a {@link ScanListener}.
   *
   * @param listener the listener to remove
   *
   * @exception SWTException
   * <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void removeScanListener( ScanListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    scanListeners.remove( listener );
  }

  private static void registerResources() {
    ResourceManager resourceManager = RWT.getResourceManager();
    for( String fileName : RESOURCES_FILES ) {
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
    loader.requireJs( System.getProperty( PROP_ZXING_JS_URL, DEF_ZXING_JS_URL )  );
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "BarcodeScanner.js" ) );
  }

  private static void register( ResourceManager resourceManager, String fileName )
    throws IOException
  {
    ClassLoader classLoader = Camera.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( RESOURCES_PATH + fileName );
    try {
      resourceManager.register( REGISTER_PATH + fileName, inputStream );
    } finally {
      inputStream.close();
    }
  }

  private class BarcodeScannerOperationHandler extends CompositeOperationHandler {

     public BarcodeScannerOperationHandler( BarcodeScanner scanner ) {
       super( scanner );
     }

     @Override
     public void handleSet( Composite control, JsonObject properties ) {
       super.handleSet( control, properties );
       handleSetRunning( control, properties );
     }

     public void handleSetRunning( Composite control, JsonObject properties ) {
       JsonValue value = properties.get( PROPERTY_RUNNING );
       if( value != null ) {
         running = value.asBoolean();
       }
     }

     @Override
     public void handleNotify( Composite control, String eventName, JsonObject properties ) {
       if( EVENT_SUCCESS.equals( eventName ) ) {
         notifyScanSucceeded( ( BarcodeScanner )control, properties );
       } else if( EVENT_ERROR.equals( eventName )  ) {
         notifyScanFailed( ( BarcodeScanner )control, properties );
       } else {
         super.handleNotify( control, eventName, properties );
       }
     }

     private void notifyScanSucceeded( final BarcodeScanner scanner, final JsonObject properties ) {
       ProcessActionRunner.add( new Runnable() {
         @Override
         public void run() {
           ScanListener[] listeners = scanListeners.toArray( new ScanListener[ 0 ] );
           for( ScanListener listener : listeners ) {
             String format = properties.get( PROPERTY_FORMAT ).asString();
             String data = properties.get( PROPERTY_DATA ).asString();
             int[] rawData = toIntArray( properties.get( PROPERTY_RAW_DATA ).asArray() );
             listener.scanSucceeded( format, data, rawData );
           }
         }
       } );
     }

     private void notifyScanFailed( final BarcodeScanner scanner, final JsonObject properties ) {
       ProcessActionRunner.add( new Runnable() {
         @Override
         public void run() {
           ScanListener[] listeners = scanListeners.toArray( new ScanListener[ 0 ] );
           for( ScanListener listener : listeners ) {
             String error = properties.get( PROPERTY_ERROR_MESSAGE ).asString();
             listener.scanFailed( error );
           }
         }
       } );
     }

     private static int[] toIntArray( JsonArray array ) {
       int[] intArray = new int[ array.size() ];
       for( int i = 0; i < array.size(); i++ ) {
         intArray[ i ] = array.get( i ).asInt();
       }
       return intArray;
     }

   }

}
