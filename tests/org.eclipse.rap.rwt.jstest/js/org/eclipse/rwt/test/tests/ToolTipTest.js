/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ToolTipTest", {

  extend : rwt.qx.Object,

  members : {

    testToolTipHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ToolTip" );

      assertEquals( [ "Selection" ], handler.events );
    },

    testCreateToolTipByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ], false );
      assertTrue( widget instanceof rwt.widgets.ToolTip );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertNull( widget._style );
      assertFalse( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipByProtocol_withMarkupEnabled : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ], true );
      assertTrue( widget instanceof rwt.widgets.ToolTip );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertNull( widget._style );
      assertTrue( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testDestroyToolTipWithParent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );

      Processor.processOperationArray( [ "destroy", "w2"] );
      TestUtil.flush();

      assertTrue( ObjectRegistry.getObject( "w2" ) == null );
      assertTrue( shell.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( widget.isDisposed() );
    },

    testCreateToolTipWithIconErrorByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_ERROR" ] );
      assertTrue( widget instanceof rwt.widgets.ToolTip );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_ERROR" ) );
      assertEquals( "error", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipWithIconWarningByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_WARNING" ] );
      assertTrue( widget instanceof rwt.widgets.ToolTip );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_WARNING" ) );
      assertEquals( "warning", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipWithIconInformationByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_INFORMATION" ] );
      assertTrue( widget instanceof rwt.widgets.ToolTip );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_INFORMATION" ) );
      assertEquals( "information", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      TestUtil.protocolSet( "w3", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetRoundedBorderByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      TestUtil.protocolSet( "w3", { "roundedBorder" : [ 2, [ 0, 255, 0, 255 ], 1, 2, 3, 4 ] } );
      var border = widget.getBorder();
      assertEquals( "rgb(0,255,0)", border.getColor() );
      assertEquals( [ 1, 2, 3, 4 ], border.getRadii() );
      shell.destroy();
      widget.destroy();
    },

    testSetBackgroundGradientByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      var gradient
        = [ [ [ 0, 0, 255, 255 ], [ 0, 255, 0, 255 ], [ 0, 0, 255, 255 ] ], [ 0, 50, 100 ], false ];
      TestUtil.protocolSet( "w3", { "backgroundGradient" : gradient } );
      var actual = widget.getBackgroundGradient();
      assertEquals( [ 0, "rgb(0,0,255)" ], actual[ 0 ] );
      assertEquals( [ 0.5, "rgb(0,255,0)" ], actual[ 1 ] );
      assertEquals( [ 1, "rgb(0,0,255)" ], actual[ 2 ] );
      assertTrue( actual.horizontal );
      shell.destroy();
      widget.destroy();
    },

    testSetAutoHideByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      TestUtil.protocolSet( "w3", { "autoHide" : true } );
      assertTrue( widget._hideAfterTimeout );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );

      TestUtil.protocolSet( "w3", { "text" : "foo && <> \" bar" } );

      assertEquals( "foo &amp;&amp; &lt;&gt; &quot; bar", widget._text.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol_withMarkupEnabled : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      widget.setMarkupEnabled( true );

      TestUtil.protocolSet( "w3", { "text" : "<b>foo</b>" } );

      assertEquals( "<b>foo</b>", widget._text.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetMessageByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );

      TestUtil.protocolSet( "w3", { "message" : "foo && <> \" \nbar" } );

      assertEquals( "foo &amp;&amp; &lt;&gt; &quot; <br/>bar", widget._message.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetMessageByProtocol_withMarkupEnabled : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      widget.setMarkupEnabled( true );

      TestUtil.protocolSet( "w3", { "message" : "<b>foo</b>" } );

      assertEquals( "<b>foo</b>", widget._message.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetLocationByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      TestUtil.protocolSet( "w3", { "location" : [ 1, 2 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      shell.destroy();
      widget.destroy();
    },

    testSetVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      TestUtil.protocolSet( "w3", { "visible" : true } );
      assertTrue( widget.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testClientAreaOverflow : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );

      assertEquals( "hidden", widget._contentArea.getOverflow() );
      shell.destroy();
      widget.destroy();
    },

    testHideToolTip_onClick : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      widget.setVisible( true );
      TestUtil.flush();
      assertTrue( widget.getVisibility() );

      TestUtil.click( widget );
      rwt.remote.Connection.getInstance().send();

      assertFalse( widget.getVisibility() );
      assertFalse( TestUtil.getMessageObject( 0 ).findSetProperty( "w3", "visible" ) );
      shell.destroy();
      widget.destroy();
    },

    testHideToolTip_withAutohide : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      widget.setHideAfterTimeout( 1000 );
      widget.setVisible( true );
      TestUtil.flush();
      assertTrue( widget.getVisibility() );

      TestUtil.forceTimerOnce();
      rwt.remote.Connection.getInstance().send();

      assertFalse( widget.getVisibility() );
      assertFalse( TestUtil.getMessageObject( 0 ).findSetProperty( "w3", "visible" ) );
      shell.destroy();
      widget.destroy();
    },

    testToolTipIsNotFocusRoot : function() {
      var tooltip = new rwt.widgets.ToolTip();

      assertFalse( tooltip.isFocusRoot() );
      assertNull( tooltip.getFocusRoot() );
      tooltip.destroy();
    },

    /////////
    // Helper

    _createToolTipByProtocol : function( id, parentId, style, markupEnabled ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolTip",
        "properties" : {
          "style" : style,
          "parent" : parentId,
          "markupEnabled" : markupEnabled
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    }

  }

} );

}() );
