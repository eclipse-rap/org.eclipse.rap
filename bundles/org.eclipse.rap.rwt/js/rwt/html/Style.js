/*******************************************************************************
 * Copyright: 2004, 2014 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 *
 *   This class contains code based on the following work:
 *
 *   * Prototype JS
 *     http://www.prototypejs.org/
 *     Version 1.5
 *
 *     Copyright:
 *       (c) 2006-2007, Prototype Core Team
 *
 *     License:
 *       MIT: http://www.opensource.org/licenses/mit-license.php
 *
 *     Authors:
 *       * Prototype Core Team
 *
 ******************************************************************************/

(function() {

var Client = rwt.client.Client;

/**
 * Style querying and modification of HTML elements.
 *
 * Automatically normalizes cross-browser differences. Optimized for
 * performance.
 */
rwt.qx.Class.define( "rwt.html.Style", {

  statics : {

    /** Internal map of style property convertions */
    __hints : {
      // Style property name correction
      names : {
        "float" : Client.isMshtml() ? "styleFloat" : "cssFloat",
        "boxSizing" : Client.isGecko() ? "mozBoxSizing" : "boxSizing"
      },

      // Mshtml has propertiery pixel* properties for locations and dimensions
      // which return the pixel value. Used by getComputed() in mshtml variant.
      mshtmlPixel : {
        width : "pixelWidth",
        height : "pixelHeight",
        left : "pixelLeft",
        right : "pixelRight",
        top : "pixelTop",
        bottom : "pixelBottom"
      }

    },

    BROWSER_PREFIX : rwt.util.Variant.select( "qx.client", {
      "gecko" : "-moz-",
      "webkit" : "-webkit-",
      "default" : ""
    } ),

    /*
    ---------------------------------------------------------------------------
      STYLE ATTRIBUTE SUPPORT
    ---------------------------------------------------------------------------
    */

    /** {Integer} Computed value of a style property. Compared to the cascaded style,
     * this one also interprets the values e.g. translates <code>em</code> units to
     * <code>px</code>.
     */
    COMPUTED_MODE : 1,


    /** {Integer} Cascaded value of a style property. */
    CASCADED_MODE : 2,


    /** {Integer} Local value of a style property. Ignores inheritance cascade. Does not interpret values. */
    LOCAL_MODE : 3,

    /**
     * Gets the value of a style property.
     *
     * *Computed*
     *
     * Returns the computed value of a style property. Compared to the cascaded style,
     * this one also interprets the values e.g. translates <code>em</code> units to
     * <code>px</code>.
     *
     * *Cascaded*
     *
     * Returns the cascaded value of a style property.
     *
     * *Local*
     *
     * Ignores inheritance cascade. Does not interpret values.
     *
     * @type static
     * @signature function(element, name, mode, smart)
     * @param element {Element} The DOM element to modify
     * @param name {String} Name of the style attribute (js variant e.g. marginTop, wordSpacing)
     * @param mode {Number} Choose one of the modes {@link #COMPUTED_MODE}, {@link #CASCADED_MODE},
     *   {@link #LOCAL_MODE}. The computed mode is the default one.
     * @param smart {Boolean?true} Whether the implementation should automatically use
     *    special implementations for some properties
     * @return {var} The value of the property
     */
    get : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(element, name, mode, smart)
      {
        var hints = this.__hints;

        // normalize name
        name = hints.names[name] || name;

        // switch to right mode
        switch(mode)
        {
          case this.LOCAL_MODE:
            return element.style[name] || "";

          case this.CASCADED_MODE:
            return element.currentStyle[name];

          default:
            // Read cascaded style
            var currentStyle = element.currentStyle[name];

            // Pixel values are always OK
            if (/^-?[\.\d]+(px)?$/i.test(currentStyle)) {
              return currentStyle;
            }

            // Try to convert non-pixel values
            var pixel = hints.mshtmlPixel[name];
            if (pixel)
            {
              // Backup local and runtime style
              var localStyle = element.style[name];

              // Overwrite local value with cascaded value
              // This is needed to have the pixel value setupped
              element.style[name] = currentStyle || 0;

              // Read pixel value and add "px"
              var value = element.style[pixel] + "px";

              // Recover old local value
              element.style[name] = localStyle;

              // Return value
              return value;
            }

            // Non-Pixel values may be problematic
            if (/^-?[\.\d]+(em|pt|%)?$/i.test(currentStyle)) {
              throw new Error("Untranslated computed property value: " + name + ". Only pixel values work well across different clients.");
            }

            // Just the current style
            return currentStyle;
        }
      },

      "default" : function(element, name, mode, smart)
      {
        var hints = this.__hints;

        // normalize name
        name = hints.names[name] || name;

        // switch to right mode
        switch(mode)
        {
          case this.LOCAL_MODE:
            return element.style[name];

          case this.CASCADED_MODE:
            // Currently only supported by Opera and Internet Explorer
            if (element.currentStyle) {
              return element.currentStyle[name];
            }

            throw new Error("Cascaded styles are not supported in this browser!");

          // Support for the DOM2 getComputedStyle method
          //
          // Safari >= 3 & Gecko > 1.4 expose all properties to the returned
          // CSSStyleDeclaration object. In older browsers the function
          // "getPropertyValue" is needed to access the values.
          //
          // On a computed style object all properties are read-only which is
          // identical to the behavior of MSHTML's "currentStyle".
          default:
            // Opera, Mozilla and Safari 3+ also have a global getComputedStyle which is identical
            // to the one found under document.defaultView.

            // The problem with this is however that this does not work correctly
            // when working with frames and access an element of another frame.
            // Then we must use the <code>getComputedStyle</code> of the document
            // where the element is defined.
            var doc = rwt.html.Nodes.getDocument(element);
            var computed = doc.defaultView.getComputedStyle(element, null);

            // All relevant browsers expose the configured style properties to
            // the CSSStyleDeclaration objects
            return computed ? computed[name] : null;
        }
      }
    }),


    /**
     * Gets the computed (CSS) style property of a given DOM element.
     */
    getStyleProperty : (document.defaultView && document.defaultView.getComputedStyle) ?
      // has computedStyle
      function( el, prop ) {
        try {
          return el.ownerDocument.defaultView.getComputedStyle(el, "")[prop];
        } catch(ex) {
          throw new Error("Could not evaluate computed style: " + el + "[" + prop + "]: " + ex);
        }
      } :
      // no computedStyle
      rwt.util.Variant.select( "qx.client", {
        "mshtml" : function( el, prop ) {
          try {
            return el.currentStyle[prop];
          } catch( ex ) {
            throw new Error( "Could not evaluate computed style: " + el + "[" + prop + "]: " + ex );
          }
        },
        "default" : function( el, prop ) {
          try {
            return el.style[prop];
          } catch( ex ) {
            throw new Error( "Could not evaluate computed style: " + el + "[" + prop + "]" );
          }
        }
      }),


    /**
     * Get a (CSS) style property of a given DOM element and interpret the property as integer value
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @param propertyName {String} the name of the style property. e.g. "paddingTop", "marginLeft", ...
     * @return {Integer} the (CSS) style property converted to an integer value
     */
    getStyleSize : function(vElement, propertyName) {
      return parseInt( rwt.html.Style.getStyleProperty( vElement, propertyName ), 10 ) || 0;
    },


    /**
     * Get the element's left margin.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's left margin size
     */
    getMarginLeft : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "marginLeft");
    },


    /**
     * Get the element's top margin.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's top margin size
     */
    getMarginTop : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "marginTop");
    },


    /**
     * Get the element's right margin.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's right margin size
     */
    getMarginRight : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "marginRight");
    },


    /**
     * Get the element's bottom margin.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's bottom margin size
     */
    getMarginBottom : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "marginBottom");
    },


    /**
     * Get the element's left padding.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's left padding size
     */
    getPaddingLeft : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "paddingLeft");
    },


    /**
     * Get the element's top padding.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's top padding size
     */
    getPaddingTop : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "paddingTop");
    },


    /**
     * Get the element's right padding.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's right padding size
     */
    getPaddingRight : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "paddingRight");
    },


    /**
     * Get the element's bottom padding.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's bottom padding size
     */
    getPaddingBottom : function(vElement) {
      return rwt.html.Style.getStyleSize(vElement, "paddingBottom");
    },


    /**
     * Get the element's left border width.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's left border width
     */
    getBorderLeft : function(vElement) {
      return rwt.html.Style.getStyleProperty(vElement, "borderLeftStyle") == "none" ? 0 : rwt.html.Style.getStyleSize(vElement, "borderLeftWidth");
    },


    /**
     * Get the element's top border width.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's top border width
     */
    getBorderTop : function(vElement) {
      return rwt.html.Style.getStyleProperty(vElement, "borderTopStyle") == "none" ? 0 : rwt.html.Style.getStyleSize(vElement, "borderTopWidth");
    },


    /**
     * Get the element's right border width.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's right border width
     */
    getBorderRight : function(vElement) {
      return rwt.html.Style.getStyleProperty(vElement, "borderRightStyle") == "none" ? 0 : rwt.html.Style.getStyleSize(vElement, "borderRightWidth");
    },

    /**
     * Get the element's bottom border width.
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @return {Integer} the element's bottom border width
     */
    getBorderBottom : function(vElement) {
      return rwt.html.Style.getStyleProperty(vElement, "borderBottomStyle") == "none" ? 0 : rwt.html.Style.getStyleSize(vElement, "borderBottomWidth");
    },

    /**
     * Sets the given gradient as a background for the target element/widget.
     * The syntax is [ [ position, color ]* ], with position <= 1 and >= 0.
     * Color is any valid css string for colors.
     * The position has to increase from every previous position.
     * The gradient flows from top to bottom unless a "horizontal" flag is added as
     * a field to the gradient object, in which case it flows from left to right.
     *
     * If a background color is set, the gradient is rendered on top of it.
     * If a background image is set, the gradient is not rendered until it is removed.
     * If the browser does not support CSS3, the gradient is never rendered.
     */
    setBackgroundGradient : function( target, gradient ) {
      // Tests for identity, not equality, which is okay since this is just an optimization
      if( target.___rwtStyle__backgroundGradient !== gradient ) {
        target.___rwtStyle__backgroundGradient = gradient;
        if( !target.___rwtStyle__backgroundImage ) {
          this._updateBackground( target );
        }
      }
    },

    /**
     * Sets the given image url as a background for the target element/widget.
     * If a background color is set, the image is rendered on top of it.
     * If a background gradient is set, only the image is rendered.
     * For background repeat/position to be respected, they have to be set by
     * setBackgroundPosition/Repeat, never directly.
     */
    setBackgroundImage : function( target, image ) {
      if( target.___rwtStyle__backgroundImage !== image ) {
        target.___rwtStyle__backgroundImage = image;
        this._updateBackground( target );
      }
    },


    setBackgroundRepeat : function( target, repeat ) {
      if( target.___rwtStyle__backgroundRepeat !== repeat ) {
        target.___rwtStyle__backgroundRepeat = repeat;
        if( target.___rwtStyle__backgroundImage ) {
          this._updateBackground( target );
        }
      }
    },

    setBackgroundPosition : function( target, position ) {
      if( target.___rwtStyle__backgroundPosition !== position ) {
        target.___rwtStyle__backgroundPosition = position;
        if( target.___rwtStyle__backgroundImage ) {
          this._updateBackground( target );
        }
      }
    },

    setBackgroundSize : function( target, size ) {
      if( target.___rwtStyle__backgroundSize !== size ) {
        target.___rwtStyle__backgroundSize = size;
        if( target.___rwtStyle__backgroundImage ) {
          this._updateBackground( target );
        }
      }
    },

    /**
     * Sets the given color as a background for the target element/widget.
     * The color is rendered in any case, but always below gradient and image.
     */
    setBackgroundColor : function( target, color ) {
      var value = color === "transparent" ? null : color;
      if( target.___rwtStyle__backgroundColor !== value ) {
        target.___rwtStyle__backgroundColor = value;
        this._updateBackground( target );
      }
    },

    /**
     * Returns the color that was previously set by setBackgroundColor
     */
    getBackgroundColor : function( target ) {
      return target.___rwtStyle__backgroundColor || "transparent";
    },

    setOpacity  : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( target, value ) {
        if( value == null || value >= 1 || value < 0 ) {
          this.removeCssFilter( target );
        } else {
          var valueStr = "Alpha(opacity=" + Math.round( value * 100 ) + ")";
          this.setStyleProperty( target, "filter", valueStr );
        }
      },
      "gecko" : function( target, value ) {
        if( value == null || value >= 1 ) {
          this.removeStyleProperty( target, "MozOpacity" );
          this.removeStyleProperty( target, "opacity" );
        } else {
          var targetValue = rwt.util.Numbers.limit( value, 0, 1 );
          this.setStyleProperty( target, "MozOpacity", targetValue );
          this.setStyleProperty( target, "opacity", targetValue );
        }
      },
      "default" : function( target, value ) {
        if( value == null || value >= 1 ) {
          this.removeStyleProperty( target, "opacity" );
        } else {
          var targetValue = rwt.util.Numbers.limit( value, 0, 1 );
          this.setStyleProperty( target, "opacity", targetValue );
        }
      }
    } ),

    setBoxShadow: function( target, shadowObject ) {
      var property;
      if( Client.isWebkit() && !Client.isMobileChrome() ) {
        property = this.BROWSER_PREFIX + "box-shadow";
      } else {
        property = "boxShadow";
      }
      if( shadowObject ) {
        // NOTE: older webkit dont accept spread, therefor only use parameters 1-3
        var string = shadowObject[ 0 ] ? "inset " : "";
        string += shadowObject.slice( 1, 4 ).join( "px " ) + "px";
        var rgba = rwt.util.Colors.stringToRgb( shadowObject[ 5 ] );
        rgba.push( shadowObject[ 6 ] );
        string += " rgba(" + rgba.join() + ")";
        this.setStyleProperty( target, property, string );
      } else {
        this.removeStyleProperty( target, property );
      }
    },

    setTextShadow  : rwt.util.Variant.select( "qx.client", {
      "default" : function( target, shadowObject ) {
        var property = "textShadow";
        if( shadowObject ) {
          var string = shadowObject.slice( 1, 4 ).join( "px " ) + "px";
          var rgba = rwt.util.Colors.stringToRgb( shadowObject[ 5 ] );
          rgba.push( shadowObject[ 6 ] );
          string += " rgba(" + rgba.join() + ")";
          this.setStyleProperty( target, property, string );
        } else {
          this.removeStyleProperty( target, property );
        }
      },
      "mshtml" : function() {}
    } ),

    setPointerEvents : function( target, value ) {
      var version = Client.getVersion();
      var ffSupport = Client.getEngine() === "gecko" && version >= 1.9;
      // NOTE: chrome does not support pointerEvents, but not on svg-nodes
      var webKitSupport = Client.getBrowser() === "safari" && version >= 530;
      if( ffSupport || webKitSupport ) {
        this.setStyleProperty( target, "pointerEvents", value );
        target.setAttribute( "pointerEvents", value );
      } else {
        this._passEventsThrough( target, value );
      }
    },

    setStyleProperty : function( target, property, value ) {
      if( target.setStyleProperty ) {
        target.setStyleProperty( property, value );
      } else {
        target.style[ property ] = value;
      }
    },

    removeStyleProperty : function( target, property ) {
      if( target instanceof rwt.widgets.base.Widget ) {
        target.removeStyleProperty( property );
      } else {
        target.style[ property ] = "";
      }
    },

    removeCssFilter : function( target ) {
      var element = null;
      if( target instanceof rwt.widgets.base.Widget ) {
        if( target.isCreated() ) {
          element = target.getElement();
        } else {
          target.removeStyleProperty( "filter" );
        }
      } else {
        element = target;
      }
      if( element !== null ) {
        var cssText = element.style.cssText;
        cssText = cssText.replace( /FILTER:[^;]*(;|$)/, "" );
        element.style.cssText = cssText;
      }
    },

    //////////
    // Private

    _updateBackground : function( target ) {
      var background = [];
      if( Client.isMshtml() ) {
        this._pushMshtmlBackground( target, background );
      } else {
        this._pushBackgroundImage( target, background );
        this._pushBackgroundGradient( target, background );
        this._pushBackgroundColor( target, background );
      }
      if( background.length > 0 ) {
        this.setStyleProperty( target, "background", background.join( ", " ) );
        // Set background size as separate backgroundSize property for Firefox compatibility
        // http://stackoverflow.com/questions/7864448/background-size-in-shorthand-background-property-css3
        if( target.___rwtStyle__backgroundImage && target.___rwtStyle__backgroundSize ) {
          this.setStyleProperty( target, "backgroundSize", target.___rwtStyle__backgroundSize );
        }
      } else {
        this._clearCssBackground( target );
      }
    },

    _pushBackgroundImage : function( target, backgroundArray ) {
      var value = target.___rwtStyle__backgroundImage;
      if( value ) {
        var repeat = target.___rwtStyle__backgroundRepeat;
        var position = target.___rwtStyle__backgroundPosition;
        var size = target.___rwtStyle__backgroundSize;
        backgroundArray.push( this._getImageString( value, repeat, position ) );
      }
    },

    _pushBackgroundGradient : function( target, backgroundArray ) {
      var value = target.___rwtStyle__backgroundGradient;
      if( value && !target.___rwtStyle__backgroundImage ) {
        backgroundArray.push( this._getGradientString( value ) );
      }
    },

    _pushBackgroundColor : function( target, backgroundArray ) {
      var value = target.___rwtStyle__backgroundColor;
      if( value ) {
        if( Client.isWebkit() && !target.___rwtStyle__backgroundGradient ) {
          backgroundArray.push( this._getGradientString( [ [ 0, value ], [ 1, value ] ] ) );
        }
        backgroundArray.push( value );
      }
    },

    _pushMshtmlBackground : function( target, backgroundArray ) {
      var color = target.___rwtStyle__backgroundColor;
      var image = target.___rwtStyle__backgroundImage;
      var result = color ? color + " " : "";
      if( image ) {
        var repeat = target.___rwtStyle__backgroundRepeat;
        var position = target.___rwtStyle__backgroundPosition;
        var size = target.___rwtStyle__backgroundSize;
        result += this._getImageString( image, repeat, position );
      }
      backgroundArray.push( result );
    },

    _getGradientString : rwt.util.Variant.select( "qx.client", {
      // TODO [tb] : Webkit and Gecko now support the default syntax, but will continue to support
      //             their old syntax if prefexied. RAP should use new syntax if possible to be
      //             future proof.
      "webkit" : function( gradientObject ) {
        var args = [ "linear", "left top" ];
        if( gradientObject.horizontal === true ) {
          args.push( "right top" );
        }  else {
          args.push( "left bottom" );
        }
        for( var i = 0; i < gradientObject.length; i++ ) {
          var position = gradientObject[ i ][ 0 ];
          var color = gradientObject[ i ][ 1 ];
          args.push( "color-stop(" + position + "," + color + ")" );
        }
        return this.BROWSER_PREFIX + "gradient( " + args.join() + ")";
      },
      "gecko" : function( gradientObject ) {
        var args = [ gradientObject.horizontal === true ? "0deg" : "-90deg" ];
        for( var i = 0; i < gradientObject.length; i++ ) {
          var position = ( gradientObject[ i ][ 0 ] * 100 ) + "%";
          var color = gradientObject[ i ][ 1 ];
          args.push( color + " " + position );
        }
        return this.BROWSER_PREFIX + "linear-gradient( " + args.join() + ")";
      },
      "default" : function( gradientObject ) {
        var args = [ gradientObject.horizontal === true ? "90deg" : "180deg" ];
        for( var i = 0; i < gradientObject.length; i++ ) {
          var position = ( gradientObject[ i ][ 0 ] * 100 ) + "%";
          var color = gradientObject[ i ][ 1 ];
          args.push( color + " " + position );
        }
        return "linear-gradient( " + args.join() + ")";
      }
    } ),

    _getImageString : function( value, repeat, position ) {
      return   "url(" + this._resolveResource( value ) + ")"
             + ( repeat ? " " + repeat : "" )
             + ( position ? " " + position : "" );
    },

    _clearCssBackground : function( target ) {
      if( Client.isNewMshtml() ) {
        this.setStyleProperty( target, "background", "rgba(0, 0, 0, 0)" );
      } else {
        this.removeStyleProperty( target, "background" );
      }
    },

    _setCssFilterImage : function( target, value ) {
      if( value ) {
        var cssImageStr =   "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
                          + value
                          + "',sizingMethod='crop')";
        this.setStyleProperty( target, "filter", cssImageStr );
      } else {
        this.removeCssFilter( target );
      }
    },

    /////////
    // Helper

    _passEventsThrough : function( target, value ) {
      // TODO [tb] : This is a very limited implementation that allowes
      // to click "through" the elmement, but won't handle hover and cursor.
      var EventRegistration = rwt.html.EventRegistration;
      var types = rwt.event.EventHandler._mouseEventTypes;
      var handler = this._passEventThroughHandler;
      if( value === "none" ) {
        this.setStyleProperty( target, "cursor", "default" );
        for( var i = 0; i < types.length; i++ ) {
          EventRegistration.addEventListener( target, types[ i ], handler );
        }
      } else {
        // TODO
      }
    },

    _passEventThroughHandler : function() {
      var EventHandlerUtil = rwt.event.EventHandlerUtil;
      var domEvent = EventHandlerUtil.getDomEvent( arguments );
      var domTarget = EventHandlerUtil.getDomTarget( domEvent );
      var type = domEvent.type;
      domTarget.style.display = "none";
      var newTarget
        = document.elementFromPoint( domEvent.clientX, domEvent.clientY );
      domEvent.cancelBubble = true;
      EventHandlerUtil.stopDomEvent( domEvent );
      if(    newTarget
          && type !== "mousemove"
          && type !== "mouseover"
          && type !== "mouseout" )
      {
        if( type === "mousedown" ) {
          rwt.html.Style._refireEvent( newTarget, "mouseover", domEvent );
        }
        rwt.html.Style._refireEvent( newTarget, type, domEvent );
        if( type === "mouseup" ) {
          rwt.html.Style._refireEvent( newTarget, "mouseout", domEvent );
        }
      }
      domTarget.style.display = "";
    },

    _refireEvent : rwt.util.Variant.select("qx.client", {
      "mshtml" : function( target, type, originalEvent ) {
        var newEvent = document.createEventObject( originalEvent );
        target.fireEvent( "on" + type , newEvent );
      },
      "default" : function( target, type, originalEvent ) {
        var newEvent = document.createEvent( "MouseEvents" );
        newEvent.initMouseEvent( type,
                                 true, /* can bubble */
                                 true, /* cancelable */
                                 originalEvent.view,
                                 originalEvent.detail,
                                 originalEvent.screenX,
                                 originalEvent.screenY,
                                 originalEvent.clientX,
                                 originalEvent.clientY,
                                 originalEvent.ctrlKey,
                                 originalEvent.altKey,
                                 originalEvent.shiftKey,
                                 originalEvent.metaKey,
                                 originalEvent.button,
                                 originalEvent.relatedTarget);
        target.dispatchEvent( newEvent );
      }
    } ),

    _resolveResource : function( url ) {
      if( Client.isMshtml() && !this._isAbsolute( url ) ) {
        return Client.getBasePath() + url;
      }
      return url;
    },

    _isAbsolute : function( url ) {
      return url.slice( 0, 7 ) === "http://" || url.slice( 0, 8 ) === "https://";
    }

  }

} );

}() );
