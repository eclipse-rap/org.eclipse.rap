/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
 * Note:
 * As long as Combo uses a ListModel to maintain its items and selection,
 * most of the add/remove/getItem.../selection test cases can be omitted.
 * They are covered in List_Test
 */
public class Combo_Test {

  protected boolean listenerCalled;
  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDeselect() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "item1" );
    combo.add( "item2" );
    combo.add( "item3" );
    combo.select( 1 );

    combo.deselect( 1 );

    assertEquals( -1, combo.getSelectionIndex() );
  }

  @Test
  public void testDeselectWithWrongIndex() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "item1" );
    combo.add( "item2" );
    combo.add( "item3" );
    combo.select( 1 );

    combo.deselect( 0 );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testGetText() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
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
    // testing editable combobox
    combo = new Combo( shell, SWT.DROP_DOWN );
    String[] cases = {
      "", "fred", "fredfred"
    };
    for( int i = 0; i < cases.length; i++ ) {
      combo.setText( cases[ i ] );
      assertTrue( ":a:" + String.valueOf( i ),
                  cases[ i ].equals( combo.getText() ) );
    }
  }

  @Test
  public void testIndexOf() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
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
    Combo combo = new Combo( shell, SWT.READ_ONLY );
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
    Combo combo = new Combo( shell, SWT.DROP_DOWN );
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
  public void testSetTextForReadOnly() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "item0" );
    combo.add( "item1" );
    combo.select( 0 );
    combo.setText( "" );
    assertEquals( "item0", combo.getText() );
    combo.setText( "item1" );
    assertEquals( "item1", combo.getText() );
  }

  @Test
  public void testTextLimit() {
    Combo combo = new Combo( shell, SWT.DROP_DOWN );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    try {
      combo.setTextLimit( 0 );
      fail( "No exception thrown for textLimit == 0" );
    } catch( IllegalArgumentException e ) {
    }
    combo.setTextLimit( -7 );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    combo.setTextLimit( 10 );
    assertEquals( 10, combo.getTextLimit() );
    combo.setTextLimit( -10 );
    assertEquals( Combo.LIMIT, combo.getTextLimit() );
    combo.setText( "Sample_text" );
    combo.setTextLimit( 6 );
    assertEquals( "Sample_text", combo.getText() );
    combo.setText( "Other_text" );
    assertEquals( "Other_", combo.getText() );
  }

  @Test
  public void testSelection() {
    Combo combo = new Combo( shell, SWT.NONE );
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
  public void testSelection2() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.select( 0 );
    assertEquals( "test", combo.getText() );
    combo.remove( 0 );
    combo.add( "foo" );
    combo.select( 0 );
    assertEquals( "foo", combo.getText() );
    combo = new Combo( shell, SWT.READ_ONLY );
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
  public void testSelection3() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.setText( "foo" );
    combo.removeAll();
    assertEquals( "", combo.getText() );
    combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.setText( "foo" );
    combo.removeAll();
    assertEquals( "", combo.getText() );
    combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.remove( 1 );
    assertEquals( "", combo.getText() );
    combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.remove( "test1" );
    assertEquals( "", combo.getText() );
    combo.removeAll();
    combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 1 );
    combo.setText( "foo" );
    combo.remove( 1 );
    assertEquals( "foo", combo.getText() );
    combo.removeAll();
    combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.add( "test3" );
    combo.select( 1 );
    combo.remove( 1, 3 );
    assertEquals( "", combo.getText() );
  }

  @Test
  public void testSelection_RemoveSingleItemAdjustSelection() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test0" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 2 );

    combo.remove( 0 );

    assertEquals( 1, combo.getSelectionIndex() );
  }

  @Test
  public void testSelection_RemoveMultipleItemsAdjustSelection() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test0" );
    combo.add( "test1" );
    combo.add( "test2" );
    combo.select( 2 );

    combo.remove( 0, 1 );

    assertEquals( 0, combo.getSelectionIndex() );
  }

  @Test
  public void testSelectWithInvalidIndex() {
    Combo combo = new Combo( shell, SWT.NONE );
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
    assertEquals( 1, combo.getSelectionIndex() );
    assertEquals( "test1", combo.getText() );
  }

  @Test
  public void testSetTextSelect() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test2" );
    combo.add( "test3" );
    combo.setText( "foo" );
    combo.select( 1 );
    assertEquals( "test2", combo.getText() );
  }

  @Test
  public void testTextSelection() {
    Combo combo = new Combo( shell, SWT.NONE );
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
  public void testRemoveAll() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "2" );
    combo.removeAll();
    assertEquals( 0, combo.getItems().length );
  }

  @Test
  public void testRemoveAllForReadOnly() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "item" );
    combo.select( 0 );
    assertEquals( "item", combo.getText() ); // precondition
    combo.removeAll();
    assertEquals( "", combo.getText() );
    assertEquals( 0, combo.getItemCount() );
  }

  @Test
  public void testRemoveForReadOnly() {
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "item" );
    combo.select( 0 );
    assertEquals( "item", combo.getText() ); // precondition
    combo.remove( 0 );
    assertEquals( "", combo.getText() );
  }

  @Test
  public void testRemoveOutOfRange() {
    final Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test1" );
    combo.add( "test2" );
    try {
      combo.remove( 0, 100 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      combo.remove( -2, 2 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      combo.remove( 2, 0 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      combo.remove( -1, -2 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDispose() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.dispose();
    assertTrue( combo.isDisposed() );
  }

  @Test
  public void testAddModifyListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Combo combo = new Combo( shell, SWT.NONE );
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
  public void testAddModifyListenerReadOnly() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Combo combo = new Combo( shell, SWT.READ_ONLY );
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
  public void testAddModifyListenerWithNullArgument() {
    Combo combo = new Combo( shell, SWT.NONE );
    try {
      combo.addModifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddModifyListenerRegistersUntypedEvents() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.addModifyListener( mock( ModifyListener.class ) );

    assertTrue( combo.isListening( SWT.Modify ) );
  }

  @Test
  public void testRemoveModifyListenerUnregistersUntypedEvents() {
    Combo combo = new Combo( shell, SWT.NONE );
    ModifyListener listener = mock( ModifyListener.class );
    combo.addModifyListener( listener );

    combo.removeModifyListener( listener );

    assertFalse( combo.isListening( SWT.Modify ) );
  }

  @Test
  public void testAddVerifyListenerWithNullArgument() {
    Combo combo = new Combo( shell, SWT.NONE );
    try {
      combo.addVerifyListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddVerifyListenerRegistersUntypedEvents() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.addVerifyListener( mock( VerifyListener.class ) );

    assertTrue( combo.isListening( SWT.Verify ) );
  }

  @Test
  public void testRemoveVerifyListenerUnregistersUntypedEvents() {
    Combo combo = new Combo( shell, SWT.NONE );
    VerifyListener listener = mock( VerifyListener.class );
    combo.addVerifyListener( listener );

    combo.removeVerifyListener( listener );

    assertFalse( combo.isListening( SWT.Verify ) );
  }

  @Test
  public void testVerifyEvent() {
    VerifyListener verifyListener;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<TypedEvent> log = new ArrayList<TypedEvent>();
    final Combo combo = new Combo( shell, SWT.NONE );
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
    Combo combo = new Combo( shell, SWT.NONE );
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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Combo combo = new Combo( shell, SWT.NONE );
    Point expected = new Point( 66, 28 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    combo = new Combo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "22" );
    combo.add( "333" );
    expected = new Point( 82, 28 );
    assertEquals( expected, combo.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 102, 102 );
    assertEquals( expected, combo.computeSize( 100, 100 ) );
  }

  @Test
  public void testSetTextAndSelection() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Combo combo = new Combo( shell, SWT.NONE );
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
  public void testListVisible() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.setListVisible( true );
    assertTrue( combo.getListVisible() );
    combo.setListVisible( false );
    assertFalse( combo.getListVisible() );
  }

  @Test
  public void testGetTextHeight() {
    Combo combo = new Combo( shell, SWT.NONE );
    // default theme font is 11px
    assertEquals( 16, combo.getTextHeight() );
    combo.setFont( new Font( display, "Helvetica", 12, SWT.NORMAL ) );
    assertEquals( 14, combo.getTextHeight() );
    combo.setFont( null );
    assertEquals( 16, combo.getTextHeight() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    String item = "foo";
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( item );

    Combo deserializedCombo = Fixture.serializeAndDeserialize( combo );

    assertEquals( item, deserializedCombo.getItem( 0 ) );
  }

  @Test
  public void testSelectionIndex() {
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test" );
    combo.add( "test" );
    assertEquals( -1, combo.getSelectionIndex() );

    combo.select( 0 );
    assertEquals( 0, combo.getSelectionIndex() );

    combo.select( 1 );
    assertEquals( 1, combo.getSelectionIndex() );

    combo.select( 2 );
    assertEquals( 2, combo.getSelectionIndex() );
  }

  @Test
  public void testAddSelectionListener() {
    Combo combo = new Combo( shell, SWT.NONE );

    combo.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( combo.isListening( SWT.Selection ) );
    assertTrue( combo.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    Combo combo = new Combo( shell, SWT.NONE );
    try {
      combo.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListener() {
    Combo combo = new Combo( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    combo.addSelectionListener( listener );

    combo.removeSelectionListener( listener );

    assertFalse( combo.isListening( SWT.Selection ) );
    assertFalse( combo.isListening( SWT.DefaultSelection ) );
  }
}

