/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.ExpandBar", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );  
    this.setAppearance( "expand-bar" );
    this.setHideFocus( true );
        
    this._vscroll = qx.lang.String.contains( style, "v_scroll" );
    
    // This object is needed for reset the scrollbar position
    this._zeroScrolling = new qx.ui.layout.CanvasLayout(); 
    this.add( this._zeroScrolling );
    // This object is needed for proper scrolling behaviour
    this._bottomSpacing = new qx.ui.layout.CanvasLayout();
    this.add( this._bottomSpacing );
  },

  destruct : function() {
    this._disposeObjects( "_zeroScrolling", "_bottomSpacing" );   
  },  

  members : {
    setBottomSpacingBounds : function( x, y, width, height ) {
    	this._bottomSpacing.setLeft( x );
    	this._bottomSpacing.setTop( y );
    	this._bottomSpacing.setWidth( width );
    	this._bottomSpacing.setHeight( height );
    },
    
    showVScrollbar : function( show ) {
    	if( show ) {
	      this.setOverflow( qx.constant.Style.OVERFLOW_VERTICAL );
	    } else {
	    	this._zeroScrolling.scrollIntoView();   	
	      this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
	    }  
    }   
  }
} );
