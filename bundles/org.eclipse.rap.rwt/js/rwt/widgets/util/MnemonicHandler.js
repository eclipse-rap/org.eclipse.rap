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
  extend : rwt.qx.Object,

  construct : function() {
    this.base( arguments );
    this._map = {};
    this._activator = null;
    this._active = false;
  },

  members : {

    add : function( widget, listener ) {
      this._map[ widget.toHashCode() ] = [ widget, listener ];
    },

    remove : function( widget ) {
      delete this._map[ widget.toHashCode() ];
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
      } else if( this._isDeactivation( eventType, keyCode, charCode, domEvent ) ) {
        this.deactivate();
      } else if( this._isTrigger( eventType, keyCode, charCode, domEvent ) ) {
        this.trigger( keyCode );
      }
    },

    activate : function() {
      this._active = true;
      this._fire( { "type" : "show" } );
    },

    deactivate : function() {
      this._active = false;
      this._fire( { "type" : "hide" } );
    },

    trigger : function( charCode ) {
      this._fire( {
        "type" : "trigger",
        "charCode" : charCode
      } );
    },

    _fire : function( event ) {
      for( var key in this._map ) {
        var entry = this._map[ key ];
        entry[ 1 ].call( entry[ 0 ], event );
      }
    },

    _isActivation : function( eventType, keyCode, charCode, domEvent ) {
      return    this._activator
             && eventType === "keydown"
             && EventHandlerUtil.isModifier( keyCode )
             && this._isActivatorCombo( domEvent );
    },

    _isDeactivation : function( eventType, keyCode, charCode, domEvent ) {
      return this._active && !this._isActivatorCombo( domEvent );
    },

    _isActivatorCombo : function( domEvent ) {
      return    this._activator.ctrlKey === domEvent.ctrlKey
             && this._activator.altKey === domEvent.altKey
             && this._activator.shiftKey === domEvent.shiftKey;
    },

    _isTrigger : function( eventType, keyCode, charCode, domEvent ) {
      var isChar = !isNaN( keyCode ) && rwt.event.EventHandlerUtil.isAlphaNumericKeyCode( keyCode );
      return this._active && eventType === "keydown" && isChar;
     }

  }

} );

}());