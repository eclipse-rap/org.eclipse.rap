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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.coolbarkit.CoolBarLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class CoolBar_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private CoolBar coolBar;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    coolBar = new CoolBar( shell, SWT.NONE );
  }

  @Test
  public void testHierarchy() {
    assertTrue( Composite.class.isAssignableFrom( coolBar.getClass() ) );
    assertSame( shell, coolBar.getParent() );
    assertSame( display, coolBar.getDisplay() );

    CoolItem item = new CoolItem( coolBar, SWT.NONE );
    assertEquals( 1, coolBar.getItemCount() );
    assertSame( display, item.getDisplay() );
    assertSame( coolBar, item.getParent() );
  }

  @Test
  public void testItems() {
    assertEquals( 0, coolBar.getItemCount() );
    assertTrue( Arrays.equals( new CoolItem[ 0 ], coolBar.getItems() ) );

    CoolItem item = new CoolItem( coolBar, SWT.NONE );
    assertEquals( 1, coolBar.getItemCount() );
    assertSame( item, coolBar.getItems()[ 0 ] );
    assertSame( item, coolBar.getItem( 0 ) );
    assertEquals( 0, coolBar.indexOf( item ) );

    CoolBar anotherBar = new CoolBar( shell, SWT.NONE );
    CoolItem anotherItem = new CoolItem( anotherBar, SWT.NONE );
    assertEquals( -1, coolBar.indexOf( anotherItem ) );
  }

  @Test
  public void testIndexOnCreation() {
    CoolItem coolItem = new CoolItem( coolBar, SWT.NONE );
    coolItem.setText( "1" );
    assertSame( coolItem, coolBar.getItem( 0 ) );
    CoolItem coolItem2 = new CoolItem( coolBar, SWT.NONE, 0 );
    coolItem2.setText( "2" );
    assertSame( coolItem2, coolBar.getItem( 0 ) );
  }

  @Test
  public void testStyle() {
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.NO_FOCUS );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.H_SCROLL );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.FLAT | SWT.HORIZONTAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.VERTICAL );
    assertEquals( SWT.NO_FOCUS | SWT.VERTICAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.VERTICAL | SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.VERTICAL | SWT.FLAT | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.HORIZONTAL );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );

    coolBar = new CoolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.FLAT | SWT.LEFT_TO_RIGHT, coolBar.getStyle() );
  }

  @Test
  public void testIndexOf() {
    CoolItem item = new CoolItem( coolBar, SWT.NONE );
    assertEquals( 0, coolBar.indexOf( item ) );

    item.dispose();
    try {
      coolBar.indexOf( item );
      fail( "indexOf must not answer for a disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      coolBar.indexOf( null );
      fail( "indexOf must not answer for null item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDispose() {
    final java.util.List<Object> log = new ArrayList<Object>();
    DisposeListener disposeListener = new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        log.add( event.getSource() );
      }
    };
    coolBar.addDisposeListener( disposeListener );
    CoolItem item1 = new CoolItem( coolBar, SWT.NONE );
    item1.addDisposeListener( disposeListener );
    CoolItem item2 = new CoolItem( coolBar, SWT.NONE );
    item2.addDisposeListener( disposeListener );

    item1.dispose();
    assertTrue( item1.isDisposed() );
    assertEquals( 1, coolBar.getItemCount() );

    coolBar.dispose();
    assertTrue( coolBar.isDisposed() );
    assertTrue( item2.isDisposed() );

    assertSame( item1, log.get( 0 ) );
    assertSame( item2, log.get( 1 ) );
    assertSame( coolBar, log.get( 2 ) );
  }

  @Test
  public void testLocked() {
    assertFalse( coolBar.getLocked() );
    coolBar.setLocked( true );
    assertTrue( coolBar.getLocked() );
  }

  @Test
  public void testItemOrder() {
    new CoolItem( coolBar, SWT.NONE );
    new CoolItem( coolBar, SWT.NONE );

    // Test initial itemOrder -> matches the order in which the items are added
    assertEquals( 0, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 1, coolBar.getItemOrder()[ 1 ] );

    // Test setItemOrder with legal arguments
    coolBar.setItemOrder( new int[] { 1, 0 } );
    assertEquals( 0, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 1, coolBar.getItemOrder()[ 0 ] );

    // Test setItemOrder with illegal arguments
    int[] expectedItemOrder = coolBar.getItemOrder();
    try {
      coolBar.setItemOrder( null );
      fail( "setItemOrder must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertArrayEquals( expectedItemOrder, coolBar.getItemOrder() );
    }
    try {
      coolBar.setItemOrder( new int[] { 0, 5 } );
      fail( "setItemOrder must not allow argument with indics out of range" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertArrayEquals( expectedItemOrder, coolBar.getItemOrder() );
    }
    try {
      coolBar.setItemOrder( new int[] { 0, 0 } );
      fail( "setItemOrder must not allow argument with duplicate indices" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertArrayEquals( expectedItemOrder, coolBar.getItemOrder() );
    }
    try {
      coolBar.setItemOrder( new int[] { 1 } );
      String msg
        = "setItemOrder must not allow argument whose length doesn't match "
        + "the number of items";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertArrayEquals( expectedItemOrder, coolBar.getItemOrder() );
    }
  }

  @Test
  public void testComputeSize() {
    CoolBar coolBar = new CoolBar( shell, SWT.HORIZONTAL );
    Point expected = new Point( 0, 0 );
    assertEquals( expected, coolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    createItem( coolBar );
    expected = new Point( 178, 30 );
    assertEquals( expected, coolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    coolBar = new CoolBar( shell, SWT.VERTICAL );
    createItem( coolBar );
    expected = new Point( 168, 40 );
    assertEquals( expected, coolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    coolBar = new CoolBar( shell, SWT.FLAT );
    createItem( coolBar );
    expected = new Point( 178, 30 );
    assertEquals( expected, coolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, coolBar.computeSize( 100, 100 ) );
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    new CoolItem( coolBar, SWT.NONE );
    new CoolItem( coolBar, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    coolBar.setFont( font );
    coolBar.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    coolBar.dispose();
  }

  @Test
  public void testIsSerializable() throws Exception {
    new CoolItem( coolBar, SWT.NONE );

    CoolBar deserializedCoolBar = serializeAndDeserialize( coolBar );

    assertEquals( 1, deserializedCoolBar.getItemCount() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( coolBar.getAdapter( WidgetLCA.class ) instanceof CoolBarLCA );
    assertSame( coolBar.getAdapter( WidgetLCA.class ), coolBar.getAdapter( WidgetLCA.class ) );
  }

  @Test
  public void testSetDirection() {
    CoolItem coolItem = new CoolItem( coolBar, SWT.NONE );
    ToolBar control = new ToolBar( coolBar, SWT.NONE );
    coolItem.setControl( control );

    coolBar.setOrientation( SWT.RIGHT_TO_LEFT );

    assertEquals( SWT.RIGHT_TO_LEFT, coolBar.getOrientation() );
    assertEquals( SWT.RIGHT_TO_LEFT, control.getOrientation() );
  }

  private static CoolItem createItem( CoolBar coolBar ) {
    ToolBar toolBar = new ToolBar( coolBar, SWT.FLAT );
    for( int i = 0; i < 3; i++ ) {
      ToolItem item = new ToolItem( toolBar, SWT.PUSH );
      item.setText( "item " + i );
    }
    toolBar.pack();
    Point size = toolBar.getSize();
    CoolItem item = new CoolItem( coolBar, SWT.NONE );
    item.setControl( toolBar );
    Point preferred = item.computeSize( size.x, size.y );
    item.setPreferredSize( preferred );
    return item;
  }

}
