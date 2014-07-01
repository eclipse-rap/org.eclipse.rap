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

describe( "ProgressBarHandler", function() {

  var ProgressBar = rwt.widgets.ProgressBar;

  var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ProgressBar" );


  beforeEach( function() {
  } );

  afterEach( function() {
  } );

  describe( "factory", function() {

    var parent;

    beforeEach( function() {
      parent = mock( rwt.widgets.Composite );
      parent.getChildren.andReturn( [] );
      rwt.remote.ObjectRegistry.add( "parent-id", parent, null );
    } );

    afterEach( function() {
      rwt.remote.ObjectRegistry.remove( "parent-id" );
    } );

    it( "creates bar", function() {
      expect( handler.factory( { "parent" : "parent-id" } ) ).toEqual( any( ProgressBar ) );
    } );

    it( "creates horizontal bar", function() {
      var properties = { "parent" : "parent-id", style : [ "HORIZONTAL" ] };
      expect( handler.factory( properties ).isHorizontal() ).toBe( true );
    } );

    it( "creates vertical bar", function() {
      var properties = { "parent" : "parent-id", style : [ "VERTICAL" ] };
      expect( handler.factory( properties ).isVertical() ).toBe( true );
    } );

    it( "creates indeterminate bar", function() {
      var properties = { "parent" : "parent-id", style : [ "INDETERMINATE" ] };
      expect( handler.factory( properties ).isIndeterminate() ).toBe( true );
    } );

  } );

  describe( "properties - ", function() {

    it( "set selection after minimum", function() {
      expect( handler.properties.indexOf( "selection" ) )
        .toBeGreaterThan( handler.properties.indexOf( "minimum" ) );
    } );

    it( "set selection after maximum", function() {
      expect( handler.properties.indexOf( "selection" ) )
        .toBeGreaterThan( handler.properties.indexOf( "maximum" ) );
    } );

    it( "set maximum after maximum", function() {
      expect( handler.properties.indexOf( "maximum" ) )
        .toBeGreaterThan( handler.properties.indexOf( "minimum" ) );
    } );

  } );



} );
