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

package org.eclipse.rap.rwt.addons.canvas;

import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWINGS_PROPERTY;
import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWING_EVENT;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.registerDataKeys;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator;
import org.eclipse.rap.rwt.addons.internal.canvas.ClientDrawListenerAdapter;
import org.eclipse.rap.rwt.addons.internal.canvas.DrawingsCache;
import org.eclipse.rap.rwt.addons.internal.canvas.GCOperationDispatcher;
import org.eclipse.rap.rwt.addons.internal.util.ResourceLoaderUtil;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.internal.widgets.WidgetRemoteAdapter;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * A <code>ClientCanvas</code> can be used like a SWT <code>Canvas</code> with the difference
 * that a client can draw on it's area, too. Client side drawing will be sent back to the server
 * and registered <code>ClientDrawListeners</code> will be notified.
 * </p>
 *
 * @see ClientDrawListener
 *
 * @since 4.4
 */
@SuppressWarnings({
  "restriction",
  "serial"
})
public class ClientCanvas extends Canvas {

  private final String CLIENT_CANVAS_DATA_KEY = "clientCanvas";

  private final DrawingsCache cache;
  private final ClientDrawListenerAdapter clientDrawListenerAdapter;
  private PaintListener paintListener;

  public ClientCanvas( Composite parent, int style ) {
    super( parent, style );
    clientDrawListenerAdapter = new ClientDrawListenerAdapter();
    cache = new DrawingsCache();
    addDispatchPaintListener();
    addClientListeners();
    exchangesOperationHandler( this );
    setDataKeys();
  }

  /**
   * <p>
   * Adds a <code>ClientDrawListener</code> that gets called when a client draws.
   * </p>
   *
   * @see ClientDrawListener
   */
  public void addClientDrawListener( ClientDrawListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    clientDrawListenerAdapter.addClientDrawListener( listener );
  }

  /**
   * <p>
   * Removes a <code>ClientDrawListener</code>.
   * </p>
   *
   * @see ClientDrawListener
   */
  public void removeClientDrawListener( ClientDrawListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    clientDrawListenerAdapter.removeClientDrawListener( listener );
  }

  @Override
  public void addPaintListener( PaintListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    removePaintListener( paintListener );
    super.addPaintListener( listener );
    super.addPaintListener( paintListener );
  }

  /**
   * <p>
   * Clears all client side drawings.
   * </p>
   */
  public void clear() {
    if( !isDisposed() ) {
      cache.clear();
      redraw();
      fireDrawEvent();
    }
  }

  /**
   * <p>
   * Undo the most recent client side drawing.
   * </p>
   */
  public void undo() {
    if( !isDisposed() ) {
      if( cache.hasUndo() ) {
        cache.undo();
        redraw();
        fireDrawEvent();
      }
    }
  }

  /**
   * <p>
   * returns if a undo can be performed.
   * </p>
   */
  public boolean hasUndo() {
    return cache.hasUndo();
  }

  /**
   * <p>
   * Redo the most recent undo.
   * </p>
   */
  public void redo() {
    if( !isDisposed() ) {
      if( cache.hasRedo() ) {
        cache.redo();
        redraw();
        fireDrawEvent();
      }
    }
  }

  /**
   * <p>
   * Returns if a redo can be performed.
   * </p>
   */
  public boolean hasRedo() {
    return cache.hasRedo();
  }

  private void addClientListeners() {
    String clientListenerPath = "org/eclipse/rap/rwt/addons/canvas/ClientCanvas.js";
    String scriptCode = ResourceLoaderUtil.readTextContent( clientListenerPath );
    ClientListener clientListener = new ClientListener( scriptCode );
    addListener( SWT.MouseDown, clientListener );
    addListener( SWT.MouseUp, clientListener );
    addListener( SWT.MouseMove, clientListener );
    addListener( SWT.Paint, clientListener );
  }

  private void fireDrawEvent() {
    if( !isDisposed() ) {
      clientDrawListenerAdapter.notifyReceivedDrawing();
    }
  }

  private void addDispatchPaintListener() {
    paintListener = new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        GC gc = event.gc;
        processClientDrawings( gc );
        gc.drawPoint( -1, -1 ); //TODO: This is a workaround to force updates, see RAP bug 377070
      }
    };
    super.addPaintListener( paintListener );
  }

  private void processClientDrawings( GC gc ) {
    JsonValue drawings = readEventPropertyValue( getId( this ), DRAWING_EVENT, DRAWINGS_PROPERTY );
    if( drawings != null ) {
      cache.cache( drawings.asString() );
      cache.clearRemoved();
      fireDrawEvent();
    }
    dispatchDrawings( gc );
  }

  private void dispatchDrawings( GC gc ) {
    for( String drawing : cache.getCachedDrawings() ) {
      if( drawing != null ) {
        GCOperationDispatcher dispatcher = new GCOperationDispatcher( gc, drawing );
        dispatcher.dispatch();
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter( Class<T> adapter ) {
    T result = super.getAdapter( adapter );
    if( adapter == DrawingsCache.class ) {
      result = ( T )cache;
    } else if( adapter == ClientDrawListenerAdapter.class ) {
      return ( T )clientDrawListenerAdapter;
    }
    return result;
  }

  private void exchangesOperationHandler( ClientCanvas widget ) {
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )getAdapter( RemoteAdapter.class );
    adapter.addRenderRunnable( new Runnable() {
      @Override
      public void run() {
        ClientCanvasOperator handler = new ClientCanvasOperator( widget );
        RemoteObjectFactory.getRemoteObject( widget ).setHandler( handler );
      }
    } );
  }

  private void setDataKeys() {
    registerDataKeys( CLIENT_CANVAS_DATA_KEY );
    setData( CLIENT_CANVAS_DATA_KEY, getId( this ) );
  }

}
