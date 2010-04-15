/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.ToolBar", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this._hoverItem = null;
    this.setAppearance( "toolbar" );
    this.setOverflow( "hidden" );
    this.initTabIndex();
    this.addEventListener( "focus", this._onFocus );
    this.addEventListener( "blur", this._onBlur );
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "keypress", this._onKeyPress );
    this.addEventListener( "keydown", this._onKeyDown );
    this.addEventListener( "keyup", this._onKeyUp );
  },
  
  properties : {

    tabIndex : {
      refine : true,
      init : 1
    }
        
  },
  
  members : {
    
    _onMouseOver : function( event ) {
      var item = event.getTarget();
      if( item.getParent() == this && this._hoverItem != item ) {
        if( this._hoverItem ) {
          this._hoverItem.removeState( "over" );
        }
        if( item instanceof org.eclipse.rwt.widgets.ToolItem ) {
          this._hoverItem = item;
          this._hoverItem.addState( "over" );
        } else {
          this._hoverItem = null;
        }
      }      
    },
    
    _onFocus : function( event ) {
      if( this._hoverItem == null ) {
        this._hoverItem = this.getFirstChild();
      }
      if( this._hoverItem != null ) {
        this._hoverItem.addState( "over" );
      }
    },
    
    _onBlur : function( event ) {
      if( this._hoverItem != null ) {
        this._hoverItem.removeState( "over" );
      }
    },
    
    _onKeyPress : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Left":
          this._hoverNext( true );
        break;      
        case "Right":
          this._hoverNext( false );
        break;
      }
    },
    
    _onKeyDown : function( event ) {
      if( this._hoverItem != null ) {
        this._hoverItem._onKeyDown( event );
      }
    },
    
    _onKeyUp : function( event ) {
      if( this._hoverItem != null ) {
        this._hoverItem._onKeyUp( event );
      }      
    },
    
    _hoverNext : function( backwards ) {
      if( this._hoverItem != null ) {
        this._hoverItem.removeState( "over" );
        var isToolItem;
        do {
          if( backwards ) {
            this._hoverItem = this._hoverItem.getPreviousSibling();
            if( this._hoverItem == null ) {
              this._hoverItem = this.getLastChild();
            } 
          } else {
            this._hoverItem = this._hoverItem.getNextSibling();
            if( this._hoverItem == null ) {
              this._hoverItem = this.getFirstChild();
            }             
          }
          isToolItem 
            = this._hoverItem instanceof org.eclipse.rwt.widgets.ToolItem;
        } while( !( isToolItem && this._hoverItem.isEnabled() ) );
        this._hoverItem.addState( "over" );
      }
    }
    
  }
  
} );
