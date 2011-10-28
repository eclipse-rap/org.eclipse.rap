/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.Text", {

  extend : qx.ui.form.TextField,
  
  construct : function( isTextarea ) {
    this.base( arguments );
    if( isTextarea ) {
      // NOTE: This essentially turns TextField into qx.ui.form.TextArea
      this._inputTag = "textarea";
      this._inputType = null;
      this._inputOverflow = "auto";
      this.setAppearance( "text-area" );
      this.setAllowStretchY( true );
      this.__oninput = qx.lang.Function.bindEvent( this._oninputDomTextarea, this );
    }
    this._hasSelectionListener = false;
    this._hasModifyListener = false;
    this._hasVerifyListener = false;
  },

  properties : {

    wrap : {
      check : "Boolean",
      init : true,
      apply : "_applyWrap"
    }

  },

  members : {
    
    ///////////////////
    // textarea support
    
    _applyElement : function( value, oldValue ) {
      this.base( arguments, value, oldValue );
      if( this._inputTag == "textarea" ) {
        this._styleWrap();
      }
      // Fix for bug 306354
      this._inputElement.style.paddingRight = "1px";
    },

    _webkitMultilineFix : function() {
      if( this._inputTag !== "textarea" ) {
        this.base( arguments );
      }
    },

    _applyWrap : function( value, oldValue ) {
      if( this._inputTag == "textarea" ) {
        this._styleWrap();
      }
    },

    _styleWrap : qx.core.Variant.select( "qx.client", {
      "mshtml" : function() {
        if( this._inputElement ) {
          this._inputElement.wrap = this.getWrap() ? "soft" : "off";
        }
      },
      "gecko" : function() {
        if( this._inputElement ) {
          var wrapValue = this.getWrap() ? "soft" : "off";
          var styleValue = this.getWrap() ? "" : "auto";
          this._inputElement.setAttribute( 'wrap', wrapValue );
          this._inputElement.style.overflow = styleValue;
        }
      },
      "default" : function() {
        if( this._inputElement ) {
          var wrapValue = this.getWrap() ? "soft" : "off";
          this._inputElement.setAttribute( 'wrap', wrapValue );
        }
      }
    } ),

    _applyMaxLength : function( value, oldValue ) {
      if( this._inputTag != "textarea" ) {
        this.base( arguments, value, oldValue );
      }
    },

    _oninputDomTextarea : function( event ) {
      var maxLength = this.getMaxLength();
      var fireEvents = true;
      if( maxLength != null ) {
        var value = this._inputElement.value;
        if( value.length > this.getMaxLength() ) {
          //NOTE [tb] : Works because LiveUpdate is true, set by TextUtil
          var oldValue = this.getValue();
          // NOTE [tb] : When pasting strings, this might not always 
          //             behave like SWT. There is no reliable fix for that.
          var position = this.getSelectionStart();
          if( oldValue.length == ( value.length - 1 ) ) {
            // The user added one character, undo.
            this._inputElement.value = oldValue;
            this.setSelectionStart( position - 1 );
            this.setSelectionLength( 0 );
          } else if( value.length >= oldValue.length && value != oldValue) {
            // The user pasted a string, shorten:
            this._inputElement.value = value.slice( 0, this.getMaxLength() );            
            this.setSelectionStart( Math.min( position, this.getMaxLength() ) );
            this.setSelectionLength( 0 );
          }
          if( this._inputElement.value == oldValue ) {
            fireEvents = false;
          }
        }
      } 
      if( fireEvents ) {
        this._oninputDom( event );
      }
    },
    
    ///////////////////
    // password support
    
    setPasswordMode : function( value ) {
      var type = value ? "password" : "text";
      if( this._inputTag != "textarea" && this._inputType != type ) {
        this._inputType = type;
        if( this._isCreated ) {
          if( org.eclipse.rwt.Client.getEngine() == "mshtml" ) {
            this._reCreateInputField();
          } else {
            this._inputElement.type = this._inputType;
          }        
        }
      }
    },
    
    _reCreateInputField : function() {
      var selectionStart = this.getSelectionStart();
      var selectionLength = this.getSelectionLength();
      this._inputElement.parentNode.removeChild( this._inputElement );
      this._inputElement.onpropertychange = null;
      this._inputElement = null;
      this._firstInputFixApplied = false;
      this._applyElement( this.getElement(), null );
      this._afterAppear();
      org.eclipse.swt.TextUtil._updateLineHeight( this );
      this._postApply();
      this._applyFocused( this.getFocused() );
      this.setSelectionStart( selectionStart );
      this.setSelectionLength( selectionLength );
    },

    ///////////////////
    // events listeners

    setHasSelectionListener : function( value ) {
      if( !this.hasState( "rwt_MULTI" ) ) {
        this._hasSelectionListener = value;
      }
    },

    hasSelectionListener : function() {
      // Emulate SWT (on Windows) where a default button takes precedence over
      // a SelectionListener on a text field when both are on the same shell.
      var shell = org.eclipse.rwt.protocol.AdapterUtil.getShell( this );
      var defButton = shell.getDefaultButton();
      // TODO [rst] On GTK, the SelectionListener is also off when the default
      //      button is invisible or disabled. Check with Windows and repair.
      var hasDefaultButton = defButton != null && defButton.isSeeable();
      return !hasDefaultButton && this._hasSelectionListener;
    },

    setHasModifyListener : function( value ) {
      this._hasModifyListener = value;
    },

    hasModifyListener : function() {
      return this._hasModifyListener;
    },

    setHasVerifyListener : function( value ) {
      this._hasVerifyListener = value;
    },

    hasVerifyListener : function() {
      return this._hasVerifyListener;
    }

  }

} );