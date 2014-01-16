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

(function() {

var selection;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SelectionTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      selection = new rwt.widgets.util.Selection();
    },

    tearDown : function() {
      selection.dispose();
    },

    testSelectionInitialEmpty : function() {
      assertEquals( [], selection.toArray() );
    },

    testIsEmpty : function() {
      assertTrue( selection.isEmpty() );

      selection.add( "a" );

      assertFalse( selection.isEmpty() );
    },

    testAdd : function() {
      selection.add( "a" );

      assertEquals( [ "a" ], selection.toArray() );
    },

    testAdd_twice : function() {
      selection.add( "a" );
      selection.add( "a" );

      assertEquals( [ "a" ], selection.toArray() );
    },

    testRemove : function() {
      selection.add( "a" );
      selection.add( "b" );
      selection.add( "c" );

      selection.remove( "b" );

      assertEquals( [ "a", "c" ], selection.toArray() );
    },

    testRemove_missing : function() {
      selection.add( "a" );

      selection.remove( "b" );

      assertEquals( [ "a" ], selection.toArray() );
    },

    testRemoveAll : function() {
      selection.add( "a" );
      selection.add( "b" );
      selection.add( "c" );

      selection.removeAll();

      assertEquals( [], selection.toArray() );
      assertTrue( selection.isEmpty() );
    },

    testContains : function() {
      selection.add( "a" );

      assertTrue( selection.contains( "a" ) );
      assertFalse( selection.contains( "b" ) );
    },

    testGetFirst : function() {
      selection.add( "a" );
      selection.add( "b" );
      selection.add( "c" );

      assertEquals( "a", selection.getFirst() );
    },

    testGetFirst_onEmptySelection : function() {
      assertNull( selection.getFirst() );
    },

    testGetChangeValue : function() {
      selection.add( "b" );
      selection.add( "c" );
      selection.add( "a" );

      assertEquals( "a;b;c", selection.getChangeValue() );
    }

  }

} );

}());
