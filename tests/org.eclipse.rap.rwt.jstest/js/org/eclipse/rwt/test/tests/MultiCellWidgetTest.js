/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MultiCellWidgetTest", {

  extend : rwt.qx.Object,

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

    testEmptyContentNotCreated : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellContent( 0, null );
      widget.setCellContent( 1, null );
      TestUtil.flush();
      assertEquals( 0, widget._getTargetNode().childNodes.length );
      this.disposeWidget( widget );
    },

    testNoDisplayContentCreated : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setDisplay( false );
      TestUtil.flush();
      assertEquals( 2, widget._getTargetNode().childNodes.length );
      this.disposeWidget( widget );
    },

    testContent : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );

      var url = TestUtil.getCssBackgroundImage( widget._getTargetNode().firstChild );
      if( rwt.client.Client.isWebkit() ) {
        assertTrue( url.indexOf( "test.jpg" ) !== -1 );
      } else {
        assertEquals( "test.jpg", url );
      }
      assertEquals( "test text", widget._getTargetNode().lastChild.innerHTML );
      this.disposeWidget( widget );
    },

    testSetCellVisible : function() {
      var widget = this.createDefaultWidget();
      widget.setCellVisible( 0, false );
      this.initWidget( widget, true );
      var node0 = widget._getTargetNode().firstChild;
      var node1 = widget._getTargetNode().lastChild;
      assertEquals( "", node1.style.display );
      assertEquals( "none", node0.style.display );
      widget.setCellVisible( 0, true );
      widget.setCellVisible( 1, false );
      assertEquals( "", node0.style.display );
      assertEquals( "none", node1.style.display );
      this.disposeWidget( widget );
    },

    testSpacing : function() {
      var widget = new rwt.widgets.base.MultiCellWidget(
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
        TestUtil.getElementBounds( firstChild ).left );
      widget.set( {
        paddingRight : 11,
        horizontalChildrenAlign : "right"
      } );
      this.initWidget( widget, true );
      assertEquals(
        "padding right is 11",
        11,
        TestUtil.getElementBounds( lastChild ).right );
      widget.set( {
        paddingTop : 7,
        verticalChildrenAlign : "top"
      } );
      this.initWidget( widget, true );
      assertEquals(
        "padding top is 7",
        7,
        TestUtil.getElementBounds( firstChild ).top );
      assertEquals(
        "padding top is 7",
        7,
        TestUtil.getElementBounds( lastChild ).top );
      widget.set( {
        paddingBottom : 19,
        verticalChildrenAlign : "bottom"
      } );
      this.initWidget( widget, true );
      assertEquals(
        "padding bottom is 10",
        19,
        TestUtil.getElementBounds( firstChild ).bottom );
      assertEquals(
        "padding bottom is 19",
        19,
        TestUtil.getElementBounds( lastChild ).bottom );
      this.disposeWidget( widget );
    },

    testCenter : function() {
      var widget = this.createDefaultWidget();
      widget.setDimension( 100, 100 );
      this.initWidget( widget, true );
      widget.set( {
        padding : 0,
        horizontalChildrenAlign : "center",
        verticalChildrenAlign : "middle",
        spacing : 5
      } );
      this.initWidget( widget, true );
      var cell0 = TestUtil.getElementBounds( widget._getTargetNode().firstChild );
      var cell1 = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertTrue( "1 - horizontal align", this.almostEqual( cell0.left, cell1.right ) );
      assertTrue( "2 - vertical align cell0", this.almostEqual( cell0.top, cell0.bottom ) );
      assertTrue( "3 - vertical align cell1", this.almostEqual( cell1.top, cell1.bottom ) );
      this.disposeWidget( widget );
    },

    testPreferredDimension : function() {
      var widget = new rwt.widgets.base.MultiCellWidget(
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
      TestUtil.flush();
      var cell0 = TestUtil.getElementBounds( widget._getTargetNode().firstChild );
      var cell1 = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertEquals( 11, cell0.width );
      assertEquals( 12, cell0.height );
      assertEquals( 13, cell1.width );
      assertEquals( 14, cell1.height );
      this.disposeWidget( widget );
    },

    testFlexibleCellSize : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 100 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellDimension( 1, 30, 30 );
      TestUtil.flush();
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

    testFlexibleCellSize_withExpandFlexCell : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 100 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellDimension( 1, 30, 30 );
      TestUtil.flush();

      widget.expandFlexCell( true );
      widget.setCellDimension( 0, 20, 20 );

      assertEquals( 56, widget.getPreferredInnerWidth() );
      assertEquals( 30, widget.getPreferredInnerHeight() );
      assertEquals( [ 20, 20 ], widget.getCellDimension( 0 ) );
      assertEquals( [ 64, 30 ], widget.getCellDimension( 1 ) );
      this.disposeWidget( widget );
    },

    testFlexibleCellBounds_withWordWrap : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 400 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellContent( 1, "some longer text that wraps" );
      TestUtil.flush();
      var originalTextBounds = TestUtil.getElementBounds( widget._getTargetNode().lastChild );

      widget.setWordWrap( true );
      widget.setWidth( 80 );
      TestUtil.flush();

      var newTextBounds = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertTrue( originalTextBounds.width > newTextBounds.width );
      assertTrue( originalTextBounds.height < newTextBounds.height );

      widget.setWidth( 400 );
      TestUtil.flush();

      newTextBounds = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertEquals( originalTextBounds, newTextBounds );
      this.disposeWidget( widget );
    },

    testFlexibleCellBounds_withoutWordWarp : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setWidth( 400 );
      widget.setHeight( 100 );
      widget.setPadding( 5 );
      widget.setSpacing( 6 );
      widget.setFlexibleCell( 1 );
      widget.setCellContent( 1, "some longer text that wraps" );
      TestUtil.flush();
      var originalTextBounds = TestUtil.getElementBounds( widget._getTargetNode().lastChild );

      widget.setWordWrap( false );
      widget.setWidth( 80 );
      TestUtil.flush();

      var newTextBounds = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertTrue( originalTextBounds.width > newTextBounds.width );
      assertEquals( originalTextBounds.height, newTextBounds.height );
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

    testSettingCellWidthTwice : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setCellWidth( 0, 10 );
      assertTrue( widget._isInGlobalJobQueue );
      TestUtil.flush();
      widget.setCellWidth( 0, 10 );
      assertIdentical( undefined, widget._isInGlobalJobQueue );
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
      assertTrue( TestUtil.hasElementOpacity( node ) );
      widget.setEnabled( true );
      assertFalse( TestUtil.hasElementOpacity( node ) );
      this.disposeWidget( widget );
    },

    testOverflow : function() {
      var widget = this.createDefaultWidget();
      widget.setDimension( 100, 100 );
      this.initWidget( widget, true );
      var cell0 = TestUtil.getElementBounds( widget._getTargetNode().firstChild );
      var cell1 = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertFalse( cell0.left < 0 );
      assertFalse( cell1.right < 0 );
      assertFalse( cell0.top < 0 );
      assertFalse( cell0.bottom < 0 );
      assertFalse( cell1.top < 0 );
      assertFalse( cell1.bottom < 0 );
      widget.setCellContent( 1, "looooooooooooooooooooooooooooooooooooooong");
      widget.setCellDimension( 0, 16, 150 );
      TestUtil.flush();
      cell0 = TestUtil.getElementBounds( widget._getTargetNode().firstChild );
      cell1 = TestUtil.getElementBounds( widget._getTargetNode().lastChild );
      assertTrue( cell0.left < 0 );
      assertTrue( cell1.right < 0 );
      assertTrue( cell0.top < 0 );
      assertTrue( cell0.bottom < 0 );
      assertFalse( cell1.top < 0 );
      assertFalse( cell1.bottom < 0 );
      this.disposeWidget( widget );
    },

    testTextOverflow : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );

      widget.setTextOverflow( "ellipsis" );

      var style = widget._getTargetNode().lastChild.style;
      assertEquals( "ellipsis", style.textOverflow );

      widget.setTextOverflow( "clip" );
      assertEquals( "", style.textOverflow );
      widget.destroy();
    },

    testTextOverflow_byTheming : function() {
      var widget = this.createDefaultWidget();
      TestUtil.fakeAppearance( "foo", {
        "style" : function() {
          return {
            textOverflow : "ellipsis"
          };
        }
      } );
      widget.setAppearance( "foo" );
      this.initWidget( widget, true );

      var style = widget._getTargetNode().lastChild.style;
      assertEquals( "ellipsis", style.textOverflow );

      widget.destroy();
    },

    testWordWrap_initialValue : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );

      var style = widget._getTargetNode().lastChild.style;
      assertEquals( "nowrap", style.whiteSpace );
      assertFalse( widget.getWordWrap() );
      widget.destroy();
    },

    testWordWrap_appliesOnFlexibleCell : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      widget.setFlexibleCell( 1 );

      var style = widget._getTargetNode().lastChild.style;
      widget.setWordWrap( true );
      assertEquals( "", style.whiteSpace );
      widget.destroy();
    },

    testWordWrap_doesNotApplyOnNonFlexibleCell : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );

      var style = widget._getTargetNode().lastChild.style;
      widget.setWordWrap( true );
      assertEquals( "nowrap", style.whiteSpace );
      widget.destroy();
    },

    testTextAlign : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget );
      assertEquals( "center", widget.getStyleProperty( "textAlign" ) );
      widget.setHorizontalChildrenAlign( "right" );
      assertEquals( "right", widget.getStyleProperty( "textAlign" ) );
      widget.destroy();
    },

    testFont : function() {
      var widget = this.createDefaultWidget();
      widget.setFont( new rwt.html.Font( 10, [ "monospace" ] ) );
      this.initWidget( widget, true );
      var style = widget._getTargetNode().lastChild.style;
      assertEquals( '10px', style.fontSize );
      assertTrue( style.fontFamily.search( 'monospace' ) != -1 );
      widget.setFont( new rwt.html.Font( 12, [ "serif" ] ) );
      assertEquals( '12px', style.fontSize );
      assertTrue( style.fontFamily.search( 'serif' ) != -1 );
      this.disposeWidget( widget );
    },

    testTextColor : function() {
      var widget = this.createDefaultWidget();
      widget.setTextColor( "#FF0000" );
      this.initWidget( widget, true );
      var style = widget._getTargetNode().style;
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      widget.setTextColor( "#00FF00" );
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
      this.disposeWidget( widget );
    },

    testTextColor_byChangingEnabled_withoutUserColor : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      TestUtil.fakeAppearance( "foo", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? "#FF0000" : "#00FF00"
          };
        }
      } );
      widget.setAppearance( "foo" );
      var style = widget._getTargetNode().style;

      widget.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      widget.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
      this.disposeWidget( widget );
    },

    testTextColor_byChangingEnabled_withUnchangedUserColor : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      TestUtil.fakeAppearance( "foo", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? "#FF0000" : "#00FF00"
          };
        }
      } );
      widget.setAppearance( "foo" );
      widget.setTextColor( "#0000FF" );
      var style = widget._getTargetNode().style;

      widget.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      widget.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 0, 255 ], rwt.util.Colors.stringToRgb( style.color ) );
      this.disposeWidget( widget );
    },

    testTextColor_byChangingEnabled_withChangedUserColor : function() {
      var widget = this.createDefaultWidget();
      this.initWidget( widget, true );
      TestUtil.fakeAppearance( "foo", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? "#FF0000" : "#00FF00"
          };
        }
      } );
      widget.setAppearance( "foo" );
      widget.setTextColor( "#0000FF" );
      var style = widget._getTargetNode().style;

      widget.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      widget.setTextColor( "#FF00FF" );
      widget.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 255, 0, 255 ], rwt.util.Colors.stringToRgb( style.color ) );
      this.disposeWidget( widget );
    },

    testContentNotSelectable : rwt.util.Variant.select("qx.client", {
      "mshtml|trident|opera" : function(){},
      "default": function() {
        var widget = this.createDefaultWidget();
        this.initWidget( widget, true );
        assertFalse( TestUtil.getElementSelectable( widget._getTargetNode() ) );
        this.disposeWidget( widget );
      }
    } ),

    /* ------------------------ helper ------------------------------- */

    createDefaultWidget : function() {
      return new rwt.widgets.base.MultiCellWidget(
        [ "image", "label"] );
    },

    disposeWidget : function( widget ) {
      widget.setParent( null );
      widget.dispose();
      TestUtil.flush();
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
      TestUtil.flush();
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

}() );
