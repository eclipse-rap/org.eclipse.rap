/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.Spinner", {
  extend : qx.ui.form.Spinner,

  construct : function() {
    this.base( arguments );
    this._isModified = false;
    this._hasModifyListener = false;
    this._hasSelectionListener = false;
    this.setWrap( false );
    // Hack to prevent the spinner text field to request the focus
    this._textfield.setFocused = function() {};
    this._textfield.addEventListener( "changeValue", this._onChangeValue, this );
    this._textfield.addEventListener( "keyinput", this._onChangeValue, this );
    this._textfield.addEventListener( "blur", this._onChangeValue, this );
    this._textfield.addEventListener( "keydown", this._onKeyDown, this );
    this._textfield.setTabIndex( null );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    this.addEventListener( "focusout", this._onFocusOut, this );
    this._checkValue = this.__checkValueWithDigits;
  },

  destruct : function() {
    this._textfield.removeEventListener( "changeValue", this._onChangeValue, this );
    this._textfield.removeEventListener( "keyinput", this._onChangeValue, this );
    this._textfield.removeEventListener( "blur", this._onChangeValue, this );
    this._textfield.removeEventListener( "keydown", this._onKeyDown, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    this.removeEventListener( "focusout", this._onFocusOut, this );
  },

  properties : {

    digits : {
      check : "Integer",
      init : 0,
      apply : "_applyDigits"
    },

    decimalSeparator : {
      check : "String",
      init : ".",
      apply : "_applyDecimalSeparator"
    }

  },

  members : {

    setFont : function( value ) {
      this._textfield.setFont( value );
    },

    setMaxLength : function( value ) {
      this._textfield.setMaxLength( value );
    },

    // [if] Spinner#setValues allows minimum, maximum and selection to be set in
    // one hop. In case of not crossed ranges ( for example new min > old max ),
    // a javascript error appears if we set them one by one.
    setMinMaxSelection : function( min, max, value ) {
      this.setMin( Math.min( min, this.getMin() ) );
      this.setMax( Math.max( max, this.getMax() ) );
      this.setValue( value );
      this.setMin( min );
      this.setMax( max );
    },

    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
      if( value ) {
        this._upbutton.setCursor( value );
        this._downbutton.setCursor( value );
        this._textfield.setCursor( value );
      } else {
        this._upbutton.resetCursor();
        this._downbutton.resetCursor();
        this._textfield.resetCursor();
      }
    },

    setHasModifyListener : function( value ) {
      this._hasModifyListener = value;
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    _visualizeFocus : function() {
      this._textfield._visualizeFocus();
      if( this._textfield.isCreated() ) {
        this._textfield.selectAll();
      }
    },

    _visualizeBlur : function() {
      // setSelectionLength( 0 ) for TextField - needed for IE
      this._textfield.setSelectionLength( 0 );
      this._textfield._visualizeBlur();
    },

    // [if] Override original qooxdoo Spinner method. Fix for bug 209476
    _oninput : function( evt ) {
      this._suspendTextFieldUpdate = true;
      this._checkValue( true, false );
      this._suspendTextFieldUpdate = false;
    },

    _onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && !this._isModified ) {
        this._isModified = true;
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", this._onSend, this );
        if( this._hasSelectionListener ) {
          this._addModifyTextEvent();
          this._sendWidgetSelected();
        } else if( this._hasModifyListener ) {
          this._addModifyTextEvent();
          qx.client.Timer.once( this._sendModifyText, this, 500 );
        }
      }
    },

    // TODO [rst] workaround: setting enabled to false still leaves the buttons
    //      enabled
    _onChangeEnabled : function( evt ) {
      var enabled = evt.getValue();
      this._upbutton.setEnabled( enabled && this.getValue() < this.getMax() );
      this._downbutton.setEnabled( enabled && this.getValue() > this.getMin() );
    },

    _onKeyDown : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if(    event.getKeyIdentifier() == "Enter"
            && !event.isShiftPressed()
            && !event.isAltPressed()
            && !event.isCtrlPressed()
            && !event.isMetaPressed()
            && this._hasSelectionListener )
        {
          event.stopPropagation();
          this._sendWidgetDefaultSelected();
        }
      }
    },

    _onmousewheel : function( evt ) {
      if( this.getFocused() ) {
        this.base( arguments, evt );
      }
    },

    _addModifyTextEvent : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.modifyText", id );
    },

    _sendWidgetSelected : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.send();
    },

    _sendWidgetDefaultSelected : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.send();
    },

    _onSend : function( evt ) {
      this._isModified = false;
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".selection", this.getManager().getValue() );
      req.removeEventListener( "send", this._onSend, this );
    },

    _sendModifyText : function( evt ) {
      if( this._isModified ) {
        org.eclipse.swt.Request.getInstance().send();
        this._isModified = false;
      }
    },

    /////////////////
    // Digits support

    _applyDigits : function( value, old ) {
      var spinnerValue = this.getManager().getValue();
      if( this.getDigits() > 0 ) {
        this._textfield.setValue( this._format( spinnerValue ) );
      } else {
        this._textfield.setValue( String( spinnerValue ) );
      }
    },

    _applyDecimalSeparator : function( value, old ) {
      var spinnerValue = this.getManager().getValue();
      if( this.getDigits() > 0 ) {
        this._textfield.setValue( this._format( spinnerValue ) );
      }
    },

    _format : function( value ) {
      var digits = this.getDigits();
      var floatValue = value / Math.pow( 10, digits );
      var result = floatValue.toFixed( digits );
      var separator = this.getDecimalSeparator();
      if( separator != "." ) {
        var dot = qx.lang.String.escapeRegexpChars( "." );
        result = result.replace( new RegExp( dot ), separator );
      }  
      return result
    },

    _limit : function( value ) {
      var result = value;
      var digits = this.getDigits();
      if( digits > 0 ) {
        result = result * Math.pow( 10, digits );
      }
      result = Math.round( result );
      if( result > this.getMax() ) {
        result = this.getMax();
      }
      if( result < this.getMin() ) {
        result = this.getMin();
      }
      return result;
    },

    _onFocusOut : function( evt ) {
      this._checkValue( true, false );
    },

    _onkeypress : function( evt ) {
      var identifier = evt.getKeyIdentifier();
      var separator = this.getDecimalSeparator();
      if( identifier == '-' ) {
        evt.preventDefault();
      } else if( !( this.getDigits() > 0 && identifier == separator ) ) {
        this.base( arguments, evt );
      }
    },

    _onchange : function( evt ) {
      var value = this.getManager().getValue();
      if( !this._suspendTextFieldUpdate ) {
        if( this.getDigits() > 0 ) {
          this._textfield.setValue( this._format( value ) );
        } else {
          this._textfield.setValue( String( value ) );
        }
      }
      if( value == this.getMin() && !this.getWrap() ) {
        this._downbutton.removeState( "pressed" );
        this._downbutton.setEnabled( false );
        this._timer.stop();
      } else {
        this._downbutton.resetEnabled();
      }
      if( value == this.getMax() && !this.getWrap() ) {
        this._upbutton.removeState( "pressed" );
        this._upbutton.setEnabled( false );
        this._timer.stop();
      } else {
        this._upbutton.resetEnabled();
      }
      this.createDispatchDataEvent( "change", value );
    },

    __checkValueWithDigits : function( acceptEmpty, acceptEdit ) {
      var inputElement = this._textfield.getInputElement();
      if( inputElement ) {
        if( inputElement.value == "" && !acceptEmpty ) {
          this.resetValue();
        } else {          
          var strValue = inputElement.value;
          var parseValue = strValue;
          var separator = this.getDecimalSeparator();
          if( this.getDigits() > 0 && separator != "." ) {
            separator = qx.lang.String.escapeRegexpChars( separator );
            parseValue = strValue.replace( new RegExp( separator ), "." );
          }
          var value = parseFloat( parseValue );          
          var limitedValue = this._limit( value );
          var oldValue = this.getManager().getValue();
          var fixedValue = limitedValue;
          if( isNaN( value ) || value != limitedValue || value != parseValue ) {
            if( acceptEdit ) {
              this._textfield.setValue( this._last_value );
            } else if( isNaN( limitedValue ) ) {
              fixedValue = oldValue;
            }
          }
          if( !acceptEdit ) {
            var formattedValue = String( fixedValue );
            if( this.getDigits() > 0 ) {
              formattedValue = this._format( fixedValue );
            }
            if(    fixedValue === oldValue
                && strValue !== formattedValue
                && !this._suspendTextFieldUpdate )
            {
              this._textfield.setValue( formattedValue );
            }
            this.getManager().setValue( fixedValue );
          }
        }
      }
    }
  }
});
