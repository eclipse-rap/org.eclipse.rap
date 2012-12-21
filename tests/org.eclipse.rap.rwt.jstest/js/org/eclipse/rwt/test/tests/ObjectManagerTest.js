/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ObjectManagerTest", {

  extend : rwt.qx.Object,
  
  members : {

    testAdd : function() {
      var manager = rwt.remote.ObjectRegistry;
      var obj = {};

      manager.add( "myId", obj, "myType" );

      assertIdentical( obj, manager.getObject( "myId" ) );
      assertEquals( "myType", manager.getType( "myId" ) );
      this._clearObjectManager();
    },

    testRemove : function() {
      var manager = rwt.remote.ObjectRegistry;
      var obj = {};
      manager.add( "myId", obj, "myType" );

      manager.remove( "myId" );

      assertIdentical( undefined, manager.getObject( "myId" ) );
      assertIdentical( undefined, manager.getType( "myId" ) );
      this._clearObjectManager();
    },

    testApplyObjectIdIsCalled : function() {
      var manager = rwt.remote.ObjectRegistry;
      var log = "";
      var obj = {
        applyObjectId : function( id ) {
          log += id;
        }
      };

      manager.add( "myId", obj, "myType" );

      assertEquals( "myId", log );
      this._clearObjectManager();
    },

    _clearObjectManager: function() {
      var manager = rwt.remote.ObjectRegistry;
      manager._map = {};
      manager._callbacks = {};
    }
  }
  
} );
