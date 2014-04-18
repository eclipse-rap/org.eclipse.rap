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

describe( "util.Strings", function() {

  var Strings = rwt.util.Strings;

  describe( "contains", function() {

    it( "discovers substring at start", function() {
      expect( Strings.contains( "foo", "fo" ) ).toBe( true );
    });

    it( "discovers substring at end", function() {
      expect( Strings.contains( "bar", "ar" ) ).toBe( true );
    });

    it( "regards entire string as substring", function() {
      expect( Strings.contains( "foo", "foo" ) ).toBe( true );
    });

    it( "regards empty string as substring", function() {
      expect( Strings.contains( "foo", "" ) ).toBe( true );
    });

    it( "rejects super string", function() {
      expect( Strings.contains( "bar", "bart" ) ).toBe( false );
    });

  });

  describe( "toFirstUp", function() {

    it( "turns first char of a string to upper case", function() {
      expect( Strings.toFirstUp( "foo" ) ).toBe( "Foo" );
    });

    it( "does not change title-case string", function() {
      expect( Strings.toFirstUp( "Foo" ) ).toBe( "Foo" );
    });

    it( "does not change empty string", function() {
      expect( Strings.toFirstUp( "" ) ).toBe( "" );
    });

  });

});
