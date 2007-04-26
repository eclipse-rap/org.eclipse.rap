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

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import com.w4t.engine.lifecycle.PhaseId;


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
    
    assertEquals( "" + item1 + bar + item2, log.toString() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
