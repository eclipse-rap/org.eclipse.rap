/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*global foo:true */
/*jshint scripturl:true */

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.BrowserTest", {

  extend : rwt.qx.Object,

  members : {

    BLANK : "../rwt-resources/resource/static/html/blank.html",
    URL1 : "http://www.eclipse.org/rap",

    testCreateBrowserByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Browser",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Browser );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      shell.destroy();
      widget.destroy();
    },

    testSetProgressListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Browser",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );

      TestUtil.protocolListen( "w3", { "Progress" : true } );

      var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( widget );
      assertTrue( remoteObject.isListening( "Progress" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetUrlByProtocol :  [
      function() {
        TestUtil.createShellByProtocol( "w2" );
        Processor.processOperation( {
          "target" : "w3",
          "action" : "create",
          "type" : "rwt.widgets.Browser",
          "properties" : {
            "style" : [],
            "parent" : "w2",
            "url" : this.URL1
          }
        } );
        TestUtil.delayTest( 7000 );
        var browser = ObjectRegistry.getObject( "w3" );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertEquals( this.URL1, browser.getSource() );
        assertTrue( "slow connection?", browser._isLoaded );
        browser.destroy();
      }
    ],

    testGetDomain : function() {
      var url1 = "HTtp://google.de/";
      var url2 =   "http://www.sub.somedomain.com:84/"
                 + "example/document.html?param=value&param2=%20value2";
      var url3 = "https://amazon.com";
      var url4 = "FTPS://mydomain.cOM";
      var domain1 = rwt.widgets.Browser.getDomain( url1 );
      var domain2 = rwt.widgets.Browser.getDomain( url2 );
      var domain3 = rwt.widgets.Browser.getDomain( url3 );
      var domain4 = rwt.widgets.Browser.getDomain( url4 );
      assertEquals( "google.de", domain1 );
      assertEquals( "www.sub.somedomain.com:84", domain2 );
      assertEquals( "amazon.com", domain3 );
      assertEquals( "mydomain.com", domain4 );
    },

    testGetDomainFails : function() {
      // url1/2 are assumed to be relative
      var url1 = "htp://google.de/";
      var url2 = "ftp.www.sub.somedomain.com:84";
      var domain1 = rwt.widgets.Browser.getDomain( url1 );
      var domain2 = rwt.widgets.Browser.getDomain( url2 );
      var domain3 = rwt.widgets.Browser.getDomain( null );
      assertNull( domain1 );
      assertNull( domain2 );
      assertNull( domain3 );
    },

    testExecute :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        //NOTE: Using "_isLoaded" instead of "isLoaded" because of IE.
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        foo = 17;

        // Note: Using this line would fail in firefox, no workaround known:
        //browser.execute( "foo = 33;" );

        browser.execute( "window.foo = 33;" );
        assertEquals( 17, foo );
        assertEquals( 33, browser.getContentWindow().foo );
        assertEquals( 1, TestUtil. getRequestsSend() );
        var message = TestUtil.getMessageObject();
        assertNotNull( message.findCallOperation( "w6", "evaluationSucceeded" ) );
        browser.destroy();
        foo = undefined;
      }
    ],

    testEvaluateByProtocol :  [
      function() {
        TestUtil.createShellByProtocol( "w2" );
        Processor.processOperation( {
          "target" : "w3",
          "action" : "create",
          "type" : "rwt.widgets.Browser",
          "properties" : {
            "style" : [],
            "parent" : "w2"
          }
        } );
        TestUtil.delayTest( 2000 );
        var browser = ObjectRegistry.getObject( "w3" );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "evaluate",
          "properties" : {
            "script" : "33;"
          }
        } );
        var message = TestUtil.getMessageObject();
        assertEquals( [ 33 ], message.findCallProperty( "w3", "evaluationSucceeded", "result" ) );
        browser.destroy();
      }
    ],

    testCreateDestroyBrowserFunctionByProtocol :  [
      function() {
        TestUtil.createShellByProtocol( "w2" );
        var browser = this._createBrowserByProtocol( "w3", "w2" );
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "createFunctions",
          "properties" : {
            "functions" : [ "abc" ]
          }
        } );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "destroyFunctions",
          "properties" : {
            "functions" : [ "abc" ]
          }
        } );
        assertTrue( typeof( win.abc ) === "undefined" );
        assertTrue( typeof( win.abc_impl ) === "undefined" );
        browser.destroy();
      }
    ],

    testExecuteFailed :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.execute( "for(){}" );
        var message = TestUtil.getMessageObject();
        assertNotNull( message.findCallOperation( "w6", "evaluationFailed" ) );
        browser.destroy();
        foo = undefined;
      }
    ],

    testEvaluateReturnsRegexp :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.execute( "/regexp/;" );
        var message = TestUtil.getMessageObject();
        assertNull( message.findCallProperty( "w6", "evaluationSucceeded", "result" ) );
        browser.destroy();
      }
    ],

    testEvaluateReturnsMap :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.execute( "( function(){ return {};})();" );
        var message = TestUtil.getMessageObject();
        assertNull( message.findCallProperty( "w6", "evaluationSucceeded", "result" ) );
        browser.destroy();
      }
    ],

    testEvaluateReturnsArray :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.execute( "( function(){ return [ 1,2,3 ]; } )();" );
        var expected = [1,2,3];
        var message = TestUtil.getMessageObject();
        var actual = message.findCallProperty( "w6", "evaluationSucceeded", "result" )[ 0 ];
        assertEquals( expected, actual );
        browser.destroy();
      }
    ],

    testEvaluateReturnsFunction :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.execute( "( function(){ return function(){}; } )();" );
        var message = TestUtil.getMessageObject();
        assertEquals( [], message.findCallProperty( "w6", "evaluationSucceeded", "result" )[ 0 ] );
        browser.destroy();
      }
    ],

    testExecuteSecurityException : [
      function() {
        var browser = this._createBrowser();
        browser.setSource( "http://www.google.de/" );
        browser.syncSource();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        var error = null;
        try {
          browser.execute( "alert(\" This should not happen\" );" );
          fail();
        } catch( ex ) {
          error = ex;
        }
        assertTrue( error !== null );
        browser.destroy();
      }
    ],

    testBrowserFunctionSecurityExceptionInResponse : [
      function() {
        var browser = this._createBrowser();
        browser.setSource( "http://www.google.de/" );
        browser.syncSource();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        rwt.remote.EventUtil.setSuspended( true );
        var error = null;
        try {
          browser.createFunction( "abc" );
        } catch( ex ) {
          error = ex;
        }
        rwt.remote.EventUtil.setSuspended( false );
        assertTrue( error !== null );
        browser.destroy();
      }
    ],

    testCreateDestroyBrowserFunction :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        browser.destroyFunction( "abc" );
        assertTrue( typeof( win.abc ) === "undefined" );
        assertTrue( typeof( win.abc_impl ) === "undefined" );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionBeforeLoaded :  [
      function() {
        var browser = this._createBrowser();
        browser.createFunction( "abc" );
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionThenNavigate :  [
      function() {
        var browser = this._createBrowser();
        browser.setSource( "javascript:false;" );
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        browser.createFunction( "abc" );
        browser.getIframeNode().src = this.BLANK;
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        browser.destroy();
      }
    ],

    testCreateDestroyBrowserFunctionThenNavigate :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.createFunction( "abc" );
        browser.destroyFunction( "abc" );
        browser.reload();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "undefined" );
        assertTrue( typeof( win.abc_impl ) === "undefined" );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionThenSetSourceToOtherDomain :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        browser.createFunction( "abc" );
        browser.setSource( this.URL1 );
        browser.syncSource();
        TestUtil.delayTest( 7000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        assertNull( TestUtil.getErrorPage() );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionThenNavigateToOtherDomain :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        browser.createFunction( "abc" );
        browser.getIframeNode().onload = function() {
          browser.setUserData( "nativeLoaded", true );
        };
        // Simulates navigation: source stays the same, no load event
        // since the "load" flag is already true:
        browser.getIframeNode().src = this.URL1;
        TestUtil.delayTest( 10000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "native loaded?", browser.getUserData( "nativeLoaded" ) );
        assertNull( TestUtil.getErrorPage() );
        browser.destroy();
      }
    ],

    testNavigateToOtherDomainThenCreateBrowserFunction :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        browser.getIframeNode().onload = function() {
          browser.setUserData( "nativeLoaded", true );
        };
        browser.getIframeNode().src = this.URL1;
        TestUtil.delayTest( 7000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "native loaded?", browser.getUserData( "nativeLoaded" ) );
        var error = null;
        try{
          browser.createFunction( "abc" );
        }catch( ex ) {
          error = ex;
        }
        assertNotNull( error );
        browser.destroy();
      }
    ],

    testBrowserFunctionCall  :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );

        win.abc( 5 );

        var message = TestUtil.getMessageObject();
        assertEquals( "abc", message.findCallProperty( "w6", "executeFunction", "name" ) );
        assertEquals( [ 5 ], message.findCallProperty( "w6", "executeFunction", "arguments" ) );
        browser.destroy();
      }
    ],

    testBrowserFunctionFailed :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        TestUtil.scheduleResponse( function() {
          browser.setFunctionResult( "abc", null, "error" );
        } );
        try {
          win.abc();
          throw "Browser function should throw an error";
        } catch( e ) {
          assertEquals( "error", e.message );
        }
        browser.destroy();
      }
    ],

    testBrowserFunctionSucceed  :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        TestUtil.scheduleResponse( function() {
          browser.setFunctionResult( "abc", "result", null );
        } );
        try {
          var result = win.abc();
          assertEquals( "result", result );
        } catch( e ) {
          throw "Browser function shouldn't throw an error";
        }
        browser.destroy();
      }
    ],

    testBrowserFunctionFailedByProtocol :  [
      function() {
        TestUtil.createShellByProtocol( "w2" );
        var browser = this._createBrowserByProtocol( "w3", "w2" );
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "createFunctions",
          "properties" : {
            "functions" : [ "abc" ]
          }
        } );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        TestUtil.scheduleResponse( function() {
          TestUtil.protocolSet( "w3", { "functionResult" : [ "abc", null, "error" ] } );
        } );
        try {
          win.abc();
          throw "Browser function should throw an error";
        } catch( e ) {
          assertEquals( "error", e.message );
        }
        browser.destroy();
      }
    ],

    testBrowserFunctionSucceedByProtocol  :  [
      function() {
        TestUtil.createShellByProtocol( "w2" );
        var browser = this._createBrowserByProtocol( "w3", "w2" );
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        TestUtil.initRequestLog();
        Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "createFunctions",
          "properties" : {
            "functions" : [ "abc" ]
          }
        } );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        TestUtil.scheduleResponse( function() {
          TestUtil.protocolSet( "w3", { "functionResult" : [ "abc", "result", null ] } );
        } );
        try {
          var result = win.abc();
          assertEquals( "result", result );
        } catch( e ) {
          throw "Browser function shouldn't throw an error";
        }
        browser.destroy();
      }
    ],

    testDispose :  [
      function() {
        // See Bug 327440 - Memory leak problem with Iframe in Internet Explorer
        var browser = this._createBrowser();
        assertTrue( browser.isSeeable() );
        assertFalse( browser._isLoaded );
        TestUtil.delayTest( 500 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var wm = rwt.remote.WidgetManager.getInstance();
        var el = browser._getTargetNode();
        var iframe = browser._iframeNode;
        assertTrue( iframe.parentNode === el );
        wm.dispose( "w6" );
        TestUtil.flush();
        assertTrue( wm.findWidgetById( "w6" ) == null ); /* may be undefined */
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser, el, iframe );
      },
      function( browser, el, iframe ) {
        var log = [];
        browser.addEventListener( "load", function( e ) {
          log.push( e );
        }, this );
        browser._onload();
        assertEquals( 0, log.length );
        TestUtil.flush();
        assertTrue( "disposed?", browser.isDisposed() );
        assertTrue( el.innerHTML === "" );
        assertTrue( iframe.parentNode == null );
        browser.destroy();
      }
    ],

    testEnabled :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.delayTest( 2000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var blockerNode = browser.getBlockerNode();
        var blockerParent = browser._getBlockerParent();
        assertTrue( blockerNode.parentNode !== blockerParent );
        browser.setEnabled( false );
        assertTrue( blockerNode.parentNode === blockerParent );
        browser.setEnabled( true );
        assertTrue( blockerNode.parentNode !== blockerParent );
        browser.destroy();
      }
    ],

    testBlokerElementBackgroundImage : function() {
      var browser = this._createBrowser();

      browser.setEnabled( false );

      var image = TestUtil.getCssBackgroundImage( browser.getBlockerNode() );
      var expected = rwt.client.Client.isTrident() && rwt.client.Client.getVersion() < 11;
      assertEquals( expected, image.indexOf( "blank.gif" ) !== -1 );
    },

    testToJSON_NoChanges : function() {
      var browser = this._createBrowser();
      var object = [];
      object[ 0 ] = 12;
      object[ 1 ] = false;
      object[ 2 ] = null;
      object[ 3 ] = "eclipse";
      object[ 4 ] = "double \" \" quotes";
      var ecpected = [ 12, false, null, "eclipse", "double \" \" quotes" ];
      assertEquals( ecpected, browser.toJSON( object ) );
      browser.destroy();
    },

    testToJSON_SpecialCases : function() {
      var browser = this._createBrowser();
      var object = [];
      object[ 0 ] = [ 3.6, [ 'swt', true ] ];
      object[ 1 ] = function(){};
      object[ 2 ] = {};

      var actual = browser.toJSON( object );

      assertEquals( 3.6, actual[ 0 ][ 0 ] );
      assertEquals( [ 'swt', true ], actual[ 0 ][ 1 ] );
      assertEquals( "string", typeof actual[ 1 ] );
      assertTrue( actual[ 2 ] instanceof Array );
      browser.destroy();
    },

    testProgressEvent :  [
      function() {
        var browser = this._createBrowser();
        TestUtil.fakeListener( browser, "Progress", true );
        TestUtil.initRequestLog();
        browser.setSource( this.URL1 );
        browser.syncSource();
        TestUtil.delayTest( 7000 );
        TestUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        assertEquals( 1, TestUtil.getRequestsSend() );
        assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w6", "Progress" ) );
        browser.destroy();
      }
    ],

    /////////////
    // helper

    _createBrowserByProtocol : function( id, parentId ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Browser",
        "properties" : {
          "style" : [],
          "parent" : parentId
        }
      } );
      return ObjectRegistry.getObject( id );
    },

    _createBrowser : function() {
      var browser = new rwt.widgets.Browser();
      browser.addToDocument();
      browser.setSpace( 10, 576, 57, 529 );
      browser.setSource( this.BLANK );
      browser.syncSource();
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Browser" );
      ObjectRegistry.add( "w6", browser, handler );
      TestUtil.flush();
      return browser;
    }

 }

} );

}() );
