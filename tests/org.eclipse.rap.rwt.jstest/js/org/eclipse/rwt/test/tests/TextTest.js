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

qx.Class.define( "org.eclipse.rwt.test.tests.TextTest", {

  extend : qx.core.Object,

  members : {

    testCreateSingleTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.Text );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_SINGLE" ) );
      assertEquals( "text-field", widget.getAppearance() );
      assertNotNull( widget.getUserData( "selectionStart" ) );
      assertEquals( "right", widget.getTextAlign() );
      assertFalse( widget.getReadOnly() );
      assertNull( widget.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testCreateMultiTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_MULTI" ) );
      assertEquals( "text-area", widget.getAppearance() );
      assertFalse( widget.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testCreateMultiTextWithWarpByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_MULTI" ) );
      assertEquals( "text-area", widget.getAppearance() );
      assertTrue( widget.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testCreatePasswordTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "PASSWORD" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_PASSWORD" ) );
      assertEquals( "text-field", widget.getAppearance() );
      assertEquals( "password", widget._inputType );
      shell.destroy();
      widget.destroy();
    },

    testSetMessageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      var messageLabel = widget.getUserData( "messageLabel" );
      assertTrue( messageLabel instanceof qx.ui.basic.Atom );
      assertEquals( "text-field-message", messageLabel.getAppearance() );
      assertIdentical( widget.getParent(), messageLabel.getParent() );
      shell.destroy();
      widget.destroy();
      messageLabel.destroy();
    },

    testSetMessageOnMultiByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertNull( widget.getUserData( "messageLabel" ) );
      shell.destroy();
      widget.destroy();
    },

    testDestroySingleTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "message" : "some text"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      var messageLabel = widget.getUserData( "messageLabel" );
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      assertNull( messageLabel.getParent() );
      assertNull( widget.getParent() );
      shell.destroy();
    },

    testSetEchoCharByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "password", widget._inputType );
      shell.destroy();
      widget.destroy();
    },

    testSetEchoCharOnMultiByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "echoChar" : "?"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._inputType !== "password" );
      shell.destroy();
      widget.destroy();
    },

    testSetEditableByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "editable" : false
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.getReadOnly() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "selection" : [ 1, 3 ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 1, widget.getUserData( "selectionStart" ) );
      assertEquals( 2, widget.getUserData( "selectionLength" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "textLimit" : 30
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 30, widget.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitOnMultiByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "textLimit" : 30
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 30, widget.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasSelectionListener() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerOnMultiByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget.hasSelectionListener() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerWithDefaultButtonByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
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
      processor.processOperation( {
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
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget.hasSelectionListener() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasModifyListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "modify" : true } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasModifyListener() );
      assertTrue( org.eclipse.swt.TextUtil.hasVerifyOrModifyListener( widget ) );
      shell.destroy();
      widget.destroy();
    },

    testSetHasVerifyListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "verify" : true } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasVerifyListener() );
      assertTrue( org.eclipse.swt.TextUtil.hasVerifyOrModifyListener( widget ) );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "text" : "foo\nbar"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "foo bar", widget.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetMultiTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "text" : "foo\nbar"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "foo\nbar", widget.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testRenderPaddingWithRoundedBorder : function() {
      if( !org.eclipse.rwt.Client.supportsCss3() ) {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var text = new org.eclipse.rwt.widgets.Text( false );
        org.eclipse.swt.TextUtil.initialize( text );
        text.setPadding( 3 );
        text.setBorder( new org.eclipse.rwt.Border( 1, "rounded", "black", 0 ) );
        text.addToDocument();
        TestUtil.flush();
        assertEquals( "", text._style.paddingLeft );
        assertEquals( "3px", text._innerStyle.paddingLeft );
        text.destroy();
      }
    },

    testSetSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setValue( "asdfjkloe" );
      text.addToDocument();
      TestUtil.flush();
      text.focus();
      org.eclipse.swt.TextUtil.setSelection( text, 2, 3 );
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
      text.destroy();
    },

    testSetSelectionBeforeAppear : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setValue( "asdfjkloe" );
      org.eclipse.swt.TextUtil.setSelection( text, 2, 3 );
      text.addToDocument();
      TestUtil.flush();
      text.focus();
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
      text.destroy();
    },

    testCreateAsTextSetPasswordMode : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
      TestUtil.flush();
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
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
    },

    testCreateAsPasswordSetTextMode : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
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
      TestUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );      
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
    },
    
    testValueSetPaswordMode : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
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
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
    },
    
    testSelectionSetPasswordMode : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
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
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
    },
    
    testCsssetPasswordMode : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
      TestUtil.flush();
      var oldCss = text._inputElement.style.cssText;
      text.setPasswordMode( true );
      TestUtil.flush();
      assertEquals( oldCss, text._inputElement.style.cssText );
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
    },
    
    testCreateTextArea : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( true );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setPasswordMode( true ); // should be ignored
      text.setWrap( false ); 
      text.addToDocument();
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
      text.setParent( null );
      text.destroy();
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
    },
    
    testTextAreaMaxLength : qx.core.Variant.select( "qx.client", {
      "mshtml|webkit" : function() {
        // NOTE: This test would fail in IE because it has a bug that sometimes
        // prevents a textFields value from being overwritten and read in the 
        // same call. In webkit it seems to fail randomly aswell.
      },
      "default" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        TestUtil.prepareTimerUse();
        var text = new org.eclipse.rwt.widgets.Text( true );
        org.eclipse.swt.TextUtil.initialize( text );
        var changeLog = [];
        text.addEventListener( "input", function(){
          changeLog.push( true );
        } );
        text.setValue( "0123456789" );
        text.addToDocument();
        TestUtil.flush();
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
        text.setParent( null );
        text.destroy();
        TestUtil.flush();
        TestUtil.clearTimerOnceLog();
      }
    } ),

    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330857 
    testGetSelectionWithLineBreakAtTheEnd : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( true );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setValue( "0123456789\r\n" );
      text.addToDocument();
      TestUtil.flush();
      text.setFocused( true );
      text.setSelectionStart( 0 );
      text.setSelectionLength( 5 );
      assertEquals( 0, text.getSelectionStart() );
      text.destroy();
    },

    testKeyPressPropagation : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = new org.eclipse.swt.widgets.Composite();
      composite.setSpace( 0, 100, 0, 100 );
      var text = new org.eclipse.rwt.widgets.Text( false );
      text.setParent( composite );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setSpace( 0, 50, 0, 21 );
      composite.addToDocument();
      TestUtil.flush();
      text.focus();
      var counter = 0;
      composite.addEventListener( "keypress", function( event ) {
        counter++;
      } );
      TestUtil.keyDown( text._getTargetNode(), "Left" );
      TestUtil.keyDown( text._getTargetNode(), "Up" );
      TestUtil.keyDown( text._getTargetNode(), "Home" );
      TestUtil.keyDown( text._getTargetNode(), "x" );
      assertEquals( 0, counter );
      text.destroy();
      composite.destroy();      
    },

    testFirstInputIE : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        TestUtil.prepareTimerUse();
        var text = new org.eclipse.rwt.widgets.Text( true );
        org.eclipse.swt.TextUtil.initialize( text );
        text.addToDocument();
        TestUtil.flush();
        assertEquals( " ", text._inputElement.value );
        TestUtil.forceTimerOnce();
        assertEquals( "", text._inputElement.value );          
        text.destroy();
      }
    } ),

    testBoxShadowAndNonRoundedBorder : qx.core.Variant.select( "qx.client", {
      "default" : function() {},
      "mshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        TestUtil.fakeAppearance( "text-field", {
          "style" : function( states ) {
            return {
              "shadow" : [ false, 0, 0, 0, 0, "red", 0 ],
              "border" : new org.eclipse.rwt.Border( 3, "solid", "green" )
            };
          }
        } );
        var text = new org.eclipse.rwt.widgets.Text( true );
        text.addToDocument();
        TestUtil.flush();
        var border = text.getBorder();
        assertEquals( 3, border.getWidthTop() );
        assertEquals( "rounded", border.getStyle() );
        assertEquals( "green", border.getColor() );
        assertEquals( [ 0, 0, 0, 0 ], border.getRadii() );
        TestUtil.restoreAppearance();
        text.destroy();
      }
    } )

  }
  
} );