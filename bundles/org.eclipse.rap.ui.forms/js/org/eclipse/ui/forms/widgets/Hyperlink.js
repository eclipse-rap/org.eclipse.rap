/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.ui.forms.widgets.Hyperlink", {

  extend : rwt.widgets.base.MultiCellWidget,

  construct : function( style ) {
    this.base( arguments, [ "image", "label" ] );
    this.setAppearance( "hyperlink" );
    if( rwt.util.Strings.contains( style, "wrap" ) ) {
      this.setFlexibleCell( 1 );
      this.setWordWrap( true );
    }
    this._text = "";
    this._underlined = false;
    this._savedBackgroundColor = null;
    this._savedTextColor = null;
    this._activeBackgroundColor = null;
    this._activeTextColor = null;
    this._underlineMode = null;
    this._hover = false;
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    setText : function( value ) {
      this._text = rwt.util.Encoding.escapeText( value, false );
      this._updateText();
    },

    setImage : function( value ) {
      if( value === null ) {
        this.setCellContent( 0, null );
        this.setCellDimension( 0, 0, 0 );
      } else {
        this.setCellContent( 0, value[ 0 ] );
        this.setCellDimension( 0, value[ 1 ], value[ 2 ] );
      }
    },

    setUnderlined : function( value ) {
      this._underlined = value;
      this._updateText();
    },

    _updateText : function() {
      var text = this._underlined ? "<u>" + this._text + "</u>" : this._text;
      this.setCellContent( 1, text );
    },

    setActiveBackgroundColor : function( value ) {
      this._activeBackgroundColor = value;
    },

    setActiveTextColor : function( value ) {
      this._activeTextColor = value;
    },

    setUnderlineMode : function( value ) {
      this._underlineMode = value;
    },

    setHasDefaultSelectionListener : function( value ) {
      if( value ) {
        this.addEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      } else {
        this.removeEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      }
    },

    _onMouseMove : function( evt ) {
      if( !this._hover ) {
        this._savedBackgroundColor = this.getBackgroundColor();
        if( this._activeBackgroundColor != null ) {
          this.setBackgroundColor( this._activeBackgroundColor );
        }
        this._savedTextColor = this.getTextColor();
        if( this._activeTextColor != null ) {
          this.setTextColor( this._activeTextColor );
        }
        var mode = org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;
        if( this._underlineMode == mode ) {
          this.setStyleProperty( "textDecoration", "underline");
        }
        this._hover = true;
      }
    },

    _onMouseOut : function( evt ) {
      if( this._hover ) {
        this._hover = false;
        this.setBackgroundColor( this._savedBackgroundColor );
        this.setTextColor( this._savedTextColor );
        var mode = org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;
        if( this._underlineMode == mode ) {
          this.setStyleProperty( "textDecoration", "none");
        }
      }
    }
  }

} );
