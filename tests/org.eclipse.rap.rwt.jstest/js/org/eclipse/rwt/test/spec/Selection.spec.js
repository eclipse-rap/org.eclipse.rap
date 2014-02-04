/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "Selection", function() {

  var selection;
  var mockItem = function( hashCode ) {
    return {
      toHashCode : function() { return hashCode; }
    };
  };

  beforeEach( function() {
    selection = new rwt.widgets.util.Selection();
  } );

  afterEach( function() {
    selection.dispose();
  } );

  describe( "constructor", function() {

    it( "initializes internal storage", function() {
      expect( selection.__storage ).toEqual( [] );
    } );

  } );

  describe( "toArray", function() {

    it( "should return empty array initially", function() {
      expect( selection.toArray() ).toEqual( [] );
    } );

    it( "should return array with added items", function() {
      var item1 = mockItem( 1 );
      var item2 = mockItem( 2 );

      selection.add( item1 );
      selection.add( item2 );

      expect( selection.toArray() ).toEqual( [ item1, item2 ] );
    } );

  } );

  describe( "isEmpty", function() {

    it( "should return true on empty selection", function() {
      expect( selection.isEmpty() ).toBe( true );
    } );

    it( "should return false non on empty selection", function() {
      selection.add( mockItem( 1 ) );

      expect( selection.isEmpty() ).toBe( false );
    } );

  } );

  describe( "add", function() {

    it( "adds item to selection", function() {
      var item = mockItem( 1 );

      selection.add( item );

      expect( selection.toArray() ).toEqual( [ item ] );
    } );

    it( "does nothing if item is already in selection", function() {
      var item = mockItem( 1 );

      selection.add( item );
      selection.add( item );

      expect( selection.toArray() ).toEqual( [ item ] );
    } );

  } );

  describe( "remove", function() {

    it( "removes item from selection", function() {
      var item1 = mockItem( 1 );
      var item2 = mockItem( 2 );
      var item3 = mockItem( 3 );
      selection.add( item1 );
      selection.add( item2 );
      selection.add( item3 );

      selection.remove( item2 );

      expect( selection.toArray() ).toEqual( [ item1, item3 ] );
    } );

    it( "does nothing if item is not in selection", function() {
      var item = mockItem( 1 );
      selection.add( item );

      selection.remove( mockItem( 2 ) );

      expect( selection.toArray() ).toEqual( [ item ] );
    } );

  } );

  describe( "removeAdd", function() {

    it( "removes all items from selection", function() {
      selection.add( mockItem( 1 ) );
      selection.add( mockItem( 2 ) );
      selection.add( mockItem( 3 ) );

      selection.removeAll();

      expect( selection.toArray() ).toEqual( [] );
      expect( selection.isEmpty() ).toBe( true );
    } );

  } );

  describe( "contains", function() {

    it( "should return true if item is in selection", function() {
      var item = mockItem( 1 );
      selection.add( item );

      expect( selection.contains( item ) ).toBe( true );
    } );

    it( "should return false if item is not in selection", function() {
      expect( selection.contains( mockItem( 1 ) ) ).toBe( false );
    } );

  } );

  describe( "getFirst", function() {

    it( "should return the first item added to selection", function() {
      var item = mockItem( 1 );
      selection.add( item );
      selection.add( mockItem( 2 ) );
      selection.add( mockItem( 3 ) );

      expect( selection.getFirst() ).toEqual( item );
    } );

    it( "should return null on empty selection", function() {
      expect( selection.getFirst() ).toEqual( null );
    } );

  } );

  describe( "getChangeValue", function() {

    it( "should return sorted, ';' separated string with items hash codes", function() {
      selection.add( mockItem( 2 ) );
      selection.add( mockItem( 3 ) );
      selection.add( mockItem( 1 ) );

      expect( selection.getChangeValue() ).toEqual( "1;2;3" );
    } );

    it( "should return the same result for equal selections", function() {
      var item = mockItem( 1 );
      selection.add( item );
      var changeValue1 = selection.getChangeValue();

      selection.removeAll();
      selection.add( item );
      var changeValue2 = selection.getChangeValue();

      expect( changeValue1 === changeValue2 ).toBe( true );
    } );

    it( "should return different results for non equal selections", function() {
      var item1 = mockItem( 1 );
      var item2 = mockItem( 2 );
      selection.add( item1 );
      var changeValue1 = selection.getChangeValue();

      selection.removeAll();
      selection.add( item2 );
      var changeValue2 = selection.getChangeValue();

      expect( changeValue1 === changeValue2 ).toBe( false );
    } );

  } );

} );
