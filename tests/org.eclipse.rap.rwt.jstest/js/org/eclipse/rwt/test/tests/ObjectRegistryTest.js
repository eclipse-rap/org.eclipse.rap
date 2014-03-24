/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ObjectRegistryTest", {

  extend : rwt.qx.Object,

  members : {

    testGetObject_withFalsyArgumentReturnsNull : function() {
      assertNull( ObjectRegistry.getObject( null ) );
    },

    testGetObject_withInvalidIdReturnsNull : function() {
      assertNull( ObjectRegistry.getObject( "myId" ) );
    },

    testGetObject_afterObjectWasAddedReturnsId : function() {
      var obj = {};

      ObjectRegistry.add( "myId", obj, {} );

      assertIdentical( obj, ObjectRegistry.getObject( "myId" ) );
    },

    testGetObject_afterObjectWasRemovedReturnsNull : function() {
      ObjectRegistry.add( "myId", {}, {} );

      ObjectRegistry.remove( "myId" );

      assertNull( ObjectRegistry.getObject( "myId" ) );
    },

    testGetId_withFalsyArgumentReturnsNull : function() {
      assertNull( ObjectRegistry.getId( null ) );
    },

    testGetId_withUnregisteredObjectReturnsNull : function() {
      assertNull( ObjectRegistry.getId( {} ) );
    },

    testGetId_withRegisteredObjectReturnsId : function() {
      var obj = {};
      ObjectRegistry.add( "myId", obj, {} );

      assertEquals( "myId", ObjectRegistry.getId( obj ) );
    },

    testGetId_withRemovedObjectReturnsNull : function() {
      var obj = {};
      ObjectRegistry.add( "myId", obj, {} );
      ObjectRegistry.remove( "myId" );

      assertNull( ObjectRegistry.getId( obj ) );
    },

    testContainsObject_withFalsyArgumentReturnsFalse : function() {
      assertFalse( ObjectRegistry.containsObject( null ) );
    },

    testContainsObject_withUnregisteredObjectReturnsFalse : function() {
      assertFalse( ObjectRegistry.containsObject( {} ) );
    },

    testContainsObject_withRegisteredObjectReturnsTrue : function() {
      var obj = {};
      ObjectRegistry.add( "myId", obj, {} );

      assertTrue( ObjectRegistry.containsObject( obj ) );
    },

    testContainsObject_withRemovedObjectReturnsFalse : function() {
      var obj = {};
      ObjectRegistry.add( "myId", obj, {} );
      ObjectRegistry.remove( "myId" );

      assertFalse( ObjectRegistry.containsObject( obj ) );
    },

    testAdd_callsApplyObjectId : function() {
      var log = [];
      var obj = {
        applyObjectId : function( id ) {
          log.push( id );
        }
      };

      ObjectRegistry.add( "myId", obj, {} );

      assertEquals( [ "myId" ], log );
    }

  }

} );

}());