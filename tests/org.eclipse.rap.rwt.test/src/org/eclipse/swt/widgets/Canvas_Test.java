/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.internal.graphics.IGCAdapter;


public class Canvas_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPaintEvent() {
    final java.util.List<PaintEvent> log = new ArrayList<PaintEvent>();
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( final PaintEvent event ) {
        log.add( event );
      }
    } );
    assertEquals( 0, log.size() );
    canvas.redraw();
    assertEquals( 1, log.size() );
    PaintEvent event = log.get( 0 );
    assertSame( canvas, event.widget );
    assertTrue( event.gc.isDisposed() );
  }
  
  public void testRemovePaintListener() {
    final java.util.List<PaintEvent> log = new ArrayList<PaintEvent>();
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    PaintListener listener = new PaintListener() {
      public void paintControl( final PaintEvent event ) {
        log.add( event );
      }
    };
    canvas.addPaintListener( listener );
    canvas.removePaintListener( listener );
    canvas.redraw();
    assertEquals( 0, log.size() );
  }
  
  public void testResize() {
    final java.util.List<PaintEvent> log = new ArrayList<PaintEvent>();
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( final PaintEvent event ) {
        log.add( event );
      }
    } );
    assertEquals( 0, log.size() );
    canvas.setSize( 100, 100 );
    assertEquals( 1, log.size() );
  }
  
  public void testMultiplePaintEvents() {
    final java.util.List<PaintEvent> log = new ArrayList<PaintEvent>();
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( final PaintEvent event ) {
        log.add( event );
        event.gc.drawLine( 1, 2, 3, 4 );
      }
    } );
    canvas.redraw();
    canvas.redraw();
    assertEquals( 2, log.size() );
    IGCAdapter adapter = ( IGCAdapter )canvas.getAdapter( IGCAdapter.class );
    assertEquals( 1, adapter.getGCOperations().length );
  }
}
