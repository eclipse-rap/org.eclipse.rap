/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.runtime.System.getInstance().addEventListener( "uiready", function() {
  rwt.remote.MessageProcessor.processMessage( {
    "head": {},
    "operations": [
      [
        "call",
        "rwt.theme.ThemeStore",
        "loadFallbackTheme", {
          "url" : "rwt-resources/rap-rwt.theme.Fallback.json"
        }
      ],
      [
        "call",
        "rwt.theme.ThemeStore",
        "loadActiveTheme", {
          "url" : "rwt-resources/rap-rwt.theme.Fallback.json"
        }
      ]
    ]
  } );
  rwt.remote.Request.createXHR = function() {
    return new org.eclipse.rwt.test.fixture.NativeRequestMock();
  };
  rwt.remote.KeyEventSupport.getInstance()._sendRequestAsync = function() {
    rwt.remote.Server.getInstance().sendImmediate( true );
  };
  var server = rwt.remote.Server.getInstance();
  server.send = function() {
    if( !this._sendTimer.isEnabled() ) {
      this._sendTimer.start();
      if( this._requestCounter === -1 ) {
        // prevent infinite loop:
        throw new Error( "_requestCounter is -1" );
      }
      this.sendImmediate( true ); // omit timer
    }
  };
  server._delayTimer = new rwt.client.Timer();
  server._delayTimer.addEventListener( "interval", function() {
    this._delayTimer.stop();
    this.send();
  }, server );
  org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
  rwt.remote.MessageProcessor.processMessage( {
    "head": {},
    "operations": [
      [ "create", "w1", "rwt.widgets.Display" ]
    ]
  } );
  rwt.runtime.ErrorHandler.processJavaScriptErrorInResponse
    = function( script, error, currentRequest ) { throw error; };
  rwt.remote.Server.getInstance().setRequestCounter( 0 );
  org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests();
  org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
  org.eclipse.rwt.test.Asserts.createShortcuts();
  org.eclipse.rwt.test.TestRunner.getInstance().run();
} );
