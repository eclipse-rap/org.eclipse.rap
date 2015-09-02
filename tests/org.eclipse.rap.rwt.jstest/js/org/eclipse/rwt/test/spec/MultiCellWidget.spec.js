/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "MultiCellWidget", function() {

  var MultiCellWidget = rwt.widgets.base.MultiCellWidget;
  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

  var widget;

  describe( "with vertical orientation", function() {

    beforeEach( function() {
      widget = new MultiCellWidget( [ "image", "image", "image" ] );
      widget.setVertical( true );
      widget.setDimension( 200, 600 );
      widget.setPadding( 120, 90, 60, 30 );
      widget.setSpacing( 10 );
      widget.addToDocument();
      TestUtil.flush();
    } );

    afterEach( function() {
      widget.destroy();
    } );


    it( "computes preferred dimension", function() {
      assertEquals( 0, widget.getPreferredInnerWidth() );
      assertEquals( 0, widget.getPreferredInnerHeight() );
    });

    describe( "and content", function() {

      beforeEach( function() {
        widget.setCellContent( 0, "foo0" );
        widget.setCellContent( 1, "foo1" );
        widget.setCellContent( 2, "foo2" );
        widget.setCellDimension( 0, 10, 20 );
        widget.setCellDimension( 1, 20, 40 );
        widget.setCellDimension( 2, 40, 60 );
        TestUtil.flush();
      } );

      it( "centers cells horizontally", function() {
        var bounds = getBounds( widget );
        expect( bounds[ 0 ].left ).toBe( 65 );
        expect( bounds[ 1 ].left ).toBe( 60 );
        expect( bounds[ 2 ].left ).toBe( 50 );
        expect( bounds[ 0 ].right ).toBe( 125 );
        expect( bounds[ 1 ].right ).toBe( 120 );
        expect( bounds[ 2 ].right ).toBe( 110 );
      });

      it( "stacks items vertically in the middle from top to bottom", function() {
        var bounds = getBounds( widget );
        expect( bounds[ 0 ].top ).toBe( 260 ); // 120 + (600 - 120 - 60) / 2 - (20 + 10 + 40 + 10 + 60) / 2
        expect( bounds[ 0 ].height ).toBe( 20 );
        expect( bounds[ 1 ].top ).toBe( 290 );
        expect( bounds[ 1 ].height ).toBe( 40 );
        expect( bounds[ 2 ].top ).toBe( 340 );
        expect( bounds[ 2 ].height ).toBe( 60 );
      });

      it( "computes preferred dimension", function() {
        assertEquals( 40, widget.getPreferredInnerWidth() );
        assertEquals( 140, widget.getPreferredInnerHeight() );
      });

    });

  });

  function getBounds( widget ) {
    return widget.$cells.map(function( cell ) {
      return TestUtil.getElementBounds( cell.get( 0 ) );
    });
  }


} );
