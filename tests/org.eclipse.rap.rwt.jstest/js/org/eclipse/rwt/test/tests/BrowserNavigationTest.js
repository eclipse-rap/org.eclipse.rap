/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.BrowserNavigationTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateBrowserNavigationByProtocol : function() {
      var browserNavigation = this._createBrowserNavigationByProtocol();
      assertTrue( browserNavigation instanceof rwt.client.BrowserNavigation );
      assertFalse( browserNavigation._timer.getEnabled() );
    },

    testSetHasNavigationListenerByProtocol : function() {
      var browserNavigation = this._createBrowserNavigationByProtocol();
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "rwt.client.BrowserNavigation",
        "action" : "listen",
        "properties" : {
          "Navigation" : true
        }
      } );
      assertTrue( browserNavigation._hasNavigationListener );
      assertTrue( browserNavigation.hasEventListeners( "request" ) );
      assertTrue( browserNavigation._timer.getEnabled() );
      browserNavigation.setHasNavigationListener( false );
    },

    testAddByProtocol : function() {
      var browserNavigation = this._createBrowserNavigationByProtocol();
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "rwt.client.BrowserNavigation",
        "action" : "call",
        "method" : "addToHistory",
        "properties" : {
          "entries" : [ [ "id1", "text1" ] ]
        }
      } );
      assertEquals( "text1", browserNavigation._titles[ "id1" ] );
    },

    testSendNavigated : [
      function() {
        var browserNavigation = this._createBrowserNavigationByProtocol();
        rwt.remote.MessageProcessor.processOperation( {
          "target" : "rwt.client.BrowserNavigation",
          "action" : "call",
          "method" : "addToHistory",
          "properties" : {
            "entries" : [ [ "id1", "text1" ], [ "id2", "text2" ] ]
          }
        } );
        rwt.remote.MessageProcessor.processOperation( {
          "target" : "rwt.client.BrowserNavigation",
          "action" : "listen",
          "properties" : {
            "Navigation" : true
          }
        } );
        org.eclipse.rwt.test.fixture.TestUtil.store( browserNavigation );
        org.eclipse.rwt.test.fixture.TestUtil.delayTest( 200 );
      },
      function( browserNavigation) {
        browserNavigation.__getState = function() { return "id1"; };
        org.eclipse.rwt.test.fixture.TestUtil.forceInterval( browserNavigation._timer );

        var message = org.eclipse.rwt.test.fixture.TestUtil.getMessageObject();
        var actual = message.findNotifyProperty( "rwt.client.BrowserNavigation",
                                                 "Navigation",
                                                 "state" );
        assertEquals( "id1", actual );
      }
    ],

    testSendNavigatedWithUnknownEntry : [
      function() {
        var browserNavigation = this._createBrowserNavigationByProtocol();
        rwt.remote.MessageProcessor.processOperation( {
          "target" : "rwt.client.BrowserNavigation",
          "action" : "call",
          "method" : "addToHistory",
          "properties" : {
            "entries" : [ [ "id1", "text1" ], [ "id2", "text2" ] ]
          }
        } );
        rwt.remote.MessageProcessor.processOperation( {
          "target" : "rwt.client.BrowserNavigation",
          "action" : "listen",
          "properties" : {
            "Navigation" : true
          }
        } );
        org.eclipse.rwt.test.fixture.TestUtil.store( browserNavigation );
        org.eclipse.rwt.test.fixture.TestUtil.delayTest( 200 );
      },
      function( browserNavigation) {
        browserNavigation.__getState = function() { return "id3"; };
        org.eclipse.rwt.test.fixture.TestUtil.forceInterval( browserNavigation._timer );

        var message = org.eclipse.rwt.test.fixture.TestUtil.getMessageObject();
        var actual = message.findNotifyProperty( "rwt.client.BrowserNavigation",
                                                 "Navigation",
                                                 "state" );
        assertEquals( "id3", actual );
      }
    ],

    /////////
    // Helper

    _createBrowserNavigationByProtocol : function() {
      return rwt.remote.ObjectRegistry.getObject( "rwt.client.BrowserNavigation" );
    }

  }

} );
