/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.dnd;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Widget;

/**
 * The DropTargetEvent contains the event information passed in the methods of
 * the DropTargetListener.
 * 
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @since 1.3
 */
public class DropTargetEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int DRAG_ENTER = DND.DragEnter;
  public static final int DRAG_OVER = DND.DragOver;
  public static final int DRAG_LEAVE = DND.DragLeave;
  public static final int DROP_ACCEPT = DND.DropAccept;
  public static final int DROP = DND.Drop;
  public static final int DRAG_OPERATION_CHANGED = DND.DragOperationChanged;
  
  private static final Class LISTENER = DropTargetListener.class;

  /**
   * The x-cordinate of the cursor relative to the <code>Display</code>
   */
  public int x;

  /**
   * The y-cordinate of the cursor relative to the <code>Display</code>
   */
  public int y;
  
  /**
   * The operation being performed.
   * 
   * @see DND#DROP_NONE
   * @see DND#DROP_MOVE
   * @see DND#DROP_COPY
   * @see DND#DROP_LINK
   * @see DND#DROP_DEFAULT
   */
  
  public int detail;
  /**
   * A bitwise OR'ing of the operations that the DragSource can support (e.g.
   * DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK). The detail value must be a
   * member of this list or DND.DROP_NONE.
   * 
   * @see DND#DROP_NONE
   * @see DND#DROP_MOVE
   * @see DND#DROP_COPY
   * @see DND#DROP_LINK
   * @see DND#DROP_DEFAULT
   */
  public int operations;
  
  /**
   * A bitwise OR'ing of the drag under effect feedback to be displayed to the
   * user (e.g. DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL |
   * DND.FEEDBACK_EXPAND).
   * <p>
   * A value of DND.FEEDBACK_NONE indicates that no drag under effect will be
   * displayed.
   * </p>
   * <p>
   * Feedback effects will only be applied if they are applicable.
   * </p>
   * <p>
   * The default value is DND.FEEDBACK_SELECT.
   * </p>
   * 
   * @see DND#FEEDBACK_NONE
   * @see DND#FEEDBACK_SELECT
   * @see DND#FEEDBACK_INSERT_BEFORE
   * @see DND#FEEDBACK_INSERT_AFTER
   * @see DND#FEEDBACK_SCROLL
   * @see DND#FEEDBACK_EXPAND
   */
  public int feedback;

  /**
   * If the associated control is a table or tree, this field contains the item
   * located at the cursor coordinates.
   */
  public Widget item;

  /**
   * The type of data that will be dropped.
   */
  public TransferData currentDataType;

  /**
   * A list of the types of data that the DragSource is capable of providing.
   * The currentDataType must be a member of this list.
   */
  public TransferData[] dataTypes;

  /**
   * the time that the event occurred.
   * 
   * NOTE: This field is an unsigned integer and should
   * be AND'ed with 0xFFFFFFFFL so that it can be treated
   * as a signed long.
   */ 
  // TODO [rh] in SWT, the field 'time' is declared in TypedEvent
  public int time;
  
  public DropTargetEvent( final Widget widget, final int id ) {
    super( widget, id );
  }

  public String toString() {
    String string = super.toString();
    StringBuffer sb = new StringBuffer();
    sb.append( string.substring( 0, string.length() - 1 ) ); // remove trailing '}'
    sb.append( " x=" );
    sb.append( x );
    sb.append( " y=" );
    sb.append( y );
    sb.append( " item=" );
    sb.append( item );
    sb.append( " operations=" );
    sb.append( operations );
    sb.append( " operation=" );
    sb.append( detail );
    sb.append( " feedback=" );
    sb.append( feedback );
    sb.append( " dataTypes={ " );
    if( dataTypes != null ) {
      for( int i = 0; i < dataTypes.length; i++ ) {
        sb.append( dataTypes[ i ].type );
        sb.append( ' ' );
      }
    }
    sb.append( '}' );
    sb.append( " currentDataType=" );
    sb.append( currentDataType != null ? currentDataType.type : '0' );
    sb.append( '}' );
    return sb.toString();
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case DRAG_ENTER:
        ( ( DropTargetListener )listener ).dragEnter( this );
      break;
      case DRAG_OVER:
        ( ( DropTargetListener )listener ).dragOver( this );
      break;
      case DRAG_LEAVE:
        ( ( DropTargetListener )listener ).dragLeave( this );
      break;
      case DROP_ACCEPT:
        ( ( DropTargetListener )listener ).dropAccept( this );
      break;
      case DROP:
        ( ( DropTargetListener )listener ).drop( this );
      break;
      case DRAG_OPERATION_CHANGED:
        ( ( DropTargetListener )listener ).dragOperationChanged( this );
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
                                  final DropTargetListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final DropTargetListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
