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

    BLANK : "../org.eclipse.rap.rwt.q07/resources/resource/static/html/blank.html",
        
    testCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertTrue( upload instanceof org.eclipse.rwt.widgets.FileUpload );
      assertTrue( upload.isInDom() );
      upload.destroy();
    },
  
    testDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var iframe = upload._iframe;
      upload.destroy();
      testUtil.flush();
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.setText( "Hello World!" );
      testUtil.flush();
      assertEquals( "Hello World!", upload.getCellNode( 2 ).innerHTML );
      upload.destroy();
    },
    
    testImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.setImage( "test.jpg" );
      testUtil.flush();
      assertTrue(
        testUtil.getCssBackgroundImage( upload.getCellNode( 1 ) ).search( "test.jpg" ) != -1 
      );
      upload.destroy();
    },

    testHasFormElements : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var form = upload._getTargetNode().lastChild;
      assertEquals( "form", form.tagName.toLowerCase() );
      assertEquals( "input", form.firstChild.tagName.toLowerCase() );
      assertIdentical( form, upload._formElement );
      assertIdentical( form.firstChild, upload._inputElement );
      upload.destroy();
    },

    testKeepFormElementsWhenAddingContent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      assertEquals( 1, upload._getTargetNode().childNodes.length );
      var originalForm = upload._formElement;
      var originalInput = upload._inputElement;
      upload.setText( "bla" );
      testUtil.flush();
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var target = upload._formElement.getAttribute( "target" );
      var iframe = upload._iframe.getIframeNode();
      assertEquals( target, iframe.getAttribute( "name" ) ); 
      upload.destroy();
    },

    testIframeProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var iframe = upload._iframe;
      assertEquals( testUtil.getDocument(), iframe.getParent() );
      assertEquals( 0, iframe.getHeight() );
      assertEquals( 0, iframe.getWidth() );
      assertFalse( iframe.getVisibility() );
      upload.destroy();
    },

    testOpacity : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      var input = upload._inputElement;
      testUtil.hasElementOpacity( input );
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

    testCursor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      upload.setCursor( "pointer" );
      testUtil.flush();
      assertEquals( "", upload.getElement().style.cursor );
      assertEquals( "pointer", upload._inputElement.style.cursor );
      upload.setCursor( "default" );
      assertEquals( "", upload.getElement().style.cursor );
      assertEquals( "default", upload._inputElement.style.cursor );
      upload.destroy(); 
    },

    testMouseDown : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      testUtil.fakeMouseEventDOM( upload.getElement(), "mousedown" );
      assertFalse( upload.hasState( "pressed" ) );
      testUtil.fakeMouseEventDOM( upload._inputElement, "mousedown" );
      assertTrue( upload.hasState( "pressed" ) );
      testUtil.fakeMouseEventDOM( upload.getElement(), "mouseup" );
      assertFalse( upload.hasState( "over" ) );
      upload.destroy(); 
    },

    testMouseOver : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      testUtil.hoverFromTo( document.body, upload.getElement() );
      assertFalse( upload.hasState( "over" ) );
      testUtil.hoverFromTo( upload.getElement(), upload._inputElement );
      assertTrue( upload.hasState( "over" ) );
      upload.destroy(); 
    },

    testDontInterfereWithKeyEvents : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      upload.focus();
      testUtil.keyDown( upload.getElement(), "Space" );
      assertFalse( upload.hasState( "pressed" ) );
      upload.addState( "pressed" );
      testUtil.keyUp( upload.getElement(), "Space" );
      assertTrue( upload.hasState( "pressed" ) );
      upload.destroy(); 
    },

    testShowFocusIndicator : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      testUtil.click( upload );
      assertEquals( !isChrome, focusIndicator._frame.parentNode === node );
      upload.focus();
      upload._ontabfocus();
      assertTrue( focusIndicator._frame.parentNode === node );
      upload.destroy();
    },
    
    testMouseUpWhileAbandoned : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload();
      testUtil.fakeMouseEventDOM( upload._inputElement, "mouseover" );
      assertTrue( upload.hasState( "over" ) );
      testUtil.fakeMouseEventDOM( upload._inputElement, "mousedown" );
      assertTrue( upload.hasState( "pressed" ) );
      testUtil.fakeMouseEventDOM( upload._inputElement, "mouseout" );
      assertFalse( upload.hasState( "over" ) );
      assertFalse( upload.hasState( "pressed" ) );
      assertTrue( upload.hasState( "abandoned" ) );
      testUtil.fakeMouseEventDOM( document.body, "mouseup" );
      assertFalse( upload.hasState( "abandoned" ) );
      testUtil.fakeMouseEventDOM( upload._inputElement, "mouseover" );
      assertTrue( upload.hasState( "over" ) );
      assertFalse( upload.hasState( "pressed" ) );
      upload.destroy();
    },

    testDisable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = this._createFileUpload( true );
      upload.setEnabled( false );
      testUtil.flush();
      assertEquals( "none", upload._inputElement.style.display );
      upload.setWidth( 600 );
      upload.setEnabled( true );
      assertEquals( "", upload._inputElement.style.display );
      this._checkInputLayout( upload );
      upload.destroy(); 
    },

    testSendValueChanged : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      testUtil.initRequestLog();
      var upload = this._createFileUpload();
      wm.add( upload, "w200", true );
      this._setFileName( upload, "foo" );
      upload._onValueChange();
      var msg = testUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      upload.destroy(); 
    },

    testDontSendFullPathValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      testUtil.initRequestLog();
      var upload = this._createFileUpload();
      wm.add( upload, "w200", true );
      this._setFileName( upload, "c:\\mypath\\foo" );
      upload._onValueChange();
      var msg = testUtil.getRequestLog()[ 0 ];
      testUtil.clearRequestLog();
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      this._setFileName( upload, "c:/mypath/foo" );
      upload._onValueChange();
      var msg = testUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( "w200.fileName=foo" ) !== -1 );
      upload.destroy(); 
    },

    testSubmitWithoutUrl : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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

// TODO [rst] Fix and re-enable
//    testSubmit : [
//      function() {
//        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//        var upload = this._createFileUpload();
//        this._setFileName( upload, "foo" );
//        upload.submit( this.BLANK );
//        testUtil.delayTest( 600 );
//        testUtil.store( upload );
//      },
//      function( upload ) {
//        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//        var iframe = upload._iframe;
//        assertTrue( iframe.isLoaded() );
//        assertTrue( iframe.queryCurrentUrl().indexOf( "blank.html" ) != -1 );
//        upload.destroy(); 
//      }
//    ],

    /////////
    // Helper

    _createFileUpload : function( noFlush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var upload = new org.eclipse.rwt.widgets.FileUpload();
      upload.addToDocument();
      upload.setWidth( 100 );
      upload.setHeight( 30 );
      if( !noFlush ) {
        testUtil.flush();
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
