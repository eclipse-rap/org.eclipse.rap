/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TextTest", {

  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "mshtml" ],
          
    testCreateAsTextSetPasswordMode : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text();
      text.addToDocument();
      testUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      var oldElement = text._inputElement;
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      assertTrue( testUtil.getTimerOnceLog().length == 1 );
      testUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      assertTrue( oldElement != text._inputElement );
      assertTrue( oldElement.parentNode != text._getTargetNode() );
      assertNull( oldElement.onpropertychange );
      oldElement = text._inputElement;
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      assertTrue( testUtil.getTimerOnceLog().length == 1 );
      testUtil.flush();
      assertTrue( oldElement != text._inputElement );
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      assertTrue( oldElement.parentNode != text._getTargetNode() );
      assertNull( oldElement.onpropertychange );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
          
    testCreateAsPasswordSetTextMode : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text();
      text.addToDocument();
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      testUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      var oldElement = text._inputElement;
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      assertTrue( testUtil.getTimerOnceLog().length == 1 );
      testUtil.flush();
      assertTrue( oldElement != text._inputElement );
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      assertTrue( oldElement.parentNode != text._getTargetNode() );
      assertNull( oldElement.onpropertychange );
      oldElement = text._inputElement;
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      assertTrue( testUtil.getTimerOnceLog().length == 1 );
      testUtil.flush();
      assertTrue( oldElement != text._inputElement );
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );      
      assertTrue( oldElement.parentNode != text._getTargetNode() );
      assertNull( oldElement.onpropertychange );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
    
    testValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text();
      text.addToDocument();
      text.setPasswordMode( true );
      text.setValue( "asdf" );
      testUtil.flush();
      assertEquals( "asdf", text.getValue() );
      assertEquals( "asdf", text.getComputedValue() );
      assertEquals( "asdf", text._inputElement.value );
      text.setPasswordMode( false );
      testUtil.flush();
      assertEquals( "asdf", text.getValue() );
      assertEquals( "asdf", text.getComputedValue() );
      assertEquals( "asdf", text._inputElement.value );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
    
    testSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text();
      text.setValue( "asdfjklö" );
      text.addToDocument();
      testUtil.flush();
      text.setSelectionStart( 2 );
      text.setSelectionLength( 3 );
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
      text.setPasswordMode( false );
      testUtil.flush();
      assertEquals( 2, text.getSelectionStart() );
      assertEquals( 3, text.getSelectionLength() );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
    
    testCss : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text();
      text.addToDocument();
      testUtil.flush();
      var oldCss = text._inputElement.style.cssText;
      text.setPasswordMode( true );
      testUtil.flush();
      assertEquals( oldCss, text._inputElement.style.cssText );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    }

  }
  
} );