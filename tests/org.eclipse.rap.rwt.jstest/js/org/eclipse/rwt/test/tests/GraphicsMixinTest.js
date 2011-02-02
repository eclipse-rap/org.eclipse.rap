/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GraphicsMixinTest", {
  extend : qx.core.Object,

	construct : function(){
	  this.gfxBorder = new org.eclipse.rwt.RoundedBorder( 1, "black", 0 );
	  this.cssBorder = new qx.ui.core.Border( 1, "solid", "black" );
	  this.gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
	},
	
  members : {
    
    testSetGradient : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      assertTrue( shell.isSeeable() );      
      assertFalse( this.usesGfxBackground( shell ) );
      shell.setBackgroundGradient( this.gradient );
      var shape = shell._gfxData.currentShape;
      assertTrue( this.usesGfxBackground( shell ) );
      assertTrue( gfxUtil.getFillType( shape ) == "gradient" );
      shell.setBackgroundGradient( null );
      testUtil.flush();
      assertFalse( this.usesGfxBackground( shell ) );
      shell.destroy();
      testUtil.flush();
    },
    
    testSetGradientWhileNotInDOM : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setParent( null );
      testUtil.flush();
      shell.setBackgroundGradient( this.gradient );
      shell.addToDocument();
      testUtil.flush();
      var shape = shell._gfxData.currentShape;
      assertTrue( this.usesGfxBackground( shell ) );
      assertTrue( gfxUtil.getFillType( shape ) == "gradient" );
      shell.setParent( null );
      testUtil.flush();
      shell.setBackgroundGradient( null );
      shell.addToDocument();
      testUtil.flush();
      assertFalse( this.usesGfxBackground( shell ) );
      shell.destroy();
      testUtil.flush();
    },

    testSetSolidFillAfterBorder : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.currentShape;
      assertNull( gfxUtil.getFillType( shape ) );
      shell.setBackgroundColor( "green" );
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      shell.destroy();
      testUtil.flush();
    },

    testSetSolidFillBeforeBorder : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.currentShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      shell.destroy();
      testUtil.flush();
    },
    
    testRestoreBackgroundColor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );          
      assertEquals( "green", testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( this.gradient );
      assertNull( testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( null );
      assertEquals( "green", testUtil.getCssBackgroundColor( shell ) );
      shell.destroy();
      testUtil.flush();
    },
    
    testCssBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      var node = shell.getElement();
      shell.setBorder( this.cssBorder );
      testUtil.flush();
      assertTrue( testUtil.hasCssBorder( node ) );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( testUtil.hasCssBorder( node ) );      
      shell.setBorder( this.cssBorder );
      testUtil.flush();
      assertTrue( testUtil.hasCssBorder( node ) );
      shell.setBorder( null );
      testUtil.flush();
      assertFalse( testUtil.hasCssBorder( node ) );
      shell.destroy();
      testUtil.flush();
    },

    testPadding : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      this.gfxBorder.setWidth( 1 );
      widget.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( widget.getElement() !== widget._getTargetNode() );
      assertEquals( [ 1, 1, 1, 1 ], this.getFakePadding( widget ) );
      assertEquals( [ 1, 1, 1, 1 ], this.getBorderCache( widget ) );
      this.gfxBorder.setWidth( 0 );
      testUtil.flush();      
      assertEquals ( [ 0, 0, 0, 0 ], this.getFakePadding( widget ) );
      assertEquals ( [ 0, 0, 0, 0 ], this.getBorderCache( widget ) );
      this.gfxBorder.setWidth( 4, 0, 3, 0 );
      testUtil.flush();      
      assertEquals ( [ 4, 0, 3, 0 ], this.getFakePadding( widget ) );      
      assertEquals ( [ 4, 0, 3, 0 ], this.getBorderCache( widget ) );      
      widget.setBackgroundGradient( this.gradient );
      widget.setBorder( null );
      testUtil.flush();
      assertEquals ( [ 0, 0, 0, 0 ], this.getBorderCache( widget ) );      
      assertEquals ( [ 0, 0, 0, 0 ], this.getFakePadding( widget ) );
      widget.destroy();
      testUtil.flush();
    },

    testRoundedBorderWidth : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      this.gfxBorder.setRadius( 3 );      
      this.gfxBorder.setWidth( 0 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var shape = widget._gfxData.currentShape;
      assertEquals( 0, gfxUtil.getStrokeWidth( shape ) );
      this.gfxBorder.setWidth( 2 );
      testUtil.flush();
      assertEquals( 2, gfxUtil.getStrokeWidth( shape ) );
      this.gfxBorder.setWidth( 1, 2, 3, 4 );
      testUtil.flush();
      assertEquals( 4, gfxUtil.getStrokeWidth( shape ) );
      widget.destroy();
      testUtil.flush();
    },
        
    testOutline : function() {
      // in Safari the outline must always be set because the default 
      // outline is visually incompatible with SVG (glitch)
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      this.gfxBorder.setRadius( 4 );
      shell.setBorder( this.gfxBorder );
      shell.setHideFocus( false );
      testUtil.flush();
      assertTrue( this.widgetContainsCanvas( shell ) );
      shell.blur();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "" );
      shell.focus();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "");
      shell.setHideFocus( true );
      shell.blur();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "" );
      shell.focus();
      testUtil.flush();     
      assertTrue( shell.getElement().style.outline != "" );
      shell.destroy();
      testUtil.flush();     
    },
    
    testGfxRadiusInvisibleEdges : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidthTop( 1 );
      this.gfxBorder.setWidthRight( 0 );
      this.gfxBorder.setWidthBottom( 0 );
      this.gfxBorder.setWidthLeft( 1 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var radii = widget.getGfxProperty( "borderRadii", radii );
      assertEquals( [ 5, 0, 0, 0 ], radii );
      widget.destroy();
      testUtil.flush();      
    },
    
    testGfxRadiusInvisibleBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var radii = widget.getGfxProperty( "borderRadii", radii );
      assertEquals( [ 5, 5, 5, 5 ], radii );
      widget.destroy();
      testUtil.flush();      
    },

    testGfxBackgroundImage : function() {
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      widget.setBackgroundImage( "bla.jpg" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( this.usesGfxBackground( widget ) );
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shape = widget._gfxData.currentShape;
      assertTrue( gfxUtil.getFillType( shape ) == "pattern" );
      widget.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testGfxBackgroundColorToImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.currentShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      shell.setBackgroundImage( "bla.jpg" );
      testUtil.flush();
      assertTrue( gfxUtil.getFillType( shape ) == "pattern" );
      shell.destroy();
      testUtil.flush();
    },

    testOnCanvasAppearOnWidgetInsert : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.addToDocument();
      widget2.addToDocument();
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      widget1.destroy();
      widget2.destroy();
    },

    testOnCanvasAppearOnSetDisplay : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.addToDocument();
      widget2.addToDocument();
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      widget1.setDisplay( false );
      widget2.setDisplay( false );
      testUtil.flush();
      widget1.setDisplay( true );
      widget2.setDisplay( true );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2", "widget1", "widget2" ], log );
      testUtil.flush();
    },

    testOnCanvasAppearOnEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.setParent( shell );
      widget2.setParent( shell );
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2", "widget1", "widget2" ], log );
      shell.destroy();
      testUtil.flush();
    },

    testOpacityWidthEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      assertTrue( widget.isSeeable() );      
      assertTrue( widget.getElement() === widget._getTargetNode() );
      widget.setOpacity( 0.5 );
      assertTrue( testUtil.hasElementOpacity( widget.getElement() ) );
      widget.setBackgroundGradient( this.gradient );
      assertTrue( widget.getElement() !== widget._getTargetNode() );
      assertTrue( testUtil.hasElementOpacity( widget.getElement() ) );
      assertFalse( testUtil.hasElementOpacity( widget._getTargetNode() ) );
      widget.setOpacity( 1 );
      assertFalse( testUtil.hasElementOpacity( widget.getElement() ) );
      assertFalse( testUtil.hasElementOpacity( widget._getTargetNode() ) );
      widget.destroy();
    },
    
    testAntialiasingBugIE : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        assertTrue( widget.isSeeable() );      
        assertTrue( widget.getElement() === widget._getTargetNode() );      
        widget.setBackgroundGradient( this.gradient );
        assertTrue( this.usesGfxBackground( widget ) );
        var innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        var outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) == -1 );      
        widget.destroy();
      },
      "default" : function(){}
    } ),
    
    
    testAntialiasingBugIEWithOpacitySet : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setOpacity( 0.5 );
        assertTrue( widget.isSeeable() );      
        assertTrue( widget.getElement() === widget._getTargetNode() );      
        widget.setBackgroundGradient( this.gradient );
        assertTrue( this.usesGfxBackground( widget ) );
        var innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        var outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) != -1 );
        widget.setOpacity( 1 );      
        innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) == -1 );
        widget.destroy();
      },
      "default" : function(){}
    } ),

    
    testSetBackgroundImageOverwritesGradient : function() {
      // Note: a gradient and background-image can note be set at the same
      // time via CSS, but when using CSS and Java-API.  
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      this.gfxBorder.setRadius( 5 );
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      widget.setBorder( this.gfxBorder ); 
      widget.setBackgroundImage( "bla.jpg" );
      widget.setBackgroundGradient( this.gradient );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( widget ) );
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shape = widget._gfxData.currentShape;
      assertEquals( "pattern", gfxUtil.getFillType( shape )  );
      widget.destroy();
    },
    
    testChangeBackgroundImageWhileBackgoundGradientSet : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      widget.setBackgroundGradient( this.gradient );
      testUtil.flush();
      assertTrue( "initally uses gfx", this.usesGfxBackground( widget ) );      
      widget.setBackgroundImage( "bla.jpg" );
      testUtil.flush();
      assertFalse( "no more gfx", this.usesGfxBackground( widget ) );
      var src = widget._getTargetNode().style.backgroundImage;
      assertTrue( "normal background", src.indexOf( "bla.jpg" ) != -1 );
      widget.setBackgroundImage( null );
      src = widget._getTargetNode().style.backgroundImage;
      assertTrue( "no more normal background", src.indexOf( "bla.jpg" ) == -1 );
      assertTrue( "uses gfx again", this.usesGfxBackground( widget ) );
      widget.destroy();
    },
    
    /////////
    // Helper

    _createShell : function() {
      var result = new org.eclipse.swt.widgets.Shell();
      result.addToDocument();
      result.setBackgroundColor( null );
      result.open();
      qx.ui.core.Widget.flushGlobalQueues();
      return result;      
    },
    
    _createWidget : function() {
      var result = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      return result;
    },

    usesGfxBorder : function( widget ) {      
      return    widget._gfxBorderEnabled 
             && this.widgetContainsCanvas( widget )
             && widget._gfxData.currentShape == widget._gfxData.pathElement; 
    },

    usesGfxBackground : function( widget ) {
      var result =    widget._gfxBackgroundEnabled 
                   && this.widgetContainsCanvas( widget );
      return result;
    },

   widgetContainsCanvas : function( widget ) {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var result = false;
      if( widget._gfxCanvas != null && widget.getElement() != null ) {
        var widgetNode = widget.getElement();
        var canvasNode = gfxUtil.getCanvasNode( widget._gfxCanvas );
        for( var i = 0; i < widgetNode.childNodes.length; i++ ) {
          if( widgetNode.childNodes[ i ] == canvasNode ) {
            result = true;
          }
        }
      }
      return result;
    },

    getFakePadding : function( widget ) {
      var result = null;
      var inner = widget._getTargetNode().style;
      var outer = widget._style;
      var isPx = true;
      isPx = ( inner.width.search( "%" ) == -1 ) ? isPx : false;
      isPx = ( inner.height.search( "%" ) == -1 ) ? isPx : false;
      if( inner == outer || isPx == false ) { 
        result = [ 0, 0, 0, 0 ];
      } else {
        var top = inner.top ? parseFloat( inner.top ) : 0;
        var left = inner.left ? parseFloat( inner.left ) : 0;
        var width = inner.width ? parseFloat( inner.width ) : 0; 
        var height = inner.height ? parseFloat( inner.height ) : 0;         
        var right = parseFloat( outer.width ) - left - width; 
        var bottom = parseFloat( outer.height ) - top - height;         
        result = [ top, right, bottom, left ];
      }
      return result;
    },
    
    getBorderCache : function( widget ) {
      return [ widget._cachedBorderTop,
               widget._cachedBorderRight,
               widget._cachedBorderBottom,
               widget._cachedBorderLeft ];
    }

  }

} );