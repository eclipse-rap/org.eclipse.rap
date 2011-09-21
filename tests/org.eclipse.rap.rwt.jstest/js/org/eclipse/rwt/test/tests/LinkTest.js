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

qx.Class.define( "org.eclipse.rwt.test.tests.LinkTest", {

  extend : qx.core.Object,

  members : {

    testCreateLinkByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Link );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
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
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var expected 
        = "text1 "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"0\">link1</span>"
        + " text2 "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"1\">link2</span>";
      assertEquals( expected, widget._link.getHtml() );
      assertEquals( 2, widget._linksCount );
      shell.destroy();
      widget.destroy();
    },

    testSetTextWithLineBreaksByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
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
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var expected 
        = "text<br/>text "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"0\">link<br/>link</span>";
      assertEquals( expected, widget._link.getHtml() );
      assertEquals( 1, widget._linksCount );
      shell.destroy();
      widget.destroy();
    },

    testSetTextEscapedByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
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
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var expected 
        = "foo &amp;&amp; &lt;&gt; &quot; bar "
        + "<span tabIndex=\"1\" style=\"text-decoration:underline; \" id=\"0\">"
        + "foo &amp;&amp; &lt;&gt; &quot; bar"
        + "</span>";
      assertEquals( expected, widget._link.getHtml() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Link",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      this._protocolListen( "w3", { "selection" : true } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },
    
    //////////
    // Helpers

    _protocolListen : function( target, properties ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : target,
        "action" : "listen",
        "properties" : properties
      } );
    }

  }
  
} );