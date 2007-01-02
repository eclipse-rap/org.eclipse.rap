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
import org.eclipse.rap.rwt.layout.FillLayout;

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

  
  public void testCoordinateMappings() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Rectangle shellBounds = new Rectangle( 10, 10, 400, 400 );
    shell.setBounds( shellBounds );
    
    Rectangle actual = display.map( shell, shell, 1, 2 , 3, 4 );
    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, actual );
    
    actual = display.map( shell, null, 5, 6, 7, 8 );
    expected = new Rectangle( shellBounds.x + 5,
                              shellBounds.y + 6,
                              7,
                              8 );
    assertEquals( expected, actual );
    
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, RWT.NONE );
    shell.layout();
    actual = display.map( folder, shell, 6, 7, 8, 9 );
    expected = new Rectangle( folder.getBounds().x + 6,
                              folder.getBounds().y + 7,
                              8,
                              9 );
    assertEquals( expected, actual );
    
    actual = display.map( null, folder, 1, 2, 3, 4 );
    expected = new Rectangle( 1 - shell.getBounds().x - folder.getBounds().x,
                              2 - shell.getBounds().y - folder.getBounds().y,
                              3,
                              4 );
    assertEquals( expected, actual );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
