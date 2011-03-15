/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ButtonTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;        
  },
  
  members : {
        
    testFocusIndicatorPush : function() {
      var hasFocusIndicator = function( widget ) {
        var node = widget._getTargetNode();
        var result = false;
        for( var i = 0; i < node.childNodes.length; i++ ) {
          if( node.childNodes[ i ].getAttribute( "id") == "focusIndicator" ) {
            result = true;
          }
        }
        return result;
      }
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
      }
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
        this.testUtil.getCssBackgroundImage( button.getCellNode( 1 ) ).search( "test.jpg" ) != -1 
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
      button.setUserData( "id", "w1" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertEquals( 0, this.testUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w1.selection" ]
      );
      button.setHasSelectionListener( true );
      button.setSelection( false );
      assertFalse( button.hasState( "selected" ) );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertContains( "w1.selection=true",  this.testUtil.getMessage() );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertFalse( button.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertContains( "w1.selection=false",  this.testUtil.getMessage() );
      this.testUtil.clearRequestLog();
      this.testUtil.press( button, "Space" );
      assertTrue( button.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertContains( "w1.selection=true",  this.testUtil.getMessage() );
    },
    
    testExecuteRadioButton : function() {
      var button = new org.eclipse.rwt.widgets.Button( "radio" );
      button.addState( "rwt_RADIO" );
      button.setUserData( "id", "w1" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertEquals( 0, this.testUtil.getRequestsSend() );      
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w1.selection" ]
      );
      button.setSelection( false );      
      button.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertTrue( button.hasState( "selected" ) );      
      assertContains( "w1.selection=true",  this.testUtil.getMessage() );
      var button2 = new org.eclipse.rwt.widgets.Button( "radio" );
      button2.addState( "rwt_RADIO" );
      button2.setUserData( "id", "w2" );
      button2.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.testUtil.clearRequestLog();
      button2.setHasSelectionListener( true );
      this.testUtil.click( button2 );
      assertFalse( button.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );      
      var msg = this.testUtil.getMessage();      
      assertContains( "w1.selection=false", msg );
      assertContains( "w2.selection=true", msg );
      this.testUtil.clearRequestLog();
      this.testUtil.press( button2, "Up" );
      assertTrue( button.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "w1.selection=true", msg );
      assertContains( "w2.selection=false", msg );
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      var button1 = new org.eclipse.rwt.widgets.Button( "radio" );
      button1.addState( "rwt_RADIO" );
      button1.setUserData( "id", "w1" );
      button1.setNoRadioGroup( true );
      button1.setHasSelectionListener( true );
      button1.addToDocument();
      var button2 = new org.eclipse.rwt.widgets.Button( "radio" );
      button2.addState( "rwt_RADIO" );
      button2.setUserData( "id", "w2" );
      button2.setNoRadioGroup( true );
      button2.setHasSelectionListener( true );
      button2.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      this.testUtil.clearRequestLog();
      this.testUtil.click( button1 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );      
      var msg = this.testUtil.getMessage();      
      assertContains( "w1.selection=true", msg );
      assertContainsNot( "w2.selection", msg );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertTrue( button2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );      
      var msg = this.testUtil.getMessage();      
      assertContainsNot( "w1.selection", msg );
      assertContains( "w2.selection=true", msg );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button2 );
      assertTrue( button1.hasState( "selected" ) );
      assertFalse( button2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );      
      var msg = this.testUtil.getMessage();      
      assertContainsNot( "w1.selection", msg );
      assertContains( "w2.selection=false", msg );
    },

    testExecutePushButton : function() {
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      this._currentButton = button;
      button.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      button.setUserData( "id", "w1" );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertEquals( 0, this.testUtil.getRequestsSend() );      
      button.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.click( button );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertFalse( button.hasState( "selected" ) );      
      var msg = this.testUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );
      assertContainsNot( "w1.selection=true",  msg );   
      this.testUtil.clearRequestLog();
      this.testUtil.press( button, "Enter" );
      assertFalse( button.hasState( "selected" ) );      
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );      
    },
    
    testWrap : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      button.addState( "rwt_PUSH" );
      button.addToDocument();
      button.setWrap( true );
      testUtil.flush();
      assertEquals( 2, button._flexibleCell );
      button.destroy();
    }
    
  }
  
} );