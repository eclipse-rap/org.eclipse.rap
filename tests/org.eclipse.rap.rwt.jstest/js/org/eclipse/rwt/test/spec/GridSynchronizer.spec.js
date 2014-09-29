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

/*jshint nonew:false */
describe( "GridSynchronizer", function() {

  var grid;
  var rootItem;
  var gridRemoteObject;
  var connection;
  var synchronizer;

  beforeEach( function() {
    grid = mock( rwt.widgets.Grid, "grid" );
    rootItem = mock( rwt.widgets.GridItem, "gridItem" );
    grid.getRootItem.andReturn( rootItem );
    gridRemoteObject = mock( rwt.remote.RemoteObject, "gridRemoteObject" );
    synchronizer = new rwt.widgets.util.GridSynchronizer( grid );
    connection = rwt.remote.Connection.getInstance();
    spyOn( connection, "onNextSend" ).andCallFake( function( func ) {
      func();
    } );
    spyOn( connection, "getRemoteObject" ).andReturn( gridRemoteObject );
  } );

  describe( "selectionChanged event", function() {

    var item;
    var itemRemoteObject;

    beforeEach( function() {
      item = mock( rwt.widgets.GridItem, "item" );
      item.isCached.andReturn( true );
      rwt.remote.ObjectRegistry.add( "foo", item );
      itemRemoteObject = mock( rwt.remote.RemoteObject, "itemRemoteObject" );
      connection.getRemoteObject.andCallFake( function( target ) {
        return target === grid ? gridRemoteObject : target === item ? itemRemoteObject : null;
      } );
    } );

    it( "sets selection property", function() {
      grid.getSelection.andReturn( [ item ] );

      notifyListener( "selectionChanged", { "item" : item, "type" : "selection" } );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "selection", [ "foo" ] );
    } );

    it( "sets selection property to unresolved item", function() {
      var item2 = mock( rwt.widgets.GridItem, "item" );
      item2.isCached.andReturn( false );
      item2.getParent.andReturn( item );
      item.indexOf.andReturn( 0 );
      grid.getSelection.andReturn( [ item2 ] );

      notifyListener( "selectionChanged", { "item" : item, "type" : "selection" } );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "selection", [ "foo#0" ] );
    } );

    it( "sets checked property", function() {
      item.isChecked.andReturn( true );

      notifyListener( "selectionChanged", { "item" : item, "type" : "check" } );

      expect( itemRemoteObject.set ).toHaveBeenCalledWith( "checked", true );
    } );

    it( "sets cellChecked property", function() {
      grid.getRenderConfig.andReturn( { "columnCount" : 3 } );
      item.getCellChecked.andReturn( [ true, undefined, false ] );

      notifyListener( "selectionChanged", { "item" : item, "type" : "cellCheck" } );

      expect( itemRemoteObject.set ).toHaveBeenCalledWith( "cellChecked", [ true, false, false ] );
    } );

    it( "notifies Selection", function() {
      grid.getSelection.andReturn( [ item ] );
      var data = {
        "item" : item,
        "type" : "selection",
        "index" : 33,
        "text" : "rap"
      };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "index" : 33,
        "text" : "rap",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
      expect( gridRemoteObject.notify ).not.toHaveBeenCalledWith( "DefaultSelection", properties );
    } );

    it( "notifies Selection with unresolved item", function() {
      var item2 = mock( rwt.widgets.GridItem, "item" );
      item2.isCached.andReturn( false );
      item2.getParent.andReturn( item );
      item.indexOf.andReturn( 0 );
      grid.getSelection.andReturn( [ item2 ] );
      var data = {
        "item" : item2,
        "type" : "selection"
      };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo#0",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

    it( "notifies Selection when check is selected", function() {
      var data = { "item" : item, "type" : "check" };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "detail" : "check",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

    it( "notifies Selection when cell check is selected", function() {
      var data = { "item" : item, "type" : "check", "index" : 1 };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "detail" : "check",
        "index" : 1,
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

    it( "notifies Selection when cell is selected", function() {
      var data = { "item" : item, "type" : "cell", "text" : "bar" };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "detail" : "cell",
        "text" : "bar",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

    it( "notifies Selection when hyperlink is selected", function() {
      var data = { "item" : item, "type" : "hyperlink", "text" : "bar" };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "detail" : "hyperlink",
        "text" : "bar",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Selection", properties );
    } );

    it( "notifies DefaultSelection", function() {
      var data = { "item" : item, "type" : "defaultSelection" };
      notifyListener( "selectionChanged", data );

      var properties = {
        "item" : "foo",
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false
      };
      expect( gridRemoteObject.notify ).not.toHaveBeenCalledWith( "Selection", properties );
      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "DefaultSelection", properties );
    } );

  } );

  describe( "focusItemChanged event", function() {

    var item;

    beforeEach( function() {
      item = mock( rwt.widgets.GridItem, "item" );
      item.isCached.andReturn( true );
      rwt.remote.ObjectRegistry.add( "foo", item );
    } );

    it( "sets focusItem property", function() {
      grid.getFocusItem.andReturn( item );

      notifyListener( "focusItemChanged" );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "focusItem", "foo" );
    } );

    it( "sets focusItem property to unresolved item", function() {
      var item2 = mock( rwt.widgets.GridItem, "item" );
      item2.isCached.andReturn( false );
      item2.getParent.andReturn( item );
      item.indexOf.andReturn( 0 );
      grid.getFocusItem.andReturn( item2 );

      notifyListener( "focusItemChanged" );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "focusItem", "foo#0" );
    } );

  } );

  describe( "topItemChanged event", function() {

    var vScroll = {
      getHasSelectionListener : rwt.util.Functions.returnTrue
    };
    var vScrollRemoteObject;

    beforeEach( function() {
      grid.getVerticalBar.andReturn( vScroll );
      vScrollRemoteObject = mock( rwt.remote.RemoteObject, "vScrollRemoteObject" );
      connection.getRemoteObject.andCallFake( function( target ) {
        return target === grid ? gridRemoteObject : target === vScroll ? vScrollRemoteObject : null;
      } );
    } );

    it( "sets topItemIndex property", function() {
      grid.getTopItemIndex.andReturn( 1 );

      notifyListener( "topItemChanged" );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "topItemIndex", 1 );
    } );

    it( "notifies SetData", function() {
      gridRemoteObject.isListening.andReturn( true );

      notifyListener( "topItemChanged" );

      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "SetData" );
    } );

    it( "notifies vertical scrollbar Selection", function() {
      notifyListener( "topItemChanged" );

      expect( vScrollRemoteObject.notify ).toHaveBeenCalledWith( "Selection" );
    } );

  } );

  describe( "scrollLeftChanged event", function() {

    var hScroll = {
      getValue : function() { return 10; },
      getHasSelectionListener : rwt.util.Functions.returnTrue
    };
    var hScrollRemoteObject;

    beforeEach( function() {
      grid.getHorizontalBar.andReturn( hScroll );
      hScrollRemoteObject = mock( rwt.remote.RemoteObject, "hScrollRemoteObject" );
      connection.getRemoteObject.andCallFake( function( target ) {
        return target === grid ? gridRemoteObject : target === hScroll ? hScrollRemoteObject : null;
      } );
    } );

    it( "sets scrollLeft property", function() {
      notifyListener( "scrollLeftChanged" );

      expect( gridRemoteObject.set ).toHaveBeenCalledWith( "scrollLeft", 10 );
    } );

    it( "notifies horizontal scrollbar Selection", function() {
      notifyListener( "scrollLeftChanged" );

      expect( hScrollRemoteObject.notify ).toHaveBeenCalledWith( "Selection" );
    } );

  } );

  describe( "item update event", function() {

    var item;
    var itemRemoteObject;

    beforeEach( function() {
      item = mock( rwt.widgets.GridItem, "gridItem" );
      item.isCached.andReturn( true );
      rwt.remote.ObjectRegistry.add( "foo", item );
      itemRemoteObject = mock( rwt.remote.RemoteObject, "itemRemoteObject" );
      connection.getRemoteObject.andCallFake( function( target ) {
        return target === grid ? gridRemoteObject : target === item ? itemRemoteObject : null;
      } );
    } );

    it( "sets expanded property", function() {
      notifyListener( "update", { "target" : item, "msg" : "expanded" } );

      expect( itemRemoteObject.set ).toHaveBeenCalledWith( "expanded", true );
    } );

    it( "notifies Expand", function() {
      notifyListener( "update", { "target" : item, "msg" : "expanded" } );

      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Expand", { "item" : "foo" } );
    } );

    it( "notifies Collapse", function() {
      notifyListener( "update", { "target" : item, "msg" : "collapsed" } );

      expect( gridRemoteObject.notify ).toHaveBeenCalledWith( "Collapse", { "item" : "foo" } );
    } );

    it( "sets height property", function() {
      item.getOwnHeight.andReturn( 23 );
      notifyListener( "update", { "target" : item, "msg" : "height" } );

      expect( itemRemoteObject.set ).toHaveBeenCalledWith( "height", 23 );
    } );

    it( "do not set height property if in response", function() {
      rwt.remote.EventUtil.setSuspended( true );
      notifyListener( "update", { "target" : item, "msg" : "height" } );
      rwt.remote.EventUtil.setSuspended( false );

      expect( itemRemoteObject.set ).not.toHaveBeenCalled();
    } );

    it( "do set height property if in response and rendering", function() {
      item.getOwnHeight.andReturn( 23 );

      rwt.remote.EventUtil.setSuspended( true );
      notifyListener( "update", { "target" : item, "msg" : "height", "rendering" : true } );
      rwt.remote.EventUtil.setSuspended( false );

      expect( itemRemoteObject.set ).toHaveBeenCalledWith( "height", 23 );
    } );

  } );

  function notifyListener( type, data ) {
    var mock = type === "update" ? rootItem : grid;
    getListener( mock, type ).apply( synchronizer, [ data ] );
  }

  function getListener( mock, type ) {
    var spy = mock.addEventListener;
    for( var i = 0; i < spy.callCount; i++ ) {
      if( spy.argsForCall[ i ][ 0 ] === type ) {
        return spy.argsForCall[ i ][ 1 ];
      }
    }
    throw new Error( "Listener " + type + " not found" );
  }

} );
