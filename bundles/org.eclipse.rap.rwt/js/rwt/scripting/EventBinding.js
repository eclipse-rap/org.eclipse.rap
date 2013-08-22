/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

rwt.qx.Class.createNamespace( "rwt.scripting", {} );

var SWT = rwt.scripting.SWT;

var wrapperRegistry = {};

// TODO : better name?
rwt.scripting.EventBinding = {

  addListener : function( widget, eventType, targetFunction ) {
    var wrapperKey = this._getWrapperKey( widget, eventType, targetFunction );
    var wrapperList = this._getWrapperList( wrapperKey );
    var nativeType = this._getNativeEventType( widget, eventType );
    var nativeSource = this._getNativeEventSource( widget, eventType );
    var wrappedListener = this._wrapListener( widget, eventType, targetFunction );
    nativeSource.addEventListener( nativeType, wrappedListener, window );
    wrapperList.push( wrappedListener );
  },

  removeListener : function( widget, eventType, targetFunction ) {
    var wrapperKey = this._getWrapperKey( widget, eventType, targetFunction );
    var wrapperList = this._getWrapperList( wrapperKey );
    var nativeType = this._getNativeEventType( widget, eventType );
    var nativeSource = this._getNativeEventSource( widget, eventType );
    var wrappedListener = wrapperList.pop();
    nativeSource.removeEventListener( nativeType, wrappedListener, window );
  },

  _wrapListener : function( widget, eventType, targetFunction ) {
    return function( nativeEvent ) {
      try {
        var eventProxy = new rwt.scripting.EventProxy( SWT[ eventType ], widget, nativeEvent );
        var wrappedEventProxy = rwt.scripting.EventProxy.wrapAsProto( eventProxy );
        targetFunction( wrappedEventProxy );
        rwt.scripting.EventProxy.postProcessEvent( eventProxy, wrappedEventProxy, nativeEvent );
        rwt.scripting.EventProxy.disposeEventProxy( eventProxy );
      } catch( ex ) {
        var msg = "Error in scripting event type ";
        throw new Error( msg + eventType + ": " + ( ex.message ? ex.message : ex ) );
      }
    };
  },

  _getWrapperKey : function( widget, eventType, targetFunction ) {
    var result = [
      rwt.qx.Object.toHashCode( widget ),
      eventType,
      rwt.qx.Object.toHashCode( targetFunction )
    ];
    return result.join( ":" );
  },

  _getWrapperList : function( wrapperKey ) {
    if( wrapperRegistry[ wrapperKey ] == null ) {
      wrapperRegistry[ wrapperKey ] = [];
    }
    return wrapperRegistry[ wrapperKey ];
  },

  _getNativeEventSource : function( source, eventType ) {
    var SWT = rwt.scripting.SWT;
    var result;
    if( source.classname === "rwt.widgets.List" && eventType === "Selection" ) {
      result = source.getManager();
    } else {
      result = source;
    }
    return result;
  },

  _getNativeEventType : function( source, eventType ) {
    var map = this._eventTypeMapping;
    var result;
    if( map[ source.classname ] && map[ source.classname ][ eventType ] ) {
      result = map[ source.classname ][ eventType ];
    } else {
      result = map[ "*" ][ eventType ];
    }
    return result;
  },

  _eventTypeMapping : {
    "*" : {
      "KeyDown" : "keypress",
      "KeyUp" : "keyup",
      "MouseDown" : "mousedown",
      "MouseUp" : "mouseup",
      "MouseMove" : "mousemove",
      "MouseEnter" : "mouseover",
      "MouseExit" : "mouseout",
      "MouseDoubleClick" : "dblclick",
      "Paint" : "paint",
      "FocusIn" : "focus",
      "FocusOut" : "blur",
      "Show" : "appear",
      "Hide" : "disappear"
    },
    "rwt.widgets.List" : {
      "Selection" : "changeSelection",
      "DefaultSelection" : "dblclick"
    },
    "rwt.widgets.Text" : {
      "Verify" : "input", // TODO [tb] : does currently not react on programatic changes
      "Modify" : "changeValue"
    }
  }

};

}());
