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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GroupTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateGroupByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var MessageProcessor = rwt.remote.MessageProcessor;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
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
      assertEquals( "foo &amp; &lt;&gt; &quot; bar", widget.getLegend() );
      shell.destroy();
      widget.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );

      var group = createMnemonicGroup();

      assertEquals( 1, group.getMnemonicIndex() );
      shell.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.protocolSet( "w3", { "text" : "baa" } );

      assertEquals( null, group.getMnemonicIndex() );

      shell.destroy();
    },

    testRenderMnemonic_OnActivate : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.flush();
      shell.setActive( true );

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", group.getLegend() );
      shell.destroy();
    },

    testRenderMnemonic_OnDeActivate : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var group = createMnemonicGroup();
      TestUtil.flush();
      shell.setActive( true );

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", group.getLegend() );
      shell.destroy();
    },

    testMnemonicTrigger_FocusFirstChild : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
    }

    // TODO [tb] : breaks IE7 (commented to be able to run all other tests)
//    testApplyGroupLabelId : function(){
//      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var shell = TestUtil.createShellByProtocol( "w2" );
//      var processor = rwt.remote.MessageProcessor;
//      processor.processOperation( {
//        "target" : "w3",
//        "action" : "create",
//        "type" : "rwt.widgets.Group",
//        "properties" : {
//          "style" : [],
//          "parent" : "w2"
//        }
//      } );
//      var ObjectManager = rwt.remote.ObjectRegistry;
//      var widget = ObjectManager.getObject( "w3" );
//      var labelObject = widget.getLegendObject().getLabelObject();
//
//      assertEquals( "w3-label", labelObject.getHtmlAttribute( "id" ) );
//
//      shell.destroy();
//      widget.destroy();
//    }

  }

} );

var createMnemonicGroup = function() {
  var processor = rwt.remote.MessageProcessor;
  processor.processOperation( {
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

}());