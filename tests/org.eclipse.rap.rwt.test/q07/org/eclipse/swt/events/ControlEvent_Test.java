/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

public class ControlEvent_Test extends TestCase {

  private Display display;
  private Control control;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    control = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testResize() {
    final Control[] source = new Control[ 1 ];
    control.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        source[ 0 ] = ( Control )event.getSource();
      }
    } );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.setSize( 10, 20 );
    assertSame( control, source[ 0 ] );
    
    // this does not belong to all controls, only to those which allow
    // resize or move operations by the user (e.g. Shell)
    source[ 0 ] = null;
    String id = WidgetUtil.getId( control );
    Fixture.fakeRequestParam( id + ".bounds.width", "50" );
    Fixture.fakeRequestParam( id + ".bounds.height", "100" );
    Fixture.readDataAndProcessAction( control );
    assertSame( control, source[ 0 ] );
    assertEquals( new Point( 50, 100 ), control.getSize() );
  }

  public void testMoved() {
    final Control[] source = new Control[ 1 ];
    control.addControlListener( new ControlAdapter() {
      public void controlMoved( final ControlEvent event ) {
        source[ 0 ] = ( Control )event.getSource();
      }
    } );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.setLocation( 30, 40 );
    assertSame( control, source[ 0 ] );

    // this does not belong to all controls, only to those which allow
    // resize or move operations by the user (e.g. Shell)
    source[ 0 ] = null;
    String id = WidgetUtil.getId( control );
    Fixture.fakeRequestParam( id + ".bounds.x", "150" );
    Fixture.fakeRequestParam( id + ".bounds.y", "200" );
    Fixture.readDataAndProcessAction( control );
    assertSame( control, source[ 0 ] );
    assertEquals( new Point( 150, 200 ), control.getLocation() );
  }
}