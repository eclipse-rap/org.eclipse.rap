/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "rwt.widgets.RichTextEditor", function() {

  var editor;

  var createEditor = function() {
    editor = new rwt.widgets.RichTextEditor( { "parent" : "w2" } );
  };

  beforeEach( function() {
    rap = new RapMock();
    editor = null;
  } );

  afterEach( function() {
    editor = null;
  } );

  describe( "The Constructor", function() {

    it( "should get a Composite with the parent id", function() {
      spyOn( rap, "getObject" ).andCallThrough();
      createEditor();
      expect( rap.getObject ).toHaveBeenCalledWith( "w2" );
    } );

    it( "should add an element to the composite", function() {
      spyOn( rap.fakeComposite, "append" );
      createEditor();
      expect( rap.fakeComposite.append ).toHaveBeenCalledWith( editor.element );
    } );

    it( "should add a resize listener", function() {
      spyOn( rap.fakeComposite, "addListener" );
      createEditor();
      expect( rap.fakeComposite.addListener ).toHaveBeenCalledWith( "Resize", editor.layout );
    } );

    it( "should add a render listener", function() {
      spyOn( rap, "on" );
      createEditor();
      expect( rap.on ).toHaveBeenCalledWith( "render", editor.onRender );
    } );

  } );

  describe( "The render function", function() {

    beforeEach( function() {
      createEditor();
    } );

    it( "should remove itself from the listeners", function() {
      spyOn( rap, "off" );
      editor.onRender.call();
      expect( rap.off ).toHaveBeenCalledWith( "render", editor.onRender );
    } );

    it( "should create a CKEditor instance with it's own element'", function() {
      spyOn( CKEDITOR, "appendTo" ).andCallThrough();
      editor.onRender.call();
      expect( CKEDITOR.appendTo ).toHaveBeenCalledWith( editor.element );
    } );

    it( "should add a send listener", function() {
      spyOn( rap, "on" );
      editor.onRender.call();
      expect( rap.on ).toHaveBeenCalledWith( "send", editor.onSend );
    } );

    it( "should add a ready listener", function() {
      spyOn( CKEDITOR.editor.prototype, "on" );
      editor.onRender.call();
      expect( editor.editor.on ).toHaveBeenCalledWith( "instanceReady", editor.onReady );
    } );

  } );

  describe( "The layout function", function() {

    beforeEach( function() {
      createEditor();
      editor.onRender.call();
      spyOn( rap.fakeComposite, "getClientArea" ).andReturn( [ 1, 2, 100, 110 ] );
    } );

    var getPosition = function( element ) {
      return [
        parseInt( element.style.left, 10 ),
        parseInt( element.style.top, 10 )
      ];
    };

    describe( "of an editor that is not ready, ", function() {

      it( "does nothing on a Resize event", function() {
        spyOn( editor.editor, "resize" );
        editor.layout.call(); // call without context like the Resize event would
        expect( editor.editor.resize ).not.toHaveBeenCalled();
      } );

      it( "updates the outer element position on a ready event", function() {
        editor.onReady.call();
        expect( getPosition( editor.element ) ).toEqual( [ 1, 2 ] );
      } );

      it( "updates the editor element size on a ready event", function() {
        spyOn( editor.editor, "resize" );
        editor.onReady.call();
        expect( editor.editor.resize ).toHaveBeenCalledWith( 100, 110 );
      } );

    } );

    describe( "of an editor that is ready, ", function() {

      beforeEach( function() {
        editor.onReady.call();
      } );

      it( "updates the outer element position on a Resize event", function() {
        editor.layout.call();
        expect( getPosition( editor.element ) ).toEqual( [ 1, 2 ] );
      } );

      it( "updates the editor element size on a Resize event", function() {
        spyOn( editor.editor, "resize" );
        editor.layout.call();
        expect( editor.editor.resize ).toHaveBeenCalledWith( 100, 110 );
      } );

    } );

  } );

  describe( "The setText function", function() {

    beforeEach( function() {
      createEditor();
      editor.onRender.call();
      spyOn( editor.editor, "setData" );
      editor.setText( "foo" );
    } );

    it( "does nothing when editor is not ready", function() {
      expect( editor.editor.setData ).not.toHaveBeenCalled();
    } );

    it( "calls setData on a ready event", function() {
      editor.onReady.call();
      expect( editor.editor.setData ).toHaveBeenCalledWith( "foo" );
    } );

    it( "calls setData after a ready event", function() {
      editor.onReady.call();
      editor.setText( "bar" );
      expect( editor.editor.setData ).toHaveBeenCalledWith( "bar" );
    } );

  } );

  describe( "The setFont function", function() {

    var body;

    beforeEach( function() {
      jasmine.Clock.useMock(); // ckeditor uses async functions because IE says so
      createEditor();
      editor.onRender.call();
      body = editor.editor.document.getBody();
      spyOn( body, "setStyle" );

      editor.setFont( "13px Arial" );

      jasmine.Clock.tick( 0 );
    } );

    it( "does nothing when editor is not ready", function() {
      expect( body.setStyle ).not.toHaveBeenCalled();
    } );

    it( "calls body.setStyle on a ready event", function() {
      editor.onReady.call();
      jasmine.Clock.tick( 0 );
      expect( body.setStyle ).toHaveBeenCalledWith( "font", "13px Arial" );
    } );

    it( "calls body.setStyle after a ready event", function() {
      editor.onReady.call();
      editor.setFont( "15px Fantasy" );
      jasmine.Clock.tick( 0 );
      expect( body.setStyle ).toHaveBeenCalledWith( "font", "15px Fantasy" );
    } );

  } );

  describe( "The setEditable function", function() {

    beforeEach( function() {
      createEditor();
      editor.onRender.call();
      spyOn( editor.editor, "setReadOnly" );
      editor.setEditable( false );
    } );

    it( "does nothing when editor is not ready", function() {
      expect( editor.editor.setReadOnly ).not.toHaveBeenCalled();
    } );

    it( "calls setReadOnly on a ready event", function() {
      editor.onReady.call();
      expect( editor.editor.setReadOnly ).toHaveBeenCalledWith( true );
    } );

    it( "calls setReadOnly after a ready event", function() {
      editor.onReady.call();
      editor.setEditable( true );
      expect( editor.editor.setReadOnly ).toHaveBeenCalledWith( false );
    } );

  } );

  describe( "The onSend function", function() {

    beforeEach( function() {
      spyOn( rap, "getRemoteObject" ).andCallThrough();
      createEditor();
      editor.onRender.call();
    } );

    describe( "of an editor that has not changed", function() {

      it( "sends nothing", function() {
        spyOn( rap.fakeRemoteObject, "set" );
        editor.onSend.call();
        expect( rap.fakeRemoteObject.set ).not.toHaveBeenCalled();
      } );

      it( "does not call resetDirty", function() {
        spyOn( editor.editor, "resetDirty" );
        editor.onSend.call();
        expect( editor.editor.resetDirty ).not.toHaveBeenCalled();
      } );

    } );

    describe( "of an editor that has changed", function() {

      beforeEach( function() {
        spyOn( editor.editor, "checkDirty" ).andReturn( true );
      } );

      it( "gets a remote object for itself", function() {
        editor.onSend.call();
        expect( rap.getRemoteObject ).toHaveBeenCalledWith( editor );
      } );

      it( "sends text from getData", function() {
        spyOn( rap.fakeRemoteObject, "set" );
        spyOn( editor.editor, "getData" ).andReturn( "foo bar" );
        editor.onSend.call();
        expect( rap.fakeRemoteObject.set ).toHaveBeenCalledWith( "text", "foo bar" );
      } );

      it( "calls resetDirty", function() {
        spyOn( editor.editor, "resetDirty" );
        editor.onSend.call();
        expect( editor.editor.resetDirty ).toHaveBeenCalled();
      } );

    } );

  } );

  describe( "The destroy function", function() {

    beforeEach( function() {
      createEditor();
      editor.onRender.call();
    } );

    it( "de-registers the onSend function", function() {
      spyOn( rap, "off" );
      editor.destroy();
      expect( rap.off ).toHaveBeenCalledWith( "send", editor.onSend );
    } );

    it( "destroys the editor", function() {
      spyOn( editor.editor, "destroy" );
      editor.destroy();
      expect( editor.editor.destroy ).toHaveBeenCalled();
    } );

    it( "doesn't throw if destroyed twice", function() {
      spyOn( editor.editor, "destroy" );
      editor.destroy();
      editor.destroy();
      expect( editor.editor.destroy ).not.toThrow();
    } );

  } );

} );
