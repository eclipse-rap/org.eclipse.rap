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
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;

public class ExpandItem_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    // Add one item
    ExpandItem item1 = new ExpandItem( expandBar, SWT.NONE, 0 );
    assertEquals( 1, expandBar.getItemCount() );
    assertSame( item1, expandBar.getItem( 0 ) );
    // Insert an item before first item
    ExpandItem item0 = new ExpandItem( expandBar, SWT.NONE, 0 );
    assertEquals( 2, expandBar.getItemCount() );
    assertSame( item0, expandBar.getItem( 0 ) );
    // Try to add an item whit an index which is out of bounds
    try {
      new ExpandItem( expandBar, SWT.NONE, expandBar.getItemCount() + 8 );
      String msg = "Index out of bounds expected when creating an item with "
                   + "index > itemCount";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParent() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    // Test creating with valid parent
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    assertSame( expandBar, item.getParent() );
    // Test creating column without parent
    try {
      new ExpandItem( null, SWT.NONE );
      fail( "Must not allow to create ExpandItem with null-parent." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testDispose() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    for( int i = 0; i < 10; i++ ) {
      new ExpandItem( expandBar, SWT.NONE );
    }
    // force item to dispose it
    ExpandItem item = expandBar.getItem( 0 );
    item.dispose();
    assertEquals( 9, expandBar.getItemCount() );
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( "", item.getText() );
    item.setText( "abc" );
    assertEquals( "abc", item.getText() );
    item = new ExpandItem( expandBar, SWT.NONE );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertEquals( "", item.getText() );
  }

  public void testImage() {
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( null, item.getImage() );
    item.setImage( image );
    assertSame( image, item.getImage() );
  }

  public void testHeight() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    Composite composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout() );
    new Button( composite, SWT.PUSH ).setText( "SWT.PUSH" );
    new Button( composite, SWT.RADIO ).setText( "SWT.RADIO" );
    new Button( composite, SWT.CHECK ).setText( "SWT.CHECK" );
    new Button( composite, SWT.TOGGLE ).setText( "SWT.TOGGLE" );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    item.setText( "What is your favorite button?" );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    assertEquals( composite.getSize().y + ExpandItem.BORDER, item.getHeight() );
  }

  public void testHeaderHeight() {
    Display display = new Display();
    Shell shell = new Shell( display );
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    item.setText( "What is your favorite button?" );
    assertEquals( 24, item.getHeaderHeight() );
    item.setImage( display.getSystemImage( SWT.ICON_WARNING ) );
    assertEquals( 32, item.getHeaderHeight() );
    Font font = Graphics.getFont( "font", 30, SWT.BOLD );
    expandBar.setFont( font );
    assertEquals( 34, item.getHeaderHeight() );
  }
}
