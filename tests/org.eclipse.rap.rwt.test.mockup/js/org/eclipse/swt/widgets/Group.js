/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class extends qx.ui.groupbox.GroupBox to ease its usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.Group", {
  extend : qx.ui.groupbox.GroupBox,

  construct : function() {
    this.base( arguments );
    this.rap_init();
  },

  members : {
    rap_init : function() {
      // Make sure that the 'labelObject' is created
      var labelObject = this.getLegendObject().getLabelObject(); 
      if ( labelObject == null ) {
        this.setLegend( "(empty)" );
        this.setLegend( "" );
      }
      labelObject = this.getLegendObject().getLabelObject(); 
      labelObject.setMode( qx.constant.Style.LABEL_MODE_HTML );
      this.addEventListener( "changeBackgroundColor",
                             this._onChangeBackgroundColor,
                             this );
    },
    
    rap_reset : function() {
      this.removeEventListener( "changeBackgroundColor",
                             this._onChangeBackgroundColor,
                             this );
    },

    _onChangeBackgroundColor : function( evt ) {
      var newColor = evt.getValue();
      this.getLegendObject().setBackgroundColor( newColor );
    }
  }
});
