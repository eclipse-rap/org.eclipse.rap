/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Point;


public class Text_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, RWT.NONE );
    assertEquals( "", text.getText() );
    assertEquals( -1, text.getTextLimit() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
  }
  
  public void testTextLimit() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, RWT.NONE );
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
  }
  
  public void testSelection() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, RWT.NONE );
    
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
}
