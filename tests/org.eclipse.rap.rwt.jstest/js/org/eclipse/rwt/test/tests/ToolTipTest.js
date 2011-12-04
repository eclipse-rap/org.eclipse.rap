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

qx.Class.define( "org.eclipse.rwt.test.tests.ToolTipTest", {

  extend : qx.core.Object,

  members : {

    testCreateToolTipByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      assertTrue( widget instanceof org.eclipse.swt.widgets.ToolTip );
      assertIdentical( qx.ui.core.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertNull( widget._style );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipWithIconErrorByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_ERROR" ] );
      assertTrue( widget instanceof org.eclipse.swt.widgets.ToolTip );
      assertIdentical( qx.ui.core.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_ERROR" ) );
      assertEquals( "error", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipWithIconWarningByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_WARNING" ] );
      assertTrue( widget instanceof org.eclipse.swt.widgets.ToolTip );
      assertIdentical( qx.ui.core.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_WARNING" ) );
      assertEquals( "warning", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testCreateToolTipWithIconInformationByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON", "ICON_INFORMATION" ] );
      assertTrue( widget instanceof org.eclipse.swt.widgets.ToolTip );
      assertIdentical( qx.ui.core.ClientDocument.getInstance(), widget.getParent() );
      assertTrue( widget.hasState( "rwt_BALLOON" ) );
      assertTrue( widget.hasState( "rwt_ICON_INFORMATION" ) );
      assertEquals( "information", widget._style );
      shell.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetRoundedBorderByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "roundedBorder" : [ 2, "#00ff00", 1, 2, 3, 4 ] } );
      var border = widget.getBorder();
      assertEquals( "#00ff00", border.getColor() );
      assertEquals( [ 1, 2, 3, 4 ], border.getRadii() );
      shell.destroy();
      widget.destroy();
    },

    testSetBackgroundGradientByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      var gradient = [ [ "#0000ff", "#00ff00", "#0000ff" ], [ 0, 50, 100 ], false ];
      testUtil.protocolSet( "w3", { "backgroundGradient" : gradient } );
      var actual = widget.getBackgroundGradient();
      assertEquals( [ 0, "#0000ff" ], actual[ 0 ] );
      assertEquals( [ 0.5, "#00ff00" ], actual[ 1 ] );
      assertEquals( [ 1, "#0000ff" ], actual[ 2 ] );
      assertTrue( actual.horizontal );
      shell.destroy();
      widget.destroy();
    },

    testSetAutoHideByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "autoHide" : true } );
      assertTrue( widget._hideAfterTimeout );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "text" : "foo && <> \" bar" } );
      assertEquals( "foo &amp;&amp; &lt;&gt; &quot; bar", widget._text.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetMessageByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "message" : "foo && <> \" \nbar" } );
      assertEquals( "foo &amp;&amp; &lt;&gt; &quot; <br/>bar", widget._message.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetLocationByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "location" : [ 1, 2 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      shell.destroy();
      widget.destroy();
    },

    testSetVisibleByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolSet( "w3", { "visible" : true } );
      assertTrue( widget.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createToolTipByProtocol( "w3", "w2", [ "BALLOON" ] );
      testUtil.protocolListen( "w3", { "selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    /////////
    // Helper

    _createToolTipByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolTip",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    }

  }
  
} );