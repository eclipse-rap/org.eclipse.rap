/*******************************************************************************
 * Copyright (c) 2025, 2026 EclipseSource and others.
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

  var VIDEO_INPUT_DEVICE_ID_KEY = "rwt.widgets.Camera.videoInputDeviceId";

  rwt.widgets.Camera = function( properties ) {
    bindAll( this, [ "init", "layout", "start", "stop", "takePicture", "showNextVideoInputDevice",
                     "isRunning", "onRender", "destroy", "capture" ] );
    this.parent = rap.getObject( properties.parent );
    this.uploadPath = properties.uploadPath;
    this.canvasElement = document.createElement( "canvas" );
    this.videoElement = document.createElement( "video" );
    this.cameraFlipElement = document.createElement( "img" );
    this.cameraCaptureElement = document.createElement( "img" );
    this.parent.append( this.videoElement );
    this.parent.append( this.canvasElement );
    this.parent.append( this.cameraFlipElement );
    this.parent.append( this.cameraCaptureElement );
    this.parent.addListener( "Resize", this.layout );
    this.parent.addListener( "Dispose", this.destroy );
    this.videoInputDeviceId = null;
    this.currentStream = null;
    rap.on( "render", this.onRender );
  };

  rwt.widgets.Camera.prototype = {

    onRender : async function() {
      await this.init();
      this.layout();
      rap.off( "render", this.onRender );
      this.start();
    },

    destroy : function() {
      this.stop();
      this.cameraFlipElement.removeEventListener( "click", this.showNextVideoInputDevice );
      this.cameraCaptureElement.removeEventListener( "click", this.capture );
      this.canvasElement.parentNode.removeChild( this.canvasElement );
      this.videoElement.parentNode.removeChild( this.videoElement );
      this.cameraFlipElement.parentNode.removeChild( this.cameraFlipElement );
      this.cameraCaptureElement.parentNode.removeChild( this.cameraCaptureElement );
    },

    init : async function() {
      this.canvasElement.style.display = "none";
      this.canvasElement.style.position = "absolute";
      this.videoElement.style.position = "absolute";
      this.cameraFlipElement.src = "rwt-resources/camera/camera-flip-32.png";
      this.cameraFlipElement.width = 32;
      this.cameraFlipElement.height = 32;
      this.cameraFlipElement.style.position = "absolute";
      this.cameraFlipElement.style.cursor = "pointer";
      this.cameraFlipElement.style.visibility = "hidden";
      this.cameraFlipElement.addEventListener( "click", this.showNextVideoInputDevice );
      this.cameraCaptureElement.src = "rwt-resources/camera/circle-slice-8-64.png";
      this.cameraCaptureElement.width = 64;
      this.cameraCaptureElement.height = 64;
      this.cameraCaptureElement.style.position = "absolute";
      this.cameraCaptureElement.style.cursor = "pointer";
      this.cameraCaptureElement.style.visibility = "hidden";
      this.cameraCaptureElement.addEventListener( "click", this.capture );
      this.videoInputDeviceId = await initVideoInputDeviceId();
    },

    layout : function() {
      var area = this.parent.getClientArea();
      this.canvasElement.width = area[ 2 ];
      this.canvasElement.height = area[ 3 ];
      this.videoElement.width = area[ 2 ];
      this.videoElement.height = area[ 3 ];
      this.cameraFlipElement.style.top = "5px";
      this.cameraFlipElement.style.left = "20px";
      this.cameraFlipElement.style.visibility = "visible";
      this.cameraCaptureElement.style.top = "40px";
      this.cameraCaptureElement.style.left = "5px";
      this.cameraCaptureElement.style.visibility = "visible";
    },

    start : async function() {
      if( !this.isRunning() ) {
        if( this.videoInputDeviceId != null ) {
          storeVideoInputDeviceId( this.videoInputDeviceId );
          var constraints = {
            video: {
              deviceId: {
                exact: this.videoInputDeviceId
              }
            },
            audio: false
          };
          try {
            this.currentStream = await navigator.mediaDevices.getUserMedia( constraints );
            this.videoElement.autoplay = true;
            this.videoElement.srcObject = this.currentStream;
          } catch( error ) {
            console.error( "Error accessing the camera: ", error );
          };
        }
      }
    },

    stop : function() {
      if( this.isRunning() ) {
        this.currentStream.getTracks().forEach( track => track.stop() );
        try {
          this.videoElement.srcObject = null;
        } catch( error ) {
          this.videoElement.src = "";
        }
        this.videoElement.removeAttribute( "src" );
        this.currentStream = null;
      }
    },

    takePicture : function( properties ) {
      if( this.isRunning() ) {
        var uploadPath = this.uploadPath;
        var video = this.videoElement;
        var canvas = this.canvasElement;
        var context = canvas.getContext( "2d" );
        if( properties.resolution ) {
          canvas.width = properties.resolution[ 0 ];
          canvas.height = properties.resolution[ 1 ];
        } else {
          var aspectRatio = video.videoWidth / video.videoHeight;
          canvas.width = video.width;
          canvas.height = canvas.width / aspectRatio;
        }
        context.drawImage( video, 0, 0, canvas.width, canvas.height );
        canvas.toBlob( ( blob ) => {
          var formData = new FormData();
          formData.append( "image", blob, "camera_picture.jpg" );
          fetch( uploadPath, {
            method: "POST",
            body: formData
          } );
        }, "image/jpeg", properties.compressionQuality );
      }
    },

    capture : function() {
      this.takePicture( { compressionQuality : 1 } );
    },

    showNextVideoInputDevice : async function() {
      var running = this.isRunning();
      if( running ) {
        this.stop();
      }
      this.videoInputDeviceId = await nextVideoInputDeviceId( this.videoInputDeviceId );
      if( running ) {
        this.start();
      }
    },

    isRunning : function() {
      return this.currentStream != null;
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

  rap.registerTypeHandler( "rwt.widgets.Camera", {

    factory : function( properties ) {
      return new rwt.widgets.Camera( properties );
    },

    properties : [],

    methods : [ "start", "stop", "takePicture" ]

  } );

}());

