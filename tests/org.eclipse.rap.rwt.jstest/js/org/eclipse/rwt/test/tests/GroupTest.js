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
var MessageProcessor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GroupTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateGroupByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Group );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "group-box", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testDestroyGroupWithChildrenByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var ObjectRegistry = rwt.remote.ObjectRegistry;
      var group = ObjectRegistry.getObject( "w3" );
      MessageProcessor.processOperationArray( [ "create", "w4", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var child = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperationArray( [ "destroy", "w3" ] );
      TestUtil.flush();

      assertTrue( group.isDisposed() );
      assertTrue( child.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      shell.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "foo & <> \" bar"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "foo &amp; &lt;&gt; &quot; bar", getLegend( widget ) );
      shell.destroy();
      widget.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      var group = createMnemonicGroup();

      assertEquals( 1, group.getMnemonicIndex() );
      shell.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "text" : "baa" } );

      assertEquals( null, group.getMnemonicIndex() );

      shell.destroy();
    },

    testRenderMnemonic_OnActivate : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.flush();
      shell.setActive( true );

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", getLegend( group ) );
      shell.destroy();
    },

    testRenderMnemonic_OnDeActivate : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.flush();
      shell.setActive( true );

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", getLegend( group ) );
      shell.destroy();
    },

    testMnemonicTrigger_FocusFirstChild : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "children" : [ "w4", "w5" ] } );
      var widget = TestUtil.createWidgetByProtocol( "w4", "w3", "rwt.widgets.Text" );
      TestUtil.createWidgetByProtocol( "w5", "w3", "rwt.widgets.Text" );
      TestUtil.flush();
      shell.setActive( true );
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( widget.getFocused() );
      shell.destroy();
    },

    testMnemonicTrigger_FocusFirstVisibleChild : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "children" : [ "w4", "w5" ] } );
      TestUtil.createWidgetByProtocol( "w4", "w3", "rwt.widgets.Text" ).setVisibility( false );
      var widget = TestUtil.createWidgetByProtocol( "w5", "w3", "rwt.widgets.Text" );
      TestUtil.flush();
      shell.setActive( true );
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( widget.getFocused() );
      shell.destroy();
    },

    testMnemonicTrigger_FocusFirstEnabledChild : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "children" : [ "w4", "w5" ] } );
      TestUtil.createWidgetByProtocol( "w4", "w3", "rwt.widgets.Text" ).setEnabled( false );
      var widget = TestUtil.createWidgetByProtocol( "w5", "w3", "rwt.widgets.Text" );
      TestUtil.flush();
      shell.setActive( true );
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( widget.getFocused() );
      shell.destroy();
    },

    testMnemonicTrigger_FocusChildInComposite : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "children" : [ "w4" ] } );
      TestUtil.createWidgetByProtocol( "w4", "w3", "rwt.widgets.Composite" );
      TestUtil.protocolSet( "w4", { "children" : [ "w5" ] } );
      var widget = TestUtil.createWidgetByProtocol( "w5", "w4", "rwt.widgets.Text" );
      TestUtil.flush();
      shell.setActive( true );
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( widget.getFocused() );
      shell.destroy();
    },

    testMnemonicTrigger_FocusNothing : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "children" : [ "w4" ] } );
      var widget = TestUtil.createWidgetByProtocol( "w4", "w3", "rwt.widgets.Text" );
      widget.setEnabled( false );
      TestUtil.flush();
      shell.setActive( true );
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertFalse( widget.getFocused() );
      shell.destroy();
    },

    testSetCustomVariant : function() {
      var group = new rwt.widgets.Group();

      group.setCustomVariant( "foo" );

      assertTrue( group.hasState( "foo" ) );
      assertTrue( group._legend.hasState( "foo" ) );
      assertTrue( group._frame.hasState( "foo" ) );
      group.dispose();
    },

    testSetFont : function() {
      var group = new rwt.widgets.Group();

      group.setFont( rwt.html.Font.fromString( "Arial 20px" ) );

      assertEquals( "20px Arial", group.getFont().toCss() );
      assertEquals( "20px Arial", group._legend.getFont().toCss() );
      group.dispose();
    },

    testApplyGroupLabelId : function(){
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;

      rwt.widgets.base.Widget._renderHtmlIds = true;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );

      var widget = rwt.remote.ObjectRegistry.getObject( "w3" );
      assertEquals( "w3-label", widget._legend.getHtmlAttribute( "id" ) );
      shell.destroy();
      rwt.widgets.base.Widget._renderHtmlIds = false;
    }

  }

} );

var createMnemonicGroup = function() {
  MessageProcessor.processOperation( {
    "target" : "w3",
    "action" : "create",
    "type" : "rwt.widgets.Group",
    "properties" : {
      "style" : [],
      "parent" : "w2",
      "text" : "foo",
      "mnemonicIndex" : 1
    }
  } );
  return rwt.remote.ObjectRegistry.getObject( "w3" );
};

var getLegend = function( group ) {
  return group._legend.getCellContent( 0 );
};

}() );
