/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Text_Test {

  private Display display;
  private Shell shell;
  private Text text;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    text = new Text( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValuesForSingleText() {
    assertEquals( "", text.getText() );
    assertEquals( "", text.getMessage() );
    assertEquals( Text.LIMIT, text.getTextLimit() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( ( char )0, text.getEchoChar() );
  }

  @Test
  public void testTextLimit() {
    text.setTextLimit( -1 );
    assertEquals( -1, text.getTextLimit() );
    text.setTextLimit( -20 );
    assertEquals( -20, text.getTextLimit() );
    text.setTextLimit( -12345 );
    assertEquals( -12345, text.getTextLimit() );
    text.setTextLimit( 20 );
    assertEquals( 20, text.getTextLimit() );
    try {
      text.setTextLimit( 0 );
      fail( "Must not allow to set textLimit to zero" );
    } catch( IllegalArgumentException e ) {
      // as expected
    }
    text.setText( "Sample_text" );
    text.setTextLimit( 6 );
    assertEquals( "Sample_text", text.getText() );
    text.setText( "Other_text" );
    assertEquals( "Other_", text.getText() );
  }

  @Test
  public void testGetLineHeight() {
    Text text = new Text( shell, SWT.MULTI );
    // default theme font is 14px
    assertEquals( 16, text.getLineHeight() );
    text.setFont( new Font( display, "Helvetica", 12, SWT.NORMAL ) );
    assertEquals( 14, text.getLineHeight() );
    text.setFont( null );
    assertEquals( 16, text.getLineHeight() );
  }

  @Test
  public void testSelection() {
    // test select all
    text.setText( "abc" );
    text.selectAll();
    assertEquals( new Point( 0, 3 ), text.getSelection() );
    assertEquals( "abc", text.getSelectionText() );

    // test clearSelection
    text.setText( "abc" );
    text.clearSelection();
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getSelectionText() );

    // test setSelection
    text.setText( "abc" );
    text.setSelection( 1 );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );
    text.setSelection( 1000 );
    assertEquals( new Point( 3, 3 ), text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );
    Point saveSelection = text.getSelection();
    text.setSelection( -1 );
    assertEquals( saveSelection, text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );
    text.setText( "abcdefg" );
    text.setSelection( new Point( 5, 2 ) );
    assertEquals( new Point( 2, 5 ), text.getSelection() );

    // test selection when changing text
    text.setText( "abcefg" );
    text.setSelection( 1, 2 );
    text.setText( "gfecba" );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    // ... even setting the same text again will clear the selection
    text.setText( "abcefg" );
    text.setSelection( 1, 2 );
    text.setText( text.getText() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
  }

  @Test
  public void testModifyEvent() {
    final StringBuilder log = new StringBuilder();
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        log.append( "modifyEvent|" );
        assertSame( text, event.getSource() );
      }
    } );
    // Changing the text fires a modifyEvent
    text.setText( "abc" );
    assertEquals( "modifyEvent|", log.toString() );
    // Setting the same value also fires a modifyEvent
    log.setLength( 0 );
    text.setText( "abc" );
    assertEquals( "modifyEvent|", log.toString() );
  }

  @Test
  public void testVerifyEvent() {
    VerifyListener verifyListener;
    final java.util.List<TypedEvent> log = new ArrayList<TypedEvent>();
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        log.add( event );
      }
    } );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        assertEquals( '\0', event.character );
        assertEquals( 0, event.keyCode );
        log.add( event );
      }
    } );

    // VerifyEvent is also sent when setting text to the already set value
    log.clear();
    text.setText( "" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Test verifyListener that prevents (doit=false) change
    text.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.doit = false;
      }
    };
    text.addVerifyListener( verifyListener );
    text.setText( "other" );
    assertEquals( 1, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( "", text.getText() );
    text.removeVerifyListener( verifyListener );

    // Test verifyListener that manipulates text
    text.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = "manipulated";
      }
    };
    text.addVerifyListener( verifyListener );
    text.setText( "other" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    assertEquals( "manipulated", text.getText() );
    text.removeVerifyListener( verifyListener );

    // Ensure that VerifyEvent#start and #end denote the positions of the old
    // text and #text denotes the text to be set
    String oldText = "old";
    text.setText( oldText );
    log.clear();
    String newText = oldText + "changed";
    text.setText( newText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    VerifyEvent verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 0, verifyEvent.start );
    assertEquals( oldText.length(), verifyEvent.end );
    assertEquals( newText, verifyEvent.text );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Ensure that VerifyEvent gets fired when setEditable was set to false
    text.setText( "" );
    text.setEditable( false );
    log.clear();
    text.setText( "whatever" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    text.setEditable( true );

    // Ensure that VerifyEvent#text denotes the text to be set
    // and not the cut by textLimit one
    text.setTextLimit( 5 );
    String sampleText = "sample_text";
    log.clear();
    text.setText( sampleText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( sampleText, verifyEvent.text );
  }

  // TODO [bm] extend testcase with newline chars and getLineCount
  @Test
  public void testInsert() {
    // Test insert on multi-line Text
    Text text = new Text( shell, SWT.MULTI );
    text.setBounds( 0, 0, 500, 500 );
    // Ensure initial state
    assertEquals( "", text.getText() );
    // Test with allowed arguments
    text.insert( "" );
    assertEquals( "", text.getText() );
    text.insert( "fred" );
    assertEquals( "fred", text.getText() );
    text.setSelection( 2 );
    text.insert( "helmut" );
    assertEquals( "frhelmuted", text.getText() );
    // Test with illegal argument
    try {
      text.setText( "oldText" );
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldText", text.getText() );
    }

    // Test insert on single-line Text
    text = new Text( shell, SWT.SINGLE );
    assertEquals( "", text.getText() );
    text.insert( "" );
    assertEquals( "", text.getText() );
    text.insert( "fred" );
    assertEquals( "fred", text.getText() );
    text.setSelection( 2 );
    text.insert( "helmut" );
    assertEquals( "frhelmuted", text.getText() );
    // Test with illegal arguments
    text = new Text( shell, SWT.SINGLE );
    try {
      text.setText( "oldText" );
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldText", text.getText() );
    }
	}

  // TODO [bm] extend testcase with newline chars for SWT.MULTI
  @Test
  public void testAppend() {
		Text text = new Text(shell, SWT.SINGLE);

		try {
			text.append(null);
			fail("No exception thrown for string == null");
		} catch (IllegalArgumentException e) {
		}

		text = new Text(shell, SWT.SINGLE);

		try {
			text.append(null);
			fail("No exception thrown on string == null");
		} catch (IllegalArgumentException e) {
		}

		// tests a SINGLE line text editor
		text = new Text(shell, SWT.SINGLE);

		text.setText("01");
		text.append("23");
		assertEquals("0123", text.getText());
		text.append("45");
		assertEquals("012345", text.getText());
		text.setSelection(0);
		text.append("67");
		assertEquals("01234567", text.getText());

	}

  @Test
  public void testInsertWithModifyListener() {
    final java.util.List<ModifyEvent> log = new ArrayList<ModifyEvent>();
    Text text = new Text( shell, SWT.SINGLE );
    text.setBounds( 0, 0, 100, 20 );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        log.add( event );
      }
    } );

    // Test that event is fired when correctly using insert
    log.clear();
    text.insert( "abc" );
    assertEquals( 1, log.size() );

    // Test that event is *not* fired when passing illegal argument to insert
    log.clear();
    text = new Text( shell, SWT.SINGLE );
    try {
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
    }
    assertEquals( 0, log.size() );
  }

  @Test
  public void testComputeSize_Empty() {
    assertEquals( new Point( 85, 26 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_WithText() {
    text.setText( "This is a long long text!" );
    assertEquals( new Point( 189, 28 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_WithMessage() {
    text.setMessage( "This is a message that is longer than the text!" );
    assertEquals( new Point( 337, 28 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_WithMessage_Multi() {
    text = new Text( shell, SWT.MULTI );
    text.setMessage( "This is a message that is longer than the text!" );
    assertEquals( new Point( 337, 30 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_WithTextAndMessage() {
    text.setText( "This is a long long text!" );
    text.setMessage( "This is a message that is longer than the text!" );
    assertEquals( new Point( 337, 28 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_WithTextAndMessage_Multi() {
    text = new Text( shell, SWT.MULTI );
    text.setText( "This is a long long text!" );
    text.setMessage( "This is a message that is longer than the text!" );
    assertEquals( new Point( 337, 30 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_Multi() {
    text = new Text( shell, SWT.MULTI );
    text.setText( "This is a long long text!\nThis is the second row." );
    assertEquals( new Point( 189, 47 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_MultiWithWrap() {
    text = new Text( shell, SWT.MULTI | SWT.WRAP );
    text.setText( "This is a long long text!\nThis is the second row." );
    assertEquals( new Point( 71, 152 ), text.computeSize( 50, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_MultiWithWrapAndBorder() {
    text = new Text( shell, SWT.MULTI | SWT.WRAP | SWT.BORDER );
    text.setText( "This is a long long text!\nThis is the second row." );
    assertEquals( new Point( 73, 154 ), text.computeSize( 50, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_MultiWithHint() {
    text = new Text( shell, SWT.MULTI | SWT.WRAP | SWT.BORDER );
    assertEquals(  new Point( 123, 114 ), text.computeSize( 100, 100 ) );
  }

  @Test
  public void testComputeSize_SearchWithoutIcons() {
    text = new Text( shell, SWT.SEARCH );
    text.setText( "This is a long long text!" );
    assertEquals( new Point( 191, 30 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_SearchWithOneIcon() {
    text = new Text( shell, SWT.SEARCH | SWT.ICON_SEARCH );
    text.setText( "This is a long long text!" );
    assertEquals( new Point( 210, 30 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeSize_SearchWithTwoIcon() {
    text = new Text( shell, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL );
    text.setText( "This is a long long text!" );
    assertEquals( new Point( 229, 30 ), text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testComputeTrim() {
    Text text = new Text( shell, SWT.SINGLE );
    Rectangle expected = new Rectangle( -10, -5, 20, 10 );
    assertEquals( expected, text.computeTrim( 0, 0, 0, 0 ) );
    expected = new Rectangle( 0, 5, 120, 110 );
    assertEquals( expected, text.computeTrim( 10, 10, 100, 100 ) );

    text = new Text( shell, SWT.H_SCROLL );
    expected = new Rectangle( -10, -5, 21, 20 );
    assertEquals( expected, text.computeTrim( 0, 0, 0, 0 ) );
    expected = new Rectangle( 0, 5, 121, 120 );
    assertEquals( expected, text.computeTrim( 10, 10, 100, 100 ) );

    text = new Text( shell, SWT.BORDER );
    expected = new Rectangle( -11, -6, 23, 12);
    assertEquals( 1, text.getBorderWidth() );
    assertEquals( expected, text.computeTrim( 0, 0, 1, 0 ) );
  }

  @Test
  public void testGetCaretPosition() {
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "Sample text" );
    assertEquals( 0, text.getCaretPosition() );
    text.setSelection( 5 );
    assertEquals( 5, text.getCaretPosition() );
    text.setSelection( 3, 8 );
    assertEquals( 3, text.getCaretPosition() );
    text.setSelection( 8, 5 );
    assertEquals( 5, text.getCaretPosition() );
    text.setText( "New text" );
    assertEquals( 0, text.getCaretPosition() );
    text.setSelection( 3, 8 );
    text.clearSelection();
    assertEquals( new Point( 8, 8 ), text.getSelection() );
    assertEquals( 8, text.getCaretPosition() );
  }

  @Test
  public void testGetText() {
    text.setText( "Test Text" );
    assertEquals( "Test", text.getText( 0, 3 ) );
    assertEquals( "", text.getText( 5, 4 ) );
    assertEquals( "s", text.getText( 2, 2 ) );
    assertEquals( "Test Text", text.getText( 0, 25 ) );
    assertEquals( "Test ", text.getText( -3, 4 ) );
  }

  @Test
  public void testMessage() {
    Text text = new Text( shell, SWT.SINGLE );
    assertEquals( "", text.getMessage() );
    text.setMessage( "New message" );
    assertEquals( "New message", text.getMessage() );
  }

  @Test
  public void testStyle() {
    Text text = new Text( shell, SWT.SEARCH | SWT.PASSWORD );
    int style = text.getStyle();
    assertTrue( ( style & SWT.SINGLE ) != 0 );
    assertTrue( ( style & SWT.BORDER ) != 0 );
    assertTrue( ( style & SWT.PASSWORD ) == 0 );
  }

  @Test
  public void testEchoChar() {
    // single line text field
    Text singleText = new Text( shell, SWT.NONE );
    assertEquals( ( char )0, singleText.getEchoChar() );
    singleText.setEchoChar( '?' );
    assertEquals( '?', singleText.getEchoChar() );
    // multi line text field
    Text multiText = new Text( shell, SWT.MULTI );
    assertEquals( ( char )0, multiText.getEchoChar() );
    multiText.setEchoChar( '?' );
    assertEquals( ( char )0, multiText.getEchoChar() );
    // password text field
    Text passwordText = new Text( shell, SWT.PASSWORD );
    assertEquals( '?', passwordText.getEchoChar() );
    passwordText.setEchoChar( '*' );
    assertEquals( '*', passwordText.getEchoChar() );
  }

  @Test
  public void testIconStyles() {
    Text text = new Text( shell, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH );
    assertTrue( ( text.getStyle() & SWT.ICON_CANCEL ) != 0 );
    assertTrue( ( text.getStyle() & SWT.ICON_SEARCH ) != 0 );
  }

  @Test
  public void testSetTextChars() {
    char[] expected = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
    text.setTextChars( expected );
    char[] result = text.getTextChars();
    assertEquals( expected.length, result.length );
    for( int i = 0; i < expected.length; i++ ) {
      assertEquals( expected[ i ], result[ i ] );
    }
    assertEquals( "password", text.getText() );
  }

  @Test
  public void testSetTextChars_NullValue() {
    try {
      text.setTextChars( null );
      fail( "No exception thrown for chars == null" );
    } catch( IllegalArgumentException e ) {
    }
  }

  @Test
  public void testSetTextChars_EmptyArray() {
    char[] expected = new char[ 0 ];
    text.setTextChars( expected );
    char[] result = text.getTextChars();
    assertEquals( 0, result.length );
    assertEquals( "", text.getText() );
  }

  @Test
  public void testGetTextChars_FromText() {
    String string = "new string";
    text.setText( string );
    char[]result = text.getTextChars();
    for( int i = 0; i < string.length(); i++ ) {
      assertEquals( string.charAt( i ), result[ i ] );
    }
  }

  @Test
  public void testIsSerializable() throws Exception {
    text.setText( "foo" );

    Text deserializedText = Fixture.serializeAndDeserialize( text );

    assertEquals( text.getText(), deserializedText.getText() );
  }

  @Test
  public void testSelectionAfterInsertText() {
    text.setText( "foobar" );
    text.setSelection( 3 );

    text.insert( "xxx" );

    assertEquals( new Point( 6, 6 ), text.getSelection() );
  }

  @Test
  public void testAddModifyListenerRegistersUntypedEvents() {
    text.addModifyListener( mock( ModifyListener.class ) );

    assertTrue( text.isListening( SWT.Modify ) );
  }

  @Test
  public void testRemoveModifyListenerUnregistersUntypedEvents() {
    ModifyListener listener = mock( ModifyListener.class );
    text.addModifyListener( listener );

    text.removeModifyListener( listener );

    assertFalse( text.isListening( SWT.Modify ) );
  }

  @Test
  public void testAddVerifyListenerRegistersUntypedEvents() {
    text.addVerifyListener( mock( VerifyListener.class ) );

    assertTrue( text.isListening( SWT.Verify ) );
  }

  @Test
  public void testRemoveVerifyListenerUnregistersUntypedEvents() {
    VerifyListener listener = mock( VerifyListener.class );
    text.addVerifyListener( listener );

    text.removeVerifyListener( listener );

    assertFalse( text.isListening( SWT.Verify ) );
  }

  @Test
  public void testAddSelectionListener() {
    Text text = new Text( shell, SWT.NONE );

    text.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( text.isListening( SWT.Selection ) );
    assertTrue( text.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    Text text = new Text( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    text.addSelectionListener( listener );

    text.removeSelectionListener( listener );

    assertFalse( text.isListening( SWT.Selection ) );
    assertFalse( text.isListening( SWT.DefaultSelection ) );
  }

}
