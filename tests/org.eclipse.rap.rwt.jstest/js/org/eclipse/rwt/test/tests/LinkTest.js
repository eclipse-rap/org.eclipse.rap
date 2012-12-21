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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.LinkTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateLinkByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Link );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "link", widget.getAppearance() );
      assertEquals( "", widget._text );
      assertEquals( 0, widget._linksCount );
      assertFalse( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : [ [ "text1 ", null ], [ "link1", 0 ], [ " text2 ", null ], [ "link2", 1 ] ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var expected
        = "text1 "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"w3#0\">link1</span>"
        + " text2 "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"w3#1\">link2</span>";
      assertEquals( expected, widget._link.getHtml() );
      assertEquals( 2, widget._linksCount );
      shell.destroy();
      widget.destroy();
    },

    testSetTextWithLineBreaksByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : [ [ "text\ntext ", null ], [ "link\nlink", 0 ] ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var expected
        = "text<br/>text "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"w3#0\">"
        + "link<br/>link"
        + "</span>";
      assertEquals( expected, widget._link.getHtml() );
      assertEquals( 1, widget._linksCount );
      shell.destroy();
      widget.destroy();
    },

    testSetTextEscapedByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : [ [ "foo && <> \" bar ", null ], [ "foo && <> \" bar", 0 ] ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var expected
        = "foo &amp;&amp; &lt;&gt; &quot; bar "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"w3#0\">"
        + "foo &amp;&amp; &lt;&gt; &quot; bar"
        + "</span>";
      assertEquals( expected, widget._link.getHtml() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSendSelectionEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : [ [ "text1 ", null ], [ "link1", 0 ], [ " text2 ", null ], [ "link2", 1 ] ]
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      TestUtil.flush();
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );

      //TestUtil.clickDOM( widget._link.getElement().lastChild );
      widget._sendChanges( 1 ); // Can not use fixture in this case

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getLastMessage();
      assertEquals( 1, message.findNotifyProperty( "w3", "Selection", "index" ) );
      shell.destroy();
    }

  }

} );