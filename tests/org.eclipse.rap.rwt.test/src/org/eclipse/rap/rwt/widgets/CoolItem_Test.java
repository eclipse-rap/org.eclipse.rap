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

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;


public class CoolItem_Test extends TestCase {

  public void testSetControl() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    Button button = new Button( bar, RWT.NONE );
    CoolItem item = new CoolItem( bar, RWT.NONE );
    
    item.setControl( button );
    assertSame( button, item.getControl() );

    item.setControl( null );
    assertEquals( null, item.getControl() );
    
    try {
      Button disposedButton = new Button( bar, RWT.PUSH );
      disposedButton.dispose();
      item.setControl( disposedButton );
      fail( "Must not allow to set disposed control" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      Button shellButton = new Button( shell, RWT.PUSH );
      item.setControl( shellButton );
      fail( "Must not allow to set control with a parent other than CoolBar" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testSize() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.NONE );
    CoolItem item = new CoolItem( bar, RWT.NONE );
    Point size = new Point( 80, 30 );
    item.setSize( size );
    assertEquals( new Point( 80 + CoolItem.HANDLE_SIZE, 30 ), item.getSize() );
    assertNotSame( size, item.getSize() );
    
    item.setSize( -2, -1 );
    assertEquals( CoolItem.HANDLE_SIZE, item.getSize().x );
    assertEquals( 0, item.getSize().y );
  }

  public void testBoundsHorizontal() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar horizontalBar = new CoolBar( shell, RWT.HORIZONTAL );
    CoolItem item1 = new CoolItem( horizontalBar, RWT.NONE );
    item1.setSize( 20, 10 );
    CoolItem item2 = new CoolItem( horizontalBar, RWT.NONE );
    item2.setSize( 30, 10 );
    CoolItem item3 = new CoolItem( horizontalBar, RWT.NONE );
    item3.setSize( 40, 10 );
    
    Rectangle expected = new Rectangle( 0, 0, 20 + CoolItem.HANDLE_SIZE, 10 );
    assertEquals( expected, item1.getBounds() );
    expected = new Rectangle( 20 + CoolItem.HANDLE_SIZE, 
                              0, 
                              30 + CoolItem.HANDLE_SIZE, 
                              10 );
    assertEquals( expected, item2.getBounds() );
    expected = new Rectangle( 50 + CoolItem.HANDLE_SIZE + CoolItem.HANDLE_SIZE, 
                              0, 
                              40 + CoolItem.HANDLE_SIZE, 
                              10 );
    assertEquals( expected, item3.getBounds() );
  }
  
  public void testBoundsVertical() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.VERTICAL );
    CoolItem item1 = new CoolItem( bar, RWT.NONE );
    item1.setSize( 10, 20 );
    CoolItem item2 = new CoolItem( bar, RWT.NONE );
    item2.setSize( 10, 30 );
    CoolItem item3 = new CoolItem( bar, RWT.NONE );
    item3.setSize( 10, 40 );
    
    Rectangle expected = new Rectangle( 0, 0, 20 + CoolItem.HANDLE_SIZE, 10 );
    assertEquals( expected, item1.getBounds() );
    expected = new Rectangle( 0, 
                              20 + CoolItem.HANDLE_SIZE, 
                              30 + CoolItem.HANDLE_SIZE, 
                              10 );
    assertEquals( expected, item2.getBounds() );
    expected = new Rectangle( 0, 
                              50 + CoolItem.HANDLE_SIZE + CoolItem.HANDLE_SIZE, 
                              40 + CoolItem.HANDLE_SIZE, 
                              10 );
    assertEquals( expected, item3.getBounds() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
