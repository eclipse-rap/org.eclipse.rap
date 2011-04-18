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
    
    testRenderSimpleBackgroundGradient : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        var expected1 = "gradient(-90deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
        var expected2 = "gradient(linear, 0% 0%, 0% 100%, from(rgb(255, 0, 255)), to(rgb(0, 255, 0)))";
        assertTrue( result === expected1 || result === expected2 );
        widget.destroy();
      }
    },
    
    testRemoveBackgroundGradient : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        assertFalse( result === "" );
        widget.setBackgroundGradient( null );
        testUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        assertTrue( result === "" );
        widget.destroy();
      }
    },
    
    testRemoveBackgroundGradientAndRestoreBakgroundColor : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundColor( "red" );
        testUtil.flush();
        assertEquals( "red", testUtil.getCssBackgroundColor( widget ) );
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        widget.setBackgroundGradient( null );
        testUtil.flush();
        assertEquals( "red", testUtil.getCssBackgroundColor( widget ) );
        widget.destroy();
      }
    },
    
    testRemoveBackgroundGradientAndRestoreBakgroundImage : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundImage( "bla.png" );
        testUtil.flush();
        assertTrue( testUtil.getCssBackgroundImage( widget.getElement() ).indexOf( "bla.png" ) !== -1 );
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        widget.setBackgroundGradient( null );
        testUtil.flush();
        assertTrue( testUtil.getCssBackgroundImage( widget.getElement() ).indexOf( "bla.png" ) !== -1 );
        widget.destroy();
      }
    },

    testRenderHorizontalBackgroundGradient : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        gradient.horizontal = true;
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        var expected1 = "gradient(0deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
        var expected2 = "gradient(linear, 0% 0%, 100% 0%, from(rgb(255, 0, 255)), to(rgb(0, 255, 0)))";
        assertTrue( result === expected1 || result === expected2 );
        widget.destroy();
      }
    },
    
    testRenderComplexBackgroundGradient : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ 
          [ 0, "rgb(255, 0, 255)" ], 
          [ 0.33, "rgb(255, 128, 255)" ],
          [ 1, "rgb(0, 255, 0)" ] 
        ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        testUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        var expected1 =   "gradient(-90deg, rgb(255, 0, 255) 0%, " 
                        + "rgb(255, 128, 255) 33%, rgb(0, 255, 0) 100%)";
        var expected2 = "gradient(linear, 0% 0%, 0% 100%, from(rgb(255, 0, 255)), color-stop(0.33, rgb(255, 128, 255)), to(rgb(0, 255, 0)))";
        assertTrue( result === expected1 || result === expected2 );
        widget.destroy();
      }
    },
    
    testRenderBoxShadow : function() {
//      Syntax for shadow:
//      [
//         inset, //boolean, currently not supported
//         offsetX, // positive or negative number
//         offsetY, // positive or negative number
//         blurRadius, // positive number or zero
//         spread, // positive or negative number, currently not supported
//         color, // string
//         opacity, // number between 0 and 1
//      ]
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var shadow = [ false, 3, 5, 1, 0, "#090807", 0.4 ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setShadow( shadow );
        testUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        var expected;
        if( org.eclipse.rwt.Client.isWebkit() ) {
          // webkit currently outputs "rgba(9, 8, 7, 0.398438) 3px 5px 1px"
          assertTrue( result.indexOf( "3px 5px 1px" ) !== -1 );
          assertTrue( result.indexOf( "rgba(9, 8, 7, 0." ) !== -1 );
        } else {
          expected1 = "3px 5px 1px rgba(9, 8, 7, 0.4)"
          expected2 = "3px 5px 1px rgba(9,8,7,0.4)"
          assertTrue( result === expected1 || result === expected2 );
        }
        widget.destroy();
      }
    },
    
    testRemoveBoxShadow : function() {
//      Syntax for shadow:
//      [
//         inset, //boolean, currently not supported
//         offsetX, // positive or negative number
//         offsetY, // positive or negative number
//         blurRadius, // positive number or zero
//         spread, // positive or negative number, currently not supported
//         color, // string
//         opacity, // number between 0 and 1
//      ]
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var shadow = [ false, 3, 5, 1, 0, "#090807", 0.4 ];
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setShadow( shadow );
        testUtil.flush();
        widget.setShadow( null );
        testUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        assertEquals( "", result );
        widget.destroy();
      }
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
      return new org.eclipse.rwt.Border( 2, "complex", "green", "red" );
    },
    
    _getCssGradient : function( element ) {
      var result = "";
      var background = element.style.background;
      var start = background.indexOf( "gradient(" );
      if( start !== -1 ) {
        var end = background.indexOf( ") repeat", start );
        if( end != -1 ) {
          result = background.slice( start, end + 1 );
        } else {
          result = background.slice( start );          
        }
      }
      return result;
    },
    
    _getCssShadow : function( element ) {
      var result = element.style.boxShadow;
      if( !result ) {
        result = element.style[ "-webkit-box-shadow" ];
      }
      if( !result ) {
        result = "";
      }
      return result;
    }
      
  }
} );