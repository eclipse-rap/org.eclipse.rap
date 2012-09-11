/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.runtime.System.getInstance().addEventListener( "uiready", function() {
  org.eclipse.rwt.KeyEventSupport.getInstance()._sendRequestAsync = function() {
    rwt.remote.Server.getInstance().sendImmediate( true );
  };
  rwt.remote.Server.getInstance().send = function() {
    if( this._requestCounter === -1 ) {
      // prevent infinite loop:
      throw new Error( "_requestCounter is -1" );
    }
    this.sendImmediate( true ); // omit timer
  };
  rwt.protocol.MessageProcessor.processMessage( {
    "meta": {
      "requestCounter": -1
    },
    "operations": [ [ "create", "w1", "rwt.Display" ] ]
  } );
  rwt.runtime.ErrorHandler.processJavaScriptErrorInResponse
    = function( script, error, currentRequest ) { throw error; };
  rwt.remote.Server.getInstance().setRequestCounter( 0 );
  org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
  org.eclipse.rwt.test.Asserts.createShortcuts();
  org.eclipse.rwt.test.TestRunner.getInstance().run();
} );