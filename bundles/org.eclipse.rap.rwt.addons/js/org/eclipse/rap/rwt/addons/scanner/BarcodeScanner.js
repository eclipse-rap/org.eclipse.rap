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

(function(){
  'use strict';

  rwt.define( "rwt.widgets" );

  var VIDEO_INPUT_DEVICE_ID_KEY = "rwt.widgets.BarcodeScanner.videoInputDeviceId";

  rwt.widgets.BarcodeScanner = function( properties ) {
    bindAll( this, [ "init", "layout", "start", "stop", "showNextVideoInputDevice",
                     "isRunning", "onRender", "destroy" ] );
    this.parent = rap.getObject( properties.parent );
    this.videoElement = document.createElement( "video" );
    this.cameraFlipElement = document.createElement( "img" );
    this.parent.append( this.videoElement );
    this.parent.append( this.cameraFlipElement );
    this.parent.addListener( "Resize", this.layout );
    this.parent.addListener( "Dispose", this.destroy );
    this.videoInputDeviceId = null;
    this.controls = null;
    this.currentScanProperties = {};
    rap.on( "render", this.onRender );
  };

  rwt.widgets.BarcodeScanner.prototype = {

    onRender : async function() {
      await this.init();
      this.layout();
      rap.off( "render", this.onRender );
    },

    destroy : function() {
      this.stop();
      this.cameraFlipElement.removeEventListener( "click", this.showNextVideoInputDevice );
      this.videoElement.parentNode.removeChild( this.videoElement );
      this.cameraFlipElement.parentNode.removeChild( this.cameraFlipElement );
    },

    init : async function() {
      this.videoElement.style.position = "absolute";
      this.cameraFlipElement.src = "rwt-resources/scanner/camera-flip-32.png";
      this.cameraFlipElement.width = 32;
      this.cameraFlipElement.height = 32;
      this.cameraFlipElement.style.position = "absolute";
      this.cameraFlipElement.style.cursor = "pointer";
      this.cameraFlipElement.addEventListener( "click", this.showNextVideoInputDevice );
      this.videoInputDeviceId = await initVideoInputDeviceId();
    },

    layout : function() {
      var area = this.parent.getClientArea();
      this.videoElement.width = area[ 2 ];
      this.videoElement.height = area[ 3 ];
      this.cameraFlipElement.style.top = "5px";
      this.cameraFlipElement.style.left = "5px";
    },

    start : async function( properties ) {
      if( !this.isRunning() ) {
        this.currentScanProperties = properties;
        var remoteObject = rap.getRemoteObject( this );
        remoteObject.isListening = rwt.util.Functions.returnTrue;
        var codeReader = new ZXingBrowser.BrowserMultiFormatReader();
        if( properties.formats ) {
          codeReader.possibleFormats = toZXingBarcodeFormat( properties.formats );
        }
        if( this.videoInputDeviceId != null ) {
          storeVideoInputDeviceId( this.videoInputDeviceId );
          this.controls = await codeReader.decodeFromVideoDevice( this.videoInputDeviceId, this.videoElement,
            ( result, error, controls ) => {
              if( result ) {
                remoteObject.notify( "Success", {
                  format: ZXingBrowser.BarcodeFormat[ result.format ],
                  data: result.text,
                  rawData: Array.from( result.rawBytes )
                } );
              } else if( error && error.message !== "No MultiFormat Readers were able to detect the code." ) {
                remoteObject.notify( "Error", {
                  errorMessage: error.message
                } );
              }
            } );
        } else {
          remoteObject.notify( "Error", {
            errorMessage: "No video input device detected."
          } );
        }
      }
    },

    stop : function() {
      if( this.isRunning() ) {
        this.controls.stop();
        this.controls = null;
      }
    },

    showNextVideoInputDevice : async function() {
      var running = this.isRunning();
      if( running ) {
        this.stop();
      }
      this.videoInputDeviceId = await nextVideoInputDeviceId( this.videoInputDeviceId );
      if( running ) {
        this.start( this.currentScanProperties );
      }
    },

    isRunning : function() {
      return this.controls != null;
    }

  };

  function bindAll( context, methodNames ) {
    for( var i = 0; i < methodNames.length; i++ ) {
      var method = context[ methodNames[ i ] ];
      context[ methodNames[ i ] ] = bind( context, method );
    }
  };

  function bind( context, method ) {
    return function() {
      return method.apply( context, arguments );
    };
  };

  async function initVideoInputDeviceId() {
    // Workaround for Firefox to ask for Camera permissions
    var stream = await navigator.mediaDevices.getUserMedia( { video: true } );
    stream.getTracks().forEach( track => track.stop() );

    var videoInputDeviceId = null;
    var videoInputDevices = await getVideoInputDevices();
    if( videoInputDevices.length > 0 ) {
      videoInputDeviceId = videoInputDevices[ 0 ].deviceId;
    }
    var storedVideoInputDeviceId = rwt.client.Cookie.get( VIDEO_INPUT_DEVICE_ID_KEY );
    for( var i = 0; i < videoInputDevices.length; i++ ) {
      if( videoInputDevices[ i ].deviceId === storedVideoInputDeviceId ) {
        videoInputDeviceId = storedVideoInputDeviceId;
      }
    }
    return videoInputDeviceId;
  };

  async function nextVideoInputDeviceId( currentDeviceId ) {
    var videoInputDeviceId = null;
    var videoInputDevices = await getVideoInputDevices();
    if( videoInputDevices.length > 0 ) {
      videoInputDeviceId = videoInputDevices[ 0 ].deviceId;
    }
    for( var i = 0; i < videoInputDevices.length - 1; i++ ) {
      if( videoInputDevices[ i ].deviceId === currentDeviceId ) {
        videoInputDeviceId = videoInputDevices[ i + 1 ].deviceId;
      }
    }
    return videoInputDeviceId;
  };

  function storeVideoInputDeviceId( currentDeviceId ) {
    rwt.client.Cookie.set( VIDEO_INPUT_DEVICE_ID_KEY, currentDeviceId, 365, "/" );
  };

  async function getVideoInputDevices() {
    var videoInputDevices = [];
    var mediaDevices = await navigator.mediaDevices.enumerateDevices();
    mediaDevices.forEach( mediaDevice => {
      if( mediaDevice.kind === "videoinput" && mediaDevice.deviceId ) {
        videoInputDevices.push( mediaDevice );
      }
    } );
    return videoInputDevices;
  };

  function toZXingBarcodeFormat( formats ) {
    var result = [];
    for( var i = 0; i < formats.length; i++ ) {
      result.push( ZXingBrowser.BarcodeFormat[ formats[ i ] ] );
    }
    return result;
  };

  rap.registerTypeHandler( "rwt.widgets.BarcodeScanner", {

    factory : function( properties ) {
      return new rwt.widgets.BarcodeScanner( properties );
    },

    properties : [],

    methods : [ "start", "stop" ]

  } );

}());

