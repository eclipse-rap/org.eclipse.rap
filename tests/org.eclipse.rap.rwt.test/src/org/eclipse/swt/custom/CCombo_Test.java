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
package org.eclipse.swt.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
 * Note:
 * As long as CCombo uses a ListModel to maintain its items and selection,
 * most of the add/remove/getItem.../selection test cases can be omitted.
 * They are covered in List_Test
 */
public class CCombo_Test {

  private boolean listenerCalled;
  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDeselect() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "item1" );
    combo.add( "item2" );
    combo.add( "item3" );
    // deselect the currently selected index/item
    combo.select( 1 );
    combo.deselect( 1 );
    assertEquals( -1, combo.getSelectionIndex() );
    // deselect works only if the argument matches the currently selected index
    // (as far as I understand SWT doc/implementation...)
    combo.select( 1 );
    combo.deselect( 0 );
    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testGetText() {
    CCombo combo = new CCombo( shell, SWT.READ_ONLY );
    combo.add( "item1" );
    combo.add( "item2" );
    combo.add( "item3" );
    // test get text without setting an explicit selection
    assertEquals( combo.getText(), "" );
    // test after selection
    combo.select( 2 );
    assertEquals( combo.getText(), "item3" );
    // test after deselection
    combo.deselectAll();
    assertEquals( combo.getText(), "" );
    // testing editable CCombo
    combo = new CCombo( shell, SWT.NONE );
    String[] cases = { "", "fred", "fredfred" };
    for( int i = 0; i < cases.length; i++ ) {
      combo.setText( cases[ i ] );
      assertTrue( ":a:" + String.valueOf( i ),
                  cases[ i ].equals( combo.getText() ) );
    }
  }

  @Test
  public void testTextLimit() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setTextLimit( -1 );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    combo.setTextLimit( -20 );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    combo.setTextLimit( -12345 );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    combo.setTextLimit( 20 );
    assertEquals( 20, combo.getTextLimit() );
    try {
      combo.setTextLimit( 0 );
      fail( "Must not allow to set textLimit to zero" );
    } catch( IllegalArgumentException e ) {
      // as expected
    }
    combo.setText( "Sample_text" );
    combo.setTextLimit( 6 );
    assertEquals( "Sample_text", combo.getText() );
    combo.setText( "Other_text" );
    assertEquals( "Other_", combo.getText() );
  }

  @Test
  public void testListVisible() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setListVisible( true );
    assertTrue( combo.getListVisible() );
    combo.setListVisible( false );
    assertFalse( combo.getListVisible() );
  }

  @Test
  public void testSelection() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    // test clearSelection
    combo.setText( "abc" );
    combo.setSelection( new Point( 1, 3 ) );
    combo.clearSelection();
    assertEquals( new Point( 0, 0 ), combo.getSelection() );
    // test setSelection( a, b ), a < b
    combo.clearSelection();
    combo.setText( "test text" );
    combo.setSelection( new Point( 3, 6 ) );
    assertEquals( new Point( 3, 6 ), combo.getSelection() );
    // test setSelection( a, b ), a > b
    combo.clearSelection();
    combo.setSelection( new Point( 5, 2 ) );
    assertEquals( new Point( 2, 5 ), combo.getSelection() );
  }

  @Test
  public void testIndexOf() {
    CCombo combo = new CCombo( shell, SWT.READ_ONLY );
    combo.add( "string0" );
    try {
      combo.indexOf( null );
      fail( "No exception thrown for string == null" );
    } catch( IllegalArgumentException e ) {
    }
    combo.removeAll();
    int number = 5;
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    for( int i = 0; i < number; i++ ) {
      assertEquals( i, combo.indexOf( "fred" + i ) );
    }
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    combo.removeAll();
    for( int i = 0; i < number; i++ ) {
      assertEquals( -1, combo.indexOf( "fred" + i ) );
    }
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    combo.remove( "fred3" );
    for( int i = 0; i < 3; i++ ) {
      assertEquals( i, combo.indexOf( "fred" + i ) );
    }
    assertEquals( -1, combo.indexOf( "fred3" ) );
    for( int i = 4; i < number; i++ ) {
      assertEquals( i - 1, combo.indexOf( "fred" + i ) );
    }
    combo.removeAll();
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    combo.remove( 2 );
    for( int i = 0; i < 2; i++ ) {
      assertEquals( i, combo.indexOf( "fred" + i ) );
    }
    assertEquals( -1, combo.indexOf( "fred2" ) );
    for( int i = 3; i < number; i++ ) {
      assertEquals( i - 1, combo.indexOf( "fred" + i ) );
    }
  }

  @Test
  public void testIndexOfI() {
    CCombo combo = new CCombo( shell, SWT.READ_ONLY );
    combo.add( "string0" );
    try {
      combo.indexOf( null );
      fail( "No exception thrown for string == null" );
    } catch( IllegalArgumentException e ) {
    }
    assertEquals( -1, combo.indexOf( "string0", -1 ) );
    combo.removeAll();
    int number = 5;
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    for( int i = 0; i < number; i++ ) {
      assertTrue( ":a:" + i, combo.indexOf( "fred" + i, 0 ) == i );
    }
    for( int i = 0; i < number; i++ ) {
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, i + 1 ) == -1 );
    }
    for( int i = 0; i < number; i++ ) {
      combo.add( "fred" + i );
    }
    for( int i = 0; i < 3; i++ ) {
      assertTrue( ":a:" + i, combo.indexOf( "fred" + i, 0 ) == i );
    }
    for( int i = 3; i < number; i++ ) {
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, 3 ) == i );
    }
    for( int i = 0; i < number; i++ ) {
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, i ) == i );
    }
  }

  @Test
  public void testSetText() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    try {
      combo.setText( null );
      fail( "No exception thrown for text == null" );
    } catch( IllegalArgumentException e ) {
    }
    String[] cases = {
      "", "fred", "fred0"
    };
    for( int i = 0; i < cases.length; i++ ) {
      combo.setText( cases[ i ] );
      assertTrue( ":a:" + i, combo.getText().equals( cases[ i ] ) );
    }
    for( int i = 0; i < 5; i++ ) {
      combo.add( "fred" );
    }
    for( int i = 0; i < cases.length; i++ ) {
      combo.setText( cases[ i ] );
      assertTrue( ":b:" + i, combo.getText().equals( cases[ i ] ) );
    }
    for( int i = 0; i < 5; i++ ) {
      combo.add( "fred" + i );
    }
    for( int i = 0; i < cases.length; i++ ) {
      combo.setText( cases[ i ] );
      assertTrue( ":c:" + i, combo.getText().equals( cases[ i ] ) );
    }
  }

  @Test
  public void testSetTextReadOnly() {
    CCombo combo = new CCombo( shell, SWT.READ_ONLY );

    combo.setText( "foo" );

    assertEquals( "foo", combo.getText() );
  }

  @Test
  public void testSetTextNotEditable() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setEditable( false );

    combo.setText( "foo" );

    assertEquals( "foo", combo.getText() );
  }

  @Test
  public void testSetTextNotInItems() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setItems( new String[] { "a", "b", "c" } );
    combo.select( 1 );

    combo.setText( "foo" );

    assertEquals( "foo", combo.getText() );
    assertEquals( -1, combo.getSelectionIndex() );
  }

  @Test
  public void testSetTextInItems() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setItems( new String[] { "a", "b", "c" } );

    combo.setText( "b" );

    assertEquals( "b", combo.getText() );
    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testSelect() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.select( 0 );
    assertEquals( "test", combo.getText() );
    combo.removeAll();
    assertEquals( "", combo.getText() );
    combo.add( "foo" );
    combo.select( 0 );
    assertEquals( "foo", combo.getText() );
  }

  @Test
  public void testSelect2() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.select( 0 );
    assertEquals( "test", combo.getText() );
    combo.remove( 0 );
    combo.add( "foo" );
    combo.select( 0 );
    assertEquals( "foo", combo.getText() );
    combo = new CCombo( shell, SWT.READ_ONLY );
    combo.add( "test" );
    combo.select( 0 );
    assertEquals( "test", combo.getText() );
    combo.remove( 0 );
    assertEquals( "", combo.getText() );
    combo.add( "foo" );
    combo.select( 0 );
    assertEquals( "foo", combo.getText() );
  }

  @Test
  public void testSelect3() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.setText( "foo" );
    combo.removeAll();
    assertEquals( "", combo.getText() );
    combo = new CCombo( shell, SWT.READ_ONLY );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.setText( "foo" );
    combo.removeAll();
    assertEquals( "", combo.getText() );
    combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.remove( 1 );
    assertEquals( "", combo.getText() );
    combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.remove( "test1" );
    assertEquals( "", combo.getText() );
    combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.setText( "foo" );
    combo.remove( 1 );
    assertEquals( "foo", combo.getText() );
    combo.removeAll();
    combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.add( "test3" );
    combo.select( 1 );
    combo.remove( 1, 3 );
    assertEquals( "", combo.getText() );
  }

  @Test
  public void testSelectWithInvalidIndex() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    assertEquals( 1, combo.getSelectionIndex() );
    assertEquals( "test1", combo.getText() );
    combo.select( -2 );
    assertEquals( 1, combo.getSelectionIndex() );
    assertEquals( "test1", combo.getText() );
    combo.select( 10 );
    assertEquals( 1, combo.getSelectionIndex() );
    assertEquals( "test1", combo.getText() );
    combo.select( -1 );
    assertEquals( -1, combo.getSelectionIndex() );
    assertEquals( "", combo.getText() );
  }

  @Test
  public void testSetTextSelect() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test2" );
    combo.add( "test3" );
    combo.setText( "foo" );
    combo.select( 1 );
    assertEquals( "test2", combo.getText() );
  }

  @Test
  public void testRemoveAll() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "2" );
    combo.removeAll();
    assertEquals( 0, combo.getItems().length );
  }

  @Test
  public void testDispose() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.dispose();
    assertTrue( combo.isDisposed() );
  }

  @Test
  public void testAddModifyListener() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.setItems( new String [] { "A-1", "B-1", "C-1" } );
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        listenerCalled = true;
      }
    };
    try {
      combo.addModifyListener( null );
      fail( "removeModifyListener must not allow null listener" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // test whether all content modifying API methods send a Modify event
    combo.addModifyListener( listener );
    listenerCalled = false;
    combo.setText( "new text" );
    assertTrue( listenerCalled );
    listenerCalled = false;
 // select and deselect item(s) test cases
    combo.select( 1 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.deselect( 1 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.select( 0 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.deselectAll();
    assertTrue( listenerCalled );
    // remove item(s) test cases
    listenerCalled = false;
    combo.select(0);
    combo.remove(0);
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.remove("A-1");
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.remove(0,1);
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.removeAll();
    assertTrue( listenerCalled );
    //
    listenerCalled = false;
    combo.removeModifyListener( listener );
    // cause to call the listener.
    combo.setText( "line" );
    assertFalse( listenerCalled );
    try {
      combo.removeModifyListener( null );
      fail( "removeModifyListener must not allow null listener" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testAddModifyListenerWithNullArgument() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    try {
      combo.addModifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddModifyListenerReadOnly() {
    CCombo combo = new CCombo( shell, SWT.READ_ONLY );
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    ModifyListener listener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        listenerCalled = true;
      }
    };
    try {
      combo.addModifyListener( null );
      fail( "removeModifyListener must not allow null listener" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // test whether all content modifying API methods send a Modify event
    combo.addModifyListener( listener );
    listenerCalled = false;
 // select and deselect item(s) test cases
    combo.select( 1 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.deselect( 1 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.select( 0 );
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.deselectAll();
    assertTrue( listenerCalled );
    // remove item(s) test cases
    listenerCalled = false;
    combo.select(0);
    combo.remove(0);
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.remove("A-1");
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.remove(0,1);
    assertTrue( listenerCalled );
    listenerCalled = false;
    combo.setItems (new String [] {"A-1", "B-1", "C-1"});
    combo.select(0);
    combo.removeAll();
    assertTrue( listenerCalled );
    //
    listenerCalled = false;
    combo.removeModifyListener( listener );
    // cause to call the listener.
    combo.select( 2 );
    assertFalse( listenerCalled );
    try {
      combo.removeModifyListener( null );
      fail( "removeModifyListener must not allow null listener" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testAddModifyListenerRegistersUntypedEvents() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.addModifyListener( mock( ModifyListener.class ) );

    assertTrue( combo.isListening( SWT.Modify ) );
  }

  @Test
  public void testRemoveModifyListenerUnregistersUntypedEvents() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    ModifyListener listener = mock( ModifyListener.class );
    combo.addModifyListener( listener );

    combo.removeModifyListener( listener );

    assertFalse( combo.isListening( SWT.Modify ) );
  }

  @Test
  public void testAddVerifyListenerWithNullArgument() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    try {
      combo.addVerifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddVerifyListenerRegistersUntypedEvents() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.addVerifyListener( mock( VerifyListener.class ) );

    assertTrue( combo.isListening( SWT.Verify ) );
  }

  @Test
  public void testRemoveVerifyListenerUnregistersUntypedEvents() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    VerifyListener listener = mock( VerifyListener.class );
    combo.addVerifyListener( listener );

    combo.removeVerifyListener( listener );

    assertFalse( combo.isListening( SWT.Verify ) );
  }

  @Test
  public void testVerifyEvent() {
    VerifyListener verifyListener;
    final java.util.List<TypedEvent> log = new ArrayList<TypedEvent>();
    final CCombo combo = new CCombo( shell, SWT.NONE );
    combo.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        log.add( event );
      }
    } );
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        log.add( event );
      }
    } );

    // VerifyEvent is also sent when setting text to the already set value
    log.clear();
    combo.setText( "" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Test verifyListener that prevents (doit=false) change
    combo.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.doit = false;
      }
    };
    combo.addVerifyListener( verifyListener );
    combo.setText( "other" );
    assertEquals( 1, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( "", combo.getText() );
    combo.removeVerifyListener( verifyListener );

    // Test verifyListener that manipulates text
    combo.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = "manipulated";
      }
    };
    combo.addVerifyListener( verifyListener );
    combo.setText( "other" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    assertEquals( "manipulated", combo.getText() );
    combo.removeVerifyListener( verifyListener );

    // Ensure that VerifyEvent#start and #end denote the positions of the old
    // text and #text denotes the text to be set
    String oldText = "old";
    combo.setText( oldText );
    log.clear();
    String newText = oldText + "changed";
    combo.setText( newText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    VerifyEvent verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 0, verifyEvent.start );
    assertEquals( oldText.length(), verifyEvent.end );
    assertEquals( newText, verifyEvent.text );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Ensure that VerifyEvent#text denotes the text to be set
    // and not the cut by textLimit one
    combo.setTextLimit( 5 );
    String sampleText = "sample_text";
    log.clear();
    combo.setText( sampleText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( sampleText, verifyEvent.text );
  }

  @Test
  public void testVisibleItemCount() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "2" );
    combo.add( "3" );
    int visibleItemCount = combo.getVisibleItemCount();
    combo.setVisibleItemCount( -2 );
    assertEquals( visibleItemCount, combo.getVisibleItemCount() );
    combo.setVisibleItemCount( 2 );
    assertEquals( 2, combo.getVisibleItemCount() );
    combo.setVisibleItemCount( 3 );
    assertEquals( 3, combo.getVisibleItemCount() );
  }

  @Test
  public void testComputeSize() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    Point expected = new Point( 64, 26 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    combo = new CCombo( shell, SWT.BORDER );
    expected = new Point( 66, 28 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    combo = new CCombo( shell, SWT.FLAT );
    expected = new Point( 64, 26 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    combo = new CCombo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "22" );
    combo.add( "333" );
    expected = new Point( 80, 26 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, combo.computeSize( 100, 100 ) );
  }

  @Test
  public void testSetTextAndSelection() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.addVerifyListener( new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = event.text + "2";
      }
    } );

    combo.setText( "test" );

    assertEquals( 2, combo.getSelectionIndex() );
  }

  @Test
  public void testGetTextHeight() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    // default theme font is 11px
    assertEquals( 16, combo.getTextHeight() );
    combo.setFont( new Font( display, "Helvetica", 12, SWT.NORMAL ) );
    assertEquals( 14, combo.getTextHeight() );
    combo.setFont( null );
    assertEquals( 16, combo.getTextHeight() );
  }

  @Test
  public void testSelectionIsSerializable() throws Exception {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "0" );
    combo.add( "1" );
    combo.select( 1 );

    CCombo deserializedCombo = Fixture.serializeAndDeserialize( combo );

    assertEquals( "1", deserializedCombo.getText() );
    assertEquals( 1, deserializedCombo.getSelectionIndex() );
    assertEquals( "0", deserializedCombo.getItem( 0 ) );
    assertEquals( "1", deserializedCombo.getItem( 1 ) );
  }

  @Test
  public void testSelectionIndex() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test" );
    combo.add( "test" );
    assertEquals(-1, combo.getSelectionIndex() );

    combo.select( 0 );
    assertEquals( 0, combo.getSelectionIndex() );

    combo.select( 1 );
    assertEquals( 1, combo.getSelectionIndex() );

    combo.select( 2 );
    assertEquals( 2, combo.getSelectionIndex() );
  }

  @Test
  public void testAddSelectionListener() {
    CCombo combo = new CCombo( shell, SWT.NONE );

    combo.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( combo.isListening( SWT.Selection ) );
    assertTrue( combo.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    combo.addSelectionListener( listener );

    combo.removeSelectionListener( listener );

    assertFalse( combo.isListening( SWT.Selection ) );
    assertFalse( combo.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    CCombo combo = new CCombo( shell, SWT.NONE );
    try {
      combo.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
