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

qx.Class.define( "org.eclipse.rwt.test.tests.LabelTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "LEFT" ],
          "parent" : "w2"
        }
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w2" );
      var widget = widgetManager.findWidgetById( "w3" );
      assertTrue( widget instanceof qx.ui.basic.Atom );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_LEFT" ) );
      // properties set by LabelUtil:
      assertEquals( "left", widget.getHorizontalChildrenAlign() );
      assertEquals( "top", widget.getVerticalChildrenAlign() );
      assertEquals( "label-wrapper", widget.getAppearance() );
      assertEquals( "hidden", widget.getOverflow() );
      var labelObject = widget.getLabelObject();
      assertEquals( "html", labelObject.getMode() );
      assertEquals( false, labelObject.getTextOverflow() );
      assertEquals( "label-graytext", labelObject.getAppearance() );
      assertEquals( "", widget.getLabel() );
      assertEquals( true, widget.getHideFocus() );
      assertTrue( widget.hasEventListeners( "mouseover" ) );
      assertTrue( widget.hasEventListeners( "mouseout" ) );
      assertFalse( labelObject.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testCreateByProtocolWithWRAP : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "WRAP" ],
          "parent" : "w2"
        }
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w2" );
      var widget = widgetManager.findWidgetById( "w3" );
      var labelObject = widget.getLabelObject();
      assertTrue( labelObject.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testSetText : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "WRAP" ],
          "parent" : "w2",
          "text" : "bla  \n<"
        }
      } );
      TestUtil.flush(); // LabelUtil delays setter for some reason
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w2" );
      var widget = widgetManager.findWidgetById( "w3" );
      var labelObject = widget.getLabelObject();
      var expected = "bla&nbsp; <br/>&lt;"
      assertEquals( expected, labelObject.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "WRAP" ],
          "parent" : "w2",
          "image" : [ "image.png", 10, 20 ]
        }
      } );
      TestUtil.flush(); // LabelUtil delays setter for some reason
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "image.png", widget.getIcon() );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "WRAP" ],
          "parent" : "w2",
          "alignment" : "right"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "right", widget.getLabelObject().getTextAlign() );
      assertEquals( "right", widget.getHorizontalChildrenAlign() );
      shell.destroy();
      widget.destroy();
    }

  }
  
} );