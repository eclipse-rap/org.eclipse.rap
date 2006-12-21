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
import org.eclipse.rap.rwt.graphics.Rectangle;

public class Display_Test extends TestCase {

  public void testSingleDisplayPerSession() {
    Display display = new Display();
    assertEquals( Display.getCurrent(), display );
    try {
      new Display();
      fail( "Only one display allowed per session" );
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testGetShells() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Composite shell1 = new Shell( display , RWT.NONE );
    assertSame( shell1, display.getShells()[ 0 ] );
    Composite shell2 = new Shell( display , RWT.NONE );
    Composite[] shells = display.getShells();
    assertTrue( shell2 == shells[ 0 ] || shell2 == display.getShells()[ 1 ] );
  }
  
  public void testProperties() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Rectangle bounds = display.getBounds();
    assertNotNull( bounds );
    bounds.x += 1;
    assertTrue( bounds.x != display.getBounds().x );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
