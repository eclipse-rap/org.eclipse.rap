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
 
qx.Class.define( "org.eclipse.swt.FontSizeCalculation", {
  
  statics : {
    MEASUREMENT_LABEL : new qx.ui.basic.Label(),
    
    probe : function( probeList ) {
      for( var i = 0; i < probeList.length; i++ ) {
        var item = probeList[ i ];
        var size = this._doMeasurement( item, false );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        this._addRequestParam( id, param );
      }
    },
    
    measureStrings : function( stringList ) {      
      for( var i = 0; i < stringList.length; i++ ) {
        var item = stringList[ i ];
        var size = this._doMeasurement( item, true );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        this._addRequestParam( id, param );
        org.eclipse.swt.Request.getInstance().send();
      }
    },
  
    _doMeasurement : function( item, escapeText ) {
      var lbl = this.MEASUREMENT_LABEL;
      var measureNode = qx.ui.basic.Label._getMeasureNode();
      // [if] Move the measure node to the left to prevent text split on
      // small browser window - see bug 298798
      measureNode.style.left = -1000;
      if( item[ 6 ] > 0 ) {
        measureNode.style.width = item[ 6 ];
      } else {
        measureNode.style.width = "auto";
      }
      var text = escapeText ? this._escapeText( item[ 1 ] ) : item[ 1 ];
      lbl.setText( text );
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.setFont( lbl, item[ 2 ], item[ 3 ], item[ 4 ], item[ 5 ] );
      var result =  [ lbl._computePreferredInnerWidth(), 
                      lbl._computePreferredInnerHeight() ];
      measureNode.style.width = "auto";
      return result;
    },
    
    _addRequestParam : function ( name, value ) {
      var request = org.eclipse.swt.Request.getInstance();
      request.addParameter( name, value );
    },

    _escapeText : function( text ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var result = encodingUtil.escapeText( text, true );
      result = encodingUtil.replaceNewLines( result, "<br/>" );
      result = encodingUtil.escapeLeadingTrailingSpaces( result );
      return result;
    }
  }
} );