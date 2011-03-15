/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.MultiCellWidgetTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;
  },
  
  members : {
        
    testCreateWidget : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );      
      assertTrue( widget.isCreated() );
      assertTrue( widget.isSeeable() );
      this.disposeWidget( widget );
    },

    testContentCreated : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      assertEquals( 2, widget._getTargetNode().childNodes.length );
      this.disposeWidget( widget );
    },

    testContentParent : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      var parentNode = widget._getTargetNode();
      assertIdentical( parentNode, widget.getCellNode( 0 ).parentNode );
      assertIdentical( parentNode, widget.getCellNode( 1 ).parentNode );
      this.disposeWidget( widget );
    },

    testContentNotCreated : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellContent( 0, null );
      widget.setCellContent( 1, null );
      this.flush();
      assertEquals( 0, widget._getTargetNode().childNodes.length );
      this.disposeWidget( widget );
    },
    
    testContent : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      assertTrue( this.testUtil.getCssBackgroundImage( 
        widget._getTargetNode().firstChild).search( "test.jpg" ) != -1 );
      assertEquals( 
        "test text", 
        widget._getTargetNode().lastChild.innerHTML );
      this.disposeWidget( widget );
    },
    
    testSpacing : function() {
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget(
        [ "image", "label", "image", "label", "image" ] );
      this._currentWidget = widget;
      widget.setSpacing( 10 );
      assertEquals( 0 , widget.getTotalSpacing() );
      widget.setCellContent( 0, "bla" );
      widget.setCellContent( 1, "bla" );
      widget.setCellContent( 3, "bla" );
      widget.setCellContent( 4, "bla" );
      // only labels ( 1 + 3 )are visible since their size is computed:
      assertEquals( 10 , widget.getTotalSpacing() );
      widget.setCellWidth( 0, 10 ); // visible
      widget.setCellWidth( 1, 0 ); // not visible (but created)
      widget.setCellWidth( 2, 10 ); // visible
      widget.setCellContent( 3, "" ); //not visible (but created)
      widget.setCellWidth( 4, 10 ); // visible
      assertEquals( 20 , widget.getTotalSpacing() );
      
      this.disposeWidget( widget );      
    },

    testPadding : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );        
      widget.set( { 
        paddingLeft : 23,
        horizontalChildrenAlign : "left",
        width : 100,
        height : 100
      } );
      this.initWidget( widget );
      var lastChild = widget._getTargetNode().lastChild; 
      var firstChild = widget._getTargetNode().firstChild; 
      assertEquals( 
        "padding left is 23",
        23,
        this.testUtil.getElementBounds( firstChild ).left );      
      widget.set( {
        paddingRight : 11,
        horizontalChildrenAlign : "right"
      } );
      this.initWidget( widget, true );
      assertEquals( 
        "padding right is 11", 
        11,
        this.testUtil.getElementBounds( lastChild ).right );      
      widget.set( { 
        paddingTop : 7,
        verticalChildrenAlign : "top"
      } );
      this.initWidget( widget, true );
      assertEquals( 
        "padding top is 7", 
        7,
        this.testUtil.getElementBounds( firstChild ).top );
      assertEquals(  
        "padding top is 7", 
        7,
        this.testUtil.getElementBounds( lastChild ).top );      
      widget.set( { 
        paddingBottom : 19,
        verticalChildrenAlign : "bottom"
      } );
      this.initWidget( widget, true );
      assertEquals( 
        "padding bottom is 10", 
        19,
        this.testUtil.getElementBounds( firstChild ).bottom );
      assertEquals( 
        "padding bottom is 19", 
        19,
        this.testUtil.getElementBounds( lastChild ).bottom );
      this.disposeWidget( widget );
    },

    testCenter : function() {  
      var widget = this.createDefaultWidget();
      widget.setDimension( 100, 100 )
      this.initWidget( widget, true );        
      widget.set( { 
        padding : 0,
        horizontalChildrenAlign : "center",
        verticalChildrenAlign : "middle",
        spacing : 5
      } );
      
      this.initWidget( widget, true );
      var cell0 = this.testUtil.getElementBounds( 
        widget._getTargetNode().firstChild );
      var cell1 = this.testUtil.getElementBounds( 
        widget._getTargetNode().lastChild );
      assertTrue(
        "1 - horizontal align", 
        this.almostEqual( cell0.left, cell1.right ) );
      assertTrue(
        "2 - vertical align cell0", 
        this.almostEqual( cell0.top, cell0.bottom ) );
      assertTrue(
        "3 - vertical align cell1", 
        this.almostEqual( cell1.top, cell1.bottom ) );
      this.disposeWidget( widget );      
    },
    
    testPreferredDimension : function() {
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget(
        [ "image", "label", "image", "label", "image" ] );
      widget.setCellContent( 0, "bla" );
      widget.setCellContent( 1, "bla" );
      widget.setCellContent( 3, "bla" );
      widget.setCellContent( 4, "bla" );
      widget.setCellDimension( 0, 10, 10 );
      widget.setCellDimension( 1, 10, 11 );
      widget.setCellDimension( 3, 10, 10 );
      widget.setCellDimension( 4, 10, 10 );      
      widget.setSpacing( 10 );
      assertEquals( 70, widget.getPreferredInnerWidth() );
      assertEquals( 11, widget.getPreferredInnerHeight() );
      this.disposeWidget( widget );      
    },
    
    testCellDimension : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellDimension( 0, 11, 12 );
      widget.setCellDimension( 1, 13, 14 );
      this.flush();
      var cell0 = this.testUtil.getElementBounds( 
        widget._getTargetNode().firstChild );
      var cell1 = this.testUtil.getElementBounds( 
        widget._getTargetNode().lastChild );
      assertEquals( 11, cell0.width ); 
      assertEquals( 12, cell0.height ); 
      assertEquals( 13, cell1.width ); 
      assertEquals( 14, cell1.height ); 
      this.disposeWidget( widget );
    },    
    
    
    testFlexibleCellLimit : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 100 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellDimension( 1, 30, 30 );
      this.flush();
      // Flexible cell has maximal value
      assertEquals( 52, widget.getPreferredInnerWidth() );
      assertEquals( 30, widget.getPreferredInnerHeight() );
      assertEquals( [ 16, 16 ], widget.getCellDimension( 0 ) );
      assertEquals( [ 30, 30 ], widget.getCellDimension( 1 ) );
      // Flexible cell is reduced
      widget.setCellDimension( 0, 80, 80 );
      assertEquals( 116, widget.getPreferredInnerWidth() );
      assertEquals( 80, widget.getPreferredInnerHeight() );
      assertEquals( [ 80, 80 ], widget.getCellDimension( 0 ) );
      assertEquals( [ 4, 30 ], widget.getCellDimension( 1 ) );
      // Flexible cell is zero
      widget.setCellDimension( 0, 110, 110 );
      assertEquals( 146, widget.getPreferredInnerWidth() ); 
      assertEquals( 110, widget.getPreferredInnerHeight() );
      assertEquals( [ 110, 110 ], widget.getCellDimension( 0 ) );
      assertEquals( [ 0, 30 ], widget.getCellDimension( 1 ) );
      this.disposeWidget( widget );
    },    
    
    testFlexibleCellWrap : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 400 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellContent( 1, "some longer text that wraps" );
      this.flush();
      var originalTextDimension = widget.getCellDimension( 1 );
      widget.setWidth( 80 );
      this.flush();
      var newTextDimension = widget.getCellDimension( 1 );
      console.log( originalTextDimension );
      console.log( newTextDimension );
      assertTrue( originalTextDimension[ 0 ] > newTextDimension[ 0 ] );
      assertTrue( originalTextDimension[ 1 ] < newTextDimension[ 1 ] );
      widget.setWidth( 400 );
      this.flush();
      var newTextDimension = widget.getCellDimension( 1 );
      console.log( newTextDimension );
      assertEquals( originalTextDimension, newTextDimension );
      this.disposeWidget( widget );
    },    
    
    testInvalidateSpacing : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellWidth( 0, 10 );
      widget.setCellWidth( 0, 10 );
      widget.setSpacing( 0 );
      assertEquals( 0, widget.getTotalSpacing() );
      widget.setSpacing( 12 );
      assertEquals( 12 , widget.getTotalSpacing() );            
      widget.setCellWidth( 0, null ); // => self-compute results in width 0
      widget.setCellWidth( 0, null );
      assertEquals( 0 , widget.getTotalSpacing() );
      this.disposeWidget( widget );      
    },
    
    testInvalidateFrameDimension : function() {
      var widget = this.createDefaultWidget();
      widget.setPadding( 0, 0, 0, 0 );
      widget.setBorder( null );
      assertEquals( 0 , widget.getFrameHeight() );      
      widget.setPaddingTop( 1 );
      assertEquals( 1 , widget.getFrameHeight() );      
      widget.setPaddingBottom( 1 );
      assertEquals( 2 , widget.getFrameHeight() );      
      assertEquals( 0 , widget.getFrameWidth() );
      widget.setPaddingLeft( 1 );
      assertEquals( 1 , widget.getFrameWidth() );      
      widget.setPaddingRight( 1 );
      assertEquals( 2 , widget.getFrameWidth() );      
      this.disposeWidget( widget );
    },
    
    testInvalidatePreferredInnerDimension : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellDimension( 0, 100, 100 );
      widget.setCellDimension( 1, 100, 100 );
      widget.setCellContent( 1, null );      
      widget.setSpacing( 0 );
      assertEquals( 200, widget.getPreferredInnerWidth() );
      assertEquals( 100, widget.getPreferredInnerHeight() );
      widget.setSpacing( 10 );
      assertEquals( 210, widget.getPreferredInnerWidth() );
      widget.setCellDimension( 1, 10, 200 );
      assertEquals( 120, widget.getPreferredInnerWidth() );
      assertEquals( 200, widget.getPreferredInnerHeight() );      
      widget.setCellDimension( 1, null, null );      
      assertEquals( 100, widget.getPreferredInnerWidth() );
      assertEquals( 100, widget.getPreferredInnerHeight() );
      this.disposeWidget( widget );      
    },

    testEnabled : function() {
      var widget = this.createDefaultWidget();
      widget.setEnabled( false );
      this.initWidget( widget, true );
      var node = widget._getTargetNode().firstChild;
      assertTrue( this.testUtil.hasElementOpacity( node ) );
      widget.setEnabled( true );
      assertFalse( this.testUtil.hasElementOpacity( node ) );
      this.disposeWidget( widget );
    },

    testOverflow : function() {
      var widget = this.createDefaultWidget();
      widget.setDimension( 100, 100 )      
      this.initWidget( widget, true );
      var cell0 = this.testUtil.getElementBounds(
        widget._getTargetNode().firstChild );
      var cell1 = this.testUtil.getElementBounds( 
        widget._getTargetNode().lastChild );
      assertFalse( cell0.left < 0 );
      assertFalse( cell1.right < 0 );
      assertFalse( cell0.top < 0 );
      assertFalse( cell0.bottom < 0 );
      assertFalse( cell1.top < 0 );
      assertFalse( cell1.bottom < 0 );
      widget.setCellContent( 1, "looooooooooooooooooooooooooooooooooooooong");
      widget.setCellDimension( 0, 16, 150 );
      this.flush();
      cell0 = this.testUtil.getElementBounds( 
        widget._getTargetNode().firstChild );
      cell1 = this.testUtil.getElementBounds( 
        widget._getTargetNode().lastChild );      
      assertTrue( cell0.left < 0 );
      assertTrue( cell1.right < 0 );
      assertTrue( cell0.top < 0 );
      assertTrue( cell0.bottom < 0 );
      assertFalse( cell1.top < 0 );
      assertFalse( cell1.bottom < 0 );
      this.disposeWidget( widget );
    },
    
    testFont : function() {
      var widget = this.createDefaultWidget();  
      widget.setFont( new qx.ui.core.Font( 10, [ "monospace" ] ) );
      this.initWidget( widget, true );
      var style = widget._getTargetNode().lastChild.style;
      assertEquals( '10px', style.fontSize );
      assertTrue( style.fontFamily.search( 'monospace' ) != -1 );
      widget.setFont( new qx.ui.core.Font( 12, [ "serif" ] ) );
      assertEquals( '12px', style.fontSize );
      assertTrue( style.fontFamily.search( 'serif' ) != -1 );
      this.disposeWidget( widget );
    },
    
    testTextColor : function() {
      var widget = this.createDefaultWidget();
      widget.setTextColor( "#FF0000" );  
      this.initWidget( widget, true );
      var style = widget._getTargetNode().style;
      var rgb = qx.util.ColorUtil.stringToRgb( style.color );
      assertEquals( 255, rgb[ 0 ] ); 
      assertEquals( 0, rgb[ 1 ] );
      assertEquals( 0 , rgb[ 2 ] );      
      widget.setTextColor( "#00FF00" );
      rgb = qx.util.ColorUtil.stringToRgb( style.color );
      assertEquals( 0, rgb[ 0 ] ); 
      assertEquals( 255, rgb[ 1 ] );
      assertEquals( 0 , rgb[ 2 ] );      
      this.disposeWidget( widget );     
    },

    testContentNotSelectable : qx.core.Variant.select("qx.client", {
      "mshtml" : function(){},
      "default": function() {
        var widget = this.createDefaultWidget();
        this.initWidget( widget, true );
        var parentNode = widget._getTargetNode();
        assertFalse( 
          this.testUtil.getElementSelectable( widget._getTargetNode() ) );  
        this.disposeWidget( widget );
      } 
    } ),

        
    /* ------------------------ helper ------------------------------- */

    createDefaultWidget : function() {
      return new org.eclipse.rwt.widgets.MultiCellWidget( 
        [ "image", "label"] );
    },

    disposeWidget : function( widget ) {
      widget.setParent( null );
      widget.dispose();
      this.flush();
    },

    initWidget : function( widget, content ) {
      this._currentWidget = widget;
      if( content ) {
        widget.setCellContent( 0, "test.jpg" );
        widget.setCellContent( 1, "test text" );
        widget.setCellDimension( 0, 16, 16 );
      }      
      if( !widget.getParent() ) {
        widget.addToDocument();
      }
      this.flush();
    },
    
    flush : function() {
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    almostEqual : function( value1, value2 ) {
      if( isNaN( value1 ) ) {
        this.warn( "almostEqual: value1 is NaN" );
      }
      if( isNaN( value2 ) ) {
        this.warn( "almostEqual: value2 is NaN" );
      }
      return Math.abs( value1 - value2 ) <= 1; 
    }

  }
} );