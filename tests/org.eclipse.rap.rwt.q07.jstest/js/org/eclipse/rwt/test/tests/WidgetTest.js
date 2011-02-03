/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.WidgetTest", {
  extend : qx.core.Object,
  
  members : {
        
    testRenderComplexBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      var element = widget.getElement();
      assertIdentical( element, widget._getTargetNode() );
      widget.setBorder( this._getComplexBorder() );
      testUtil.flush();
      var isGecko = org.eclipse.rwt.Client.isGecko();
      if( isGecko ) {        
        assertIdentical( element, widget._getTargetNode() );
      } else {
        assertIdentical( element, widget._getTargetNode().parentNode );
      }
      widget.destroy();        
    },
    
    testLayoutTargetNodeWithNoBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      widget.prepareEnhancedBorder();
      testUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = org.eclipse.rwt.Client.isMshtml();
      if( isMshtml ) {
        var bounds = testUtil.getElementBounds( targetNode )
        var expected = {
          "top" : 0,
          "left" : 0,
          "width" : 100,
          "height" : 100,
          "bottom" : 0,
          "right" : 0
        };
        assertEquals( expected, bounds );
      } else {
        assertEquals( "100%", targetNode.style.width );
        assertEquals( "100%", targetNode.style.height );
      }
      widget.destroy();
    },
    
    testLayoutTargetNodeWithComplexBorder : function() {
      // NOTE: layouting with rounded border in GraphicsMixinTest
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      widget.setBorder( this._getComplexBorder() );
      widget.prepareEnhancedBorder();
      testUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = org.eclipse.rwt.Client.isMshtml();
      if( isMshtml ) {
        var bounds = testUtil.getElementBounds( targetNode )
        var expected = {
          "top" : 0,
          "left" : 0,
          "width" : 98,
          "height" : 98,
          "bottom" : 0,
          "right" : 0
        };
        assertEquals( expected, bounds );
      } else {
        assertEquals( "100%", targetNode.style.width );
        assertEquals( "100%", targetNode.style.height );
      }
      widget.destroy();
    },
    
    testInsertDomEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      var child1 = new qx.ui.basic.Terminator();
      var child2 = new qx.ui.basic.Terminator();
      child1.setParent( parent );
      child2.setParent( parent );
      child1.setVisibility( false );
      child2.setDisplay( false );
      var log = [];
      var logger = function( event ) {
        log.push( event.getTarget() );
      }
      parent.addEventListener( "insertDom", logger );
      child1.addEventListener( "insertDom", logger );
      child2.addEventListener( "insertDom", logger );
      testUtil.flush();
      assertEquals( [ parent, child1 ], log );
      child2.setDisplay( true );
      testUtil.flush();
      assertEquals( [ parent, child1, child2 ], log );
      parent.destroy();
      child1.destroy();
      child2.destroy();
    },
    
    testInsertDomEventOnPrepareEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      var child = new qx.ui.basic.Terminator();
      child.setParent( parent );
      var log = [];
      var logger = function( event ) {
        log.push( event.getTarget() );
      }
      parent.addEventListener( "insertDom", logger );
      child.addEventListener( "insertDom", logger );
      testUtil.flush();
      assertEquals( [ parent, child ], log );
      parent.prepareEnhancedBorder();
      assertEquals( [ parent, child, parent, child ], log );
      parent.destroy();
      child.destroy();
    },
    
    testGetWidgetWidgetRenderAdapter : function() {
      var widget = new qx.ui.basic.Terminator();
      var adapter1 = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      assertTrue( adapter1 instanceof org.eclipse.rwt.WidgetRenderAdapter );
      var adapter2 = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      assertIdentical( adapter1, adapter2 );
      widget.destroy();
    },
    
    testPreventMultipleWidgetRenderAdapter : function() {
      var widget = new qx.ui.basic.Terminator();
      var adapter1 = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      assertTrue( adapter1 instanceof org.eclipse.rwt.WidgetRenderAdapter );
      var error = null;
      try {
        var adapter2 = new org.eclipse.rwt.WidgetRenderAdapter( widget );
      } catch( ex ) {
        error = ex;
      }
      assertNotNull( error );
      widget.destroy();
    },
    
    testDisposeWidgetRenderAdapterWithWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      widget.destroy();
      testUtil.flush();
      assertTrue( adapter.isDisposed() );
    },
    
    testRenderVisibilityListener : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      var log = [];
      var logger = function( event ) {
        log.push( event.getData() );
        log.push( this );
      };
      adapter.addRenderListener( "visibility", logger, this );
      widget.hide();
      assertEquals( [ false, this ], log );
      assertEquals( "none", widget.getStyleProperty( "display" ) );
      widget.destroy();
    },
    
    testAddMultipleRenderVisibilityListener : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      var log = [];
      var logger1 = function( event ) {
        log.push( 1 );
      };
      var logger2 = function( event ) {
        log.push( 2 );
      };
      adapter.addRenderListener( "visibility", logger1, this );
      adapter.addRenderListener( "visibility", logger2, this );
      widget.hide();
      assertEquals( [ 1, 2 ], log );
      assertEquals( "none", widget.getStyleProperty( "display" ) );
      widget.destroy();
    },

    testRenderListenerPreventDefault : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      var log = [];
      var logger = function( event ) {
        log.push( event )
        event.preventDefault();
      };
      adapter.addRenderListener( "visibility", logger, this );
      widget.hide();
      assertEquals( 1, log.length );
      assertEquals( "", widget.getStyleProperty( "display" ) );
      widget.destroy();
    },

    testRenderAdapterForceRender : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      var log = [];
      var logger = function( event ) {
        log.push( event )
        event.preventDefault();
      };
      adapter.addRenderListener( "visibility", logger, this );
      widget.hide();
      assertEquals( 1, log.length );
      assertEquals( "", widget.getStyleProperty( "display" ) );
      adapter.forceRender( "visibility", false )
      assertEquals( "none", widget.getStyleProperty( "display" ) );
      assertEquals( 1, log.length );
      widget.destroy();
    },

    testRemoveRenderListener : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      var log = [];
      var logger = function( event ) {
        log.push( event );
      };
      adapter.addRenderListener( "visibility", logger, this );
      widget.hide();
      assertEquals( 1, log.length );
      adapter.removeRenderListener( "visibility", logger, this );
      widget.show();
      assertEquals( 1, log.length );
      assertEquals( "", widget.getStyleProperty( "display" ) );
      widget.destroy();
    },
    
    /////////
    // Helper
    
    _createWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      widget.setWidth( 100 );        
      widget.setHeight( 100 );
      testUtil.flush();
      return widget;
    },
    
    _getComplexBorder : function() {
      var complexBorder = new qx.ui.core.Border( 2, "outset" );
      complexBorder.setColor( "green" );
      complexBorder.setInnerColor( "red" );
      return complexBorder;
    }
    
    
    
  }
} );