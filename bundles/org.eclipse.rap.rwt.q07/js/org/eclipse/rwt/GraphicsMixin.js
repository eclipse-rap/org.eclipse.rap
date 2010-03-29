/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
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
      themeable : true
    }

  },

  members : {
    // NOTE : "gfx" (short for "graphics") is used in field-names to prevent
    //        potential name-clashes with the classes including this mixin.
    _gfxData : null,
    _gfxProperties : null,
    _gfxCanvas : null,
    _gfxEnabled : false,
    _gfxBorderEnabled : false,
    _gfxBackgroundEnabled : false,
    _gfxCanvasAppended : false,
    _gfxLayoutEnabled : false,

    //////////
    // GFX-API

    _applyBackgroundGradient : function( value, old ) {
      // color-theme values are NOT supported for gradient
      this.setGfxProperty( "gradient", value );
      this._handleGfxBackground();
    } ,

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
            && this._isCanvasReady() ) 
        {
          if( value != null ) {
            this._renderGfxBackground();
          } else {
            this._handleGfxBackground();
          }
        }
      } else {
        this.base( arguments, value );
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
      this.setGfxProperty( "borderLayouted", false ); // use GfxBorder to chcek
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
      var result = this._gfxEnabled;
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
                        && this.getGfxProperty( "borderColor") != null;
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
        if ( useBorder && this._willBeLayouted() ) {
          this._enableGfxLayout( true );
          //_layoutGfxBorder will be called on the next _layoutPost anyway
        } else {
          this._layoutGfxBorder();
        }
      } else if( toggle && !useBorder && this._innerStyle ) {
        this._setSimulatedPadding();
      }
    },

    _handleGfxBackground : function() {
      var useGradient = this.getGfxProperty( "gradient" ) != null;
      if( useGradient ) {
        this.setGfxProperty( "fillType", "gradient" );
      } else {
        var useImage = this.getBackgroundImage() != null; 
        this.setGfxProperty( "fillType", useImage? "image" : "solid" );        
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
      }
    },

    _handleGfxStatus : function() {
      var useGfx = ( this._gfxBorderEnabled || this._gfxBackgroundEnabled );
      if( useGfx != this._gfxEnabled ) {
        if( useGfx ) {
          this.addEventListener( "create", this._gfxOnCreate, this );
          this.addEventListener( "changeElement",
                                 this._gfxOnElementChanged,
                                 this );
          this._gfxEnabled = true;
        } else {
          this.removeEventListener( "create", this._gfxOnCreate, this );
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
      var ret = false;
      if( this._isCreated ) {
        if( this._gfxEnabled && this._gfxCanvasAppended ) {
          ret = true;
        } else if( this._gfxEnabled && !this._gfxCanvasAppended ) {
          if( this._gfxCanvas == null ) {
            this._createCanvas();
          }
          this._appendCanvas();
          ret = true;
        } else if( !this._gfxEnabled && this._gfxCanvasAppended ) {
          this._removeCanvas();
        }
      }
      return ret;
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
          this.removeStyleProperty( "filter" );
          this.__outerElementStyleProperties.filter = true;
        } else {
          this.removeStyleProperty( "opacity" );
          this.__outerElementStyleProperties.opacity = true;
          if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
            this.removeStyleProperty( "MozOpacity" );
            this.__outerElementStyleProperties.MozOpacity = true;
          }
        }
        this.prepareEnhancedBorder();
        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          this.addToQueue( "width" );
          this.addToQueue( "height" );
        }
        if( outline ) {
          this.setStyleProperty( "outline", outline );
        }
        this._applyOpacity( this.getOpacity() );
      }
      this._gfxData = {};      
      this._gfxCanvas = org.eclipse.rwt.GraphicsUtil.createCanvas();
      this._prepareGfxShape();
    },
    
    _appendCanvas : function() {
      var parentNode = this.getElement();
      var gfxNode 
        = org.eclipse.rwt.GraphicsUtil.getCanvasNode( this._gfxCanvas );
      if( gfxNode != null ) {
        parentNode.insertBefore( gfxNode, parentNode.firstChild );
      }
      this._gfxCanvasAppended = true;
      this.addEventListener( "appear", this._onCanvasAppear );
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
        this.removeEventListener( "appear", this._onCanvasAppear );
      }
    },

    // overwritten
    prepareEnhancedBorder : function() {
      //a precaution:
      if( !this._innerStyle && !this._innerStyleHidden ) {
        //This is mostly the same code as in the original "Widget"
        //class, but it is defined regardless of the browser
        //that is used, since the "enhanced border" function is needed
        //by the gfxBorder in every browser, including gecko
        var elem = this.getElement();
        var cl = this._borderElement = document.createElement("div");
        var es = elem.style;
        var cs = this._innerStyle = cl.style;
        if( !qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          cs.width = cs.height = "100%";
        }
        cs.position = "absolute";
        for( var i in this._styleProperties ) {
          switch( i ) {
            case "zIndex":
            case "filter":
            case "display":
            case "cursor":
              break;
            default:
              cs[i] = this._styleProperties[i];
              es[i] = "";
          }
        }
        // [if] Fix for bug 279800: Some focused widgets look strange in webkit
        es.outline = "none";
        // The next line is needed for clipping in IE. Overflow is an
        // "outerStyle" property, so this this css-value will never be set or 
        // reset. Therefore, this widget also no longer has the ability to 
        // show overflow:
        es.overflow = "hidden";
        for( var i in this._htmlProperties) {
          switch( i ) {
            case "unselectable":
              cl.unselectable = this._htmlProperties[i];
          }
        }
        while( elem.firstChild ) {
          cl.appendChild( elem.firstChild );
        }
        elem.appendChild( cl );
        if( this instanceof qx.ui.core.Parent ) {
          org.eclipse.swt.WidgetUtil.forAllChildren( this, function() {
          if( this._gfxCanvasAppended ) {
            this._onCanvasAppear();
          }
        } );
        }
      } else {
        if( this._innerStyleHidden ) {
          this._setSimulatedPadding();
        }
      }
    },

    //overwritten, analog to the above function:
    _getTargetNode : function() {
        return this._borderElement || this._element;
    },

    ////////////////////
    // internals - shape

    _prepareGfxShape : function() {
      var util = org.eclipse.rwt.GraphicsUtil;
      var shape = this._gfxData.currentShape;
      if( shape ) {
        if( !this._gfxBorderEnabled && shape !== this._gfxData.rect ) {
          util.removeFromCanvas( this._gfxCanvas, shape );
          if( !this._gfxData.rect ) {
            shape = this._createGfxShape( false );
          } else {
            shape = this._gfxData.rect;
          }
          util.addToCanvas( this._gfxCanvas, shape );
          this._gfxData.currentShape = shape;
        } else if(    this._gfxBorderEnabled 
                   && shape !== this._gfxData.pathElement ) 
        {
          util.removeFromCanvas( this._gfxCanvas, shape );
          if( !this._gfxData.pathElement ) {
            shape = this._createGfxShape( true );
          } else {
            shape = this._gfxData.pathElement;
          }
          util.addToCanvas( this._gfxCanvas, shape );
          this._gfxData.currentShape = shape;
        }
      } else { // no shape created at all
        shape = this._createGfxShape( this._gfxBorderEnabled );
        util.addToCanvas( this._gfxCanvas, shape );
        this._gfxData.currentShape = shape;
      }
    },

    _createGfxShape : function( usePath ) {
      var shape = null;
      var util = org.eclipse.rwt.GraphicsUtil;
      if( usePath ) {
        shape = util.createShape( "roundrect" );
        this._gfxData.pathElement = shape;
      } else {
        var shape = util.createShape( "rect" );
        util.setRectBounds( shape, "0%", "0%", "100%", "100%" );
        this._gfxData.rect = shape;
      }
      return shape;
    },


    /////////////////////////
    // internals - background

    _renderGfxBackground : function() {
      this._prepareGfxShape();
      var fillType = this.getGfxProperty( "fillType" );
      var util = org.eclipse.rwt.GraphicsUtil;
      if( fillType == "gradient" ) {
        var gradient = this.getGfxProperty( "gradient" );
        util.setFillGradient( this._gfxData.currentShape, gradient );
      } else if( fillType == "image" ) { 
        var image = this.getGfxProperty( "backgroundImage" );
        image = typeof image == "undefined" ? null : image;
        var size = this._getImageSize( image );
        util.setFillPattern( this._gfxData.currentShape, image, size[ 0 ], size[ 1 ] );
      } else { //assume fillType is "solid"
        var color = this.getGfxProperty( "backgroundColor" );
        color = color == "" ? null : color;
        util.setFillColor( this._gfxData.currentShape, color );
      }
    },
    
    ////////////////////
    // internal - border

    _renderGfxBorder : function() {
      this._style.borderWidth = 0;
      var inner = this._innerStyle || this._innerStyleHidden;
      inner.borderWidth = 0;
      this._prepareGfxShape();
      var shape = this._gfxData.currentShape;
      var width = this.getGfxProperty( "borderMaxWidth" );
      var color = this.getGfxProperty( "borderColor" );
      org.eclipse.rwt.GraphicsUtil.setStroke( shape, color, width );
    },

    _layoutGfxBorder : function() {
      var rectDimension = [ this.getBoxWidth(), this.getBoxHeight() ];
      var oldDimension = this.getGfxProperty( "rectDimension" );
      if(    !this.getGfxProperty( "borderLayouted" )
          || ( rectDimension[ 0 ] != oldDimension[ 0 ] )
          || ( rectDimension[ 1 ] != oldDimension[ 1 ] ) )
      {
        this.setGfxProperty( "rectDimension", rectDimension );
        this._setSimulatedPadding();
        var radii = this.getGfxProperty( "borderRadii" );
        var borderWidth = this.getGfxProperty( "borderWidths" );
        if( borderWidth != null && radii != null ) {
          var shape = this._gfxData.pathElement;
          var maxWidth = this.getGfxProperty( "borderMaxWidth" );
          this._enableGfxLayout( true );
          var rectDimension = this.getGfxProperty( "rectDimension" );
          var borderTop = 0;
          var borderRight = 0;
          var borderBottom = 0;
          var borderLeft = 0;
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
          org.eclipse.rwt.GraphicsUtil.setRoundRectLayout( shape, 
                                                           left, 
                                                           top, 
                                                           rectWidth, 
                                                           rectHeight, 
                                                           radii );
        } else {
          this._enableGfxLayout( false );
        }
        this.setGfxProperty( "borderLayouted", true );
      }
    },

    _setSimulatedPadding : function() {
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      var width = this.getGfxProperty( "borderWidths" );
      if( width ) {
        var rect = this.getGfxProperty( "rectDimension" );
        if( isMshtml && this._innerStyle  ) {
          //don't let ie know there is an inner element!
          this._innerStyleHidden = this._innerStyle;
          delete this._innerStyle;
        }
        var style = this._innerStyle || this._innerStyleHidden;
        style.top = width[ 0 ] + "px";
        style.left = width[ 3 ] + "px";
        style.width = Math.max( 0, rect[ 0 ] - width[ 3 ] - width[ 1 ] ) + "px";
        style.height = Math.max( 0, rect[ 1 ] - width[ 0 ] - width[ 2 ] ) + "px";
      } else {
        if( this._innerStyleHidden ) {
          this._innerStyle = this._innerStyleHidden;
          delete this._innerStyleHidden;
        }
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

    _enableGfxLayout : function( value ) {
      this._gfxLayoutEnabled = value;
      var util = org.eclipse.rwt.GraphicsUtil;
      util.setLayoutMode( this._gfxCanvas, value ?  "absolute" : "relative" );
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

    //called after the element of the widget has been set
    _gfxOnElementChanged : function( event ) {
      if ( event.getValue() === null && this._gfxCanvasAppended ) {
        this._removeCanvas();
      }
    },

    //called after the element has been applied 
    _gfxOnCreate : function() {
      if( this._isCanvasReady() ) {
        this._renderGfxBackground();
        //border is handled by widget queue
      }
    },

    //called if the GfxBorder object has been replaced
    _gfxOnBorderChanged : function( event ) {
      if ( !( event.getValue() instanceof org.eclipse.rwt.RoundedBorder ) ) {
        this._styleGfxBorder( null, null, null );
      }
    },

    _onCanvasAppear : function() {
      org.eclipse.rwt.GraphicsUtil.handleAppear( this._gfxCanvas );
    },

    //overwritten:
    _layoutPost : function( changes ) {
      // This function is also implemented in "Terminator" and "Parent",
      // therefore the mixin can not be applied to "Widget" itself. 
      // For any widget that implements "_layoutPost", rounded corners will
      // not work unless the widget implements the code below itself:
      this.base( arguments, changes );
      if( this._gfxLayoutEnabled ) {
        if ( changes.paddingRight || changes.paddingBottom ) {
          this.setGfxProperty( "borderLayouted", false ); 
        }
        this._layoutGfxBorder();
      }
    }
        
  }

});
