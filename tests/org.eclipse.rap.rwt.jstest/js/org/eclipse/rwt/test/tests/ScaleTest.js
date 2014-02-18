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
var ObjectManager = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ScaleTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateScaleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Scale );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "scale", widget.getAppearance() );
      assertFalse( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testCreateScaleHorizontalByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Scale );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "scale", widget.getAppearance() );
      assertTrue( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._minimum );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget._maximum );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._selection );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 5, widget._increment );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 20, widget._pageIncrement );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Scale",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );

      TestUtil.protocolListen( "w3", { "Selection" : true } );

      var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( widget );
      assertTrue( remoteObject.isListening( "Selection" ) );
      shell.destroy();
      widget.destroy();
    },

    testFiresSelectionChangedEvent : function() {
      var scale = new rwt.widgets.Scale();
      TestUtil.flush();

      var log = 0;
      scale.addEventListener( "selectionChanged", function() {
        log++;
      } );
      scale.setSelection( 33 );

      assertEquals( 1, log );
    },

    testFiresMinimumChangedEvent : function() {
      var scale = new rwt.widgets.Scale();
      TestUtil.flush();

      var log = 0;
      scale.addEventListener( "minimumChanged", function() {
        log++;
      } );
      scale.setMinimum( 5 );

      assertEquals( 1, log );
    },

    testFiresMaximumChangedEvent : function() {
      var scale = new rwt.widgets.Scale();
      TestUtil.flush();

      var log = 0;
      scale.addEventListener( "maximumChanged", function() {
        log++;
      } );
      scale.setMaximum( 100 );

      assertEquals( 1, log );
    },

    testGetToolTipTargetBounds_Horizontal : function() {
      var scale = new rwt.widgets.Scale( true );
      scale.setDimension( 100, 20 );
      scale.setSelection( 30 );
      scale.addToDocument();
      scale.setBorder( new rwt.html.Border( 2 ) );
      TestUtil.flush();

      var bounds = scale.getToolTipTargetBounds();

      var thumb = scale._thumb.getElement();
      assertEquals( 2 + parseInt( thumb.style.left, 10 ), bounds.left );
      assertEquals( 2 + parseInt( thumb.style.top, 10 ), bounds.top );
      assertEquals( parseInt( thumb.style.width, 10 ), bounds.width );
      assertEquals( parseInt( thumb.style.height, 10 ), bounds.height );
      scale.destroy();
    },

    testGetToolTipTargetBounds_Vertical : function() {
      var scale = new rwt.widgets.Scale( false );
      scale.setDimension( 20, 100 );
      scale.setSelection( 30 );
      scale.addToDocument();
      scale.setBorder( new rwt.html.Border( 2 ) );
      TestUtil.flush();

      var bounds = scale.getToolTipTargetBounds();

      var thumb = scale._thumb.getElement();
      assertEquals( 2 + parseInt( thumb.style.left, 10 ), bounds.left );
      assertEquals( 2 + parseInt( thumb.style.top, 10 ), bounds.top );
      assertEquals( parseInt( thumb.style.width, 10 ), bounds.width );
      assertEquals( parseInt( thumb.style.height, 10 ), bounds.height );
      scale.destroy();
    },

    testSetSelection_FiresUpdateToolTip : function() {
      var scale = new rwt.widgets.Scale( false );
      scale.setDimension( 20, 100 );
      scale.setSelection( 30 );
      scale.addToDocument();
      scale.setBorder( new rwt.html.Border( 2 ) );
      TestUtil.flush();
      var fired = false;
      scale.addEventListener( "updateToolTip", function( arg ) {
        fired = arg;
      } );

      scale.setSelection( 30 );

      assertIdentical( scale, fired );
      scale.destroy();
    }

  }

} );

}() );
