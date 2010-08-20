/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.GCAdapter;
import org.eclipse.swt.internal.graphics.IGCAdapter;

/**
 * This class serves as a base class for custom widgets.
 * It does not yet provides any drawing capabilities.
 * Instances of this class provide a surface for drawing
 * arbitrary graphics.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * This class may be subclassed by custom control implementors
 * who are building controls that are <em>not</em> constructed
 * from aggregates of other controls. That is, they are either
 * painted using SWT graphics calls or are handled by native
 * methods.
 * </p>
 *
 * @see Composite
 * @since 1.0
 */
public class Canvas extends Composite {

  private GCAdapter gcAdapter;

  Canvas( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
  }

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *        instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the parent</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Canvas( final Composite parent, final int style ) {
    super( parent, style );
    repaint();
  }

  /**
   * Implementation of the <code>Adaptable</code> interface.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IGCAdapter.class ) {
      if( gcAdapter == null ) {
        gcAdapter = new GCAdapter();
      }
      result = gcAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver needs to be painted, by sending it
   * one of the messages defined in the <code>PaintListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see PaintListener
   * @see #removePaintListener
   * @since 1.3
   */
  public void addPaintListener( final PaintListener listener ) {
    checkWidget();
    PaintEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver needs to be painted.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see PaintListener
   * @see #addPaintListener
   * @since 1.3
   */
  public void removePaintListener( final PaintListener listener ) {
    checkWidget();
    PaintEvent.removeListener( this, listener );
  }

  /////////////
  // repainting

  void notifyResize( final Point oldSize ) {
    super.notifyResize( oldSize );
    if( !oldSize.equals( getSize() ) ) {
      repaint();
    }
  }

  void internalSetRedraw( final boolean redraw ) {
    super.internalSetRedraw( redraw );
    if( redraw ) {
      repaint();
    }
  }

  private void repaint() {
    if( gcAdapter != null ) {
      gcAdapter.clearGCOperations();
      gcAdapter.setForceRedraw( true );
    }
    GC gc = new GC( this );
    Rectangle clientArea = getClientArea();
    PaintEvent paintEvent = new PaintEvent( this, gc, clientArea );
    paintEvent.processEvent();
    gc.dispose();
  }
}
