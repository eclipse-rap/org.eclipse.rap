/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.CompositeTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateCompositeByProtocol : function() {
      var processor = rwt.protocol.MessageProcessor;
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
        "type" : "rwt.widgets.Composite",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w2" );
      var composite = widgetManager.findWidgetById( "w3" );
      assertTrue( composite instanceof rwt.widgets.Composite );
      assertIdentical( shell, composite.getParent() );
      assertTrue( composite.getUserData( "isControl") );
      assertTrue( composite.hasState( "rwt_BORDER" ) );
    },

    testCompositeBackgroundInitial : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var composite = new rwt.widgets.Composite();
        composite.setBackgroundColor( null );
        composite.setBackgroundImage( null );
        composite.addToDocument();
        TestUtil.flush();
        var image = composite._getTargetNode().style.backgroundImage;
        assertTrue( image.indexOf( "blank.gif" ) != -1 );
        composite.destroy();
        TestUtil.flush();
      },
      "default" : function(){}
    } ),
        
    testCompositeBackgroundFromColor : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var composite = new rwt.widgets.Composite();
        composite.setBackgroundColor( "green" );
        composite.setBackgroundImage( null );
        composite.addToDocument();
        TestUtil.flush();
        var image = composite._getTargetNode().style.backgroundImage;
        assertEquals( "", image );
        composite.setBackgroundColor( null );
        image = composite._getTargetNode().style.backgroundImage;
        assertTrue( image.indexOf( "blank.gif" ) != -1 );
        composite.destroy();
        TestUtil.flush();
      },
      "default" : function(){}
    } ),
        
    testCompositeBackgroundFromImage : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var composite = new rwt.widgets.Composite();
        composite.setBackgroundColor( null );
        composite.setBackgroundImage( "bla.jpg" );
        composite.addToDocument();
        TestUtil.flush();
        var image = composite._getTargetNode().style.backgroundImage;
        assertEquals( "url(bla.jpg)", image );
        composite.setBackgroundImage( null );
        image = composite._getTargetNode().style.backgroundImage;
        assertTrue( image.indexOf( "blank.gif" ) != -1 );
        composite.destroy();
        TestUtil.flush();
      },
      "default" : function(){}
    } )

  }
  
} );