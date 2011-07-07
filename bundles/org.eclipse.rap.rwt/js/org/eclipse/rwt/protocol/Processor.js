/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.protocol.Processor", {

  statics : {

    processMessage : function( messageObject ) {
      // NOTE : Temporary implementation, as this function should parse json-text directly later
      var operations = messageObject.operations;
      for( var i = 0; i < operations.length; i++ ) {
        this.processOperation( operations[ i ] );
      }
    },
    
    processOperation : function( operation ) {
      try {
        switch( operation.type ) {
          case "create":
            this._processCreate( operation.target, operation.details );
          break; 
          case "destroy":
            this._processDestroy( operation.target, operation.details );
          break; 
          case "set":
            this._processSet( operation.target, operation.details );
          break; 
          case "do":
            this._processDo( operation.target, operation.details );
          break; 
          case "listen":
            this._processListen( operation.target, operation.details );
          break; 
          case "execute":
            this._processExecute( operation.target, operation.details );
          break; 
        }
      } catch( ex ) {
        this._processError( ex, operation );
      }
    },

    _processCreate : function( targetId, details ) {
      var type = details.type;
      var adapter = org.eclipse.rwt.protocol.AdapterRegistry.getAdapter( type );
      var targetObject = new adapter.constructor();
      this._addTarget( targetObject, targetId, adapter.isControl, type );
      this._processStyleFlags( targetObject, adapter, details.style );
      this._processParent( targetObject, adapter, details.parent );
   },
    
    _processDestroy : function( targetId, details ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.dispose( targetId );      
    },

    _processSet : function( targetId, details ) {
      var adapter = this._getAdapter( targetId );
      if( adapter.knownProperties instanceof Array ) {
        var targetObject = this._getTarget( targetId );
        for( var i = 0; i < adapter.knownProperties.length; i++ ) {
          var property = adapter.knownProperties[ i ];
          var value = details[ property ];
          if( value !== undefined ) {
            var setterName = this._getSetterName( property );
            targetObject[ setterName ]( value );
          }
        }
      }
    },

    _processDo : function( targetId, details ) {
      var name = details.name;
      var adapter = this._getAdapter( targetId );
      if( adapter.knownActions.indexOf( name ) !== -1 ) {
        var targetObject = this._getTarget( targetId );
        var parameter = details.parameter instanceof Array ? details.parameter : [];
        targetObject[ name ].apply( targetObject, parameter );
      }
    },
    
    _processListen : function( targetId, details ) {
      var adapter = this._getAdapter( targetId );
      if( adapter.knownEvents instanceof Array ) {
        var targetObject = this._getTarget( targetId );
        if( details.add instanceof Array ) {
          this._processAddListeners( targetObject, adapter, details.add );
        }
        if( details.remove instanceof Array ) {
          this._processRemoveListeners( targetObject, adapter, details.remove );
        }
      }
   },

   _processExecute : function( targetId, details ) {
     if( details.scriptType === "text/javascript" ) {
       try {
         eval( details.script );
       } catch( ex ) {
         // ignored
         // TODO [tb] : handle in processOperation
       }
     }
   },

    ////////////
    // Internals

    _processError : function( error, operation ) {
      var msg = "Operation \"" + operation.type + "\"";
      msg += " on target \"" +  operation.target + "\"";
      var target = this._getTarget( operation.target );
      msg += " of type \"" +  ( target && target.classname ? target.classname : target ) + "\"";
      msg += " failed:";
      msg += "\n" + error;
      throw new Error( msg );
    },

    _processStyleFlags : function( targetOject, adapter, style ) {
      if( style instanceof Array ) {
        for( var i = 0; i < style.length; i++ ) {
          if( adapter.knownStyles.indexOf( style[ i ] ) !== -1 ) {
            targetOject.addState( "rwt_" + style[ i ] );
          }
        }
      }
    },
    
    _processParent : function( targetObject, adapter, parentId ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.setParent( targetObject, parentId );
    },

    _getSetterName : function( property ) {
      return "set" + qx.lang.String.toFirstUp( property );
    },

    _addTarget : function( target, targetId, isControl, type ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( target, targetId, isControl === true, type );
    },

    _getTarget : function( targetId ) {
      // TODO [tb] : support any kind of target-object, not just widgets
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var widget = widgetManager.findWidgetById( targetId );
      return widget;
    },

    _getAdapter : function( targetId ) {
      // TODO [tb] : support objects not implementing setUserData 
      var target = this._getTarget( targetId );
      var type = target.getUserData( "rwtType" );
      var adapter = org.eclipse.rwt.protocol.AdapterRegistry.getAdapter( type );
      return adapter;
    },

    _processAddListeners : function( targetObject, adapter, types ) {
      for( var i = 0; i < types.length; i++ ) {
        if( adapter.knownEvents.indexOf( types[ i ] ) !== -1 ) {
          this._addListener( targetObject, types[ i ] );
        }
      }
    },

    _processRemoveListeners : function( targetObject, adapter, types ) {
      for( var i = 0; i < types.length; i++ ) {
        if( adapter.knownEvents.indexOf( types[ i ] ) !== -1 ) {
          this._removeListener( targetObject, types[ i ] );
        }
      }
    },

    _addListener : function( targetObject, eventType ) {
      var list = this._listenerMap[ eventType ];
      for( var i = 0; i < list.length; i++ ) {
        targetObject.addEventListener( list[ i ].nativeType, 
                                       list[ i ].listener, 
                                       list[ i ].context );
      }
    },

    _removeListener : function( targetObject, eventType ) {
      var list = this._listenerMap[ eventType ];
      for( var i = 0; i < list.length; i++ ) {
        targetObject.removeEventListener( list[ i ].nativeType, 
                                          list[ i ].listener, 
                                          list[ i ].context );
      }
    },
    
    _listenerMap : {
      "focus" : [ 
        { 
          nativeType : "focusin", 
          context : org.eclipse.swt.EventUtil, 
          listener : org.eclipse.swt.EventUtil.focusGained 
        },
        { 
          nativeType : "focusout", 
          context : org.eclipse.swt.EventUtil, 
          listener : org.eclipse.swt.EventUtil.focusLost 
        }
      ],
      "mouse" : [
        { 
          nativeType : "mousedown", 
          context : org.eclipse.swt.EventUtil, 
          listener : org.eclipse.swt.EventUtil.mouseDown 
        },
        { 
          nativeType : "mouseup", 
          context : org.eclipse.swt.EventUtil, 
          listener : org.eclipse.swt.EventUtil.mouseUp
        }
      ]
    }

  }

} );
