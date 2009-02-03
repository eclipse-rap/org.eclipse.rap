/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;


public class QxImage_Test extends TestCase {

  ResourceLoader dummyLoader = new ResourceLoader() {

    public InputStream getResourceAsStream( final String resourceName )
      throws IOException
    {
      return null;
    }
  };

  public void testIllegalArguments() {
    try {
      QxImage.valueOf( null, null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.valueOf( "", null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.valueOf( "", dummyLoader  );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testNone() {
    assertSame( QxImage.NONE, QxImage.valueOf( "none", null ) );
    assertSame( QxImage.NONE, QxImage.valueOf( "none", dummyLoader ) );
    assertNotSame( QxImage.NONE, QxImage.valueOf( "None", dummyLoader ) );
    assertTrue( QxImage.NONE.none );
    assertNull( QxImage.NONE.path );
    assertNull( QxImage.NONE.loader );
  }

  public void testCreate() {
    QxImage qxImage = QxImage.valueOf( "foo", dummyLoader );
    assertFalse( qxImage.none );
    assertEquals( "foo", qxImage.path );
    assertSame( dummyLoader, qxImage.loader );
  }

  public void testDefaultString() {
    assertEquals( "none", QxImage.NONE.toDefaultString() );
    assertEquals( "", QxImage.valueOf( "foo", dummyLoader ).toDefaultString() );
  }

  public void testHashCode() {
    assertEquals( -1, QxImage.NONE.hashCode() );
    QxImage qxImage1 = QxImage.valueOf( "None", dummyLoader );
    QxImage qxImage2 = QxImage.valueOf( "None", dummyLoader );
    assertEquals( qxImage1.hashCode(), qxImage2.hashCode() );
  }
}
