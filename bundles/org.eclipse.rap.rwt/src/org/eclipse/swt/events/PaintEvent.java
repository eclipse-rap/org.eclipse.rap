/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation, EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    EclipseSource - ongoing development
 *******************************************************************************/
package org.eclipse.swt.events;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this class are sent as a result of visible areas of controls
 * requiring re-painting.
 *
 * @see PaintListener
 * @since 1.3
 */
public final class PaintEvent extends TypedEvent {

  private static final long serialVersionUID = 3256446919205992497L;

  private static final Class LISTENER = PaintListener.class;
  private static final int PAINT_CONTROL = 9/* SWT.Paint */;

  /**
   * the graphics context to use when painting that is configured to use the
   * colors, font and damaged region of the control. It is valid only during the
   * paint and must not be disposed
   */
  public GC gc;

  /**
   * the x offset of the bounding rectangle of the region that requires painting
   */
  public int x;

  /**
   * the y offset of the bounding rectangle of the region that requires painting
   */
  public int y;

  /**
   * the width of the bounding rectangle of the region that requires painting
   */
  public int width;

  /**
   * the height of the bounding rectangle of the region that requires painting
   */
  public int height;

  /**
   * the number of following paint events which are pending which may always be
   * zero on some platforms
   */
  public int count;

  /**
   * Constructs a new instance of this class based on the information in the
   * given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public PaintEvent( Event event ) {
    super( event );
    gc = event.gc;
    x = event.x;
    y = event.y;
    width = event.width;
    height = event.height;
    count = event.count;
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public PaintEvent( Widget widget, GC gc, Rectangle bounds ) {
    super( widget, PAINT_CONTROL );
    this.gc = gc;
    x = bounds.x;
    y = bounds.y;
    width = bounds.width;
    height = bounds.height;
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case PAINT_CONTROL:
        ( ( PaintListener )listener ).paintControl( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  @Override
  protected Class getListenerType() {
    return LISTENER;
  }

  @Override
  protected boolean allowProcessing() {
    return true;
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void addListener( Adaptable adaptable, PaintListener listener ) {
    addListener( adaptable, LISTENER, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, PaintListener listener ) {
    removeListener( adaptable, LISTENER, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }

  /**
   * Returns a string containing a concise, human-readable description of the
   * receiver.
   *
   * @return a string representation of the event
   */
  @Override
  public String toString() {
    String string = super.toString();
    return   string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " gc="
           + gc
           + " x="
           + x
           + " y="
           + y
           + " width="
           + width
           + " height="
           + height
           + " count="
           + count
           + "}";
  }
}
