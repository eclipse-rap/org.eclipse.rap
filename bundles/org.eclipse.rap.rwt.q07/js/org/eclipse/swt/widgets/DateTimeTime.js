/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.DateTimeTime", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "datetime-time" );

    // Get styles
    this._short = qx.lang.String.contains( style, "short" );
    this._medium = qx.lang.String.contains( style, "medium" );
    this._long = qx.lang.String.contains( style, "long" );

    // Has selection listener
    this._hasSelectionListener = false;

    // Flag that indicates that the next request can be sent
    this._readyToSendChanges = true;

    // Add listener for font change
    this.addEventListener( "changeFont", this._rwt_onChangeFont, this );

    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "keyup", this._onKeyUp, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );

    // Focused text field
    this._focusedTextField = null;
    // Hours
    this._hoursTextField = new qx.ui.basic.Label( "00" );
    this._hoursTextField.setAppearance( "datetime-field" );
    this._hoursTextField.setUserData( "maxLength", 2 );
    this._hoursTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    this.add(this._hoursTextField);
    // Separator
    this._separator3 = new qx.ui.basic.Label( ":" );
    this._separator3.setAppearance( "datetime-separator" );
    this._separator3.addEventListener( "contextmenu", this._onContextMenu, this );
    this.add(this._separator3);
    // Minutes
    this._minutesTextField = new qx.ui.basic.Label( "00" );
    this._minutesTextField.setAppearance( "datetime-field" );
    this._minutesTextField.setUserData( "maxLength", 2 );
    this._minutesTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    this.add(this._minutesTextField);
    // Separator
    this._separator4 = new qx.ui.basic.Label( ":" );
    this._separator4.setAppearance( "datetime-separator" );
    if( this._medium || this._long ) {
      this.add(this._separator4);
    }
    // Seconds
    this._secondsTextField = new qx.ui.basic.Label( "00" );
    this._secondsTextField.setAppearance( "datetime-field" );
    this._secondsTextField.setUserData( "maxLength", 2 );
    this._secondsTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    if( this._medium || this._long ) {
      this.add(this._secondsTextField);
    }
    // Spinner
    this._spinner = new qx.ui.form.Spinner();
    this._spinner.set({
      wrap: true,
      border: null,
      backgroundColor: null
    });
    this._spinner.setMin( 0 );
    this._spinner.setMax( 23 );
    this._spinner.setValue( 0 );
    this._spinner.addEventListener( "change",  this._onSpinnerChange, this );
    this._spinner._textfield.setTabIndex( -1 );
    // Hack to prevent the spinner text field to request the focus
    this._spinner._textfield.setFocused = function() {};
    this._spinner.removeEventListener("keypress", this._spinner._onkeypress, this._spinner);
    this._spinner.removeEventListener("keydown", this._spinner._onkeydown, this._spinner);
    this._spinner.removeEventListener("keyup", this._spinner._onkeyup, this._spinner);
    this.add( this._spinner );
    // Set the default focused text field
    this._focusedTextField = this._hoursTextField;
  },

  destruct : function() {
    this.removeEventListener( "changeFont", this._rwt_onChangeFont, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "keyup", this._onKeyUp, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
    this._hoursTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._minutesTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._secondsTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._spinner.removeEventListener( "change",  this._onSpinnerChange, this );
    this._disposeObjects( "_hoursTextField",
                          "_minutesTextField",
                          "_secondsTextField",
                          "_focusedTextField",
                          "_spinner",
                          "_separator3",
                          "_separator4" );
  },

  statics : {
    HOURS_TEXTFIELD : 8,
    MINUTES_TEXTFIELD : 9,
    SECONDS_TEXTFIELD : 10,
    HOURS_MINUTES_SEPARATOR : 11,
    MINUTES_SECONDS_SEPARATOR : 12,
    SPINNER : 7,

    _isNoModifierPressed : function( evt ) {
      return    !evt.isCtrlPressed()
             && !evt.isShiftPressed()
             && !evt.isAltPressed()
             && !evt.isMetaPressed();
    }
  },

  members : {
    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._hoursTextField.addState( state );
        this._minutesTextField.addState( state );
        this._secondsTextField.addState( state );
        this._spinner.addState( state );
        this._separator3.addState( state );
        this._separator4.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._hoursTextField.removeState( state );
        this._minutesTextField.removeState( state );
        this._secondsTextField.removeState( state );
        this._spinner.removeState( state );
        this._separator3.removeState( state );
        this._separator4.removeState( state );
      }
    },
    
    _rwt_onChangeFont : function( evt ) {
      var value = evt.getValue();
      this._hoursTextField.setFont( value );
      this._minutesTextField.setFont( value );
      this._secondsTextField.setFont( value );
    },

    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },

    _onFocusIn : function( evt ) {
      this._focusedTextField.addState( "selected" );
    },

    _onFocusOut : function( evt ) {
      this._focusedTextField.removeState( "selected" );
    },

    _onMouseDown : function( evt ) {
      this._setFocusedTextField( evt.getTarget() );
    },

    _setFocusedTextField :  function( textField ) {
      var tmpValue;
      this._focusedTextField.removeState( "selected" );
      // Set focused text field to null
      this._focusedTextField = null;
      if( textField === this._hoursTextField ) {
        this._spinner.setMin( 0 );
        this._spinner.setMax( 23 );
        tmpValue = this._removeLeadingZero( this._hoursTextField.getText() );
        this._spinner.setValue( parseInt( tmpValue ) );
      } else if( textField === this._minutesTextField ) {
        this._spinner.setMin( 0 );
        this._spinner.setMax( 59 );
        tmpValue = this._removeLeadingZero( this._minutesTextField.getText() );
        this._spinner.setValue( parseInt( tmpValue ) );
      } else if( textField === this._secondsTextField ) {
        this._spinner.setMin( 0 );
        this._spinner.setMax( 59 );
        tmpValue = this._removeLeadingZero( this._secondsTextField.getText() );
        this._spinner.setValue( parseInt( tmpValue ) );
      }
      // Set focused text field
      this._focusedTextField = textField;
      // Set highlight on focused text field
      this._focusedTextField.addState( "selected" );
    },

    _onSpinnerChange : function( evt ) {
      if( this._focusedTextField != null ) {
        var oldValue = this._focusedTextField.getText();
        var newValue = this._addLeadingZero( this._spinner.getValue() );
        this._focusedTextField.setText( newValue );
        if( oldValue != newValue && this._readyToSendChanges ) {
          this._readyToSendChanges = false;
          // Send changes
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },

    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      if( org.eclipse.swt.widgets.DateTimeTime._isNoModifierPressed( evt ) ) {
        switch( keyIdentifier ) {
          case "Left":
            if( this._focusedTextField === this._hoursTextField ) {
              if( this._short ) {
                this._setFocusedTextField( this._minutesTextField );
              } else {
                this._setFocusedTextField( this._secondsTextField );
              }
            } else if( this._focusedTextField === this._minutesTextField ) {
              this._setFocusedTextField( this._hoursTextField );
            } else if( this._focusedTextField === this._secondsTextField ) {
              this._setFocusedTextField( this._minutesTextField );
            }
            break;
          case "Right":
            if( this._focusedTextField === this._hoursTextField ) {
              this._setFocusedTextField( this._minutesTextField );
            } else if( this._focusedTextField === this._minutesTextField ) {
              if( this._short ) {
                this._setFocusedTextField( this._hoursTextField );
              } else {
                this._setFocusedTextField( this._secondsTextField );
              }
            } else if( this._focusedTextField === this._secondsTextField ) {
              this._setFocusedTextField( this._hoursTextField );
            }
            break;
          case "Up":
            var value = this._spinner.getValue();
            if( value == this._spinner.getMax() ) {
              this._spinner.setValue( this._spinner.getMin() );
            } else {
              this._spinner.setValue( value + 1 );
            }
            break;
          case "Down":
            var value = this._spinner.getValue();
            if( value == this._spinner.getMin() ) {
              this._spinner.setValue( this._spinner.getMax() );
            } else {
              this._spinner.setValue( value - 1 );
            }
            break;
        }
      }
    },

    _onKeyUp : function( evt ) {
      var keypress = evt.getKeyIdentifier();
      var value = this._focusedTextField.getText();
      value = this._removeLeadingZero( value );
      if( org.eclipse.swt.widgets.DateTimeTime._isNoModifierPressed( evt ) ) {
        switch( keypress ) {
          case "0": case "1": case "2": case "3": case "4":
          case "5": case "6": case "7": case "8": case "9":
            var maxChars = this._focusedTextField.getUserData( "maxLength" );
            var newValue = keypress;
            if( value.length < maxChars ) {
              newValue = value + keypress;
            }
            var intValue = parseInt( this._removeLeadingZero( newValue ) );
            if( intValue >= this._spinner.getMin() &&
                intValue <= this._spinner.getMax() ) {
              this._spinner.setValue( intValue );
            } else {
              newValue = keypress;
              intValue = parseInt( newValue );
              if( intValue >= this._spinner.getMin() &&
                  intValue <= this._spinner.getMax() ) {
                this._spinner.setValue( intValue );
              }
            }
            break;
          case "Home":
            var newValue = this._spinner.getMin();
            this._spinner.setValue( newValue );
            break;
          case "End":
            var newValue = this._spinner.getMax();
            this._spinner.setValue( newValue );
            break;
        }
      }
    },

    _addLeadingZero : function( value ) {
      return value < 10 ? "0" + value : "" + value;
    },

    _removeLeadingZero : function( value ) {
      var result = value;
      if( value.length == 2 ) {
        var firstChar = value.substring( 0, 1 );
        if( firstChar == "0" ) result = value.substring( 1 );
      }
      return result;
    },

    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addParameter( id + ".hours",
                          this._removeLeadingZero( this._hoursTextField.getText() ) );
        req.addParameter( id + ".minutes",
                          this._removeLeadingZero( this._minutesTextField.getText() ) );
        req.addParameter( id + ".seconds",
                          this._removeLeadingZero( this._secondsTextField.getText() ) );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
        this._readyToSendChanges = true;
      }
    },

    setHours : function( value ) {
      this._hoursTextField.setText( this._addLeadingZero( value ) );
      if( this._focusedTextField === this._hoursTextField ) {
        this._spinner.setValue( value );
      }
    },

    setMinutes : function( value ) {
      this._minutesTextField.setText( this._addLeadingZero( value ) );
      if( this._focusedTextField === this._minutesTextField ) {
        this._spinner.setValue( value );
      }
    },

    setSeconds : function( value ) {
      this._secondsTextField.setText( this._addLeadingZero( value ) );
      if( this._focusedTextField === this._secondsTextField ) {
        this._spinner.setValue( value );
      }
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setBounds : function( ind, x, y, width, height ) {
      var widget;
      switch( ind ) {
        case org.eclipse.swt.widgets.DateTimeTime.HOURS_TEXTFIELD:
          widget = this._hoursTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeTime.MINUTES_TEXTFIELD:
          widget = this._minutesTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeTime.SECONDS_TEXTFIELD:
          widget = this._secondsTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeTime.HOURS_MINUTES_SEPARATOR:
          widget = this._separator3;
        break;
        case org.eclipse.swt.widgets.DateTimeTime.MINUTES_SECONDS_SEPARATOR:
          widget = this._separator4;
        break;
        case org.eclipse.swt.widgets.DateTimeTime.SPINNER:
          widget = this._spinner;
        break;
      }
      widget.set({
        left: x,
        top: y,
        width: width,
        height: height
      });
    }
  }
} );
