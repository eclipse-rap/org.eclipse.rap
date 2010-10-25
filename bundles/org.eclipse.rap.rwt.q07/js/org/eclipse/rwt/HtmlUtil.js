/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipsevent.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/


/**
 * This class contains static functions for HTML and CSS handling.
 * It generally accepts widgets or html-elements as targets.
 * In IE, vml-elements should work aswell.
 */
qx.Class.define( "org.eclipse.rwt.HtmlUtil", {

  statics : {

    setOpacity  : qx.core.Variant.select("qx.client", {
      "mshtml" : function( target, value ) {
        if( value == null || value >= 1 || value < 0 ) {
          this._removeCssFilter( target );
        } else {
          var valueStr = "Alpha(opacity=" + Math.round( value * 100 ) + ")";
          this.setStyleProperty( target, "filter", valueStr );
        }
      },
      "default" : function( target, value) {
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
    
    _removeCssFilter : function( target ) {
      var element = null;
      if( target instanceof qx.ui.core.Widget ) {        if( target.isCreated() ) {
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
