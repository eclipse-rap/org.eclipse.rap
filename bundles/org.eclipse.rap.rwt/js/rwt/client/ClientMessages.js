/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.client" );

rwt.client.ClientMessages = function() {
  this._messages = {
    "ServerError" : "Server Error",
    "ServerErrorDescription" : "The application terminated unexpectedly.",
    "ConnectionError" : "Network Error",
    "ConnectionErrorDescription" : "The server seems to be temporarily unavailable.",
    "SessionTimeout" : "Session Timeout",
    "SessionTimeoutDescription" : "The server session timed out.",
    "ClientError" : "Client Error",
    "ClientErrorDescription" : "The application terminated unexpectedly.",
    "Retry" : "Retry",
    "Restart" : "Restart",
    "Details" : "Details"
  };
};

rwt.client.ClientMessages.getInstance = function() {
  return rwt.runtime.Singletons.get( rwt.client.ClientMessages );
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
