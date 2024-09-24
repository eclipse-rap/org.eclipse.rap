/*******************************************************************************
 * Copyright (c) 2010, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.internal.events.EventList;
import org.eclipse.swt.internal.graphics.GCAdapter;
import org.eclipse.swt.internal.widgets.canvaskit.CanvasLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Canvas_Test {

  @Rule
  public TestContext context = new TestContext();

  private java.util.List<PaintEvent> paintEventLog;
  private Display display;
  private Canvas canvas;

  @Before
  public void setUp() {
    paintEventLog = new ArrayList<PaintEvent>();
    display = new Display();
    Shell shell = new Shell( display );
    canvas = new Canvas( shell, SWT.NONE );
    canvas.setSize( 100, 200 );
  }

  @Test
  public void testPaintEvent_afterRedraw() {
    canvas.addPaintListener( new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    } );

    canvas.redraw();
    while (display.readAndDispatch()) {}

    // 2 = 1 orignal drawing + 1 redraws
    assertEquals( 2, paintEventLog.size() );
    PaintEvent event = paintEventLog.get( 1 );
    assertSame( canvas, event.widget );
    assertTrue( event.gc.isDisposed() );
    assertEquals( event.x, 0 );
    assertEquals( event.y, 0 );
    assertEquals( event.width, 100 );
    assertEquals( event.height, 200 );
  }

  @Test
  public void testPaintEvent_afterPartialRedraw() {
    canvas.addPaintListener( new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    } );

    canvas.redraw( 1, 2, 3, 4, true );
    while (display.readAndDispatch()) {}

    // 2 = 1 orignal drawing + 1 redraws
    assertEquals( 2, paintEventLog.size() );
    PaintEvent event = paintEventLog.get( 1 );
    assertSame( canvas, event.widget );
    assertTrue( event.gc.isDisposed() );
    assertEquals( event.x, 1 );
    assertEquals( event.y, 2 );
    assertEquals( event.width, 3 );
    assertEquals( event.height, 4 );
  }

  @Test
  public void testResize() {
    canvas.addPaintListener( new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
      }
    } );
    canvas.setSize( 100, 100 );
    while (display.readAndDispatch()) {}

    // 2 = 1 orignal drawing + 1 redraws after resize
    assertEquals( 2, paintEventLog.size() );
  }

  @Test
  public void testMultiplePaintEvents() {
    canvas.addPaintListener( new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        paintEventLog.add( event );
        event.gc.drawLine( 1, 2, 3, 4 );
      }
    } );
    canvas.redraw();
    canvas.redraw();
    while (display.readAndDispatch()) {}
    
    // 3 = 1 orignal drawing + 2 redraws
    assertEquals( 3, paintEventLog.size() );
    GCAdapter adapter = canvas.getAdapter( GCAdapter.class );
    assertEquals( 1, adapter.getGCOperations().length );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Canvas deserializedCanvas = serializeAndDeserialize( canvas );
    assertNotNull( deserializedCanvas );
  }

  @Test
  public void testAddPaintListener() {
    canvas.addPaintListener( mock( PaintListener.class ) );

    assertTrue( canvas.isListening( SWT.Paint ) );
  }

  @Test
  public void testRemovePaintListener() {
    PaintListener listener = mock( PaintListener.class );
    canvas.addPaintListener( listener );

    canvas.removePaintListener( listener );

    assertFalse( canvas.isListening( SWT.Paint ) );
  }

  @Test
  public void testRemovePaintListenerUnregistersUntypedEvent() {
    PaintListener listener = mock( PaintListener.class );
    canvas.addPaintListener( listener );

    canvas.removePaintListener( listener );

    assertFalse( canvas.isListening( SWT.Paint ) );
  }

  @Test
  public void testAddPaintListenerWithNullArgument() {
    try {
      canvas.addPaintListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemovePaintListenerWithNullArgument() {
    try {
      canvas.removePaintListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorDoesNotSendPaintEvents() {
    // See bug 393771
    canvas.addPaintListener( mock( PaintListener.class ) );

    assertEquals( 0, EventList.getInstance().getAll().length );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( canvas.getAdapter( WidgetLCA.class ) instanceof CanvasLCA );
    assertSame( canvas.getAdapter( WidgetLCA.class ), canvas.getAdapter( WidgetLCA.class ) );
  }

}
