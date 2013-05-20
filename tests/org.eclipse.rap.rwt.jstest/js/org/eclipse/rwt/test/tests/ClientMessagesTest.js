/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var Processor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ClientMessagesTest", {

  extend : rwt.qx.Object,

  members : {

    testClientMessagesInstance : function() {
      var clientMessages = this._createClientMessages();
      assertTrue( clientMessages instanceof rwt.client.ClientMessages );
      assertIdentical( clientMessages, rwt.client.ClientMessages.getInstance() );
    },

    testSetMessagesByProtocol : function() {
      var clientMessages = this._createClientMessages();
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
      var clientMessages = this._createClientMessages();
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
      var clientMessages = this._createClientMessages();

      assertEquals( "", clientMessages.getMessage( "missing" ) );
    },

    _createClientMessages : function() {
      return ObjectManager.getObject( "rwt.client.ClientMessages" );
    }

  }

} );

}());
