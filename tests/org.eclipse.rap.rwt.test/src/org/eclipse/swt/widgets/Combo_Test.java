/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;

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
	
  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Combo combo = new Combo( shell, SWT.NONE );
    combo.add( "test" );
    combo.dispose();
    assertTrue( combo.isDisposed() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
