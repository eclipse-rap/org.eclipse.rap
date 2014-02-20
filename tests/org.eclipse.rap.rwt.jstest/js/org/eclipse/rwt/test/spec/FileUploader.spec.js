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

describe( "FileUploader", function() {

  var any = jasmine.any;
  var fileUploader;

  beforeEach( function() {
    fileUploader = new rwt.client.FileUploader();
  } );

  if( window.FormData ) {

    describe( "createFormData", function() {

      it( "returns new FormData", function() {
        expect( rwt.client.FileUploader.createFormData() ).toEqual( any( FormData ) );
      } );

    } );

  }

  describe( "addFile", function() {

    it( "returns unique values for different objects", function() {
      var id1 = fileUploader.addFile( {} );
      var id2 = fileUploader.addFile( {} );

      expect( id1 ).not.toEqual( id2 );
    } );

  } );

  describe( "getFileById", function() {

    it( "returns same Object as registered", function() {
      var file = {};
      var id = fileUploader.addFile( file );

      expect( fileUploader.getFileById( id ) ).toBe( file );
    } );

  } );

  if( window.FormData ) {

    describe( "submit", function() {

      var fileIds;
      var files = [ { "name" : "file1.txt" }, { "name" : "file2.txt" } ];
      var formDataMock;
      var xhrMock;

      beforeEach( function() {
        fileIds = [
          fileUploader.addFile( files[ 0 ] ),
          fileUploader.addFile( files[ 1 ] )
        ];
        formDataMock = mock( FormData );
        xhrMock = mock( XMLHttpRequest );
        spyOn( rwt.client.FileUploader, "createFormData" ).andReturn( formDataMock );
        spyOn( rwt.remote.Request, "createXHR" ).andReturn( xhrMock );
      } );

      it( "creates FormData", function() {
        fileUploader.submit( { "fileIds" : fileIds, "url" : "http://www.foo.bar/" } );

        expect( rwt.client.FileUploader.createFormData ).toHaveBeenCalled();
      } );

      it( "adds files to FormData", function() {
        fileUploader.submit( { "fileIds" : fileIds, "url" : "http://www.foo.bar/" } );

        expect( formDataMock.append ).toHaveBeenCalledWith( "file1.txt", same( files[ 0 ] ) );
        expect( formDataMock.append ).toHaveBeenCalledWith( "file2.txt", same( files[ 1 ] ) );
      } );

      it( "sends the Request", function() {
        fileUploader.submit( { "fileIds" : fileIds, "url" : "http://www.foo.bar/" } );

        expect( rwt.remote.Request.createXHR ).toHaveBeenCalled();
        expect( xhrMock.open ).toHaveBeenCalledWith( "POST", "http://www.foo.bar/" );
        expect( xhrMock.send ).toHaveBeenCalledWith( same( formDataMock ) );
      } );

      it( "throws exception for unkown ids", function() {
        expect( function() {
          fileUploader.submit( { "fileIds" : [ "notid" ], "url" : "http://www.foo.bar/" } );
        } ).toThrow( "Unkown file id \"notid\"." );
      } );

    } );

  }

} );
