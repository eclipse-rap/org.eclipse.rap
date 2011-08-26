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
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w2",
        "action" : "create",
        "type" : "rwt.widgets.Shell",
        "properties" : {
          "style" : [ "BORDER" ]
        }
      } );
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
    },

    testCreateByProtocolWithWRAP : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w2",
        "action" : "create",
        "type" : "rwt.widgets.Shell",
        "properties" : {
          "style" : [ "BORDER" ]
        }
      } );
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
      var labelObject = widgetManager.findWidgetById( "w3" ).getLabelObject();
      assertTrue( labelObject.getWrap() );
    },

    testSetText : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w2",
        "action" : "create",
        "type" : "rwt.widgets.Shell",
        "properties" : {
          "style" : [ "BORDER" ]
        }
      } );
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
      org.eclipse.rwt.test.fixture.TestUtil.flush(); // LabelUtil delays setter for some reason
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var labelObject = widgetManager.findWidgetById( "w3" ).getLabelObject();
      var expected = "bla&nbsp; <br/>&lt;"
      assertEquals( expected, labelObject.getText() );
    }

  }
  
} );