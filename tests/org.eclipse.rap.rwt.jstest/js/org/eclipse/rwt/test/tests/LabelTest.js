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

(function(){

var Processor = org.eclipse.rwt.protocol.Processor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;

var shell;
var labelWidget;

qx.Class.define( "org.eclipse.rwt.test.tests.LabelTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateByProtocol : function() {
      this.createLabel( [ "LEFT" ] );

      assertIdentical( shell, labelWidget.getParent() );
      assertTrue( labelWidget.getUserData( "isControl") );
      assertTrue( labelWidget.hasState( "rwt_LEFT" ) );
      assertEquals( "left", labelWidget.getHorizontalChildrenAlign() );
      assertEquals( "top", labelWidget.getVerticalChildrenAlign() );
      assertEquals( "label-wrapper", labelWidget.getAppearance() );
      assertEquals( "hidden", labelWidget.getOverflow() );
      var labelObject = labelWidget.getLabelObject();
      assertEquals( "html", labelObject.getMode() );
      assertEquals( false, labelObject.getTextOverflow() );
      assertEquals( "label-graytext", labelObject.getAppearance() );
      assertEquals( "", labelWidget.getLabel() );
      assertEquals( true, labelWidget.getHideFocus() );
      assertTrue( labelWidget.hasEventListeners( "mouseover" ) );
      assertTrue( labelWidget.hasEventListeners( "mouseout" ) );
      assertFalse( labelObject.getWrap() );
    },

    testCreateByProtocolWithWRAP : function() {
      this.createLabel( [ "WRAP" ] );

      assertTrue( labelWidget.getLabelObject().getWrap() );
    },

    testSetText : function() {
      this.createLabel( [ "LEFT" ], { "text" : "bla  \n<" } );

      var content = this.getTextElement().innerHTML.toLowerCase().replace(/^\s+|\s+$/g, '');
      assertTrue( content === "bla&nbsp; <br>&lt;" || content === "bla&nbsp; <br/>&lt;" );
    },

    testSetImageByProtocol : function() {
      this.createLabel( [ "LEFT" ], { "image" : [ "image.png", 10, 20 ] } );

      assertEquals( "image.png", labelWidget.getIcon() );
      // TODO [tb] : test url directly, currently not possible
//      var src = TestUtil.getCssBackgroundImage( this.getImageElement() );
//      assertTrue( src.indexOf( "image.png" ) !== -1 );
    },

    testSetAlignmentByProtocol : function() {
      // TODO [tb] : setting a text should not be necessary
      this.createLabel( [ "LEFT" ], { "alignment" : "right", "text" : "bla" } ); 

      assertEquals( "right", labelWidget.getHorizontalChildrenAlign() );
      assertEquals( "right", this.getTextElement().style.textAlign );
    },
    
    /////////
    // helper
    
    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },
    
    tearDown : function() {
      shell.destroy();
      shell = null;
      if( labelWidget !== null ) {
        labelWidget.destroy();
      }
      labelWidget = null;
    },
    
    createLabel : function( styles, properties ) {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : styles,
          "parent" : "w2"
        }
      } );
      if( properties ) {
        Processor.processOperation( {
          "target" : "w3",
          "action" : "set",
          "properties" : properties
        } );
      }
      labelWidget = ObjectManager.getObject( "w3" );
      TestUtil.flush(); // LabelUtil delays setter for some BS reason...
      TestUtil.flush(); // TWICE!!
    },
    
    getTextElement : function() {
      return labelWidget.getLabelObject()._getTargetNode();
    },

    getImageElement : function() {
      return labelWidget.getIconObject()._image;
    }

  }
  
} );

}());