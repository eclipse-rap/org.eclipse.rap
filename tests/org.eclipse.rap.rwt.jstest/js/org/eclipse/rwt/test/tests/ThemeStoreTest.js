/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.protocol.MessageProcessor;

var themeStore = rwt.theme.ThemeStore.getInstance();
var originalTheme;
var originalFallback;

qx.Class.define( "org.eclipse.rwt.test.tests.ThemeStoreTest", {

  extend : qx.core.Object,

  members : {

    testLoadSendsRequest : function() {
      scheduleResponse();
      loadActiveTheme( "rwt-resource/myTheme" );

      assertNotNull( getRequestArguments( "send" ) );
    },

    testRequestOpenParams : function() {
      scheduleResponse();
      loadActiveTheme( "rwt-resource/myTheme" );

      var open = getRequestArguments( "open" );
      assertEquals( "GET", open[ 0 ] );
      assertEquals( "rwt-resource/myTheme", open[ 1 ] );
      assertFalse( open[ 2 ] );
    },

    setUp : function() {
      originalTheme = themeStore.getCurrentTheme();
      originalFallback = themeStore.getFallbackTheme();
    },

    tearDown : function() {
      themeStore.setCurrentTheme( originalTheme );
      themeStore.setFallbackTheme( originalFallback );
    }

  }

} );

function loadActiveTheme( url ) {
  TestUtil.protocolCall( "rwt.theme.ThemeStore", "loadActiveTheme", { "url" : url } );
}

function scheduleResponse() {
  org.eclipse.rwt.test.fixture.FakeServer.getInstance().setRequestHandler( function() {
    return "{ \"values\" : {}, \"theme\" : {} }";
  } );
}

function getRequestArguments( type ) {
  var log = TestUtil.getXMLHttpRequests()[ 0 ].getLog();
  var result = null;
  for( var i = 0; i < log.length; i++ ) {
    if( log[ i ][ 0 ] === type ) {
      result = log[ i ][ 1 ];
    }
  }
  return result;
}

}());
