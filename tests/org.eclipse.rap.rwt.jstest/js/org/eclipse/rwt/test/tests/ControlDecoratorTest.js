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

qx.Class.define( "org.eclipse.rwt.test.tests.ControlDecoratorTest", {

  extend : qx.core.Object,

  members : {

    testCreateControlDecoratorByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ControlDecorator );
      assertIdentical( shell, widget.getParent() );
      shell.destroy();
      widget.destroy();
    },

    testSetBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolSet( "w3", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 4, widget.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolSet( "w3", { "text" : "foo && <> \" bar" } );
      assertEquals( "foo &amp;&amp; &lt;&gt; &quot; bar", widget._text );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolSet( "w3", { "image" : [ "image.png", 10, 12 ] } );
      assertEquals( "image.png", widget.getSource() );
      shell.destroy();
      widget.destroy();
    },

    testSetVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolSet( "w3", { "visible" : true } );
      assertTrue( widget.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testSetShowHoverByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolSet( "w3", { "showHover" : false } );
      assertFalse( widget._showHover );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      assertTrue( widget.hasEventListeners( "mousedown" ) );
      assertTrue( widget.hasEventListeners( "dblclick" ) );
      shell.destroy();
      widget.destroy();
    },

    /////////
    // Helper

    _createControlDecoratorByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ControlDecorator",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    }

  }
  
} );