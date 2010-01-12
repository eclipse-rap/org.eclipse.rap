/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
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
	  this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;
	  this.gfxBorder = new org.eclipse.rwt.RoundedBorder( 1, "black", 0 );
	  this.cssBorder = new qx.ui.core.Border( 1, "solid", "black" );
	  this.gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
	},
	
  members : {
    
    testGfxBackground : function() {
      this.shell1 = new org.eclipse.swt.widgets.Shell();
      var shell = this.shell1;
      shell.addToDocument();
      shell.setBackgroundColor( null );
      shell.open();      
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "Shell is seeable!", shell.isSeeable() );      
      assertTrue( "Element created!", shell._getTargetNode() != null );
      assertFalse( "No gfx-node in DOM", this.usesGfxBackground( shell ) );
      shell.setBackgroundGradient( this.gradient );
      assertTrue( "uses gfx gradient", this.usesGfxBackground( shell ) );
      shell.setBackgroundGradient( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "no more gradient", this.usesGfxBackground( shell ) );
      shell.getParent().remove( shell );
      shell.setBackgroundGradient( this.gradient );
      shell.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "gradient was set while parentless", 
                  this.usesGfxBackground( shell ) );
      shell.getParent().remove( shell );
      shell.setBackgroundGradient( null );
      shell.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "gradient was removed while parentless", 
                   this.usesGfxBackground( shell ) );
      shell.getParent().remove( shell );
      qx.ui.core.Widget.flushGlobalQueues();
      shell.setBackgroundGradient( this.gradient );
      shell.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "gradient was set while not in dom", 
                  this.usesGfxBackground( shell ) );
      shell.getParent().remove( shell );
      qx.ui.core.Widget.flushGlobalQueues();
      shell.setBackgroundGradient( null );
      shell.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "gradient was removed while not in dom",
                   this.usesGfxBackground( shell ) );
      shell.setBorder( this.gfxBorder );
      shell.setBackgroundColor( "green" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "background used due to gfxborder",
                  this.usesGfxBackground( shell ) );
      shell.setBackgroundColor( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "background used due to gfxborder w/o background color",
                  this.usesGfxBackground( shell ) );      
    },
    
    testBackgroundRestore : function() {
      this.shell2 = new org.eclipse.swt.widgets.Shell();
      var shell = this.shell2;
      shell.addToDocument();
      shell.open();      
      shell.setBackgroundColor( "green" );
      qx.ui.core.Widget.flushGlobalQueues();      
      assertEquals( "css-background is green", 
                    "green", 
                    this.testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( this.gradient );
      assertEquals( "css-background is not set",
                    null,
                    this.testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( null );
      assertEquals( "css-background is green again",
                    "green",
                    this.testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundColor( "blue" );
      shell.setBackgroundGradient( this.gradient );
      shell.setBackgroundColor( "red" );
      assertEquals( "css-background is not set yet",
                    null,
                    this.testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( null );
      assertEquals( "css-background is now red",
                    "red",
                    this.testUtil.getCssBackgroundColor( shell ) );
    },
    
    testCssBorder : function() {
      this.shell4 = new org.eclipse.swt.widgets.Shell();
      var shell = this.shell4;
      shell.addToDocument();
      shell.setBorder( null );
      shell.open();      
      qx.ui.core.Widget.flushGlobalQueues();      
      assertFalse( "no css border is set", this.hasCssBorder( shell ) );
      shell.setBorder( this.cssBorder );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "a css border is set", this.hasCssBorder( shell ) );
      this.info( "apply gfxBorder " + this.gfxBorder);
      shell.setBorder( this.gfxBorder );
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "the css border is no longer present",
                   this.hasCssBorder( shell ) );      
      shell.setBorder( this.cssBorder );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "the css border is back", this.hasCssBorder( shell ) );
      shell.setBorder( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "css border removed", this.hasCssBorder( shell ) );
    },

    testPadding : function() {
      this.widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget = this.widget1;
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      this.gfxBorder.setWidth( 1 );
      widget.setBorder( this.gfxBorder );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "the enhanced border has been enabled",
                  widget.getElement() !== widget._getTargetNode() );
      assertFalse( "no css padding", this.hasCssPadding( widget ) );
      assertEquals ( "1px padding",
                   [ 1, 1, 1, 1 ], 
                   this.getFakePadding( widget ) );
      assertEquals ( "1px border layout",
                   [ 1, 1, 1, 1 ], 
                   this.getBorderCache( widget ) );      
      this.gfxBorder.setWidth( 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals ( "0px padding",
                  [ 0, 0, 0, 0 ], 
                  this.getFakePadding( widget ) );
      assertEquals ( "0px padding",
                   [ 0, 0, 0, 0 ], 
                   this.getBorderCache( widget ) );
      this.gfxBorder.setWidth( 7 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals ( "7px padding",
                   [ 7, 7, 7, 7 ], 
                   this.getFakePadding( widget ) );
      assertEquals ( "7px padding",
                   [ 7, 7, 7, 7 ], 
                   this.getBorderCache( widget ) );
      this.gfxBorder.setWidthTop( 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals ( "7px padding except top",
                   [ 0, 7, 7, 7 ],
                   this.getFakePadding( widget ) );      
      assertEquals ( "7px padding except top",
                   [ 0, 7, 7, 7 ], 
                   this.getBorderCache( widget ) );      
      this.gfxBorder.setWidth( 4, 0, 3, 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals ( [ 4, 0, 3, 0 ], this.getFakePadding( widget ) );      
      assertEquals ( [ 4, 0, 3, 0 ], this.getBorderCache( widget ) );      
      widget.setBackgroundGradient( this.gradient );
      widget.setBorder( null );
      assertEquals ( "no padding", 
                   [ 0, 0, 0, 0 ], 
                   this.getBorderCache( widget ) );
      qx.ui.core.Widget.flushGlobalQueues();
      if( qx.core.Client.getEngine() == "mshtml" ) {
        // IE handles targetNode differently
        assertEquals ( "no padding",
                     [ 0, 2, 2, 0 ], 
                     this.getFakePadding( widget ) );
      } else {
        assertEquals ( "no padding", 
                     [ 0, 0, 0, 0 ], 
                     this.getFakePadding( widget ) );
      }
    },

    testGfxBorder : function() {
      this.widget3 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget = this.widget3; 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 0 );
      widget.setBorder( this.gfxBorder ); 
      this.gfxBorder.setWidth( 1 );      
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "no radius, uses gfxborder", 
                  this.usesGfxBorder( widget ) );
      assertEquals( "1px border", 1, this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 2 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "2px border", 2, this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 3, 0, 0, 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals("3px border", 3, this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 1, 2, 3, 4 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "4px border", 4, this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 10 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "10px border", 10, this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 1 ); 
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "no px, uses gfxborder", this.usesGfxBorder( widget ) );
      widget.getParent().remove( widget );
      this.gfxBorder.setRadius( 1 );
      widget.addToDocument()
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "border-radius set while parentless", 
                  this.usesGfxBorder( widget ) );      
      widget.getParent().remove( widget );
      this.gfxBorder.setRadius( 0 ); 
      widget.addToDocument()
      widget.getParent().remove( widget );
      this.gfxBorder.setRadius( 1 );
      qx.ui.core.Widget.flushGlobalQueues();
      widget.addToDocument()
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "border-radius set while not in DOM", 
                  this.usesGfxBorder( widget ) );      
      widget.getParent().remove( widget );
      this.gfxBorder.setRadius( 0 ); 
      widget.addToDocument()
      qx.ui.core.Widget.flushGlobalQueues();
      widget.getParent().remove( widget );
      this.gfxBorder.setWidth( 1 );      
      widget.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "border width set while parentless",
                    1, 
                    this.getGfxBorderWidth( widget ) );
      widget.getParent().remove( widget );
      qx.ui.core.Widget.flushGlobalQueues();
      this.gfxBorder.setWidth( 2 );      
      widget.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "border removed not in DOM", 
                    2, 
                    this.getGfxBorderWidth( widget ) );
      this.gfxBorder.setWidth( 1 );
      this.gfxBorder.setRadius( 0 );
    },

    testGfxShape : function() {
      this.widget4 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget = this.widget4; 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );            
      widget.setBackgroundColor( "green" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( this.widgetContainsCanvas( widget ) );
      widget.setBorder( this.gfxBorder ); 
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "Uses path shape", this.usesPathShape( widget ) );
      assertTrue( "path layouting enabled", widget._gfxLayoutEnabled );
      assertTrue( "gfx background", this.usesGfxBackground( widget) );
      widget.setBackgroundColor( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "gfx background", this.usesGfxBackground( widget) );
      widget.setBackgroundGradient( this.gradient );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "gfx background", this.usesGfxBackground( widget) );
      widget.setBorder( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( "gfx background", this.usesGfxBackground( widget) );      
      assertTrue( "Uses rect shape", this.usesRectShape( widget ) );
      assertFalse( "path layouting disabled", widget._gfxLayoutEnabled );
      widget.setBackgroundGradient( null );
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( "no path shape", this.usesPathShape( widget ) );
      assertFalse( "no rect shape", this.usesRectShape( widget ) );
   },
        
    testSafariFocusOutline : qx.core.Variant.select("qx.client", {
      "default" : function() {
      },
      "webkit" : function() {
        // in Safari the outline must always be set because the default 
        // outline is visually incompatible with SVG (glitch)
        this.shell3 = new org.eclipse.swt.widgets.Shell(); 
        var shell = this.shell3;         
        shell.setBackgroundGradient( [ "red", "yellow" ] );
        shell.addToDocument();
        shell.setHideFocus( true );
        shell.open();      
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( shell.isSeeable() );
        assertTrue( this.widgetContainsCanvas( shell ) );
        shell.blur();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 1", this._isOutlineDefined( shell ) );
        shell.focus();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 2", this._isOutlineDefined( shell ) );
        shell.setHideFocus( false );
        shell.blur();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 3", this._isOutlineDefined( shell ) );
        shell.focus();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 4", this._isOutlineDefined( shell ) );                    
        shell.prepareEnhancedBorder();
        shell.setHideFocus( true );
        qx.ui.core.Widget.flushGlobalQueues();
        shell.blur();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 5", this._isOutlineDefined( shell ) );
        shell.focus();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 6", this._isOutlineDefined( shell ) );
        shell.setHideFocus( false );
        shell.blur();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 7", this._isOutlineDefined( shell ) );
        shell.focus();
        qx.ui.core.Widget.flushGlobalQueues();
        assertTrue( "Outline defined 8", this._isOutlineDefined( shell ) );       
      }      
    } ),
    
    testGfxInvisibleCorners : function() {
      this.widget5 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget = this.widget5; 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidthTop( 1 );
      this.gfxBorder.setWidthRight( 0 );
      this.gfxBorder.setWidthBottom( 0 );
      this.gfxBorder.setWidthLeft( 1 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( this.usesGfxBorder( widget ) );
      var radii = widget.getGfxProperty( "borderRadii", radii );
      assertEquals( [ 5, 0, 0, 0 ], radii );
    },    
    
    /////////
    // Helper
    
    _isOutlineDefined : function( widget ) {      
      return ( widget.getElement().style.outline != "" );
    },
    
    widgetContainsCanvas : function( widget ) {
      var node = widget.getElement();
      var ret = false;
      for( var i = 0; i < node.childNodes.length; i++ ) {
        if( this.nodeIsCanvas( node.childNodes[ i ] ) ) {
          ret = true;
        }
      }
      return ret;
    },
    
    nodeIsCanvas : function( node ) {
      var ret = false;
      if( qx.core.Client.getEngine() == "mshtml" ) {
        ret = ( node.tagName == "group" );
      } else {
        ret = ( node.toString() == "[object SVGSVGElement]");
      }
      return ret;
    },
    
    // this won't work in every case  
    // but for this tests pruposes it will suffice
    hasNodeCssBorder : function( node ) {      
      var hasPx = node.style.borderWidth.search( "px" );
      var hasZeroPx = node.style.borderWidth.search( "0px" );
      var isPx = ( ( hasPx != -1 ) && ( hasZeroPx == -1 ) );
      var hasPt = node.style.borderWidth.search( "pt" );
      var hasZeroPt = node.style.borderWidth.search( "0pt" );
      var isPt = ( ( hasPt != -1 ) && ( hasZeroPt == -1 ) );
      var isThin = ( node.style.borderWidth.search( "thin" ) != -1 );
      var isMedium = ( node.style.borderWidth.search( "medium" ) != -1 );
      var isThick = ( node.style.borderWidth.search( "thick" ) != -1 );
      return ( isPx || isPt || isThin || isMedium || isThick );
    },
    
    hasCssBorder : function( widget ) {      
      var inner = this.hasNodeCssBorder( widget.getElement() );
      var outer = this.hasNodeCssBorder( widget._getTargetNode() );
      return ( inner || outer );
    },

    hasCssPadding : function( widget ) {      
      var hasPx = widget._getTargetNode().style.padding.search( "px" );
      var hasZeroPx = widget._getTargetNode().style.padding.search( "0px" );
      return ( ( hasPx != -1 ) && ( hasZeroPx == -1 ) ); 
    },
    
    getFakePadding : function( widget ) {
      var ret = null;
      var inner = widget._getTargetNode().style;
      var outer = widget._style;
      this.info( "fakePadding: " + inner.width + " " + inner.height );
      var isPx = true;
      isPx = ( inner.width.search( "%" ) == -1 ) ? isPx : false;
      isPx = ( inner.height.search( "%" ) == -1 ) ? isPx : false;
      if( inner == outer || isPx == false ) { 
        ret = [ 0, 0, 0, 0 ];
      } else {
        var top = inner.top ? parseFloat( inner.top ) : 0;
        var left = inner.left ? parseFloat( inner.left ) : 0;
        var width = inner.width ? parseFloat( inner.width ) : 0; 
        var height = inner.height ? parseFloat( inner.height ) : 0;         
        var right = parseFloat( outer.width ) - left - width; 
        var bottom = parseFloat( outer.width ) - top - height;         
        ret = [ top, right, bottom, left ];
      }
      return ret;
    },
    
    getBorderCache : function( widget ) {
      return [ widget._cachedBorderTop,
               widget._cachedBorderRight,
               widget._cachedBorderBottom,
               widget._cachedBorderLeft ];
    },
        
    getGfxBorderWidth : function( widget ) {
     var rectNode = widget._gfxData.pathElement.node;
      var width = null;
      if( qx.core.Client.getEngine() == "mshtml" ) {
        //IE returns strokeweight either as number (then its pt)
        //or as string with a "px" or "pt" postfix
        width =  rectNode.strokeweight; 
        if( typeof width == "number" || width.search( "pt" ) != -1 ) {
          width = parseFloat( width );                     
          width = width / 0.75; 
        } else { // assume its px
          width = parseFloat( width );  
        }        
      } else {
        width = parseInt( rectNode.getAttribute( "stroke-width" ) );
      }
      return width;
    },
    
    usesGfxBorder : function( widget ) {      
      return widget._gfxBorderEnabled && this.widgetContainsCanvas( widget );           
    },
    
    usesGfxBackground : function( widget ) {
      return ( 
           widget._gfxBackgroundEnabled 
        && this.widgetContainsCanvas( widget ) 
        && !this.testUtil.getCssBackgroundColor( widget )
     );      
    },
    
    usesRectShape : function( widget ) {
      var ret = false;
      if( this.widgetContainsCanvas( widget ) ) {
        ret = widget._gfxData.currentShape == widget._gfxData.rect;
      }
      return ret;
    },
    
    usesPathShape : function( widget ) {
      var ret = false;
      if( this.widgetContainsCanvas( widget ) ) {
        ret = widget._gfxData.currentShape == widget._gfxData.pathElement;
      }
      return ret;    
    }
  },
  
  destruct : function(){
  	this._disposeObjects( this.shell1, 
  	                      this.shell2, 
  	                      this.shell3, 
  	                      this.shell4,
  	                      this.widget1,
  	                      this.widget2,
  	                      this.widget3,
  	                      this.widget5  );
  }
  
} );