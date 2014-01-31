/*******************************************************************************
 * Copyright: 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
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
        "float" : rwt.client.Client.isMshtml() ? "styleFloat" : "cssFloat",
        "boxSizing" : rwt.client.Client.isGecko() ? "mozBoxSizing" : "boxSizing"
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
     * Get the computed (CSS) style property of a given DOM element
     *
     * @type static
     * @param vElement {Element} the DOM element
     * @param propertyName {String} the name of the style property. e.g. "color", "border", ...
     * @return {String} the (CSS) style property
     * @signature function(vElement, propertyName)
     */
    getStyleProperty : rwt.util.Objects.select((document.defaultView && document.defaultView.getComputedStyle) ? "hasComputed" : "noComputed",
    {
      "hasComputed" : function(el, prop)
      {
        try {
          return el.ownerDocument.defaultView.getComputedStyle(el, "")[prop];
        } catch(ex) {
          throw new Error("Could not evaluate computed style: " + el + "[" + prop + "]: " + ex);
        }
      },

      "noComputed" : rwt.util.Variant.select("qx.client",
      {
        "mshtml" : function(el, prop)
        {
          try {
            return el.currentStyle[prop];
          } catch(ex) {
            throw new Error("Could not evaluate computed style: " + el + "[" + prop + "]: " + ex);
          }
        },

        "default" : function(el, prop)
        {
          try {
            return el.style[prop];
          } catch(ex) {
            throw new Error("Could not evaluate computed style: " + el + "[" + prop + "]");
          }
        }
      })
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

    // TODO [tb] : Without IE6-support the browser-switch and opacity parameter can be removed
    setBackgroundImage : ( function() {
      var result;
      // For IE6 without transparency we need to use CssFilter for PNG opacity to work:
      if( rwt.client.Client.isMshtml() && rwt.client.Client.getVersion() < 7 ) {
        result = function( target, value, opacity ) {
          if( opacity != null && opacity < 1 ) {
            this.removeCssFilter( target );
            this._setCssBackgroundImage( target, value );
            this.setOpacity( target, opacity );
          } else {
            this._setCssBackgroundImage( target, null );
            // NOTE: This overwrites opacity for this node:
            this._setCssFilterImage( target, value );
          }
        };
      } else {
        result = function(  target, value, opacity ) {
          this._setCssBackgroundImage( target, value );
          if( opacity != null ) {
            this.setOpacity( target, opacity );
          }
        };
      }
      return result;
    }() ),

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

    setBackgroundGradient : rwt.util.Variant.select( "qx.client", {
      // TODO [tb] : Webkit and Gecko now support the default syntax, but will continue to support
      //             their old syntax if prefexied. RAP should use new syntax if possible to be
      //             future proof.
      "webkit" : function( target, gradientObject ) {
        if( gradientObject ) {
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
          var string = this.BROWSER_PREFIX + "gradient( " + args.join() + ")";
          this.setStyleProperty( target, "background", string );
        } else {
          this.removeStyleProperty( target, "background" );
        }
      },
      "gecko" : function( target, gradientObject ) {
        if( gradientObject ) {
          var args = [ gradientObject.horizontal === true ? "0deg" : "-90deg" ];
          for( var i = 0; i < gradientObject.length; i++ ) {
            var position = ( gradientObject[ i ][ 0 ] * 100 ) + "%";
            var color = gradientObject[ i ][ 1 ];
            args.push( color + " " + position );
          }
          var string = this.BROWSER_PREFIX + "linear-gradient( " + args.join() + ")";
          this.setStyleProperty( target, "background", string );
        } else {
          this.removeStyleProperty( target, "background" );
        }
      },
      "default" : function( target, gradientObject ) {
        if( gradientObject ) {
          var args = [ gradientObject.horizontal === true ? "90deg" : "180deg" ];
          for( var i = 0; i < gradientObject.length; i++ ) {
            var position = ( gradientObject[ i ][ 0 ] * 100 ) + "%";
            var color = gradientObject[ i ][ 1 ];
            args.push( color + " " + position );
          }
          var string = "linear-gradient( " + args.join() + ")";
          this.setStyleProperty( target, "background", string );
        } else {
          this.removeStyleProperty( target, "background" );
        }
      }
    } ),

    setBoxShadow: function( target, shadowObject ) {
      var property;
      if( rwt.client.Client.isWebkit() && !rwt.client.Client.isMobileChrome() ) {
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
      var version = rwt.client.Client.getVersion();
      var ffSupport = rwt.client.Client.getEngine() === "gecko" && version >= 1.9;
      // NOTE: chrome does not support pointerEvents, but not on svg-nodes
      var webKitSupport = rwt.client.Client.getBrowser() === "safari" && version >= 530;
      if( ffSupport || webKitSupport ) {
        this.setStyleProperty( target, "pointerEvents", value );
        target.setAttribute( "pointerEvents", value );
      } else {
        this._passEventsThrough( target, value );
      }
    },

    setStyleProperty : function( target, property, value ) {
      if( target instanceof rwt.widgets.base.Widget ) {
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

    _setCssBackgroundImage : function( target, value ) {
      var cssImageStr = value ? "URL(" + value + ")" : "none";
      this.setStyleProperty( target, "backgroundImage", cssImageStr );
      this.setStyleProperty( target, "backgroundRepeat", "no-repeat" );
      this.setStyleProperty( target, "backgroundPosition", "center" );
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
                                 true,  /* can bubble */
                                 true, /*cancelable */
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
    } )


  }
});
