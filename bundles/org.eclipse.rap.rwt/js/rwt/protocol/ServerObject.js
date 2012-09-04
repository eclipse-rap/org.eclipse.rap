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

rwt.protocol.ServerObject = function( id ) {
  this._id = id;
};

rwt.protocol.ServerObject.prototype = {

  set : function( key, value ) {
    server.getMessageWriter().appendSet( this._id, key, value );
  },

  notify : function( event, properties ) {
    var actualProps = properties ? properties : {};
    server.getMessageWriter().appendNotify( this._id, event, actualProps );
    server.send();
  },

  call : function( method, properties ) {
    var actualProps = properties ? properties : {};
    server.getMessageWriter().appendCall( this._id, method, actualProps );
    server.send();
  }

};

}());
