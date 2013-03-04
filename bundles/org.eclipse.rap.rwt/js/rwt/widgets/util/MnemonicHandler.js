/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var EventHandlerUtil = rwt.event.EventHandlerUtil;

rwt.qx.Class.define( "rwt.widgets.util.MnemonicHandler", {

  type : "singleton",
  extend : rwt.qx.Target,

  construct : function() {
    this.base( arguments );
    this._map = {};
    this._activator = null;
    this._active = false;
  },

  members : {

    add : function( widget, listener ) {
      this._map[ widget.toHashCode() ] = [ widget, listener ];
      this.addEventListener( "mnemonic", listener, widget );
    },

    remove : function( widget ) {
      if( this._map[ widget.toHashCode() ] ) {
        var listener = this._map[ widget.toHashCode() ][ 1 ];
        this.removeEventListener( "mnemonic", listener, widget );
        delete this._map[ widget.toHashCode() ];
      }
    },

    setActivator : function( str ) {
      if( str ) {
        this._activator = {};
        this._activator.ctrlKey = str.indexOf( "CTRL" ) !== -1;
        this._activator.altKey = str.indexOf( "ALT" ) !== -1;
        this._activator.shiftKey = str.indexOf( "SHIFT" ) !== -1;
      } else {
        this._activator = null;
      }
    },

    handleKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      if( this._isActivation( eventType, keyCode, charCode, domEvent ) ) {
        this.activate();
      } else if( this._isTrigger( eventType, keyCode, charCode, domEvent ) ) {
        this.trigger( keyCode );
      }
    },

    activate : function() {
      this._active = true;
      this.dispatchSimpleEvent( "mnemonic", {
        "type" : "show"
      } );
    },

    deactivate : function() {
      this._active = false;
    },

    trigger : function( charCode ) {
      this.dispatchSimpleEvent( "mnemonic", {
        "type" : "trigger",
        "charCode" : charCode
      } );
    },

    _isActivation : function( eventType, keyCode, charCode, domEvent ) {
      var result = false;
      if( this._activator && eventType === "keydown" && EventHandlerUtil.isModifier( keyCode ) ) {
        result =    this._activator.ctrlKey === domEvent.ctrlKey
                 && this._activator.altKey === domEvent.altKey
                 && this._activator.shiftKey === domEvent.shiftKey;
      }
      return result;
    },

    _isTrigger : function( eventType, keyCode, charCode, domEvent ) {
      var isChar = !isNaN( keyCode ) && rwt.event.EventHandlerUtil.isAlphaNumericKeyCode( keyCode );
      return this._active && eventType === "keydown" && isChar;
     }

  }

} );

}());