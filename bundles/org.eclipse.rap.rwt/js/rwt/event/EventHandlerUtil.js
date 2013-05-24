/*******************************************************************************
 *  Copyright: 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define( "rwt.event.EventHandlerUtil", {
  type : "static",

  statics : {
    // TODO [tb] : integrate rwt.html.EventRegistration
    _lastUpDownType : {},
    _lastKeyCode : null,

    cleanUp : function() {
      delete this.__onKeyEvent;
      delete this._lastUpDownType;
      delete this._lastKeyCode;
    },

    applyBrowserFixes  : rwt.util.Variant.select( "qx.client", {
      "gecko" : function() {
        // Fix for bug 295475:
        // Prevent url-dropping in FF as a whole (see bug 304651)
        var doc = rwt.widgets.base.ClientDocument.getInstance();
        doc.getElement().setAttribute( "ondrop", "event.preventDefault();" );
        var docElement = document.documentElement;
        // also see ErrorHandler.js#_enableTextSelection
        this._ffMouseFixListener = function( event ) {
          var tagName = null;
          try{
            tagName = event.originalTarget.tagName;
          } catch( e ) {
            // Firefox bug: On the very first mousedown, access to the events target
            // is forbidden and causes an error.
          }
          // NOTE: See also Bug 321372
          if( event.button === 0 && tagName != null && tagName != "INPUT" ) {
            event.preventDefault();
          }
        };
        rwt.html.EventRegistration.addEventListener( docElement,
                                                    "mousedown",
                                                    this._ffMouseFixListener );
      },
      "default" : function() { }
    } ),

    /////////////////////////
    // GENERAL EVENT HANDLING

    getDomEvent : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( args ) {
        return args.length > 0 ? args[ 0 ] : window.event;
      },
      "default" : function( args ) {
        return args[ 0 ];
      }
    } ),

    getDomTarget : rwt.util.Variant.select("qx.client", {
      "mshtml" : function( vDomEvent ) {
        return vDomEvent.target || vDomEvent.srcElement;
      },
      "webkit" : function( vDomEvent ) {
        var vNode = vDomEvent.target || vDomEvent.srcElement;
        // Safari takes text nodes as targets for events
        if( vNode && ( vNode.nodeType == rwt.html.Nodes.TEXT ) ) {
          vNode = vNode.parentNode;
        }
        return vNode;
      },
      "default" : function( vDomEvent ) {
        return vDomEvent.target;
      }
    } ),

    stopDomEvent : function( vDomEvent ) {
      vDomEvent._prevented = true;
      if( vDomEvent.preventDefault ) {
        vDomEvent.preventDefault();
      }
      try {
        // this allows us to prevent some key press events in IE and Firefox.
        // See bug #1049
        vDomEvent.keyCode = 0;
      } catch( ex ) {
        // do nothing
      }
      vDomEvent.returnValue = false;
    },

    wasStopped : function( domEvent ) {
      return domEvent._prevented ? true : false;
    },


    blockUserDomEvents : function( element, value ) {
      var eventUtil = rwt.html.EventRegistration;
      if( value ) {
        for( var i = 0; i < this._userEventTypes.length; i++ ) {
          eventUtil.addEventListener( element, this._userEventTypes[ i ], this._domEventBlocker );
        }
      } else {
        for( var i = 0; i < this._userEventTypes.length; i++ ) {
          eventUtil.removeEventListener( element, this._userEventTypes[ i ], this._domEventBlocker );
        }
      }
    },

    _userEventTypes : [
      "mouseover",
      "mousemove",
      "mouseout",
      "mousedown",
      "mouseup",
      "click",
      "dblclick",
      "contextmenu",
      ( rwt.client.Client.isGecko() ? "DOMMouseScroll" : "mousewheel" ),
      "keydown",
      "keypress",
      "keyup"
    ],

    _domEventBlocker : function( event ) {
      rwt.event.EventHandlerUtil.stopDomEvent( event );
      event.cancelBubble = true; // MSIE
      if( event.stopPropagation ) {
        event.stopPropagation();
      }
    },

    // BUG: http://xscroll.mozdev.org/
    // If your Mozilla was built with an option `--enable-default-toolkit=gtk2',
    // it can not return the correct event target for DOMMouseScroll.
    getOriginalTargetObject : function( vNode ) {
      // Events on the HTML element, when using absolute locations which
      // are outside the HTML element. Opera does not seem to fire events
      // on the HTML element.
      if( vNode == document.documentElement ) {
        vNode = document.body;
      }
      // Walk up the tree and search for an rwt.widgets.base.Widget
      try {
        while( vNode != null && vNode.rwtWidget == null )       {
          vNode = vNode.parentNode;
        }
      } catch( vDomEvent ) {
        vNode = null;
      }
      return vNode ? vNode.rwtWidget : null;
    },

    getOriginalTargetObjectFromEvent : function( vDomEvent, vWindow ) {
      var vNode = this.getDomTarget( vDomEvent );
      // Especially to fix key events.
      // 'vWindow' is the window reference then
      if( vWindow ) {
        var vDocument = vWindow.document;
        if(    vNode == vWindow
            || vNode == vDocument
            || vNode == vDocument.documentElement
            || vNode == vDocument.body )
        {
          return vDocument.body.rwtWidget;
        }
      }
      return this.getOriginalTargetObject( vNode );
    },

    getRelatedTargetObjectFromEvent : function( vDomEvent ) {
      var EventHandlerUtil = rwt.event.EventHandlerUtil;
      var target = vDomEvent.relatedTarget;
      if( !target ) {
        if( vDomEvent.type == "mouseover" ) {
          target = vDomEvent.fromElement;
        } else {
          target = vDomEvent.toElement;
        }
      }
      return EventHandlerUtil.getTargetObject( target );
    },

    getTargetObject : function( vNode, vObject, allowDisabled ) {
      if( !vObject ) {
        var vObject = this.getOriginalTargetObject( vNode );
        if (!vObject) {
          return null;
        }
      }
      while( vObject ) {
        if( !allowDisabled && !vObject.getEnabled() ) {
          return null;
        }
        if( !vObject.getAnonymous() ) {
          break;
        }
        vObject = vObject.getParent();
      }
      return vObject;
    },

    ///////////////
    // KEY HANDLING

    getKeyCode : rwt.util.Variant.select( "qx.client", {
      "gecko" : function( event ) {
        return event.keyCode;
      },
      "default" : function( event ) {
        // the value in "keyCode" on "keypress" is actually the charcode:
        var hasKeyCode = event.type !== "keypress" || event.keyCode === 13 || event.keyCode === 27;
        return hasKeyCode ? event.keyCode : 0;
      }
    } ),

    getCharCode : rwt.util.Variant.select( "qx.client", {
      "default" : function( event ) {
        var hasCharCode = event.type === "keypress" && event.keyCode !== 13 && event.keyCode !== 27;
        return hasCharCode ? event.charCode : 0;
      },
      "mshtml|newmshtml|opera" : function( event ) {
        var hasCharCode = event.type === "keypress" && event.keyCode !== 13 && event.keyCode !== 27;
        return hasCharCode ? event.keyCode : 0;
      }
    } ),

    _isFirstKeyDown : function( keyCode ) {
      return this._lastUpDownType[ keyCode ] !== "keydown";
    },

    getEventPseudoTypes : rwt.util.Variant.select( "qx.client", {
      "default" : function( event, keyCode, charCode ) {
        var result;
        if( event.type === "keydown" ) {
          var printable = !this.isNonPrintableKeyCode( keyCode );
          if( this._isFirstKeyDown( keyCode ) ) {
            // add a "keypress" for non-printable keys:
            result = printable ? [ "keydown" ] : [ "keydown", "keypress" ];
          } else {
            // convert non-printable "keydown" to "keypress", suppress other:
            result = printable ? [] : [ "keypress" ];
          }
        } else {
          result = [ event.type ];
        }
        return result;
      },
      "gecko" : function( event, keyCode, charCode ) {
        var result;
        if( event.type === "keydown" && this.isModifier( keyCode ) ) {
          if( this._isFirstKeyDown( keyCode ) ) {
            result = [ "keydown", "keypress" ];
          } else {
            result = [ "keypress" ];
          }
        } else {
          if( event.type === "keydown" && !this._isFirstKeyDown( keyCode ) ) {
            // suppress unwanted "keydown":
            result = [];
          } else {
            result = [ event.type ];
          }
        }
        return result;
      }
    } ),


    mustRestoreKeyup  : function( keyCode, pseudoTypes  ) {
      // For these keys it is assumed to be more likely that a keyup event was missed
      // than the key being hold down while another key is pressed.
      var result = [];
      if( pseudoTypes[ 0 ] === "keydown" ) {
        if( !this._isFirstKeyDown( 93 ) && keyCode !== 93 ) {
          result.push( 93 );
        }
      }
      return result;
    },

    mustRestoreKeypress  : rwt.util.Variant.select( "qx.client", {
      "default" : function( event, pseudoTypes ) {
        var result = false;
        if( this.wasStopped( event ) ) {
          result =    ( pseudoTypes.length === 1 && pseudoTypes[ 0 ] === "keydown" )
                   || pseudoTypes.length === 0;
        }
        return result;
      },
      "gecko" : function( event, pseudoTypes ) {
        return false;
      }
    } ),

    saveData : function( event, keyCode, charCode ) {
      if( event.type !== "keypress" ) {
        this._lastUpDownType[ keyCode ] = event.type;
        this._lastKeyCode = keyCode;
      }
    },

    clearStuckKey : function( keyCode ) {
      this._lastUpDownType[ keyCode ] = "keyup";
    },

    keyCodeToIdentifier : function( keyCode ) {
      var result = "Unidentified";
      if( this._numpadToCharCode[ keyCode ] !== undefined ) {
        result = String.fromCharCode( this._numpadToCharCode[ keyCode ] );
      } else if( this._keyCodeToIdentifierMap[ keyCode ] !== undefined ) {
        result = this._keyCodeToIdentifierMap[ keyCode ];
      } else if( this._specialCharCodeMap[ keyCode ] !== undefined ) {
        result = this._specialCharCodeMap[ keyCode ];
      } else if( this.isAlphaNumericKeyCode( keyCode ) ) {
        result = String.fromCharCode( keyCode );
      }
      return result;
    },

    charCodeToIdentifier : function( charCode ) {
      var result;
      if( this._specialCharCodeMap[ charCode ] !== undefined ) {
        result = this._specialCharCodeMap[ charCode ];
      } else {
        result = String.fromCharCode( charCode ).toUpperCase();
      }
      return result;
    },

    isNonPrintableKeyCode  : rwt.util.Variant.select( "qx.client", {
      "default" : function( keyCode ) {
        return this._keyCodeToIdentifierMap[ keyCode ] ? true : false;
      },
      "webkit" : function( keyCode ) {
        return ( this._keyCodeToIdentifierMap[ keyCode ] || keyCode === 27 ) ? true : false;
      }
    } ),

    isSpecialKeyCode : function( keyCode ) {
      return this._specialCharCodeMap[ keyCode ] ? true : false;
    },

    isModifier : function( keyCode ) {
      return keyCode >= 16 && keyCode <= 20 && keyCode !== 19;
    },

    isAlphaNumericKeyCode : function( keyCode ) {
      var result = false;
      if(    ( keyCode >= this._charCodeA && keyCode <= this._charCodeZ )
          || ( keyCode >= this._charCode0 && keyCode <= this._charCode9 ) )
      {
        result = true;
      }
      return result;
    },

    /**
     * Determines if this key event should be blocked if key events are disabled
     */
    shouldBlock : function( type, keyCode, charCode, event ) {
      var result = true;
      var keyIdentifier;
      if( !isNaN( keyCode ) && keyCode !== 0 ) {
        keyIdentifier = this.keyCodeToIdentifier( keyCode );
      } else {
        keyIdentifier = this.charCodeToIdentifier( charCode );
      }
      if( this._nonBlockableKeysMap[ keyIdentifier ] || event.altKey ) {
        result = false;
      } else if( event.ctrlKey ) {
        // block only those combos that are used for text editing:
        result = this._blockableCtrlKeysMap[ keyIdentifier ] === true;
      }
      return result;
    },

    ///////////////
    // Helper-maps:

    _specialCharCodeMap : {
      13  : "Enter",
      27  : "Escape",
      32 : "Space"
    },

    _nonBlockableKeysMap : {
      "Control" : true,
      "Alt" : true,
      "Shift" : true,
      "Meta" : true,
      "Win" : true,
      "F1" : true,
      "F2" : true,
      "F3" : true,
      "F4" : true,
      "F5" : true,
      "F6" : true,
      "F7" : true,
      "F8" : true,
      "F9" : true,
      "F10" : true,
      "F11" : true,
      "F12" : true
    },

    _blockableCtrlKeysMap : {
      "F" : true,
      "A" : true,
      "C" : true,
      "V" : true,
      "X" : true,
      "Z" : true,
      "Y" : true
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
