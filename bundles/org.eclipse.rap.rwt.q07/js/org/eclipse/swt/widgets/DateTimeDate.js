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

qx.Class.define( "org.eclipse.swt.widgets.DateTimeDate", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style,
                        monthNames,
                        weekdayNames,
                        dateSeparator,
                        datePattern )
  {
    this.base( arguments );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this.setAppearance( "datetime-date" );

    // Get styles
    this._short = qx.lang.String.contains( style, "short" );
    this._medium = qx.lang.String.contains( style, "medium" );
    this._long = qx.lang.String.contains( style, "long" );

    // Has selection listener
    this._hasSelectionListener = false;

    // Flag that indicates that the next request can be sent
    this._readyToSendChanges = true;

    // Get names of weekdays and months
    this._weekday = weekdayNames;
    this._monthname = monthNames;

    // Date pattern
    this._datePattern = datePattern;

    // Add listener for font change
    this.addEventListener( "changeFont", this._rwt_onChangeFont, this );

    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "keyup", this._onKeyUp, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );

    // Focused text field
    this._focusedTextField = null;
    // Weekday
    this._weekdayTextField = new qx.ui.basic.Label();
    this._weekdayTextField.setAppearance( "datetime-field" );
    if( this._long ) {
      this.add( this._weekdayTextField );
    }
    // Separator
    this._separator0 = new qx.ui.basic.Label(",");
    this._separator0.setAppearance( "datetime-separator" );
    if( this._long ) {
      this.add(this._separator0);
    }
    // Month
    this._monthTextField = new qx.ui.basic.Label();
    this._monthTextField.setAppearance( "datetime-field" );
    this._monthTextField.set({
      textAlign: this._medium ? "right" : "center"
    });
    // Integer value of the month
    this._monthInt = 1;
    if( this._medium ) {
      this._monthTextField.setText( "1" );
    } else {
      this._monthTextField.setText( this._monthname[ this._monthInt - 1 ] );
    }
    this._monthTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    this.add( this._monthTextField );
    // Separator
    this._separator1 = new qx.ui.basic.Label( dateSeparator );
    this._separator1.setAppearance( "datetime-separator" );
    if( this._medium ) {
      this.add(this._separator1);
    }
    // Date
    this._dayTextField = new qx.ui.basic.Label( "1" );
    this._dayTextField.setAppearance( "datetime-field" );
    this._dayTextField.setUserData( "maxLength", 2 );
    this._dayTextField.set({
      textAlign: "right"
    });
    this._dayTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    if( !this._short ) {
      this.add( this._dayTextField );
    }
    // Separator
    this._separator2 = new qx.ui.basic.Label( "," );
    this._separator2.setAppearance( "datetime-separator" );
    if( this._medium ) {
      this._separator2.setText( dateSeparator );
    }
    this.add(this._separator2);
    // Year
    this._yearTextField = new qx.ui.basic.Label( "1970" );
    this._yearTextField.setAppearance( "datetime-field" );
    this._yearTextField.setUserData( "maxLength", 4 );
    this._yearTextField.set({
      textAlign: "right"
    });
    // Last valid year
    this._lastValidYear = 1970;
    this._yearTextField.addEventListener( "mousedown",  this._onMouseDown, this );
    this.add( this._yearTextField );
    // Spinner
    this._spinner = new qx.ui.form.Spinner();
    this._spinner.set({
      wrap: true,
      border: null,
      backgroundColor: null
    });
    this._spinner.setMin( 1 );
    this._spinner.setMax( 12 );
    this._spinner.setValue( this._monthInt );
    this._spinner.addEventListener( "change",  this._onSpinnerChange, this );
    this._spinner._textfield.setTabIndex( -1 );
    // Hack to prevent the spinner text field to request the focus
    this._spinner._textfield.setFocused = function() {};
    this._spinner._upbutton.setAppearance("datetime-button-up");
    this._spinner._downbutton.setAppearance("datetime-button-down");
    this._spinner.removeEventListener("keypress", this._spinner._onkeypress, this._spinner);
    this._spinner.removeEventListener("keydown", this._spinner._onkeydown, this._spinner);
    this._spinner.removeEventListener("keyup", this._spinner._onkeyup, this._spinner);
    this.add( this._spinner );
    // Set the default focused text field
    this._focusedTextField = this._monthTextField;
    // Set the weekday
    this._setWeekday();
  },

  destruct : function() {
    this.removeEventListener( "changeFont", this._rwt_onChangeFont, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "keyup", this._onKeyUp, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
    this._monthTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._dayTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._yearTextField.removeEventListener( "mousedown",  this._onMouseDown, this );
    this._spinner.removeEventListener( "change",  this._onSpinnerChange, this );
    this._disposeObjects( "_weekdayTextField",
                          "_monthTextField",
                          "_dayTextField",
                          "_yearTextField",
                          "_focusedTextField",
                          "_spinner",
                          "_separator0",
                          "_separator1",
                          "_separator2" );
  },

  statics : {
    WEEKDAY_TEXTFIELD : 0,
    DATE_TEXTFIELD : 1,
    MONTH_TEXTFIELD : 2,
    YEAR_TEXTFIELD : 3,
    WEEKDAY_MONTH_SEPARATOR : 4,
    MONTH_DATE_SEPARATOR : 5,
    DATE_YEAR_SEPARATOR : 6,
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
        this._weekdayTextField.addState( state );
        this._monthTextField.addState( state );
        this._dayTextField.addState( state );
        this._yearTextField.addState( state );
        this._spinner.addState( state );
        this._separator0.addState( state );
        this._separator1.addState( state );
        this._separator2.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._weekdayTextField.removeState( state );
        this._monthTextField.removeState( state );
        this._dayTextField.removeState( state );
        this._yearTextField.removeState( state );
        this._spinner.removeState( state );
        this._separator0.removeState( state );
        this._separator1.removeState( state );
        this._separator2.removeState( state );
      }
    },
    
    _rwt_onChangeFont : function( evt ) {
      var value = evt.getValue();
      this._weekdayTextField.setFont( value );
      this._dayTextField.setFont( value );
      this._monthTextField.setFont( value );
      this._yearTextField.setFont( value );
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
      if( this._focusedTextField === this._yearTextField ) {
        this._checkAndApplyYearValue();
      }
      this._focusedTextField.removeState( "selected" );
    },

    _onMouseDown : function( evt ) {
      if( this._focusedTextField === this._yearTextField ) {
        this._checkAndApplyYearValue();
      }
      this._setFocusedTextField( evt.getTarget() );
    },

    _setFocusedTextField :  function( textField ) {
      var tmpValue;
      this._focusedTextField.removeState( "selected" );
      // Set focused text field to null
      this._focusedTextField = null;
      if( textField === this._dayTextField ) {
        this._spinner.setMin( 1 );
        this._spinner.setMax( this._getDaysInMonth() );
        tmpValue = this._removeLeadingZero( this._dayTextField.getText() );
        this._spinner.setValue( parseInt( tmpValue ) );
      } else if( textField === this._monthTextField ) {
        this._spinner.setMin( 1 );
        this._spinner.setMax( 12 );
        this._spinner.setValue( this._monthInt );
      } else if( textField === this._yearTextField ) {
        this._spinner.setMax( 9999 );
        this._spinner.setMin( 1752 );
        this._spinner.setValue( this._lastValidYear );
      }
      // Set focused text field
      this._focusedTextField = textField;
      // Set highlight on focused text field
      this._focusedTextField.addState( "selected" );
    },

    _onSpinnerChange : function( evt ) {
      if( this._focusedTextField != null ) {
        var oldValue = this._focusedTextField.getText();
        // Set the value
        if( this._focusedTextField === this._monthTextField ) {
          this._monthInt = this._spinner.getValue();
          if( this._medium ) {
            this._focusedTextField.setText( this._addLeadingZero( this._monthInt ) );
          } else {
            this._focusedTextField.setText( this._monthname[ this._monthInt - 1 ] );
          }
        } else if( this._focusedTextField === this._yearTextField ) {
            this._lastValidYear = this._spinner.getValue();
            this._focusedTextField.setText( "" + this._spinner.getValue() );
        } else {
          this._focusedTextField.setText( this._addLeadingZero( this._spinner.getValue() ) );
        }
        // Adjust date field
        if( this._focusedTextField == this._monthTextField || // month
            this._focusedTextField == this._yearTextField ) { // year
          var dateValue = this._dayTextField.getText();
          if( dateValue > this._getDaysInMonth() ) {
            this._dayTextField.setText( "" + this._getDaysInMonth() );
          }
        }
        // Set the weekday field
        this._setWeekday();

        var newValue = this._focusedTextField.getText();
        if( oldValue != newValue && this._readyToSendChanges ) {
          this._readyToSendChanges = false;
          // Send changes
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },

    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      if( org.eclipse.swt.widgets.DateTimeDate._isNoModifierPressed( evt ) ) {
        switch( keyIdentifier ) {
          case "Left":
            if( this._datePattern == "MDY") {
              this._rollLeft( this._monthTextField,
                              this._dayTextField,
                              this._yearTextField );
            } else if( this._datePattern == "DMY") {
              this._rollLeft( this._dayTextField,
                              this._monthTextField,
                              this._yearTextField );
            } else {
              if( this._medium ) {
                this._rollLeft( this._yearTextField,
                                this._monthTextField,
                                this._dayTextField );
              } else {
                this._rollLeft( this._monthTextField,
                                this._dayTextField,
                                this._yearTextField );
              }
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Right":
            if( this._datePattern == "MDY") {
              this._rollRight( this._monthTextField,
                               this._dayTextField,
                               this._yearTextField );
            } else if( this._datePattern == "DMY") {
              this._rollRight( this._dayTextField,
                               this._monthTextField,
                               this._yearTextField );
            } else {
              if( this._medium ) {
                this._rollRight( this._yearTextField,
                                 this._monthTextField,
                                 this._dayTextField );
              } else {
                this._rollRight( this._monthTextField,
                                 this._dayTextField,
                                 this._yearTextField );
              }
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Up":
            if( this._focusedTextField === this._yearTextField ) {
              this._checkAndApplyYearValue();
            }
            var value = this._spinner.getValue();
            if( value == this._spinner.getMax() ) {
              this._spinner.setValue( this._spinner.getMin() );
            } else {
              this._spinner.setValue( value + 1 );
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Down":
            if( this._focusedTextField === this._yearTextField ) {
              this._checkAndApplyYearValue();
            }
            var value = this._spinner.getValue();
            if( value == this._spinner.getMin() ) {
              this._spinner.setValue( this._spinner.getMax() );
            } else {
              this._spinner.setValue( value - 1 );
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
        }
      }
    },

    _rollRight : function( first, second, third ) {
      // Apply year value
      if( this._focusedTextField === this._yearTextField ) {
        this._checkAndApplyYearValue();
      }
      // Roll right
      if( this._focusedTextField === first ){
        if( second.isSeeable() ) {
          this._setFocusedTextField( second );
        } else {
          this._setFocusedTextField( third );
        }
      } else if( this._focusedTextField === second ) {
        if( third.isSeeable() ) {
          this._setFocusedTextField( third );
        } else {
          this._setFocusedTextField( first );
        }
      } else if( this._focusedTextField === third ) {
        if( first.isSeeable() ) {
          this._setFocusedTextField( first );
        } else {
          this._setFocusedTextField( second );
        }
      }
    },

    _rollLeft : function( first, second, third ) {
      // Apply year value
      if( this._focusedTextField === this._yearTextField ) {
        this._checkAndApplyYearValue();
      }
      // Roll left
      if( this._focusedTextField === first ){
        if( third.isSeeable() ) {
          this._setFocusedTextField( third );
        } else {
          this._setFocusedTextField( second );
        }
      } else if( this._focusedTextField === second ) {
        if( first.isSeeable() ) {
          this._setFocusedTextField( first );
        } else {
          this._setFocusedTextField( third );
        }
      } else if( this._focusedTextField === third ) {
        if( second.isSeeable() ) {
          this._setFocusedTextField( second );
        } else {
          this._setFocusedTextField( first );
        }
      }
    },

    _onKeyUp : function( evt ) {
      var keypress = evt.getKeyIdentifier();
      var value = this._focusedTextField.getText();
      value = this._removeLeadingZero( value );
      if( org.eclipse.swt.widgets.DateTimeDate._isNoModifierPressed( evt ) ) {
        switch( keypress ) {
          case "0": case "1": case "2": case "3": case "4":
          case "5": case "6": case "7": case "8": case "9":
            var maxChars = this._focusedTextField.getUserData( "maxLength" );
            if( this._focusedTextField === this._monthTextField ) {
              value = "" + this._monthInt;
              maxChars = 2;
            }
            var newValue = keypress;
            if( value.length < maxChars ) {
              newValue = value + keypress;
            }
            var intValue = parseInt( this._removeLeadingZero( newValue ) );
            if( this._focusedTextField === this._dayTextField ||
                this._focusedTextField === this._monthTextField ) {
              if( intValue >= this._spinner.getMin() &&
                  intValue <= this._spinner.getMax() ) {
                this._spinner.setValue( intValue );
              } else {
                // Do it again without adding the old value
                newValue = keypress;
                intValue = parseInt( newValue );
                if( intValue >= this._spinner.getMin() &&
                    intValue <= this._spinner.getMax() ) {
                  this._spinner.setValue( intValue );
                }
              }
            } else if( this._focusedTextField == this._yearTextField ) {
              this._focusedTextField.setText( newValue );
              if( newValue.length == 4 ) {
                this._checkAndApplyYearValue();
              }
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Home":
            var newValue = this._spinner.getMin();
            this._spinner.setValue( newValue );
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "End":
            var newValue = this._spinner.getMax();
            this._spinner.setValue( newValue );
            evt.preventDefault();
            evt.stopPropagation();
            break;
        }
      }
    },

    _getDaysInMonth : function() {
      var result = 31;
      var tmpMonth = this._monthInt - 1;
      var tmpYear = parseInt( this._yearTextField.getText() );
      var tmpDate = new Date();
      tmpDate.setYear( tmpYear );
      tmpDate.setMonth( tmpMonth );
      // Test 31
      tmpDate.setDate( 31 );
      if( tmpDate.getMonth() != tmpMonth ) {
        result = 30;
        tmpDate.setMonth( tmpMonth );
        // Test 30
        tmpDate.setDate( 30 );
        if( tmpDate.getMonth() != tmpMonth ) {
          result = 29;
          tmpDate.setMonth( tmpMonth );
          // Test 29
          tmpDate.setDate( 29 );
          if( tmpDate.getMonth() != tmpMonth ) {
            result = 28;
          }
        }
      }
      return result;
    },

    _setWeekday : function() {
      var tmpDate = new Date();
      tmpDate.setDate( parseInt( this._dayTextField.getText() ) );
      tmpDate.setMonth( this._monthInt - 1 );
      tmpDate.setFullYear( parseInt( this._yearTextField.getText() ) );
      this._weekdayTextField.setText( this._weekday[ tmpDate.getDay() + 1 ] );
    },

    _checkAndApplyYearValue : function() {
      var oldValue = this._lastValidYear;
      var value = parseInt( this._yearTextField.getText() );
      if( value >= 0 && value <= 29 ) {
        this._lastValidYear = 2000 + value;
      } else if( value >= 30 && value <= 99 ) {
        this._lastValidYear = 1900 + value;
      } else if( value >= 1752 ) {
        this._lastValidYear = value;
      }
      this._yearTextField.setText( "" + oldValue );
      if( oldValue != this._lastValidYear ) {
        this._spinner.setValue( this._lastValidYear );
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
        req.addParameter( id + ".day", this._removeLeadingZero( this._dayTextField.getText() ) );
        req.addParameter( id + ".month", this._monthInt - 1 );
        req.addParameter( id + ".year", this._lastValidYear );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
        this._readyToSendChanges = true;
      }
    },

    setMonth : function( value ) {
      this._monthInt = value + 1;
      if( this._medium ) {
        this._monthTextField.setText( this._addLeadingZero( this._monthInt ) );
      } else {
        this._monthTextField.setText( this._monthname[ this._monthInt - 1 ] );
      }
      if( this._focusedTextField === this._monthTextField ) {
        this._spinner.setValue( this._monthInt );
      }
      // Set the weekday
      this._setWeekday();
    },

    setDay : function( value ) {
      this._dayTextField.setText( this._addLeadingZero( value ) );
      if( this._focusedTextField === this._dayTextField ) {
        this._spinner.setValue( value );
      }
      // Set the weekday
      this._setWeekday();
    },

    setYear : function( value ) {
      this._lastValidYear = value;
      this._yearTextField.setText( "" + value );
      if( this._focusedTextField === this._yearTextField ) {
        this._spinner.setValue( value );
      }
      // Set the weekday
      this._setWeekday();
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setBounds : function( ind, x, y, width, height ) {
      var widget;
      switch( ind ) {
        case org.eclipse.swt.widgets.DateTimeDate.WEEKDAY_TEXTFIELD:
          widget = this._weekdayTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.DATE_TEXTFIELD:
          widget = this._dayTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.MONTH_TEXTFIELD:
          widget = this._monthTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.YEAR_TEXTFIELD:
          widget = this._yearTextField;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.WEEKDAY_MONTH_SEPARATOR:
          widget = this._separator0;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.MONTH_DATE_SEPARATOR:
          widget = this._separator1;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.DATE_YEAR_SEPARATOR:
          widget = this._separator2;
        break;
        case org.eclipse.swt.widgets.DateTimeDate.SPINNER:
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
