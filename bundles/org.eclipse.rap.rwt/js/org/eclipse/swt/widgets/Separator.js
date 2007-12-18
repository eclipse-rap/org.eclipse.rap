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

    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this );

    // the actual separator line
    this._line = new qx.ui.basic.Terminator();
    this._line.setAnonymous( true );
    this._line.setAppearance( "separator-line" );
    this.add( this._line );
  },
  
  properties : {

    appearance : {
      refine : true,
      init : "separator"
    },

    lineOrientation : {
      check : [ "horizontal", "vertical" ],
      apply : "_applyLineOrientation",
      init : "horizontal",
      nullable : true
    }
  },
  
  destruct : function() {
    this._disposeObjects( "_line" );
  },
  
  members : {

    addLineStyle : function( style ) {
      this._line.addState( style );
    },
    
    removeLineStyle : function( style ) {
      this._line.removeState( style );
    },
    
    _applyLineOrientation : function( value, old ) {
      if( value == "vertical" ) {
        this.setHorizontalChildrenAlign( "center" );
        this._line.setWidth( "auto" );
        this._line.setHeight( "100%" );
        this._line.addState( "rwt_VERTICAL" );
      } else {
        this.setVerticalChildrenAlign( "middle" );
        this._line.setWidth( "100%" );
        this._line.setHeight( "auto" );
        this._line.removeState( "rwt_VERTICAL" );
      }
    }
  }
});
