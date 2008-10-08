/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.DateTimeDate", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style, monthNames, weekdayNames, 
                        dateSeparator, datePattern ) {
    this.base( arguments );
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
    
    // Add listeners for font, background and foregraund color change
    this.addEventListener( "changeFont", this._rwt_onChangeFont, this );
    this.addEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
    this.addEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
    
    // Background color
    this._backgroundColor = "white";
    // Foreground color
    this._foregroundColor = "black";
    
    // Focused text field
    this._focusedTextField = null;    
    // Weekday
    this._weekdayTextField = new qx.ui.form.TextField;
    this._weekdayTextField.set({  
      textAlign: "center",
      selectable: false,
      readOnly: true,
      border: null,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    if( this._long ) {
      this.add( this._weekdayTextField ); 
    }
    this._weekdayTextField.addEventListener( "keypress", this._onKeyPress, this );
    this._weekdayTextField.addEventListener( "contextmenu", this._onContextMenu, this );
    // Separator
    this._separator0 = new qx.ui.basic.Label(",");        
    this._separator0.set({
      paddingTop: 3,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    this._separator0.addEventListener( "contextmenu", this._onContextMenu, this );
    if( this._long ) {
      this.add(this._separator0);
    }    
    // Month       
    this._monthTextField = new qx.ui.form.TextField;
    this._monthTextField.set({        
      textAlign: this._medium ? "right" : "center",
      selectable: false,
      readOnly: true,
      border: null,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    // Integer value of the month 
    this._monthInt = 1;
    if( this._medium ) {
      this._monthTextField.setValue( this._monthInt );
    } else {   
      this._monthTextField.setValue( this._monthname[ this._monthInt - 1 ] );
    }
    this._monthTextField.addEventListener( "click",  this._onClick, this ); 
    this._monthTextField.addEventListener( "keypress", this._onKeyPress, this );
    this._monthTextField.addEventListener( "keyup", this._onKeyUp, this ); 
    this._monthTextField.addEventListener( "contextmenu", this._onContextMenu, this );
    this.add( this._monthTextField );
    // Separator 
    this._separator1 = new qx.ui.basic.Label( dateSeparator );        
    this._separator1.set({
      paddingTop: 3,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    this._separator1.addEventListener( "contextmenu", this._onContextMenu, this );
    if( this._medium ) {
      this.add(this._separator1);
    }   
    // Date     
    this._dayTextField = new qx.ui.form.TextField;
    this._dayTextField.set({
      maxLength: 2,
      textAlign: "right",
      selectable: false,
      readOnly: true,       
      border: null,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    this._dayTextField.setValue( 1 );
    this._dayTextField.addEventListener( "click",  this._onClick, this );
    this._dayTextField.addEventListener( "keypress", this._onKeyPress, this );
    this._dayTextField.addEventListener( "keyup", this._onKeyUp, this );  
    this._dayTextField.addEventListener( "contextmenu", this._onContextMenu, this );  
    if( !this._short ) {
      this.add( this._dayTextField );
    }
    // Separator
    this._separator2 = new qx.ui.basic.Label( "," );        
    this._separator2.set({
      paddingTop: 3,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    if( this._medium ) {
      this._separator2.setText( dateSeparator );
    }
    this._separator2.addEventListener( "contextmenu", this._onContextMenu, this );  
    this.add(this._separator2);     
    // Year    
    this._yearTextField = new qx.ui.form.TextField;  
    this._yearTextField.set({        
      maxLength: 4,
      textAlign: "right",
      selectable: false,
      readOnly: true,
      border: null,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    }); 
    // Last valid year
    this._lastValidYear = 1970;
    this._yearTextField.setValue( this._lastValidYear );
    this._yearTextField.addEventListener( "click",  this._onClick, this );
    this._yearTextField.addEventListener( "focusout",  this._onFocusOut, this );
    this._yearTextField.addEventListener( "keypress", this._onKeyPress, this );
    this._yearTextField.addEventListener( "keyup", this._onKeyUp, this ); 
    this._yearTextField.addEventListener( "contextmenu", this._onContextMenu, this );  
    this.add( this._yearTextField ); 
    // Spinner 
    this._spinner = new qx.ui.form.Spinner;
    this._spinner.set({
      wrap: true,
      border: null,
      backgroundColor: this._backgroundColor,
      textColor : this._foregroundColor
    });
    this._spinner.setMin( 1 ); 
    this._spinner.setMax( 12 );        
    this._spinner.setValue( this._monthInt );
    this._spinner.addEventListener( "change",  this._onSpinnerChange, this );
    this._spinner.addEventListener( "mousedown",  this._onSpinnerMouseDown, this );
    this._spinner.addEventListener( "mouseup",  this._onSpinnerMouseUp, this ); 
    this._spinner.addEventListener( "keypress", this._onKeyPress, this );
    this._spinner.addEventListener( "keyup", this._onKeyUp, this );
    this.add( this._spinner );
    // Set the default focused text field
    this._focusedTextField = this._monthTextField;  
    // Set the weekday
    this._setWeekday();
      
  },

  destruct : function() {
    this.removeEventListener( "changeFont", this._rwt_onChangeFont, this );
    this.removeEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
    this.removeEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
    this._weekdayTextField.removeEventListener( "keypress", this._onKeyPress, this );
    this._weekdayTextField.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._monthTextField.removeEventListener( "click",  this._onClick, this ); 
    this._monthTextField.removeEventListener( "keypress", this._onKeyPress, this );
    this._monthTextField.removeEventListener( "keyup", this._onKeyUp, this );
    this._monthTextField.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._dayTextField.removeEventListener( "click",  this._onClick, this );
    this._dayTextField.removeEventListener( "keypress", this._onKeyPress, this );
    this._dayTextField.removeEventListener( "keyup", this._onKeyUp, this );
    this._dayTextField.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._yearTextField.removeEventListener( "click",  this._onClick, this );
    this._yearTextField.removeEventListener( "focusout",  this._onFocusOut, this );
    this._yearTextField.removeEventListener( "keypress", this._onKeyPress, this );
    this._yearTextField.removeEventListener( "keyup", this._onKeyUp, this );
    this._yearTextField.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._spinner.removeEventListener( "change",  this._onSpinnerChange, this ); 
    this._spinner.removeEventListener( "mousedown",  this._onSpinnerMouseDown, this ); 
    this._spinner.removeEventListener( "mouseup",  this._onSpinnerMouseUp, this ); 
    this._spinner.removeEventListener( "keypress", this._onKeyPress, this );
    this._spinner.removeEventListener( "keyup", this._onKeyUp, this ); 
    this._separator0.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._separator1.removeEventListener( "contextmenu", this._onContextMenu, this );
    this._separator2.removeEventListener( "contextmenu", this._onContextMenu, this );
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
    _rwt_onChangeFont : function( evt ) {
      var value = evt.getValue();
      this._weekdayTextField.setFont( value );
      this._dayTextField.setFont( value );
      this._monthTextField.setFont( value );
      this._yearTextField.setFont( value );
    },
    
    _rwt_onChangeTextColor : function( evt ) {
      var value = evt.getValue();
      this._foregroundColor = value;
      this._weekdayTextField.setTextColor( value );
      this._dayTextField.setTextColor( value );
      this._monthTextField.setTextColor( value );
      this._yearTextField.setTextColor( value ); 
      this._separator0.setTextColor( value );
      this._separator1.setTextColor( value );
      this._separator2.setTextColor( value );     
    },
    
    _rwt_onChangeBackgoundColor : function( evt ) {
      var value = evt.getValue();
      this._backgroundColor = value;
      this._weekdayTextField.setBackgroundColor( value );
      this._dayTextField.setBackgroundColor( value );
      this._monthTextField.setBackgroundColor( value );
      this._yearTextField.setBackgroundColor( value );
      this._separator0.setBackgroundColor( value );
      this._separator1.setBackgroundColor( value );
      this._separator2.setBackgroundColor( value );
      this._spinner.setBackgroundColor( value );
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
    
    _onClick : function( evt ) {           
      this._setFocusedTextField( evt.getTarget() );
    },
    
    _setFocusedTextField :  function( textField ) {
      var tmpValue;
      this._focusedTextField.setBackgroundColor( this._backgroundColor );
      this._focusedTextField.setTextColor( this._foregroundColor );
      // Set focused text field to null
      this._focusedTextField = null;
      if( textField === this._dayTextField ) {
        this._spinner.setMin( 1 );            
        this._spinner.setMax( this._getDaysInMonth() ); 
        tmpValue = this._removeLeadingZero( this._dayTextField.getValue() );
        this._spinner.setValue( parseInt( tmpValue ) );
      } else if( textField === this._monthTextField ) { 
        this._spinner.setMin( 1 );           
        this._spinner.setMax( 12 );
        this._spinner.setValue( this._monthInt );
      } else if( textField === this._yearTextField ) {
        this._spinner.setMin( 1752 );                        
        this._spinner.setMax( 9999 );                            
        this._spinner.setValue( this._lastValidYear );
      }
      // Set focused text field
      this._focusedTextField = textField;
      // Set highlight on focused text field
      this._focusedTextField.setBackgroundColor( "#0A246A" );
      this._focusedTextField.setTextColor( "white" );
      // Request focus
      this._focusedTextField.setFocused( true );
    },
    
    _onFocusOut : function( evt ) {
      if( evt.getTarget() === this._yearTextField ) {
        this._checkAndApplyYearValue();
      }     
    }, 
    
    _onSpinnerChange : function( evt ) {          
      if( this._focusedTextField != null ) {        
        var oldValue = this._focusedTextField.getValue();
        // Set the value
        if( this._focusedTextField === this._monthTextField ) {
          this._monthInt = this._spinner.getValue();
          if( this._medium ) {
            this._focusedTextField.setValue( this._addLeadingZero( this._monthInt ) );            
          } else {   
            this._focusedTextField.setValue( this._monthname[ this._monthInt - 1 ] );
          }          
        } else if( this._focusedTextField === this._yearTextField ) {
            this._lastValidYear = this._spinner.getValue();
            this._focusedTextField.setValue( this._spinner.getValue() );
        } else {
          this._focusedTextField.setValue( this._addLeadingZero( this._spinner.getValue() ) );
        }
        // Adjust date field
        if( this._focusedTextField == this._monthTextField || // month
            this._focusedTextField == this._yearTextField ) { // year          
          var dateValue = this._dayTextField.getValue();
          if( dateValue > this._getDaysInMonth() ) {
            this._dayTextField.setValue( this._getDaysInMonth() );
          }
        }  
        // Set the weekday field
        this._setWeekday();
        
        var newValue = this._focusedTextField.getValue();
        if( oldValue != newValue && this._readyToSendChanges ) {
          this._readyToSendChanges = false;
          // Send changes
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },
    
    _onSpinnerMouseDown : function( evt ) {
      // Set highlight on focused text field
      this._focusedTextField.setBackgroundColor( "#0A246A" );
      this._focusedTextField.setTextColor( "white" );      
    },
    
    _onSpinnerMouseUp : function( evt ) {      
      this._focusedTextField.setFocused( true );
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
      var value = this._focusedTextField.getComputedValue();
      value = this._removeLeadingZero( value );
      if( org.eclipse.swt.widgets.DateTimeDate._isNoModifierPressed( evt ) ) {
        switch( keypress ) {
          case "Tab":         
            this._focusedTextField.setBackgroundColor( this._backgroundColor );
            this._focusedTextField.setTextColor( this._foregroundColor );
            break;
          case "0": case "1": case "2": case "3": case "4":
          case "5": case "6": case "7": case "8": case "9":
            this._focusedTextField.setFocused( true );          
            var maxChars = this._focusedTextField.getMaxLength(); 
            if( this._focusedTextField === this._monthTextField ) {
              value = "" + this._monthInt;
              maxChars = 2;
            }  
            var newValue = keypress;    
            if( value.length < maxChars ) {
              newValue = value + keypress;
            } 
            var intValue = parseInt( newValue );  
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
              this._focusedTextField.setValue( newValue );
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
    
    _getDaysInMonth : function() {
      var result = 31;
      var tmpMonth = this._monthInt - 1;
      var tmpYear = parseInt( this._yearTextField.getValue() );
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
      tmpDate.setDate( parseInt( this._dayTextField.getValue() ) );
      tmpDate.setMonth( this._monthInt - 1 );
      tmpDate.setFullYear( parseInt( this._yearTextField.getValue() ) );
      this._weekdayTextField.setValue( this._weekday[ tmpDate.getDay() + 1 ] );
    },
    
    _checkAndApplyYearValue : function() {       
      var oldValue = this._lastValidYear;     
      var value = parseInt( this._yearTextField.getValue() );        
      if( value >= 0 && value <= 29 ) {
        this._lastValidYear = 2000 + value;
      } else if( value >= 30 && value <= 99 ) {
        this._lastValidYear = 1900 + value;
      } else if( value >= 1752 ) {
        this._lastValidYear = value;
      }
      if( oldValue != this._lastValidYear ) {
        this._spinner.setValue( this._lastValidYear );
      }
    },
    
    _addLeadingZero : function( value ) {
      return value < 10 ? "0" + value : value;
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
        req.addParameter( id + ".day", this._removeLeadingZero( this._dayTextField.getValue() ) );
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
        this._monthTextField.setValue( this._addLeadingZero( this._monthInt ) );
      } else {   
        this._monthTextField.setValue( this._monthname[ this._monthInt - 1 ] );
      }
      if( this._focusedTextField === this._monthTextField ) {
        this._spinner.setValue( this._monthInt );
      }
      // Set the weekday
      this._setWeekday();
    },
    
    setDay : function( value ) {
      this._dayTextField.setValue( this._addLeadingZero( value ) );
      if( this._focusedTextField === this._dayTextField ) {
        this._spinner.setValue( value );
      }
      // Set the weekday
      this._setWeekday();
    },
    
    setYear : function( value ) {
      this._lastValidYear = value;
      this._yearTextField.setValue( value );
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
