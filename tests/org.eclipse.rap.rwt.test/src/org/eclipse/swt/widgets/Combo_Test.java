/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/*
 * Note:
 * As long as Combo uses a ListModel to maintain its items and selection,
 * most of the add/remove/getItem.../selection test cases can be omitted.
 * They are covered in List_Test 
 */
public class Combo_Test extends TestCase {

  public void testDeselect() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
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
  
  public void testGetText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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

  public void testIndexOf() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.READ_ONLY );
    combo.add( "string0" );
    try {
      combo.indexOf( null );
      fail( "No exception thrown for string == null" );
    } catch( IllegalArgumentException e ) {
    }
    combo.removeAll();
    int number = 5;
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    for( int i = 0; i < number; i++ )
      assertEquals( i, combo.indexOf( "fred" + i ) );
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    combo.removeAll();
    for( int i = 0; i < number; i++ )
      assertEquals( -1, combo.indexOf( "fred" + i ) );
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    combo.remove( "fred3" );
    for( int i = 0; i < 3; i++ )
      assertEquals( i, combo.indexOf( "fred" + i ) );
    assertEquals( -1, combo.indexOf( "fred3" ) );
    for( int i = 4; i < number; i++ )
      assertEquals( i - 1, combo.indexOf( "fred" + i ) );
    combo.removeAll();
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    combo.remove( 2 );
    for( int i = 0; i < 2; i++ )
      assertEquals( i, combo.indexOf( "fred" + i ) );
    assertEquals( -1, combo.indexOf( "fred2" ) );
    for( int i = 3; i < number; i++ )
      assertEquals( i - 1, combo.indexOf( "fred" + i ) );
  }

  public void testIndexOfI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    for( int i = 0; i < number; i++ )
      assertTrue( ":a:" + i, combo.indexOf( "fred" + i, 0 ) == i );
    for( int i = 0; i < number; i++ )
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, i + 1 ) == -1 );
    for( int i = 0; i < number; i++ )
      combo.add( "fred" + i );
    for( int i = 0; i < 3; i++ )
      assertTrue( ":a:" + i, combo.indexOf( "fred" + i, 0 ) == i );
    for( int i = 3; i < number; i++ )
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, 3 ) == i );
    for( int i = 0; i < number; i++ )
      assertTrue( ":b:" + i, combo.indexOf( "fred" + i, i ) == i );
  }

  public void testSetText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
	
  public void testSelection() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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

  public void testSelection2() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.select( 0 );
    assertEquals( "test", combo.getText() );
    combo.remove( 0 );
    // XXX: [bm] normal swt windows behavior: calling removeAll clears textfield,
    // just removing the item in question leaves the text - should we support this?
//    assertEquals( "test", combo.getText() );
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

  public void testSelection3() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    combo.setText( "foo" );
    combo.remove( 1 );
    assertEquals( "", combo.getText() );
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

  public void testSetTextSelect() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.add( "test2" );
    combo.add( "test3" );
    combo.setText( "foo" );
    combo.select( 1 );
    assertEquals( "test2", combo.getText() );
  }
  public void testRemoveAll() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "1" );
    combo.add( "2" );
    combo.removeAll();
    assertEquals( 0, combo.getItems().length );
  }
  
  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.dispose();
    assertTrue( combo.isDisposed() );
  }

  protected boolean listenerCalled;
  public void testAddModifyListener() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    
    boolean exceptionThrown = false;
    
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    
    ModifyListener listener = new ModifyListener() {

      public void modifyText( ModifyEvent event ) {
        listenerCalled = true;
      }
    };
    try {
      combo.addModifyListener( null );
    } catch( IllegalArgumentException e ) {
      exceptionThrown = true;
    }
    assertTrue( "Expected exception not thrown", exceptionThrown );
    exceptionThrown = false;
    // test whether all content modifying API methods send a Modify event
    combo.addModifyListener( listener );
    listenerCalled = false;
    combo.setText( "new text" );
    assertTrue( "setText does not send event", listenerCalled );
    listenerCalled = false;
    combo.removeModifyListener( listener );
    // cause to call the listener.
    combo.setText( "line" );
    assertTrue( "Listener not removed", listenerCalled == false );
    try {
      combo.removeModifyListener( null );
    } catch( IllegalArgumentException e ) {
      exceptionThrown = true;
    }
    assertTrue( "Expected exception not thrown", exceptionThrown );
  }
  
  public void testVisibleItemCount() {
	  Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
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
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
