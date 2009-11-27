/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.dnd;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Widget;

/**
 * The DragSourceEvent contains the event information passed in the methods of
 * the DragSourceListener.
 * 
 * @see DragSourceListener
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @since 1.3
 */
public class DragSourceEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int DRAG_START = DND.DragStart;
  public static final int DRAG_END = DND.DragEnd;
  public static final int DRAG_SET_DATA = DND.DragSetData;

  private static final Class LISTENER = DragSourceListener.class;

  /**
   * The operation that was performed.
   * 
   * @see DND#DROP_NONE
   * @see DND#DROP_MOVE
   * @see DND#DROP_COPY
   * @see DND#DROP_LINK
   * @see DND#DROP_TARGET_MOVE
   */
  public int detail;

  /**
   * In dragStart, the doit field determines if the drag and drop operation
   * should proceed; in dragFinished, the doit field indicates whether the
   * operation was performed successfully.
   * <p>
   * </p>
   * In dragStart:
   * <p>
   * Flag to determine if the drag and drop operation should proceed. The
   * application can set this value to false to prevent the drag from starting.
   * Set to true by default.
   * </p>
   * <p>
   * In dragSetData:
   * </p>
   * <p>
   * This will be set to true when the call to dragSetData is made. Set it to
   * false to cancel the drag.
   * </p>
   * <p>
   * In dragFinished:
   * </p>
   * <p>
   * Flag to indicate if the operation was performed successfully. True if the
   * operation was performed successfully.
   * </p>
   */
  public boolean doit;
  
  /**
   * In dragStart, the x coordinate (relative to the control) of the position
   * the mouse went down to start the drag.
   */
  public int x;
  
  /**
   * In dragStart, the y coordinate (relative to the control) of the position
   * the mouse went down to start the drag.
   */
  public int y;
  
  /**
   * The type of data requested. Data provided in the data field must be of the
   * same type.
   */
  public TransferData dataType;
  
  /**
   * The drag source image to be displayed during the drag.
   * <p>
   * A value of null indicates that no drag image will be displayed.
   * </p>
   * <p>
   * The default value is null.
   * </p>
   */
  public Image image;
  
  /**
   * In dragStart, the x offset (relative to the image) where the drag source
   * image will be displayed.
   */
  public int offsetX;
  
  /**
   * In dragStart, the y offset (relative to the image) where the drag source
   * image will be displayed.
   */
  public int offsetY;
  
  /**
   * the time that the event occurred.
   * 
   * NOTE: This field is an unsigned integer and should
   * be AND'ed with 0xFFFFFFFFL so that it can be treated
   * as a signed long.
   */ 
  // TODO [rh] in SWT, the field 'time' is declared in TypedEvent
  public int time;
  
  public DragSourceEvent( final Widget widget, final int id ) {
    super( widget, id );
  }

  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " operation="
           + detail
           + " type="
           + ( dataType != null ? dataType.type : 0 )
           + " doit="
           + doit
           + "}";
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case DRAG_START:
        ( ( DragSourceListener )listener ).dragStart( this );
      break;
      case DRAG_END:
        ( ( DragSourceListener )listener ).dragFinished( this );
      break;
      case DRAG_SET_DATA:
        ( ( DragSourceListener )listener ).dragSetData( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static void addListener( final Adaptable adaptable,
                                  final DragSourceListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final DragSourceListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
