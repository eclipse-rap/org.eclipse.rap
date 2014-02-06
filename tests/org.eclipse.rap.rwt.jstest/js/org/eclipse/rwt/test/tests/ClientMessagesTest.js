/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var Processor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;

var clientMessages;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ClientMessagesTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      clientMessages = new rwt.client.ClientMessages();
    },

    testClientMessagesInstance : function() {
      var clientMessages = ObjectManager.getObject( "rwt.client.ClientMessages" );
      assertTrue( clientMessages instanceof rwt.client.ClientMessages );
      assertIdentical( clientMessages, rwt.client.ClientMessages.getInstance() );
    },

    testGetInstance : function() {
      var instance = rwt.client.ClientMessages.getInstance();

      assertIdentical( instance, rwt.runtime.Singletons.get( rwt.client.ClientMessages ) );
    },

    testSetMessagesByProtocol : function() {
      clientMessages = rwt.client.ClientMessages.getInstance();
      Processor.processOperation( {
        "target" : "rwt.client.ClientMessages",
        "action" : "set",
        "properties" : {
          "messages" : {
            "foo" : "bar"
          }
        }
      } );
      assertEquals( "bar", clientMessages.getMessage( "foo" ) );
    },

    testSetMessagesUpdatesExisting : function() {
      clientMessages._messages = {
        "foo1" : "bar1",
        "foo2" : "bar2",
        "foo3" : "bar3"
      };

      clientMessages.setMessages( { "foo2" : "updated" } );

      assertEquals( "bar1", clientMessages.getMessage( "foo1" ) );
      assertEquals( "updated", clientMessages.getMessage( "foo2" ) );
      assertEquals( "bar3", clientMessages.getMessage( "foo3" ) );
    },

    testMessingMessageReturnsEmptyString : function() {
      assertEquals( "", clientMessages.getMessage( "missing" ) );
    }

  }

} );

}() );
