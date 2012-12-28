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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Cursor_Test {

  private Display device;

  @Before
  public void setUp() {
    Fixture.setUp();
    device = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructorWithNullDevice() {
    device.dispose();
    try {
      new Cursor( null, SWT.CURSOR_ARROW );
      fail( "Must provide device for cursor constructor" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorWithInvaidStyle() {
    try {
      new Cursor( device, -8 );
      fail( "Must not accept illegal style values" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testGetDevice() {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );
    assertSame( device, cursor.getDevice() );
  }

  @Test
  public void testEquality() {
    Cursor cursor1 = device.getSystemCursor( SWT.CURSOR_ARROW );
    Cursor cursor2 = new Cursor( device, SWT.CURSOR_ARROW );
    assertTrue( cursor1.equals( cursor2 ) );
  }

  @Test
  public void testIdentity() {
    Cursor cursor1 = new Cursor( device, SWT.CURSOR_ARROW );
    Cursor cursor2 = device.getSystemCursor( SWT.CURSOR_ARROW );
    assertNotSame( cursor1, cursor2 );
  }

  @Test
  public void testDispose() {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );
    cursor.dispose();
    assertTrue( cursor.isDisposed() );
  }

  @Test
  public void testDisposeFactoryCreated() {
    Cursor cursor = device.getSystemCursor( SWT.CURSOR_ARROW );
    try {
      cursor.dispose();
      fail( "It is not allowed to dispose of a factory-created cursor" );
    } catch( IllegalStateException e ) {
      assertFalse( cursor.isDisposed() );
    }
  }

  @Test
  public void testSerializeSessionCursor() throws Exception {
    Cursor cursor = new Cursor( device, SWT.CURSOR_ARROW );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );

    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
    assertNotNull( deserializedCurosr.getDevice() );
    assertNotSame( cursor.getDevice(), deserializedCurosr.getDevice() );
  }

  @Test
  public void testSerializeSystemCursor() throws Exception {
    Cursor cursor = device.getSystemCursor( SWT.CURSOR_CROSS );

    Cursor deserializedCurosr = Fixture.serializeAndDeserialize( cursor );

    assertEquals( cursor, deserializedCurosr );
    assertFalse( deserializedCurosr.isDisposed() );
  }
}
