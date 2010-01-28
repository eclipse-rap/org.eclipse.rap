/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.widgets.ProgressBar", {
  extend : qx.ui.layout.CanvasLayout,
  
  construct : function() {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this.setAppearance( "progressbar" );        
    // this._gfxCanvasAppended must be present for 
    // org.eclipse.rwt.GraphicsMixin#prepareEnhancedBorder
    this._timer = null;
    this._gfxCanvasAppended = false;
    this._canvas = null;
    this._backgroundShape = null;
    this._indicatorShape = null;    
    this._borderShape = null;
    this._useBorderShape = false;
    this._gfxBorderWidth = 0;
    this._indicatorVirtualPosition = 0;
    this._separatorStartShape = null;
    this._separatorEndShape = null;
    this._useSeparator = false;
    this._separatorWidth = 0;
    this._minimum = 0;
    this._maximum = 100;
    this._selection = 0;
    this._flag = 0;
  },
  
  destruct : function() {
    if( this._timer != null ) {
      this._timer.stop();
      this._timer.dispose();
    }
    this._timer = null;
    this._canvas = null;
    this._backgroundShape = null;
    this._indicatorShape = null;    
    this._borderShape = null;
    this._separatorStartShape = null;
    this._separatorEndShape = null;
  },

  statics : {
    UNDETERMINED_SIZE : 40,
    FLAG_UNDETERMINED : 2,
    FLAG_HORIZONTAL : 256,
    FLAG_VERTICAL : 512
  },

  properties : {

    indicatorColor : {
      nullable : true,
      init : null,
      apply : "_applyIndicatorFill",
      themeable : true
    },
    
    // TODO [tb] : wrong offset in IE when vertical (or undetermined)
    indicatorImage : {
      nullable : true,
      init : null,
      apply : "_applyIndicatorFill",
      themeable : true
    },
    
    indicatorGradient : {
      nullable : true,
      init : null,
      apply : "_applyIndicatorFill",
      themeable : true
    },
    
    backgroundImageSized : {
      nullable : true,
      init : null,
      apply : "_applyBackgroundImageSized",
      themeable : true
    },
    
    separatorBorder : {
      nullable : true,
      init : null,
      apply : "_applySeparatorBorder",
      themeable : true
    }

  },

  members : {

    //////
    // API

    setMinimum : function( minimum ) {
      this._minimum = minimum;
    },
    
    setMaximum : function( maximum ) {
      this._maximum = maximum;
    },
    
    setSelection : function( selection ) {
      this._selection = selection;
      this.addToQueue( "indicatorSelection" );
    },

    setFlag : function( flag ) {
      this._flag = flag;
      if( this._isUndetermined() ) {
        this._timer = new qx.client.Timer( 120 );
        this._timer.addEventListener( "interval", this._onInterval, this );
        this._timer.start();
        this.addState( "rwt_UNDETERMINED" );
      }
      if( this._isVertical() ) {
        this.addState( "rwt_VERTICAL" );        
      }
    },
    
   setState : function( state ) {
      if( state == "error" ) {
        this.removeState( "paused" );
        this.addState( "error" );
      } else if( state == "paused" ) {
        this.removeState( "error" );
        this.addState( "paused" );
      } else {
        this.removeState( "error" );
        this.removeState( "paused" );
      }
    },

    //////////////
    // state-info

    _isUndetermined : function() {
      var masked = 
        this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED;
      return masked != 0;
    },

    _isHorizontal : function() {
      var masked
        = this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_HORIZONTAL;
      return masked != 0;
    },

    _isVertical : function() {
      var masked
        = this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL;
      return masked != 0;
    },

    ////////////////
    // apply-methods
    
    // OVERWRITTEN, called indirectly by _applyBorder in qx.ui.core.Widget
    _queueBorder : function( value, edge ) {
      this.addToQueue( "indicatorBorder" );
      if( value instanceof org.eclipse.rwt.RoundedBorder ) {
        // rounded borders are to be ignored by the qooxdoo-layouting:
        this._cachedBorderTop = 0;
        this._cachedBorderRight = 0;
        this._cachedBorderBottom = 0;
        this._cachedBorderLeft = 0;
        this._invalidateFrameDimensions();
      } else {
        this.base( arguments, value, edge );
      }
    },

    // Overwritten from Widget
    _applyBackgroundColor : function( value ) {
      if( this._gfxCanvasAppended ) {
        this._styleBackgroundFill();
      }
    },

    // OVERWRITTEN FROM org.eclipse.rwt.GraphicsMixin
    _applyBackgroundGradient : function( value ) {
      if( this._gfxCanvasAppended ) {
        this._styleBackgroundFill();
      }
    },

    _applyBackgroundImage : function( value ) {
      // nothing to do, uses _applyBackgroundImageSized instead
    },

    _applyBackgroundImageSized : function( value ) {
      if( this._gfxCanvasAppended ) {
        this._styleBackgroundFill();
      }
    },

    _applyIndicatorFill : function( value ) {
      if( this._gfxCanvasAppended ) {
        this._styleIndicatorFill();
      }
    },

    _applySeparatorBorder : function( value ) {
      this.addToQueue( "separatorBorder" );
    },

    ///////////////
    // eventhandler

    _onCanvasAppear : function() {
      org.eclipse.rwt.GraphicsUtil.handleAppear( this._canvas );
    },

    _onInterval : function() {
      if( this.isSeeable() ) {
        this._renderIndicatorSelection();
      }      
    },

    ///////
    // core

    _layoutPost : function( changes ) {
      if( !this._gfxCanvasAppended ) {
        this._createCanvas();
      }
      var dimensionChanged =    changes.width
                             || changes.height
                             || changes.frameWidth
                             || changes.frameHeight
                             || changes.initial;
      if( changes.separatorBorder ) {
        this._styleSeparatorBorder();
      }
      if( changes.indicatorBorder ) {
        this._styleIndicatorBorder();
      }
      if( changes.indicatorBorder || dimensionChanged ) {
        this._renderDimension();
        this._renderIndicatorSelection();
      } else if( changes.indicatorSelection || changes.separatorBorder ) {
        this._renderIndicatorSelection();        
      }
    },

    _createCanvas : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      this._canvas = gfxUtil.createCanvas();
      gfxUtil.setLayoutMode( this._canvas, "absolute" );
      this._getTargetNode().appendChild( gfxUtil.getCanvasNode( this._canvas ) );
      this._gfxCanvasAppended = true;
      this.addEventListener( "appear", this._onCanvasAppear );
      this._backgroundShape = gfxUtil.createShape( "roundrect" );
      this._indicatorShape = gfxUtil.createShape( "roundrect" );
      gfxUtil.addToCanvas( this._canvas, this._backgroundShape );
      gfxUtil.addToCanvas( this._canvas, this._indicatorShape );
      this._styleBackgroundFill();
      this._styleIndicatorFill();
      if( this.isSeeable() ) {
        this._onCanvasAppear();
      }
    },

    ///////////////
    // render style

    _styleIndicatorBorder : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      if( this.getBorder() instanceof org.eclipse.rwt.RoundedBorder ) {
        if( !this._useBorderShape ) {
          this._style.border = "";
          if( this._borderShape == null ) {
            this._borderShape = gfxUtil.createShape( "roundrect" );
          }
          gfxUtil.addToCanvas( this._canvas, this._borderShape );
          this._useBorderShape = true;
        }
        this._gfxBorderWidth = this._getMaxBorderWidth( this.getBorder() );
        var color = this.getBorder().getColor();
        // NOTE : Different widths for different edges are not supported
        gfxUtil.setStroke( this._borderShape, color, this._gfxBorderWidth );
      } else {
        if( this._useBorderShape ) {
          gfxUtil.removeFromCanvas( this._canvas, this._borderShape );
          this._useBorderShape = true;
          this._gfxBorderWidth = 0;
        }        
      }
    },

    _styleSeparatorBorder : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var border = this.getSeparatorBorder();
      if( border != null ) {
        if( !this._useSeparator ) {
          if( this._isUndetermined() ) {
            if( this._separatorStartShape == null ) {
              this._separatorStartShape = gfxUtil.createShape( "rect" );
            }
            gfxUtil.addToCanvas( this._canvas, this._separatorStartShape );            
          }
          if( this._separatorEndShape == null ) {
            this._separatorEndShape = gfxUtil.createShape( "rect" );
          }
          gfxUtil.addToCanvas( this._canvas, this._separatorEndShape );
          this._useSeparator = true;
        }
        this._separatorWidth = this._getMaxBorderWidth( border );        
        // use one color for all edges:
        var color = border.getColorTop();
        gfxUtil.setFillColor( this._separatorEndShape, color );
        if( this._isUndetermined() ) {
          gfxUtil.setFillColor( this._separatorStartShape, color );
        }
      } else if( this._useSeparator ) {
        gfxUtil.removeFromCanvas( this._canvs, this._separatorEndShape );
        this._useSeparator = false;
        if( this._isUndetermined() ) {
          gfxUtil.removeFromCanvas( canvas, this._separatorStartShape );
        }
        this._separatorWidth = 0;   
      }
    },

    // indicator and separator do not support different border-widths 
    _getMaxBorderWidth : function( border ) {
      var maxWidth = 0;
      maxWidth = Math.max( maxWidth, border.getWidthTop() ); 
      maxWidth = Math.max( maxWidth, border.getWidthLeft() ); 
      maxWidth = Math.max( maxWidth, border.getWidthRight() ); 
      maxWidth = Math.max( maxWidth, border.getWidthBottom() ); 
      return maxWidth;
    },
    
    _styleIndicatorFill : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      if(    this.getIndicatorImage() != null 
          && this.getIndicatorImage()[ 0 ] != null )
      {
        var image = this.getIndicatorImage();
        gfxUtil.setFillPattern( this._indicatorShape, 
                                image[ 0 ], 
                                image[ 1 ], 
                                image[ 2 ] );
      } else if( this.getIndicatorGradient() != null ) {
        gfxUtil.setFillGradient( this._indicatorShape, 
                                 this.getIndicatorGradient() );
      } else {
        gfxUtil.setFillColor( this._indicatorShape, this.getIndicatorColor() );
      }
    },

    _styleBackgroundFill : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      if(    this.getBackgroundImageSized() != null 
          && this.getBackgroundImageSized()[ 0 ] != null )
      {
        var image = this.getBackgroundImageSized();
        gfxUtil.setFillPattern( this._backgroundShape, 
                                image[ 0 ], 
                                image[ 1 ], 
                                image[ 2 ] );
      } else if( this.getBackgroundGradient() != null ) {
        gfxUtil.setFillGradient( this._backgroundShape, 
                                 this.getBackgroundGradient() );
      } else {
        gfxUtil.setFillColor( this._backgroundShape, 
                              this.getBackgroundColor() );
      }
    },
    
    ////////////////
    // render layout

    _renderDimension : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;      
      var radii = [ 0, 0, 0, 0 ];
      var width = this.getInnerWidth();
      var height = this.getInnerHeight();      
      if( this._useBorderShape ) {
        radii = this.getBorder().getRadii();        
        gfxUtil.setRoundRectLayout( this._borderShape, 
                                    this._gfxBorderWidth / 2, 
                                    this._gfxBorderWidth / 2,
                                    width - this._gfxBorderWidth, 
                                    height - this._gfxBorderWidth, 
                                    radii );    
      }
      gfxUtil.setRoundRectLayout( this._backgroundShape, 
                                  this._gfxBorderWidth / 2, 
                                  this._gfxBorderWidth / 2,
                                  width - this._gfxBorderWidth, 
                                  height - this._gfxBorderWidth, 
                                  radii );    
    },

    _renderIndicatorSelection : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var virtualPosition = this._getIndicatorVirtualPosition();
      var position = Math.max( virtualPosition, 0 );
      var length = this._getIndicatorLength( virtualPosition );
      if( length > 0 ) {
        var radii = this._getIndicatorRadii( position, length );
        // adjust position and length to hide edges under the border
        var displayPosition = position;
        var displayLength = length;
        if( position + length == this._getIndicatorFullLength() ) {          
          displayLength += this._gfxBorderWidth / 2;          
        } else {
          // this is done to reduce flickering in IE:
          displayLength += this._separatorWidth;
        }
        if( displayPosition == 0 ) {
          displayPosition += this._gfxBorderWidth / 2;
          displayLength += this._gfxBorderWidth / 2;
        } else {
          displayPosition += this._gfxBorderWidth;
        }
        // compute bounds
        var vertical = this._isVertical();
        var width =   vertical 
                    ? this.getInnerWidth() - this._gfxBorderWidth 
                    : displayLength;
        var height =   vertical 
                     ? displayLength 
                     : this.getInnerHeight() - this._gfxBorderWidth;
        var top =   vertical 
                  ? this.getInnerHeight() - ( displayPosition + displayLength ) 
                  : this._gfxBorderWidth / 2;
        var left = vertical ? this._gfxBorderWidth / 2 : displayPosition;
        var shape = this._indicatorShape;
        gfxUtil.setDisplay( this._indicatorShape, true );        
        gfxUtil.setRoundRectLayout( shape, left, top, width, height, radii );    
      } else {
        gfxUtil.setDisplay( this._indicatorShape, false );
      }
      if( this._useSeparator ) {
        this._renderSeparator( position, length )
      }
    },

    _renderSeparator : function( position, length ) {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;              
      var full = length + position == this._getIndicatorFullLength();
      if( length == 0 ) {
        gfxUtil.setDisplay( this._separatorEndShape, false );
        if( this._isUndetermined() ) {
          gfxUtil.setDisplay( this._separatorStartShape, false );
        }
      } else {
        gfxUtil.setDisplay( this._separatorEndShape, !full );
        if( this._isUndetermined() ) {
          gfxUtil.setDisplay( this._separatorStartShape, position != 0 );
        }
        var displayPosition =   position 
                              + this._gfxBorderWidth 
                              - this._separatorWidth; 
        var displayLength = length + 2 * this._separatorWidth;
        if( this._isVertical() ) {
          var left = this._gfxBorderWidth;
          var top = this.getInnerHeight() - ( displayLength + displayPosition );
          var width = this.getInnerWidth() - 2 * this._gfxBorderWidth;
          var height = this._separatorWidth;
          var shape = this._separatorEndShape;
          if( !full ) {
            gfxUtil.setRectBounds( shape, left, top, width, height );
          }
          if( position != 0 ) {
            top = this.getInnerHeight() - displayPosition - this._separatorWidth;
            shape = this._separatorStartShape;
            gfxUtil.setRectBounds( shape, left, top, width, height );
          } 
        } else {
          var left = displayPosition + displayLength - this._separatorWidth;
          var top = this._gfxBorderWidth;
          var width = this._separatorWidth;
          var height = this.getInnerHeight() - 2 * this._gfxBorderWidth;
          var shape = this._separatorEndShape;
          if( !full ) { 
            gfxUtil.setRectBounds( shape, left, top, width, height );
          }
          if( position != 0 ) {
            left = displayPosition;
            shape = this._separatorStartShape;
            gfxUtil.setRectBounds( shape, left, top, width, height );
          } 
        }
      }
    },

    ////////////////
    // layout helper

    _getIndicatorLength : function( virtualPosition ) {
      var result = this._getIndicatorVirtualLength();
      var fullLength = this._getIndicatorFullLength();
      if( this._isUndetermined() ) {
        // shorten the length to fit in the bar 
        if( virtualPosition < 0 ) {
          result += virtualPosition;
        } 
        if( ( virtualPosition + result ) > fullLength ) {
          result = fullLength - virtualPosition;
        }
      } else if( this._useBorderShape ) {
        // round length so it falls into a save area, position is assumed 0 
        var minLength = this._getIndicatorMinSafeLength();
        var maxLength = this._getIndicatorMaxSafeLength();
        if( result < minLength ) {
          if( result > 0 ) {
            result = minLength;          
          } else {            
            result = 0;
          }         
        }
        if( result > maxLength && result < fullLength ) {
          result = maxLength;
        }
      }
      return Math.round( result );
    },

    _getIndicatorVirtualLength : function() {
      var result;
      if( this._isUndetermined() ) {
        result = org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE;
      } else {
        var fullLength = this._getIndicatorFullLength();
        var selected = this._selection - this._minimum;
        var max = this._maximum - this._minimum;
        result = ( selected / max ) * fullLength;
      }
      return result;
    },
        
    _getIndicatorVirtualPosition : function() {
      var result = 0;
      if( this._isUndetermined() ) {
        result = this._computeNextSaveIndicatorPosition();
      }
      return result;
    },

    _computeNextSaveIndicatorPosition : function() {
      var length = org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE;      
      var fullLength = this._getIndicatorFullLength();
      var position = this._indicatorVirtualPosition + 2;
      if( this._useBorderShape ) { 
        var minWidth = this._getIndicatorMinSafeLength();
        var maxWidth = this._getIndicatorMaxSafeLength();      
        var endPosition = position + length;
        if( endPosition > 0 && endPosition < minWidth ) {
          position = minWidth - length;
        }      
        if( position > 0 && position < minWidth ) {
          position = minWidth;
        }
        endPosition = position + length;
        if( endPosition > maxWidth && endPosition < fullLength ) {
          position = fullLength - length;
        }      
        if( position > maxWidth ) {
          position = -length;
        }
      } else if( position >= fullLength ) {
          position = -length;
      }
      this._indicatorVirtualPosition = position;
      return position;      
    },
    
    _getIndicatorRadii : function( position, length ) {
      // works under the assumption that positon and length are "radii-save"
      var result = [ 0, 0, 0, 0 ];
      if( this._useBorderShape && length > 0 ) {
        var radii = this.getBorder().getRadii();
        var endPosition = position + length;
        var fullLength = this._getIndicatorFullLength();
        if( this._isVertical() ) {
          if( position == 0 ) {
            result[ 2 ] = radii[ 2 ];
            result[ 3 ] = radii[ 3 ];
          }
          if( endPosition == fullLength ) {
            result[ 0 ] = radii[ 0 ];
            result[ 1 ] = radii[ 1 ];
          }
        } else {
          if( position == 0 ) {
            result[ 0 ] = radii[ 0 ];
            result[ 3 ] = radii[ 3 ];
          }
          if( endPosition == fullLength ) {
            result[ 1 ] = radii[ 1 ];
            result[ 2 ] = radii[ 2 ];          
          }
        }
      }
      return result;
    },

    _getIndicatorFullLength : function() {
      return   this._isVertical() 
             ? this.getInnerHeight() - 2 * this._gfxBorderWidth
             : this.getInnerWidth() - 2 * this._gfxBorderWidth;
    },
    
    // minimal indicator-length for the left/lower rounded corners to work
    _getIndicatorMinSafeLength : function() {
      var radii = this.getBorder().getRadii();
      var result =   this._isVertical()
                   ? Math.max( radii[ 2 ], radii[ 3 ] )
                   : Math.max( radii[ 0 ], radii[ 3 ] );
      result += this._separatorWidth;
      result -= Math.floor( this._gfxBorderWidth / 2 );
      return result;
    },
    
    // maximum indicator-length for the right/upper corners to be rectangular
    _getIndicatorMaxSafeLength : function() {
      var radii = this.getBorder().getRadii();
      var fullLength = this._getIndicatorFullLength();
      var result =   this._isVertical() 
                   ? fullLength - Math.max( radii[ 0 ], radii[ 1 ] )
                   : fullLength - Math.max( radii[ 1 ], radii[ 2 ] );
      result -= this._separatorWidth;
      result += Math.floor( this._gfxBorderWidth / 2 );
      return result;
    }

  }
});
