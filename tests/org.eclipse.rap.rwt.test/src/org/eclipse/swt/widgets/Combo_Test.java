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
    Combo combo = new Combo( shell, SWT.NONE );
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
