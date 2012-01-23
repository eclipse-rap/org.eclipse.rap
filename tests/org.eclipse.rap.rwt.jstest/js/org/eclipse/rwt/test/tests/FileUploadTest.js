/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.FileUploadTest", {

  extend : qx.core.Object,
    
  members : {

    BLANK : "../rwt-resources/resource/static/html/blank.html",

    testCreateFileUploadByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createFileUploadByProtocol( "w3", "w2" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.FileUpload );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      shell.destroy();
      widget.destroy();
    },

    testDisposeFileUploadByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createFileUploadByProtocol( "w3", "w2" );
      var iframe = widget._iframe;
       org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( widget.isDisposed() );
      assertNull( widget._formElement );
      assertNull( widget._inputElement );
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      if( isMshtml ) {
        // IE disposes with delay
        assertEquals( "javascript:false;", iframe.getSource() );       
      } else {        
        assertTrue( iframe.isDisposed() );      
      }
      shell.destroy();
    },

    testSetTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createFileUploadByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "text" : "text\n && \"text" } );
      assertEquals( "text\n &amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createFileUploadByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "image" : [ "image.png", 10, 20 ] } );
      assertEquals( "image.png", widget.getCellContent( 1 ) );
      assertEquals( [ 10, 20 ], widget.getCellDimension( 1 ) );
      shell.destroy();
      widget.destroy();
    },

    testSubmitProtocol : [
      function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var shell = TestUtil.createShellByProtocol( "w2" );
        var widget = this._createFileUploadByProtocol( "w3", "w2" );
        this._setFileName( widget, "foo" );
        org.eclipse.rwt.protocol.Processor.processOperation( {
          "target" : "w3",
          "action" : "call",
          "method" : "submit",
          "properties" : {
            "url" : this.BLANK
          }
        } );
        TestUtil.delayTest( 600 );
        TestUtil.store( shell );
        TestUtil.store( widget );
      },
      function( widget ) {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var iframe = widget._iframe;
        assertTrue( iframe.isLoaded() );
        assertTrue( iframe.queryCurrentUrl().indexOf( "blank.html" ) != -1 );
        shell.destroy();
        widget.destroy();
      }
    ],

    testSubmitProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createFileUploadByProtocol( "w3", "w2" );
      
      shell.destroy();
      widget.destroy();
    },

    testCreate : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertTrue( upload instanceof org.eclipse.rwt.widgets.FileUpload );
      assertTrue( upload.isInDom() );
      upload.destroy();
    },
  
    testDispose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var iframe = upload._iframe;
      upload.destroy();
      TestUtil.flush();
      assertTrue( upload.isDisposed() );
      assertNull( upload._formElement );
      assertNull( upload._inputElement );
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      if( isMshtml ) {
        // IE disposes with delay
        assertEquals( "javascript:false;", iframe.getSource() );       
      } else {        
        assertTrue( iframe.isDisposed() );      
      }
    },
  
    testText : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.setText( "Hello World!" );
      TestUtil.flush();
      assertEquals( "Hello World!", upload.getCellNode( 2 ).innerHTML );
      upload.destroy();
    },
    
    testImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.setImage( "test.jpg" );
      TestUtil.flush();
      assertTrue(
        TestUtil.getCssBackgroundImage( upload.getCellNode( 1 ) ).search( "test.jpg" ) != -1 
      );
      upload.destroy();
    },

    testHasFormElements : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var form = upload._getTargetNode().lastChild;
      assertEquals( "form", form.tagName.toLowerCase() );
      assertEquals( "input", form.firstChild.tagName.toLowerCase() );
      assertIdentical( form, upload._formElement );
      assertIdentical( form.firstChild, upload._inputElement );
      upload.destroy();
    },

    testKeepFormElementsWhenAddingContent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertEquals( 1, upload._getTargetNode().childNodes.length );
      var originalForm = upload._formElement;
      var originalInput = upload._inputElement;
      upload.setText( "bla" );
      TestUtil.flush();
      assertEquals( 2, upload._getTargetNode().childNodes.length );
      var form = upload._getTargetNode().lastChild;
      var input = form.firstChild;
      assertEquals( "form", form.tagName.toLowerCase() );
      assertEquals( "input", input.tagName.toLowerCase() );
      assertIdentical( form, upload._formElement );
      assertIdentical( input, upload._inputElement );
      assertIdentical( form, originalForm);
      assertIdentical( input, originalInput );
      upload.destroy();
    },

    testFormElementsDefaultAttributes : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var form = upload._formElement;
      assertEquals( "POST", form.getAttribute( "method" ).toUpperCase() );
      assertEquals( "multipart/form-data", form.getAttribute( "enctype" ) );
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      if( isMshtml ) {
        assertEquals( "multipart/form-data", form.getAttribute( "encoding" ) );        
      }
      var input = upload._inputElement;
      assertEquals( "file", input.getAttribute( "name" ) ); // Make custom?
      assertEquals( "file", input.getAttribute( "type" ) );
      assertEquals( "1", input.getAttribute( "size" ) );
      assertEquals( "absolute", input.style.position );
      upload.destroy();
    },

    testTarget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var target = upload._formElement.getAttribute( "target" );
      var iframe = upload._iframe.getIframeNode();
      assertEquals( target, iframe.getAttribute( "name" ) ); 
      upload.destroy();
    },

    testIframeProperties : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var iframe = upload._iframe;
      assertEquals( TestUtil.getDocument(), iframe.getParent() );
      assertEquals( 0, iframe.getHeight() );
      assertEquals( 0, iframe.getWidth() );
      assertFalse( iframe.getVisibility() );
      upload.destroy();
    },

    testOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var input = upload._inputElement;
      TestUtil.hasElementOpacity( input );
      upload.destroy();      
    },

    testInputLayout : function() {
      var upload = this._createFileUpload();
      this._checkInputLayout( upload );
      upload.destroy(); 
    },

    testInputLayoutResized : function() {
      var upload = this._createFileUpload();
      upload.setWidth( 200 );
      upload.setHeight( 40 );
      this._checkInputLayout( upload );
      upload.destroy();       
    },

    testInputLayoutNotVisible : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      upload.setVisibility( false );
      TestUtil.flush();
      upload.setVisibility( true );
      TestUtil.flush();
      this._checkInputLayout( upload );
      upload.destroy();       
    },

    testInputLayoutParentNotVisible : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      var parent = new org.eclipse.swt.widgets.Composite();
      parent.addToDocument();
      upload.setParent( parent );
      upload.getParent().setVisibility( false );
      TestUtil.flush();
      upload.getParent().setVisibility( true );
      TestUtil.flush();
      this._checkInputLayout( upload );
      upload.destroy();       
    },

    testCursor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      upload.setCursor( "pointer" );
      TestUtil.flush();
      assertEquals( "", upload.getElement().style.cursor );
      assertEquals( "pointer", upload._inputElement.style.cursor );
      upload.setCursor( "default" );
      assertEquals( "", upload.getElement().style.cursor );
      assertEquals( "default", upload._inputElement.style.cursor );
      upload.destroy(); 
    },

    testMouseDown : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      TestUtil.fakeMouseEventDOM( upload.getElement(), "mousedown" );
      assertFalse( upload.hasState( "pressed" ) );
      TestUtil.fakeMouseEventDOM( upload._inputElement, "mousedown" );
      assertTrue( upload.hasState( "pressed" ) );
      TestUtil.fakeMouseEventDOM( upload.getElement(), "mouseup" );
      assertFalse( upload.hasState( "over" ) );
      upload.destroy(); 
    },

    testMouseOver : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      TestUtil.hoverFromTo( document.body, upload.getElement() );
      assertFalse( upload.hasState( "over" ) );
      TestUtil.hoverFromTo( upload.getElement(), upload._inputElement );
      assertTrue( upload.hasState( "over" ) );
      upload.destroy(); 
    },

    testDontInterfereWithKeyEvents : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.focus();
      TestUtil.keyDown( upload.getElement(), "Space" );
      assertFalse( upload.hasState( "pressed" ) );
      upload.addState( "pressed" );
      TestUtil.keyUp( upload.getElement(), "Space" );
      assertTrue( upload.hasState( "pressed" ) );
      upload.destroy(); 
    },

    testShowFocusIndicator : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var isChrome = org.eclipse.rwt.Client.getBrowser() === "chrome";
      var focusIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
      if( focusIndicator._frame == null ) {
        focusIndicator._createFrame();
      }
      var upload = this._createFileUpload();
      var node = upload._getTargetNode();
      upload.focus();
      assertEquals( !isChrome, focusIndicator._frame.parentNode === node );
      upload.blur();
      assertFalse( focusIndicator._frame.parentNode === node );
      TestUtil.click( upload );
      assertEquals( !isChrome, focusIndicator._frame.parentNode === node );
      upload.focus();
      upload._ontabfocus();
      assertTrue( focusIndicator._frame.parentNode === node );
      upload.destroy();
    },
    
    testMouseUpWhileAbandoned : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      TestUtil.fakeMouseEventDOM( upload._inputElement, "mouseover" );
      assertTrue( upload.hasState( "over" ) );
      TestUtil.fakeMouseEventDOM( upload._inputElement, "mousedown" );
      assertTrue( upload.hasState( "pressed" ) );
      TestUtil.fakeMouseEventDOM( upload._inputElement, "mouseout" );
      assertFalse( upload.hasState( "over" ) );
      assertFalse( upload.hasState( "pressed" ) );
      assertTrue( upload.hasState( "abandoned" ) );
      TestUtil.fakeMouseEventDOM( document.body, "mouseup" );
      assertFalse( upload.hasState( "abandoned" ) );
      TestUtil.fakeMouseEventDOM( upload._inputElement, "mouseover" );
      assertTrue( upload.hasState( "over" ) );
      assertFalse( upload.hasState( "pressed" ) );
      upload.destroy();
    },

    testDisable : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      upload.setEnabled( false );
      TestUtil.flush();
      assertEquals( "none", upload._inputElement.style.display );
      upload.setWidth( 600 );
      upload.setEnabled( true );
      assertEquals( "", upload._inputElement.style.display );
      this._checkInputLayout( upload );
      upload.destroy(); 
    },

    testSendValueChanged : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      TestUtil.initRequestLog();
      var upload = this._createFileUpload();
      wm.add( upload, "w200", true );
      this._setFileName( upload, "foo" );
      upload._onValueChange();
      var msg = TestUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      upload.destroy(); 
    },

    testDontSendFullPathValue : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      TestUtil.initRequestLog();
      var upload = this._createFileUpload();
      wm.add( upload, "w200", true );
      this._setFileName( upload, "c:\\mypath\\foo" );
      upload._onValueChange();
      var msg = TestUtil.getRequestLog()[ 0 ];
      TestUtil.clearRequestLog();
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      this._setFileName( upload, "c:/mypath/foo" );
      upload._onValueChange();
      var msg = TestUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      upload.destroy(); 
    },

    testSubmitWithoutUrl : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertTrue( typeof upload.submit == "function" );
      var error = null;
      try {
        upload.submit( null );
      } catch( ex ) {
        error = ex;
      }
      assertNotNull( error );
      upload.destroy(); 
    },

    testSubmitWithoutValue : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertTrue( typeof upload.submit == "function" );
      var error = null;
      try {
        upload.submit( this.BLANK );
      } catch( ex ) {
        error = ex;
      }
      assertNotNull( error );
      upload.destroy(); 
    },

    // NOTE [tb] : Has been reported to sometimes fail under unkown conditions (FF 3.6 on Linux),
    //             currently no way to reproduce.
    testSubmit : [
      function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var upload = this._createFileUpload();
        this._setFileName( upload, "foo" );
        upload.submit( this.BLANK );
        TestUtil.delayTest( 600 );
        TestUtil.store( upload );
      },
      function( upload ) {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var iframe = upload._iframe;
        assertTrue( iframe.isLoaded() );
        assertTrue( iframe.queryCurrentUrl().indexOf( "blank.html" ) != -1 );
        upload.destroy(); 
      }
    ],

    /////////
    // Helper

    _createFileUploadByProtocol : function( id, parentId ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.FileUpload",
        "properties" : {
          "style" : [],
          "parent" : parentId
        }
      } );
      TestUtil.flush();
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createFileUpload : function( noFlush ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = new org.eclipse.rwt.widgets.FileUpload();
      upload.addToDocument();
      upload.setWidth( 100 );
      upload.setHeight( 30 );
      if( !noFlush ) {
        TestUtil.flush();
      }
      return upload;      
    },

    _checkInputLayout : function( upload ) {
      var input = upload._inputElement;
      var widgetWidth = upload.getBoxWidth();
      var widgetHeight = upload.getBoxHeight();
      var padding = 10;
      var inputLeft = parseInt( input.style.left );
      var inputTop = parseInt( input.style.top );
      var inputWidth = parseInt( input.offsetWidth ); 
      var inputHeight = parseInt( input.offsetHeight );
      assertEquals( -1 * padding, inputTop ); 
      assertEquals( widgetHeight + padding * 2, inputHeight ); 
      assertTrue( inputWidth > ( widgetWidth * 1.4 + padding * 2 ) ); 
      assertTrue( inputLeft < ( ( inputWidth * -0.4 ) - padding ) );      
    },

    _setFileName : function( upload, value ) {
      upload._getFileName = function(){ return value };
    }

  }

} );
