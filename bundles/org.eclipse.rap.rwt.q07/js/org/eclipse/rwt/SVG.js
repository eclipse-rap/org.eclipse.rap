/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.SVG", {

  statics : {
    
    init : function(){ 
      // nothing to do
    },
    
    createCanvas : function() {
      var result = {};
      var node = this._createNode( "svg" );
      node.style.position = "absolute"
      node.style.left = "0px";
      node.style.right = "0px";
      node.style.width = "100%";
      node.style.height = "100%"
      node.style.overflow = "hidden";
      var defs = this._createNode( "defs" );
      node.appendChild( defs );
      result.type = "svgCanvas";
      result.node = node;     
      result.defsNode = defs;
      return result;
    },
    
    setLayoutMode : function( canvas, mode ) {
      // nothing to do
    },
    
    getCanvasNode : function( canvas ) {
      return canvas.node;
    },
    
    handleAppear : function( canvas ) {
      // nothing to do
    },
       
    createShape : function( type ) {
      var result;
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
      result.node.setAttribute( "stroke", "none" );
      result.node.setAttribute( "fill", "none" );
      result.defNodes = {};
      result.parent = null
      return result;
    },
    
    addToCanvas : function( canvas, shape ) {
      shape.parent = canvas;
      canvas.node.appendChild( shape.node );
      this._attachDefinitions( shape );
    },
    
    removeFromCanvas : function( canvas, shape ) {
      this._detachDefinitions( shape );
      canvas.node.removeChild( shape.node );
      shape.parent = null;
    },
    
    setDisplay : function( shape, value ) {
      shape.node.setAttribute( "display", value ? "inline" : "none" );
    },
    
    getDisplay : function( shape ) {
      var display = shape.node.getAttribute( "display" );
      var result = display == "none" ? false : true;
      return result;
    },

    setRectBounds : function( shape, x, y, width, height ) {
      var node = shape.node;      
      node.setAttribute( "width", this._convertNumeric( width ) );
      node.setAttribute( "height", this._convertNumeric( height ) );
      node.setAttribute( "x", this._convertNumeric( x ) );
      node.setAttribute( "y", this._convertNumeric( y ) );
    },
    
    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      var radiusLeftTop = radii[ 0 ];
      var radiusTopRight = radii[ 1 ];
      var radiusRightBottom = radii[ 2 ];
      var radiusBottomLeft = radii[ 3 ];
      if(    ( radiusLeftTop + radiusTopRight ) > width
          || ( radiusRightBottom  + radiusBottomLeft ) > width
          || ( radiusLeftTop + radiusBottomLeft ) > height
          || ( radiusRightBottom + radiusTopRight ) > height )
      {
        radiusLeftTop = 0;
        radiusTopRight = 0;
        radiusRightBottom = 0;
        radiusBottomLeft = 0;         
      }
      var path = [];
      path.push( "M", x , y + radiusLeftTop );
      if( radiusLeftTop > 0 ) {
        path.push( "A", radiusLeftTop, radiusLeftTop, 0, 0, 1 );
        path.push( x + radiusLeftTop, y );
      }
      path.push( "L", x + width - radiusTopRight, y );
      if( radiusTopRight > 0 ) {
        path.push( "A", radiusTopRight, radiusTopRight, 0, 0, 1 );
      }
      path.push( x + width, y + radiusTopRight);
      path.push( "L", x + width, y + height - radiusRightBottom );
      if( radiusRightBottom > 0 ) {
        path.push( "A", radiusRightBottom, radiusRightBottom, 0, 0, 1 );
      }
      path.push( x + width - radiusRightBottom, y + height );
      path.push( "L", x + radiusBottomLeft, y + height );
      if( radiusBottomLeft > 0 ) {
        path.push( "A", radiusBottomLeft, radiusBottomLeft, 0, 0, 1 );
      }
      path.push( x , y + height - radiusBottomLeft );
      path.push( "Z" );      
      shape.node.setAttribute( "d", path.join(" ") );
    },

    setFillColor : function( shape, color ) {
      this.setFillGradient( shape, null );
      if( color != null ) {
        shape.node.setAttribute( "fill", color );
      } else {
        shape.node.setAttribute( "fill", "none" );
      }
    },
    
    setFillGradient : function( shape, gradient ) {
      if( gradient != null ) {
        var id = "gradient_" + qx.core.Object.toHashCode( shape );
        var gradNode;
        if( typeof shape.defNodes[ id ] == "undefined" ) {
          gradNode = this._createNode( "linearGradient" ); 
          gradNode.setAttribute( "id", id );
          gradNode.setAttribute( "x1", 0 );
          gradNode.setAttribute( "y1", 0 );
          gradNode.setAttribute( "x2", 0 );
          gradNode.setAttribute( "y2", 1 );
          this._addNewDefinition( shape, gradNode, id );
        } else {
          gradNode = shape.defNodes[ id ];
        }
        // clear old colors:
        var stopColor = null;
        while( stopColor = gradNode.childNodes[ 0 ] ) {
          gradNode.removeChild( stopColor );
        }
        // set new colors 
        for( var colorPos = 0; colorPos < gradient.length; colorPos++ ) {
          stopColor = this._createNode( "stop" );
          stopColor.setAttribute( "offset", gradient[ colorPos ][ 0 ] );
          stopColor.setAttribute( "stop-color", gradient[ colorPos ][ 1 ] );
          gradNode.appendChild( stopColor );
        }
        shape.node.setAttribute( "fill", "url(#" + id + ")" );
      } else {
        shape.node.setAttribute( "fill", "none" );
      }
    },

    setFillPattern : function( shape, source, width, height ) {
      if( source != null ) {
        var hash = qx.core.Object.toHashCode( shape );
        var patternId = "pattern_" + hash;
        var patternNode;
        var imageNode;
        if( typeof shape.defNodes[ patternId ] == "undefined" ) {
          patternNode = this._createNode( "pattern" ); 
          patternNode.setAttribute( "id", patternId );
          patternNode.setAttribute( "x", 0 );
          patternNode.setAttribute( "y", 0 );
          patternNode.setAttribute( "patternUnits", "userSpaceOnUse" );                      
          imageNode = this._createNode( "image" );       
          imageNode.setAttribute( "x", 0 );
          imageNode.setAttribute( "y", 0 );          
          imageNode.setAttribute( "preserveAspectRatio", "none" );          
          patternNode.appendChild( imageNode );                           
          this._addNewDefinition( shape, patternNode, patternId );          
        } else {
          patternNode = shape.defNodes[ patternId ];
          imageNode = patternNode.firstChild;
        }
        // the "-1" offset drastically reduces the white lines between
        // the tiles when zoomed in firefox.
        patternNode.setAttribute( "width", width - 1 );
        patternNode.setAttribute( "height", height - 1 );
        imageNode.setAttribute( "width", width );
        imageNode.setAttribute( "height", height );
        shape.node.setAttribute( "fill", "url(#" + patternId + ")" );
        if( qx.core.Client.getEngine() == "webkit" ) {
          // Bug 301236: Loading an image using SVG causes a bad request
          // AFTER the image-request. Prevent by pre-loading the image. 
          var loader = new Image();
          loader.src = source;
          var that = this;
          loader.onload = function() { 
            that._setXLink( imageNode, source );
            org.eclipse.rwt.SVG._redrawWebkit( shape );
          };
        } else {
          this._setXLink( imageNode, source );          
        }
      } else {
        shape.node.setAttribute( "fill", "none" );
      }
    },
    
    getFillType : function( shape ) {
      var result = shape.node.getAttribute( "fill" );     
      if( result.search( "pattern_") != -1 ) {
        result = "pattern";
      } else if( result.search( "gradient_") != -1 ) { 
        result = "gradient";
      } else if( result == "none" ) { 
        result = null;
      } else {
        result = "color";
      }
      return result;      
    },

    setStroke : function( shape, color, width ) {
      shape.node.setAttribute( "stroke-width", width + "px" );
      // needed due to a bug in Google Chrome (see bug 300509 ):
      if( width == 0 ) {
        shape.node.setAttribute( "stroke", "none" );
      } else {
        shape.node.setAttribute( "stroke", color != null ? color : "none" );
      }      
    },

    /////////
    // helper
        
    _createNode : function( type ) {
      return document.createElementNS( "http://www.w3.org/2000/svg", type );
    },    
    
    _createRect : function() {
      var result = {};
      result.type = "svgRect";
      var node = this._createNode( "rect" );
      node.setAttribute( "width", "0" );
      node.setAttribute( "height", "0" );
      node.setAttribute( "x", "0" );
      node.setAttribute( "y", "0" );
      result.node = node;
      return result;      
    },
    
    _setXLink : function( node, value ) {
      node.setAttributeNS( "http://www.w3.org/1999/xlink", "href", value ); 
    },

    _createRoundRect : function() {
      var result = {};
      result.type = "svgRoundRect";
      var node = this._createNode( "path" );
      result.node = node;
      return result;      
    },
    
    _addNewDefinition : function( shape, node, id ) {
      shape.defNodes[ id ] = node;
      if( shape.parent != null ) {     
        shape.parent.defsNode.appendChild( node );
      }
    },
    
    // TODO [tb] : optimize so only the currently needed defs. are attached?
    _attachDefinitions : function( shape ) {
      for( var id in shape.defNodes ) {
        var node = shape.defNodes[ id ];
        shape.parent.defsNode.appendChild( node );
      }
    },
    
    _detachDefinitions : function( shape ) {
      for( var id in shape.defNodes ) {
        var node = shape.defNodes[ id ];
        node.parentNode.removeChild( node );
      }
    },
    
    _convertNumeric : function( value ) {
      return typeof value == "string" ? value : value + "px";
    },
   
    _redrawWebkit : function( shape ) {      
      var wrapper = function() {
        org.eclipse.rwt.SVG._redrawWebkitCore( shape );
      };
      window.setTimeout( wrapper, 10 );
    },

    _redrawWebkitCore : function( shape ) {
      if( shape.parent != null ) {
        shape.node.style.webkitTransform = "scale(1)"; 
      }
    },
    
    // TODO [tb] : remove if no longer needed:
    
    _dummyNode : null,
    
    _getDummyNode : function() {
      if( this._dummyNode == null ) {
        this._dummyNode = this._createNode( "rect" );
      }
      return this._dummyNode;
    }
    
  }

} );