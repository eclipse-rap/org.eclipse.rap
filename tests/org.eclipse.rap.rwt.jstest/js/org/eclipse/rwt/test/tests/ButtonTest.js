/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ButtonTest", {

  extend : rwt.qx.Object,

  members : {

    testCreatePushButtonByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Button );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertEquals( "push-button", widget.getAppearance() );
      assertEquals( -1, widget.getFlexibleCell() );
      assertFalse( widget.getWordWrap() );
      assertFalse( widget._markupEnabled );
      shell.destroy();
    },

    testCreateToggleButtonByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "TOGGLE" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_TOGGLE" ) );
      assertEquals( "push-button", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateCheckButtonByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "CHECK" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_CHECK" ) );
      assertEquals( "check-box", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateRadioButtonByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "RADIO" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_RADIO" ) );
      assertEquals( "radio-button", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testSetNoRadioGroupByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.addState( "rwt_NO_RADIO_GROUP" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "RADIO" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.getNoRadioGroup() );
      shell.destroy();
      widget.destroy();
    },

    testSetWrapByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 2, widget.getFlexibleCell() );
      assertTrue( widget.getWordWrap() );
      shell.destroy();
      widget.destroy();
    },

    testSetMarkupEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "text" : "text\n & \"text"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "text\n &amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "text" : "asdfjkloeqwerty",
          "mnemonicIndex" : 6
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 6, widget.getMnemonicIndex() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "text" : "asdfjkloeqwerty",
          "mnemonicIndex" : 6
        }
      } );
      TestUtil.protocolSet( "w3", { "text" : "blue" } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertNull( widget.getMnemonicIndex() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocolWithWrap : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH", "WRAP" ],
          "parent" : "w2",
          "text" : "text\n && \"text"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "text<br/> &amp;&amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var Processor = rwt.remote.MessageProcessor;
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "alignment" : "right"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "right", widget.getHorizontalChildrenAlign() );
      assertEquals( "right", widget._alignment );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol_Arrow : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "ARROW" ],
          "parent" : "w2",
          "alignment" : "down"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_DOWN" ) );
      assertEquals( "down", widget._alignment );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "image" : [ "image.png", 10, 20 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "image.png", widget.getCellContent( 1 ) );
      assertEquals( [ 10, 20 ], widget.getCellDimension( 1 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "TOGGLE" ],
          "parent" : "w2",
          "selection" : true
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._selected );
      shell.destroy();
      widget.destroy();
    },

    testSetGrayedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "CHECK" ],
          "parent" : "w2",
          "grayed" : true
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "grayed" ) );
      shell.destroy();
      widget.destroy();
    },

    testFocusIndicatorPush : function() {
      var hasFocusIndicator = function( widget ) {
        var node = widget._getTargetNode();
        var result = false;
        for( var i = 0; i < node.childNodes.length; i++ ) {
          if( node.childNodes[ i ].getAttribute( "id" ) == "focusIndicator" ) {
            result = true;
          }
        }
        return result;
      };
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setText( "bla" );
      button.addToDocument();
      TestUtil.flush();
      assertFalse( button.hasState( "focus" ) );
      assertFalse( hasFocusIndicator( button ) );
      button.focus();
      TestUtil.flush();
      assertTrue( hasFocusIndicator( button ) );
      button.setImage( "test.jpg" );
      TestUtil.flush();
      assertTrue( hasFocusIndicator( button ) );
      button.blur();
      TestUtil.flush();
      assertFalse( hasFocusIndicator( button ) );
      button.destroy();
      TestUtil.flush();
    },

    testFocusIndicatorCheck : function() {
      var hasFocusIndicator = function( widget ) {
        var node = widget._getTargetNode();
        var result = false;
        for( var i = 0; i < node.childNodes.length; i++ ) {
          if( node.childNodes[ i ].getAttribute( "id") == "focusIndicator" ) {
            result = true;
          }
        }
        return result;
      };
      var button = new rwt.widgets.Button( "check" );
      button.addState( "rwt_CHECK" );
      button.setText( "bla" );
      button.addToDocument();
      TestUtil.flush();
      assertFalse( button.hasState( "focus" ) );
      assertFalse( hasFocusIndicator( button ) );
      button.focus();
      TestUtil.flush();
      assertTrue( hasFocusIndicator( button ) );
      button.setImage( "test.jpg" );
      TestUtil.flush();
      assertTrue( hasFocusIndicator( button ) );
      button.blur();
      TestUtil.flush();
      assertFalse( hasFocusIndicator( button ) );
      button.destroy();
      TestUtil.flush();
    },

    testParent : function() {
      var button = new rwt.widgets.Button( "push" );
      button.setText( "Hello World!" );
      button.setImage( "url.jpg" );
      button.addToDocument();
      TestUtil.flush();
      assertIdentical( button._getTargetNode(), button.getCellNode( 1 ).parentNode );
      assertIdentical( button._getTargetNode(), button.getCellNode( 2 ).parentNode );
      button.setParent( null );
      button.destroy();
      TestUtil.flush();
    },

    testText : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();

      button.setText( "Hello World!" );
      TestUtil.flush();

      assertEquals( "Hello World!", button.getCellNode( 2 ).innerHTML );
      button.destroy();
    },

    testText_isEscaped : function() {
      var button = new rwt.widgets.Button( "push" );
      button.setMarkupEnabled( false );
      button.addToDocument();

      button.setText( "<b>foo</b> bar" );
      TestUtil.flush();

      assertEquals( "&lt;b&gt;foo&lt;/b&gt; bar", button.getCellNode( 2 ).innerHTML );
      button.destroy();
    },

    testText_withMarkupEnabled_isNotEscaped : function() {
      var button = new rwt.widgets.Button( "push" );
      button.setMarkupEnabled( true );
      button.addToDocument();

      button.setText( "<b>foo</b> bar" );
      TestUtil.flush();

      assertEquals( "<b>foo</b> bar", button.getCellNode( 2 ).innerHTML );
      button.destroy();
    },

    testImage : function() {
      var button = new rwt.widgets.Button( "push" );
      button.setImage( "test.jpg" );
      button.addToDocument();
      TestUtil.flush();
      assertTrue(
        TestUtil.getCssBackgroundImage( button.getCellNode( 1 ) ).search( "test.jpg" ) != -1
      );
      button.setParent( null );
      button.destroy();
      TestUtil.flush();
    },

    testExecuteCheckButton : function() {
      //this test is also valid for toggle button
      var button = this.createButton( "w11", "check" );

      TestUtil.click( button );

      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );
      rwt.remote.Connection.getInstance().send();
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w11", "selection" ) );
    },

    testExecuteCheckButtonWithSelectionListener : function() {
      //this test is also valid for toggle button
      var button = this.createButton( "w11", "check" );
      TestUtil.clearRequestLog();
      TestUtil.fakeListener( button, "Selection", true );

      TestUtil.click( button );

      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w11", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.press( button, "Space" );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w11", "selection" ) );
      button.destroy();
    },

    testExecuteRadioButton_ByMouse : function() {
      var button = this.createButton( "w11", "radio" );
      button.setSelection( true );
      TestUtil.fakeListener( button, "Selection", true );
      var button2 = this.createButton( "w2", "radio" );
      TestUtil.fakeListener( button2, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.click( button2 );

      assertFalse( button.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 2, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject( 0 ).findSetProperty( "w11", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w11", "Selection" ) );
      assertTrue( TestUtil.getMessageObject( 1 ).findSetProperty( "w2", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w2", "Selection" ) );
      button.destroy();
      button2.destroy();
    },

    testExecuteRadioButton_ByKeyboard : function() {
      var button = this.createButton( "w11", "radio" );
      button.setSelection( true );
      TestUtil.fakeListener( button, "Selection", true );
      var button2 = this.createButton( "w2", "radio" );
      TestUtil.fakeListener( button2, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.press( button, "Down" );

      assertFalse( button.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 2, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject( 0 ).findSetProperty( "w11", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w11", "Selection" ) );
      assertTrue( TestUtil.getMessageObject( 1 ).findSetProperty( "w2", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w2", "Selection" ) );
      button.destroy();
      button2.destroy();
    },

    testExecuteSelectedRadioButton : function() {
      var button = this.createButton( "w11", "radio" );
      button.setSelection( true );
      TestUtil.fakeListener( button, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.click( button );

      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w11", "selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      button.destroy();
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      var button1 = this.createButton( "w11", "radio" );
      button1.setNoRadioGroup( true );
      TestUtil.fakeListener( button1, "Selection", true );
      var button2 = this.createButton( "w2", "radio" );
      button2.setNoRadioGroup( true );
      TestUtil.fakeListener( button2, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.click( button1 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w11", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
      button1.destroy();
      button2.destroy();
    },

    testSelectNextRadioButtonByKeyboard : function() {
      var radios = [];
      for( var i = 0; i < 3; i++ ) {
        radios[ i ] = this.createButton( "w1" + i, "radio" );
      }
      TestUtil.flush();
      radios[ 0 ].setSelection( true );
      radios[ 0 ].setFocused( true );
      radios[ 1 ].setEnabled( false );

      TestUtil.pressOnce( radios[ 0 ].getElement(), "Right", 0 );

      assertFalse( radios[ 0 ]._selected );
      assertFalse( radios[ 0 ].getFocused() );
      assertFalse( radios[ 1 ]._selected );
      assertFalse( radios[ 1 ].getFocused() );
      assertTrue( radios[ 2 ]._selected );
      assertTrue( radios[ 2 ].getFocused() );
      for( var i = 0; i < 3; i++ ) {
        radios[ i ].destroy();
      }
    },

    testSelectPreviousRadioButtonByKeyboard : function() {
      var radios = [];
      for( var i = 0; i < 3; i++ ) {
        radios[ i ] = this.createButton( "w1" + i, "radio" );
      }
      TestUtil.flush();
      radios[ 2 ].setSelection( true );
      radios[ 2 ].setFocused( true );
      radios[ 1 ].setEnabled( false );

      TestUtil.pressOnce( radios[ 2 ].getElement(), "Left", 0 );

      assertFalse( radios[ 2 ]._selected );
      assertFalse( radios[ 2 ].getFocused() );
      assertFalse( radios[ 1 ]._selected );
      assertFalse( radios[ 1 ].getFocused() );
      assertTrue( radios[ 0 ]._selected );
      assertTrue( radios[ 0 ].getFocused() );
      for( var i = 0; i < 3; i++ ) {
        radios[ i ].destroy();
      }
    },

    testExecutePushButton : function() {
      var button = this.createButton( "w11", "push" );
      TestUtil.clearRequestLog();
      TestUtil.click( button );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.fakeListener( button, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.click( button );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( button.hasState( "selected" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.press( button, "Enter" );
      assertFalse( button.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
    },

    testExecutePushButton_sendsModifier : function() {
      var button = this.createButton( "w11", "push" );
      TestUtil.fakeListener( button, "Selection", true );

      TestUtil.shiftClick( button );

      var message = TestUtil.getMessageObject();
      assertTrue( message.findNotifyProperty( "w11", "Selection", "shiftKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "altKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "ctrlKey" ) );
    },

    testExecutePushButton_byMouse_sendsButton : function() {
      var button = this.createButton( "w11", "push" );
      TestUtil.fakeListener( button, "Selection", true );

      TestUtil.click( button );

      var message = TestUtil.getMessageObject();
      assertEquals( 1, message.findNotifyProperty( "w11", "Selection", "button" ) );
    },

    testExecutePushButton_byKeyboard_doesNotSendButton : function() {
      var button = this.createButton( "w11", "push" );
      TestUtil.fakeListener( button, "Selection", true );

      TestUtil.press( button, "Enter" );

      var message = TestUtil.getMessageObject();
      assertEquals( undefined, message.findNotifyProperty( "w11", "Selection", "button" ) );
    },

    testWrap : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setWrap( true );
      TestUtil.flush();
      assertEquals( 2, button.getFlexibleCell() );
      button.destroy();
    },

    testRenderMnemonic_NotInitially : function() {
      var shell = createActiveShell();
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setParent( shell );
      button.setText( "foo" );

      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      assertEquals( "foo", button.getCellContent( 2 ) );
      shell.destroy();
    },

    testRenderMnemonic_FirstChar : function() {
      var shell = createActiveShell();
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 0 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "<span style=\"text-decoration:underline\">f</span>oo", button.getCellContent( 2 ) );
      shell.destroy();
    },

    testRenderMnemonic_OnActivate : function() {
      var shell = createActiveShell();
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", button.getCellContent( 2 ) );
      shell.destroy();
    },

    testDoNotRenderMnemonic_OnDeactivate : function() {
      var shell = createActiveShell();
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", button.getCellContent( 2 ) );
      shell.destroy();
    },

    testTriggerMnemonic_WrongCharacterDoesNothing : function() {
      var shell = createActiveShell();
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 78 );
      TestUtil.flush();

      assertFalse( button.getFocused() );
      shell.destroy();
    },

    testTriggerMnemonic_FocusesButton : function() {
      var shell = createActiveShell();
      var button = this.createButton( "w11", "push" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( button.getFocused() );
      shell.destroy();
    },

    testTriggerMnemonic_SendsSelection : function() {
      var shell = createActiveShell();
      var button = this.createButton( "w11", "push" );
      button.setParent( shell );
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.fakeListener( button, "Selection", true );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      shell.destroy();
    },

    testPushButton_getToolTipTargetBounds : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "bounds" : [ 10, 10, 100, 20 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      TestUtil.flush();

      var bounds = widget.getToolTipTargetBounds();
      assertEquals( 0, bounds.left );
      assertEquals( 0, bounds.top );
      assertEquals( 100, bounds.width );
      assertEquals( 20, bounds.height );
      shell.destroy();
    },

    testCheckButton_getToolTipTargetBounds : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "CHECK" ],
          "parent" : "w2",
          "bounds" : [ 10, 10, 100, 20 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      widget.setSelectionIndicator( [ "foo.jpg", 7, 8 ] );
      widget.setPadding( 4 );
      TestUtil.flush();

      assertEquals( { "left" : 4, "top" : 6, "width" : 7, "height" : 8 },
                    widget.getToolTipTargetBounds() );
      shell.destroy();
    },

    testHoverAnimation_Bug418667 : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();
      button.setAnimation( { "hoverIn" : [ 400, "linear" ] } );
      button.setBackgroundGradient( [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ] );
      TestUtil.flush();

      button.addState( "over" );
      button._animation.activateRendererOnce();
      button.setBackgroundGradient( [ [ 0, "rgb(255, 255, 255)" ], [ 1, "rgb(255, 0, 255)" ] ] );
      assertTrue( button._animation.isStarted() );
      button.setBackgroundGradient( null );

      assertFalse( button._animation.isStarted() );
      button.destroy();
    },

    createButton : function( id, type ) {
      var button = new rwt.widgets.Button( type );
      button.addState( "rwt_" + type.toUpperCase() );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( id, button, handler );
      button.addToDocument();
      TestUtil.flush();
      return button;
    }

  }

} );

var createActiveShell = function() {
  var shell = TestUtil.createShellByProtocol( "w2" );
  shell.show();
  shell.setActive( true );
  TestUtil.flush();
  TestUtil.clearRequestLog();
  return shell;
};

}() );
