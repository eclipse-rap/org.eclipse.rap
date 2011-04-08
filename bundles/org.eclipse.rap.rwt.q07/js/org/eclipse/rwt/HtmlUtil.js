/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/


/**
 * This class contains static functions for HTML and CSS handling.
 * It generally accepts widgets or html-elements as targets.
 * In IE, vml-elements should work aswell.
 */
qx.Class.define( "org.eclipse.rwt.HtmlUtil", {

  statics : {
    
    // TODO [tb] : Without IE6-support the browser-switch and opacity parameter could be removed  
    setBackgroundImage : ( function() {
      var result;
      // For IE6 without transparency we need to use CssFilter for PNG opacity to work:
      if( org.eclipse.rwt.Client.isMshtml() && org.eclipse.rwt.Client.getVersion() < 7 ) {
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
    } )(),

    setOpacity  : qx.core.Variant.select("qx.client", {
      "mshtml" : function( target, value ) {
        if( value == null || value >= 1 || value < 0 ) {
          this.removeCssFilter( target );
        } else {
          var valueStr = "Alpha(opacity=" + Math.round( value * 100 ) + ")";
          this.setStyleProperty( target, "filter", valueStr );
        }
      },
      "default" : function( target, value ) {
        if( value == null || value >= 1 ) {
          if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
            this.removeStyleProperty( target, "MozOpacity" );
          }
          this.removeStyleProperty( target, "opacity" );
        } else {
          var targetValue = qx.lang.Number.limit( value, 0, 1 );
          if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
            this.setStyleProperty( target, "MozOpacity", targetValue );
          }
          this.setStyleProperty( target, "opacity", targetValue );
        }
      }
    } ),
    
    setPointerEvents : function( target, value ) {
      var version = org.eclipse.rwt.Client.getVersion();
      var ffSupport 
        = org.eclipse.rwt.Client.getEngine() === "gecko" && version >= 1.9;
      // NOTE: chrome does not support pointerEvents, but not on svg-nodes
      var webKitSupport 
        = org.eclipse.rwt.Client.getBrowser() === "safari" && version >= 530;
      if( ffSupport || webKitSupport ) {
        this.setStyleProperty( target, "pointerEvents", value );
        target.setAttribute( "pointerEvents", value );
      } else {
        this._passEventsThrough( target, value );
      }
    },

    setStyleProperty : function( target, property, value ) {
      if( target instanceof qx.ui.core.Widget ) {
        target.setStyleProperty( property, value );          
      } else {
        target.style[ property ] = value;          
      }
    },

    removeStyleProperty : function( target, property ) {
      if( target instanceof qx.ui.core.Widget ) {
        target.removeStyleProperty( property );
      } else {
        target.style[ property ] = "";
      }
    },
    
    removeCssFilter : function( target ) {
      var element = null;
      if( target instanceof qx.ui.core.Widget ) {
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
      var util = qx.html.EventRegistration;
      var types = org.eclipse.rwt.EventHandler._mouseEventTypes;
      var handler = this._passEventThroughHandler;
      if( value === "none" ) {
        this.setStyleProperty( target, "cursor", "default" );
        for( var i = 0; i < types.length; i++ ) {
          util.addEventListener( target, types[ i ], handler );
        }
      } else {
        // TODO
      }
    },
    
    _passEventThroughHandler : function() {
      var util = org.eclipse.rwt.EventHandlerUtil;
      var domEvent = util.getDomEvent( arguments );
      var domTarget = util.getDomTarget( domEvent );
      var type = domEvent.type;
      domTarget.style.display = "none";
      var newTarget 
        = document.elementFromPoint( domEvent.clientX, domEvent.clientY );
      domEvent.cancelBubble = true;
      util.stopDomEvent( domEvent );
      if(    newTarget
          && type !== "mousemove" 
          && type !== "mouseover" 
          && type !== "mouseout" )  
      {
        if( type === "mousedown" ) {
          org.eclipse.rwt.HtmlUtil._refireEvent( newTarget, "mouseover", domEvent );
        } 
        org.eclipse.rwt.HtmlUtil._refireEvent( newTarget, type, domEvent );
        if( type === "mouseup" ) {
          org.eclipse.rwt.HtmlUtil._refireEvent( newTarget, "mouseout", domEvent );          
        }
      }
      domTarget.style.display = "";
    },
    
    _refireEvent : qx.core.Variant.select("qx.client", {
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
        console.log( "dispatch " + event.type );
        target.dispatchEvent( newEvent );
      }
    } )

  }

} );
