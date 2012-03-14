/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){
  
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = org.eclipse.rwt.protocol.Processor;
var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
var Font = qx.ui.core.Font;
var Border = org.eclipse.rwt.Border;
var Client = org.eclipse.rwt.Client;
var Request = org.eclipse.swt.Request;

var shell;
var text;
var log;

qx.Class.define( "org.eclipse.rwt.test.tests.TextTest", {

  extend : qx.core.Object,

  members : {

    testCreateSingleTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text instanceof org.eclipse.rwt.widgets.Text );
      assertIdentical( shell, text.getParent() );
      assertTrue( text.getUserData( "isControl") );
      assertTrue( text.hasState( "rwt_SINGLE" ) );
      assertEquals( "text-field", text.getAppearance() );
      assertEquals( "right", text.getTextAlign() );
      assertFalse( text.getReadOnly() );
      assertNull( text.getMaxLength() );
    },

    testCreateMultiTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasState( "rwt_MULTI" ) );
      assertEquals( "text-area", text.getAppearance() );
      assertFalse( text.getWrap() );
    },

    testCreateMultiTextWithWarpByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI", "WRAP" ],
          "parent" : "w2"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasState( "rwt_MULTI" ) );
      assertEquals( "text-area", text.getAppearance() );
      assertTrue( text.getWrap() );
    },

    testCreatePasswordTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "PASSWORD" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasState( "rwt_PASSWORD" ) );
      assertEquals( "text-field", text.getAppearance() );
      assertEquals( "password", text._inputType );
    },

    testSetMessageByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( "some text", text.getMessage() );
    },

    testSetMessageOnMultiByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertNull( text.getUserData( "messageLabel" ) );
    },

    testDestroySingleTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      text = ObjectManager.getObject( "w3" );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );

      assertNull( text.getParent() );
    },

    testSetEchoCharByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( "password", text._inputType );
    },

    testSetEchoCharOnMultiByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text._inputType !== "password" );
    },

    testSetEditableByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "editable" : false
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.getReadOnly() );
    },

    testSetSelectionByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "selection" : [ 1, 3 ]
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( 1, text._selectionStart );
      assertEquals( 2, text._selectionLength );
    },

    testSetTextLimitByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "textLimit" : 30
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( 30, text.getMaxLength() );
    },

    testSetTextLimitOnMultiByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "textLimit" : 30
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( 30, text.getMaxLength() );
    },

    testSetHasSelectionListenerByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasSelectionListener() );
    },

    testSetHasSelectionListenerOnMultiByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      text = ObjectManager.getObject( "w3" );
      assertFalse( text.hasSelectionListener() );
    },

    testSetHasSelectionListenerWithDefaultButtonByProtocol : function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var defaultButton = ObjectManager.getObject( "w4" );
      shell.setDefaultButton( defaultButton );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      TestUtil.flush();
      text = ObjectManager.getObject( "w3" );
      assertFalse( text.hasSelectionListener() );
    },

    testSetHasModifyListenerByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "modify" : true } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasModifyListener() );
    },

    testSetHasVerifyListenerByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "verify" : true } );
      text = ObjectManager.getObject( "w3" );
      assertTrue( text.hasVerifyListener() );
    },

    testSetTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "text" : "foo\nbar"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( "foo bar", text.getValue() );
    },

    testSetMultiTextByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "text" : "foo\nbar"
        }
      } );
      text = ObjectManager.getObject( "w3" );
      assertEquals( "foo\nbar", text.getValue() );
    },

    testRenderPaddingWithRoundedBorder : function() {
      if( !org.eclipse.rwt.Client.supportsCss3() ) {
        createText();
        text.setPadding( 3 );
        text.setBorder( new Border( 1, "rounded", "black", 0 ) );
        TestUtil.flush();
        assertEquals( "", text._style.paddingLeft );
        assertEquals( "3px", text._innerStyle.paddingLeft );
      }
    },

    testInitialSelection : function() {
      createText();

      assertEquals( [ 0, 0 ], text.getSelection() );
      assertEquals( [ 0, 0 ], text.getComputedSelection() );
    },

    testSetSelection : function() {
      createText();
      text.setValue( "asdfjkloe" );

      text.setSelection( [ 2, 5 ] );

      assertEquals( [ 2, 5 ], text.getSelection() );
      assertEquals( [ 2, 5 ], text.getComputedSelection() );
    },

    testRestoreSelectionOnTabFocus : function() {
      createText();
      text.setValue( "asdfjkloe" );
      text.setSelection( [ 2, 5 ] );
      text.blur();
      
      text.focus();
      text._ontabfocus();
      TestUtil.keyUp( text, "Tab" );

      assertEquals( [ 2, 5 ], text.getSelection() );
      assertEquals( [ 2, 5 ], text.getComputedSelection() );
    },

    testSetSelectionBeforeAppear : function() {
      createText( true );
      text.setValue( "asdfjkloe" );

      text.setSelection( [ 2, 5 ] );
      assertEquals( [ 2, 5 ], text.getSelection() );
      TestUtil.flush();
      text.focus();
      
      if( !Client.isGecko() ) {
        // Fails in gecko about half of the time for no known reason, but works in actuality
        assertEquals( [ 2, 5 ], text.getComputedSelection() );
      }
    },

    testSetEmptySelectionBeforeAppear : function() {
      createText( true );
      text.setValue( "asdfjkloe" );

      text.setSelection( [ 2, 2 ] );
      assertEquals( [ 2, 2 ], text.getSelection() );
      TestUtil.flush();
      text.focus();

      if( !Client.isGecko() ) {
        // Fails in gecko about half of the time for no known reason, but works in actuality
        assertEquals( [ 2, 2 ], text.getComputedSelection() );
      }
    },

    testCreateAsTextSetPasswordMode : function() {
      createText();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      TestUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      TestUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      TestUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
    },

    testCreateAsPasswordSetTextMode : function() {
      createText( true );
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      TestUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      TestUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      TestUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );      
    },
    
    testValueSetPaswordMode : function() {
      createText( true );
      text.setPasswordMode( true );
      text.setValue( "asdf" );
      TestUtil.flush();
      assertEquals( "asdf", text.getValue() );
      assertEquals( "asdf", text.getComputedValue() );
      assertEquals( "asdf", text._inputElement.value );
      text.setPasswordMode( false );
      TestUtil.flush();
      assertEquals( "asdf", text.getValue() );
      assertEquals( "asdf", text.getComputedValue() );
      assertEquals( "asdf", text._inputElement.value );
    },

    testSelectionSetPasswordMode : function() {
      createText( true );
      text.setValue( "asdfjkloe" );
      text.addToDocument();
      TestUtil.flush();
      text.focus();
      text._setSelectionStart( 2 );
      text._setSelectionLength( 3 );
      assertEquals( [ 2, 5 ], text.getComputedSelection() );
      text.setPasswordMode( false );
      TestUtil.flush();
      assertEquals( [ 2, 5 ], text.getComputedSelection() );
    },
    
    testCssSetPasswordMode : function() {
      createText();
      var oldCss = text._inputElement.style.cssText;
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( oldCss, text._inputElement.style.cssText );
    },

    testCreateTextArea : function() {
      createText( true, true );
      text.setPasswordMode( true ); // should be ignored
      text.setWrap( false ); 
      TestUtil.flush();
      assertNull( text._inputType );
      assertEquals( "textarea", text._inputTag );
      assertEquals( "textarea", text._inputElement.tagName.toLowerCase() );
      assertEquals( "text-area", text.getAppearance() );
      text.setWrap( true );
      var wrapProperty = "";
      var wrapAttribute = "";
      try {
        wrapProperty = text._inputElement.wrap;
      } catch( ex ) {}
      try {
        wrapAttribute = text._inputElement.getAttribute( "wrap" );
      } catch( ex ) {}
      assertTrue( wrapProperty == "soft" || wrapAttribute == "soft" );
    },

    testTextAreaMaxLength : qx.core.Variant.select( "qx.client", {
      "mshtml|webkit" : function() {
        // NOTE: This test would fail in IE because it has a bug that sometimes
        // prevents a textFields value from being overwritten and read in the 
        // same call. In webkit it seems to fail randomly aswell.
      },
      "default" : function() {
        createText( false, true );
        var changeLog = [];
        text.addEventListener( "input", function(){
          changeLog.push( true );
        } );
        text.setValue( "0123456789" );
        assertEquals( "0123456789", text.getValue() );
        assertEquals( "0123456789", text.getComputedValue() );
        text.setMaxLength( 5 );
        assertEquals( "0123456789", text.getValue() );
        assertEquals( "0123456789", text.getComputedValue() );
        assertEquals( 0, changeLog.length );
        text._inputElement.value = "012345678";
        text.__oninput( {} );
        assertEquals( "012345678", text.getValue() );
        assertEquals( "012345678", text.getComputedValue() );
        assertEquals( 1, changeLog.length );
        text._inputElement.value = "01234567x8";
        text._setSelectionStart( 9 );
        text.__oninput( {} );
        assertEquals( "012345678", text.getValue() );
        assertEquals( "012345678", text.getComputedValue() );
        assertEquals( 1, changeLog.length );
        assertEquals( 8, text._getSelectionStart() );
        text._inputElement.value = "abcdefghiklmnopq";
        text.__oninput( {} );
        assertEquals( "abcde", text.getValue() );
        assertEquals( "abcde", text.getComputedValue() );
        assertEquals( 2, changeLog.length );
        assertEquals( 5, text._getSelectionStart() );
      }
    } ),

    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330857 
    // NOTE [tb] : This test seems to fail at random in IE9?
    testGetSelectionWithLineBreakAtTheEnd : function() {
      createText( true, true );
      text.setValue( "0123456789\r\n" );
      TestUtil.flush();
      text.setFocused( true );
      text._setSelectionStart( 0 );
      text._setSelectionLength( 5 );
      assertEquals( 0, text._getSelectionStart() );
    },

    testKeyPressPropagation : function() {
      shell.setSpace( 0, 100, 0, 100 );
      createText();
      text.setSpace( 0, 50, 0, 21 );
      TestUtil.flush();
      var counter = 0;
      shell.addEventListener( "keypress", function( event ) {
        counter++;
      } );
      TestUtil.keyDown( text._getTargetNode(), "Left" );
      TestUtil.keyDown( text._getTargetNode(), "Up" );
      TestUtil.keyDown( text._getTargetNode(), "Home" );
      TestUtil.keyDown( text._getTargetNode(), "x" );
      assertEquals( 0, counter );
      shell.destroy();      
    },
    
    testFiresChangeReadOnlyEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text = new org.eclipse.rwt.widgets.Text( false );
      text.addToDocument();
      TestUtil.flush();
      var log = 0;
      text.addEventListener( "changeReadOnly", function(){
        log++;
      } );
      
      text.setReadOnly( true );
            
      assertTrue( log > 0 );
      text.destroy();
    },

    testFirstInputIE : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
        createText( true, true );
        TestUtil.flush();
        assertEquals( " ", text._inputElement.value );
        TestUtil.forceTimerOnce();
        assertEquals( "", text._inputElement.value );          
        }
    } ),

    testBoxShadowAndNonRoundedBorder : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
          TestUtil.fakeAppearance( "text-field", {
          "style" : function( states ) {
            return {
              "shadow" : [ false, 0, 0, 0, 0, "red", 0 ],
              "border" : new Border( 3, "solid", "green" )
            };
          }
        } );
        createText();
        var border = text.getBorder();
        assertEquals( 3, border.getWidthTop() );
        assertEquals( "rounded", border.getStyle() );
        assertEquals( "green", border.getColor() );
        assertEquals( [ 0, 0, 0, 0 ], border.getRadii() );
        TestUtil.restoreAppearance();
      }
    } ),

    testSetFontLineHeight : function() {
      createText();

      text.setFont( new Font( 10 ) );

      assertEquals( 12, parseInt( text.getInputElement().style.lineHeight ) );
    },

    testSetFontBeforeCreateLineHeight : function() {
      createText( true );

      text.setFont( new Font( 10 ) );
      TestUtil.flush();

      assertEquals( 12, parseInt( text.getInputElement().style.lineHeight ) );
    },

    testLiveUpdate : function() {
      createText();
      createChangeLogger();
      
      typeCharacter( "A" );
      
      assertEquals( [ "A" ], log );
    },

    testSendSelectionChangeOnMouseDown : function() {
      createText();
      text.setValue( "foobar" );

      TestUtil.fakeMouseEvent( text, "mousedown" );
      setSelection( [ 3, 3 ] );
      TestUtil.fakeMouseEvent( text, "mouseup" );
      Request.getInstance().send();   
      
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionStart=3" ) !== -1 );
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionLength=0" ) !== -1 );
    },

//    TODO [tb] : activate when fixed
//
//    testSendSelectionChangeOnMouseMoveOut : function() {
//      createText();
//      text.setValue( "foobar" );
//      
//      TestUtil.fakeMouseEvent( text, "mousedown" );
//      setSelection( [ 3, 3 ] );
//      TestUtil.fakeMouseEvent( text, "mouseout" );
//      TestUtil.fakeMouseEvent( shell, "mouseup" );
//      Request.getInstance().send();   
//      
//      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionStart=3" ) !== -1 );
//      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionLength=0" ) !== -1 );
//    },
//
    testSendSelectionChangeOnKeyPress : function() {
      createText();
      text.setValue( "foobar" );
      
      TestUtil.keyDown( text, "Right" );
      setSelection( [ 3, 3 ] );
      TestUtil.keyDown( text, "Enter" );
      Request.getInstance().send();   
      
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionStart=3" ) !== -1 );
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionLength=0" ) !== -1 );
    },

    testSendSelectionChangeOnTwoKeyPress : function() {
      createText();
      text.setValue( "foobar" );
      
      TestUtil.keyDown( text, "Right" );
      setSelection( [ 3, 3 ] );
      TestUtil.keyDown( text, "Enter" ); // can send a request without releasing Right
      Request.getInstance().send();   
      
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionStart=3" ) !== -1 );
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionLength=0" ) !== -1 );
    },

    testSendSelectionChangeOnProgrammaticValueChange : function() {
      createText();
      text.setValue( "foobar" );
      setSelection( [ 3, 3 ] );
      
      text.setValue( "f" );
      Request.getInstance().send();   
      
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionStart=1" ) !== -1 );
      assertTrue( TestUtil.getMessage().indexOf( "w3.selectionLength=0" ) !== -1 );
    },

    testSendTextChange : function() {
      createText();

      text.setValue( "foobar" );
      Request.getInstance().send();   
      
      assertTrue( TestUtil.getMessage().indexOf( "w3.text=foobar" ) !== -1 );
    },

    testSendTextModifyEventWithModifyListener : function() {
      createText();
      text.setHasModifyListener( true );

      text.setValue( "foobar" );
      TestUtil.forceTimerOnce();
      
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.modifyText=w3" ) !== -1 );
    },

    testSendTextModifyEventWithVerifyListener : function() {
      createText();
      text.setHasVerifyListener( true );

      text.setValue( "foobar" );
      TestUtil.forceTimerOnce();
      
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.modifyText=w3" ) !== -1 );
    },

    testSendNoModifyEvent : function() {
      createText();

      text.setValue( "foobar" );
      TestUtil.forceTimerOnce();

      assertEquals( 0, TestUtil.getRequestsSend() );      
      Request.getInstance().send();   
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.modifyText" ) === -1 );
    },

    testDontSendTextModifyEventTwice : function() {
      createText();
      text.setHasModifyListener( true );

      text.setValue( "foobar" );
      text.setValue( "barfoo" );
      TestUtil.forceTimerOnce();

      assertEquals( 1, TestUtil.getRequestsSend() );      
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.modifyText=w3" ) !== -1 );
      assertTrue( TestUtil.getMessage().indexOf( "w3.text=barfoo" ) !== -1 );
    },

    testSetMessageCreatesLabel : function() {
      createText();

      text.setMessage( "konnichiwa" );

      var element = text._getTargetNode().firstChild;
      assertEquals( "konnichiwa", element.innerHTML );
    },

    testSetMessageOnMulti : function() {
      createText( false, true );

      text.setMessage( "konnichiwa" );

      assertEquals( 1, text._getTargetNode().childNodes.length );
    },

    testSetMessageBeforeCreate : function() {
      createText( true );

      text.setMessage( "konnichiwa" );
      TestUtil.flush();

      var element = text._getTargetNode().firstChild;
      assertEquals( "konnichiwa", element.innerHTML );
    },

    testSetMessageTwice : function() {
      createText();

      text.setMessage( "konnichiwa" );
      var element = text._getTargetNode().firstChild;
      text.setMessage( "arigatto" );

      var elementAgain = text._getTargetNode().firstChild;
      assertIdentical( element, elementAgain );
      assertEquals( "arigatto", element.innerHTML );
    },

    testSetMessageToNull : function() {
      createText();

      text.setMessage( "konnichiwa" );
      var element = text._getTargetNode().firstChild;
      text.setMessage( null );

      var elementAgain = text._getTargetNode().firstChild;
      assertIdentical( element, elementAgain );
      assertEquals( "", element.innerHTML );
    },

    testMessageLabelDefaultProperties : function() {
      createText();

      text.setMessage( "konnichiwa" );

      var style = text._getTargetNode().firstChild.style;
      assertEquals( "absolute", style.position );
      if( !Client.isMshtml() && !Client.isNewMshtml() ) {
        assertTrue( style.outline.indexOf( "none" ) !== -1 );
      }
    },

    testMessageLabelSetCursor : function() {
      createText();
      text.setMessage( "bla" );

      text.setCursor( "wait" );

      var style = text._getTargetNode().firstChild.style;
      assertEquals( "wait", style.cursor );
    },

    testMessageLabelSetCursorBeforeCreate : function() {
      createText( true );
      text.setMessage( "bla" );

      text.setCursor( "wait" );
      TestUtil.flush();

      var style = text._getTargetNode().firstChild.style;
      assertEquals( "wait", style.cursor );
    },

    testMessageLabelThemingProperties : function() {
      createText( true );
      text.setMessage( "konnichiwa" );
      text.setWidth( 100 );
      text.setHeight( 30 );

      TestUtil.fakeAppearance( "text-field-message", {
        "style" : function( states ) {
          var result = {};
          result.font = new Font( 10 );
          result.textColor = "red";
          result.paddingRight = 4;
          result.paddingLeft = 3;
          result.textShadow = [ false, 0, 3, 0, 0, "red", 0 ];
          return result;
        }      
      } );
      TestUtil.flush();

      var style = text._getTargetNode().firstChild.style;
      assertEquals( "10px", style.fontSize );
      assertEquals( "red", style.color );
      if( Client.isGecko() || Client.isWebkit() ) {
        assertTrue( style.textShadow.indexOf( "3px" ) != -1 );
      }
      assertEquals( "3px", style.left );
      assertEquals( "93px", style.width );
      assertEquals( "9px", style.top );
      assertEquals( "12px", style.height );
    },

    testMessageLabelResize : function() {
      createText( true );
      text.setMessage( "konnichiwa" );
      text.setWidth( 100 );
      text.setHeight( 30 );
      TestUtil.fakeAppearance( "text-field-message", {
        "style" : function( states ) {
          var result = {};
          result.font = new Font( 10 );
          result.textColor = "red";
          result.paddingRight = 4;
          result.paddingLeft = 3;
          result.textShadow = [ false, 0, 3, 0, 0, "red", 0 ];
          return result;
        }      
      } );
      TestUtil.flush();

      text.setWidth( 120 );
      text.setHeight( 50 );
      TestUtil.flush();

      var style = text._getTargetNode().firstChild.style;
      assertEquals( "113px", style.width );
      assertEquals( "19px", style.top );
    },

    testMessageAppearsOnBlur : function() {
      createText();
      text.setMessage( "xxx" );
      text.setValue( "" );
      var element = text._getTargetNode().firstChild;
      assertEquals( "none", element.style.display );

      text.blur();

      assertEquals( "", element.style.display );
    },

    testMessageAppearsOnEmptyText : function() {
      createText();
      text.setMessage( "xxx" );
      text.setValue( "foo" );
      var element = text._getTargetNode().firstChild;
      text.blur();
      assertEquals( "none", element.style.display );

      text.setValue( "" );

      assertEquals( "", element.style.display );
    },

    testMessageDisappearsOnFocus : function() {
      createText();
      text.setMessage( "xxx" );
      text.setValue( "" );
      var element = text._getTargetNode().firstChild;
      text.blur();
      assertEquals( "", element.style.display );

      text.focus();

      assertEquals( "none", element.style.display );
    },

    testMessageDisappearsOnSetText : function() {
      createText();
      text.setMessage( "xxx" );
      text.setValue( "" );
      var element = text._getTargetNode().firstChild;
      text.blur();
      assertEquals( "", element.style.display );

      text.setValue( "foo" );

      assertEquals( "none", element.style.display );
    },

    testDisposeText : function() {
      createText();
      text.setMessage( "xxx" );

      text.destroy();
      TestUtil.flush();

      assertTrue( TestUtil.hasNoObjects( text, true ) );
      text = null;
    },

    testInputEvent: function() {
      createText();
      text.setValue( "c" );
      var insert;
      text.addEventListener( "input", function( event ) {
        insert = event.getData();
      }, this );
      
      typeCharacter( "A" );
      
      assertEquals( "A", insert );
    },

    testPreventUpdate : function() {
      createText();
      text.setValue( "c" );
      text.addEventListener( "input", function( event ) {
        event.preventDefault();
      }, this );
      
      typeCharacter( "A" );
      
      assertEquals( "c", text.getValue() );
      assertEquals( "cA", text.getComputedValue() );
    },

    testManualUpdate : function() {
      createText();
      text.setValue( "123456789" );
      text.setSelection( [ 9, 9 ] );
      
      text.addEventListener( "input", function( event ) {
        text.setValue( "987654321" );
        text.setSelection( [ 2, 5 ] );
        event.preventDefault();
      } );
      typeCharacter( "0" );
      TestUtil.forceTimerOnce();
      
      assertEquals( "987654321", text.getValue() );
      assertEquals( "987654321", text.getComputedValue() );
      assertEquals( [ 2, 5 ], text.getSelection() );
      assertEquals( [ 2, 5 ], text.getComputedSelection() );
    },

    testMissedDeleteInputEvent  : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "newmshtml" : function() {
        createText();
        text.setValue( "foobar" );
        var log = [];
        text.addEventListener( "input", function( event ) {
          log.push( event );
        }, this );
        
        TestUtil.keyDown( text, "Delete" );
        text.getInputElement().value = "fooba";
        TestUtil.keyUp( text, "Delete" );
        TestUtil.forceInterval( text._checkTimer );
        
        assertEquals( "fooba", text.getValue() );
        assertEquals( "fooba", text.getComputedValue() );
        assertEquals( 1, log.length );
      }
    } ),

    testMissedInputEventCatchOnBlur  : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "newmshtml" : function() {
        createText();
        text.setValue( "foobar" );
        var log = [];
        text.addEventListener( "input", function( event ) {
          log.push( event );
        }, this );
        
        text.getInputElement().value = "fooba";
        text.blur();
        
        assertEquals( "fooba", text.getValue() );
        assertEquals( "fooba", text.getComputedValue() );
        assertEquals( 1, log.length );
      }
    } ),

    /////////
    // Helper

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.clearRequestLog();
      log = [];
    },

    tearDown : function() {
      TestUtil.clearTimerOnceLog();
      shell.destroy();
      if( text ) {
        text.destroy();
      }
      text = null;
    }

  }
  
} );

var createText = function( noflush, arg ) {
  text = new org.eclipse.rwt.widgets.Text( arg ? arg : false );
  ObjectManager.add( "w3", text, null );
  text.setParent( shell );
  if( noflush !== true ) {
    TestUtil.flush();
    TestUtil.forceTimerOnce(); // apply first input fix in IE
    text.focus();
    TestUtil.clearRequestLog();
  }
};

var createChangeLogger = function() {
  var logger = function( event ) {
    log.push( event.getTarget().getValue() );
  };
  text.addEventListener( "changeValue", logger );
};

var typeCharacter = function( character ) {
  TestUtil.keyDown( text, character );
  // we will assume that the carret is at the end
  var newValue = text.getInputElement().value + character;
  text._inValueProperty = true;
  text.getInputElement().value = newValue;
  text._inValueProperty = false;
  if( Client.isWebkit() ) {
    text._setSelectionStart( newValue.length - character.length );
    text._oninputDom( { "propertyName" : "value" } );
    text._setSelectionStart( newValue.length );
  } else {
    text._setSelectionStart( newValue.length );
    text._oninputDom( { "propertyName" : "value" } );
  }
  TestUtil.keyUp( text, character );
};

var setSelection = function( selection ) {
  text._setSelectionStart( selection[ 0 ] );
  text._setSelectionLength( selection[ 1 ] - selection[ 0 ] );
};

}());