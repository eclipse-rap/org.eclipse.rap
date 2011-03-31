/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
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

    testRenderPaddingWithRoundedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setPadding( 3 );
      text.setBorder( new org.eclipse.rwt.RoundedBorder( 1, "black", 0 ) );
      text.addToDocument();
      testUtil.flush();
      assertEquals( "", text._style.paddingLeft );
      assertEquals( "3px", text._innerStyle.paddingLeft );
      text.destroy();
    },

    testCreateAsTextSetPasswordMode : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
      testUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      testUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      testUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },

    testCreateAsPasswordSetTextMode : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      testUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( false );
      testUtil.flush();
      assertEquals( "text", text._inputType );
      assertEquals( "text", text._inputElement.type );
      testUtil.clearTimerOnceLog();
      text.setPasswordMode( true );
      testUtil.flush();
      assertEquals( "password", text._inputType );
      assertEquals( "password", text._inputElement.type );      
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
    
    testValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
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
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setValue( "asdfjkloe" );
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
      var text = new org.eclipse.rwt.widgets.Text( false );
      org.eclipse.swt.TextUtil.initialize( text );
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
    },
    
    testCreateTextArea : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var text = new org.eclipse.rwt.widgets.Text( true );
      org.eclipse.swt.TextUtil.initialize( text );
      text.setPasswordMode( true ); // should be ignored
      text.setWrap( false ); 
      text.addToDocument();
      testUtil.flush();
      assertNull( text._inputType );
      assertEquals( "textarea", text._inputTag );
      assertEquals( "textarea", text._inputElement.tagName.toLowerCase() );
      assertEquals( "text-area", text.getAppearance() );
      text.setWrap( true );
      var wrapProperty = "";
      var wrapAttribute = "";
      try{
        wrapProperty = text._inputElement.wrap;
      }catch( ex ){}
      try{
        wrapAttribute = text._inputElement.getAttribute( "wrap" );
      }catch( ex ){}
      assertTrue( wrapProperty == "soft" || wrapAttribute == "soft" );
      text.setParent( null );
      text.destroy();
      testUtil.flush();
      testUtil.clearTimerOnceLog();
    },
    
    testTextAreaMaxLength : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        // NOTE: This test would fail in IE because it has a bug that sometimes
        // prevents a textFields value from being overwritten and read in the 
        // same call
      },
      "default" : function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil.prepareTimerUse();
        var text = new org.eclipse.rwt.widgets.Text( true );
        org.eclipse.swt.TextUtil.initialize( text );
        var changeLog = [];
        text.addEventListener( "input", function(){
          changeLog.push( true );
        } );
        text.setValue( "0123456789" );
        text.addToDocument();
        testUtil.flush();
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
        testUtil.flush();
        testUtil.clearTimerOnceLog();
      }
    } )

  }
  
} );