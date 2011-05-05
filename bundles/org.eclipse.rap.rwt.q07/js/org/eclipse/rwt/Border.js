/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.Border", {
  
  extend : qx.core.Object,

  /**
   * All arguments can be either a single value, or an array of four values in the order of 
   * [ top, right, bottom, left ]. The contructor does not recognize an array of four identical 
   * values colors as one single value. 
   * 
   * @param width Integer. Multiple values can be given for rounded border but only the 
   *        biggest and zero may be used. Null is allowed.
   * @param style String, either a browser-recognized border-style, or "complex" or "rounded".
   *        The last two are not accepted as an array, only as a single string. 
   * @param color String in any browser-recognized color-format. For rounded border only one 
   *        color may be given.  
   * @param colorsOrRadii String/number either innerColors(s) for "complex" or radius/radii 
   *        for "rounded" border. Throws exception if its undefined for those or defiend for others.
   */
  construct : function( width, style, color, colorsOrRadii ) {
    this.base( arguments );
    this._colors = null;
    this._widths = null;
    this._styles = null;
    this._innerColors = [ null, null, null, null ];
    this._radii = [ null, null, null, null ];
    this._singleColor = null;
    this._singleStyle = null;
    this._setWidth( width );
    this._setStyle( style ? style : "none" );
    this._setColor( color ? color : "" );
    if( style === "complex" ) {
      if( colorsOrRadii === undefined ) {
        throw new Error( "Missing innerColors" );
      }
      this._setInnerColor( colorsOrRadii );
    } else if( style === "rounded" ) {
      if( colorsOrRadii === undefined || this.getColor() === null ) {
        throw new Error( "Invalid arguments for border style rounded" );
      }
      this._setRadii( colorsOrRadii );        
    } else if( colorsOrRadii !== undefined ) {
      throw new Error( "colorsOrRadii set for style " + this.getStyle() );
    }  
  },
  
  statics : {
    _EDGEWIDTH : [ "borderTopWidth", "borderRightWidth", "borderBottomWidth", "borderLeftWidth" ],
    _EDGECOLOR : [ "borderTopColor", "borderRightColor", "borderBottomColor", "borderLeftColor" ],
    _EDGESTYLE : [ "borderTopStyle", "borderRightStyle", "borderBottomStyle", "borderLeftStyle" ],
    _EDGEMOZCOLORS : [
      "MozBorderTopColors", 
      "MozBorderRightColors", 
      "MozBorderBottomColors", 
      "MozBorderLeftColors" 
    ],
    _BORDERRADII : qx.core.Variant.select( "qx.client", {
      "webkit" : [
        "-webkit-border-top-left-radius", 
        "-webkit-border-top-right-radius", 
        "-webkit-border-bottom-right-radius", 
        "-webkit-border-bottom-left-radius" 
      ],
      "gecko" : [ 
        "borderTopLeftRadius", 
        "borderTopRightRadius", 
        "borderBottomRightRadius", 
        "borderBottomLeftRadius"
      ],
      "default" : []
    } ), 
    
    
    reset : function( widget ) {
      widget._style.border = "";
      if( widget._innerStyle ) {
        widget._innerStyle.border = "";
      }
      try{
        for( var i = 0; i < 4; i++ ) {
          widget._style[ this._BORDERRADII[ i ] ] = "";
        }
      } catch( ex ) {
        //ignore
      }
      this._resetComplexBorder( widget );
    },
    
    _resetComplexBorder : qx.core.Variant.select("qx.client", {
      "gecko" : function( widget ) {
        var statics = org.eclipse.rwt.Border
        var style = widget._style;
        for( var i = 0; i < 4; i++ ) {
          style[ statics._EDGEMOZCOLORS[ i ] ] = "";
        }
      }, 
      "default" : function( widget ) {
        var statics = org.eclipse.rwt.Border
        var inner = widget._innerStyle;
        if( inner ) {
          for( var i = 0; i < 4; i++ ) {
            inner[ statics._EDGEWIDTH[ i ] ] = "";
            inner[ statics._EDGESTYLE[ i ] ] = "";
            inner[ statics._EDGECOLOR[ i ] ] = "";
          }
        }
      }
    } )

  },

  members : {
    _setColor : function( value ) {
      if( typeof value === "string" ) {
        this._singleColor = value;
      }
      this._colors = this._normalizeValue( value );
    },
    
    _setWidth : function( value ) {
      this._widths = this._normalizeValue( value );
    },
    
    _setStyle : function( value ) {
      if( typeof value === "string" ) {
        this._singleStyle = value;
        if( value === "complex" || value === "rounded" ) {
          this._styles = this._normalizeValue( "solid" );          
        } else {
          this._styles = this._normalizeValue( value );          
        }
      } else {
        this._styles = this._normalizeValue( value );
      }
    }, 

    _setInnerColor : function( value ) {
      this._innerColors = this._normalizeValue( value );
    },
    
    _setRadii : function( value ) {
      this._radii = this._normalizeValue( value );
    },
    
    getRadii : function() {
      return this._radii.concat();
    },
    
    getColor : function() {
      return this._singleColor;
    },

    getColors : function() {
      return this._colors.concat();
    },
    
    getColorTop : function() {
      return this._colors[ 0 ];
    },

    getColorRight : function() {
      return this._colors[ 1 ];
    },

    getColorBottom : function() {
      return this._colors[ 2 ];
    },

    getColorLeft : function() {
      return this._colors[ 3 ];
    },

    getInnerColors : function() {
      return this._innerColors.concat();
    },

    getColorInnerTop : function() {
      return this._innerColors[ 0 ];
    },

    getColorInnerRight : function() {
      return this._innerColors[ 1 ];
    },

    getColorInnerBottom : function() {
      return this._innerColors[ 2 ];
    },

    getColorInnerLeft : function() {
      return this._innerColors[ 3 ];
    },

    getStyle : function() {
      return this._singleStyle;
    },
    
    getStyles : function() {
      return this._styles.concat();
    },

    getStyleTop : function() {
      return this._styles[ 0 ];
    },

    getStyleRight : function() {
      return this._styles[ 1 ];
    },

    getStyleBottom : function() {
      return this._styles[ 2 ];
    },

    getStyleLeft : function() {
      return this._styles[ 3 ];
    },

    getWidths : function() {
      return this._widths.concat();
    },

    getWidthTop : function() {
      return this._widths[ 0 ];
    },

    getWidthRight : function() {
      return this._widths[ 1 ];
    },

    getWidthBottom : function() {
      return this._widths[ 2 ];
    },

    getWidthLeft : function() {
      return this._widths[ 3 ];
    },

    _normalizeValue : function( value ) {
      var result;
      if( value instanceof Array ) {
        result = value;
      } else {
        result = [ value, value, value, value ];
      }
      return result;
    },

    render : function( widget ) {
      if( this.getStyle() === "complex" ) {
        this._renderComplexBorder( widget );
      } else if( this.getStyle() === "rounded" ) {
        this._renderRoundedBorder( widget );        
      } else {
        this._renderSimpleBorder( widget );        
      }
    },
    
    _renderSimpleBorder : function( widget ) {
      org.eclipse.rwt.Border._resetComplexBorder( widget );
      var style = widget._style;
      var statics = org.eclipse.rwt.Border
      for( var i = 0; i < 4; i++ ) {
        style[ statics._EDGEWIDTH[ i ] ] = ( this._widths[ i ] || 0 ) + "px";
        style[ statics._EDGESTYLE[ i ] ] = this._styles[ i ] || "none";
        style[ statics._EDGECOLOR[ i ] ] = this._colors[ i ] || "";
      }
    },

    _renderComplexBorder : qx.core.Variant.select("qx.client", {
      "gecko" : function( widget ) {
        var statics = org.eclipse.rwt.Border
        var style = widget._style;
        for( var i = 0; i < 4; i++ ) {
          style[ statics._EDGEWIDTH[ i ] ] = ( this._widths[ i ] || 0 ) + "px";
          style[ statics._EDGECOLOR[ i ] ] = this._colors[ i ] || "";
          if( this._widths[ i ] === 2 ) {
            style[ statics._EDGESTYLE[ i ] ] = "solid";
            style[ statics._EDGEMOZCOLORS[ i ] ] = this._colors[ i ] + " " + this._innerColors[ i ];
          } else {
            style[ statics._EDGESTYLE[ i ] ] = this._styles[ i ] || "none";
            style[ statics._EDGEMOZCOLORS[ i ] ] = "";
          }
        }
      },
      "default" : function( widget ) {
        var statics = org.eclipse.rwt.Border
        var outer = widget._style;
        var inner = widget._innerStyle;
        for( var i = 0; i < 4; i++ ) {
          if( this._widths[ i ] === 2 ) {
            if( !inner ) {
              widget.prepareEnhancedBorder();
              inner = widget._innerStyle;
            }
            outer[ statics._EDGEWIDTH[ i ] ] = "1px"
            outer[ statics._EDGESTYLE[ i ] ] = "solid";
            outer[ statics._EDGECOLOR[ i ] ] = this._colors[ i ] || "";
            inner[ statics._EDGEWIDTH[ i ] ] = "1px"
            inner[ statics._EDGESTYLE[ i ] ] = "solid";
            inner[ statics._EDGECOLOR[ i ] ] = this._innerColors[ i ];
          } else {
            outer[ statics._EDGEWIDTH[ i ] ] = ( this._widths[ i ] || 0 ) + "px";
            outer[ statics._EDGESTYLE[ i ] ] = this._styles[ i ] || "none";
            outer[ statics._EDGECOLOR[ i ] ] = this._colors[ i ] || "";
            if( inner ) {
              inner[ statics._EDGEWIDTH[ i ] ] = "";
              inner[ statics._EDGESTYLE[ i ] ] = "";
              inner[ statics._EDGECOLOR[ i ] ] = "";
            }
          }
        }
      }
    } ),

    _renderRoundedBorder : function( widget ) {
      var statics = org.eclipse.rwt.Border
      this._renderSimpleBorder( widget );
      var style = widget._style;
      for( var i = 0; i < 4; i++ ) {
        style[ statics._BORDERRADII[ i ] ] = this._radii[ i ] + "px";
      }
    }

  }

} );
