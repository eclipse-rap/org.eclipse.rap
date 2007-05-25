
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

/**
 * This class extends qx.ui.groupbox.GroupBox to ease its usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.Group", {
  extend : qx.ui.groupbox.GroupBox,

  construct : function() {
    this.base( arguments );
    this._getLabelObject().setMode( "html" ); 
  },
  
  members : {
    setFont : function( value ) {
      this._getLabelObject().setFont( value );
    },

    _getLabelObject : function() {
      if ( this.getLegendObject().getLabelObject() == null ) {
        this.setLegend( "(empty)" );
        this.setLegend( "" );
      }
      return this.getLegendObject().getLabelObject();
    }
  }
});
