/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ControlDecoratorTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateControlDecoratorByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      assertTrue( widget instanceof rwt.widgets.ControlDecorator );
      assertIdentical( shell, widget.getParent() );
      // see bug 407397
      assertNull( widget.getUserData( "protocolParent" ) );
    },

    testSetBoundsByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolSet( "w3", { "bounds" : [ 1, 2, 3, 4 ] } );

      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 4, widget.getHeight() );
    },

    testSetTextByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolSet( "w3", { "text" : "foo\n && <> \" bar" } );

      assertEquals( "foo<br/> &amp;&amp; &lt;&gt; &quot; bar", widget._text );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolSet( "w3", { "image" : [ "image.png", 10, 12 ] } );

      assertEquals( "image.png", widget.getSource() );
    },

    testSetVisibleByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolSet( "w3", { "visible" : true } );

      assertTrue( widget.getVisibility() );
    },

    testSetShowHoverByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolSet( "w3", { "showHover" : false } );

      assertFalse( widget._showHover );
    },

    testSetHasSelectionListenerByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolListen( "w3", { "Selection" : true } );

      assertTrue( widget.hasEventListeners( "mousedown" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );

      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );

      assertTrue( widget.hasEventListeners( "dblclick" ) );
      shell.destroy();
      widget.destroy();
    },

    testWidgetDefaultSelected : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );

      TestUtil.doubleClick( widget );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
    },

    testWidgetDefaultSelectedModifier : function() {
      var widget = this._createControlDecoratorByProtocol( "w3", "w2", [ "LEFT", "CENTER" ] );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );

      TestUtil.fakeMouseEventDOM(
          widget.getElement(),
          "dblclick",
          rwt.event.MouseEvent.buttons.left,
          0,
          0,
          rwt.event.DomEvent.SHIFT_MASK
       );

      var message = TestUtil.getLastMessage();
      assertTrue( message.findNotifyProperty( "w3", "DefaultSelection", "shiftKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "DefaultSelection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "DefaultSelection", "altKey" ) );
    },

    /////////
    // Helper

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    },

    _createControlDecoratorByProtocol : function( id, parentId, style ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ControlDecorator",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      TestUtil.flush();
      return rwt.remote.ObjectRegistry.getObject( id );
    }

  }

} );


}());
