/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.test.Asserts", {
  type : "static",

  statics : {

  	createShortcuts : function(){
  		assertTrue = org.eclipse.rwt.test.Asserts.assertTrue;
  		assertFalse = org.eclipse.rwt.test.Asserts.assertFalse;
  		assertNull = org.eclipse.rwt.test.Asserts.assertNull;
  		assertNotNull = org.eclipse.rwt.test.Asserts.assertNotNull;
  		assertEquals = org.eclipse.rwt.test.Asserts.assertEquals;
  		assertIdentical = org.eclipse.rwt.test.Asserts.assertIdentical;
  		assertContains = org.eclipse.rwt.test.Asserts.assertContains;
  		assertContainsNot = org.eclipse.rwt.test.Asserts.assertContainsNot;
  		assertLarger = org.eclipse.rwt.test.Asserts.assertLarger;
  		assertSmaller = org.eclipse.rwt.test.Asserts.assertSmaller;
  		fail = org.eclipse.rwt.test.Asserts.fail;
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
  	
  	_getObjectsDiff : function( object1, object2 ) {
  	  // the "==" and "===" fail for NaN/NaN
  	  var diffsArr = [];
  	  var diffMap = {};
      for( var key in object1 ) {
        if( object1[ key ] !== object2[ key ] ) {
          diffsArr.push( key );
          diffMap[ key ] = true;
        }
      }
      for( var key in object2 ) {
        if( object1[ key ] !== object2[ key ] ) {
          if( !diffMap[ key ] ) {
            diffsArr.push( key );
          }
        }
      }
      return diffsArr;
  	},

  	fail : function() {
  	  org.eclipse.rwt.test.TestRunner.getInstance()._handleException( new Error( "fail()" ) );
  	},
  	
    assertTrue : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, false );
      var failed = ( args.actual !== true );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertTrue",
  	   true,
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
  	
    assertFalse : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, false );
      var failed = ( args.actual !== false );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertFalse",
  	   false,
  	   args.actual,
  	   failed,
  	   args.message 
  	  );
    },
    
    assertNull : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, false );
      var failed = ( args.actual !== null );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertNull",
  	   null,
  	   args.actual,
  	   failed,
  	   args.message 
  	  );
    },

    assertNotNull : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, false );
      var failed = ( args.actual === null );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertNotNull",
  	   undefined,
  	   args.actual,
  	   failed,
  	   args.message 
  	  );
    },
    
    assertEquals : function() {      
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = false;
      var expectedIsObject 
        = typeof args.expected == "object" && args.expected != null;
      var actualIsObject 
        = typeof args.actual == "object" && args.actual != null;
      if( actualIsObject || expectedIsObject ) {
        if( actualIsObject && expectedIsObject ) {
          var diffs = org.eclipse.rwt.test.Asserts._getObjectsDiff( 
            args.expected,
            args.actual
          );
          failed = ( diffs.length > 0 );
        } else {
          failed = true;
        }
      } else {
        failed = ( args.actual != args.expected );
      }
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertEquals",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
    
    assertIdentical : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = ( args.actual !== args.expected );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertIdentical",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
    
    assertContains : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = !(    args.actual.indexOf 
                      && args.actual.indexOf( args.expected ) != -1 );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertContains",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
    
    assertContainsNot : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = (    args.actual.indexOf 
                     && args.actual.indexOf( args.expected ) != -1 );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertContainsNot",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
    
    assertLarger : function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = ( args.actual <= args.expected );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertLarger",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    },
    
    assertSmaller: function() {
      var args = org.eclipse.rwt.test.Asserts._getArguments( arguments, true );
      var failed = ( args.actual >= args.expected );
  	  org.eclipse.rwt.test.TestRunner.getInstance().processAssert(
  	   "assertSmaller",
  	   args.expected, 
  	   args.actual,
  	   failed,
  	   args.message
  	  );
    }
    
  }
});