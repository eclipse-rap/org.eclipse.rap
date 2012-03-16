/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class Cursor_Test extends TestCase {

  private Display device;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testConstructorWithNullDevice() {
    device.dispose();
    try {
      new Cursor( null, SWT.CURSOR_ARROW );
      fail( "Must provide device for cursor constructor" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConstructorWithInvaidStyle() {
    try {
      new Cursor( device, -8 );
      fail( "Must not accept illegal style values" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testGetDevice() {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );
    assertSame( device, cursor.getDevice() );
  }

  public void testEquality() {
    Cursor cursor1 = device.getSystemCursor( SWT.CURSOR_ARROW );
    Cursor cursor2 = new Cursor( device, SWT.CURSOR_ARROW );
    assertTrue( cursor1.equals( cursor2 ) );
  }

  public void testIdentity() {
    Cursor cursor1 = new Cursor( device, SWT.CURSOR_ARROW );
    Cursor cursor2 = device.getSystemCursor( SWT.CURSOR_ARROW );
    assertNotSame( cursor1, cursor2 );
  }

  public void testDispose() {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );
    cursor.dispose();
    assertTrue( cursor.isDisposed() );
  }

  public void testDisposeFactoryCreated() {
    Cursor cursor = device.getSystemCursor( SWT.CURSOR_ARROW );
    try {
      cursor.dispose();
      fail( "It is not allowed to dispose of a factory-created cursor" );
    } catch( IllegalStateException e ) {
      assertFalse( cursor.isDisposed() );
    }
  }

  public void testSerializeSessionCursor() throws Exception {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );

    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
    assertNotNull( deserializedCurosr.getDevice() );
    assertNotSame( cursor.getDevice(), deserializedCurosr.getDevice() );
  }

  public void testSerializeSystemCursor() throws Exception {
    Cursor cursor = device.getSystemCursor( SWT.CURSOR_CROSS );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );

    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
  }
}
