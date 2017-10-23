/*******************************************************************************
 * Copyright (c) 2010, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.GC", {

  extend : rwt.qx.Object,

  construct : function( control ) {
    this.base( arguments );
    this._control = control;
    this._control.addEventListener( "create", this._onControlCreate, this );
    this._control.addEventListener( "changeWidth", this._onControlChangeWidth, this );
    this._control.addEventListener( "changeHeight", this._onControlChangeHeight, this );
    this._canvas = null;
    this._context = null;
    this._createCanvas();
    this._canvas.rwtObject = this; // like "rwtWidget" in Widget.js, useful for custom JS components
    if( this._control.isCreated() ) {
      this._addCanvasToDOM();
    }
    this._linearGradient = null;
  },

  destruct : function() {
    this._control.removeEventListener( "create", this._onControlCreate, this );
    this._control.removeEventListener( "changeWidth", this._onControlChangeWidth, this );
    this._control.removeEventListener( "changeHeight", this._onControlChangeHeight, this );
    if( this._control.isCreated() && !this._control.isDisposed() ) {
      this._removeCanvasFromDOM();
    }
    this._control = null;
    this._canvas.rwtObject = null;
    this._canvas = null;
    if( this._context.dispose ) {
      this._context.dispose();
    }
    this._context = null;
  },

  members : {

    init : function( x, y, width, height, font, background, foreground  ) {
      this._initClipping( x, y, width, height );
      this._initFields( font, background, foreground );
      this._control.dispatchSimpleEvent( "paint" ); // client-side painting on server-side redraw
    },

    /**
     * Executes drawing operations using the HTML5-Canvas 2D-Context syntax.
     * Only a subset is supported on all browser, especially IE is limited.
     * Each operation is an array starting with the name of the function to call, followed
     * by its parameters. Properties are treated the same way, i.e. [ "propertyName", "value" ].
     * Other differences from official HTML5-Canvas API:
     *  - Colors are to be given as array ( [ red, green blue ] )
     *  - "addColorStop" will automatically applied to the last created gradient.
     *  - To assign the last created linear gradient as a style, use "linearGradient" as the value.
     *  - strokeText behaves like fillText and fillText draws a rectangular background
     *  - ellipse is not a W3C standard, only WHATWG, but we need it for SWT arc to work.
     */
    draw : function( operations ) {
      for( var i = 0; i < operations.length; i++ ) {
        try {
          var op = operations[ i ][ 0 ];
          switch( op ) {
            case "fillStyle":
            case "strokeStyle":
            case "globalAlpha":
            case "lineWidth":
            case "lineCap":
            case "lineJoin":
            case "font":
              this._setProperty( operations[ i ] );
            break;
            case "createLinearGradient":
            case "addColorStop":
            case "fillText":
            case "strokeText":
            case "ellipse":
            case "drawImage":
            case "setTransform":
              this[ "_" + op ]( operations[ i ] );
            break;
            default:
              this._context[ op ].apply( this._context, operations[ i ].slice( 1 ) );
            break;
          }
        } catch( ex ) {
          var opArrStr = "[ " + operations[ i ].join( ", " ) + " ]";
          throw new Error( "Drawing operation failed: " + opArrStr + " :" + ex.message );
        }
      }
    },

    getNativeContext : function() {
      return this._context;
    },

    ////////////
    // Internals

    _createCanvas : function() {
      this._canvas = document.createElement( "canvas" );
      this._context = this._canvas.getContext( "2d" );
    },

    _onControlCreate : function() {
      this._addCanvasToDOM();
    },

    _addCanvasToDOM  : function() {
      var controlElement = this._control._getTargetNode();
      var firstChild = controlElement.firstChild;
      if( firstChild ) {
        controlElement.insertBefore( this._canvas, firstChild );
      } else {
        controlElement.appendChild( this._canvas );
      }
    },

    _removeCanvasFromDOM : function() {
      this._canvas.parentNode.removeChild( this._canvas );
    },

    _onControlChangeWidth : function( event ) {
      var width = event.getValue();
      this._canvas.width = width;
      this._canvas.style.width = width + "px";
    },

    _onControlChangeHeight : function( event ) {
      var height = event.getValue();
      this._canvas.height = height;
      this._canvas.style.height = height + "px";
    },

    _initClipping : function( x, y, width, height ) {
      this._context.clearRect( x, y, width, height );
      this._context.beginPath();
      this._context.rect( x, y, width, height );
      this._context.clip();
    },

    _initFields : function( font, background, foreground ) {
      this._context.strokeStyle = rwt.util.Colors.rgbToRgbString( foreground );
      this._context.fillStyle = rwt.util.Colors.rgbToRgbString( background );
      this._context.globalAlpha = 1.0;
      this._context.lineWidth = 1;
      this._context.lineCap = "butt";
      this._context.lineJoin = "miter";
      this._context.font = this._toCssFont( font );
      this._context.textBaseline = "top";
      this._context.textAlign = "left";
    },

    // See http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#building-paths
    _ellipse : function( operation ) {
      var cx = operation[ 1 ];
      var cy = operation[ 2 ];
      var rx = operation[ 3 ];
      var ry = operation[ 4 ];
      //var rotation = operation[ 5 ]; // not supported
      var startAngle = operation[ 6 ];
      var endAngle = operation[ 7 ];
      var dir = operation[ 8 ];
      if( rx > 0 && ry > 0 ) {
        this._context.translate( cx, cy );
        // TODO [tb] : using scale here changes the stroke-width also, looks wrong
        this._context.scale( 1, ry / rx );
        this._context.arc( 0, 0, rx, startAngle, endAngle, dir );
      }
    },

    _setProperty : function( operation ) {
      var property = operation[ 0 ];
      var value = operation[ 1 ];
      if( value === "linearGradient" ) {
        value = this._linearGradient;
      } else if( property === "fillStyle" || property === "strokeStyle" ) {
        value = rwt.util.Colors.rgbToRgbString( value );
      } else if( property === "font" ) {
        value = this._toCssFont( value );
      }
      this._context[ property ] = value;
    },

    _strokeText : function( operation ) {
      var x = operation[ 5 ];
      var y = operation[ 6 ];
      var text = this._prepareText.apply( this, operation.slice( 1, 5 ) );
      var lines = text.split( "\n" );
      if( lines.length > 1 ) {
        var textBounds = this._getTextBounds.apply( this, operation.slice( 1, 7 ) );
        this._drawText( lines, textBounds, false );
      } else {
        this._context.save();
        this._context.fillStyle = this._context.strokeStyle;
        this._context.fillText( text, x, y );
        this._context.restore();
      }
    },

    _fillText : function( operation ) {
      var text = this._prepareText.apply( this, operation.slice( 1, 5 ) );
      var lines = text.split( "\n" );
      var textBounds = this._getTextBounds.apply( this, operation.slice( 1, 7 ) );
      this._drawText( lines, textBounds, true );
    },

    _drawText : function( textLines, bounds, fill ) {
      this._context.save();
      if( fill ) {
        this._context.fillRect.apply( this._context, bounds );
      }
      this._context.fillStyle = this._context.strokeStyle;
      var lineHeight = bounds[ 3 ] / textLines.length;
      for( var i = 0; i < textLines.length; i++ ) {
        this._context.fillText( textLines[ i ], bounds[ 0 ], i * lineHeight + bounds[ 1 ] );
      }
      this._context.restore();
    },

    _drawImage : function( operation ) {
      var args = operation.slice( 1 );
      var image = new Image();
      image.src = args[ 0 ];
      args[ 0 ] = image;
      // On (native) canvas, only loaded images can be drawn:
      if( image.complete ) {
        this._context.drawImage.apply( this._context, args );
      } else {
        var alpha = this._context.globalAlpha;
        var context = this._context;
        image.onload = function() {
          // TODO [tb] : The z-order will be wrong in this case.
          context.save();
          context.globalAlpha = alpha;
          context.drawImage.apply( context, args );
          context.restore();
        };
      }
    },

    _setTransform : function( operation ) {
      this._context.setTransform.apply( this._context, operation.slice( 1 ) );
    },

    _createLinearGradient : function( operation ) {
      var func = this._context.createLinearGradient;
      this._linearGradient = func.apply( this._context, operation.slice( 1 ) );
    },

    _addColorStop : function( operation ) {
      this._linearGradient.addColorStop(
        operation[ 1 ],
        rwt.util.Colors.rgbToRgbString( operation[ 2 ] )
      );
    },

    _prepareText : function( value, drawMnemonic, drawDelemiter, drawTab ) {
      var EncodingUtil = rwt.util.Encoding;
      var text = drawMnemonic ? EncodingUtil.removeAmpersandControlCharacters( value ) : value;
      var replacement = drawDelemiter ? "\n" : "";
      text = EncodingUtil.replaceNewLines( text, replacement );
      replacement = drawTab ? "    " : "";
      text = text.replace( /\t/g, replacement );
      return text;
    },

    _getTextBounds : function( text, drawMnemonic, drawDelemiter, drawTab, x, y ) {
      var escapedText = this._escapeText( text, drawMnemonic, drawDelemiter, drawTab );
      var fontProps = {};
      rwt.html.Font.fromString( this._context.font ).renderStyle( fontProps );
      var calc = rwt.widgets.util.FontSizeCalculation;
      var dimension = calc.computeTextDimensions( escapedText, fontProps );
      return [ x, y, dimension[ 0 ], dimension[ 1 ] ];
    },

    _escapeText : function( value, drawMnemonic, drawDelemiter, drawTab ) {
      var EncodingUtil = rwt.util.Encoding;
      var text = EncodingUtil.escapeText( value, drawMnemonic );
      var replacement = drawDelemiter ? "<br/>" : "";
      text = EncodingUtil.replaceNewLines( text, replacement );
      replacement = drawTab ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "";
      text = text.replace( /\t/g, replacement );
      return text;
    },

    _toCssFont : function( fontArray ) {
      if( fontArray === null ) {
        return "";
      }
      return rwt.html.Font.fromArray( fontArray ).toCss();
    }

  }
} );
