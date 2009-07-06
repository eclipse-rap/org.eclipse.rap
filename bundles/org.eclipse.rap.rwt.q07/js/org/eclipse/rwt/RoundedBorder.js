/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.RoundedBorder",
{
  extend : qx.ui.core.Border,

  /**
   * @param width {Integer} The border width, see also {@link #width}
   * @param color {Color} The border color, see also {@link #color}
   * @param radius {Number} The corner radius.
   */
  construct : function(width, color, radius)
  {
    this.base( arguments );

    this.__width = [ 0, 0, 0, 0 ];

    if ( width !== undefined ) {
      this.setWidth( width );
    }

    if ( color !== undefined ) {
      this.setColor( color );
    }

    if ( radius !== undefined ) {
      this.setRadius( radius );
    }
  },


  properties : {

    // starting top-left going clock-wise
    radii : { // TODO [tb] group properties?
      check : "Array",
      nullable : false,
      apply : "_applyRadii",
      init : [ 0, 0, 0, 0 ]
    }

  },



  members : {

    // color-top is used for all edges
    setColor : function( value ) {
      this.setColorTop( value );
    },

    getColor : function() {
      return this.getColorTop();
    },

    setRadius : function( value ) {
      this.setRadii( [ value, value, value, value ] );
    },


    //handle properties
    // the manager is always informed with "top" since "renderTop" handles
    // everything, however on changed borderWidths the affected edge
    // must also be given for the widget to recalculate it's layout
    _applyWidthTop : function( value, old ) {
      this.__width[ 0 ] = value;
      this.__informManager( "top" );
    },

    _applyWidthRight : function(value, old) {
      this.__width[ 1 ] = value;
      this.__informManager( "top" );
      this.__informManager( "right" );
    },

    _applyWidthBottom : function(value, old) {
      this.__width[ 2 ] = value;
      this.__informManager( "top" );
      this.__informManager( "bottom" );
    },

    _applyWidthLeft : function(value, old) {
      this.__width[ 3 ] = value;
      this.__informManager( "top" );
      this.__informManager( "left" );
    },

    _changeColorTop : function( value ) {
      this.__color = value;
      this.__informManager( "top" );
    },

    _applyRadii : function( value, old ) {
      this.__radii = value; // TODO [tb] : check the values?
      this.__informManager( "top" );
    },

    //ignore all other properties
    // TODO [tb] : prodouce error on call?
    _applyColorRight : function() { },
    _applyColorBottom : function() { },
    _applyColorLeft : function() { },
    _applyColorInnerTop : function() { },
    _applyColorInnerBottom : function() { },
    _applyColorInnerLeft : function() { },
    _applyStyleTop : function() { },
    _applyStyleRight : function() { },
    _applyStyleBottom : function() { },
    _applyStyleLeft : function() { },

    renderTop : function( obj ) {
      // TODO [tb] : a value check for widths could be done here:
      // only two different values are allowed for width, one of them
      // zero, the other any positive number
      var width = this.__width;
      var color = this.__color || "black";
      var radii = this.__radii || this.getRadii();

      if( obj._styleGfxBorder ) {
        obj._styleGfxBorder( width, color, radii );
      }
    },

    //everything is done by "renderTop", so the others are useless
    renderRight : function() { },
    renderBottom : function() { },
    renderLeft : function() { }
  }//members
});
