
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.FontSizeCalculation", {
  
  statics : {
    MEASUREMENT_LABEL : new qx.ui.basic.Label(),
    
    probe : function( probeList ) {
      for( i = 0; i < probeList.length; i++ ) {
        var item = probeList[ i ];
        var size = org.eclipse.swt.FontSizeCalculation._doMeasurement( item );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        org.eclipse.swt.FontSizeCalculation._addRequestParam( id, param );
      }
    },
    
    measureStrings : function( stringList ) {      
      for( i = 0; i < stringList.length; i++ ) {
        var item = stringList[ i ];
        var size = org.eclipse.swt.FontSizeCalculation._doMeasurement( item );
        var param = size[ 0 ] + "," + size[ 1 ];
        var id = item[ 0 ];
        org.eclipse.swt.FontSizeCalculation._addRequestParam( id, param );
        org.eclipse.swt.Request.getInstance().send();
      }
    },
  
    _doMeasurement : function( item ) {
      var lbl = org.eclipse.swt.FontSizeCalculation.MEASUREMENT_LABEL;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      if( item[ 6 ] > 0 ) {
        qx.ui.basic.Label._getMeasureNode().style.width = item[ 6 ];
      } else {
        qx.ui.basic.Label._getMeasureNode().style.width = "auto";
      }
      lbl.setText( item[ 1 ] );
      wm.setFont( lbl, item[ 2 ], item[ 3 ], item[ 4 ], item[ 5 ] );
      var result =  [ lbl._computePreferredInnerWidth(), 
                      lbl._computePreferredInnerHeight() ];
      qx.ui.basic.Label._getMeasureNode().style.width = "auto";
      return result;
    },
    
    _addRequestParam : function ( name, value ) {
      var request = org.eclipse.swt.Request.getInstance();
      request.addParameter( name, value );
    }
  }
} );