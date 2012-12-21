/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.WidgetTest", {

  extend : rwt.qx.Object,

  members : {

    testRenderComplexBorder : function() {
      var widget = this._createWidget();
      var element = widget.getElement();
      assertIdentical( element, widget._getTargetNode() );
      widget.setBorder( this._getComplexBorder() );
      TestUtil.flush();
      var isGecko = rwt.client.Client.isGecko();
      if( isGecko ) {
        assertIdentical( element, widget._getTargetNode() );
      } else {
        assertIdentical( element, widget._getTargetNode().parentNode );
      }
      widget.destroy();
    },

    testRenderNormalBorderAfterComplexBorder : function() {
      if( rwt.client.Client.isMshtml() ) {
        var widget = this._createWidget();
        widget.setBorder( this._getComplexBorder() );
        TestUtil.flush();
        widget.setBorder( new rwt.html.Border( [ 5, 6, 7, 8 ], "solid", "black" ) );
        TestUtil.flush();

        var target = widget._getTargetNode();
        var bounds = TestUtil.getElementBounds( target );

        assertEquals( 0, bounds.left );
        assertEquals( 0, bounds.top );
        assertEquals( 0, bounds.right );
        assertEquals( 0, bounds.bottom );

        widget.destroy();
      }
    },

    testRenderTooBigNormalBorderAfterComplexBorder : function() {
      if( rwt.client.Client.isMshtml() ) {
        var widget = this._createWidget();
        widget.setWidth( 6 );
        widget.setBorder( this._getComplexBorder() );
        TestUtil.flush();
        widget.setBorder( new rwt.html.Border( [ 5, 6, 7, 8 ], "solid", "black" ) );
        TestUtil.flush();

        var target = widget._getTargetNode();
        var bounds = TestUtil.getElementBounds( target );

        assertEquals( 0, bounds.left );
        assertEquals( 0, bounds.top );
        assertEquals( 0, bounds.width );
        assertEquals( 0, bounds.bottom );

        widget.destroy();
      }
    },

    testLayoutTargetNodeWithNoBorder : function() {
      var widget = this._createWidget();
      widget.prepareEnhancedBorder();
      TestUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = rwt.client.Client.isMshtml();
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
      var widget = this._createWidget();
      widget.setBorder( this._getComplexBorder() );
      widget.prepareEnhancedBorder();
      TestUtil.flush();
      var targetNode = widget._getTargetNode();
      var isMshtml = rwt.client.Client.isMshtml();
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
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      var child1 = new rwt.widgets.base.Terminator();
      var child2 = new rwt.widgets.base.Terminator();
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
      var parent1 = new rwt.widgets.base.Parent();
      parent1.addToDocument();
      var child1 = new rwt.widgets.base.Terminator();
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
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      TestUtil.flush();
      // Note: parent must be seeable for the lazy queue to be used in Widget.js
      assertTrue( parent.isSeeable() );
      // Note: we need at least 3 siblings for the documentFragment to be used
      var child1 = new rwt.widgets.base.Terminator();
      var child2 = new rwt.widgets.base.Terminator();
      var child3 = new rwt.widgets.base.Terminator();
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
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      var child = new rwt.widgets.base.Terminator();
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
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      TestUtil.flush();
      var child = new rwt.widgets.base.Terminator();
      var log = [];
      var logger = function( event ) {
        log.push( child.getElement().parentNode );
      }
      child.addEventListener( "insertDom", logger );
      child.setParent( parent );
      parent.setBorder( new rwt.html.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] ) );
      TestUtil.flush();
      assertEquals( 1, log.length );
      assertIdentical( parent._getTargetNode(), log[ 0 ] );
      parent.destroy();
      child.destroy();
    },

    testRemoveDom : function() {
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      var child = new rwt.widgets.base.Terminator();
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
      var parent = new rwt.widgets.base.Parent();
      parent.addToDocument();
      var child = new rwt.widgets.base.Terminator();
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
      var widget = new rwt.widgets.base.Terminator();
      var adapter1 = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      assertTrue( adapter1 instanceof rwt.widgets.util.WidgetRenderAdapter );
      var adapter2 = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      assertIdentical( adapter1, adapter2 );
      widget.destroy();
    },

    testPreventMultipleWidgetRenderAdapter : function() {
      var widget = new rwt.widgets.base.Terminator();
      var adapter1 = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      assertTrue( adapter1 instanceof rwt.widgets.util.WidgetRenderAdapter );
      var error = null;
      try {
        var adapter2 = new rwt.widgets.util.WidgetRenderAdapter( widget );
      } catch( ex ) {
        error = ex;
      }
      assertNotNull( error );
      widget.destroy();
    },

    testDisposeWidgetRenderAdapterWithWidget : function() {
      var widget = new rwt.widgets.base.Terminator();
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      widget.destroy();
      TestUtil.flush();
      assertTrue( adapter.isDisposed() );
    },

    testRenderVisibilityListener : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      var log = [];
      var logger = function( args ) {
        log.push( args[ 0 ] );
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
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
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
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      var log = [];
      var logger = function( args ) {
        log.push( args[ 0 ] )
        return false;
      };
      adapter.addRenderListener( "visibility", logger, this );
      widget.hide();
      assertEquals( 1, log.length );
      assertEquals( "", widget.getStyleProperty( "display" ) );
      widget.destroy();
    },

    testRenderAdapterForceRender : function() {
      var widget = this._createWidget();
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
      var log = [];
      var logger = function( args ) {
        log.push( args[ 0 ] )
        return false;
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
      var adapter = widget.getAdapter( rwt.widgets.util.WidgetRenderAdapter );
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
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
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
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
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

    testRemoveBackgroundGradientAndRestoreBackgroundColor : function() {
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
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

    testRemoveBackgroundGradientAndRestoreBackgroundImage : function() {
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
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
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
        gradient.horizontal = true;
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
      if( rwt.client.Client.supportsCss3() ) {
        var gradient = [
          [ 0, "rgb(255, 0, 255)" ],
          [ 0.33, "rgb(255, 128, 255)" ],
          [ 1, "rgb(0, 255, 0)" ]
        ];
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
      if( rwt.client.Client.supportsCss3() ) {
        var shadow = [ false, 3, 5, 1, 0, "#090807", 0.4 ];
        var widget = this._createWidget();
        widget.setShadow( shadow );
        TestUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        var expected;
        if( rwt.client.Client.isWebkit() ) {
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
      if( rwt.client.Client.supportsCss3() ) {
        var shadow = [ true, 3, 5, 1, 0, "#090807", 0.4 ];
        var widget = this._createWidget();
        widget.setShadow( shadow );
        TestUtil.flush();
        var result = this._getCssShadow( widget.getElement() );
        assertTrue( result.indexOf( "inset" ) !== -1 );
        widget.destroy();
      }
    },

    testRemoveBoxShadow : function() {
      if( rwt.client.Client.supportsCss3() ) {
        var shadow = [ false, 3, 5, 1, 0, "#090807", 0.4 ];
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

    testBackgroundColorTransparent : rwt.util.Variant.select( "qx.client", {
      "default" : function() {},
      "newmshtml" : function() {
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
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();
      button.setFocused( true );
      assertTrue( button.getFocused() );
      button.setEnabled( false );
      assertFalse( button.getFocused() );
    },

    testApplyObjectId_default : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();

      rwt.widgets.base.Widget._renderHtmlIds = false;
      button.applyObjectId( "w23" );

      assertIdentical( "", button.getHtmlAttribute( "id" ) );
      button.destroy();
    },

    testApplyObjectId_whenActivated : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();

      rwt.widgets.base.Widget._renderHtmlIds = true;
      button.applyObjectId( "w23" );
      rwt.widgets.base.Widget._renderHtmlIds = false;

      assertEquals( "w23", button.getHtmlAttribute( "id" ) );
      button.destroy();
    },

    testFiresChangeEnabledEvent : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();
      var log = 0;
      button.addEventListener( "changeEnabled", function() {
        log++;
      } );

      button.setEnabled( false );

      assertTrue( log > 0 );
      button.destroy();
    },

    testFiresChangeContextMenuEvent : function() {
      var button = new rwt.widgets.Button( "push" );
      var menu = this._createMenuWithItems( 3 );
      button.addToDocument();
      TestUtil.flush();
      var log = 0;
      button.addEventListener( "changeContextMenu", function() {
        log++;
      } );

      button.setContextMenu( menu );

      assertTrue( log > 0 );
      button.destroy();
      menu.destroy();
    },


    testDisposeChildrenWithParent : function() {
      var widget = new rwt.widgets.base.Terminator();
      var composite = new rwt.widgets.Composite();
      composite.addToDocument();
      widget.setParent( composite );
      TestUtil.flush();
      var node = widget.getElement();
      var parentNode = widget.getElement().parentNode;

      composite.destroy();
      TestUtil.flush();

      assertTrue( composite.isDisposed() );
      assertTrue( widget.isDisposed() );
      assertTrue( node.parentNode !== parentNode );
    },

    testShowToolTipOnHover : function() {
      if(! rwt.client.Client.supportsTouch() ) { // Test in MobileWebkitSupport.js
        var widget = this._createWidget();
        widget.setUserData( "toolTipText", "gogo" );
        var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
        widget.setToolTip( toolTip );
        TestUtil.flush();

        TestUtil.hoverFromTo( document.body, widget.getElement() );
        TestUtil.forceInterval( toolTip._showTimer );
        TestUtil.flush();

        assertTrue( toolTip.isSeeable() );
        assertEquals( "gogo", toolTip.getAtom().getLabel() );
        toolTip.hide();
        widget.destroy();
      }
    },

    testDontShowToolTipOnTab : function() {
      var widget = this._createWidget();
      widget.setUserData( "toolTipText", "gogo" );
      var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
      widget.setToolTip( toolTip );
      TestUtil.flush();
      widget.focus();
      try {
        TestUtil.forceInterval( toolTip._showTimer );
      } catch( ex ) {
        // expected
      }
      TestUtil.flush();

      assertFalse( toolTip.isSeeable() );
      toolTip.hide();
      widget.destroy();
    },


    /////////
    // Helper

    _createWidget : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      widget.setWidth( 100 );
      widget.setHeight( 100 );
      TestUtil.flush();
      return widget;
    },

    _createMenuWithItems : function( itemCount ) {
      var menu = new rwt.widgets.Menu();
      for( var i = 0; i < itemCount; i++ ) {
        var menuItem = new rwt.widgets.MenuItem( "push" );
        menu.addMenuItemAt( menuItem, i );
      }
      var menuItem = new rwt.widgets.MenuItem( "push" );
      menu.addMenuItemAt( menuItem, 0 );
      menu.show();
      TestUtil.flush();
      return menu;
    },

    _getComplexBorder : function() {
      return new rwt.html.Border( 2, "complex", "green", "red" );
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

}());