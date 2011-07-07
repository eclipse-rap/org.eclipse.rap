/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.ExpandBar", {
  extend : org.eclipse.swt.widgets.Scrollable,

  construct : function() {
    this.base( arguments, new qx.ui.layout.CanvasLayout() );
    this.setAppearance( "expand-bar" );
    this.setHideFocus( true );
    // This object is needed for proper scrolling behaviour
    this._bottomSpacing = new qx.ui.layout.CanvasLayout();
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._bottomSpacing );
    this._clientArea.add( this._bottomSpacing );
  },
  
  destruct : function() {
    this._disposeObjects( "_bottomSpacing" );
  },

  members : {

    addWidget : function( widget ) {
      this._clientArea.add( widget );
    },

    setBottomSpacingBounds : function( x, y, width, height ) {
      this._bottomSpacing.setLeft( x );
      this._bottomSpacing.setTop( y );
      this._bottomSpacing.setWidth( width );
      this._bottomSpacing.setHeight( height );
    },

    showVScrollbar : function( show ) {
      this.setScrollBarsVisible( false, show );
      if( !show ) {
        this.setVBarSelection( 0 );
      }
    },

    setVScrollbarMax : function( value ) {
      this._vertScrollBar.setMaximum( value );
    }

  }
} );
