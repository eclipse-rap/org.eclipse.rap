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

import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;


public class CoolBar_Test extends TestCase {
  
  public void testHierarchy() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    
    assertTrue( Composite.class.isAssignableFrom( bar.getClass() ) );
    assertSame( shell, bar.getParent() );
    assertSame( display, bar.getDisplay() );

    CoolItem item = new CoolItem( bar, SWT.NONE );
    assertEquals( 1, bar.getItemCount() );
    assertSame( display, item.getDisplay() );
    assertSame( bar, item.getParent() );
  }

  public void testItems() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    assertEquals( 0, bar.getItemCount() );
    assertTrue( Arrays.equals( new CoolItem[ 0 ], bar.getItems() ) );

    CoolItem item = new CoolItem( bar, SWT.NONE );
    assertEquals( 1, bar.getItemCount() );
    assertSame( item, bar.getItems()[ 0 ] );
    assertSame( item, bar.getItem( 0 ) );
    assertEquals( 0, bar.indexOf( item ) );
    
    CoolBar anotherBar = new CoolBar( shell, SWT.NONE );
    CoolItem anotherItem = new CoolItem( anotherBar, SWT.NONE );
    assertEquals( -1, bar.indexOf( anotherItem ) );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL, bar.getStyle() );
    
    bar = new CoolBar( shell, SWT.NO_FOCUS );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, SWT.H_SCROLL );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.FLAT | SWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, SWT.VERTICAL );
    assertEquals( SWT.NO_FOCUS | SWT.VERTICAL, bar.getStyle() );

    bar = new CoolBar( shell, SWT.VERTICAL | SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.VERTICAL | SWT.FLAT, bar.getStyle() );
    
    bar = new CoolBar( shell, SWT.HORIZONTAL );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    assertEquals( SWT.NO_FOCUS | SWT.HORIZONTAL | SWT.FLAT, bar.getStyle() );
  }
  
  public void testIndexOf() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    CoolItem item = new CoolItem( bar, SWT.NONE );
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
  
  public void testDispose() {
    final java.util.List log = new ArrayList();
    DisposeListener disposeListener = new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        log.add( event.getSource() );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    bar.addDisposeListener( disposeListener );
    CoolItem item1 = new CoolItem( bar, SWT.NONE );
    item1.addDisposeListener( disposeListener );
    CoolItem item2 = new CoolItem( bar, SWT.NONE );
    item2.addDisposeListener( disposeListener );
    
    item1.dispose();
    assertEquals( true, item1.isDisposed() );
    assertEquals( 1, bar.getItemCount() );
    
    bar.dispose();
    assertEquals( true, bar.isDisposed() );
    assertEquals( true, item2.isDisposed() );
    
    assertSame( item1, log.get( 0 ) );
    assertSame( item2, log.get( 1 ) );
    assertSame( bar, log.get( 2 ) );
  }
  
  public void testLocked() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    
    assertEquals( false, bar.getLocked() );
    bar.setLocked( true );
    assertEquals( true, bar.getLocked() );
  }
  
  public void testItemOrder() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CoolBar bar = new CoolBar( shell, SWT.NONE );
    new CoolItem( bar, SWT.NONE );
    new CoolItem( bar, SWT.NONE );
    
    // Test initial itemOrder -> matches the order in which the items are added
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    
    // Test setItemOrder with legal arguments
    bar.setItemOrder( new int[] { 1, 0 } );
    assertEquals( 0, bar.getItemOrder()[ 1 ] );
    assertEquals( 1, bar.getItemOrder()[ 0 ] );
    
    // Test setItemOrder with illegal arguments
    int[] expectedItemOrder = bar.getItemOrder();
    try {
      bar.setItemOrder( null );
      fail( "setItemOrder must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertEquals( expectedItemOrder, bar.getItemOrder() );
    }
    try {
      bar.setItemOrder( new int[] { 0, 5 } );
      fail( "setItemOrder must not allow argument with indics out of range" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertEquals( expectedItemOrder, bar.getItemOrder() );
    }
    try {
      bar.setItemOrder( new int[] { 0, 0 } );
      fail( "setItemOrder must not allow argument with duplicate indices" );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertEquals( expectedItemOrder, bar.getItemOrder() );
    }
    try {
      bar.setItemOrder( new int[] { 1 } );
      String msg 
        = "setItemOrder must not allow argument whose length doesn't match " 
        + "the number of items";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // Ensure that nothing that itemOrder hasn't changed
      assertEquals( expectedItemOrder, bar.getItemOrder() );
    }
  }
  
  private static void assertEquals( int[] expected, int[] actual ) {
    assertEquals( expected.length, actual.length );
    for( int i = 0; i < expected.length; i++ ) {
      assertEquals( expected[ i ], actual[ i ] );
    }
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
