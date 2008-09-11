/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class ExpandBar_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( ExpandItem.CHEVRON_SIZE, expandBar.getBandHeight() );
    assertEquals( 4, expandBar.getSpacing() );
    assertEquals( 0, expandBar.getItemCount() );
    assertNull( expandBar.getBackgroundImage() );
    assertNull( expandBar.getMenu() );
  }

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( 0, expandBar.getItemCount() );
    assertEquals( 0, expandBar.getItems().length );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( 1, expandBar.getItemCount() );
    assertEquals( 1, expandBar.getItems().length );
    assertEquals( item, expandBar.getItem( 0 ) );
    assertEquals( item, expandBar.getItems()[ 0 ] );
    try {
      expandBar.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item.getDisplay() );
    item.dispose();
    assertEquals( 0, expandBar.getItemCount() );
    assertEquals( 0, expandBar.getItems().length );
    // search operation indexOf
    item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( 0, expandBar.indexOf( item ) );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) == 0 );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) == 0 );
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) != 0 );
    expandBar = new ExpandBar( shell, SWT.BORDER );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) != 0 );
    expandBar = new ExpandBar( shell, SWT.BORDER | SWT.V_SCROLL );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) != 0 );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) != 0 );
  }

  public void testBandHeight() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( ExpandItem.CHEVRON_SIZE, expandBar.getBandHeight() );
    Font font = Graphics.getFont( "font", 30, SWT.BOLD );
    expandBar.setFont( font );
    assertEquals( 34, expandBar.getBandHeight() );
  }

  public void testSpacing() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( 4, expandBar.getSpacing() );
    expandBar.setSpacing( 8 );
    assertEquals( 8, expandBar.getSpacing() );
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    expandBar.dispose();
    assertTrue( expandBar.isDisposed() );
    assertTrue( item.isDisposed() );
  }
}
