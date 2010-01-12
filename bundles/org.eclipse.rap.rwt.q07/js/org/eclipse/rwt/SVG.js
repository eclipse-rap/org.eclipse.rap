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
      result.fillId = null;
      result.fillNode = {};
      result.parent = null
      return result;
    },
    
    addToCanvas : function( canvas, shape ) {
      canvas.node.appendChild( shape.node );
      shape.parent = canvas;
      if( shape.fillId != null ) {
        this._attachFill( shape );
      } 
    },
    
    removeFromCanvas : function( canvas, shape ) {
      if( shape.fillId != null ) {
        this._detachFill( shape );
      }
      canvas.node.removeChild( shape.node );
      shape.parent = null;
    },
    
    setRectBounds : function( shape, x, y, width, height ) {
      var node = shape.node;      
      node.setAttribute( "width", this._convertNumeric( width ) );
      node.setAttribute( "height", this._convertNumeric( height ) );
      node.setAttribute( "x", this._convertNumeric( x ) );
      node.setAttribute( "y", this._convertNumeric( y ) );
    },
    
    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      var maxRadius = Math.min( width, height ) / 2;
      var radiusLeftTop = Math.min( radii[ 0 ], maxRadius );
      var radiusTopRight = Math.min( radii[ 1 ], maxRadius );
      var radiusRightBottom = Math.min( radii[ 2 ], maxRadius );
      var radiusBottomLeft = Math.min( radii[ 3 ], maxRadius );
      var path = [];
      path.push( "M", x , y + radiusLeftTop );
      if( radiusLeftTop > 0 ) {
        path.push( "A", radiusLeftTop, radiusLeftTop, 0, 0, 1);
        path.push( x + radiusLeftTop, y );
      }
      path.push( "L", x + width - radiusTopRight, y );
      if( radiusTopRight > 0 ) {
        path.push( "A", radiusTopRight, radiusTopRight, 0, 0, 1);
      }
      path.push( x + width, y + radiusTopRight);
      path.push( "L", x + width, y + height - radiusRightBottom  );
      if( radiusRightBottom > 0 ) {
        path.push( "A", radiusRightBottom, radiusRightBottom, 0, 0, 1);
      }
      path.push( x + width - radiusRightBottom, y + height );
      path.push( "L", x + radiusBottomLeft, y + height );
      if( radiusBottomLeft > 0 ) {
        path.push( "A", radiusBottomLeft, radiusBottomLeft, 0, 0, 1);
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
        if( typeof shape.fillNode[ id ] == "undefined" ) {
          gradNode = this._createNode( "linearGradient" ); 
          gradNode.setAttribute( "id", id );
          gradNode.setAttribute( "x1", 0 );
          gradNode.setAttribute( "y1", 0 );
          gradNode.setAttribute( "x2", 0 );
          gradNode.setAttribute( "y2", 1 );
          shape.fillNode[ id ] = gradNode;
        } else {
          gradNode = shape.fillNode[ id ];
        }
        if( shape.fillId != id ) {
          if( shape.fillId != null ) {
            this._detachFill( shape );
          }
          shape.fillId = id;
          if( shape.parent != null ) {
            this._attachFill( shape );
          }
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
        if( shape.parent != null && shape.fillId != null ) {
          this._detachFill( shape );
        }
        shape.node.setAttribute( "fill", "none" );
        shape.fillId = null;
      }
    },
    
    setStroke : function( shape, color, width ) {
      shape.node.setAttribute( "stroke-width", width + "px");
      shape.node.setAttribute( "stroke", color != null ? color : "none" );      
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
    
    _createRoundRect : function() {
      var result = {};
      result.type = "svgRoundRect";
      var node = this._createNode( "path" );
      result.node = node;
      return result;      
    },
    
    _attachFill : function( shape ) {
      var id = shape.fillId;
      var node = shape.fillNode[ id ];
      shape.parent.defsNode.appendChild( node );
    },
    
    _detachFill : function( shape ) {
      var id = shape.fillId;
      var node = shape.fillNode[ id ];
      node.parentNode.removeChild( node );
    },
    
    _convertNumeric : function( value ) {
      return typeof value == "string" ? value : value + "px";
    }
    
  }

} );