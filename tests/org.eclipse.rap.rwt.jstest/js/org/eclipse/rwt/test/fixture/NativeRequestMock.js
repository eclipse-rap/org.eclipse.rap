/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which
 * is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.test.fixture.NativeRequestMock = function() {
  this._log = [ [ "constructor", arguments ] ];
  org.eclipse.rwt.test.fixture.NativeRequestMock.history.push( this );
  this.status = 0;
  this.statusText = "";
  this.readyState = 0;
  this.responseText = "";
};

org.eclipse.rwt.test.fixture.NativeRequestMock.history = [];

org.eclipse.rwt.test.fixture.NativeRequestMock.useFakeServer = true;

org.eclipse.rwt.test.fixture.NativeRequestMock.prototype = {

  //////////
  // LOG API

  getLog : function() {
    return this._log;
  },

  ///////////
  // MOCK API

  send : function() {
    this._log.push( [ "send", arguments ] );
    if( org.eclipse.rwt.test.fixture.NativeRequestMock.useFakeServer ) {
      org.eclipse.rwt.test.fixture.FakeServer.getInstance().receive( this, arguments );
    }
  },

  open : function() {
    this._log.push( [ "open", arguments ] );
  },

  setRequestHeader : function() {
    this._log.push( [ "setRequestHeader", arguments ] );
  },

  abort : function() {
    this._log.push( [ "abort", arguments ] );
  },

  getAllResponseHeaders : function() {
    return "";
  }

};
