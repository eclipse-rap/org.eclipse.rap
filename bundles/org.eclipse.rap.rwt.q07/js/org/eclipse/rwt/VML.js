/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.VML", {

  statics : {
    
    init : function() {
      document.namespaces.add( "v", "urn:schemas-microsoft-com:vml");
      document.namespaces.add( "o",
                               "urn:schemas-microsoft-com:office:office");
      var sheet = document.createStyleSheet();
      sheet.cssText = "v\\:* { behavior:url(#default#VML);" +
                              "display:inline-block; } "+
                      "o\\:* { behavior: url(#default#VML);}";
      this._vmlEnabled = true;      
    },
    
    createCanvas : function() {
      var result = {};
      result.type = "vmlCanvas";
      var node = this._createNode( "group" );
      node.style.position = "absolute"
      node.style.width = "100%";
      node.style.height = "100%";
      node.style.top = "0";
      node.style.left = "0";
      result.node = node;
      result.children = {};
      this.setLayoutMode( result, "relative" );      
      return result;
    },

    setLayoutMode : function( canvas, mode ) {
      if( mode == "absolute" ) {
        var node = canvas.node;
        node.style.width = 100 + "px";
        node.style.height = 100 + "px";
        var coordsize = 100 * this._VMLFACTOR + "," + 100 * this._VMLFACTOR;
        node.setAttribute( "coordsize", coordsize );
      } else if( mode == "relative" ) {
        var node = canvas.node;
        node.style.width = "100%";
        node.style.height = "100%";
        node.setAttribute( "coordsize", "1000, 1000" );
      }
    },

    getCanvasNode : function( canvas ) {
      return canvas.node;
    },
    
    handleAppear : function( canvas ) {
      var children = canvas.children;
      for( var hash in children ) {
        this._handleAppearShape( children[ hash ] );
      }
    },
    
    createShape : function( type ) {
      var result = null;
      switch( type ) {
        case "rect":
          result = this._createRect();
        break;
        case "roundrect":
          result = this._createRoundRect();
        break;
        default: 
          throw "invalid shape " + type;
        break;
      }
      result.node.stroked = false;
      var fill = this._createNode( "fill" );
      fill.method = "sigma";
      fill.angle = 180;
      result.node.appendChild( fill );
      result.fill = fill;
      this.setFillColor( result, null );
      return result;
    },
    
    addToCanvas : function( canvas, shape ) {
      var hash = qx.core.Object.toHashCode( shape );
      canvas.children[ hash ] = shape;
      canvas.node.appendChild( shape.node );
    },
    
    removeFromCanvas : function( canvas, shape ) {
      var hash = qx.core.Object.toHashCode( shape );
      delete canvas.children[ hash ];
      canvas.node.removeChild( shape.node );
    },
    
    setDisplay : function( shape, value ) {
      shape.node.style.display = value ? "" : "none";
    },

    getDisplay : function( shape) {
      var result = shape.node.style.display == "none" ? false : true;
      return result;
    },

    setRectBounds : function( shape, x, y, width, height ) {
      var node = shape.node;      
      node.style.width = this._convertNumeric( width, false );
      node.style.height = this._convertNumeric( height, false );
      node.style.left = this._convertNumeric( x, true );
      node.style.top = this._convertNumeric( y, true );
    },
    
    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      var quarter = this._VMLQCIRCEL;
      var radiusLeftTop = this._convertNumeric( radii[ 0 ], false );
      var radiusTopRight = this._convertNumeric( radii[ 1 ], false );
      var radiusRightBottom = this._convertNumeric( radii[ 2 ], false );
      var radiusBottomLeft = this._convertNumeric( radii[ 3 ], false );
      var rectLeft = this._convertNumeric( x, true );
      var rectTop = this._convertNumeric( y, true )
      var rectWidth = this._convertNumeric( width, false );
      var rectHeight = this._convertNumeric( height, false );
      if(    ( radiusLeftTop + radiusTopRight ) > rectWidth
          || ( radiusRightBottom  + radiusBottomLeft ) > rectWidth
          || ( radiusLeftTop + radiusBottomLeft ) > rectHeight
          || ( radiusRightBottom + radiusTopRight ) > rectHeight )
      {
        radiusLeftTop = 0;
        radiusTopRight = 0;
        radiusRightBottom = 0;
        radiusBottomLeft = 0;         
      }      
      var path = [];
      if( radiusLeftTop > 0 ) {
        path.push( "AL", rectLeft + radiusLeftTop, rectTop + radiusLeftTop );
        path.push( radiusLeftTop, radiusLeftTop, 2 * quarter, quarter );
      } else {
        path.push( "M", rectLeft, rectTop + radiusLeftTop );
      }
      if( radiusTopRight > 0 ) {
        path.push( "AE", rectLeft + rectWidth - radiusTopRight );
        path.push( rectTop + radiusTopRight );
        path.push( radiusTopRight, radiusTopRight, 3 * quarter, quarter );
      } else {
        path.push( "L", rectLeft + rectWidth, rectTop );
      }
      if( radiusRightBottom > 0 ) {
        path.push( "AE", rectLeft + rectWidth - radiusRightBottom );
        path.push( rectTop + rectHeight - radiusRightBottom );
        path.push( radiusRightBottom, radiusRightBottom, 0, quarter );
      } else {
        path.push( "L", rectLeft + rectWidth, rectTop + rectHeight );
      }
      if( radiusBottomLeft > 0 ) {
        path.push( "AE", rectLeft + radiusBottomLeft );
        path.push( rectTop + rectHeight - radiusBottomLeft );
        path.push( radiusBottomLeft, radiusBottomLeft, quarter, quarter );
      } else {
        path.push( "L", rectLeft, rectTop + rectHeight );
      }

      path.push( "X E" );
      shape.node.path = path.join(" ");      
    },   
    
    setFillColor : function( shape, color ) {
      var fill = shape.fill;
      fill.type = "solid";
      if( color != null ) {
        this._setFillEnabled( shape, true );
        fill.color = color;
        shape.restoreColor = color;
      } else {
        this._setFillEnabled( shape, false );
        delete shape.restoreColor;
      }
    },

    setFillGradient : function( shape, gradient ) {
      var fill = shape.fill;
      if( gradient != null ) {
        shape.node.removeChild( shape.fill );
        this._setFillEnabled( shape, true );
        delete shape.restoreColor;
        fill.type = "gradient";
        //the "color" attribute of fill is lost when the node
        //is removed from the dom. However, it can be overwritten
        //by a transition colors, so it doesn't matter
        var startColor = gradient[ 0 ][ 1 ];
        //fill.color = startColor;
        fill.color2 = gradient[ gradient.length - 1 ][ 1 ];
        var transitionColors = "0% " + startColor;
        var lastColor = qx.util.ColorUtil.stringToRgb( startColor );
        var nextColor = null;
        var lastOffset = 0;
        var currentOffset = null;
        for( var colorPos = 1; colorPos < gradient.length; colorPos++ ) {
          var color = gradient[ colorPos ][ 1 ];
          nextColor = qx.util.ColorUtil.stringToRgb( color );
          nextOffset = gradient[ colorPos ][ 0 ];
          transitionColors += ", ";
          transitionColors += this._transitionColors( lastColor, 
                                                      nextColor, 
                                                      lastOffset, 
                                                      nextOffset, 
                                                      3 );
          transitionColors += ", " + ( nextOffset * 100 ) + "% " + color;
          lastColor = nextColor;
          lastOffset = nextOffset;
        }
        fill.colors = transitionColors;
        shape.node.appendChild( fill );
      } else {
        this._setFillEnabled( shape, true );
      }
    },

    setFillPattern : function( shape, source, width, height ) {
      var fill = shape.fill;
      if( source != null ) {
        shape.node.removeChild( shape.fill );
        this._setFillEnabled( shape, true );
        fill.type = "tile";
        fill.src = source;
        // IE only accepts "pt" for the size:
        fill.size = ( width * 0.75 ) + "pt," + ( height * 0.75 ) + "pt";
        shape.node.appendChild( fill );
      } else {
        this._setFillEnabled( shape, false );
      }
    },
    
    getFillType : function( shape ) {
      var on = shape.fill.on;
      var result = !on ? null : shape.fill.type;
      if( result == "solid" ) result = "color";
      if( result == "tile" ) result = "pattern";
      return result;
    },

    // About VML-strokes and opacity:
    // There is a bug in the VML antialiasing, that can produce grey pixels
    // around vml elements if the css-opacity-filter is used on any of its
    // parents, including the widgets div or any of the parent-widgets divs.
    // However this ONLY happens if the element that the opacity is applied to,
    // does NOT have a background of its own!
    // If antialiasing is turned off, the effect is gone, but without
    // antaliasing the element looks just as ugly as with the glitch.    
    setStroke : function( shape, color, width ) {
      if( width > 0 ) {
        shape.node.stroked = true;
        shape.node.strokecolor = color;
        shape.node.strokeweight = width + "px";
        // TODO [tb] : joinstyle (currently not implemented because it would
        // need the subelement "stroke" and create conflict with the other
        // stroke-attributes - IE "forgets" them if the element is moved in DOM)
      } else {
        shape.node.stroked = false;
      }
    },

    /////////
    // helper

    _VMLFACTOR : 10,
    _VMLQCIRCEL : -65535 * 90,
        
    _createNode : function( type ) {
      return document.createElement( "v:" + type );
    },
    
    _createRect : function() {
      var result = {};
      result.type = "vmlRect";
      var node = this._createNode( "rect" );
      node.style.position = "absolute"
      node.style.width = 0;
      node.style.height = 0;
      node.style.top = 0;
      node.style.left = 0;
      node.style.antialias = false;
      result.node = node;
      return result;      
    },
    
    _createRoundRect : function() {
      var result = {};
      var node = this._createNode( "shape" );
      node.coordsize="100,100";
      node.coordorigin="0 0";
      node.style.width = 100;
      node.style.height = 100;
      node.style.top = 0;
      node.style.left = 0;
      result.node = node;
      return result;      
    },
    
    _setFillEnabled : function( shape, value ) {
      shape.fill.on = value;
      shape.restoreFill = value;
    },

    _transitionColors : function( color1, color2, start, stop, steps ) {
      var diff = stop-start;
      var stepwidth = diff / ( steps + 1 );
      var str =[];
      var color3 = [];
      var pos;
      for ( var i = 1; i <= steps; i++ ) {
        pos = i * ( 1 / ( steps + 1 ) );
        color3[ 0 ] = this._transitionColorPart( color1[ 0 ], color2[ 0 ], pos);
        color3[ 1 ] = this._transitionColorPart( color1[ 1 ], color2[ 1 ], pos);
        color3[ 2 ] = this._transitionColorPart( color1[ 2 ], color2[ 2 ], pos);
        str.push(   Math.round( ( ( start + ( i * stepwidth ) ) * 100 ) )
                  + "% RGB(" + color3.join()
                  + ")" );
      }
      return str.join(" ,");
    },
    
    _handleAppearShape: function( shape ) {
      shape.fill.on = shape.restoreFill;
      if(   typeof shape.restoreColor != "undefined" 
         && shape.restoreColor != null ) 
      {
        shape.fill.color = shape.restoreColor;
      }
    },

    _transitionColorPart : function( color1, color2, pos ) {
      var part = parseInt( color1 ) + ( ( color2 - color1 ) * pos );
      return Math.round( part );
    },
    
    _convertNumeric : function( value, fixOffset ) {
      var result;
      if( typeof value == "number" ) {
        result = ( fixOffset ? value - 0.5 : value ) * this._VMLFACTOR;        
      } else {
        result = value;
      }      
      return  result;
    }

  }

} );