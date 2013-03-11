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
    this._active = null;
  },

  members : {

    add : function( widget, listener ) {
      var root = widget.getFocusRoot();
      this._registerFocusRoot( root );
      this._map[ root.toHashCode() ][ widget.toHashCode() ] = [ widget, listener ];
    },

    remove : function( widget ) {
      // NOTE: The focus root may be gone if the widget is in dispose, therefore we have to search:
      for( var key in this._map ) {
        delete this._map[ key ][ widget.toHashCode() ];
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

    isActive : function() {
      return this._active != null;
    },

    handleKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      var result = false;
      if( this._isActivation( eventType, keyCode, charCode, domEvent ) ) {
        this.activate();
      } else if( this._isDeactivation( eventType, keyCode, charCode, domEvent ) ) {
        this.deactivate();
      } else if( this._isTrigger( eventType, keyCode, charCode, domEvent ) ) {
        result = this.trigger( keyCode );
      }
      return result;
    },

    activate : function() {
      if( this._noMenuOpen() ) {
        // TODO : The Active shell is not always the focus root - why?
        //var root = rwt.event.EventHandler.getFocusRoot();
        var root = rwt.widgets.base.Window.getDefaultWindowManager().getActiveWindow();
        if( root == null ) {
          root = rwt.widgets.base.ClientDocument.getInstance();
        }
        this._active = root.toHashCode();
        this._fire( { "type" : "show" } );
      }
    },

    deactivate : function() {
      if( this._active ) {
        this._fire( { "type" : "hide" } );
        this._active = null;
      }
    },

    trigger : function( charCode ) {
      var event = {
        "type" : "trigger",
        "charCode" : charCode,
        "success" : false
      };
      this._fire( event, true );
      return event.success;
    },

    _registerFocusRoot : function( root ) {
      if( !this._map[ root.toHashCode() ] ) {
        this._map[ root.toHashCode() ] = {};
        root.addEventListener( "dispose", function() {
          this.deactivate();
          delete this._map[ root.toHashCode() ];
        }, this );
        root.addEventListener( "changeActive", this.deactivate, this );
      }
    },

    _fire : function( event, onlyVisible ) {
      var result = null;
      if( this._map[ this._active ] ) {
        var handlers = this._map[ this._active ];
        for( var key in handlers ) {
          var entry = handlers[ key ];
          if( !onlyVisible || entry[ 0 ].isSeeable() ) {
            try{
              entry[ 1 ].call( entry[ 0 ], event );
            } catch( ex ) {
              var msg = "Could not handle mnemonic " + event.type + ". ";
              if( entry[ 0 ].isDisposed() ) {
                msg +=  entry[ 0 ].classname + " is disposed. ";
              }
              msg += ex.message;
              throw new Error( msg );
            }
          }
        }
      }
      return result;
    },

    _isActivation : function( eventType, keyCode, charCode, domEvent ) {
      return    this._activator
             && this._active == null
             && eventType === "keydown"
             && EventHandlerUtil.isModifier( keyCode )
             && this._isActivatorCombo( domEvent );
    },

    _isDeactivation : function( eventType, keyCode, charCode, domEvent ) {
      return    this._activator != null
             && this._active != null
             && eventType != "keypress"
             && !this._isActivatorCombo( domEvent );
    },

    _isActivatorCombo : function( domEvent ) {
      return    this._activator.ctrlKey === domEvent.ctrlKey
             && this._activator.altKey === domEvent.altKey
             && this._activator.shiftKey === domEvent.shiftKey;
    },

    _isTrigger : function( eventType, keyCode, charCode, domEvent ) {
      var isChar = !isNaN( keyCode ) && rwt.event.EventHandlerUtil.isAlphaNumericKeyCode( keyCode );
      return this._active != null && eventType === "keydown" && isChar;
     },

     _noMenuOpen : function() {
       return rwt.util.Objects.isEmpty( rwt.widgets.util.MenuManager.getInstance().getAll() );
     }

  }

} );

}());