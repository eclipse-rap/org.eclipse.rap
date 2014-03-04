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

describe( "DropTarget", function() {

  var control;
  var element;

  var fakeElement = function(){
    return {
      addEventListener : jasmine.createSpy( "addEventListener" ),
      removeEventListener : jasmine.createSpy( "removeEventListener" ),
      // old IE support:
      attachEvent : function( type, fun ) {
        this.addEventListener( type.slice( 2 ), fun, false );
      },
      detachEvent : function( type, fun ) {
        this.removeEventListener( type.slice( 2 ), fun, false );
      }
    };
  };

  beforeEach( function() {
    element = fakeElement();
    control = {
      addEventListener : jasmine.createSpy(),
      removeEventListener : jasmine.createSpy(),
      setDropDataTypes : jasmine.createSpy(),
      setSupportsDropMethod : jasmine.createSpy(),
      toHashCode : jasmine.createSpy(),
      getUserData : jasmine.createSpy(),
      setUserData : jasmine.createSpy(),
      getElement : function() { return element; }
    };
  } );

  afterEach( function() {
    rwt.remote.ObjectRegistry.remove( "some-id" );
  } );

  describe( "constructor", function() {

    it( "assigns control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( dropTarget.control ).toBe( control );
    } );

    it( "assigns actions", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [ "DROP_MOVE", "DROP_COPY" ] );

      expect( dropTarget.actions ).toEqual( { "move" : true, "copy" : true } );
    } );

    it( "registers listeners with control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( control.addEventListener.calls.length ).toEqual( 4 );
      expect( control.addEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragover" );
      expect( control.addEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragmove" );
      expect( control.addEventListener.calls[ 2 ].args[ 0 ] ).toEqual( "dragout" );
      expect( control.addEventListener.calls[ 3 ].args[ 0 ] ).toEqual( "dragdrop" );
    } );

    it( "sets support drop method on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( control.setSupportsDropMethod ).toHaveBeenCalledWith( jasmine.any( Function ) );
    } );

  } );

  describe( "dispose", function() {

    it( "deregisters listeners from control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.removeEventListener.calls.length ).toEqual( 4 );
      expect( control.removeEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragover" );
      expect( control.removeEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragmove" );
      expect( control.removeEventListener.calls[ 2 ].args[ 0 ] ).toEqual( "dragout" );
      expect( control.removeEventListener.calls[ 3 ].args[ 0 ] ).toEqual( "dragdrop" );
    } );

    it( "resets support drop method on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.setSupportsDropMethod.calls[ 1 ].args[ 0 ] ).toEqual( null );
    } );

    it( "clears drop data types on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.setDropDataTypes ).toHaveBeenCalledWith( [] );
    } );

  } );

  describe( "setTransfer", function() {

    it( "sets data types", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.setTransfer( [ "foo", "bar" ] );

      expect( control.setDropDataTypes ).toHaveBeenCalledWith( [ "foo", "bar" ] );
    } );

    it( "registers no DOM event listener without RemoteFileTransfer", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.setTransfer( [ "foo", "bar" ] );

      expect( element.addEventListener ).not.toHaveBeenCalled();
    } );

  } );

  describe( "with fileDrop enabled", function() {

    var event;
    var dropTarget;

    function getListener( type ) {
      var spy = element.addEventListener;
      for( var i = 0; i < spy.callCount; i++ ) {
        if( spy.argsForCall[ i ][ 0 ] === type ) {
          return spy.argsForCall[ i ][ 1 ];
        }
      }
      throw new Error( "Listener " + type + " not found" );
    }

    beforeEach( function() {
      dropTarget = new rwt.widgets.DropTarget( control, [] );
      dropTarget.setFileDropEnabled( true );
      event = {
        "preventDefault" : jasmine.createSpy( "preventDefault" ),
        "stopPropagation" : jasmine.createSpy( "stopPropagation" ),
        "dataTransfer" : {
          "types" : [ "Files" ],
          "files" : [
            {
              "name" : "foo.txt",
              "type" : "text/plain",
              "size" : 9000
            },
            {
              "name" : "bar.html",
              "type" : "text/html",
              "size" : 10000
            }
          ]
        }
      };
      event.dataTransfer.files.item = function( index ) {
        return this[ index ];
      };
    } );

    it( "registers no DOM event listener if browser does not support FormData", function() {
      dropTarget.setFileDropEnabled( false );
      element.addEventListener.reset();
      spyOn( rwt.client.Client, "supportsFileDrop" ).andReturn( false );

      dropTarget.setFileDropEnabled( true );

      expect( element.addEventListener ).not.toHaveBeenCalled();
    } );

    if( window.FormData ) { // Skip on unsupported Browser

      it( "registers DOM event listener", function() {
        expect( element.addEventListener.callCount ).toBe( 3 );
        expect( element.addEventListener )
          .toHaveBeenCalledWith( "dragenter", any( Function ), false );
        expect( element.addEventListener )
          .toHaveBeenCalledWith( "dragover", any( Function ), false );
        expect( element.addEventListener )
          .toHaveBeenCalledWith( "drop", any( Function ), false );
      } );

      it( "removes DOM event listener when disableing fileDrop", function() {
        dropTarget.setFileDropEnabled( false );

        expect( element.removeEventListener.callCount ).toBe( 3 );
        expect( element.removeEventListener )
          .toHaveBeenCalledWith( "dragenter", getListener( "dragenter" ), false );
        expect( element.removeEventListener )
          .toHaveBeenCalledWith( "dragover", getListener( "dragover" ), false );
        expect( element.removeEventListener )
          .toHaveBeenCalledWith( "drop", getListener( "drop" ), false );
      } );

      it( "cancels dragenter events if data is File", function() {

        getListener( "dragenter" )( event );

        expect( event.preventDefault ).toHaveBeenCalled();
        expect( event.stopPropagation ).toHaveBeenCalled();
      } );

      it( "cancels dragenter events if data is File in Firefox/IE", function() {
        // Only in webkit this is actually an array, every other browser has DOMStringList
        event.dataTransfer.types.indexOf = null;
        event.dataTransfer.types.contains = function( value ) {
          return true;
        };

        getListener( "dragenter" )( event );

        expect( event.preventDefault ).toHaveBeenCalled();
        expect( event.stopPropagation ).toHaveBeenCalled();
      } );

      it( "does not cancel dragenter events if data is not File", function() {
        event.dataTransfer.types = [];
        getListener( "dragenter" )( event );

        expect( event.preventDefault ).not.toHaveBeenCalled();
        expect( event.stopPropagation ).not.toHaveBeenCalled();
      } );

      it( "cancels dragover events", function() {
        getListener( "dragover" )( event );

        expect( event.preventDefault ).toHaveBeenCalled();
        expect( event.stopPropagation ).toHaveBeenCalled();
      } );

      it( "cancels drop events", function() {
        getListener( "drop" )( event );

        expect( event.preventDefault ).toHaveBeenCalled();
        expect( event.stopPropagation ).toHaveBeenCalled();
      } );

      describe( "sending DropAccept", function() {

        var connection;
        var remoteObject;
        var fileUploader;

        beforeEach( function() {
          fileUploader = rwt.client.FileUploader.getInstance();
          connection = rwt.remote.Connection.getInstance();
          remoteObject = mock( rwt.remote.RemoteObject, "remoteObject" );
          spyOn( connection, "getRemoteObject" ).andReturn( remoteObject );
          event.type = "drop";
        } );

        it( "calls notify on RemoteObject", function() {
          getListener( "drop" )( event );

          expect( connection.getRemoteObject ).toHaveBeenCalledWith( dropTarget );
          expect( remoteObject.notify ).toHaveBeenCalledWith( "DropAccept", any( Object ) );
        } );

        it( "attaches mouse coordinates", function() {
          event.pageX = 101;
          event.pageY = 102;
          getListener( "drop" )( event );

          var properties = remoteObject.notify.argsForCall[ 0 ][ 1 ];
          expect( properties.x ).toBe( event.pageX );
          expect( properties.y ).toBe( event.pageY );
        } );

        it( "attaches timestamp", function() {
          spyOn( rwt.remote.EventUtil, "eventTimestamp" ).andReturn( 1024 );
          getListener( "drop" )( event );

          var properties = remoteObject.notify.argsForCall[ 0 ][ 1 ];
          expect( properties.time ).toBe( 1024 );
        } );

        it( "attaches operation", function() {
          getListener( "drop" )( event );

          var properties = remoteObject.notify.argsForCall[ 0 ][ 1 ];
          // Currently "move" is the default operation and the only supported by all browser
          expect( properties.operation ).toBe( "move" );
        } );

        it( "attaches feedback", function() {
          getListener( "drop" )( event );

          var properties = remoteObject.notify.argsForCall[ 0 ][ 1 ];
          expect( properties.feedback ).toBe( 0 );
        } );

        it( "attaches files", function() {
          getListener( "drop" )( event );

          var files = remoteObject.notify.argsForCall[ 0 ][ 1 ].files;
          var fileIds = rwt.util.Objects.getKeys( files );
          expect( fileIds.length ).toBe( 2 );
          expect( files[ fileIds[ 0 ] ] ).toEqual( event.dataTransfer.files[ 0 ] );
          expect( files[ fileIds[ 1 ] ] ).toEqual( event.dataTransfer.files[ 1 ] );
        } );

      } );

    } // if FormData

  } );

} );
