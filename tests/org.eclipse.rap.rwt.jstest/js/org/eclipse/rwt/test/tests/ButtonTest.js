/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ButtonTest", {

  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this.TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  },
  
  members : {

    testCreatePushButtonByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.Button );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertEquals( "push-button", widget.getAppearance() );
      assertEquals( -1, widget._flexibleCell );
      shell.destroy();
      widget.destroy();
    },
    
    testCreateToggleButtonByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "TOGGLE" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_TOGGLE" ) );
      assertEquals( "push-button", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateCheckButtonByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "CHECK" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_CHECK" ) );
      assertEquals( "check-box", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateRadioButtonByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "RADIO" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_RADIO" ) );
      assertEquals( "radio-button", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testSetNoRadioGroupByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.addState( "rwt_NO_RADIO_GROUP" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "RADIO" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.getNoRadioGroup() );
      shell.destroy();
      widget.destroy();
    },

    testSetWrapByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 2, widget._flexibleCell );
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
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "text" : "text\n && \"text"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "text\n &amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocolWithWrap : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH", "WRAP" ],
          "parent" : "w2",
          "text" : "text\n && \"text"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "text<br/> &amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "alignment" : "right"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "right", widget.getHorizontalChildrenAlign() );
      assertEquals( "right", widget._alignment );
      shell.destroy();
      widget.destroy();
    },

    testSetAlignmentByProtocol_Arrow : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "ARROW" ],
          "parent" : "w2",
          "alignment" : "down"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_DOWN" ) );
      assertEquals( "down", widget._alignment );
      shell.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2",
          "image" : [ "image.png", 10, 20 ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "image.png", widget.getCellContent( 1 ) );
      assertEquals( [ 10, 20 ], widget.getCellDimension( 1 ) );
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
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "TOGGLE" ],
          "parent" : "w2",
          "selection" : true
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._selected );
      shell.destroy();
      widget.destroy();
    },

    testSetGrayedByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "CHECK" ],
          "parent" : "w2",
          "grayed" : true
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
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
      var focudIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.setText( "bla" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( button.hasState( "focus" ) );
      assertFalse( hasFocusIndicator( button ) );
      button.focus();
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( hasFocusIndicator( button ) );
      button.setImage( "test.jpg" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( hasFocusIndicator( button ) );
      button.blur();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( hasFocusIndicator( button ) );
      button.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
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
      var focudIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
      var button = new org.eclipse.rwt.widgets.Button( "check" );      
      button.addState( "rwt_CHECK" );
      button.setText( "bla" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( button.hasState( "focus" ) );
      assertFalse( hasFocusIndicator( button ) );
      button.focus();
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( hasFocusIndicator( button ) );
      button.setImage( "test.jpg" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( hasFocusIndicator( button ) );
      button.blur();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( hasFocusIndicator( button ) );
      button.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testParent : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      this._currentButton = button;
      button.setText( "Hello World!" );
      button.setImage( "url.jpg" );
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertIdentical( 
        button._getTargetNode(), 
        button.getCellNode( 1 ).parentNode 
      );
      assertIdentical(
        button._getTargetNode(), 
        button.getCellNode( 2 ).parentNode 
      );
      button.setParent( null );
      button.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testText : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      this._currentButton = button;
      button.setText( "Hello World!" );
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( "Hello World!", button.getCellNode( 2 ).innerHTML );
      button.setParent( null );
      button.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testImage : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      this._currentButton = button;
      button.setImage( "test.jpg" );
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue(
        this.TestUtil.getCssBackgroundImage( button.getCellNode( 1 ) ).search( "test.jpg" ) != -1 
      );
      button.setParent( null );
      button.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testExecuteCheckButton : function() { 
      //this test is also valid for toggle button
      //var button = new org.eclipse.swt.widgets.CheckBox();
      var button = new org.eclipse.rwt.widgets.Button( "check" );
      button.addState( "rwt_CHECK" );
      org.eclipse.swt.WidgetManager.getInstance().add( button, "w11" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w11.selection" ]
      );
      button.setHasSelectionListener( true );
      button.setSelection( false );
      assertFalse( button.hasState( "selected" ) );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertContains( "w11.selection=true",  this.TestUtil.getMessage() );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertFalse( button.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertContains( "w11.selection=false",  this.TestUtil.getMessage() );
      this.TestUtil.clearRequestLog();
      this.TestUtil.press( button, "Space" );
      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertContains( "w11.selection=true",  this.TestUtil.getMessage() );
    },
    
    testExecuteRadioButton : function() {
      var button = new org.eclipse.rwt.widgets.Button( "radio" );
      button.addState( "rwt_RADIO" );
      org.eclipse.swt.WidgetManager.getInstance().add( button, "w11" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertEquals( 0, this.TestUtil.getRequestsSend() );      
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w11.selection" ]
      );
      button.setSelection( false );      
      button.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );      
      assertContains( "w11.selection=true",  this.TestUtil.getMessage() );
      var button2 = new org.eclipse.rwt.widgets.Button( "radio" );
      button2.addState( "rwt_RADIO" );
      org.eclipse.swt.WidgetManager.getInstance().add( button2, "w2" );
      button2.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.TestUtil.clearRequestLog();
      button2.setHasSelectionListener( true );
      this.TestUtil.click( button2 );
      assertFalse( button.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );      
      var msg = this.TestUtil.getMessage();      
      assertContains( "w11.selection=false", msg );
      assertContains( "w2.selection=true", msg );
      this.TestUtil.clearRequestLog();
      this.TestUtil.press( button2, "Up" );
      assertTrue( button.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      msg = this.TestUtil.getMessage();
      assertContains( "w11.selection=true", msg );
      assertContains( "w2.selection=false", msg );
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      var button1 = new org.eclipse.rwt.widgets.Button( "radio" );
      button1.addState( "rwt_RADIO" );
      org.eclipse.swt.WidgetManager.getInstance().add( button1, "w11" );
      button1.setNoRadioGroup( true );
      button1.setHasSelectionListener( true );
      button1.addToDocument();
      var button2 = new org.eclipse.rwt.widgets.Button( "radio" );
      button2.addState( "rwt_RADIO" );
      org.eclipse.swt.WidgetManager.getInstance().add( button2, "w2" );
      button2.setNoRadioGroup( true );
      button2.setHasSelectionListener( true );
      button2.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button1 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );      
      var msg = this.TestUtil.getMessage();      
      assertContains( "w11.selection=true", msg );
      assertContainsNot( "w2.selection", msg );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );      
      msg = this.TestUtil.getMessage();      
      assertContainsNot( "w11.selection", msg );
      assertContains( "w2.selection=true", msg );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );      
      msg = this.TestUtil.getMessage();      
      assertContainsNot( "w11.selection", msg );
      assertContains( "w2.selection=false", msg );
    },

    testExecutePushButton : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      org.eclipse.swt.WidgetManager.getInstance().add( button, "w11" );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertEquals( 0, this.TestUtil.getRequestsSend() );      
      button.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( button );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertFalse( button.hasState( "selected" ) );      
      var msg = this.TestUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );
      assertContainsNot( "w11.selection=true",  msg );   
      this.TestUtil.clearRequestLog();
      this.TestUtil.press( button, "Enter" );
      assertFalse( button.hasState( "selected" ) );      
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      msg = this.TestUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );      
    },
    
    testWrap : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setWrap( true );
      TestUtil.flush();
      assertEquals( 2, button._flexibleCell );
      button.destroy();
    }
    
  }
  
} );