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
        var child = children[ hash ];
        if( typeof child.restoreColor != "undefined" ) {
          if( child.restoreColor != null ) {
            child.fill.color = child.restoreColor; 
          } else {
            child.fill.on = false;
          }
        }
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
      fill.on = false;
      result.restoreColor = null;
      result.node.appendChild( fill );
      result.fill = fill;
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
    
    setRectBounds : function( shape, x, y, width, height ) {
      var node = shape.node;      
      node.style.width = this._convertNumeric( width, false );
      node.style.height = this._convertNumeric( height, false );
      node.style.left = this._convertNumeric( x, true );
      node.style.top = this._convertNumeric( y, true );
    },
    
    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      var quarter = this._VMLQCIRCEL;
      var maxRadius = Math.floor( Math.min( width, height ) / 2 );
      var radiusLeftTop 
        = this._convertNumeric( Math.min( radii[ 0 ], maxRadius ), false );
      var radiusTopRight 
        = this._convertNumeric( Math.min( radii[ 1 ], maxRadius ), false );
      var radiusRightBottom 
        = this._convertNumeric( Math.min( radii[ 2 ], maxRadius ), false );
      var radiusBottomLeft 
        = this._convertNumeric( Math.min( radii[ 3 ], maxRadius ), false );      
      var rectLeft = this._convertNumeric( x, true );
      var rectTop = this._convertNumeric( y, true )
      var rectWidth = this._convertNumeric( width, false );
      var rectHeight = this._convertNumeric( height, false );

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
        fill.on = true;
        fill.color = color;
      } else {
        fill.on = false;
      }
      shape.restoreColor = color;
    },
    
    setFillGradient : function( shape, gradient ) {
      var fill = shape.fill;
      if( gradient != null ) {
        shape.node.removeChild( shape.fill );
        fill.on = true;
        fill.type = "gradient";
        //the "color" attribute of fill is lost when the node
        //is removed from the dom. However, it can be overwritten
        //by a transition colors, so it doesn't matter
        var startColor = gradient[ 0 ][ 1 ];
        //fill.color = startColor;
        delete shape.restoreColor;
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
        fill.on = false;        
      }
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
      result.node = node;
      return result;      
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