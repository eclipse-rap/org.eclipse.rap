/*******************************************************************************
 * Copyright: 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.BasicText", {

  extend : rwt.widgets.base.Terminator,

  construct : function( value ) {
    this.base( arguments );
    if( value != null ) {
      this.setValue( value );
    }
    this.initHideFocus();
    this.initWidth();
    this.initHeight();
    this.initTabIndex();
    this._selectionStart = 0;
    this._selectionLength = 0;
    this.__oninput = rwt.util.Functions.bindEvent( this._oninputDom, this );
    this.addEventListener( "blur", this._onblur );
    this.addEventListener( "keydown", this._onkeydown );
    this.addEventListener( "keypress", this._onkeypress );
    this.addEventListener( "keyup", this._onkeyup, this );
    this.addEventListener( "mousedown", this._onMouseDownUp, this );
    this.addEventListener( "mouseup", this._onMouseDownUp, this );
    this._updateLineHeight();
    this._typed = null;
    this._selectionNeedsUpdate = false;
    this._applyBrowserFixes();
    this._inputOverflow = "hidden";
  },

  destruct : function() {
    if( this._inputElement != null ) {
      if( rwt.client.Client.isMshtml() ) {
        this._inputElement.onpropertychange = null;
      } else {
        this._inputElement.removeEventListener( "input", this.__oninput, false );
      }
    }
    this._inputElement = null;
    this.__font = null;
    if( this._checkTimer ) {
      this._checkTimer.dispose();
      this._checkTimer = null;
    }
  },

  events: {
    "input" : "rwt.event.DataEvent"
  },

  properties : {

    allowStretchX : { refine : true, init : true },
    allowStretchY : { refine : true, init : false },
    appearance : { refine : true, init : "text-field" },
    tabIndex : { refine : true, init : 1 },
    hideFocus : { refine : true, init : true },
    width : { refine : true, init : "auto" },
    height : { refine : true, init : "auto" },
    selectable : { refine : true, init : true },

    value : {
      init : "",
      nullable : true,
      event : "changeValue",
      apply : "_applyValue",
      dispose : true // in the case we use i18n text here
    },

    textAlign : {
      check : [ "left", "center", "right", "justify" ],
      nullable : true,
      themeable : true,
      apply : "_applyTextAlign"
    },

    maxLength : {
      check : "Integer",
      apply : "_applyMaxLength",
      nullable : true
    },

    readOnly : {
      check : "Boolean",
      apply : "_applyReadOnly",
      init : false,
      event : "changeReadOnly"
    }

  },

  members : {
    _LINE_HEIGT_FACTOR : 1.2,
    _inputTag : "input",
    _inputType : "text",
    _inputElement : null,

    /////////
    // API

    setSelection : function( selection ) {
      this._selectionStart = selection[ 0 ];
      this._selectionLength = selection[ 1 ] - selection[ 0 ];
      this._renderSelection();
    },

    getSelection : function() {
      return [ this._selectionStart, this._selectionStart + this._selectionLength ];
    },

    getComputedSelection : function() {
      var start = this._getSelectionStart();
      var length = this._getSelectionLength();
      return [ start, start + length ];
    },

    getComputedValue : function() {
      var result;
      if( this._inputElement != null ) {
        result = this._inputElement.value;
      } else {
        result = this.getValue();
      }
      return result;
    },

    getInputElement : function() {
      return this._inputElement || null;
    },

    /////////////////////
    // selection handling

    _renderSelection : function() {
      // setting selection here might de-select all other selections, so only render if focused
      if( this.isCreated() && this.getFocused() ) {
        this._setSelectionStart( this._selectionStart );
        this._setSelectionLength( this._selectionLength );
        this._selectionNeedsUpdate = false;
      }
    },

    _detectSelectionChange : function() {
      if( this._isCreated ) {
        var start = this._getSelectionStart();
        var length = this._getSelectionLength();
        if( typeof start === "undefined" ) {
          start = 0;
        }
        if( typeof length === "undefined" ) {
          length = 0;
        }
        if( this._selectionStart !== start || this._selectionLength !== length ) {
          this._handleSelectionChange( start, length );
        }
      }
    },

    _handleSelectionChange : function( start, length ) {
      this._selectionStart = start;
      this._selectionLength = length;
    },

    _setSelectionStart : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( vStart ) {
        this._visualPropertyCheck();
        var vText = this._inputElement.value;
        // special handling for line-breaks
        var i = 0;
        while( i < vStart ) {
          i = vText.indexOf( "\r\n", i );
          if( i === -1 ) {
            break;
          }
          vStart--;
          i++;
        }
        var vRange = this._inputElement.createTextRange();
        vRange.collapse();
        vRange.move( "character", vStart );
        vRange.select();
      },
      "gecko" : function( vStart ) {
        this._visualPropertyCheck();
        // the try catch block is neccesary because FireFox raises an exception
        // if the property "selectionStart" is read while the element or one of
        // its parent elements is invisible
        // https://bugzilla.mozilla.org/show_bug.cgi?id=329354
        try {
          this._inputElement.selectionStart = vStart;
        } catch(ex ) {
          // do nothing
        }
      },
      "default" : function( vStart) {
        this._visualPropertyCheck();
        if( this._inputElement.selectionStart !== vStart ) {
          this._inputElement.selectionStart = vStart;
        }
      }
    } ),

    _getSelectionStart : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        this._visualPropertyCheck();
        var vSelectionRange = window.document.selection.createRange();
        // Check if the document.selection is the text range inside the input element
        if( !this._inputElement.contains( vSelectionRange.parentElement() ) ) {
          return -1;
        }
        var vRange = this._inputElement.createTextRange();
        var vRange2 = vRange.duplicate();
        // Weird Internet Explorer statement
        vRange2.moveToBookmark( vSelectionRange.getBookmark() );
        vRange.setEndPoint( 'EndToStart', vRange2 );
        // for some reason IE doesn’t always count the \n and \r in the length
        var textPart = vSelectionRange.text.replace( /[\r\n]/g, '.' );
        var textWhole = this._inputElement.value.replace( /[\r\n]/g, '.' );
        return textWhole.indexOf( textPart, vRange.text.length );
      },
      "gecko" : function() {
        this._visualPropertyCheck();
        var el = this._inputElement;
        var result;
        // the try catch block is neccesary because FireFox raises an exception
        // if the property "selectionStart" is read while the element or one of
        // its parent elements is invisible
        // https://bugzilla.mozilla.org/show_bug.cgi?id=329354
        try {
          if( this.isValidString( el.value ) ) {
            result = el.selectionStart;
          } else {
            result = 0;
          }
        } catch( ex ) {
          result = 0;
        }
        return result;
      },
      "default" : function() {
        this._visualPropertyCheck();
        return this._inputElement.selectionStart;
      }
    } ),

    _setSelectionLength : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( vLength ) {
        this._visualPropertyCheck();
        var vSelectionRange = window.document.selection.createRange();
        if( !this._inputElement.contains(vSelectionRange.parentElement() ) ) {
          return;
        }
        vSelectionRange.collapse();
        vSelectionRange.moveEnd( "character", vLength );
        vSelectionRange.select();
      },
      "gecko" : function( vLength ) {
        this._visualPropertyCheck();
        var el = this._inputElement;
        // the try catch block is neccesary because FireFox raises an exception
        // if the property "selectionStart" is read while the element or one of
        // its parent elements is invisible
        // https://bugzilla.mozilla.org/show_bug.cgi?id=329354
        try {
          if( this.isValidString( el.value ) ) {
            el.selectionEnd = el.selectionStart + vLength;
          }
        } catch (ex) {}
      },
      "default" : function(vLength) {
        this._visualPropertyCheck();
        var el = this._inputElement;
        if( this.isValidString( el.value ) ) {
          var end = el.selectionStart + vLength;
          if( el.selectionEnd != end ) {
            el.selectionEnd = el.selectionStart + vLength;
          }
        }
      }
    } ),

    _getSelectionLength : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        this._visualPropertyCheck();
        var vSelectionRange = window.document.selection.createRange();
        if( !this._inputElement.contains( vSelectionRange.parentElement() ) ) {
          return 0;
        }
        return vSelectionRange.text.length;
      },
      "gecko" : function() {
        this._visualPropertyCheck();
        var el = this._inputElement;
        // the try catch block is neccesary because FireFox raises an exception
        // if the property "selectionStart" is read while the element or one of
        // its parent elements is invisible
        // https://bugzilla.mozilla.org/show_bug.cgi?id=329354
        try {
          return el.selectionEnd - el.selectionStart;
        } catch( ex ) {
          // do nothing
        }
      },
      "default" : function() {
        this._visualPropertyCheck();
        var el = this._inputElement;
        return el.selectionEnd - el.selectionStart;
      }
    } ),

    selectAll : function() {
      this._visualPropertyCheck();
      if( this.getValue() != null ) {
        this._setSelectionStart( 0 );
        this._setSelectionLength( this._inputElement.value.length );
      }
      // to be sure we get the element selected
      this._inputElement.select();
      // RAP [if] focus() leads to error in IE if the _inputElement is disabled or not visible.
      // 277444: JavaScript error in IE when using setSelection on a ComboViewer with setEnabled
      // is false
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=277444
      // 280420: [Combo] JavaScript error in IE when using setSelection on an invisible Combo
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=280420
      if( this.isEnabled() && this.isSeeable() ) {
        this._inputElement.focus();
      }
      this._detectSelectionChange();
    },

    ////////////
    // rendering

    _applyElement : function( value, old ) {
      this.base( arguments, value, old );
      if( value ) {
        this._inputElement = document.createElement( this._inputTag );
        if( this._inputType ) {
          this._inputElement.type = this._inputType;
        }
        this._inputElement.autoComplete = "off";
        this._inputElement.setAttribute( "autoComplete", "off" );
        this._inputElement.disabled = this.getEnabled() === false;
        this._inputElement.readOnly = this.getReadOnly();
        if( rwt.client.Client.isMshtml() ) {
          if( this.getValue() != null && this.getValue() !== "" ) {
            this._inputElement.value = this.getValue();
          } else {
          // See Bug 243557 - [Text] Pasting text from clipboard does not trigger ModifyListener
            this._inputElement.value = " ";
          }
        } else {
          this._inputElement.value = this.getValue() != null ? this.getValue().toString() : "";
        }
        if( this.getMaxLength() != null ) {
          this._inputElement.maxLength = this.getMaxLength();
        }
        var istyle = this._inputElement.style;
        istyle.padding = 0;
        istyle.margin = 0;
        istyle.border = "0 none";
        istyle.background = "transparent";
        istyle.overflow = this._inputOverflow;
        istyle.outline = "none";
        istyle.resize = "none";
        istyle.WebkitAppearance = "none";
        istyle.MozAppearance = "none";
        this._renderFont();
        this._renderTextColor();
        this._renderTextAlign();
        this._renderCursor();
        this._renderTextShadow();
        this._textInit();
        this._getTargetNode().appendChild( this._inputElement );
        this._updateLineHeight();
      }
    },

    _textInit : rwt.util.Variant.select( "qx.client", {
      "default" : function() {
        // Emulate IE hard-coded margin
        // Mozilla by default emulates this IE handling, but in a wrong
        // way. IE adds the additional margin to the CSS margin where
        // Mozilla replaces it. But this make it possible for the user
        // to overwrite the margin, which is not possible in IE.
        // See also: https://bugzilla.mozilla.org/show_bug.cgi?id=73817
        // NOTE [tb] : Non-IE browser also shift text 1px to the right, correcting with margin:
        this._inputElement.style.margin = "1px 0 1px -1px";
        this._inputElement.addEventListener( "input", this.__oninput, false );
        this._applyBrowserFixesOnCreate();
      },
      "mshtml" : function() {
        this._inputElement.onpropertychange = this.__oninput;
      }
    } ),

    _postApply : function() {
      this._syncFieldWidth();
      this._syncFieldHeight();
    },

    _changeInnerWidth : function( value, old ) {
      this._syncFieldWidth();
    },

    _changeInnerHeight : function(value, old) {
      this._syncFieldHeight();
      this._centerFieldVertically();
    },

    _syncFieldWidth : function() {
      this._inputElement.style.width = Math.max( 2, this.getInnerWidth() ) + "px";
    },

    _syncFieldHeight : function() {
      if( this._inputTag !== "input" ) {
        // Reduce height by 2 pixels (the manual or mshtml margin)
        this._inputElement.style.height = Math.max( 0, this.getInnerHeight() - 2 ) + "px";
      }
    },

    _applyCursor : function( value, old ) {
      if( this._inputElement != null ) {
        this._renderCursor();
      }
    },

    _renderCursor : function() {
      var style = this._inputElement.style;
      var value = this.getCursor();
      if( value ) {
        if( value === "pointer" && rwt.client.Client.isMshtml() ) {
          style.cursor = "hand";
        } else {
          style.cursor = value;
        }
      } else {
        style.cursor = "";
      }
    },

    _applyTextAlign : function( value, old ) {
      if( this._inputElement ) {
        this._renderTextAlign();
      }
    },

    _renderTextAlign : function() {
      this._inputElement.style.textAlign = this.getTextAlign() || "";
    },

    _applyEnabled : function( value, old ) {
      if( this._inputElement != null ) {
        this._inputElement.disabled = value === false;
      }
      return this.base( arguments, value, old );
    },

    _applyValue : function( value, old ) {
      this._renderValue();
      this._detectSelectionChange();
    },

    _renderValue : function() {
      this._inValueProperty = true;
      var value = this.getValue();
      if( this._inputElement != null ) {
        if (value === null) {
          value = "";
        }
        if( this._inputElement.value !== value ) {
          this._inputElement.value = value;
        }
      }
      delete this._inValueProperty;
    },

    _applyMaxLength : function( value, old ) {
      if( this._inputElement ) {
        this._inputElement.maxLength = value == null ? "" : value;
      }
    },

    _applyReadOnly : function( value, old ) {
      if( this._inputElement ) {
        this._inputElement.readOnly = value;
      }
      if( value ) {
        this.addState( "readonly" );
      } else {
        this.removeState( "readonly" );
      }
    },

    _applyTextColor : function( value, old ) {
      this._styleTextColor( value );
    },

    _styleTextColor : function( value ) {
      this.__textColor = value;
      this._renderTextColor();
    },

    _renderTextColor : function() {
      if( this._inputElement != null ) {
        this._inputElement.style.color = this.__textColor || "";
      }
    },

    _applyFont : function( value, old ) {
      this._styleFont( value );
      this._updateLineHeight();
    },

    _styleFont : function( value ) {
      this.__font = value;
      this._renderFont();
    },

    _renderFont : function() {
      if( this._inputElement != null ) {
        if( this.__font != null ) {
          this.__font.renderElement( this._inputElement );
        } else {
          rwt.html.Font.resetElement( this._inputElement );
        }
      }
    },

    _updateLineHeight : function() {
      if( this._inputElement != null ) {
        var font = this.getFont();
        var height = Math.floor( font.getSize() * this._LINE_HEIGT_FACTOR );
        this._inputElement.style.lineHeight = height + "px";
      }
    },

    _applyTextShadow : function( value, oldValue ) {
      this.__textShadow = value;
      if( this._inputElement ) {
        this._renderTextShadow();
      }
    },

    _renderTextShadow : function() {
      rwt.html.Style.setTextShadow( this._inputElement, this.__textShadow );
    },

    _visualizeFocus : function() {
      this.base( arguments );
      if( !rwt.widgets.util.FocusHandler.blockFocus ) {
        try {
          this._inputElement.focus();
        } catch( ex ) {
          // ignore
        }
      }
    },

    _visualizeBlur : function() {
      this.base( arguments );
      try {
        this._inputElement.blur();
      } catch( ex ) {
        // ignore
      }
    },

    _afterAppear : function() {
      this.base( arguments );
      this._applyBrowserFixesOnAppear();
      this._centerFieldVertically();
      this._renderSelection();
    },


    _centerFieldVertically : function() {
      if( this._inputTag === "input" && this._inputElement ) {
        var innerHeight = this.getInnerHeight();
        var inputElementHeight = this._getInputElementHeight();
        if( inputElementHeight !== 0 ) {
          var top = ( innerHeight - inputElementHeight ) / 2 - 1;
          if( top < 0 ) {
            top = 0;
          }
          top = Math.floor( top );
          // [if] Set padding instead of style.position of the _inputElement.
          // style.position leads to problems with DOM events in FF 3.0.x
          // see bug 292487 and bug 284356
          this.setStyleProperty( "paddingTop", top + "px" );
        }
      }
    },

    _getInputElementHeight : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        var result = this._inputElement.offsetHeight;
        if( result !== 0 ) {
          result -= 2;
        }
        return result;
      },
      "default" :function() {
        return this._inputElement.offsetHeight;
      }
    } ),

    ////////////////
    // event handler

    _oninputDom : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( event ) {
        if( !this._inValueProperty && event.propertyName === "value" ) {
          this._oninput();
        }
      },
      "default" : function( event ) {
        this._oninput();
      }
    } ),

    _oninput : function() {
      try {
        var newValue = this.getComputedValue().toString();
        var doit = true;
        if( this.hasEventListeners( "input" ) ) {
          doit = this.dispatchEvent( new rwt.event.DataEvent( "input", this._typed ), true );
        }
        if( doit ) {
          // at least webkit does sometiems fire "input" before the selection is updated
          rwt.client.Timer.once( this._updateValueProperty, this, 0 );
        } else if( rwt.client.Client.isWebkit() || rwt.client.Client.isMshtml() ){
          // some browser set new selection after input event, ignoring all changes before that
          rwt.client.Timer.once( this._renderSelection, this, 0 );
          this._selectionNeedsUpdate = true;
        }
      } catch( ex ) {
        rwt.runtime.ErrorHandler.processJavaScriptError( ex );
      }
    },

    _updateValueProperty : function() {
      this.setValue( this.getComputedValue().toString() );
    },

    _ontabfocus : function() {
      this.selectAll();
    },

    _applyFocused : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      if( !rwt.widgets.util.FocusHandler.mouseFocus ) {
        this._renderSelection();
      }
    },

    _onblur : function() {
      // RAP workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=201080
      if( this.getParent() != null ) {
        this._setSelectionLength( 0 );
      }
    },

    // [rst] Catch backspace in readonly text fields to prevent browser default
    // action (which commonly triggers a history step back)
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=178320
    _onkeydown : function( e ) {
      if( e.getKeyIdentifier() == "Backspace" && this.getReadOnly() ) {
        e.preventDefault();
      }
      this._detectSelectionChange();
      this._typed = null;
    },

    // [if] Stops keypress propagation
    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=335779
    _onkeypress : function( e ) {
      if( e.getKeyIdentifier() !== "Tab" ) {
        e.stopPropagation();
      }
      if( this._selectionNeedsUpdate ) {
        this._renderSelection();
      }
      this._detectSelectionChange();
      this._typed = String.fromCharCode( e.getCharCode() );
    },

    _onkeyup : function( event ) {
      if( this._selectionNeedsUpdate ) {
        this._renderSelection();
      }
      this._detectSelectionChange();
      this._typed = null;
    },

    _onMouseDownUp : function( event ) {
      this._detectSelectionChange();
      this._typed = null;
    },

    /////////////////
    // browser quirks

    _applyBrowserFixes : rwt.util.Variant.select( "qx.client", {
      "default" : function() {},
      "newmshtml" : function() {
        // See Bug 372193 - Text widget: Modify Event not fired for Backspace key in IE
        this._checkTimer = new rwt.client.Timer( 0 );
        this._checkTimer.addEventListener( "interval", this._checkValueChanged, this );
        // For delete, backspace, CTRL+X, etc:
        this.addEventListener( "keypress", this._checkTimer.start, this._checkTimer );
        this.addEventListener( "keyup", this._checkTimer.start, this._checkTimer );
        // For context menu: (might not catch the change instantly
        this.addEventListener( "mousemove", this._checkValueChanged, this );
        this.addEventListener( "mouseout", this._checkValueChanged, this );
        // Backup for all other cases (e.g. menubar):
        this.addEventListener( "blur", this._checkValueChanged, this );
      }
    } ),

    _checkValueChanged : function() {
      this._checkTimer.stop();
      var newValue = this.getComputedValue();
      var oldValue = this.getValue();
      if( newValue !== oldValue ) {
        this._oninput();
      }
    },

    _applyBrowserFixesOnAppear : rwt.util.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
        if( this._firstInputFixApplied !== true && this._inputElement ) {
          rwt.client.Timer.once( this._ieFirstInputFix, this, 1 );
        }
      }
    } ),

    _ieFirstInputFix : function() {
      if( !this.isDisposed() ) {
        this._inValueProperty = true;
        this._inputElement.value = this.getValue() === null ? "" : this.getValue().toString();
        this._renderSelection();
        this._firstInputFixApplied = true;
        delete this._inValueProperty;
      }
    },

    _applyBrowserFixesOnCreate  : rwt.util.Variant.select( "qx.client", {
      "default" : function() {},
      "webkit" : function() {
        this.addEventListener( "keydown", this._preventEnter, this );
        this.addEventListener( "keypress", this._preventEnter, this );
        this.addEventListener( "keyup", this._preventEnter, this );
      }
    } ),

    _preventEnter : function( event ) {
      if( event.getKeyIdentifier() === "Enter" ) {
        event.preventDefault();
      }
    },

    /////////
    // helper

    isValidString : function( v ) {
      return typeof v === "string" && v !== "";
    }
  }

} );
