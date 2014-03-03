/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


/**
 * Abstract implementation of a widget life cycle adapter.
 * All widget LCAs should inherit from this class.
 *
 * @since 2.0
 * @deprecated New custom widgets should use the RemoteObject API instead of LCAs.
 * @see org.eclipse.rap.rwt.remote.RemoteObject
 */
@Deprecated
public abstract class AbstractWidgetLCA
  extends org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA
{

  @Override
  public final void render( Widget widget ) throws IOException {
    super.render( widget );
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
  @Override
  public void readData( Widget widget ) {
    super.readData( widget );
  }

  @Override
  public abstract void preserveValues( Widget widget );

  /**
   * Writes a message to the response that creates a new widget instance
   * and initializes it. This method is called only once for every widget,
   * before <code>renderChanges</code> is called for the first time.
   *
   * @param widget the widget to initialize
   * @throws IOException
   */
  @Override
  public abstract void renderInitialization( Widget widget ) throws IOException;

  /**
   * Writes a message to the response that applies the state changes of
   * the widget to the client. Implementations must only render those properties
   * that have been changed during the processing of the current request.
   *
   * @param widget the widget to render changes for
   * @throws IOException
   */
  @Override
  public abstract void renderChanges( Widget widget ) throws IOException;

  /**
   * Writes a message to the response that renders the disposal of the
   * widget.
   *
   * @param widget the widget to dispose
   * @throws IOException
   */
  @Override
  public void renderDispose( Widget widget ) throws IOException {
    super.renderDispose( widget );
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
  @Override
  public void doRedrawFake( Control control ) {
  }

}
