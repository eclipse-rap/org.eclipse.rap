/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
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
var ObjectManager = rwt.remote.ObjectRegistry;
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
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Button );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertEquals( "push-button", widget.getAppearance() );
      assertEquals( -1, widget._flexibleCell );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 2, widget._flexibleCell );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      var widget = ObjectManager.getObject( "w3" );
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
      button.setText( "Hello World!" );
      button.addToDocument();
      TestUtil.flush();
      assertEquals( "Hello World!", button.getCellNode( 2 ).innerHTML );
      button.setParent( null );
      button.destroy();
      TestUtil.flush();
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
      var button = new rwt.widgets.Button( "check" );
      button.addState( "rwt_CHECK" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addToDocument();
      TestUtil.flush();

      TestUtil.click( button );

      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );
      rwt.remote.Server.getInstance().send();
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w11", "selection" ) );
    },

    testExecuteCheckButtonWithSelectionListener : function() {
      //this test is also valid for toggle button
      var button = new rwt.widgets.Button( "check" );
      button.addState( "rwt_CHECK" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addToDocument();
      TestUtil.flush();
      TestUtil.clearRequestLog();
      button.setHasSelectionListener( true );

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
      var button = new rwt.widgets.Button( "radio" );
      button.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addToDocument();
      TestUtil.flush();
      button.setSelection( true );
      var button2 = new rwt.widgets.Button( "radio" );
      button.setHasSelectionListener( true );
      button2.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w2", button2, handler );
      button2.addToDocument();
      TestUtil.flush();
      button2.setHasSelectionListener( true );
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
      var button = new rwt.widgets.Button( "radio" );
      button.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addToDocument();
      TestUtil.flush();
      button.setSelection( true );
      var button2 = new rwt.widgets.Button( "radio" );
      button.setHasSelectionListener( true );
      button2.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w2", button2, handler );
      button2.addToDocument();
      TestUtil.flush();
      button2.setHasSelectionListener( true );
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
      var button = new rwt.widgets.Button( "radio" );
      button.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addToDocument();
      TestUtil.flush();
      button.setSelection( true );
      button.setHasSelectionListener( true );
      TestUtil.clearRequestLog();

      TestUtil.click( button );

      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w11", "selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      button.destroy();
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      var button1 = new rwt.widgets.Button( "radio" );
      button1.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button1, handler );
      button1.setNoRadioGroup( true );
      button1.setHasSelectionListener( true );
      button1.addToDocument();
      var button2 = new rwt.widgets.Button( "radio" );
      button2.addState( "rwt_RADIO" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w2", button2, handler );
      button2.setNoRadioGroup( true );
      button2.setHasSelectionListener( true );
      button2.addToDocument();
      TestUtil.flush();
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
        radios[ i ] = new rwt.widgets.Button( "radio" );
        radios[ i ].addState( "rwt_RADIO" );
        radios[ i ].addToDocument();
        var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
        rwt.remote.ObjectRegistry.add( "w1" + i, radios[ i ], handler );
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
        radios[ i ] = new rwt.widgets.Button( "radio" );
        radios[ i ].addState( "rwt_RADIO" );
        radios[ i ].addToDocument();
        var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
        rwt.remote.ObjectRegistry.add( "w1" + i, radios[ i ], handler );
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
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      TestUtil.flush();
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      TestUtil.clearRequestLog();
      TestUtil.click( button );
      assertEquals( 0, TestUtil.getRequestsSend() );
      button.setHasSelectionListener( true );
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

    testSendModifier : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setHasSelectionListener( true );
      TestUtil.flush();
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );

      TestUtil.shiftClick( button );

      var message = TestUtil.getMessageObject();
      assertTrue( message.findNotifyProperty( "w11", "Selection", "shiftKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "altKey" ) );
      assertFalse( message.findNotifyProperty( "w11", "Selection", "ctrlKey" ) );
    },

    testWrap : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setWrap( true );
      TestUtil.flush();
      assertEquals( 2, button._flexibleCell );
      button.destroy();
    },

    testRenderMnemonic_NotInitially : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );

      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      assertEquals( "foo", button.getCellContent( 2 ) );
      button.destroy();
    },

    testRenderMnemonic_FirstChar : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 0 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "<span style=\"text-decoration:underline\">f</span>oo", button.getCellContent( 2 ) );
      button.destroy();
    },

    testRenderMnemonic_OnActivate : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", button.getCellContent( 2 ) );
      button.destroy();
    },

    testDoNotRenderMnemonic_OnDeactivate : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", button.getCellContent( 2 ) );
      button.destroy();
    },

    testTriggerMnemonic_WrongCharacterDoesNothing : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 78 );
      TestUtil.flush();

      assertFalse( button.getFocused() );
      button.destroy();
    },

    testTriggerMnemonic_FocusesButton : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( button.getFocused() );
      button.destroy();
    },

    testTriggerMnemonic_SendsSelection : function() {
      var button = new rwt.widgets.Button( "push" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Button" );
      rwt.remote.ObjectRegistry.add( "w11", button, handler );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setText( "foo" );
      button.setMnemonicIndex( 1 );
      button.setHasSelectionListener( true );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      button.destroy();
    }

  }

} );

}());
