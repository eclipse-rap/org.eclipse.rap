/*******************************************************************************
 *  Copyright: 2004-2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
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
            this._removeCssFilter( target );
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
          this._removeCssFilter( target );
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
        this._removeCssFilter( target );
      }
    },
    
    _removeCssFilter : function( target ) {
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
    }    

  }

} );
