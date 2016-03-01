/*******************************************************************************
 * Copyright (c) 2010, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.CompositeTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateCompositeByProtocol : function() {
      MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "create",
        "type" : "rwt.widgets.Shell",
        "properties" : {
          "style" : [ "BORDER" ]
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Composite",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      } );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w2" );
      var composite = widgetManager.findWidgetById( "w3" );
      assertTrue( composite instanceof rwt.widgets.Composite );
      assertIdentical( shell, composite.getParent() );
      assertTrue( composite.getUserData( "isControl") );
      assertTrue( composite.hasState( "rwt_BORDER" ) );
    },

    testDestroyCompositeWithChildrenByProtocol : function() {
      MessageProcessor.processOperationArray( [ "create", "w2", "rwt.widgets.Shell", {
          "style" : [ "BORDER" ]
        }
      ] );
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      MessageProcessor.processOperationArray( [ "create", "w4", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var shell  = ObjectRegistry.getObject( "w2" );
      var parent = ObjectRegistry.getObject( "w3" );
      var child  = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperationArray( [ "destroy", "w3"] );
      TestUtil.flush();

      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( parent.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( child.isDisposed() );
      shell.destroy();
    },

    testDestroyCompositeWithGCByProtocol : function() {
      MessageProcessor.processOperationArray( [ "create", "w2", "rwt.widgets.Shell", {
          "style" : [ "BORDER" ]
        }
      ] );
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      MessageProcessor.processOperationArray( [ "create", "w4", "rwt.widgets.GC", {
          "parent" : "w3"
        }
      ] );
      var shell  = ObjectRegistry.getObject( "w2" );
      var parent = ObjectRegistry.getObject( "w3" );
      var child  = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperationArray( [ "destroy", "w3"] );
      TestUtil.flush();

      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( parent.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( child.isDisposed() );
      shell.destroy();
    },

    testMouseOver : function() {
      var composite = new rwt.widgets.Composite();
      composite.addToDocument();
      TestUtil.flush();

      TestUtil.mouseOver( composite );
      assertTrue( composite.hasState( "over" ) );

      TestUtil.mouseOut( composite );
      assertFalse( composite.hasState( "over" ) );

      composite.destroy();
    },

    testMouseOver_onChild : function() {
      var composite = new rwt.widgets.Composite();
      composite.setDimension( 200, 200 );
      composite.addToDocument();
      var child = new rwt.widgets.base.Terminator();
      child.setSpace( 50, 100, 50, 100 );
      child.setParent( composite );
      TestUtil.flush();

      TestUtil.mouseOver( child );
      assertTrue( composite.hasState( "over" ) );

      TestUtil.mouseFromTo( child, composite );
      assertTrue( composite.hasState( "over" ) );

      composite.destroy();
    },

    testSetDirection_mirrorsChildControlPosition : function() {
      var composite = new rwt.widgets.Composite();
      composite.addToDocument();
      var child = new rwt.widgets.base.Terminator();
      child.setParent( composite );
      child.setSpace( 1, 2, 3, 4 );

      composite.setDirection( "rtl" );
      TestUtil.flush();

      assertEquals( "", child.getElement().style.left );
      assertEquals( "1px", child.getElement().style.right );
      composite.destroy();
    },

    testSetBoundsByProtocol_onParentDirectionRTL : function() {
      var parent = TestUtil.createShellByProtocol( "w2" );
      TestUtil.protocolSet( "w2", { "direction" : "rtl" } );
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "bounds" : [ 1, 2, 3, 4 ]
        }
      ] );
      TestUtil.flush();

      var child = ObjectRegistry.getObject( "w3" );
      assertEquals( "", child.getElement().style.left );
      assertEquals( "1px", child.getElement().style.right );
      parent.destroy();
    },

    testSetBoundsByProtocol_onParentDirectionLTR : function() {
      var parent = TestUtil.createShellByProtocol( "w2" );
      TestUtil.protocolSet( "w2", { "direction" : "ltr" } );
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "bounds" : [ 1, 2, 3, 4 ]
        }
      ] );
      TestUtil.flush();

      var child = ObjectRegistry.getObject( "w3" );
      assertEquals( "1px", child.getElement().style.left );
      assertEquals( "", child.getElement().style.right );
      parent.destroy();
    }

  }

} );

}());
