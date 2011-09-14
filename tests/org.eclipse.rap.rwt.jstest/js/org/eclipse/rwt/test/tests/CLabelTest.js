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

qx.Class.define( "org.eclipse.rwt.test.tests.CLabelTest", {

  extend : qx.core.Object,

  members : {

    testCreateCLabelByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [ "LEFT" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof qx.ui.basic.Atom );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_LEFT" ) );
      assertEquals( "clabel", widget.getAppearance() );
      var labelObject = widget.getLabelObject();
      assertEquals( "html", labelObject.getMode() );
      assertEquals( false, labelObject.getTextOverflow() );
      assertEquals( "label-graytext", labelObject.getAppearance() );
      assertEquals( "", widget.getLabel() );
      assertEquals( true, widget.getHideFocus() );
      assertTrue( widget.hasEventListeners( "mouseover" ) );
      assertTrue( widget.hasEventListeners( "mouseout" ) );
      assertEquals( "both", widget.getShow() );
      shell.destroy();
      widget.destroy();
    },

    testCreateCLabelWithShadowInByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [ "SHADOW_IN" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_SHADOW_IN" ) );
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
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "  foo && <\n> \" bar "
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var labelObject = widget.getLabelObject();
      assertEquals( "&nbsp; foo &amp; &lt;<br/>&gt; &quot; bar&nbsp;", labelObject.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "image" : [ "image.png", 10, 20 ]
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( "image.png", widget.getIcon() );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [ "RIGHT" ],
          "parent" : "w2",
          "alignment" : "right"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( "right", widget.getLabelObject().getTextAlign() );
      assertEquals( "right", widget.getHorizontalChildrenAlign() );
      shell.destroy();
      widget.destroy();
    },

    testSettMarginByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CLabel",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "leftMargin" : 1,
          "topMargin" : 2,
          "rightMargin" : 3,
          "bottomMargin" : 4
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 1, widget.getPaddingLeft() );
      assertEquals( 2, widget.getPaddingTop() );
      assertEquals( 3, widget.getPaddingRight() );
      assertEquals( 4, widget.getPaddingBottom() );
      shell.destroy();
      widget.destroy();
    }

  }
  
} );