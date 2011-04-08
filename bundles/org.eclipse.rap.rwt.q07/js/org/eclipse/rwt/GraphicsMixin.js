/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Mixin.define( "org.eclipse.rwt.GraphicsMixin", {

  properties : {

    backgroundGradient : {
      check : "Array",
      nullable : true,
      init : null,
      apply : "_applyBackgroundGradient",
      event : "changeBackgroundGradient",      
      themeable : true
    },

    /**
     * Syntax for shadow:
     * [
     *    inset, //boolean, currently not supported
     *    offsetX, // positive or negative number
     *    offsetY, // positive or negative number
     *    blurRadius, // positive number or zero
     *    spread, // positive or negative number
     *    color, // string
     *    opacity, // number between 0 and 1
     * ]
     */
    shadow : {
      check : "Array",
      nullable : true,
      init : null,
      apply : "_applyShadow",
      event : "changeShadow",      
      themeable : true
    }

  },
  
  statics : {
    
    getSupportsShadows : function() {
      if( this._shadowSupport === undefined ) {
        var engine = org.eclipse.rwt.Client.getEngine();
        var version = org.eclipse.rwt.Client.getVersion();
        this._shadowSupport = false;
        switch( engine ) {
          case "gecko":
            this._shadowSupport = version >= 1.9;
          break;
          case "mshtml":
            this._shadowSupport = version >= 7;
          break;
          case "opera":
            this._shadowSupport = version >= 9.8;
          break;
          case "webkit":
            var browser = org.eclipse.rwt.Client.getBrowser();
            this._shadowSupport = /* browser === "chrome" && */ version >= 532.9;
          break;
        }
      }
      return this._shadowSupport;
    }
    
  },

  members : {
    // NOTE : "gfx" (short for "graphics") is used in field-names to prevent
    //        potential name-clashes with the classes including this mixin.
    // TODO [tb] : refactor to work entirely with gfxData (with setter/getter)
    _gfxData : null,
    _gfxProperties : null,
    _gfxCanvas : null,
    _gfxEnabled : false,
    _gfxBorderEnabled : false,
    _gfxBackgroundEnabled : false,
    _gfxCanvasAppended : false,

    //////////
    // GFX-API

    _applyBackgroundGradient : function( value, old ) {
      // color-theme values are NOT supported for gradient
      this.setGfxProperty( "gradient", value );
      this._handleGfxBackground();
    },

    _applyShadow : function( value, oldValue ) {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        this.setGfxProperty( "shadow", value );
        this._handleGfxShadow();
      }
    },

    //overwritten
    _styleBackgroundColor : function( value ) {
      if( this._gfxBackgroundEnabled ) {
        this.setGfxProperty( "backgroundColor", value );
        if(    this.getGfxProperty( "fillType" ) == "solid"
            && this._isCanvasReady() ) {
          this._renderGfxBackground();
        }
      } else {
        this.base( arguments, value );
      }
    },

    //overwritten
    _styleBackgroundImage : function( value ) {
      if( this._gfxBackgroundEnabled ) {
        this.setGfxProperty( "backgroundImage", value );
        if(    this.getGfxProperty( "fillType" ) == "image"
            && this._isCanvasReady() 
            && value != null ) 
        {
          this._renderGfxBackground();
        } else {
          this._handleGfxBackground();
        }
      } else {
        if( value == null && this.getGfxProperty( "gradient" ) != null ) {
          this._handleGfxBackground();
        } else {
          this.base( arguments, value );
        }
      }
    },

    //called by RoundedBorder:    
    _styleGfxBorder : function( width, color, radii ) {
      // NOTE: widgets with no dimensions of their own wont work together 
      //       with a gfxBorder (accepted bug)
      this.setGfxProperty( "borderWidths", width );
      var max = 0;
      if( width ) {
        for( var i = 0; i < width.length; i++ ) {
          max = Math.max( max, width[ i ] );
        }
      }
      var renderRadii;
      if( width != null && radii != null && max > 0 ) { 
        renderRadii = [];
        for( var i = 0; i < 4; i++ ) {
          var prev = i > 0 ? i - 1 : 3;
          if( width[ i ] == 0 || width[ prev ] == 0 ) {
            renderRadii[ i ] = 0;
          } else {
            renderRadii[ i ] = radii[ i ];          
          }
        }        
      } else {
        renderRadii = radii;
      }
      this.setGfxProperty( "borderMaxWidth", max );
      this.setGfxProperty( "borderColor", color );
      this.setGfxProperty( "borderRadii", renderRadii );
      this.setGfxProperty( "borderLayouted", null ); // use GfxBorder to chcek
      this._handleGfxBorder();
    },

    setGfxProperty : function( key, value ) {
      if( this._gfxProperties === null ) {
       this._gfxProperties = {};
      }
      this._gfxProperties[ key ] = value;
    },

    getGfxProperty : function( key ) {
      var value =   this._gfxProperties !== null 
                  ? this._gfxProperties[ key ] 
                  : null;
      return typeof value != "undefined" ? value : null;       
    },
    
    //overwritten:
    _computeUsesComplexBorder : function() {
      var result =    this._gfxBorderEnabled 
                   && this.getGfxProperty( "borderMaxWidth" ) > 0 ;
      if( !result ) {
        result = this.base( arguments );
      }
      return result;
    },

    ///////////////////
    // internals - main

    _handleGfxBorder : function() {
      var useBorder =      this.getGfxProperty( "borderRadii" ) != null
                        && this.getGfxProperty( "borderWidths" ) != null
                        && this.getGfxProperty( "borderColor" ) != null;
      var toggle = ( this._gfxBorderEnabled != useBorder );
      if( toggle ) {
        if( useBorder ) {
          this.addEventListener( "changeBorder", 
                                 this._gfxOnBorderChanged, 
                                 this );
          this._gfxBorderEnabled = true;
        } else {
          this.removeStyleProperty( "padding" );
          this.removeEventListener( "changeBorder",
                                    this._gfxOnBorderChanged,
                                    this );
          this._gfxBorderEnabled = false;
        }
        this._handleGfxBackground();
        this._handleGfxStatus();
      }
      // if gfxBorder is not used, canvas can still ready for background
      if( ( toggle || useBorder ) && this._isCanvasReady() ) {
        this._renderGfxBorder();
        if( !useBorder || !this._willBeLayouted() ) {
          // TODO [tb] : refactor conditions, "!useBorder" is only for padding
          this._layoutGfxBorder();
          if( this._gfxShadowEnabled ) {
            this._layoutGfxShadow();
          }
        }
        this._handleFlushListener();
      } else if( toggle && !useBorder && this._innerStyle ) {
        this._handleFlushListener();
        this._setSimulatedPadding();
      }
    },

    _handleGfxBackground : function() {
      var useImage = this.getBackgroundImage() != null;
      var useGradient = false;
      if( useImage ){
        this.setGfxProperty( "fillType", "image" );
      } else {
        useGradient = this.getGfxProperty( "gradient" ) != null;
        if( useGradient ) {
          this.setGfxProperty( "fillType", "gradient" );
        } else {
          this.setGfxProperty( "fillType", "solid" );
        }
      }
      var useBackground = ( useGradient || this._gfxBorderEnabled );
      var toggle = ( this._gfxBackgroundEnabled != useBackground );
      if( toggle ) {
        if( useBackground ) {
          var backgroundColor = this.getStyleProperty( "backgroundColor" );
          this.removeStyleProperty( "backgroundColor" );
          this.setGfxProperty( "backgroundColor", backgroundColor );
          var backgroundImage = this.getBackgroundImage();
          this.setGfxProperty( "backgroundImage", backgroundImage );
          this.removeStyleProperty( "backgroundImage" );
          this._gfxBackgroundEnabled = true;
        } else {
          this._gfxBackgroundEnabled = false;
          this._applyBackgroundColor( this.getBackgroundColor() );
          this.setGfxProperty( "backgroundColor", null );
          this._applyBackgroundImage( this.getBackgroundImage() );
          this.setGfxProperty( "backgroundImage", null );
        }
        this._handleGfxStatus();
      }
      if( ( toggle || useBackground ) && this._isCanvasReady() ) {
        this._renderGfxBackground();
      } else if(    !useBackground 
                 && this._gfxData 
                 && this._gfxData.backgroundInsert ) 
      {
        this._prepareBackgroundShape(); 
      }
    },

    _handleGfxShadow : function() {
      var hasShadow = this.getGfxProperty( "shadow" ) != null;
      this._gfxShadowEnabled = hasShadow;
      this._handleGfxStatus();
      this._handleFlushListener();
      if( this._isCanvasReady() ) {
        this._renderGfxShadow();
      } else if(    !this._gfxShadowEnabled 
                 && this._gfxData 
                 && this._gfxData.shadowInsert ) 
      {
        // remove shape from canvas
        this._prepareShadowShape();
      }
    },

    _handleGfxStatus : function() {
      var useGfx =    this._gfxBorderEnabled 
                   || this._gfxBackgroundEnabled 
                   || this._gfxShadowEnabled;
      if( useGfx != this._gfxEnabled ) {
        if( useGfx ) {
          this.addEventListener( "changeElement",
                                 this._gfxOnElementChanged,
                                 this );
          this._gfxEnabled = true;
        } else {
          this.removeEventListener( "changeElement",
                                 this._gfxOnElementChanged,
                                 this );
          this._gfxEnabled = false;
        }
      }
    },

    /////////////////////
    // internals - canvas

    _isCanvasReady : function() {
      var result = false;
      if( this._isCreated ) {
        if( this._gfxEnabled && this._gfxCanvasAppended ) {
          result = true;
        } else if( this._gfxEnabled && !this._gfxCanvasAppended ) {
          if( this._gfxCanvas == null ) {
            this._createCanvas();
          }
          this._appendCanvas();
          result = true;
        } else if( !this._gfxEnabled && this._gfxCanvasAppended ) {
          this._removeCanvas();
        }
      }
      return result;
    },

    _createCanvas : function() {
      if( !this._innerStyle ) {
        var outline = null;
        if( qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
          //this prevents a graphical glitch in Safari
          outline = this.getStyleProperty( "outline" );
          this.removeStyleProperty( "outline" );
          this.__outerElementStyleProperties.outline = true;
        }
        // make opacity work
        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          this.__outerElementStyleProperties.filter = true;
        } else {
          this.__outerElementStyleProperties.opacity = true;
          if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
            this.__outerElementStyleProperties.MozOpacity = true;
          }
        }
        this.prepareEnhancedBorder();
        if( outline ) {
          this.setStyleProperty( "outline", outline );
        }
        this._applyOpacity( this.getOpacity() );
      }
      this._gfxData = {};      
      this._gfxCanvas = org.eclipse.rwt.GraphicsUtil.createCanvas();
      // TODO [tb] : can be removed?
      this._prepareBackgroundShape();
    },
    
    _appendCanvas : function() {
      var parentNode = this.getElement();
      var gfxNode 
        = org.eclipse.rwt.GraphicsUtil.getCanvasNode( this._gfxCanvas );
      if( gfxNode != null ) {
        parentNode.insertBefore( gfxNode, parentNode.firstChild );
      }
      this._gfxCanvasAppended = true;
      this.addEventListener( "insertDom", this._onCanvasAppear );
      if( this.isSeeable() ) {
        this._onCanvasAppear();
      }
    },

    _removeCanvas : function() {
      var gfxNode 
        = org.eclipse.rwt.GraphicsUtil.getCanvasNode( this._gfxCanvas );
      if( gfxNode != null ) {
        gfxNode.parentNode.removeChild( gfxNode );
        this._gfxCanvasAppended = false;
        this.removeEventListener( "insertDom", this._onCanvasAppear );
      }
    },

    //////////////////////////////
    // internals - backgroundShape

    _prepareBackgroundShape : function() {
      var util = org.eclipse.rwt.GraphicsUtil;
      var backgroundShape = this._gfxData.backgroundShape;
      if( this._gfxBackgroundEnabled ) {
        var usePath = this._gfxBorderEnabled;
        if( backgroundShape === undefined ) {
          this._gfxData.backgroundShape = this._createBackgroundShape( usePath );
        } else {
          if( usePath && backgroundShape === this._gfxData.rectShape ) {
            util.removeFromCanvas( this._gfxCanvas, backgroundShape );
            this._gfxData.backgroundInsert = false;
            delete this._gfxData.rectShape;
            this._gfxData.backgroundShape = this._createBackgroundShape( usePath );
          } else if( !usePath && backgroundShape === this._gfxData.pathElement ) {
            try{
              util.removeFromCanvas( this._gfxCanvas, backgroundShape );
            }catch(ex) {
              // TODO [tb] : unkown problem, possibly related to shadows. Fix asap.
            }
            this._gfxData.backgroundInsert = false;
            delete this._gfxData.pathElement;
            this._gfxData.backgroundShape = this._createBackgroundShape( usePath );
          }
        }
        if( !this._gfxData.backgroundInsert ) {
          var shape = this._gfxData.backgroundShape;
          util.addToCanvas( this._gfxCanvas, shape );
          this._gfxData.backgroundInsert = true;
        }
      } else if( this._gfxData.backgroundInsert ) {
        util.removeFromCanvas( this._gfxCanvas, backgroundShape );
        this._gfxData.backgroundInsert = false;
      }
    },

    _createBackgroundShape : function( usePath ) {
      var shape = null;
      var util = org.eclipse.rwt.GraphicsUtil;
      if( usePath ) {
        shape = util.createShape( "roundrect" );
        this._gfxData.pathElement = shape;
      } else {
        var shape = util.createShape( "rect" );
        util.setRectBounds( shape, "0%", "0%", "100%", "100%" );
        this._gfxData.rectShape = shape;
      }
      return shape;
    },


    /////////////////////////
    // internals - background

    _renderGfxBackground : function() {
      this._prepareBackgroundShape();
      var fillType = this.getGfxProperty( "fillType" );
      var util = org.eclipse.rwt.GraphicsUtil;
      if( fillType == "gradient" ) {
        var gradient = this.getGfxProperty( "gradient" );
        util.setFillGradient( this._gfxData.backgroundShape, gradient );
      } else if( fillType == "image" ) { 
        var image = this.getGfxProperty( "backgroundImage" );
        image = typeof image == "undefined" ? null : image;
        var size = this._getImageSize( image );
        util.setFillPattern( this._gfxData.backgroundShape, image, size[ 0 ], size[ 1 ] );
      } else { //assume fillType is "solid"
        var color = this.getGfxProperty( "backgroundColor" );
        color = color == "" ? null : color;
        util.setFillColor( this._gfxData.backgroundShape, color );
      }
    },
    
    ////////////////////
    // internal - border

    _renderGfxBorder : function() {
      this._style.borderWidth = 0;
      var inner = this._innerStyle;
      inner.borderWidth = 0;
      this._prepareBackgroundShape();
      var shape = this._gfxData.backgroundShape;
      var width = this.getGfxProperty( "borderMaxWidth" );
      var color = this.getGfxProperty( "borderColor" );
      org.eclipse.rwt.GraphicsUtil.setStroke( shape, color, width );
    },

    _layoutGfxBorder : function() {
      var rectDimension = [ this.getBoxWidth(), this.getBoxHeight() ];
      var oldDimension = this.getGfxProperty( "borderLayouted" );
      var changedX 
        = !oldDimension || ( rectDimension[ 0 ] !== oldDimension[ 0 ] );
      var changedY
        = !oldDimension || ( rectDimension[ 1 ] !== oldDimension[ 1 ] );
      if( changedX || changedY ) {
        this.setGfxProperty( "borderLayouted", rectDimension );
        this._setSimulatedPadding();
        var rectDimension = [ this.getBoxWidth(), this.getBoxHeight() ];
        var radii = this.getGfxProperty( "borderRadii" );
        var borderWidth = this.getGfxProperty( "borderWidths" );
        if( borderWidth != null && radii != null ) {
          var maxWidth = this.getGfxProperty( "borderMaxWidth" );
          var borderTop = 0;
          var borderRight = 0;
          var borderBottom = 0;
          var borderLeft = 0;
          // TODO [tb] : This hides the edges with width "0" by drawing
          // them outside the element so they are hidden. ("ContainerOverflow"
          // must be set to false.) However this does not always work in IE. 
          // See bug 306820.  
          if( maxWidth > 0 ) {
            borderTop = ( borderWidth[ 0 ] == 0 ? -maxWidth - 1 : maxWidth );
            borderRight = ( borderWidth[ 1 ] == 0 ? -maxWidth - 1 : maxWidth );
            borderBottom = ( borderWidth[ 2 ] == 0 ? -maxWidth - 1 : maxWidth );
            borderLeft = ( borderWidth[ 3 ] == 0 ? -maxWidth - 1: maxWidth );
          }
          var rectWidth =
            rectDimension[ 0 ] - ( borderLeft * 0.5 + borderRight * 0.5 );
          var rectHeight =
            rectDimension[ 1 ] - ( borderTop * 0.5 + borderBottom * 0.5 );
          var left = borderLeft * 0.5;
          var top = borderTop * 0.5;
          //a few safeguards:
          rectWidth = Math.max( 0, rectWidth );
          rectHeight = Math.max( 0, rectHeight );
          var shape = this._gfxData.pathElement;
          org.eclipse.rwt.GraphicsUtil.setRoundRectLayout( shape, 
                                                           left, 
                                                           top, 
                                                           rectWidth, 
                                                           rectHeight, 
                                                           radii );
        }
      }
    },

    _setSimulatedPadding : function() {
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      var width = this.getGfxProperty( "borderWidths" );
      if( width ) {
        var rect = this.getGfxProperty( "borderLayouted" );
        var style = this._innerStyle;
        style.top = width[ 0 ] + "px";
        style.left = width[ 3 ] + "px";
        style.width = Math.max( 0, rect[ 0 ] - width[ 3 ] - width[ 1 ] ) + "px";
        style.height = Math.max( 0, rect[ 1 ] - width[ 0 ] - width[ 2 ] ) + "px";
      } else {
        this._innerStyle.left = "0px";
        this._innerStyle.top = "0px";
        if( isMshtml ) {
          this._innerStyle.width = "";
          this._innerStyle.height = "";
          this.addToQueue( "width" );
          this.addToQueue( "height" );
        } else {
          this._innerStyle.width = "100%";
          this._innerStyle.height = "100%";
        }
      }
    },

    _handleFlushListener : function( value ) {
      var enhanced = this._innerStyle || this._gfxEnabled;
      this._layoutTargetNode = enhanced && !this._gfxBorderEnabled;
      if( this._gfxBorderEnabled || this._gfxShadowEnabled ) {
        this.addEventListener( "flush", this._gfxOnFlush, this );
      } else {
        this.removeEventListener( "flush", this._gfxOnFlush, this );      
      }
    },
    
    ////////////////////
    // internal - shadow
    
    _prepareShadowShape : function() {
      var util = org.eclipse.rwt.GraphicsUtil;
      if( this._gfxShadowEnabled ) {
        if( this._gfxData.shadowShape === undefined ) {
          this._createShadowShape();
          var canvasNode = util.getCanvasNode( this._gfxCanvas );
          org.eclipse.rwt.HtmlUtil.setPointerEvents( canvasNode, "none" );
        }
        var shape = this._gfxData.shadowShape;
        if( !this._gfxData.shadowInsert ) {
          var before = null;
          if( this._gfxData.backgroundInsert ) {
            before = this._gfxData.backgroundShape;
          }
          util.addToCanvas( this._gfxCanvas, shape, before );
          this._gfxData.shadowInsert = true;
        }
      } else if( this._gfxData.shadowInsert ) {
        util.removeFromCanvas( this._gfxCanvas, this._gfxData.shadowShape );
        // disable overflow:
        util.enableOverflow( this._gfxCanvas, 0, 0, null, null );
        delete this._gfxData.shadowInsert;
      }
    },

    _createShadowShape : function() {
      var shape = null;
      var util = org.eclipse.rwt.GraphicsUtil;
      var shape = util.createShape( "roundrect" );
      this._gfxData.shadowShape = shape;
      return shape;
    },
    
    _renderGfxShadow : function() {
      this._prepareShadowShape();
      if( this._gfxShadowEnabled ) {
        var util = org.eclipse.rwt.GraphicsUtil;
        var shadow = this.getGfxProperty( "shadow" );
        var shape = this._gfxData.shadowShape;
        util.setBlur( shape, shadow[ 3 ] );
        util.setFillColor( shape, shadow[ 5 ] );
        util.setOpacity( shape, shadow[ 6 ] );
        this._layoutGfxShadow();
      }
    },
    
    _layoutGfxShadow : function() {
      var util = org.eclipse.rwt.GraphicsUtil;
      var rect = [ this.getBoxWidth(), this.getBoxHeight() ];
      var shape = this._gfxData.shadowShape;
      var shadow = this.getGfxProperty( "shadow" );
      var radii = this.getGfxProperty( "borderRadii" );
      radii = radii === null ? [ 0, 0, 0, 0 ] : radii;
      var left = shadow[ 1 ];
      var top = shadow[ 2 ];
      var width = rect[ 0 ];
      var height = rect[ 1 ];
      var blur = shadow[ 3 ];
      var overflowLeft = left < 0 ? Math.abs( left ) + blur : 0;
      var overflowTop = top < 0 ? Math.abs( top ) + blur : 0;
      var overflowRight = Math.max( 0, blur + left );
      var overflowBottom = Math.max( 0, blur + top );
      var overflowWidth = width + overflowRight;
      var overflowHeight = height + overflowBottom;
      // overflow-area must be defined every time:
      util.enableOverflow( this._gfxCanvas, 
                           overflowLeft, 
                           overflowTop, 
                           overflowWidth, 
                           overflowHeight );
      util.setRoundRectLayout( shape, left, top, width, height, radii );
    },
    
    ////////////////////////////////////
    // internals - helper & eventhandler
    
    _getImageSize : function( source ) {
      var result = this.getUserData( "backgroundImageSize" ); 
      if( result == null ) {
        var themeStore = org.eclipse.swt.theme.ThemeStore.getInstance();
        result = themeStore.getImageSize( source );
      }
      return result;
    },
    
    _willBeLayouted : function() {
      return    typeof this._jobQueue != "undefined" 
             || !qx.lang.Object.isEmpty( this._layoutChanges );
    },

    // called after the element of the widget has been set
    _gfxOnElementChanged : function( event ) {
      if( event.getValue() == null && this._gfxCanvasAppended ) {
        this._removeCanvas();
      }
      if( event.getValue() != null && this._isCanvasReady() ) {
        if( this._gfxBackgroundEnabled ) {
          this._renderGfxBackground();
        }
        if( this._gfxShadowEnabled ) {
          this._renderGfxShadow();
        }
        // border is handled by widget queue
      }
    },

    //called if the GfxBorder object has been replaced
    _gfxOnBorderChanged : function( event ) {
      if ( !( event.getValue() instanceof org.eclipse.rwt.RoundedBorder ) ) {
        this._styleGfxBorder( null, null, null );
      }
    },

    _onCanvasAppear : function() {
      if( this._gfxCanvasAppended ) { 
        org.eclipse.rwt.GraphicsUtil.handleAppear( this._gfxCanvas );
      }
    },

    _gfxOnFlush : function( event ) {
      // TODO [tb] : refactor / optimize
      var changes = event.getData();
      if ( changes.paddingRight || changes.paddingBottom ) {
        this.setGfxProperty( "borderLayouted", null ); 
      }
      if( this._gfxBorderEnabled ) {
        this._layoutGfxBorder();
      }
      if( this._gfxShadowEnabled ) {
        this._layoutGfxShadow();
      }
    }
        
  }

} );
