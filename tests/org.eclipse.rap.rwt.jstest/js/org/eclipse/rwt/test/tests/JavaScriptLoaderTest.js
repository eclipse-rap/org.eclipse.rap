/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
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
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var FakeServer = org.eclipse.rwt.test.fixture.FakeServer;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.JavaScriptLoaderTest", {

  extend : rwt.qx.Object,

  members : {

    testLoad_pausesScriptExecution : function() {
      load( [ "rwt-resource/myJS" ] );

      assertTrue( MessageProcessor.isPaused() );
      MessageProcessor.continueExecution();
    },

    testLoad_failsWithMultipleFiles : function() {
      try {
        load( [ "rwt-resource/myJS", "rwt-resource/myJS2" ] );
        fail();
      } catch( ex ) {
        // expected as long as parallel loading is not implemented
      }
    },

    testLoad_failsWithNoFiles : function() {
      try {
        load( [] );
        fail();
      } catch( ex ) {
      }
    },

    testLoad_addsScriptTagToHead : function() {
      load( [ "rwt-resource/myJS" ] );

      var head = document.getElementsByTagName( "head" )[ 0 ];
      var candidate = head.lastChild;
      assertEquals( "script", candidate.tagName.toLowerCase() );
      assertEquals( "text/javascript", candidate.getAttribute( "type" ).toLowerCase() );
      assertEquals( "rwt-resource/myjs", candidate.getAttribute( "src" ).toLowerCase() );
      MessageProcessor.continueExecution();
      head.removeChild( candidate ); // cleaning up
    },

    testLoad_addsOnLoadToScriptTagThatContinuesExecution : function() {
      load( [ "rwt-resource/myJS" ] );
      var head = document.getElementsByTagName( "head" )[ 0 ];
      var scriptEl = head.lastChild;

      scriptEl.onload();

      assertFalse( MessageProcessor.isPaused() );
      assertNull( scriptEl.onload );
      head.removeChild( scriptEl ); // cleaning up
    }

  }

} );

function load( files ) {
  var message = {
  "head" : {},
    "operations" : [
      [ "call", "rwt.client.JavaScriptLoader", "load", { "files" : files } ]
    ]
  };
  MessageProcessor.processMessage( message );
}

}());
