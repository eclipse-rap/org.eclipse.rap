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

package org.eclipse.rap.rwt.widgets;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.DisposeEvent;
import org.eclipse.rap.rwt.events.DisposeListener;


public class CoolBar_Test extends TestCase {
  
  public void testHierarchy() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    
    assertTrue( Composite.class.isAssignableFrom( bar.getClass() ) );
    assertSame( shell, bar.getParent() );
    assertSame( display, bar.getDisplay() );

    CoolItem item = new CoolItem( bar, RWT.NONE );
    assertEquals( 1, bar.getItemCount() );
    assertSame( display, item.getDisplay() );
    assertSame( bar, item.getParent() );
  }

  public void testItems() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    assertEquals( 0, bar.getItemCount() );
    assertTrue( Arrays.equals( new CoolItem[ 0 ], bar.getItems() ) );

    CoolItem item = new CoolItem( bar, RWT.NONE );
    assertEquals( 1, bar.getItemCount() );
    assertSame( item, bar.getItems()[ 0 ] );
    assertSame( item, bar.getItem( 0 ) );
    assertEquals( 0, bar.indexOf( item ) );
    
    CoolBar anotherBar = new CoolBar( shell, RWT.NONE );
    CoolItem anotherItem = new CoolItem( anotherBar, RWT.NONE );
    assertEquals( -1, bar.indexOf( anotherItem ) );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    assertEquals( RWT.NO_FOCUS | RWT.HORIZONTAL, bar.getStyle() );
    
    bar = new CoolBar( shell, RWT.NO_FOCUS );
    assertEquals( RWT.NO_FOCUS | RWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, RWT.H_SCROLL );
    assertEquals( RWT.NO_FOCUS | RWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, RWT.FLAT );
    assertEquals( RWT.NO_FOCUS | RWT.FLAT | RWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, RWT.VERTICAL );
    assertEquals( RWT.NO_FOCUS | RWT.VERTICAL, bar.getStyle() );

    bar = new CoolBar( shell, RWT.VERTICAL | RWT.FLAT );
    assertEquals( RWT.NO_FOCUS | RWT.VERTICAL | RWT.FLAT, bar.getStyle() );
    
    bar = new CoolBar( shell, RWT.HORIZONTAL );
    assertEquals( RWT.NO_FOCUS | RWT.HORIZONTAL, bar.getStyle() );

    bar = new CoolBar( shell, RWT.HORIZONTAL | RWT.FLAT );
    assertEquals( RWT.NO_FOCUS | RWT.HORIZONTAL | RWT.FLAT, bar.getStyle() );
  }
  
  public void testIndexOf() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    CoolItem item = new CoolItem( bar, RWT.NONE );
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
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testDispose() {
    final StringBuffer log = new StringBuffer();
    DisposeListener disposeListener = new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        log.append( event.getSource() );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    bar.addDisposeListener( disposeListener );
    CoolItem item1 = new CoolItem( bar, RWT.NONE );
    item1.addDisposeListener( disposeListener );
    CoolItem item2 = new CoolItem( bar, RWT.NONE );
    item2.addDisposeListener( disposeListener );
    
    item1.dispose();
    assertEquals( true, item1.isDisposed() );
    assertEquals( 1, bar.getItemCount() );
    
    bar.dispose();
    assertEquals( true, bar.isDisposed() );
    assertEquals( true, item2.isDisposed() );
    
    assertEquals( "" + item1 + bar + item2, log.toString() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
