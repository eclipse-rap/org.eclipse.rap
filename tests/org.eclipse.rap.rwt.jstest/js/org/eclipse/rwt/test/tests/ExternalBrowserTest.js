/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ExternalBrowserTest", {

  extend : qx.core.Object,

  members : {

    testExternalBrowserExists : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var externalBrowser = ObjectManager.getObject( "eb" );
      assertTrue( externalBrowser instanceof org.eclipse.rwt.widgets.ExternalBrowser );
    },

    testCreateExternalBrowserByProtocol : function() {
      var externalBrowser = org.eclipse.rwt.widgets.ExternalBrowser.getInstance();
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "eb",
        "action" : "create",
        "type" : "rwt.widgets.ExternalBrowser",
        "properties" : {}
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "eb" );
      assertIdentical( externalBrowser, widget );
    },

    testEscapeId : function() {
      var externalBrowser = org.eclipse.rwt.widgets.ExternalBrowser.getInstance();
      var escapedId = externalBrowser._escapeId( "my.id" );
      assertEquals( -1, escapedId.indexOf( "." ) );
      escapedId = externalBrowser._escapeId( "my id" );
      assertEquals( -1, escapedId.indexOf( " " ) );
      escapedId = externalBrowser._escapeId( "my-id" );
      assertEquals( -1, escapedId.indexOf( "-" ) );
  
      var escapedId1 = externalBrowser._escapeId( "my_id" );
      var escapedId2 = externalBrowser._escapeId( "my.id" );
      assertFalse( escapedId1 == escapedId2 );
  
      escapedId1 = externalBrowser._escapeId( "my_id_0" );
      escapedId2 = externalBrowser._escapeId( "my.id_0" );
      assertFalse( escapedId1 == escapedId2 );
  
      escapedId1 = externalBrowser._escapeId( "1" );
      assertEquals( "1", escapedId1 );
      escapedId2 = externalBrowser._escapeId( "2" );
      assertEquals( "2", escapedId2 );
    }

  }
  
} );