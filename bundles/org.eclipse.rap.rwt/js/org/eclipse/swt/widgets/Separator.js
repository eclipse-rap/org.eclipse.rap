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
 * This class represents RWT Labels with style RWT.SEPARATOR
 */
qx.Class.define( "org.eclipse.swt.widgets.Separator", {
  extend : qx.ui.layout.BoxLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "separator" );

    // Fix IE Styling issues
    this.setStyleProperty( "fontSize", "0" );
    this.setStyleProperty( "lineHeight", "0" );

    // the actual separator line
    this._line = new qx.ui.basic.Terminator();
    this._line.setAnonymous( true );
    this._line.setAppearance( "separator-line" );
    this.add( this._line );
    this.addEventListener( "changeOrientation", this.onChangeOrientation, this );
  },
  
  destruct : function() {
    if( this._line ) {
      this.removeEventListener( "changeOrientation", this.onChangeOrientation, this );
      this._line.dispose();
      this._line = null;
    }
  },
  
  members : {

    addLineStyle : function( style ) {
      this._line.addState( style );
    },
    
    removeLineStyle : function( style ) {
      this._line.removeState( style );
    },
    
    onChangeOrientation : function( event ) {
      if( event.getData() == "vertical" ) {
        this._line.addState( "vertical" );
      } else {
        this._line.removeState( "vertical" );
      }
    }
  }
});
