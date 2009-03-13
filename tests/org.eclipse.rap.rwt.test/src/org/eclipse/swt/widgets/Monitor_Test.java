/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;

import junit.framework.TestCase;

public class Monitor_Test extends TestCase {

  public void testBounds() throws Exception {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle expectedBounds = new Rectangle( 10, 20, 30, 40 );
    displayAdapter.setBounds( expectedBounds );
    Monitor primaryMonitor = display.getPrimaryMonitor();
    assertNotNull( primaryMonitor );
    Rectangle monitorBounds = primaryMonitor.getBounds();
    assertNotNull( monitorBounds );
    assertEquals( new Rectangle( 10, 20, 30, 40 ), monitorBounds );
    // test dynamic bounds
    displayAdapter.setBounds( new Rectangle( 100, 200, 300, 400 ) );
    Rectangle newMonitorBounds = primaryMonitor.getBounds();
    assertEquals( new Rectangle( 100, 200, 300, 400 ), newMonitorBounds );
  }

  public void testClientArea() throws Exception {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle expectedBounds = new Rectangle( 10, 20, 30, 40 );
    displayAdapter.setBounds( expectedBounds );
    Monitor primaryMonitor = display.getPrimaryMonitor();
    assertNotNull( primaryMonitor );
    Rectangle monitorClientArea = primaryMonitor.getClientArea();
    assertNotNull( monitorClientArea );
    assertEquals( new Rectangle( 10, 20, 30, 40 ), monitorClientArea );
    // test dynamic client area
    displayAdapter.setBounds( new Rectangle( 100, 200, 300, 400 ) );
    Rectangle newMonitorClientArea = primaryMonitor.getClientArea();
    assertEquals( new Rectangle( 100, 200, 300, 400 ), newMonitorClientArea );
  }

  public void testEquals() {
    Display display = new Display();
    Monitor primaryMonitor = display.getPrimaryMonitor();
    Monitor[] monitors = display.getMonitors();
    assertEquals( primaryMonitor, monitors[ 0 ] );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
