/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.coolitemkit.CoolItemLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class CoolItem_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite shell;
  private CoolBar coolBar;
  private CoolItem coolItem;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    coolBar = new CoolBar( shell, SWT.NONE );
    coolItem = new CoolItem( coolBar, SWT.NONE );
  }

  @Test
  public void testSetControl() {
    Button button = new Button( coolBar, SWT.NONE );

    coolItem.setControl( button );
    assertSame( button, coolItem.getControl() );

    coolItem.setControl( null );
    assertEquals( null, coolItem.getControl() );

    try {
      Button disposedButton = new Button( coolBar, SWT.PUSH );
      disposedButton.dispose();
      coolItem.setControl( disposedButton );
      fail( "Must not allow to set disposed control" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Button shellButton = new Button( shell, SWT.PUSH );
      coolItem.setControl( shellButton );
      fail( "Must not allow to set control with a parent other than CoolBar" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSize() {
    coolBar.setBounds( 0, 0, 100, 100 );
    Point size = new Point( 80, 30 );
    coolItem.setPreferredSize( size );
    coolBar.relayout();
    assertEquals( new Point( 100 , 30 ), coolItem.getSize() );
    assertNotSame( size, coolItem.getSize() );

    coolItem.setPreferredSize( -2, -1 );
    coolBar.relayout();
    assertEquals( CoolItem.MINIMUM_WIDTH , coolItem.getPreferredSize().x );
    assertEquals( 0, coolItem.getSize().y );
  }

  // TODO: review this - test breaks but would also break with swt
//  public void testBoundsHorizontal() {
//    CoolBar horizontalBar = new CoolBar( shell, SWT.HORIZONTAL );
//    CoolItem item1 = new CoolItem( horizontalBar, SWT.NONE );
//    item1.setSize( 20, 10 );
//    CoolItem item2 = new CoolItem( horizontalBar, SWT.NONE );
//    item2.setSize( 30, 10 );
//    CoolItem item3 = new CoolItem( horizontalBar, SWT.NONE );
//    item3.setSize( 40, 10 );
//
//    Rectangle expected = new Rectangle( 0, 0, 20 + CoolItem.HANDLE_SIZE, 10 );
//    assertEquals( expected, item1.getBounds() );
//    expected = new Rectangle( 20 + CoolItem.HANDLE_SIZE,
//                              0,
//                              30 + CoolItem.HANDLE_SIZE,
//                              10 );
//    assertEquals( expected, item2.getBounds() );
//    expected = new Rectangle( 50 + CoolItem.HANDLE_SIZE + CoolItem.HANDLE_SIZE,
//                              0,
//                              40 + CoolItem.HANDLE_SIZE,
//                              10 );
//    assertEquals( expected, item3.getBounds() );
//  }

  // TODO: review this - test breaks but would also break with swt
//  public void testBoundsVertical() {
//    CoolBar bar = new CoolBar( shell, SWT.VERTICAL );
//    CoolItem item1 = new CoolItem( bar, SWT.NONE );
//    item1.setSize( 10, 20 );
//    CoolItem item2 = new CoolItem( bar, SWT.NONE );
//    item2.setSize( 10, 30 );
//    CoolItem item3 = new CoolItem( bar, SWT.NONE );
//    item3.setSize( 10, 40 );
//
//    Rectangle expected = new Rectangle( 0, 0, 20 + CoolItem.HANDLE_SIZE, 10 );
//    assertEquals( expected, item1.getBounds() );
//    expected = new Rectangle( 0,
//                              20 + CoolItem.HANDLE_SIZE,
//                              30 + CoolItem.HANDLE_SIZE,
//                              10 );
//    assertEquals( expected, item2.getBounds() );
//    expected = new Rectangle( 0,
//                              50 + CoolItem.HANDLE_SIZE + CoolItem.HANDLE_SIZE,
//                              40 + CoolItem.HANDLE_SIZE,
//                              10 );
//    assertEquals( expected, item3.getBounds() );
//  }

  @Test
  public void test_getBounds() {
    Button button = new Button( coolBar, SWT.PUSH );
    button.setText( "foo" );
    coolItem.setControl( button );

    Rectangle rect = coolItem.getBounds();
    Point size = coolItem.getSize();
    assertEquals( size.x, rect.width );
    assertEquals( size.y, rect.height );
  }

  @Test
  public void test_computeSizeII() {
    Button button = new Button(coolBar, SWT.PUSH);
    button.setText("foo");

    Point size = coolItem.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    coolItem.setControl(button);
    Point size2 = coolItem.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    assertTrue(size2.x == size.x);

    size = coolItem.computeSize(50, 25);
    size2 = coolItem.computeSize(100, 25);
    assertEquals(size.x + 50, size2.x);
    assertEquals(size.y, size2.y);

    size = coolItem.computeSize(1,1);
    size2 = coolItem.computeSize(26, 26);
    assertEquals(25, size2.x - size.x);
  }

  @Test
  public void test_getControl() {
    assertNull(coolItem.getControl());

    Button button = new Button(coolBar, SWT.PUSH);
    coolItem.setControl(button);
    Control control = coolItem.getControl();
    assertEquals(button, control);

    button = new Button(coolBar, SWT.PUSH);
    coolItem.setControl(button);
    control = coolItem.getControl();
    assertEquals(button, control);
  }

  @Test
  public void test_getParent() {
    assertEquals(coolBar, coolItem.getParent());
  }

  @Test
  public void testDisplay() {
    assertSame( display, coolItem.getDisplay() );
    assertSame( coolBar.getDisplay(), coolItem.getDisplay() );
  }

  @Test
  public void testRemoveNonExistingSelectionListener() {
    coolItem.removeSelectionListener( new SelectionAdapter() {} );
    // no assert: must silently ignore attempt to remove unknown listener
  }

  @Test
  public void test_setPreferredSizeLorg_eclipse_swt_graphics_Point() {
    Button button = new Button(coolBar, SWT.PUSH);
    button.setText("foobar");
    coolItem.setControl(button);

    Point size = new Point(50, 30);
    coolItem.setPreferredSize(size);
    Point size2 = coolItem.getPreferredSize();
    coolItem.setPreferredSize(50, 30);
    assertEquals(size2, coolItem.getPreferredSize());
  }


  @Test
  public void test_setSizeLorg_eclipse_swt_graphics_Point() {
    Button button = new Button(coolBar, SWT.PUSH);
    button.setText("foo");
    coolItem.setControl(button);

    Point size = new Point(50, 50);
    coolItem.setSize(size);
    Point size2 = coolItem.getSize();
    coolItem.setSize(50, 50);
    assertEquals(size2, coolItem.getSize());
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( coolItem.getAdapter( WidgetLCA.class ) instanceof CoolItemLCA );
    assertSame( coolItem.getAdapter( WidgetLCA.class ), coolItem.getAdapter( WidgetLCA.class ) );
  }

}
