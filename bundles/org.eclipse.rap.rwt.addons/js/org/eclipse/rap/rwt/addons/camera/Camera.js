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

  rwt.widgets.Camera = function( properties ) {
    bindAll( this, [ "init", "layout", "takePicture", "onSend", "onRender", "destroy" ] );
    this.parent = rap.getObject( properties.parent );
    this.uploadPath = properties.uploadPath;
    this.canvasElement = document.createElement( "canvas" );
    this.videoElement = document.createElement( "video" );
    this.parent.append( this.videoElement );
    this.parent.append( this.canvasElement );
    this.parent.addListener( "Resize", this.layout );
    this.parent.addListener( "Dispose", this.destroy );
    rap.on( "render", this.onRender );
  };

  rwt.widgets.Camera.prototype = {

    ready : false,

    onRender : function() {
      this.init();
      this.layout();
      rap.off( "render", this.onRender );
      rap.on( "send", this.onSend );
      if( navigator.mediaDevices ) {
        navigator.mediaDevices.getUserMedia( { video: true } )
          .then( ( stream ) => {
            this.videoElement.autoplay = true;
            this.videoElement.srcObject = stream;
            this.ready = true;
          } )
          .catch( ( error ) => {
            console.error( "Error accessing the camera: ", error );
          } );
      }
    },

    onSend : function() {
    },

    destroy : function() {
      rap.off( "send", this.onSend );
      this.canvasElement.parentNode.removeChild( this.canvasElement );
      this.videoElement.parentNode.removeChild( this.videoElement );
    },

    init : function() {
      this.canvasElement.style.display = "none";
      this.canvasElement.style.position = "absolute";
      this.videoElement.style.position = "absolute";
    },

    layout : function() {
      var area = this.parent.getClientArea();
      this.canvasElement.width = area[ 2 ];
      this.canvasElement.height = area[ 3 ];
      this.videoElement.width = area[ 2 ];
      this.videoElement.height = area[ 3 ];
    },

    takePicture : function( properties ) {
      if( this.ready ) {
        var uploadPath = this.uploadPath;
        var video = this.videoElement;
        var canvas = this.canvasElement;
        var context = canvas.getContext( '2d' );
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
            method: 'POST',
            body: formData
          } );
        }, 'image/jpeg', properties.compressionQuality );
      }
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

  rap.registerTypeHandler( "rwt.widgets.Camera", {

    factory : function( properties ) {
      return new rwt.widgets.Camera( properties );
    },

    properties : [],

    methods : [ "takePicture" ]

  } );

}());

