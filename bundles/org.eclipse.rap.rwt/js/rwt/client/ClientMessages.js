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

namespace( "rwt.client" );

rwt.client.ClientMessages = function() {
  if( rwt.client.ClientMessages._instance !== undefined ) {
    throw new Error( "can not be created twice" );
  } else {
    rwt.client.ClientMessages._instance = this;
  }
  this._messages = {};
};

rwt.client.ClientMessages.getInstance = function() {
  if( rwt.client.ClientMessages._instance === undefined ) {
    new rwt.client.ClientMessages();
  }
  return rwt.client.ClientMessages._instance;
};

rwt.client.ClientMessages.prototype = {

  setMessages : function( messages ) {
    for( var id in messages ) {
      this._messages[ id ] = messages[ id ];
    }
  },

  getMessage : function( id ) {
    return this._messages[ id ] ? this._messages[ id ] : "";
  }

};
