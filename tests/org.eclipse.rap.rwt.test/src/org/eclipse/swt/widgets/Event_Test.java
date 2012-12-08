/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Rectangle;


public class Event_Test extends TestCase {
  
  private Event event;
  
  @Override
  protected void setUp() throws Exception {
    event = new Event();
  }

  public void testInitialValues() {
    assertTrue( event.doit );
    assertEquals( 0, event.button );
    assertEquals( 0, event.count );
    assertEquals( 0, event.detail );
    assertEquals( 0, event.end );
    assertEquals( 0, event.height );
    assertEquals( 0, event.index );
    assertEquals( 0, event.keyCode );
    assertEquals( 0, event.start );
    assertEquals( 0, event.stateMask );
    assertEquals( 0, event.time );
    assertEquals( 0, event.type );
    assertEquals( 0, event.width );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.character );
    assertEquals( 0, event.time );
    assertNull( event.data );
    assertNull( event.display );
    assertNull( event.gc );
    assertNull( event.item );
    assertNull( event.widget );
    assertNull( event.text );
  }
  
  public void testSetBounds() {
    int x = 1;
    int y = 2;
    int width = 3;
    int height = 4;
    
    event.setBounds( new Rectangle( x, y, width, height ) );
    
    assertEquals( x, event.x );
    assertEquals( y, event.y );
    assertEquals( width, event.width  );
    assertEquals( height, event.height );
  }
  
  public void testGetBounds() {
    event.x = 1;
    event.y = 2;
    event.width = 3;
    event.height = 4;
    
    Rectangle bounds = event.getBounds();
    
    assertEquals( event.x, bounds.x );
    assertEquals( event.y, bounds.y );
    assertEquals( event.width, bounds.width );
    assertEquals( event.height, bounds.height );
  }
}
