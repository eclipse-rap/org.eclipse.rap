/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.Button", {

  extend : rwt.widgets.base.BasicButton,

  construct : function( buttonType ) {
    this.base( arguments, buttonType );
    this._alignment = buttonType === "arrow" ? "up" : "center";
    switch( buttonType ) {
     case "arrow":
       this.addState( "rwt_UP" );
       this.setAppearance( "push-button" );
     break;
     case "push":
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
    this._rawText = null;
    this._mnemonicIndex = null;
  },

  destruct : function() {
    this.setMnemonicIndex( null );
  },

  properties : {

    tabIndex : {
      refine : true,
      init : 1
    }

  },

  members : {

    setText : function( value ) {
      this._rawText = value;
      this._mnemonicIndex = null;
      this._applyText( false );
    },

    setMnemonicIndex : function( value ) {
      this._mnemonicIndex = value;
      var mnemonicHandler = rwt.widgets.util.MnemonicHandler.getInstance();
      if( ( typeof value === "number" ) && ( value >= 0 ) ) {
        mnemonicHandler.add( this, this._onMnemonic );
      } else {
        mnemonicHandler.remove( this );
      }
    },

    getMnemonicIndex : function() {
      return this._mnemonicIndex;
    },

    setAlignment : function( value ) {
      if( this.hasState( "rwt_ARROW" ) ) {
        this.removeState( "rwt_" + this._alignment.toUpperCase() );
        this.addState( "rwt_" + value.toUpperCase() );
      } else {
        this.setHorizontalChildrenAlign( value );
      }
      this._alignment = value;
    },

    setWrap : function( value ) {
      if( value ) {
        this.setFlexibleCell( 2 );
      }
    },

    _onMnemonic : function( event ) {
      switch( event.type ) {
        case "show":
          this._applyText( true );
        break;
        case "hide":
          this._applyText( false );
        break;
        case "trigger":
          var charCode = this._rawText.toUpperCase().charCodeAt( this._mnemonicIndex );
          if( event.charCode === charCode ) {
            this.setFocused( true );
            this.execute();
            event.success = true;
          }
        break;
      }
    },

    _applyText : function( mnemonic ) {
      var EncodingUtil = rwt.util.Encoding;
      if( this._rawText ) {
        var mnemonicIndex = mnemonic ? this._mnemonicIndex : undefined;
        var text = EncodingUtil.escapeText( this._rawText, mnemonicIndex );
        if( this.hasState( "rwt_WRAP" ) ) {
          text = EncodingUtil.replaceNewLines( text, "<br/>" );
        }
        this.setCellContent( 2, text );
      } else {
        this.setCellContent( 2, null );
      }
    },

    //overwritten:
    _afterRenderLayout : function( changes ) {
      if( this.hasState( "focused" ) ) {
         this._showFocusIndicator();
      }
    },

    _showFocusIndicator : function() {
      var focusIndicator = rwt.widgets.util.FocusIndicator.getInstance();
      var node = this.getCellNode( 2 ) != null ? this.getCellNode( 2 ) : this.getCellNode( 1 );
      focusIndicator.show( this, "Button-FocusIndicator", node );
    },

    _onFocus : function( event ) {
      this._showFocusIndicator();
    },

    _onBlur : function( event ) {
      var focusIndicator = rwt.widgets.util.FocusIndicator.getInstance();
      focusIndicator.hide( this );
    }

  }
} );
