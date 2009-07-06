/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

// TODO [tb] :
// - opacity / antialias problem in ie
// - accepted bug: widgets with no dimensions of their own
//                 wont work together with a gfxBorder

qx.Mixin.define("org.eclipse.rwt.GfxMixin", {

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
    _gfxData : null,
    _gfxProperties : null,
    _gfxNode : null,
    _gfxEnabled : false,
    _gfxBorderEnabled : false,
    _gfxBackgroundEnabled : false,
    _gfxNodeAppended : false,
    _gfxLayoutEnabled : false,

    //-------------------- gfx api -----------------------------

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
            && this._gfxNodeReady() ) {
          this._renderGfxBackground();
        }
      } else {
        this.base( arguments, value );
      }
    },

    //called by RoundedBorder:
    _styleGfxBorder : function( width, color, radii ) {
      this.setGfxProperty( "borderWidths", width );
      var max = 0;
      if( width ) {
        for( var i = 0; i < width.length; i++ ) {
          max = Math.max( max, width[ i ] );
        }
      }
      this.setGfxProperty( "borderMaxWidth", max );
      this.setGfxProperty( "borderColor", color );
      this.setGfxProperty( "borderRadii", radii );
      // TODO  [tb] : check if ONLY the color is changed before doing this:
      this.setGfxProperty( "borderLayouted", false ); // use GfxBorder to chcek

      this._handleGfxBorder();
    },

    //-------------- common gfx functions -----------------

    setGfxProperty : function( key, value ) {
      if( this._gfxProperties === null ) {
       this._gfxProperties = {};
      }
      this._gfxProperties[ key ] = value;
    },

    // TODO [tb] : return default values if undefined?
    getGfxProperty : function( key ) {
      return ( this._gfxProperties ? this._gfxProperties[ key ] : null );
    },

    _handleGfxBorder : function() {
      var useBorder = (    this.getGfxProperty( "borderRadii" ) != null
                        && this.getGfxProperty( "borderWidths" ) != null
                        && this.getGfxProperty( "borderColor") != null
                      );
      var toggle = ( this._gfxBorderEnabled != useBorder );
      if( toggle ) {
        if( useBorder ) {
          this.addEventListener( "changeBorder", this._gfxBorderChanged, this );
          this._gfxBorderEnabled = true;
        } else {
          this.removeStyleProperty( "padding" );
          this.removeEventListener( "changeBorder",
                                    this._gfxBorderChanged,
                                    this );
          this._gfxBorderEnabled = false;
        }
        this._handleGfxBackground();
        this._handleGfxStatus();
      }
      if( ( toggle || useBorder ) && this._gfxNodeReady() ) {
        this._renderGfxBorder();
        if ( useBorder && this.willBeLayouted() ) {
          this._enableGfxLayout( true );
          //_layoutGfxBorder will be called on the next _layoutPost anyway
        } else {
          this._layoutGfxBorder();
        }
      } else if( toggle && !useBorder && this._innerStyle ) {
        this._setSimulatedPadding();
      }
    },

    willBeLayouted : function() {
      return !!this._jobQueue || !qx.lang.Object.isEmpty( this._layoutChanges );
    },

    _handleGfxBackground : function() {
      var useGradient = this.getGfxProperty( "gradient" ) != null;
      // TODO [tb] : Dont set it every single time!
      this.setGfxProperty( "fillType", useGradient ? "gradient" : "solid" );
      var useBackground = ( useGradient || this._gfxBorderEnabled );
      var toggle = ( this._gfxBackgroundEnabled != useBackground );

      if( toggle ) {
        if( useBackground ) {
          var backgroundColor = this.getStyleProperty( "backgroundColor" );
          this.removeStyleProperty( "backgroundColor" );
          this.setGfxProperty( "backgroundColor", backgroundColor );
          this._gfxBackgroundEnabled = true;
        } else {
          this._gfxBackgroundEnabled = false;
          this._applyBackgroundColor( this.getBackgroundColor() );
          this.setGfxProperty( "backgroundColor", null );
        }
        this._handleGfxStatus();
     }

      if( ( toggle || useBackground ) && this._gfxNodeReady() ) {
        this._renderGfxBackground();
      }

      if (qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        if( toggle && !useBackground ) {
         this._disableVmlColorRestore();
        }
      }
    },

    _handleGfxStatus : function() {
      var useGfx = ( this._gfxBorderEnabled || this._gfxBackgroundEnabled );
      if( useGfx != this._gfxEnabled ) {
        if( useGfx ) {
          this.addEventListener( "create", this._applyGfxProperties, this );
          this.addEventListener( "changeElement",
                                 this._gfxNodeParentChanged,
                                 this );
          this._gfxEnabled = true;
        } else {
          this.removeEventListener( "create", this._applyGfxProperties, this );
          this.removeEventListener( "changeElement",
                                 this._gfxNodeParentChanged,
                                 this );
          this._gfxEnabled = false;
        }
      }
    },

    //-----------------------------------------------------

    _gfxNodeReady : function() {
      var ret = false;
      if( this._isCreated ) {
        if( this._gfxEnabled && this._gfxNodeAppended ) {
          ret = true;
        } else if( this._gfxEnabled && !this._gfxNodeAppended ) {
          if( !this._gfxNode ) {
            this._createGfxNode();
          }
          this._appendGfxNode();
          ret = true;
        } else if( !this._gfxEnabled && this._gfxNodeAppended ) {
          this._removeGfxNode();
        }
      }
      return ret;
    },

    _appendGfxNode : function() {
      var parentNode = this.getElement();
      parentNode.insertBefore(this._gfxNode, parentNode.firstChild);
      this._gfxNodeAppended = true;
    },

    _removeGfxNode : function() {
      this._gfxNode.parentNode.removeChild(this._gfxNode);
      this._gfxNodeAppended = false;
    },

    //----------------------------------------------------

    //called if the element of the widget has been replaced
    _gfxNodeParentChanged : function( event ) {
      if ( event.getValue() === null && this._gfxNodeAppended ) {
        this._removeGfxNode();
      }
    },

    //called if the GfxBorder object has been replaced
    _gfxBorderChanged : function( event ) {
      if ( ! ( event.getValue() instanceof org.eclipse.rwt.RoundedBorder ) ) {
        this._styleGfxBorder( null, null, null );
      }
    },

    //called on element create
    _applyGfxProperties : function() {
      if( this._gfxNodeReady() ) {
        this._renderGfxBackground();
        //border is handled by widget queue
      }
    },

    //overwritten:
    _layoutPost : function( changes ) {
      //this function is also implemented in "Terminator" and "Parent"
      //without a "super"-call, therefore the mixin should not be
      //applied to "Widget" itself. For any widget that implements
      //"_layoutPost", this mixin will not work if
      // "this.base( arguments, changes );" is not called there
      this.base( arguments, changes );
      if( this._gfxLayoutEnabled ) {
       this._layoutGfxBorder();
      }
    },

    //----------------------------------------------------

    _createGfxNode : function() {
      if ( !this._innerStyle ) {
        var outline = null;
        if( qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
          //this prevents a graphical glitch in Safari
          outline = this.getStyleProperty( "outline" );
          this.removeStyleProperty( "outline" );
          this.__outerElementStyleProperties.outline = true;
        }
        // make opacity work
        if (qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          this.removeStyleProperty( "filter" );
          this.__outerElementStyleProperties.filter = true;
        } else {
          this.removeStyleProperty( "opacity" );
          this.__outerElementStyleProperties.opacity = true;
          if (qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
            this.removeStyleProperty( "MozOpacity" );
            this.__outerElementStyleProperties.MozOpacity = true;
          }
        }
        this.prepareEnhancedBorder();

        if( outline ) {
          this.setStyleProperty( "outline", outline );
        }
        delete outline;

        this._applyOpacity( this.getOpacity() );
      }
      var statics = org.eclipse.rwt.GfxMixin;
      var mode = statics.getSupportedRendermode();
      switch( mode ){
        case statics.VML_RENDERER:
          this._createVmlNode();
        break;
        case statics.SVG_RENDERER:
          this._createSvgNode();
        break;
        default:
      }
    },

    prepareEnhancedBorder : function() {
      //a precaution:
      if( !this._innerStyle && !this._innerStyleHidden ) {
        //This is the *exact* same code as in the original "Widget"
        //class, only that it is defined regardless of the browser
        //that is used, since the "enhanced border" function is needed
        //by the gfxBorder in every browser
        //Overwritten:
        var elem = this.getElement();
        var cl = this._borderElement = document.createElement("div");
        var es = elem.style;
        var cs = this._innerStyle = cl.style;
        if (qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        } else {
          cs.width = cs.height = "100%";
        }
        cs.position = "absolute";
        for ( var i in this._styleProperties ) {
          switch(i) {
            case "zIndex":
            case "filter":
            case "display":
              break;
            default:
              cs[i] = this._styleProperties[i];
              es[i] = "";
          }
        }
        // [if] Fix for bug
        // 279800: Some focused widgets look strange in webkit
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=279800
        if( qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
          es.outline = "none";
        }

        for (var i in this._htmlProperties) {
          switch( i ) {
            case "unselectable":
              cl.unselectable = this._htmlProperties[i];
          }
        }
        while (elem.firstChild) {
          cl.appendChild(elem.firstChild);
        }
        elem.appendChild(cl);
      } else {
        if( this._innerStyleHidden ) {
          this._setSimulatedPadding();
        }
      }
    },

    //analog to the above function:
    _getTargetNode : function() {
        return this._borderElement || this._element;
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
        style.width = ( rect[ 0 ] - width[ 3 ] - width[ 1 ] ) + "px";
        style.height = ( rect[ 1 ] - width[ 0 ] - width[ 2 ] ) + "px";
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

    //--------------------------------------------------------

    _renderGfxBackground : function() {
      var statics = org.eclipse.rwt.GfxMixin;
      var mode = statics.getSupportedRendermode();
      switch( mode ){
        case statics.VML_RENDERER:
        this._prepareVmlShape();
          this._renderVmlBackground();
        break;
        case statics.SVG_RENDERER:
          this._prepareSvgShape();
          this._renderSvgBackground();
        break;
        default:
      }
    },

    _renderGfxBorder : function() {
      this._style.borderWidth = 0;
      var inner = this._innerStyle || this._innerStyleHidden;
      inner.borderWidth = 0;
      delete inner;
      var statics = org.eclipse.rwt.GfxMixin;
      var mode = statics.getSupportedRendermode();
      switch( mode ){
        case statics.VML_RENDERER:
          this._prepareVmlShape();
          this._styleVmlBorder();
        break;
        case statics.SVG_RENDERER:
          this._prepareSvgShape();
          this._styleSvgBorder();
        break;
        default:
      }
    },

    _layoutGfxBorder : function() {
      var rectDimension = [ this.getBoxWidth(), this.getBoxHeight() ];
      var oldDimension = this.getGfxProperty( "rectDimension" );
      if(    !this.getGfxProperty( "borderLayouted" )
          || ( rectDimension[ 0 ] != oldDimension[ 0 ] )
          || ( rectDimension[ 1 ] != oldDimension[ 1 ] )
      ) {
        this.setGfxProperty( "rectDimension", rectDimension );
        this._setSimulatedPadding();
        var statics = org.eclipse.rwt.GfxMixin;
        var mode = statics.getSupportedRendermode();
        switch( mode ){
          case statics.VML_RENDERER:
            this._layoutVmlBorder();
          break;
          case statics.SVG_RENDERER:
            this._layoutSvgBorder();
          break;
          default:
        }
        this.setGfxProperty( "borderLayouted", true );
      }
    },

    _enableGfxLayout : function( value ) {
      var statics = org.eclipse.rwt.GfxMixin;
      var mode = statics.getSupportedRendermode();
      switch( mode ){
        case statics.VML_RENDERER:
          this._enableVmlLayout( value );
        break;
        case statics.SVG_RENDERER:
          this._enableSvgLayout( value );
        break;
        default:
      }
    },

    //-------------------------- SVG --------------------------------------//

    _createSvgNode : function() {
      this._gfxData = {};
      var create = org.eclipse.rwt.GfxMixin.createSVGNode;
      var hash = this.toHashCode();

      var node = create( "svg" );
      node.style.position = "absolute"
      node.style.left = "0px";
      node.style.right = "0px";
      node.style.width = "100%";
      node.style.height = "100%"
      node.style.overflow = "hidden";

      var defs = create( "defs" );
      node.appendChild(defs);

      var grad = create( "linearGradient" ); // TODO [tb] : create lazy ?
      grad.setAttribute( "id", "gradient_"+hash);
      grad.setAttribute( "x1", 0 );
      grad.setAttribute( "y1", 0 );
      grad.setAttribute( "x2", 0 );
      grad.setAttribute( "y2", 1 );
      defs.appendChild( grad );

      this._gfxNode = node;
      this._gfxData.gradientId = hash;;
      this._gfxData.grad = grad;

      this._prepareSvgShape();
    },

    _prepareSvgShape : function() {
      var data = this._gfxData;
      var shape = data.currentShape;
      if( shape ) {
        if( !this._gfxBorderEnabled && shape !== data.rect ) {
          this._gfxNode.removeChild( shape );
          if( !data.rect ) {
            shape = this._createSvgShape( false );
          } else {
            shape = data.rect;
          }
          this._gfxNode.appendChild( shape );
          data.currentShape = shape;
        } else if( this._gfxBorderEnabled && shape !== data.pathElement ) {
          this._gfxNode.removeChild( shape );
          if( !data.pathElement ) {
            shape = this._createSvgShape( true );
          } else {
            shape = data.pathElement;
          }
          this._gfxNode.appendChild( shape );
          data.currentShape = shape;
        }
      } else { // no shape created at all
        shape = this._createSvgShape( this._gfxBorderEnabled );
        this._gfxNode.appendChild( shape );
        data.currentShape = shape;
      }
    },

    _createSvgShape : function( usePath ) {
      var create = org.eclipse.rwt.GfxMixin.createSVGNode;
      var shape = null;
      if( usePath ) {
        var pathElement = create( "path" );
        this._gfxData.pathElement = pathElement;
        shape = pathElement;
      } else {
        var rect = create( "rect" );
        rect.setAttribute( "width", "100%" );
        rect.setAttribute( "height", "100%" );
        rect.setAttribute( "x", 0 );
        rect.setAttribute( "y", 0 );
        this._gfxData.rect = rect;
        shape = rect;
      }
      shape.setAttribute( "stroke", "none" );
      return shape;
    },

    _renderSvgBackground : function() {
      var fillType = this.getGfxProperty( "fillType" );
      if( fillType == "gradient" ) {
        this._gfxData.currentShape.setAttribute(
          "fill",
          "url(#gradient_" + this._gfxData.gradientId + ")"
        );
        var gradient = this.getGfxProperty( "gradient" );
        var create = org.eclipse.rwt.GfxMixin.createSVGNode;
        var gradDef = this._gfxData.grad;
        var stopColor = null;
        while( stopColor = gradDef.childNodes[ 0 ] ) {
          gradDef.removeChild( stopColor );
        }
        var arrLength = gradient.length;
        for( var colorPos = 0; colorPos < arrLength; colorPos++ ) {
          stopColor = create( "stop" );
          stopColor.setAttribute( "offset", gradient[ colorPos ][ 0 ] );
          stopColor.setAttribute( "stop-color", gradient[ colorPos ][ 1 ] );
          gradDef.appendChild( stopColor );
        }
      } else { //assume fillType is "solid"
        var color = this.getGfxProperty( "backgroundColor" );
        if( color ) {
          this._gfxData.currentShape.setAttribute( "fill", color );
        } else {
          this._gfxData.currentShape.setAttribute( "fill", "none" );
        }
      }
    },

    _styleSvgBorder : function() {
      var width = this.getGfxProperty( "borderMaxWidth" );
      var color = this.getGfxProperty( "borderColor" );
      var shape = this._gfxData.currentShape;
      shape.setAttribute( "stroke-width", ( width ? width : "0" ) + "px");
      shape.setAttribute( "stroke", ( color ? color : "none" ) );
    },

    _layoutSvgBorder : function() {
      var shape = this._gfxData.pathElement;
      var radius = this.getGfxProperty( "borderRadii" );
      var widths = this.getGfxProperty( "borderWidths" );
      if( widths && radius ) {
        var maxWidth = this.getGfxProperty( "borderMaxWidth" );
        this._enableSvgLayout( true );
        var rectDimension = this.getGfxProperty( "rectDimension" );
        var path = org.eclipse.rwt.GfxMixin.createSvgPath(
          widths,
          maxWidth,
          rectDimension,
          radius
        );
        shape.setAttribute( "d", path );
      } else {
        this._enableSvgLayout( false );
      }
    },

    _enableSvgLayout : function( value ) {
      this._gfxLayoutEnabled = value;
    },

    //-------------------------- VML  --------------------------------------//

    // TODO [tb] : use variant instead of switch ? (ignores version)

    _createVmlNode : function() {
      this._gfxData = {};
      var create = org.eclipse.rwt.GfxMixin.createVMLNode;
      var node = create( "group" );
      node.style.position = "absolute"
      node.style.width = "100%";
      node.style.height = "100%";
      node.style.top = "0";
      node.style.left = "0";
      this._gfxNode = node;

      var fill = create( "fill" );
      fill.method = "sigma";
      fill.angle = 180;
      this._gfxData.fill = fill;

      this._prepareVmlShape();
    },

    _prepareVmlShape : function() {
      var data = this._gfxData;
      var shape = data.currentShape;
      if( shape ) {
        if( !this._gfxBorderEnabled && shape !== data.rect ) {
          this._gfxNode.removeChild( shape );
          shape.removeChild( data.fill );
          if( !data.rect ) {
            shape = this._createVmlShape( false );
          } else {
            shape = data.rect;
          }
          shape.appendChild( data.fill );
          this._gfxNode.appendChild( shape );
          data.currentShape = shape;
        } else if( this._gfxBorderEnabled && shape !== data.pathElement ) {
          this._gfxNode.removeChild( shape );
          shape.removeChild( data.fill );
          if( !data.pathElement ) {
            shape = this._createVmlShape( true );
          } else {
            shape = this._gfxData.pathElement;
          }
          shape.appendChild( data.fill );
          this._gfxNode.appendChild( shape );
          data.currentShape = shape;
        }
      } else { // no shape created at all
        shape = this._createVmlShape( this._gfxBorderEnabled );
        shape.appendChild( data.fill );
        this._gfxNode.appendChild( shape );
        data.currentShape = shape;
      }
    },

    _createVmlShape : function( usePath ) {
      var create = org.eclipse.rwt.GfxMixin.createVMLNode;
      var shape = null;
      if( usePath ) {
        var pathElement = create( "shape" );
        pathElement.coordsize="100,100";
        pathElement.coordorigin="0 0";
        pathElement.style.width = 100;
        pathElement.style.height = 100;
        this._gfxData.pathElement = pathElement;
        shape = pathElement;
      } else {
        var rect = create( "rect" );
        rect.style.position = "absolute"
        rect.style.width = "100%";
        rect.style.height = "100%";
        rect.style.top = "0";
        rect.style.left = "0";
        rect.style.antialias = false;
        this._gfxData.rect = rect;
        shape = rect;
      }
      shape.stroked = false;
      return shape;
    },

    _renderVmlBackground : function() {
      var fill = this._gfxData.fill;
      var fillType = this.getGfxProperty( "fillType" );
      if( fillType == "gradient" ) {
        this._gfxData.currentShape.removeChild( fill );
        var gradient = this.getGfxProperty( "gradient" );
        var arrLength = gradient.length;
        fill.on = true;
        fill.type = "gradient";
        //the "color" attribute of fill is lost when the node
        //is removed from the dom. However, it can be overwritten
        //by a transition colors, so it doesn't matter
        var startColor = gradient[ 0 ][ 1 ];
        //fill.color = startColor;
        fill.color2 = gradient[ arrLength - 1 ][ 1 ];
        this._disableVmlColorRestore();
        var transitionColors = "0% " + startColor;
        var lastColor = qx.util.ColorUtil.stringToRgb( startColor );
        var nextColor = null;
        var lastOffset = 0;
        var currentOffset = null;
        for( var colorPos = 1; colorPos < arrLength; colorPos++ ) {
          var color = gradient[ colorPos ][ 1 ];
          nextColor = qx.util.ColorUtil.stringToRgb( color );
          nextOffset = gradient[ colorPos ][ 0 ];
          transitionColors += ", ";
          transitionColors += org.eclipse.rwt.GfxMixin.transitionColors(
            lastColor, nextColor, lastOffset, nextOffset, 3
          );
          transitionColors += ", " + ( nextOffset * 100 ) + "% " + color;
          lastColor = nextColor;
          lastOffset = nextOffset;
        }
        fill.colors = transitionColors;
        this._gfxData.currentShape.appendChild( fill );
      } else { //assume fillType is "solid"
        var color = this.getGfxProperty( "backgroundColor" );
        fill.type = "solid";
        if( color ) {
          fill.on = true;
          fill.color = color;
          this._enableVmlColorRestore( color );
        } else {
          fill.on = false;
          this._enableVmlColorRestore( false );
        }
      }
    },

    _enableVmlColorRestore : function( color ) {
      if( this._gfxData.colorBackup != color ) {
        if( typeof this._gfxData.colorBackup == "undefined" ) {
          this.addEventListener( "appear", this._vmlRestoreColor );
        }
        this._gfxData.colorBackup = color;
      }
    },

    _disableVmlColorRestore : function() {
      if( typeof this._gfxData.colorBackup != "undefined" ) {
        this.removeEventListener( "appear", this._vmlRestoreColor );
      }
      delete this._gfxData.colorBackup;
    },

    _vmlRestoreColor : function() {
      if( typeof this._gfxData.colorBackup != "undefined" ) {
        if( this._gfxData.colorBackup ) {
          this._gfxData.fill.color = this._gfxData.colorBackup;
        } else {
          this._gfxData.fill.on = false;
        }
      }
    },

    // About VML-borders and opacity:
    // There is a bug in the VML antialiasing, that can produce grey pixels
    // around vml elements if the css-opacity-filter is used on any of its
    // parents, including the widgets div or any of the parent-widgets divs.
    // However this ONLY happens if the element that the opacity is applied to,
    // does NOT have a background of its own!
    // If antialiasing is turned off, the effect is gone, but without
    // antaliasing the element looks just as ugly as with the glitch.
    _styleVmlBorder : function() {
      var shape = this._gfxData.currentShape;
      var width = this.getGfxProperty( "borderMaxWidth" );
      var color = this.getGfxProperty( "borderColor" );
      if( width > 0 ) {
        shape.stroked = true;
        shape.strokecolor = color;
        shape.strokeweight = width + "px";
        // TODO [tb] : joinstyle (currently not implemente because it would
        // need the subelement "stroke" and create conflict with the other
        // stroke-attributes - IE "forgets" them if the element is moved in DOM)
      } else {
        shape.stroked = false;
      }
    },

    _layoutVmlBorder : function() {
      var radius = this.getGfxProperty( "borderRadii" );
      var widths = this.getGfxProperty( "borderWidths" );
      if( widths && radius ) {
        var shape = this._gfxData.pathElement;
        this._gfxNode.removeChild( shape );
        var maxWidth = this.getGfxProperty( "borderMaxWidth" );
        var rectDimension = this.getGfxProperty( "rectDimension" );
        this._enableVmlLayout( true );
        var path = org.eclipse.rwt.GfxMixin.createVmlPath(
          widths,
          maxWidth,
          rectDimension,
          radius
        );
        shape.path = path.shape;
        //shape.style.clip = path.clip;
        this._gfxNode.appendChild( shape );
      } else {
        this._enableVmlLayout( false );
      }
      this._vmlRestoreColor();
    },

    _enableVmlLayout : function( value) {
      if( this._gfxLayoutEnabled != value ) {
        if( value ) {
          var f = org.eclipse.rwt.GfxMixin.VMLFACTOR;
          this._gfxLayoutEnabled = true;
          this._gfxNode.style.width = 100 + "px";
          this._gfxNode.style.height = 100 + "px";
          var coordsize = 100 * f + "," + 100 * f;
          this._gfxNode.setAttribute( "coordsize", coordsize );
        } else {
          this._gfxLayoutEnabled = false;
          this._gfxNode.style.width = "100%";
          this._gfxNode.style.height = "100%";
          this._gfxNode.setAttribute( "coordsize", "1000, 1000" );
        }
      }
    }
  },

  statics : {
    NO_RENDERER  : 0,
    SVG_RENDERER : 1,
    VML_RENDERER : 2,

    VMLQCIRCEL : -65535 * 90,
    VMLFACTOR : 10,

    _renderMode : -1,
    _vmlEnabled : false,

    //--------------------- select render mode ----------------------------//

    getSupportedRendermode : function() {
      if ( this._renderMode == -1) {
        var engine = qx.core.Client.getEngine();
        var version = qx.core.Client.getVersion();
        var mode = this.NO_RENDERER;
        if ( ( engine == "mshtml" ) && ( version >= 5.5 ) ) {
          mode = this.VML_RENDERER;
        } else if ( ( engine == "gecko" )  && ( version >= 1.8 ) ) {
          mode = this.SVG_RENDERER;
        } else if ( ( engine == "webkit" ) && ( version >= 523 ) ) {
          mode = this.SVG_RENDERER;
        } else if ( ( engine == "opera" )  && ( version >= 9 ) ) {
          mode = this.SVG_RENDERER;
        }
        this._renderMode = mode;
      }

      return this._renderMode;
    },



    //-------------------------- SVG --------------------------------------//
    createSVGNode : function( type ) {
      return document.createElementNS( "http://www.w3.org/2000/svg", type );
    },

    createSvgPath : function( borderWidth, maxWidth, dimension, radius ) {
      var borderTop = 0;
      var borderRight = 0;
      var borderBottom = 0;
      var borderLeft = 0;
      if( maxWidth > 0 ) {
       borderTop = ( borderWidth[ 0 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderRight = ( borderWidth[ 1 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderBottom = ( borderWidth[ 2 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderLeft = ( borderWidth[ 3 ] == 0 ? -maxWidth - 1: maxWidth);
      }
      var rectWidth =
        dimension[ 0 ] - ( borderLeft * 0.5 + borderRight * 0.5 );
      var rectHeight =
        dimension[ 1 ] - ( borderTop * 0.5 + borderBottom * 0.5 );
      var left = borderLeft * 0.5;
      var top = borderTop * 0.5;
      //a few safeguard:
      rectWidth = Math.max( 0, rectWidth );
      rectHeight = Math.max( 0, rectHeight );
      var maxRadius = Math.min( rectWidth, rectHeight ) / 2;

      var radiusLeftTop = Math.min( radius[ 0 ], maxRadius );
      var radiusTopRight = Math.min( radius[ 1 ], maxRadius );
      var radiusRightBottom = Math.min( radius[ 2 ], maxRadius );
      var radiusBottomLeft = Math.min( radius[ 3 ], maxRadius );

      var path = [];

      path.push( "M", left , top + radiusLeftTop );
      if( radiusLeftTop > 0 ) {
        path.push( "A", radiusLeftTop, radiusLeftTop, 0, 0, 1);
        path.push( left + radiusLeftTop, top );
      }
      path.push( "L", left + rectWidth - radiusTopRight, top );
      if( radiusTopRight > 0 ) {
        path.push( "A", radiusTopRight, radiusTopRight, 0, 0, 1);
      }
      path.push( left + rectWidth, top + radiusTopRight);
      path.push( "L", left + rectWidth, top + rectHeight - radiusRightBottom  );
      if( radiusRightBottom > 0 ) {
        path.push( "A", radiusRightBottom, radiusRightBottom, 0, 0, 1);
      }
      path.push( left + rectWidth - radiusRightBottom, top + rectHeight );
      path.push( "L", left + radiusBottomLeft, top + rectHeight );
      if( radiusBottomLeft > 0 ) {
        path.push( "A", radiusBottomLeft, radiusBottomLeft, 0, 0, 1);
      }
      path.push( left , top + rectHeight - radiusBottomLeft );
      path.push( "Z" );

      return path.join(" ");
    },

    //-------------------------- VML --------------------------------------//

    createVMLNode : function( type ) {
      if ( !this._vmlEnabled ) {
        document.namespaces.add( "v", "urn:schemas-microsoft-com:vml");
        document.namespaces.add( "o",
                                 "urn:schemas-microsoft-com:office:office");
        var sheet = document.createStyleSheet();
        sheet.cssText = "v\\:* { behavior:url(#default#VML);" +
                                "display:inline-block; } "+
                        "o\\:* { behavior: url(#default#VML);}";

        this._vmlEnabled = true;
      }
      return document.createElement( "v:" + type );
    },



    createVmlPath : function( borderWidth, maxWidth, dimension, radius ) {
      var f = this.VMLFACTOR;
      var quarter = this.VMLQCIRCEL;

      var borderTop = 0;
      var borderRight = 0;
      var borderBottom = 0;
      var borderLeft = 0;

      if( maxWidth > 0 ) {
       borderTop = ( borderWidth[ 0 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderRight = ( borderWidth[ 1 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderBottom = ( borderWidth[ 2 ] == 0 ? -maxWidth - 1 : maxWidth);
       borderLeft = ( borderWidth[ 3 ] == 0 ? -maxWidth - 1: maxWidth);
      }

      var clipTop = ( borderTop < 0 ? - borderTop : 0 );
      var clipLeft = ( borderLeft < 0 ? - borderLeft : 0 );
      var clipRight = (   borderRight < 0
                        ? ( clipLeft ? clipLeft : 1 ) + dimension[ 0 ]
                        : "auto"
                       );
      var clipBottom = (   borderBottom < 0
                         ? ( clipTop ? clipTop : 1 ) + dimension[ 1 ]
                         : "auto"
                       );
      var clip = [];
      clip.push( "rect(" );
      clip.push( clipTop, clipRight, clipBottom, clipLeft );
      clip.push( ")" );

      var rectWidth =
        ( dimension[ 0 ] - ( borderLeft * 0.5 + borderRight * 0.5 ) ) * f ;
      var rectHeight =
        ( dimension[ 1 ] - ( borderTop * 0.5 + borderBottom * 0.5 ) ) * f ;

      var left = ( ( borderLeft * 0.5 ) - 0.5 ) * f;
      var top = ( ( borderTop * 0.5 ) - 0.5 ) * f;

      //a few safeguards:
      rectWidth = Math.max( 0, rectWidth );
      rectHeight = Math.max( 0, rectHeight );
      var maxRadius = Math.min( rectWidth, rectHeight ) / 2;

      var radiusLeftTop = Math.min( radius[ 0 ] * f, maxRadius );
      var radiusTopRight = Math.min( radius[ 1 ] * f, maxRadius );
      var radiusRightBottom = Math.min( radius[ 2 ] * f, maxRadius );
      var radiusBottomLeft = Math.min( radius[ 3 ] * f, maxRadius );

      var path = [];

      if( radiusLeftTop > 0 ) {
        path.push( "AL", left + radiusLeftTop, top + radiusLeftTop );
        path.push( radiusLeftTop, radiusLeftTop, 2 * quarter, quarter );
      } else {
        path.push( "M", left , top + radiusLeftTop );
      }

      if( radiusTopRight > 0 ) {
        path.push( "AE", left + rectWidth - radiusTopRight );
        path.push( top + radiusTopRight );
        path.push( radiusTopRight, radiusTopRight, 3 * quarter, quarter );
      } else {
        path.push( "L", left + rectWidth, top );
      }

      if( radiusRightBottom > 0 ) {
        path.push( "AE", left + rectWidth - radiusRightBottom );
        path.push( top + rectHeight - radiusRightBottom );
        path.push( radiusRightBottom, radiusRightBottom, 0, quarter );
      } else {
        path.push( "L", left + rectWidth, top + rectHeight );
      }

      if( radiusBottomLeft > 0 ) {
        path.push( "AE", left + radiusBottomLeft );
        path.push( top + rectHeight - radiusBottomLeft );
        path.push( radiusBottomLeft, radiusBottomLeft, quarter, quarter );
      } else {
        path.push( "L", left, top + rectHeight );
      }

      path.push( "X E" );

      return {
        shape : path.join(" "),
        clip : clip.join(" ")
      };
    },

    transitionColors : function( color1, color2, start, stop, steps ) {
      var diff = stop-start;
      var stepwidth = diff / ( steps + 1 );
      var str =[];
      var color3 = [];
      var pos;
      for ( var i = 1; i <= steps; i++ ) {
        pos = i * ( 1 / ( steps + 1 ) );
        color3[ 0 ] = this.transitionColorPart( color1[ 0 ], color2[ 0 ], pos);
        color3[ 1 ] = this.transitionColorPart( color1[ 1 ], color2[ 1 ], pos);
        color3[ 2 ] = this.transitionColorPart( color1[ 2 ], color2[ 2 ], pos);
        str.push(   Math.round( ( ( start + ( i * stepwidth ) ) * 100 ) )
                  + "% RGB(" + color3.join()
                  + ")" );
      }
      return str.join(" ,");
    },

    transitionColorPart : function( color1, color2, pos ) {
      var part = parseInt( color1 ) + ( ( color2 - color1 ) * pos );
      return Math.round( part );
    }

  }
});
