/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.List;

import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


/**
 * Abstract implementation of a widget life cycle adapter.
 * All widget LCAs should inherit from this class.
 *
 * @since 2.0
 */
public abstract class AbstractWidgetLCA implements WidgetLifeCycleAdapter {

  public final void render( Widget widget ) throws IOException {
    WidgetAdapterImpl adapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
    if( !adapter.isInitialized() ) {
      renderInitialization( widget );
    }
    renderChanges( widget );
    adapter.setInitialized( true );
  }

  /**
   * Translates the bounds of a widget that is enclosed in the widget handled by
   * this LCA. The default implementation does not modify the given bounds.
   * Subclasses may override.
   *
   * @param widget the enclosed widget whose bounds to adjust
   * @param bounds the actual bounds of the enclosed widget
   * @return the adjusted bounds
   * @deprecated Adjustment of the widget bounds is now handled on the client.
   */
  @Deprecated
  public Rectangle adjustCoordinates( Widget widget, Rectangle bounds ) {
    return bounds;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The default implementation of this method passes all operations for the given widget to the
   * operation handler registered with the corresponding remote object.
   * </p>
   *
   * @since 2.2
   */
  public void readData( Widget widget ) {
    ClientMessage clientMessage = ProtocolUtil.getClientMessage();
    String id = getId( widget );
    List<Operation> operations = clientMessage.getAllOperationsFor( id );
    if( !operations.isEmpty() ) {
      OperationHandler handler = getOperationHandler( id );
      for( Operation operation : operations ) {
        handleOperation( handler, operation );
      }
    }
  }

  public abstract void preserveValues( Widget widget );

  /**
   * Writes a message to the response that creates a new widget instance
   * and initializes it. This method is called only once for every widget,
   * before <code>renderChanges</code> is called for the first time.
   *
   * @param widget the widget to initialize
   * @throws IOException
   */
  public abstract void renderInitialization( Widget widget ) throws IOException;

  /**
   * Writes a message to the response that applies the state changes of
   * the widget to the client. Implementations must only render those properties
   * that have been changed during the processing of the current request.
   *
   * @param widget the widget to render changes for
   * @throws IOException
   */
  public abstract void renderChanges( Widget widget ) throws IOException;

  /**
   * Writes a message to the response that renders the disposal of the
   * widget.
   *
   * @param widget the widget to dispose
   * @throws IOException
   */
  public void renderDispose( Widget widget ) throws IOException {
    WidgetAdapter adapter = widget.getAdapter( WidgetAdapter.class );
    if( adapter.getParent() == null || !adapter.getParent().isDisposed() ) {
      getRemoteObject( widget ).destroy();
    }
  }

  /**
   * <p>
   * As a side effect to redraw calls some native widgets trigger events like
   * resize for example. To simulate this behavior subclasses may override
   * this method. The method takes as parameter type <code>Control</code>,
   * since the redraw methods are only available at the <code>Control</code>
   * subclasses of <code>Widgets</code>.
   * </p>
   *
   * <p>
   * Note that the redraw fake takes place between the process action and
   * the render phase.
   * </p>
   * @param control the control on which redraw was called.
   */
  public void doRedrawFake( Control control ) {
  }

  private static OperationHandler getOperationHandler( String id ) {
    RemoteObjectImpl remoteObject = RemoteObjectRegistry.getInstance().get( id );
    if( remoteObject == null ) {
      throw new IllegalStateException( "No remote object found for widget: " + id );
    }
    OperationHandler handler = remoteObject.getHandler();
    if( handler == null ) {
      throw new IllegalStateException( "No operation handler found for widget: " + id );
    }
    return handler;
  }

  private static void handleOperation( OperationHandler handler, Operation operation ) {
    if( operation instanceof SetOperation ) {
      SetOperation setOperation = ( SetOperation )operation;
      handler.handleSet( setOperation.getProperties() );
    } else if( operation instanceof CallOperation ) {
      CallOperation callOperation = ( CallOperation )operation;
      handler.handleCall( callOperation.getMethodName(), callOperation.getProperties() );
    } else if( operation instanceof NotifyOperation ) {
      NotifyOperation notifyOperation = ( NotifyOperation )operation;
      handler.handleNotify( notifyOperation.getEventName(), notifyOperation.getProperties() );
    }
  }

}
