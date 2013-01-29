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
var MessageProcessor = rwt.remote.MessageProcessor;

var themeStore = rwt.theme.ThemeStore.getInstance();
var originalTheme;
var originalFallback;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ThemeStoreTest", {

  extend : rwt.qx.Object,

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

    testLoadActiveTheme : function() {
      scheduleResponse( {
        "values" : { "colors" :  { "abc" : [ 0, 255, 0, 1 ] } },
        "theme" : {
          "mywidget" : {
            "background-color" : [ [ [], "abc" ] ]
          }
        }
      } );

      loadActiveTheme( "rwt-resource/myTheme" );

      assertEquals( "rwt-resource/myTheme", themeStore.getCurrentTheme() );
      assertEquals( "#00ff00", themeStore.getColor( "mywidget", {}, "background-color" ) );
    },

    testGetColorAlpha : function() {
      scheduleResponse( {
        "values" : {
          "colors" :  {
            "def" : [ 128, 255, 200, 0.5 ]
          }
        },
        "theme" : {
          "mywidget" : {
            "background-color" : [ [ [], "def" ] ]
          }
        }
      } );

      loadActiveTheme( "rwt-resource/myTheme" );

      assertEquals( 0.5, themeStore.getAlpha( "mywidget", {}, "background-color" ) );
    },

    testGetTransparentColorAlpha : function() {
      scheduleResponse( {
        "values" : {
          "colors" :  {
            "ghi" : "transparent"
          }
        },
        "theme" : {
          "mywidget" : {
            "background-color" : [ [ [], "ghi"] ]
          }
        }
      } );

      loadActiveTheme( "rwt-resource/myTheme" );

      assertEquals( 0, themeStore.getAlpha( "mywidget", {}, "background-color" ) );
    },

    testGetNamedColorAlpha : function() {
      scheduleResponse( {
        "values" : {
          "colors" :  {
            "abc" : [ 0, 255, 0, 0.5 ]
          }
        },
        "theme" : {
          "mywidget" : {
            "background-color" : [ [ [], "red" ] ]
          }
        }
      } );

      loadActiveTheme( "rwt-resource/myTheme" );

      assertEquals( "rwt-resource/myTheme", themeStore.getCurrentTheme() );
      assertEquals( 1, themeStore.getAlpha( "mywidget", {}, "background-color" ) );
    },

    testLoadFallbackTheme : function() {
      scheduleResponse( {
        "values" : { "colors" :  { "xyz" : "#00ff00" } },
        "theme" : {
          "mywidget" : {
            "background-color" : [ [ [], "xyz" ] ]
          }
        }
      } );

      loadFallbackTheme( "rwt-resource/myFallbackTheme" );

      assertEquals( "rwt-resource/myFallbackTheme", themeStore.getFallbackTheme() );
      assertEquals( "#00ff00", themeStore.getColor( "mywidget", {}, "background-color" ) );
    },

    setUp : function() {
      originalTheme = themeStore.getCurrentTheme();
      originalFallback = themeStore.getFallbackTheme();
    },

    tearDown : function() {
      themeStore.setCurrentTheme( originalTheme );
      themeStore.setFallbackTheme( originalFallback );
      themeStore._fillNamedColors( originalFallback );
    }

  }

} );

function loadActiveTheme( url ) {
  TestUtil.protocolCall( "rwt.theme.ThemeStore", "loadActiveTheme", { "url" : url } );
}

function loadFallbackTheme( url ) {
  TestUtil.protocolCall( "rwt.theme.ThemeStore", "loadFallbackTheme", { "url" : url } );
}

function scheduleResponse( obj ) {
  var response = "{ \"values\" : {}, \"theme\" : {} }";
  if( obj ) {
    response = JSON.stringify( obj );
  }
  org.eclipse.rwt.test.fixture.FakeServer.getInstance().setRequestHandler( function() {
    return response;
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
