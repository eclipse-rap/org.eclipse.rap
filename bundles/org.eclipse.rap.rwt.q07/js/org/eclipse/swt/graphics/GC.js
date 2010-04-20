/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for
 * org.eclipse.swt.graphics.GC
 */
qx.Class.define( "org.eclipse.swt.graphics.GC", {
  extend : qx.core.Object,

  construct : function( control ) {
    this.base( arguments );
    this._control = control;
    this._control.addEventListener( "create", this._onControlCreate, this );
    if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
      var canvas = org.eclipse.rwt.VML.createCanvas();
      this._canvas = org.eclipse.rwt.VML.getCanvasNode( canvas );
      this._context = new org.eclipse.rwt.VMLCanvas( canvas );
    } else {
      this._canvas = document.createElement( "canvas" );
      this._context = this._canvas.getContext( "2d" );
    }
    this._textCanvas = document.createElement( "div" );
    this._textCanvas.style.position = 'absolute';
    this._textCanvas.style.overflow = 'hidden';
    this._textCanvas.style.left = '0px';
    this._textCanvas.style.top = '0px';
    if( this._control.isCreated() ) {
      this._addCanvasToDOM();
    }
  },

  destruct : function() {
    this._control.removeEventListener( "create", this._onControlCreate, this );
    this._control = null;
    this._canvas = null;
    if( this._context.dispose ) {
      this._context.dispose();
    }
    this._context = null;
    this._textCanvas = null;
  },

  members : {

    _onControlCreate : function() {
      this._addCanvasToDOM();
    },

    _addCanvasToDOM  : function() {
      var controlElement = this._control._getTargetNode();
      var firstChild = controlElement.firstChild;
      if( firstChild ) {
        controlElement.insertBefore( this._canvas, firstChild );
        controlElement.insertBefore( this._textCanvas, firstChild );
      } else {
        controlElement.appendChild( this._canvas );
        controlElement.appendChild( this._textCanvas );
      }
    },

    init : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( width, height, font, background, foreground ) {
        // TODO [tb]: Should the control be detached from the DOM 
        // (e.g. by Widget.prepareEnhancedBorder), this might lead to glitches 
        // in IE/VML. The flush prevents this in some cases:
        qx.ui.core.Widget.flushGlobalQueues();
        this._initTextCanvas( width, height );
        this._context.clearRect( 0, 0, width, height );
        this._initFields( font, background, foreground );
      },
      "default" : function( width, height, font, background, foreground  ) {
        this._initTextCanvas( width, height );
        this._canvas.width = width;
        this._canvas.style.width = width + 'px';
        this._canvas.height = height;
        this._canvas.style.height = height + 'px';
        this._context.clearRect( 0, 0, width, height );
        this._initFields( font, background, foreground );
      }
    } ),

    _initTextCanvas : function( width, height ) {
      this._textCanvas.width = width;
      this._textCanvas.style.width = width + 'px';
      this._textCanvas.height = height;
      this._textCanvas.style.height = height + 'px';
      this._textCanvas.innerHTML = "";
    },

    _initFields : function( font, background, foreground ) {
      this._context.strokeStyle = foreground;
      this._context.fillStyle = background;
      this._context.globalAlpha = 1.0;
      this._context.lineWidth = 1;
      this._context.lineCap = 'butt';
      this._context.lineJoin = 'miter';
      this._context.font = font;
    },

    setProperty : function( name, value ) {
      switch( name ) {
        case "foreground":
          this._context.strokeStyle = value;
        break;
        case "background":
          this._context.fillStyle = value;
        break;
        case "alpha":
          this._context.globalAlpha = value / 255;
        break;
        case "lineWidth":
          this._context.lineWidth = value > 1 ? value : 1;
        break;
        case "lineCap":
          switch( value ) {
            case 1:
              this._context.lineCap = 'butt';
            break;
            case 2:
              this._context.lineCap = 'round';
            break;
            case 3:
              this._context.lineCap = 'square';
            break;
          }
        break;
        case "lineJoin":
          switch( value ) {
            case 1:
              this._context.lineJoin = 'miter';
            break;
            case 2:
              this._context.lineJoin = 'round';
            break;
            case 3:
              this._context.lineJoin = 'bevel';
            break;
          }
        break;
        case "font":
          this._context.font = value;
        break;
      }
    },

    drawLine : function( x1, y1, x2, y2 ) {
      this._context.beginPath();
      this._context.moveTo( x1, y1 );
      this._context.lineTo( x2, y2 );
      this._stroke( false );
    },

    drawPoint : function( x, y ) {
      this._context.save();
      this._context.beginPath();
      this._context.lineWidth = 1;
      this._context.rect( x, y, 1, 1 );
      this._stroke( false );
      this._context.restore();
    },

    drawRectangle : function( x, y, width, height, fill ) {
      this._context.beginPath();
      this._context.rect( x, y, width, height );
      this._stroke( fill );
    },

    drawRoundRectangle : function( x,
                                   y,
                                   width,
                                   height,
                                   arcWidth,
                                   arcHeight,
                                   fill )
    {
      this._context.beginPath();
      this._context.moveTo( x, y + arcHeight );
      this._context.lineTo( x, y + height - arcHeight );
      this._context.quadraticCurveTo( x, y + height, x + arcWidth, y + height );
      this._context.lineTo( x + width - arcWidth, y + height );
      this._context.quadraticCurveTo( x + width,
                                      y + height,
                                      x + width,
                                      y + height - arcHeight );
      this._context.lineTo( x + width, y + arcHeight );
      this._context.quadraticCurveTo( x + width, y, x + width - arcWidth, y );
      this._context.lineTo( x + arcWidth, y );
      this._context.quadraticCurveTo( x, y, x, y + arcHeight );
      this._stroke( fill );
    },

    fillGradientRectangle : function( x, y, width, height, vertical ) {
      var x1 = x;
      var y1 = y;
      var swapColors = false;
      if( width < 0 ) {
        x1 += width;
        if( !vertical ) {
          swapColors = true;
        }
      }
      if( height < 0 ) {
        y1 += height;
        if( vertical ) {
          swapColors = true;
        }
      }
      var x2 = vertical ? x1 : x1 + Math.abs( width );
      var y2 = vertical ? y1 + Math.abs( height ) : y1;
      var startColor = swapColors
                     ? this._context.fillStyle
                     : this._context.strokeStyle;
      var endColor = swapColors
                   ? this._context.strokeStyle
                   : this._context.fillStyle;
      var gradient = this._context.createLinearGradient( x1, y1, x2, y2 );
      gradient.addColorStop( 0, startColor );
      gradient.addColorStop( 1, endColor );
      this._context.save();
      this._context.fillStyle = gradient;
      this.drawRectangle( x, y, width, height, true );
      this._context.restore();
    },

    drawArc : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( x, y, width, height, startAngle, arcAngle, fill ) {
        var radiusX = width / 2;
        var radiusY = height / 2;
        this._context.save();
        this._context.beginPath();
        this._context.arc( x + radiusX,
                           y + radiusY,
                           radiusX,
                           radiusY,
                           - startAngle * Math.PI / 180,
                           - ( startAngle + arcAngle ) * Math.PI / 180,
                           true );
        this._stroke( fill );
        this._context.restore();
      }, 
      "default" : function( x, y, width, height, startAngle, arcAngle, fill ) {
        if( width > 0 && height > 0 ) {
          var halfWidth = width / 2;
          var halfHeight = height / 2;
          this._context.save();
          this._context.beginPath();
          this._context.translate( x + halfWidth, y + halfHeight );
          this._context.scale( 1, height / width );
          this._context.arc( 0,
                             0,
                             halfWidth,
                             - startAngle * Math.PI / 180,
                             - ( startAngle + arcAngle ) * Math.PI / 180,
                             true );
          this._stroke( fill );
          this._context.restore();
        }
      }
    } ),

    drawPolyline : function( points, close, fill ) {
      this._context.beginPath();
      for( var i = 1; i < points.length; i += 2 ) {
        if( i == 1 ) {
          this._context.moveTo( points[ i - 1 ], points[ i ] );
        } else {
          this._context.lineTo( points[ i - 1 ], points[ i ] );
        }
      }
      if( points.length > 1 && close ) {
        this._context.lineTo( points[ 0 ], points[ 1 ] );
      }
      this._stroke( fill && close );
    },

    drawText : function( text, x, y, fill ) {
      var textElement = document.createElement( "div" );
      var style = textElement.style;
      style.position = 'absolute';
      style.left = x + 'px';
      style.top = y + 'px';
      style.color = this._context.strokeStyle;
      if( fill ) {
        style.backgroundColor = this._context.fillStyle;
      }
      if( this._context.font != "" ) {
        style.font = this._context.font;
      }
      textElement.innerHTML = text;
      this._textCanvas.appendChild( textElement );
    },

    drawImage : function( imageSrc,
                          srcX,
                          srcY,
                          srcWidth,
                          srcHeight,
                          destX,
                          destY,
                          destWidth,
                          destHeight,
                          simple )
    {
      var context = this._context;
      var image = new Image();
      image.src = imageSrc;
      // On (native) canvas, only loaded images can be drawn: 
      if( image.complete || qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        if( simple ) {
          context.drawImage( image, destX, destY );
        } else {
          context.drawImage( image,
                             srcX,
                             srcY,
                             srcWidth,
                             srcHeight,
                             destX,
                             destY,
                             destWidth,
                             destHeight );
        }
      } else {
	      var alpha = context.globalAlpha;
        image.onload = function() {
          // TODO [tb] : The z-order will be wrong in this case.
          // [if] As drawImage is delayed by the onload event, we have to draw
          // it with correct context parameters (alpha). 
          context.save();
          context.globalAlpha = alpha;
          if( simple ) {
            context.drawImage( image, destX, destY );
          } else {
            context.drawImage( image,
                               srcX,
                               srcY,
                               srcWidth,
                               srcHeight,
                               destX,
                               destY,
                               destWidth,
                               destHeight );
          }
          context.restore();
        };
      }
    },

    _stroke : function( fill ) {
      if( fill ) {
        this._context.fill();
      } else {
        this._context.stroke();
      }
    }

  }
} );