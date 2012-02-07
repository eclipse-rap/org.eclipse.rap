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

qx.Class.define( "org.eclipse.rwt.test.tests.BrowserHistoryTest", {

  extend : qx.core.Object,

  members : {

    testCreateBrowserHistoryByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var browserHistory = this._createBrowserHistoryByProtocol();
      assertTrue( browserHistory instanceof qx.client.History );
      assertFalse( browserHistory._timer.getEnabled() );
    },

    testSetHasNavigationListenerByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var browserHistory = this._createBrowserHistoryByProtocol();
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "bh",
        "action" : "listen",
        "properties" : {
          "navigation" : true
        }
      } );
      assertTrue( browserHistory._hasNavigationListener );
      assertTrue( browserHistory.hasEventListeners( "request" ) );
      assertTrue( browserHistory._timer.getEnabled() );
      browserHistory.setHasNavigationListener( false );
    },

    testAddByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var browserHistory = this._createBrowserHistoryByProtocol();
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "bh",
        "action" : "call",
        "method" : "add",
        "properties" : {
          "entries" : [ [ "id1", "text1" ] ]
        }
      } );
      assertEquals( "text1", browserHistory._titles[ "id1" ] );
    },

    /////////
    // Helper

    _createBrowserHistoryByProtocol : function() {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "bh",
        "action" : "create",
        "type" : "rwt.BrowserHistory"
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( "bh" );
    }

  }
  
} );