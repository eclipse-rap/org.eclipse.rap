/*******************************************************************************
 * Copyright (c) 2010,2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.BrowserTest", {
  extend : qx.core.Object,
  
  members : {

    BLANK : "../org.eclipse.rap.rwt.q07/resources/resource/static/html/blank.html",

    testGetDomain : function() {
      var url1 = "HTtp://google.de/";
      var url2 =   "http://www.sub.somedomain.com:84/"
                 + "example/document.html?param=value&param2=%20value2"
      var url3 = "https://amazon.com";
      var url4 = "FTPS://mydomain.cOM";
      var domain1 = org.eclipse.swt.browser.Browser.getDomain( url1 );
      var domain2 = org.eclipse.swt.browser.Browser.getDomain( url2 );
      var domain3 = org.eclipse.swt.browser.Browser.getDomain( url3 );
      var domain4 = org.eclipse.swt.browser.Browser.getDomain( url4 );
      assertEquals( "google.de", domain1 );
      assertEquals( "www.sub.somedomain.com:84", domain2 );
      assertEquals( "amazon.com", domain3 );
      assertEquals( "mydomain.com", domain4 );
    },

    testGetDomainFails : function() {
      // url1/2 are assumed to be relative
      var url1 = "htp://google.de/";
      var url2 = "ftp.www.sub.somedomain.com:84";
      var domain1 = org.eclipse.swt.browser.Browser.getDomain( url1 );
      var domain2 = org.eclipse.swt.browser.Browser.getDomain( url2 );
      var domain3 = org.eclipse.swt.browser.Browser.getDomain( null );
      assertNull( domain1 );
      assertNull( domain2 );
      assertNull( domain3 );
    },

    testExecute :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        //NOTE: Using "_isLoaded" instead of "isLoaded" because of IE. 
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        var win = browser.getContentWindow();
        assertNotNull( win );
        assertTrue( typeof foo === "undefined" );
        foo = 17;
        // Note: Using this line would fail in firefox, no workaround known:
        //browser.execute( "foo = 33;" );
        browser.execute( "window.foo = 33;" );
        assertEquals( 17, foo );
        assertEquals( 33, win.foo );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 )
        browser.destroy();
        delete foo;
      }
    ],
    
    testEvaluate :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "33;" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.evaluateResult=%5B33%5D" ) != -1 )
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 )
        browser.destroy();
      }
    ],
    
    testExecuteFailed :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "for(){}" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.executeResult=false" ) != -1 );
        browser.destroy();
        delete foo;
      }
    ],
    
    testEvaluateReturnsRegexp :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "/regexp/;" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.evaluateResult=null" ) != -1 );
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 );
        browser.destroy();
      }
    ],

    testEvaluateReturnsMap :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "( function(){ return {};})();" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.evaluateResult=null" ) != -1 );
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 );
        browser.destroy();
      }
    ],

    testEvaluateReturnsArray :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "( function(){ return [ 1,2,3 ]; } )();" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.evaluateResult=%5B%5B1%2C2%2C3%5D%5D" ) != -1 );
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 );
        browser.destroy();
      }
    ],

    testEvaluateReturnsFunction :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.execute( "( function(){ return function(){}; } )();" );
        var msg = testUtil.getMessage();
        assertTrue( msg.indexOf( "w6.evaluateResult=%5B%5B%5D%5D" ) != -1 )
        assertTrue( msg.indexOf( "w6.executeResult=true" ) != -1 )
        browser.destroy();
      }
    ],

    testExecuteSecurityException : [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        browser.setSource( "http://www.google.de/" );
        browser.syncSource();
        testUtil.delayTest( 1000 );
        testUtil.store( browser );
      },
      function( browser ) {
        var error = null;
        try {
          browser.execute( "alert(\" This should not happen\" );" );
        } catch( ex ) {
          error = ex;
        }
        assertTrue( error !== null );
      }
    ],

    testBrowserFunctionSecurityExceptionInResponse : [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        browser.setSource( "http://www.google.de/" );
        browser.syncSource();
        testUtil.delayTest( 1000 );
        testUtil.store( browser );
      },
      function( browser ) {
        org.eclipse.swt.EventUtil.setSuspended( true );
        var error = null;
        try {
          browser.createFunction( "abc" );
        } catch( ex ) {
          error = ex;
        }
        org.eclipse.swt.EventUtil.setSuspended( false );
        assertTrue( error !== null );
      }
    ],

    testCreateDestroyBrowserFunction :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
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
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        browser.createFunction( "abc" );
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionThenNavigate :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        browser.setSource( "javascript:false;" );
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        browser.createFunction( "abc" );
        browser.getIframeNode().src = this.BLANK;
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        browser.destroy();
      }
    ],
    
    testCreateDestroyBrowserFunctionThenNavigate :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 600 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.createFunction( "abc" );
        browser.destroyFunction( "abc" );
        browser.reload();
        testUtil.delayTest( 600 );
        testUtil.store( browser );
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
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        browser.createFunction( "abc" );
        browser.setSource( "http://www.google.com/" );
        browser.syncSource();
        testUtil.delayTest( 2500 );
        testUtil.store( browser );
      },
      function( browser ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertTrue( "slow connection?", browser._isLoaded );
        assertNull( testUtil.getErrorPage() );
        browser.destroy();
      }
    ],

    testCreateBrowserFunctionThenNavigateToOtherDomain :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        browser.createFunction( "abc" );
        browser.getIframeNode().onload = function() {
          browser.setUserData( "nativeLoaded", true );
        };
        // Simulates navigation: source stays the same, no load event
        // since the "load" flag is already true:
        browser.getIframeNode().src = "http://www.google.com/";
        testUtil.delayTest( 3000 );
        testUtil.store( browser );
      },
      function( browser ) {
        if( !qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          assertTrue( "native loaded?", browser.getUserData( "nativeLoaded" ) );
        }
        assertNull( testUtil.getErrorPage() );
        browser.destroy();
      }
    ],

    testNavigateToOtherDomainThenCreateBrowserFunction :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        browser.getIframeNode().onload = function() {
          browser.setUserData( "nativeLoaded", true );
        };
        browser.getIframeNode().src = "http://www.google.com/";
        testUtil.delayTest( 2000 );
        testUtil.store( browser );
      },
      function( browser ) {
        // NOTE: Some IE dont fire a load event for this scenario, 
        //       therefore can not check that side is loaded,
        //       could lead to false negative (red) test  
        if( !qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          assertTrue( "native loaded?", browser.getUserData( "nativeLoaded" ) );
        }
        var error = null;
        try{ 
          browser.createFunction( "abc" );
        }catch( ex ) {
          error = ex;
        }
        assertNotNull( error );
      }
    ],

    testBrowserFunctionFailed :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        testUtil.scheduleResponse( function() {
	        browser.setFunctionResult( "abc", null, "error" );
	      } );
        try {
          var result = win.abc();
          throw "Browser function should throw an error";
        } catch( e ) {
          assertEquals( "error", e.message );
        }
        browser.destroy();
      }
    ],

    testBrowserFunctionSucceed  :  [
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        testUtil.delayTest( 300 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.initRequestLog();
        browser.createFunction( "abc" );
        var win = browser.getContentWindow();
        assertTrue( typeof( win.abc ) === "function" );
        assertTrue( typeof( win.abc_impl ) === "function" );
        testUtil.scheduleResponse( function() {
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

    testDispose :  [
      function() {
        // See Bug 327440 - Memory leak problem with Iframe in Internet Explorer
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var browser = this._createBrowser();
        assertTrue( browser.isSeeable() );
        assertFalse( browser._isLoaded );
        testUtil.delayTest( 500 );
        testUtil.store( browser );
      },
      function( browser ) {
        assertTrue( "slow connection?", browser._isLoaded );
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var wm = org.eclipse.swt.WidgetManager.getInstance();      
        var el = browser._getTargetNode();
        var iframe = browser._iframeNode;
        assertTrue( iframe.parentNode === el );
        wm.dispose( "w6" );
        testUtil.flush();
        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          assertEquals( "javascript:false;", browser.getSource() );
          assertFalse( browser.isDisposed() );
          assertIdentical( testUtil.getDocument(), browser.getParent() );
          assertTrue( browser.isSeeable() );
        }
        assertTrue( wm.findWidgetById( "w6" ) == null ); /* may be undefined */
        testUtil.delayTest( 300 );
        testUtil.store( browser, el, iframe );
      },
      function( browser, el, iframe ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.flush();
        assertTrue( "disposed?", browser.isDisposed() );
        assertTrue( el.innerHTML === "" );
        assertTrue( iframe.parentNode == null );
      }
    ],

    testObjectToString : function() {
      var browser = this._createBrowser();
      var object = [];
      object[ 0 ] = 12;
      object[ 1 ] = false;
      object[ 2 ] = null;
      object[ 3 ] = "eclipse";
      object[ 4 ] = "double \" \" quotes";
      object[ 5 ] = [ 3.6, [ 'swt', true ] ];
      var ecpected = "[12,false,null,\"eclipse\",\"double \\\" \\\" quotes\",[3.6,[\"swt\",true]]]";
      assertEquals( ecpected, browser.objectToString( object ) );
    },

    /////////////
    // helper
    
    _createBrowser : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var browser = new org.eclipse.swt.browser.Browser();
      browser.addToDocument();
      browser.setSpace( 10, 576, 57, 529 );
      browser.setSource( this.BLANK );
      browser.syncSource();
      var wm = org.eclipse.swt.WidgetManager.getInstance();      
      wm.add( browser, "w6", true );
      testUtil.flush();
      return browser;
    }
    
    
 }
  
} );