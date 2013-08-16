/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = rwt.remote.MessageProcessor;
var ObjectManager = rwt.remote.ObjectRegistry;
var WidgetProxyFactory = rwt.scripting.WidgetProxyFactory;
var EventBinding = rwt.scripting.EventBinding;
var SWT = rwt.scripting.SWT;

var text;

rwt.qx.Class.define( "org.eclipse.rap.clientscripting.Synchronizer_Test", {

  extend : rwt.qx.Object,

  members : {

    testSetTextSync : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setText( "foo" );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertEquals( "foo", msg.findSetProperty( "w3", "text" ) );
    },

    testSetBackground_sync : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setBackground( [ 1, 2, 3 ] );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertEquals( [ 1, 2, 3 ], msg.findSetProperty( "w3", "background" ) );
    },

    testSetBackground_noSyncIfNotChangedbyProxy : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      text.setBackgroundColor( "rgb( 1, 2, 3 )" );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertNull( msg.findSetOperation( "w3", "background" ) );
    },

    testSetBackground_noSyncIfChangedAfterProxyChanges : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setBackground( [ 3, 2, 1 ] );
      text.setBackgroundColor( "rgb( 1, 2, 3 )" );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertEquals( [ 3, 2, 1 ], msg.findSetProperty( "w3", "background" ) );
    },

    testSetBackground_syncToNull : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setBackground( [ 1, 2, 3 ] );
      widgetProxy.setBackground( null );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertEquals( null, msg.findSetProperty( "w3", "background" ) );
    },

    testSetForeground_sync : function() {
      TestUtil.initRequestLog();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setForeground( [ 1, 2, 3 ] );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      assertEquals( [ 1, 2, 3 ], msg.findSetProperty( "w3", "foreground" ) );
    },

    ////////
    // Helper
    setUp : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      TestUtil.flush();
      text = ObjectManager.getObject( "w3" );
      text.focus();
    },

    tearDown : function() {
      Processor.processOperation( {
        "target" : "w2",
        "action" : "destroy"
      } );
      text = null;
    }

  }

} );

}());
