/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
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
      var node = document.createElement( "div" );
      node.style.position = "absolute"
      node.style.width = "100%";
      node.style.height = "100%";
      node.style.top = "0";
      node.style.left = "0";
      node.style.fontSize = "0";
      node.style.lineHeight = "0";
      result.node = node;
      result.children = {};
      return result;
    },
    
    clearCanvas : function( canvas ) {
      for( var hash in canvas.children ) {
        canvas.node.removeChild( canvas.children[ hash ].node );
      }
      canvas.children = {};
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
        case "custom":
          result = this._createCustomShape();
          result.blurRadius = 0;
        break;
        case "image":
          result = this._createImage();
        break;
        default: 
          throw "VML does not support shape " + type;
        break;
      }
      result.restoreData = { "fill" : {} };
      result.node.stroked = false;
      // TODO [tb] : test if stroke-node conflicts with stroke-properties on 
      // the element-node when moved in dom. 
      var fill = this._createNode( "fill" );
      fill.method = "sigma";
      result.node.appendChild( fill );
      result.fill = fill;
      this.setFillColor( result, null );
      return result;
    },
    
    addToCanvas : function( canvas, shape, beforeShape ) {
      var hash = qx.core.Object.toHashCode( shape );
      canvas.children[ hash ] = shape;
      //canvas.node.appendChild( shape.node );
      if( beforeShape ) {
        canvas.node.insertBefore( shape.node, beforeShape.node );
      } else {
        canvas.node.appendChild( shape.node );
      }
    },
    
    enableOverflow : function( canvas ) {
      // nothing to do
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

    /**
     * "crop" is an optional array [ top, right, bottom, left ]. The values have 
     * to be between 0 and 1, representing percentage of the image-dimension.
     */
    setImageData : function( shape, src, x, y, width, height, crop ) {
      var node = shape.node;
      node.src = src;
      if( typeof crop != "undefined" ) {
        node.cropTop = crop[ 0 ];
        node.cropRight = crop[ 1 ];
        node.cropBottom =  crop[ 2 ];
        node.cropLeft = crop[ 3 ];
      } 
      node.style.width = width;
      node.style.height = height;
      node.style.left = x;
      node.style.top = y;
    },

    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      var quarter = this._VMLDEGREE * 90;
      var radiusLeftTop = this._convertNumeric( radii[ 0 ], false );
      var radiusTopRight = this._convertNumeric( radii[ 1 ], false );
      var radiusRightBottom = this._convertNumeric( radii[ 2 ], false );
      var radiusBottomLeft = this._convertNumeric( radii[ 3 ], false );
      var bluroffsets = this._getBlurOffsets( shape.blurRadius );
      var rectLeft = this._convertNumeric( x - bluroffsets[ 1 ], true );
      var rectTop = this._convertNumeric( y - bluroffsets[ 1 ], true )
      var rectWidth = this._convertNumeric( width - bluroffsets[ 2 ], false );
      var rectHeight = this._convertNumeric( height - bluroffsets[ 2 ], false );
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
      shape.node.path = path.join( " " );      
    },
    
    applyDrawingContext : function( shape, context, fill ) {
      var opacity = context.globalAlpha;
      if( opacity != 1 ) {
        this.setOpacity( shape, opacity );
      }
      if( fill ) {
        var fill = context.fillStyle;
        if( fill instanceof Array ) {
          this.setFillGradient( shape, context.fillStyle );
        } else {
          this.setFillColor( shape, context.fillStyle );
        }
        this.setStroke( shape, null, 0 );
      } else {
        this.setFillColor( shape, null );
        this.setStroke( shape, context.strokeStyle, context.lineWidth );
        var endCap = context.lineCap == "butt" ? "flat" : context.lineCap;
        var joinStyle = context.lineJoin;
        var miterLimit = context.miterLimit;
        this._setStrokeStyle( shape, joinStyle, miterLimit, endCap );
      }
      shape.node.path = this._convertPath( context._currentPath );
    },
    
    createShapeFromContext : function( context, fill ) {
      var shape = this.createShape( "custom" );
      this.applyDrawingContext( shape, context, fill );
      return shape;
    },
    
    setFillColor : function( shape, color ) {
      var fill = shape.fill;
      fill.type = "solid";
      if( color != null ) {
        this._setFillEnabled( shape, true );
        fill.color = color;
        shape.restoreData.fill.color = color;
      } else {
        this._setFillEnabled( shape, false );
        delete shape.restoreData.fill.color;
      }
    },
    
    getFillColor : function( shape ) {
      var result = null;
      if( this.getFillType( shape ) == "color" ) {
        result = shape.restoreData.fill.color;
      }
      return result;
    },

    setFillGradient : function( shape, gradient ) {
      var fill = shape.fill;
      if( gradient != null ) {
        shape.node.removeChild( shape.fill );
        this._setFillEnabled( shape, true );
        delete shape.restoreData.fill.color;
        fill.type = "gradient";
        //the "color" attribute of fill is lost when the node
        //is removed from the dom. However, it can be overwritten
        //by a transition colors, so it doesn't matter
        var startColor = gradient[ 0 ][ 1 ];
        //fill.color = startColor;
        fill.color2 = gradient[ gradient.length - 1 ][ 1 ];
        fill.angle = gradient.horizontal ? 270 : 180;
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
        shape.restoreData.strokecolor = color;
        shape.restoreData.strokeweight = width + "px";
        // TODO [tb] : joinstyle 
      } else {
        shape.node.stroked = false;
        delete shape.restoreData.strokecolor;
        delete shape.restoreData.strokeweight;
      }
    },
    
    getStrokeWidth : function( shape ) {
      // IE returns strokeweight either as number (then its pt)
      // or as string with a "px" or "pt" postfix
      var result = false;
      if( shape.node.stroked ) {
        result = shape.node.strokeweight;
        var isPt = typeof result == "number" || result.search( "pt" ) != -1;
        result = parseFloat( result );                     
        result = isPt ? result / 0.75 : result;
      }
      return result; 
    },
    
    setOpacity : function( shape, opacity ) {
      shape.opacity = opacity;
      this._renderFilter( shape );
      this._setAntiAlias( shape, opacity < 1 );
    },
    
    getOpacity : function( shape ) {
      var result = 1;
      if( typeof shape.opacity === "number" && shape.opacity < 1 ) {
        result = shape.opacity;
      }
      return result;
    },
    
    setBlur : function( shape, radius ) {
      // NOTE: IE shifts the shape to the bottom-right, 
      // compensated ONLY in setRoundRectLayout
      shape.blurRadius = radius;
      this._renderFilter( shape );
    },
    
    getBlur : function( shape, radius ) {
      var result = 0;
      if( typeof shape.blurRadius === "number" && shape.blurRadius > 0 ) {
        result = shape.blurRadius;
      }
      return result;
    },
    
    _renderFilter : function( shape ) {
      var filterStr = [];
      var opacity = this.getOpacity( shape );
      var blurRadius = this.getBlur( shape );
      if( opacity < 1 ) {
        filterStr.push( "Alpha(opacity=" );
        filterStr.push( Math.round( opacity * 100 ) );
        filterStr.push( ")" );
      }
      if( blurRadius > 0 ) {
        filterStr.push( "progid:DXImageTransform.Microsoft.Blur(pixelradius=" ); 
        filterStr.push( this._getBlurOffsets( blurRadius )[ 0 ] );
        filterStr.push( ")" );
      }
      if( filterStr.length > 0 ) {
        shape.node.style.filter = filterStr.join( "" );
      } else {
        org.eclipse.rwt.HtmlUtil.removeCssFilter( shape.node );
      }
    },
    
    /////////
    // helper

    _VMLFACTOR : 10,
    _VMLDEGREE : -65535, 
    _VMLRAD : -65535 * ( 180 / Math.PI ),
        
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
    
    _createImage : function() {
      var result = {};
      result.type = "vmlImage";
      var node = this._createNode( "image" );
      node.style.position = "absolute"
      result.node = node;
      return result;      
    },
    
    _createCustomShape : function() {
      var result = {};
      var node = this._createNode( "shape" );
      var coordsize = 100 * this._VMLFACTOR + ", " + 100 * this._VMLFACTOR;
      node.coordsize = coordsize;
      node.coordorigin = "0 0";
      node.style.position = "absolute";
      node.style.width = 100;
      node.style.height = 100;
      node.style.top = 0;
      node.style.left = 0;
      result.node = node;
      return result;      
    },

    _setFillEnabled : function( shape, value ) {
      shape.fill.on = value;
      shape.restoreData.fill.on = value;
    },

    _ensureStrokeNode : function( shape ) {
      if( !shape.stroke ) {
        var stroke = this._createNode( "stroke" );
        shape.node.appendChild( stroke );
        shape.stroke = stroke;
      }      
    },

    _setStrokeStyle : function( shape, joinStyle, miterLimit, endCap ) {
      this._ensureStrokeNode( shape );
      shape.stroke.joinstyle = joinStyle;
      shape.stroke.miterlimit = miterLimit;
      shape.stroke.endcap = endCap;
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
    
    _copyData : function( source, target ) {
      if( !source || !target ) {
        throw "VML._copyData: source or target missing!";
      }
      for( var key in source ) {
        var value = source[ key ];
        if( typeof value === "object" ) {
          this._copyData( value, target[ key ] );
        } else {
          target[ key ] = value;
        }
      }
    },

    _handleAppearShape : function( shape ) {
      this._copyData( shape.restoreData, shape.node );
    },

    _transitionColorPart : function( color1, color2, pos ) {
      var part = parseInt( color1 ) + ( ( color2 - color1 ) * pos );
      return Math.round( part );
    },
    
    _convertNumeric : function( value, fixOffset ) {
      var result;
      if( typeof value == "number" ) {
        result = ( fixOffset ? value - 0.5 : value ) * this._VMLFACTOR;
        result = Math.round( result );        
      } else {
        result = value;
      }      
      return  result;
    },
    
    _convertPath : function( path ) {
      var string = [];
      for( var i = 0; i < path.length; i++ ) {
        var item = path[ i ];
        switch( item.type ) {
          case "moveTo":
            string.push( "M" )
            string.push( this._convertNumeric( item.x, true ) );
            string.push( this._convertNumeric( item.y, true ) );
          break;
          case "lineTo":
            string.push( "L" );
            string.push( this._convertNumeric( item.x, true ) );
            string.push( this._convertNumeric( item.y, true ) );
          break;
          case "close":
            string.push( "X" );
            item = null;
          break;
          case "quadraticCurveTo":
            string.push( "QB" );
            string.push( this._convertNumeric( item.cp1x, true ) );
            string.push( this._convertNumeric( item.cp1y, true ) );
            string.push( "L" ); // a bug in VML requires this
            string.push( this._convertNumeric( item.x, true ) );
            string.push( this._convertNumeric( item.y, true ) );
          break;
          case "bezierCurveTo":
            string.push( "C" );
            string.push( this._convertNumeric( item.cp1x, true ) );
            string.push( this._convertNumeric( item.cp1y, true ) );
            string.push( this._convertNumeric( item.cp2x, true ) );
            string.push( this._convertNumeric( item.cp2y, true ) );
            string.push( this._convertNumeric( item.x, true ) );
            string.push( this._convertNumeric( item.y, true ) );
          break;
          case "arc":
            string.push( "AE" );
            var startAngle = Math.round( item.startAngle * this._VMLRAD );
            var endAngle = Math.round( item.endAngle * this._VMLRAD );
            string.push( this._convertNumeric( item.centerX, true ) );
            string.push( this._convertNumeric( item.centerY, true ) );
            string.push( this._convertNumeric( item.radiusX, false ) );
            string.push( this._convertNumeric( item.radiusY, false ) );
            string.push( startAngle );
            string.push( endAngle - startAngle );
          break;
        }
      }
      return string.join( " " );
    },
    
    _setAntiAlias : function( shape, value ) {
      shape.node.style.antialias = value;
    },
    
    _getBlurOffsets : function( blurradius ) {
      // returns [ blurradius, location-offset, dimension-offset ]
      var result;
      var offsets = this._BLUROFFSETS[ blurradius ];
      if( offsets !== undefined ) {
        result = offsets;
      } else {
        result = [ blurradius, blurradius, 1 ];
      }
      return result;
    },
    
    _BLUROFFSETS : [
      // NOTE: these values are chosen to resemble the blur-effect on css3-shadows
      // as closely as possible, but in doubt going for the stronger effect. In IE9
      // the effect does not seem consistent: Sometimes its like in IE7/8, but most of the time
      // much weaker (around 1/3). Would be solved if we use CSS3 in IE9.
      [ 0, 0, 0 ],
      [ 2, 2, 1 ],
      [ 3, 3, 1 ], 
      [ 4, 4, 1 ] 
    ]
  }

} );