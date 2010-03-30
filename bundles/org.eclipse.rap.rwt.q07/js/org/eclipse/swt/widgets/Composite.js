/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.widgets.Composite", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "composite" );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this.setHideFocus( true ); 
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
      // Alternate fix for 299629. This might not always work if the composite 
      // is changed back and forth between rounded and normal border.   
      this._fixBackgroundTransparency();
      this.addEventListener( "changeBackgroundColor", 
                             this._fixBackgroundTransparency, 
                             this );
    }
  },
  
  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },
  
  members : {
    
    _onMouseOver : function( evt ) {
      this.addState( "over" );
    },
    
    _onMouseOut : function( evt ) {
      this.removeState( "over" );
    },
    
    _applyBackgroundImage : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( newValue, oldValue ) {
        this.base( arguments, newValue, oldValue );
        if( newValue == null ) {
          this._fixBackgroundTransparency();
        }
      },
      "default" : function( newValue, oldValue ) {
        this.base( arguments, newValue, oldValue );
      }
    } ),

    _fixBackgroundTransparency : function() {
      if(    this.getBackgroundColor() == null 
          && this.getBackgroundImage() == null ) 
      {
        this._applyBackgroundImage( "static/image/blank.gif", null );
      }
    }
    
  }
} );