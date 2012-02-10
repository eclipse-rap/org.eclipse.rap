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

var shell;
var text;

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
      assertNotNull( text.getUserData( "selectionStart" ) );
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
      var messageLabel = text.getUserData( "messageLabel" );
      assertTrue( messageLabel instanceof qx.ui.basic.Atom );
      assertEquals( "text-field-message", messageLabel.getAppearance() );
      assertIdentical( text.getParent(), messageLabel.getParent() );
      messageLabel.destroy();
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
      var messageLabel = text.getUserData( "messageLabel" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      assertNull( messageLabel.getParent() );
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
      assertEquals( 1, text.getUserData( "selectionStart" ) );
      assertEquals( 2, text.getUserData( "selectionLength" ) );
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
      assertTrue( org.eclipse.swt.TextUtil.hasVerifyOrModifyListener( text ) );
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
      assertTrue( org.eclipse.swt.TextUtil.hasVerifyOrModifyListener( text ) );
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
        text.setBorder( new org.eclipse.rwt.Border( 1, "rounded", "black", 0 ) );
        assertEquals( "", text._style.paddingLeft );
        assertEquals( "3px", text._innerStyle.paddingLeft );
      }
    },

    testSetSelection : function() {
      createText();
      text.setValue( "asdfjkloe" );
      org.eclipse.swt.TextUtil.setSelection( text, 2, 3 );
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
    },

    testSetSelectionBeforeAppear : function() {
      createText( true );
      text.setValue( "asdfjkloe" );
      org.eclipse.swt.TextUtil.setSelection( text, 2, 3 );
      TestUtil.flush();
      text.focus();
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
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
      text.setSelectionStart( 2 );
      text.setSelectionLength( 3 );
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
      text.setPasswordMode( false );
      TestUtil.flush();
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
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
        text.setSelectionStart( 9 );
        text.__oninput( {} );
        assertEquals( "012345678", text.getValue() );
        assertEquals( "012345678", text.getComputedValue() );
        assertEquals( 1, changeLog.length );
        assertEquals( 8, text.getSelectionStart() );
        text._inputElement.value = "abcdefghiklmnopq";
        text.__oninput( {} );
        assertEquals( "abcde", text.getValue() );
        assertEquals( "abcde", text.getComputedValue() );
        assertEquals( 2, changeLog.length );
        assertEquals( 5, text.getSelectionStart() );
      }
    } ),

    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330857 
    testGetSelectionWithLineBreakAtTheEnd : function() {
      createText( true, true );
      text.setValue( "0123456789\r\n" );
      TestUtil.flush();
      text.setFocused( true );
      text.setSelectionStart( 0 );
      text.setSelectionLength( 5 );
      assertEquals( 0, text.getSelectionStart() );
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

    testFirstInputIE : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
        createText( false, true );
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
              "border" : new org.eclipse.rwt.Border( 3, "solid", "green" )
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
    
    /////////
    // Helper
    
    _setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },
    
    _tearDown : function() {
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
  org.eclipse.swt.TextUtil.initialize( text );
  text.addToDocument();
  if( noflush !== true ) {
    TestUtil.flush();
    text.focus();
  }
};

}());