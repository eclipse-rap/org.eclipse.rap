/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
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
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
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
    IWidgetAdapter adapter = widget.getAdapter( IWidgetAdapter.class );
    if( adapter.getParent() == null || !adapter.getParent().isDisposed() ) {
      ClientObjectFactory.getClientObject( widget ).destroy();
    }
  }

  /**
   * <p>
   * As a side effect to redraw calls some native widgets trigger events like
   * resize for example. To simulate this behaviour subclasses may override
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
}
