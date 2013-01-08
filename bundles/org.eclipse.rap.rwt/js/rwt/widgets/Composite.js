/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.Composite", {

  extend : rwt.widgets.base.Parent,

  include : rwt.animation.VisibilityAnimationMixin,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "composite" );
    this.setOverflow( "hidden" );
    this.setHideFocus( true );
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    if( rwt.client.Client.isMshtml() ) {
      // Alternate fix for 299629. This might not always work if the composite
      // is changed back and forth between rounded and normal border.
      this._fixBackgroundTransparency();
      this.addEventListener( "changeBackgroundColor",
                             this._fixBackgroundTransparency,
                             this );
    }
    // Disable scrolling (see bug 345903)
    rwt.widgets.base.Widget.disableScrolling( this );
    this._clientArea = [ 0, 0, 0, 0 ];
  },

  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this._clientArea = null;
  },

  members : {

    setClientArea : function( clientArea ) {
      this._clientArea = clientArea;
      this.dispatchSimpleEvent( "clientAreaChanged" );
    },

    getClientArea : function() {
      return this._clientArea.concat();
    },

    _onMouseOver : function( evt ) {
      this.addState( "over" );
    },

    _onMouseOut : function( evt ) {
      this.removeState( "over" );
    },

    _applyBackgroundImage : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( newValue, oldValue ) {
        this.base( arguments, newValue, oldValue );
        if( newValue == null ) {
          this._fixBackgroundTransparency();
        }
      },
      "default" : function( newValue, oldValue ) {
        this.base( arguments, newValue, oldValue );
      }
    } ),

    _fixBackgroundTransparency : function() {
      if( this.getBackgroundColor() == null && this.getBackgroundImage() == null ) {
        this._applyBackgroundImage( "static/image/blank.gif", null );
      }
    }

  }
} );
