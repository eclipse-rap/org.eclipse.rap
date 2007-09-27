/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Widget;


/**
 * Abstract implementation of a widget life cycle adapter.
 * All widget LCAs should inherit from this class.
 * 
 * @since 1.0
 */
public abstract class AbstractWidgetLCA implements IWidgetLifeCycleAdapter {
  
  public final void render( final Widget widget ) throws IOException {
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
    if( !adapter.isInitialized() ) {
      renderInitialization( widget );
      widget.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( final DisposeEvent event ) {
          DisposedWidgets.add( ( Widget )event.getSource() );
          widget.removeDisposeListener( this );
        }
      } );
    }
    renderChanges( widget );
    adapter.setInitialized( true );
  }
  
  // TODO [rst] Javadoc
  public Rectangle adjustCoordinates( final Rectangle bounds ) {
    // subclasses may override
    return bounds;
  }
  
  // TODO [rh] Javadoc
  public abstract void preserveValues( Widget widget );
  
  /**
   * Writes JavaScript code to the response that creates a new widget instance
   * and initializes it. This method is called only once for every widget,
   * before <code>renderChanges</code> is called for the first time.
   *
   * @param widget the widget to initialize
   * @throws IOException
   */
  public abstract void renderInitialization( Widget widget ) throws IOException;
  
  /**
   * Writes JavaScript code to the response that applies the state changes of
   * the widget to the client. Implementations must only render those properties
   * that have been changed during the processing of the current request.
   *
   * @param widget the widget to render changes for
   * @throws IOException
   */
  public abstract void renderChanges( Widget widget ) throws IOException;
  
  /**
   * Writes JavaScript code to the response that renders the disposal of the
   * widget.
   *
   * @param widget the widget to dispose
   * @throws IOException
   */
  public abstract void renderDispose( Widget widget ) throws IOException;
  
  /**
   * Writes JavaScript code to the response that resets the client-side state of
   * a disposed widget in order to make it ready for later reuse. After this
   * code has been processed the client-side widget should be in a state that is
   * equivalent to a newly created widget.
   *
   * @param typePoolId the type pool id of the widget to reset
   * @throws IOException
   */
  public abstract void createResetHandlerCalls( String typePoolId )
    throws IOException;
  
  /**
   * Returns an id that is used to identify the type of a widget in the
   * client-side widget pool.
   * <p>
   * The widget pool is used to store disposed widget instances on the client
   * for later reuse. This is necessary to improve performance and to save
   * client memory. Only widgets with the same type pool id can be reused.
   * </p>
   * <p>
   * Usually, the fully qualified class name is a suitable return value. In case
   * different sub-types of widget instances should be distinguished, this
   * method must return a different string for every type, e.g. by appending a
   * suffix. If this method returns <code>null</code>, the widget will not be
   * stored in the widget pool and cannot be reused.
   * </p>
   *
   * @param widget the widget to store in the pool
   * @return the type pool id or <code>null</code> if the widget should not be
   *         pooled
   */
  public abstract String getTypePoolId( Widget widget );
}
