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

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.addons.canvas.ClientCanvas;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.canvaskit.CanvasOperationHandler;
import org.eclipse.swt.widgets.Canvas;


@SuppressWarnings({
  "restriction",
  "serial"
})
public class ClientCanvasOperator extends CanvasOperationHandler {

  public static final String DRAWING_EVENT = "Drawing";
  public static final String DRAWINGS_PROPERTY = "drawings";

  public ClientCanvasOperator( ClientCanvas canvas ) {
    super( canvas );
    if( canvas == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
  }

  @Override
  public void handleNotify( Canvas control, String eventName, JsonObject properties ) {
    if( eventName.equals( DRAWING_EVENT ) ) {
      handleDrawings( control, properties );
    } else {
      super.handleNotify( control, eventName, properties );
    }
  }

  private static void handleDrawings( Canvas control, JsonObject properties ) {
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        DrawingsCache cache = control.getAdapter( DrawingsCache.class );
        JsonValue drawings = properties.get( DRAWINGS_PROPERTY );
        if( drawings != null ) {
          cache.cache( drawings.asString() );
          cache.clearRemoved();
          fireDrawEvent( ( ClientCanvas )control );
        }
      }
    } );
  }

  private static void fireDrawEvent( ClientCanvas control ) {
    if( !control.isDisposed() ) {
      control.getAdapter( ClientDrawListenerAdapter.class ).notifyReceivedDrawing();
    }
  }

}