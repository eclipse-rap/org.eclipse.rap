/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH.
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
    if( parent.classname != "rwt.widgets.ExpandBar" ) {
      throw new Error( "illegal parent, must be a ExpandBar" );
    }
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

  statics : {
    STATE_EXPANDED : "expanded",
    STATE_OVER : "over"
  },

  members : {

    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._header.addState( state );
        this._chevron.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._header.removeState( state );
        this._chevron.removeState( state );
      }
    },

    setExpanded : function( expanded ) {
      this._expanded = expanded;
      if( expanded ) {
        this._chevron.addState( rwt.widgets.ExpandItem.STATE_EXPANDED );
        this._header.addState( rwt.widgets.ExpandItem.STATE_EXPANDED );
      } else {
        this._chevron.removeState( rwt.widgets.ExpandItem.STATE_EXPANDED );
        this._header.removeState( rwt.widgets.ExpandItem.STATE_EXPANDED );
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
        var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( this );
        remoteObject.set( "expanded", this._expanded );
        if(    ( this._expandBar._hasExpandListener && this._expanded )
            || ( this._expandBar._hasCollapseListener && !this._expanded ) )
        {
          var serverBar = rwt.remote.Server.getInstance().getRemoteObject( this._expandBar );
          var itemId = rwt.remote.ObjectRegistry.getId( this );
          var eventName = this._expanded ? "Expand" : "Collapse";
          serverBar.notify( eventName, { "item" : itemId } );
        }
      }
    },

    _onHandleMouseOver : function( evt ) {
      this._chevron.addState( rwt.widgets.ExpandItem.STATE_OVER );
    },

    _onHandleMouseOut : function( evt ) {
      this._chevron.removeState( rwt.widgets.ExpandItem.STATE_OVER );
    }
  }
} );
