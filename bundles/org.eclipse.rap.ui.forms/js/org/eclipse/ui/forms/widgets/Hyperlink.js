/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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
  extend : rwt.widgets.base.Atom,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "hyperlink" );
    // TODO [rh] workaround for weird getLabelObject behavior
    this.setLabel( "(empty)" );
    // End of workaround
    var labelObject = this.getLabelObject();
    labelObject.setAppearance( "hyperlink-label" );
    labelObject.setMode( "html" );
    labelObject.setWrap( rwt.util.Strings.contains( style, "wrap" ) );
    // TODO [rh] workaround for weird getLabelObject behavior
    this.setLabel( "" );
    // End of workaround
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

  destruct : function() {
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    setText : function( value ) {
      this._text = value;
      this._updateText();
    },

    setUnderlined : function( value ) {
      this._underlined = value;
      this._updateText();
    },

    _updateText : function() {
      var text = this._underlined ? "<u>" + this._text + "</u>" : this._text;
      this.setLabel( text );
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
