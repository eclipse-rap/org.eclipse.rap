/*******************************************************************************
 * Copyright (c) 2014, 2018 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*jshint nonew:false */
describe( "DropDownSynchronizer", function() {

  var dropdown;
  var remoteObject;

  beforeEach( function() {
    dropdown = mock( rwt.widgets.DropDown, "dropdown" );
    remoteObject = mock( rwt.remote.RemoteObject, "remoteObject" );
    var synchronizer = new rwt.widgets.util.DropDownSynchronizer( dropdown );
    var connection = rwt.remote.Connection.getInstance();
    spyOn( connection, "getRemoteObject" ).andReturn( remoteObject );
  } );

  describe( "Show event", function() {

    it( "sets visible property to true", function() {
      dropdown.getVisible.andReturn( true );

      notifyListener( "Show", { "widget" : dropdown, "type" : "Show" } );

      expect( remoteObject.set ).toHaveBeenCalledWith( "visible", true );
    } );

  } );

  describe( "Hide event", function() {

    it( "sets visible property to false", function() {
      dropdown.getVisible.andReturn( false );

      notifyListener( "Hide", { "widget" : dropdown, "type" : "Hide" } );

      expect( remoteObject.set ).toHaveBeenCalledWith( "visible", false );
    } );

  } );

  describe( "Selection event", function() {

    it( "sets selectionIndex property", function() {
      dropdown.getSelectionIndex.andReturn( 3 );

      notifyListener( "Selection", { "widget" : dropdown, "type" : "Selection" } );

      expect( remoteObject.set ).toHaveBeenCalledWith( "selectionIndex", 3 );
    } );

    it( "notifies Selection", function() {
      dropdown.getSelectionIndex.andReturn( 3 );

      var event = { "widget" : dropdown, "type" : "Selection", "text" : "foo", "index" : 3 };
      notifyListener( "Selection", event );

      var properties = { "index" : 3 };
      expect( remoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

  } );

  describe( "DefaultSelection event", function() {

    it( "sets selectionIndex property", function() {
      dropdown.getSelectionIndex.andReturn( 3 );

      notifyListener( "DefaultSelection", { "widget" : dropdown, "type" : "DefaultSelection" } );

      expect( remoteObject.set ).toHaveBeenCalledWith( "selectionIndex", 3 );
    } );

    it( "notifies DefaultSelection", function() {
      dropdown.getSelectionIndex.andReturn( 3 );

      var event = { "widget" : dropdown, "type" : "DefaultSelection", "text" : "foo", "index" : 3 };
      notifyListener( "DefaultSelection", event );

      var properties = { "index" : 3 };
      expect( remoteObject.notify ).toHaveBeenCalledWith( "DefaultSelection", properties );
    } );

  } );

  function notifyListener( type, event ) {
    getListener( type )( event );
  }

  function getListener( type ) {
    var spy = dropdown.addListener;
    for( var i = 0; i < spy.callCount; i++ ) {
      if( spy.argsForCall[ i ][ 0 ] === type ) {
        return spy.argsForCall[ i ][ 1 ];
      }
    }
    throw new Error( "Listener " + type + " not found" );
  }

} );
