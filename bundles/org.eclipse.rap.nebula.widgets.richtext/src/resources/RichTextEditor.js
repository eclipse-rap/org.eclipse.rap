/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

var CKEDITOR_BASEPATH = "rwt-resources/ckeditor/";

(function(){
  'use strict';

  rwt.define( "rwt.widgets" );

  rwt.widgets.RichTextEditor = function( properties ) {
    bindAll( this, [ "layout", "onReady", "onSend", "onRender", "destroy" ] );
    this.parent = rap.getObject( properties.parent );
    this.element = document.createElement( "div" );
    this.parent.append( this.element );
    this.parent.addListener( "Resize", this.layout );
    this.parent.addListener( "Dispose", this.destroy );
    rap.on( "render", this.onRender );
  };

  rwt.widgets.RichTextEditor.prototype = {

    ready : false,

    onReady : function() {
      this.ready = true;
      this.layout();
      if( this._text ) {
        this.setText( this._text );
        delete this._text;
      }
      if( this._font ) {
        this.setFont( this._font );
        delete this._font;
      }
      if( typeof this._editable !== "undefined" ) {
        this.setEditable( this._editable );
        delete this._editable;
      }
    },

    onRender : function() {
      if( this.element.parentNode ) {
        rap.off( "render", this.onRender );
        this.editor = CKEDITOR.appendTo( this.element );
        this.editor.on( "instanceReady", this.onReady );
        rap.on( "send", this.onSend );
      }
    },

    onSend : function() {
      if( this.editor.checkDirty() ) {
        rap.getRemoteObject( this ).set( "text", this.editor.getData() );
        this.editor.resetDirty();
      }
    },

    setText : function( text ) {
      if( this.ready ) {
        this.editor.setData( text );
      } else {
        this._text = text;
      }
    },

    setEditable : function( editable ) {
      if( this.ready ) {
        this.editor.setReadOnly( !editable );
      } else {
        this._editable = editable;
      }
    },

    setFont : function( font ) {
      if( this.ready ) {
        async( this, function() { // Needed by IE for some reason
          this.editor.document.getBody().setStyle( "font", font );
        } );
      } else {
        this._font = font;
      }
    },

    destroy : function() {
      if( this.element.parentNode ) {
        rap.off( "send", this.onSend );
        this.editor.destroy();
        this.element.parentNode.removeChild( this.element );
      }
    },

    layout : function() {
      if( this.ready ) {
        var area = this.parent.getClientArea();
        this.element.style.left = area[ 0 ] + "px";
        this.element.style.top = area[ 1 ] + "px";
        this.editor.resize( area[ 2 ], area[ 3 ] );
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

  function async( context, func ) {
    window.setTimeout( function(){
      func.apply( context );
    }, 0 );
  };

}());

