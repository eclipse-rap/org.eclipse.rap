/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.RoundedBorder",
{
  extend : qx.ui.core.Border,

  /**
   * @param width {Integer} The border width, see also {@link #width}
   * @param color {Color} The border color, see also {@link #color}
   * @param radius {Number} The corner radius.
   */
  construct : function( width, color, radii ) {
    this.base( arguments );
    this.__width = [ 0, 0, 0, 0 ];
    if ( width !== undefined ) {
      this.setWidth( width );
    }
    if ( color !== undefined ) {
      this.setColor( color );
    }
    if ( radii !== undefined ) {
      this.setRadii( radii );
    }
  },

  properties : {

    // starting top-left going clock-wise
    radii : {
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

    _applyWidthTop : function( value, old ) {
      this.__width[ 0 ] = value;
    },

    _applyWidthRight : function(value, old) {
      this.__width[ 1 ] = value;
    },

    _applyWidthBottom : function(value, old) {
      this.__width[ 2 ] = value;
    },

    _applyWidthLeft : function(value, old) {
      this.__width[ 3 ] = value;
    },

    _applyColorTop : function( value ) {
      this.__color = value;
    },

    _applyRadii : function( value, old ) {
      this.__radii = value; // TODO [tb] : check the values?
    },

    //ignore all other properties
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
