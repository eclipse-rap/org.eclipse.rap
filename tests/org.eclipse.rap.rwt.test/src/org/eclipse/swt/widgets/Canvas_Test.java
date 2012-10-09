/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.internal.graphics.IGCAdapter;


public class Canvas_Test extends TestCase {

  private java.util.List<PaintEvent> paintEventLog;
  private Canvas canvas;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    paintEventLog = new ArrayList<PaintEvent>();
    Display display = new Display();
    Shell shell = new Shell( display );
    canvas = new Canvas( shell, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPaintEvent() {
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    } );

    canvas.redraw();
    
    assertEquals( 1, paintEventLog.size() );
    PaintEvent event = paintEventLog.get( 0 );
    assertSame( canvas, event.widget );
    assertTrue( event.gc.isDisposed() );
    assertEquals( event.x, canvas.getClientArea().x );
    assertEquals( event.y, canvas.getClientArea().y );
    assertEquals( event.width, canvas.getClientArea().width );
    assertEquals( event.height, canvas.getClientArea().height );
  }
  
  public void testRemovePaintListener() {
    PaintListener listener = new PaintListener() {
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    };
    canvas.addPaintListener( listener );
    canvas.removePaintListener( listener );
    canvas.redraw();
    assertEquals( 0, paintEventLog.size() );
  }
  
  public void testResize() {
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    } );
    canvas.setSize( 100, 100 );
    assertEquals( 1, paintEventLog.size() );
  }
  
  public void testMultiplePaintEvents() {
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
        event.gc.drawLine( 1, 2, 3, 4 );
      }
    } );
    canvas.redraw();
    canvas.redraw();
    assertEquals( 2, paintEventLog.size() );
    IGCAdapter adapter = canvas.getAdapter( IGCAdapter.class );
    assertEquals( 1, adapter.getGCOperations().length );
  }
  
  public void testIsSerializable() throws Exception {
    Canvas deserializedCanvas = Fixture.serializeAndDeserialize( canvas );
    assertNotNull( deserializedCanvas );
  }
  
  public void testAddPaintListener() {
    canvas.addPaintListener( mock( PaintListener.class ) );
    
    assertTrue( canvas.isListening( SWT.Paint ) );
  }

  public void testRemovePaintListenerUnregistersUntypedEvent() {
    PaintListener listener = mock( PaintListener.class );
    canvas.addPaintListener( listener );
    
    canvas.removePaintListener( listener );

    assertFalse( canvas.isListening( SWT.Paint ) );
  }

  public void testAddPaintListenerWithNullArgument() {
    try {
      canvas.addPaintListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemovePaintListenerWithNullArgument() {
    try {
      canvas.removePaintListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
}
