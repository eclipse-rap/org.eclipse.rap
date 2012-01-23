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

qx.Class.define( "org.eclipse.rwt.test.tests.WidgetTest", {
  extend : qx.core.Object,
  
  members : {
        
    testRenderComplexBorder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      var element = widget.getElement();
      assertIdentical( element, widget._getTargetNode() );
      widget.setBorder( this._getComplexBorder() );
      TestUtil.flush();
      var isGecko = org.eclipse.rwt.Client.isGecko();
      if( isGecko ) {        
        assertIdentical( element, widget._getTargetNode() );
      } else {
        assertIdentical( element, widget._getTargetNode().parentNode );
      }
      widget.destroy();        
    },
    
    testLayoutTargetNodeWithNoBorder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      widget.prepareEnhancedBorder();
      TestUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = org.eclipse.rwt.Client.isMshtml();
      if( isMshtml ) {
        var bounds = TestUtil.getElementBounds( targetNode )
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createWidget();
      widget.setBorder( this._getComplexBorder() );
      widget.prepareEnhancedBorder();
      TestUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = org.eclipse.rwt.Client.isMshtml();
      if( isMshtml ) {
        var bounds = TestUtil.getElementBounds( targetNode )
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        log.push( event );
      };
      parent.addEventListener( "insertDom", logger );
      child1.addEventListener( "insertDom", logger );
      child2.addEventListener( "insertDom", logger );
      TestUtil.flush();
      assertEquals( 2, log.length );
      child2.setDisplay( true );
      TestUtil.flush();
      assertEquals( 3, log.length );
      parent.destroy();
      child1.destroy();
      child2.destroy();
    },

    testNoInsertDomEventOnParentInsert : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent1 = new qx.ui.layout.CanvasLayout();
      parent1.addToDocument();
      var child1 = new qx.ui.basic.Terminator();
      var log = [];
      child1.addEventListener( "insertDom", function( event ) {
        log.push( child1.getElement().parentNode );
      } );
      parent1.addEventListener( "insertDom", function( event ) {
        log.push( "parent" );
      } );
      child1.setParent( parent1 );
      TestUtil.flush();
      assertIdentical( parent1._getTargetNode(), child1.getElement().parentNode );
      assertEquals( 2, log.length );
      assertIdentical( "parent", log[ 0 ] );
      assertIdentical( parent1._getTargetNode(), log[ 1 ] );
      parent1.destroy();
      child1.destroy();
    },

    testInsertDomEventLazy : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      TestUtil.flush();
      // Note: parent must be seeable for the lazy queue to be used in Widget.js
      assertTrue( parent.isSeeable() ); 
      // Note: we need at least 3 siblings for the documentFragment to be used
      var child1 = new qx.ui.basic.Terminator();
      var child2 = new qx.ui.basic.Terminator();
      var child3 = new qx.ui.basic.Terminator();
      child1.setParent( parent );
      child2.setParent( parent );
      child3.setParent( parent );
      var log = [];
      var logger = function( event ) {
        log.push( child1.getElement().parentNode );
      }
      child1.addEventListener( "insertDom", logger );
      TestUtil.flush();
      assertEquals( [ parent.getElement() ], log );
      parent.destroy();
      child1.destroy();
      child2.destroy();
      child3.destroy();
    },
    
    testInsertDomEventFastQueue : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      var child = new qx.ui.basic.Terminator();
      child.setParent( parent );
      var log = [];
      var logger = function( event ) {
        log.push( child.getElement().parentNode );
      }
      child.addEventListener( "insertDom", logger );
      TestUtil.flush();
      assertEquals( [ parent.getElement() ], log );
      parent.destroy();
      child.destroy();
    },

	  // See Bug 359665 - "Background transparent don't work in IE"
    testNoInsertDomEventOnRoundedBorderRender : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      TestUtil.flush();
      var child = new qx.ui.basic.Terminator();
      var log = [];
      var logger = function( event ) {
        log.push( child.getElement().parentNode );
      }
      child.addEventListener( "insertDom", logger );
      child.setParent( parent );
      parent.setBorder( new org.eclipse.rwt.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] ) );
      TestUtil.flush();
      assertEquals( 1, log.length );
      assertIdentical( parent._getTargetNode(), log[ 0 ] );
      parent.destroy();
      child.destroy();
    },

    testRemoveDom : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      var child = new qx.ui.basic.Terminator();
      child.setParent( parent );
      TestUtil.flush();
      assertTrue( child.isInDom() );
      TestUtil.flush();
      child.setDisplay( false );
      TestUtil.flush();
      assertFalse( child.isInDom() );
      parent.destroy();
      child.destroy();
    },
    
    testInsertDomEventOnPrepareEnhancedBorder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new qx.ui.layout.CanvasLayout();
      parent.addToDocument();
      var child = new qx.ui.basic.Terminator();
      child.setParent( parent );
      var log = [];
      var logger = function( event ) {
        log.push( null );
      }
      parent.addEventListener( "insertDom", logger );
      child.addEventListener( "insertDom", logger );
      TestUtil.flush();
      assertEquals( 2, log.length );
      parent.prepareEnhancedBorder();
      assertEquals( 4, log.length );
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      var adapter = widget.getAdapter( org.eclipse.rwt.WidgetRenderAdapter );
      widget.destroy();
      TestUtil.flush();
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        assertFalse( result === "" );
        widget.setBackgroundGradient( null );
        TestUtil.flush();
        var result = this._getCssGradient( widget.getElement() );
        assertTrue( result === "" );
        widget.destroy();
      }
    },
    
    testRemoveBackgroundGradientAndRestoreBakgroundColor : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundColor( "red" );
        TestUtil.flush();
        assertEquals( "red", TestUtil.getCssBackgroundColor( widget ) );
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
        widget.setBackgroundGradient( null );
        TestUtil.flush();
        assertEquals( "red", TestUtil.getCssBackgroundColor( widget ) );
        widget.destroy();
      }
    },
    
    testRemoveBackgroundGradientAndRestoreBakgroundImage : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundImage( "bla.png" );
        TestUtil.flush();
        assertTrue( TestUtil.getCssBackgroundImage( widget.getElement() ).indexOf( "bla.png" ) !== -1 );
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
        widget.setBackgroundGradient( null );
        TestUtil.flush();
        assertTrue( TestUtil.getCssBackgroundImage( widget.getElement() ).indexOf( "bla.png" ) !== -1 );
        widget.destroy();
      }
    },

    testRenderHorizontalBackgroundGradient : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        gradient.horizontal = true;
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( gradient );
        TestUtil.flush();
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setShadow( shadow );
        TestUtil.flush();
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
    
    testRenderBoxShadowInset : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var shadow = [ true, 3, 5, 1, 0, "#090807", 0.4 ];
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setShadow( shadow );
        TestUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        assertTrue( result.indexOf( "inset" ) !== -1 );
        widget.destroy();
      }
    },
    
    testRemoveBoxShadow : function() {
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var shadow = [ false, 3, 5, 1, 0, "#090807", 0.4 ];
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setShadow( shadow );
        TestUtil.flush();
        widget.setShadow( null );
        TestUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        assertEquals( "", result );
        widget.destroy();
      }
    },

    testBackgroundColorTransparent : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "newmshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        assertEquals( "rgba(0, 0, 0, 0)", widget._style.backgroundColor );
        widget.setBackgroundColor( "red" );
        widget.setBackgroundColor( "transparent" );
        assertEquals( "rgba(0, 0, 0, 0)", widget._style.backgroundColor );
        widget.setBackgroundColor( "red" );
        widget.setBackgroundColor( null );
        assertEquals( "rgba(0, 0, 0, 0)", widget._style.backgroundColor );
        widget.destroy();
      }
    } ),

    testDisableOfFocused : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addToDocument();
      button.setFocused( true );
      assertTrue( button.getFocused() );
      button.setEnabled( false );
      assertFalse( button.getFocused() );
    },

    testApplyObjectId_default : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addToDocument();

      button.applyObjectId( "w23" );

      assertIdentical( "", button.getHtmlAttribute( "id" ) );
    },

    testApplyObjectId_whenActivated : function() {
      qx.ui.core.Widget._renderHtmlIds = true;
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addToDocument();

      button.applyObjectId( "w23" );

      assertEquals( "w23", button.getHtmlAttribute( "id" ) );
      qx.ui.core.Widget._renderHtmlIds = false;
    },

    /////////
    // Helper
    
    _createWidget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      widget.setWidth( 100 );        
      widget.setHeight( 100 );
      TestUtil.flush();
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