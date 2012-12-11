/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

/**
 * @public
 * @since 2.0
 * @namespace Holds all public API of the RAP WebClient.
 */
rap = {

  /**
   * @description Register a RAP protocol type-handler for a specific type of remote-objects.
   * @param {string} type
   *
   * @param {Object} handler The handler object.
   *
   * @param {Function} handler.factory Called for create operations.
   * Is given a properties object as the first argument, which may contain any number for
   * properties/fields set by the server. Has to return a "client object" representing an instance
   * of the type of this handler. <em>Required for all type handler</em>.
   *
   * @param {Function} handler.destructor Called for destroy operations with the client object as
   * the first argument. <em>Optional</em>
   *
   * @param {string[]} handler.properties List of properties supported by this handler.
   * The order in the list controls the order in which the properties are applied by the message
   * processor. The client object has to implement a setter for each supported property, unless a
   * custom property-handler is defined (see <b>handler.propertyHandler</b>). For example, if
   * the property is "bounds", <code>setBounds</code> will be called on the client object.
   * Properties given by the server that are not in this list will be ignored completely.
   * <em>Optional.</em>
   *
   * @param {Object} handler.propertyHandler A map with functions.
   * If a property is to be applied, the message processor will first look in this map for the name
   * of the property. If a function is defined for the property, it is called with the client object
   * as the first argument, and the property-value as the second. If no function is defined for the
   * property, the message processor will try to call a setter on the client object directly.
   * <em>If no property-handler are needed, this field is optional.</em>
   *
   * @param {string[]} handler.listeners List of listener supported by this handler.
   * The order in the list controls the order in which the properties "listen" operations are
   * processed by the message processor. A "listen" operations instructs the client object to start
   * or stop notifying the server of events of the given event type. For each supported event a
   * matching "has[Type]Listener" method has to be implemented by the client object, unless a custom
   * listener handler is defined (see <b>handler.listenerHandler</b>). For example, if the
   * listener/event type is "Modify", <code>setHasModifyListener</code> will be called on the client
   * object. The value given will be <code>true</code> to start and <code>false</code> to stop
   * sending events. A "listen" operation for a type not in this list will be ignored.
   * <em>Optional.</em>
   *
   * @param {Object} handler.listenerHandler A map with functions.
   * If a "listen" operation is processed, the message processor will first look in this map for the
   * name of the event type. If a function is defined for the event-type, it is called with the
   * client object as the first argument, and the boolean value as the second.
   * <em>If no listener-handler are needed, this field is optional.</em>
   *
   * @param {string[]} handler.methods List of methods supported by this handler.
   * The order in the list is meaningless, "call" operations are processed in the order in which
   * they are given by the server. The client object has to implement a method of the same name,
   * unless a custom method handler is defined (see <b<handler.methodHandler</b>). One argument
   * will be given, which is a properties object with any number of properties/fields.
   * A "call" operation for a method not in this list will be ignored. <em>Optional.</em>
   *
   * @param {Object} handler.methodHandler A map with functions.
   * If a "call" operation is processed, the message processor will first look in this map for the
   * name of the method. If a function is defined for the name of the method, it is called with the
   * client object as the first argument, and the properties map as the second.
   * <em>If no method-handler are needed, this field is optional.</em>
   */
  registerTypeHandler : function( type, handler ) {
    handler.isPublic = true;
    rwt.protocol.AdapterRegistry.add( type, handler );
  },

  /**
   * @description Returns the actual client object associated with the given id.
   * If there is no object registered for the given id, null is returned.
   * For RAP internal objects (e.g. RWT widgets) a wrapper is returned instead of the real object.
   * @see Composite
   * @param {string} id
   * @returns {Object}
   */
  getObject : function( id ) {
    var entry = rwt.protocol.ObjectRegistry.getEntry( id );
    var result;
    if( entry && entry.adapter.isPublic ) {
      result = entry.object;
    } else {
      result = getWrapperFor( entry.object );
    }
    return result;
  },

  /**
   * @description Returns an instance of {@link RemoteObject} for the given client object.
   * The object has to be one created by an type-handler factory method. Multiple calls
   * for the same objects will return the same instance.
   * @see rap.registerTypeHandler
   * @param {Object} object
   * @returns {RemoteObject}
   */
  getRemoteObject : function( object ) {
    return rwt.remote.Server.getInstance().getServerObject( object );
  }

};

var wrapperMap = {};

function getWrapperFor( obj ) {
  var result = null;
  if( obj instanceof Object ) {
    var hash = qx.core.Object.toHashCode( obj );
    if( wrapperMap[ hash ] == null ) {
      if( obj instanceof rwt.widgets.Composite ) {
        wrapperMap[ hash ] = new CompositeWrapper( obj );
      } else {
        wrapperMap[ hash ] = {};
      }
    }
    result = wrapperMap[ hash ];
  }
  return result;
}

/**
 * @private
 * @class Wrapper for RWT Composite widgets
 * @name Composite
 * @since 2.0
 */
function CompositeWrapper( widget ) {
  var children = null;
  if( !widget.isCreated() ) {
    children = [];
    widget.addEventListener( "create", function() {
      for( var i = 0; i < children.length; i++ ) {
        widget._getTargetNode().appendChild( children[ i ] );
      }
      widget.removeEventListener( "create", arguments.callee );
      children = null;
    } );
  }
  /**
   * @name append
   * @memberOf Composite#
   * @description Adds a given HTMLElement to the Composite.
   * @param {HTMLElement} childElement
   */
  this.append = function( childElement ) {
    if( children ) {
      children.push( childElement );
    } else {
      widget._getTargetNode().appendChild( childElement );
    }
  };
}

}());