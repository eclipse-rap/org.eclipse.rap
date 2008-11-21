/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.Point;

public class ToolBar_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testToolBarCreation() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    ToolItem item0 = new ToolItem( toolBar, SWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );
    assertEquals( 1, toolBar.getItems().length );
    assertEquals( item0, toolBar.getItem( 0 ) );
    assertEquals( item0, toolBar.getItems()[ 0 ] );
    try {
      toolBar.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item0.getDisplay() );
    item0.dispose();
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    item0 = new ToolItem( toolBar, SWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );

    // search operation indexOf
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem item2 = new ToolItem( toolBar, SWT.RADIO );
    item2.setImage(Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertSame( Graphics.getImage( RWTFixture.IMAGE1 ), item2.getImage() );
    assertEquals( 3, toolBar.getItemCount() );
    assertEquals( 3, toolBar.getItems().length );
    assertEquals( 1, toolBar.indexOf( item1 ) );
    assertEquals( item1, toolBar.getItem( 1 ) );
    assertEquals( item2, toolBar.getItem( 2 ) );
    ToolItem item3 = new ToolItem( toolBar, SWT.SEPARATOR );
    item3.setImage( Graphics.getImage( RWTFixture.IMAGE2 ) );
    assertNull( item3.getImage() );
  }

  public void testToolBarDispose() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    ToolItem item0 = new ToolItem( toolBar, SWT.CHECK );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    assertEquals( 2, toolBar.getItems().length );
    toolBar.dispose();
    assertTrue( toolBar.isDisposed() );
    assertTrue( item0.isDisposed() );
    assertTrue( item1.isDisposed() );
  }

  public void testIndexOf() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar bar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( bar, SWT.NONE );
    assertEquals( 0, bar.indexOf( item ) );

    item.dispose();
    try {
      bar.indexOf( item );
      fail( "indexOf must not answer for a disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      bar.indexOf( null );
      fail( "indexOf must not answer for null item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testToolItemTexts() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    String text0 = "text0";
    String text1 = "text1";

    // Test 'normal' tool item
    item.setText( text0 );
    assertEquals( text0, item.getText() );
    item.setText( text1 );
    assertEquals( text1, item.getText() );
    // Test separator tool item
    assertEquals( "", separator.getText() );
    separator.setText( text1 );
    assertEquals( "", separator.getText() );
  }

  public void testToolItemImage() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    item.setImage( null );
    assertEquals( null, item.getImage() );
  }

  public void testToolItemEnabled() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // ToolItem must be enabled initially
    assertEquals( true, item.getEnabled() );

    // Test enabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( true );
    assertEquals( true, item.getEnabled() );
    assertEquals( false, item.isEnabled() );

    // Test disabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( false );
    assertEquals( false, item.getEnabled() );
    assertEquals( false, item.isEnabled() );

    // Test SEPARATOR ToolItem
    separator.setEnabled( false );
    assertEquals( true, separator.getControl().getEnabled() );
  }

  public void testToolItemControl() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // Using control property on ToolItem without SEPARATOR style has no effect
    item.setControl( new Text( toolbar, SWT.NONE ) );
    assertEquals( null, item.getControl() );

    // Setting a valid control on a SEPARATOR ToolItem
    Text control = new Text( toolbar, SWT.NONE );
    separator.setControl( control );
    assertSame( control, separator.getControl() );
    separator.setControl( null );
    assertEquals( null, separator.getControl() );

    // Illegal values for setControl
    Control currentControl = new Text( toolbar, SWT.NONE );
    try {
      separator.setControl( currentControl );
      Control diposedControl = new Text( toolbar, SWT.NONE );
      diposedControl.dispose();
      separator.setControl( diposedControl );
      fail( "Must not allow to set diposed control in setControl" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }
    try {
      separator.setControl( currentControl );
      Control shellControl = new Text( shell, SWT.NONE );
      shellControl.dispose();
      separator.setControl( shellControl );
      fail( "Must not allow to set control with other parent than ToolItem" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }

    // Dispose of control that is currently set on the SEPARATOR
    Control tempControl = new Text( toolbar, SWT.NONE );
    separator.setControl( tempControl );
    tempControl.dispose();
    assertEquals( null, separator.getControl() );
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    Point expected = new Point( 64, 64 );
    assertEquals( expected, toolbar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );
    expected = new Point( 16, 22 );
    assertEquals( expected, toolbar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, toolbar.computeSize( 100, 100 ) );
  }
}
