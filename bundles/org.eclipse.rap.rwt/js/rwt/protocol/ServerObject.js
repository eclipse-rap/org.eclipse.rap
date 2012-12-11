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

var server = rwt.remote.Server.getInstance();

/**
 * @private
 * @class Instances of RemoteObject represents the server-side counterpart of a client object
 * and are used to write remote operations into the next protocol message. It can be obtained
 * by {@link rap.getRemoteObject}.
 * @exports rwt.protocol.ServerObject as RemoteObject
 * @since 2.0
 * @param {}
 *
 */
rwt.protocol.ServerObject = function( id ) {
  this._id = id;
};

rwt.protocol.ServerObject.prototype = {

  /**
   * @description Sets the specified property of the remote object to the given value.
   * Calling this method multiple times for the same key will overwrite the previous value,
   * the message will not become longer.
   * This method does not cause the message to be sent immediately.
   * @param {string} property
   * @param {var} value
   */
  set : function( key, value ) {
    server.getMessageWriter().appendSet( this._id, key, value );
  },

  /**
   * @description Notifies the remote object a event of the given type occured.
   * The properties object may contain any number of additional properties/fields.
   * It may also be null or ommited. Sending an event of a type the server is currently not
   * listening for (see {@link rap.registerTypeHandler}, handler.listeners) is illegal usage of the
   * RAP protocol, but is not prevented. Calling this method causes the message to be sent to the
   * server within a few milliseconds.
   * @param {string} event
   * @param {Object} properties
   * @param {}
   */
  notify : function( event, properties, suppressSend ) {
   // TODO [tb]: suppressSend should be a temporariy workaround for KeyEventSupport.js
    var actualProps = properties ? properties : {};
    server.getMessageWriter().appendNotify( this._id, event, actualProps );
    if( suppressSend !== true ) {
      server.send();
    }
  },

  /**
   * @description Intructs the remote object to call the given method.
   * The properties object may contain any number of additional properties/fields. It may also be
   * null or ommited. Calling this method causes the message to be sent to the server within a
   * few milliseconds.
   * @param {string} method
   * @param {Object} properties
   */
  call : function( method, properties ) {
    var actualProps = properties ? properties : {};
    server.getMessageWriter().appendCall( this._id, method, actualProps );
    server.send();
  }

};

}());
