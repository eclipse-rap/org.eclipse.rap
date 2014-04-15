/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.ExpandItem", {
  extend : rwt.widgets.base.Parent,

  construct : function( parent ) {
    this.base( arguments );
    this._expandBar = parent;
    this.setAppearance( "expand-item" );
    this._headerHeight = 24; // Chevron size with top/bottom insets
    this._expanded = false;
    this._image = null;
    this._text = "";
    // Construct a header area
    this._header = new rwt.widgets.base.Atom( "(empty)", this._image, 16, 16 );
    this._header.getLabelObject().setPaddingBottom( 4 );
    this._header.setAppearance( "expand-item-header" );
    this._header.addEventListener( "click", this._onClick, this );
    this._header.addEventListener( "mouseover", this._onHandleMouseOver, this );
    this._header.addEventListener( "mouseout", this._onHandleMouseOut, this );
    this._header.setHeight( this._headerHeight );
    this._header.setLabel( this._text );
    this.add( this._header );
    // Chevron image
    this._chevron = new rwt.widgets.base.Image();
    this._chevron.setAppearance( "expand-item-chevron-button" );
    this._chevron.setTop( ( this._headerHeight - this._chevron.getHeight() ) / 2 );
    this._chevron.addEventListener( "click", this._onClick, this );
    this._chevron.addEventListener( "mouseover", this._onHandleMouseOver, this );
    this._chevron.addEventListener( "mouseout", this._onHandleMouseOut, this );
    this.add( this._chevron );
  },

  destruct : function() {
    this._header.removeEventListener( "click", this._onClick, this );
    this._header.removeEventListener( "mouseover", this._onHandleMouseOver, this );
    this._header.removeEventListener( "mouseout", this._onHandleMouseOut, this );
    this._chevron.removeEventListener( "click", this._onClick, this );
    this._chevron.removeEventListener( "mouseover", this._onHandleMouseOver, this );
    this._chevron.removeEventListener( "mouseout", this._onHandleMouseOut, this );
    this._disposeObjects( "_header", "_chevron" );
  },

  members : {

    _getSubWidgets : function() {
      return [ this._header, this._chevron ];
    },

    setExpanded : function( expanded ) {
      this._expanded = expanded;
      if( expanded ) {
        this._chevron.addState( "expanded" );
        this._header.addState( "expanded" );
      } else {
        this._chevron.removeState( "expanded" );
        this._header.removeState( "expanded" );
      }
    },

    getExpanded : function( expanded ) {
      return this._expanded;
    },

    setImage : function( image ) {
      this._image = image;
      this._header.setIcon( image );
    },

    setText : function( text ) {
      this._text = text;
      this._header.setLabel( text );
    },

    setHeaderHeight : function( headerHeight ) {
      this._headerHeight = headerHeight;
      this._header.setHeight( this._headerHeight );
      this._chevron.setTop( ( this._headerHeight - this._chevron.getHeight() ) / 2 );
    },

    _onClick : function( evt ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        this.setExpanded( !this._expanded );
        var connection = rwt.remote.Connection.getInstance();
        connection.getRemoteObject( this ).set( "expanded", this._expanded );
        var eventName = this._expanded ? "Expand" : "Collapse";
        var itemId = rwt.remote.ObjectRegistry.getId( this );
        connection.getRemoteObject( this._expandBar ).notify( eventName, { "item" : itemId } );
      }
    },

    _onHandleMouseOver : function( evt ) {
      this._chevron.addState( "over" );
    },

    _onHandleMouseOut : function( evt ) {
      this._chevron.removeState( "over" );
    }

  }

} );
