/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.internal.canvas;

import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWINGS_PROPERTY;
import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWING_EVENT;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.addons.canvas.ClientCanvas;
import org.eclipse.rap.rwt.addons.canvas.ClientDrawListener;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ClientCanvasOperator_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullCanvas() {
    new ClientCanvasOperator( null );
  }

  @Test
  public void testFiresDrawEvent() {
    ClientCanvas clientCanvas = new ClientCanvas( shell, SWT.NONE );
    ClientDrawListener drawListener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( drawListener );
    ClientCanvasOperator operator = new ClientCanvasOperator( clientCanvas );

    JsonObject drawings = new JsonObject();
    drawings.add( DRAWINGS_PROPERTY, JsonValue.valueOf( ClientCanvasTestUtil.createDrawings( 1 ) ) );

    operator.handleNotify( clientCanvas, DRAWING_EVENT, drawings );

    verify( drawListener ).receivedDrawing();
  }

  @Test
  public void testAddsDrawingToCache() {
    ClientCanvas clientCanvas = new ClientCanvas( shell, SWT.NONE );
    ClientDrawListener drawListener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( drawListener );
    ClientCanvasOperator operator = new ClientCanvasOperator( clientCanvas );

    JsonObject drawings = new JsonObject();
    drawings.add( DRAWINGS_PROPERTY, JsonValue.valueOf( ClientCanvasTestUtil.createDrawings( 1 ) ) );

    operator.handleNotify( clientCanvas, DRAWING_EVENT, drawings );

    DrawingsCache cache = clientCanvas.getAdapter( DrawingsCache.class );
    assertFalse( cache.getCachedDrawings().isEmpty() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testDispatchesUnknownEventsToControlOperator() {
    ClientCanvas clientCanvas = new ClientCanvas( shell, SWT.NONE );
    ClientCanvasOperator operator = new ClientCanvasOperator( clientCanvas );

    operator.handleNotify( clientCanvas, "foobar", null );
  }

}
