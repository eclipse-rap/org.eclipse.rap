/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.LegacyAsserts", {

  statics : {

    createShortcuts : function(){
      assertTrue = org.eclipse.rwt.test.LegacyAsserts.assertTrue;
      assertFalse = org.eclipse.rwt.test.LegacyAsserts.assertFalse;
      assertNull = org.eclipse.rwt.test.LegacyAsserts.assertNull;
      assertNotNull = org.eclipse.rwt.test.LegacyAsserts.assertNotNull;
      assertEquals = org.eclipse.rwt.test.LegacyAsserts.assertEquals;
      assertIdentical = org.eclipse.rwt.test.LegacyAsserts.assertIdentical;
      assertContains = org.eclipse.rwt.test.LegacyAsserts.assertContains;
      assertContainsNot = org.eclipse.rwt.test.LegacyAsserts.assertContainsNot;
      assertLarger = org.eclipse.rwt.test.LegacyAsserts.assertLarger;
      assertSmaller = org.eclipse.rwt.test.LegacyAsserts.assertSmaller;
      fail = org.eclipse.rwt.test.LegacyAsserts.fail;
    },

    _getArguments : function( args, hasExpected ) {
      var ret = {};
      if( hasExpected && args.length == 3 ) {
        ret.message = args[ 0 ];
        ret.expected = args[ 1 ];
        ret.actual = args[ 2 ];
      } else if( hasExpected && args.length == 2 ) {
        ret.message = "";
        ret.expected = args[ 0 ];
        ret.actual = args[ 1 ];
      } else if( !hasExpected && args.length == 2 ) {
        ret.message = args[ 0 ];
        ret.actual = args[ 1 ];
      } else if( !hasExpected && args.length == 1 ) {
        ret.message = "";
        ret.actual = args[ 0 ];
      } else {
        throw( "Error: invalid arguments length in assert!" );
      }
      return ret;
    },

    fail : function() {
      throw new Error( "fail invoked" );
    },

    assertTrue : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, false );
      expect( args.actual ).toBe( true );
    },

    assertFalse : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, false );
      expect( args.actual ).toBe( false );
    },

    assertNull : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, false );
      expect( args.actual ).toBeNull();
    },

    assertNotNull : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, false );
      expect( args.actual ).not.toBeNull();
    },

    assertEquals : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).toEqual( args.expected );
    },

    assertIdentical : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).toBe( args.expected );
    },

    assertContains : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).toContain( args.expected );
    },

    assertContainsNot : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).not.toContain( args.expected );
    },

    assertLarger : function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).toBeGreaterThan( args.expected );
    },

    assertSmaller: function() {
      var args = org.eclipse.rwt.test.LegacyAsserts._getArguments( arguments, true );
      expect( args.actual ).toBeLessThan( args.expected );
    }

  }
});
