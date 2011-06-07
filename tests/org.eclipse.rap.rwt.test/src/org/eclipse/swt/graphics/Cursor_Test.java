/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class Cursor_Test extends TestCase {

  public void testConstructor() {
    try {
      new Cursor( null, SWT.CURSOR_ARROW );
      fail( "Must provide device for cursor constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new Cursor( new Display(), -8 );
      fail( "Must not accept illegal style values" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testConstructorWithNullDevice() {
    Display device = new Display();
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );
    assertSame( Display.getCurrent(), cursor.getDevice() );
  }
  
  public void testEquality() {
    Display device = new Display();
    Cursor cursor1 = device.getSystemCursor( SWT.CURSOR_ARROW );
    Cursor cursor2 = new Cursor( device, SWT.CURSOR_ARROW );
    assertTrue( cursor1.equals( cursor2 ) );
  }

  public void testIdentity() {
    Display device = new Display();
    Cursor cursor1 = new Cursor( device, SWT.CURSOR_ARROW );
    Cursor cursor2 = device.getSystemCursor( SWT.CURSOR_ARROW );
    assertNotSame( cursor1, cursor2 );
  }

  public void testDispose() {
    Display display = new Display();
    Cursor cursor = new Cursor( display, SWT.CURSOR_ARROW );
    cursor.dispose();
    assertTrue( cursor.isDisposed() );
  }
  
  public void testDisposeFactoryCreated() {
    Display display = new Display();
    Cursor cursor = display.getSystemCursor( SWT.CURSOR_ARROW );
    try {
      cursor.dispose();
      fail( "It is not allowed to dispose of a factory-created cursor" );
    } catch( IllegalStateException e ) {
      assertFalse( cursor.isDisposed() );
    }
  }
  
  public void testSerializeSessionCursor() throws Exception {
    Display display = new Display();
    Cursor cursor = new Cursor( display, SWT.CURSOR_ARROW );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );
    
    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
    assertNotNull( deserializedCurosr.getDevice() );
    assertNotSame( cursor.getDevice(), deserializedCurosr.getDevice() );
  }
  
  public void testSerializeSystemCursor() throws Exception {
    Display display = new Display();
    Cursor cursor = display.getSystemCursor( SWT.CURSOR_CROSS );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );
    
    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
  }
  
  @SuppressWarnings("deprecation")
  public void testSerializeSharedCursor() throws Exception {
    Cursor cursor = Graphics.getCursor( SWT.CURSOR_ARROW );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );
    
    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
