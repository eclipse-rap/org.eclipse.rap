/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.Button", {
  extend : org.eclipse.rwt.widgets.BasicButton,

  construct : function( buttonType ) {
    this.base( arguments, buttonType );
    switch( buttonType ) {
     case "push" :
     case "toggle":
      this.setAppearance( "push-button" );
     break;
     case "check":
      this.setAppearance( "check-box" );
     break;
     case "radio":
      this.setAppearance( "radio-button" );
    }
    this.initTabIndex();
    this.addEventListener( "focus", this._onFocus );
    this.addEventListener( "blur", this._onBlur );    
  },
  
  properties : {

    tabIndex : {
      refine : true,
      init : 1
    }
        
  },
  
  members : {

    setWrap : function( value ) {
      if( value ) {
        this.setFlexibleCell( 2 );
      }
    },

    //overwritten:
    _afterRenderLayout : function( changes ) {
      if( this.hasState( "focused" ) ) {
         this._showFocusIndicator();
      }
    },
    
    _showFocusIndicator : function() {
      var focusIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
      var node =   this.getCellNode( 2 ) != null 
                 ? this.getCellNode( 2 )
                 : this.getCellNode( 1 );
      focusIndicator.show( this, "Button-FocusIndicator", node );      
    },
    
    _onFocus : function( event ) {
      this._showFocusIndicator();
    },
    
    _onBlur : function( event ) {
      var focusIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
      focusIndicator.hide( this );
    }
    
  }
} );