/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.FontSizeCalculation", {
  
  statics : {

    MEASUREMENT_LABEL : new qx.ui.basic.Label(),
    
    probe : function( probeList ) {
      for( var i = 0; i < probeList.length; i++ ) {
        var item = probeList[ i ];
        var size = this._measureItem( item, false );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        this._addRequestParam( id, param );
      }
    },
    
    measureStringItems : function( items ) {      
      for( var i = 0; i < items.length; i++ ) {
        var item = items[ i ];
        var isMarkup = item[ 7 ];
        var size = this._measureItem( item, !isMarkup );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        this._addRequestParam( id, param );
        org.eclipse.swt.Request.getInstance().send();
      }
    },
  
    _measureItem : function( item, escapeText ) {
      var text = escapeText ? this._escapeText( item[ 1 ] ) : item[ 1 ];
      var fontProps = {
        "fontFamily" : item[ 2 ],
        "fontSize" : item[ 3 ] + "px",
        "fontWeight" : item[ 4 ] ? "bold" : "normal",
        "fontStyle" : item[ 5 ] ? "italic" : "normal"
      };
      var width = item[ 6 ] > 0 ? item[ 6 ] : null;
      return this.computeTextDimensions( text, fontProps, width );
    },
    
    computeTextDimensions : function( text, fontProps, wrapWidth ) {
      var element = this._getMeasureNode();
      var style = element.style;
      // Resetting style.font causes errors in IE with any of these syntaxes:
      // node.style.font = null | undefined | "inherit" | "";
      if( !org.eclipse.rwt.Client.isMshtml() ) {
        style.font = "";
      }
      style.fontFamily = fontProps.fontFamily || "";
      style.fontSize = fontProps.fontSize || "";
      style.fontWeight = fontProps.fontWeight || "";
      style.fontStyle = fontProps.fontStyle || "";
      element.innerHTML = text;
      if( wrapWidth ) {
        style.width = wrapWidth + "px";
      }
      var result = this._measureElement( element );
      style.width = "auto";
      return result;
    },

    _measureElement : qx.core.Variant.select( "qx.client", {
      "default" : function( element ) {
        var result;
        if( element.getBoundingClientRect ) {
          // See Bug 340841
          var bounds = element.getBoundingClientRect();
          // In FF 3.0.x getBoundingClientRect has no width/height properties
          if( bounds.width != null && bounds.height != null ) {
            result = [ Math.ceil( bounds.width ), Math.ceil( bounds.height ) ];
          } else {
            result = [ element.scrollWidth, element.scrollHeight ];
          }
        } else {
          result = [ element.scrollWidth, element.scrollHeight ];
        }
        return result;
      },
      "newmshtml" : function( element ) {
        var computed = window.getComputedStyle( element, null );
        var result = [
          Math.ceil( parseFloat( computed.width ) ), 
          Math.ceil( parseFloat( computed.height ) ) 
        ];
        return result;
      }
    } ),

    _getMeasureNode : function() {
      var node = this._measureNode;
      if( !node ) {
        node = document.createElement( "div" );
        var style = node.style;
        style.width = style.height = "auto";
        style.visibility = "hidden";
        style.position = "absolute";
        style.zIndex = "-1";
        // [if] Move the measure node to the left to prevent text split on
        // small browser window - see bug 298798
        style.left = -1000;
        document.body.appendChild( node );
        this._measureNode = node;
      }
      if( !org.eclipse.rwt.Client.isMshtml() ) {
        node.style.font = "";
      }
      return node;
    },

    _addRequestParam : function ( name, value ) {
      var request = org.eclipse.swt.Request.getInstance();
      request.addParameter( name, value );
    },

    _escapeText : function( text ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var result = EncodingUtil.escapeText( text, true );
      result = EncodingUtil.replaceNewLines( result, "<br/>" );
      result = EncodingUtil.escapeLeadingTrailingSpaces( result );
      return result;
    }

  }
} );