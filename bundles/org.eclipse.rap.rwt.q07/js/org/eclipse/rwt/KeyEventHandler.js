/*******************************************************************************
 *  Copyright: 2004-2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventHandler", {
  type : "static",

  statics : {
    _lastUpDownType : {},
    _lastKeyCode : null,
    
    init : function() {
      this.__onKeyEvent = qx.lang.Function.bind( this._onKeyEvent, this );  
    },
    
    cleanUp : function() {
      delete this.__onKeyEvent;
      delete this._lastUpDownType;
      delete this._lastKeyCode;      
    },

    _onKeyEvent : function() {
      var event = this._getDomEvent( arguments );
      var keyCode = this._getKeyCode( event );
      var charCode = this._getCharCode( event );
      var pseudoTypes = this._getEventPseudoTypes( event, keyCode, charCode );
      for( var i = 0; i < pseudoTypes.length; i++ ) {
        this._idealKeyHandler( keyCode, charCode, pseudoTypes[ i ], event );
      }
      this._saveData( event, keyCode, charCode  );
    },

    _getDomEvent : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( args ) {
        return args.length > 0 ? args[ 0 ] : window.event;
      },
      "default" : function( args ) {
        return args[ 0 ];
      }
    } ),

    _getKeyCode : qx.core.Variant.select( "qx.client", {
      "gecko" : function( event ) {
        return event.keyCode;
      },
      "opera" : function( event ) {
        var result;
        if( event.type === "keypress" ) {
          if( this._lastKeyCode === event.keyCode ) {
            result = event.keyCode;
          } else {
            // This is a printable "keypress", the keyCode is not relevant:
            result = 0;
          }
        } else {
          result = event.keyCode;
        }
        return result;
      },
      "default" : function( event ) {
        // the value in "keyCode" on "keypress" is actually the charcode: 
        return event.type !== "keypress" ? event.keyCode : 0;
      } 
    } ),

    _getCharCode : qx.core.Variant.select( "qx.client", {
      "default" : function( event ) {
        return event.charCode;
      },
      "mshtml" : function( event ) { 
        return event.type === "keypress" ? event.keyCode : 0;
      }, 
      "opera" : function( event ) {
        var result;
        if( event.type === "keypress" ) {
          if( this._lastKeyCode !== event.keyCode ) {
            result = event.keyCode;
          } else {
            // This is a non-printable "keypress"
            result = 0;
          }
        } else {
          result = 0;
        }
        return result;
      }
    } ),

    _isFirstKeyDown : function( keyCode ) {
      return this._lastUpDownType[ keyCode ] !== "keydown"
    },

    _getEventPseudoTypes : qx.core.Variant.select( "qx.client", {
      "default" : function( event, keyCode, charCode ) {
        var result;
        if( event.type === "keydown" ) { 
          var printable = !this._isNonPrintableKeyCode( keyCode );
          if( this._isFirstKeyDown( keyCode ) ) {
            // add a "keypress" for non-printable keys:
            result = printable ? [ "keydown" ] : [ "keydown", "keypress" ];             
          } else {
            // convert printable "keydown" to "keypress", suppress other:
            result = printable ? [] : [ "keypress" ]; 
          }
        } else {
          result = [ event.type ];
        }
        return result;
      },
      "gecko" : function( event, keyCode, charCode ) {
        var result;
        if(    event.type === "keydown" 
            && !this._isFirstKeyDown( keyCode )  
        ) {
          // suppress unwanted "keydown":
          result = []; 
        } else {
          result = [ event.type ];
        }
        return result; 
      },
      "opera" : function( event, keyCode, charCode ) {
        return [ event.type ]; 
      }
    } ),

    _saveData : function( event, keyCode, charCode ) {
      if( event.type !== "keypress" ) {
        this._lastUpDownType[ keyCode ] = event.type;
        this._lastKeyCode = keyCode;
      }
    },

    _isNonPrintableKeyCode : function( keyCode ) {
      return this._keyCodeToIdentifierMap[keyCode] ? true : false;
    },

    _keyCodeToIdentifier : function( keyCode ) {
      var result = "Unidentified";
      if( this._numpadToCharCode[ keyCode ] !== undefined ) {
        result = String.fromCharCode( this._numpadToCharCode[ keyCode ] );
      } else if( this._keyCodeToIdentifierMap[ keyCode ] !== undefined ) {
        result = this._keyCodeToIdentifierMap[ keyCode ];
      } else if( this._specialCharCodeMap[ keyCode ] !== undefined ) {
        result = this._specialCharCodeMap[ keyCode ];
      } else if( keyCode >= this._charCodeA && keyCode <= this._charCodeZ ) {
        result = String.fromCharCode( keyCode );
      }
      return result;
    },

    _charCodeToIdentifier : function( charCode ) {
      var result;
      if( this._specialCharCodeMap[ charCode ] !== undefined ) {
        result = this._specialCharCodeMap[ charCode ];
      } else {
        result = String.fromCharCode( charCode ).toUpperCase();
      }
      return result;
    },

    _idealKeyHandler : function( keyCode, charCode, eventType, domEvent ) {
      var util = org.eclipse.rwt.KeyEventUtil.getInstance();
      if( !util.intercept( eventType, keyCode, charCode, domEvent ) ) {
        var keyIdentifier;
        if( keyCode ) {
          keyIdentifier = this._keyCodeToIdentifier(keyCode);
        } else {
          keyIdentifier = this._charCodeToIdentifier(charCode);
        }
        var eventHandler = org.eclipse.rwt.EventHandler;
        eventHandler._onkeyevent_post( domEvent, 
                                       eventType, 
                                       keyCode, 
                                       charCode, 
                                       keyIdentifier);
      }
    },
    
    ///////////////
    // Helper-maps:

    _specialCharCodeMap : {
      13  : "Enter", 
      27  : "Escape", 
      32 : "Space" 
    },

    _keyCodeToIdentifierMap : {
      8   : "Backspace", 
      9   : "Tab",
      16  : "Shift",
      17  : "Control",
      18  : "Alt",
      20  : "CapsLock",
      224 : "Meta",
      37  : "Left",
      38  : "Up",
      39  : "Right",
      40  : "Down",
      33  : "PageUp",
      34  : "PageDown",
      35  : "End",
      36  : "Home",
      45  : "Insert",
      46  : "Delete",
      112 : "F1",
      113 : "F2",
      114 : "F3",
      115 : "F4",
      116 : "F5",
      117 : "F6",
      118 : "F7",
      119 : "F8",
      120 : "F9",
      121 : "F10",
      122 : "F11",
      123 : "F12",
      144 : "NumLock",
      44  : "PrintScreen",
      145 : "Scroll",
      19  : "Pause",
      91  : "Win", // The Windows Logo key
      93  : "Apps" // The Application key (Windows Context Menu)
    },

    /** maps the keycodes of the numpad keys to the right charcodes */
    _numpadToCharCode : {
      96  : "0".charCodeAt( 0 ),
      97  : "1".charCodeAt( 0 ),
      98  : "2".charCodeAt( 0 ),
      99  : "3".charCodeAt( 0 ),
      100 : "4".charCodeAt( 0 ),
      101 : "5".charCodeAt( 0 ),
      102 : "6".charCodeAt( 0 ),
      103 : "7".charCodeAt( 0 ),
      104 : "8".charCodeAt( 0 ),
      105 : "9".charCodeAt( 0 ),
      106 : "*".charCodeAt( 0 ),
      107 : "+".charCodeAt( 0 ),
      109 : "-".charCodeAt( 0 ),
      110 : ",".charCodeAt( 0 ),
      111 : "/".charCodeAt( 0 )
    },

    _charCodeA : "A".charCodeAt( 0 ),
    _charCodeZ : "Z".charCodeAt( 0 ),
    _charCode0 : "0".charCodeAt( 0 ),
    _charCode9 : "9".charCodeAt( 0 )

  }

} );
