/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.Label", {

  extend : rwt.widgets.base.MultiCellWidget,

  construct : function( styles ) {
    this.base( arguments, this._CELLORDER );
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "mouseout", this._onMouseOut );
    this.setVerticalChildrenAlign( "top" );
    this.setAlignment( "left" );
    this.setAppearance( "label-wrapper" );
    if( styles.WRAP ) {
      this.setFlexibleCell( 1 );
    }
    this._markupEnabled = styles.MARKUP_ENABLED === true;
  },

  members : {

    _CELLORDER : [ "image", "label" ],

    setAlignment : function( value ) {
      this.setHorizontalChildrenAlign( value );
    },

    setImage : function( image ) {
      if( image ) {
        this.setCellContent( 0, image[ 0 ] );
        this.setCellDimension( 0, image[ 1 ], image[ 2 ] );
      } else {
        this.setCellContent( 0, null );
        this.setCellDimension( 0, 0, 0 );
      }
    },

    setText : function( value ) {
      var text = value;
      if( !this._markupEnabled ) {
        var EncodingUtil = rwt.util.Encoding;
        // Order is important here: escapeText, replace line breaks
        text = EncodingUtil.escapeText( value, true );
        text = EncodingUtil.replaceNewLines( text, "<br/>" );
        text = EncodingUtil.replaceWhiteSpaces( text ); // fixes bug 192634
      }
      this.setCellContent( 1, text );
    },

    setTopMargin : function( value ) {
      this.setPaddingTop( value );
    },

    setLeftMargin : function( value ) {
      this.setPaddingLeft( value );
    },

    setRightMargin : function( value ) {
      this.setPaddingRight( value );
    },

    setBottomMargin : function( value ) {
      this.setPaddingBottom( value );
    },

    _onMouseOver : function( event ) {
      if( event.getTarget() === this && !this.hasState( "over" ) ) {
        this.addState( "over" );
      }
    },

    _onMouseOut : function( event ) {
      if( event.getTarget() === this ) {
        this.removeState( "over" );
      }
    }

  }
} );
