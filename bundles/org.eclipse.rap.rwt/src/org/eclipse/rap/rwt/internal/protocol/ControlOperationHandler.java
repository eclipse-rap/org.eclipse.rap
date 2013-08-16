/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_ACTIVATE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEACTIVATE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_IN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_OUT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_HELP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MENU_DETECT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_UP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_BUTTON;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_CHAR_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_KEY_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_TIME;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_X;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_Y;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_TRAVERSE;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.translateButton;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_END;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_ENTER;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_LEAVE;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_OPERATION_CHANGED;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_OVER;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DRAG_START;
import static org.eclipse.swt.internal.widgets.displaykit.DNDSupport.EVENT_DROP_ACCEPT;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Scrollable;


public abstract class ControlOperationHandler<T extends Control> extends WidgetOperationHandler<T> {

  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_BACKGROUND = "background";

  public ControlOperationHandler( T control ) {
    super( control );
  }

  @Override
  public void handleSet( T control, JsonObject properties ) {
    handleSetForeground( control, properties );
    handleSetBackground( control, properties );
  }

  @Override
  public void handleNotify( T control, String eventName, JsonObject properties ) {
    if( EVENT_FOCUS_IN.equals( eventName ) ) {
      handleNotifyFocusIn( control, properties );
    } else if( EVENT_FOCUS_OUT.equals( eventName ) ) {
      handleNotifyFocusOut( control, properties );
    } else if( EVENT_MOUSE_DOWN.equals( eventName ) ) {
      handleNotifyMouseDown( control, properties );
    } else if( EVENT_MOUSE_DOUBLE_CLICK.equals( eventName ) ) {
      handleNotifyMouseDoubleClick( control, properties );
    } else if( EVENT_MOUSE_UP.equals( eventName ) ) {
      handleNotifyMouseUp( control, properties );
    } else if( EVENT_TRAVERSE.equals( eventName ) ) {
      handleNotifyTraverse( control, properties );
    } else if( EVENT_KEY_DOWN.equals( eventName ) ) {
      handleNotifyKeyDown( control, properties );
    } else if( EVENT_MENU_DETECT.equals( eventName ) ) {
      handleNotifyMenuDetect( control, properties );
    } else if( EVENT_HELP.equals( eventName ) ) {
      handleNotifyHelp( control, properties );
    } else if( EVENT_ACTIVATE.equals( eventName ) ) {
      handleNotifyActivate( control, properties );
    } else if( EVENT_DEACTIVATE.equals( eventName ) ) {
      handleNotifyDeactivate( control, properties );
    // TODO: [if] Transmit and handle the DND operations by DragSource/DropTarget
    } else if( EVENT_DRAG_START.equals( eventName ) ) {
      handleNotifyDragStart( control, properties );
    } else if( EVENT_DRAG_ENTER.equals( eventName ) ) {
      handleNotifyDragEnter( control, properties );
    } else if( EVENT_DRAG_OPERATION_CHANGED.equals( eventName ) ) {
      handleNotifyDragOperationChanged( control, properties );
    } else if( EVENT_DRAG_OVER.equals( eventName ) ) {
      handleNotifyDragOver( control, properties );
    } else if( EVENT_DRAG_LEAVE.equals( eventName ) ) {
      handleNotifyDragLeave( control, properties );
    } else if( EVENT_DROP_ACCEPT.equals( eventName ) ) {
      handleNotifyDropAccept( control, properties );
    } else if( EVENT_DRAG_END.equals( eventName ) ) {
      handleNotifyDragEnd( control, properties );
    } else {
      super.handleNotify( control, eventName, properties );
    }
  }

  /*
   * PROTOCOL SET foreground
   *
   * @param foreground ([int]) the foreground color of the control as RGB array or null
   */
  public void handleSetForeground( T control, JsonObject properties ) {
    JsonValue value = properties.get( PROP_FOREGROUND );
    if( value != null ) {
      Color foreground = null;
      if( !value.isNull() ) {
        JsonArray arrayValue = value.asArray();
        foreground = new Color( control.getDisplay(),
                                arrayValue.get( 0 ).asInt(),
                                arrayValue.get( 1 ).asInt(),
                                arrayValue.get( 2 ).asInt() );
      }
      control.setForeground( foreground );
    }
  }

  /*
   * PROTOCOL SET background
   *
   * @param foreground ([int]) the background color of the control as RGB array or null
   */
  public void handleSetBackground( T control, JsonObject properties ) {
    JsonValue value = properties.get( PROP_BACKGROUND );
    if( value != null ) {
      Color background = null;
      if( !value.isNull() ) {
        JsonArray arrayValue = value.asArray();
        background = new Color( control.getDisplay(),
                                arrayValue.get( 0 ).asInt(),
                                arrayValue.get( 1 ).asInt(),
                                arrayValue.get( 2 ).asInt() );
      }
      control.setBackground( background );
    }
  }

  /*
   * PROTOCOL NOTIFY FocusIn
   */
  public void handleNotifyFocusIn( T control, JsonObject properties ) {
    control.notifyListeners( SWT.FocusIn, new Event() );
  }

  /*
   * PROTOCOL NOTIFY FocusOut
   */
  public void handleNotifyFocusOut( T control, JsonObject properties ) {
    control.notifyListeners( SWT.FocusOut, new Event() );
  }

  /*
   * PROTOCOL NOTIFY MouseDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDown( T control, JsonObject properties ) {
    processMouseEvent( SWT.MouseDown, control, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseDoubleClick
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDoubleClick( T control, JsonObject properties ) {
    processMouseEvent( SWT.MouseDoubleClick, control, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseUp
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseUp( T control, JsonObject properties ) {
    processMouseEvent( SWT.MouseUp, control, properties );
  }

  /*
   * PROTOCOL NOTIFY Traverse
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyTraverse( T control, JsonObject properties ) {
    processTraverseEvent( control, properties );
  }

  /*
   * PROTOCOL NOTIFY KeyDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyKeyDown( T control, JsonObject properties ) {
    control.notifyListeners( SWT.KeyDown, createKeyEvent( properties ) );
    control.notifyListeners( SWT.KeyUp, createKeyEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY MenuDetect
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   */
  public void handleNotifyMenuDetect( T control, JsonObject properties ) {
    control.notifyListeners( SWT.MenuDetect, createMenuDetectEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY Help
   */
  public void handleNotifyHelp( T control, JsonObject properties ) {
    control.notifyListeners( SWT.Help, new Event() );
  }

  /*
   * PROTOCOL NOTIFY Activate
   *
   * ignored, Activate event is fired when set activeControl
   */
  public void handleNotifyActivate( T control, JsonObject properties ) {
  }

  /*
   * PROTOCOL NOTIFY Deactivate
   *
   * ignored, Deactivate event is fired when set activeControl
   */
  public void handleNotifyDeactivate( T control, JsonObject properties ) {
  }

  /*
   * PROTOCOL NOTIFY DragStart
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyDragStart( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DragEnter
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   * @param operation (String) "copy", "move" or "link", optional
   * @param feedback (int) the feedback as in DND.FEEDBACK_xxx
   * @param source (String) the id of the source control
   * @param item (String) the id of the source item
   * @param dataType (String)
   */
  public void handleNotifyDragEnter( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DragOperationChanged
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   * @param operation (String) "copy", "move" or "link", optional
   * @param feedback (int) the feedback as in DND.FEEDBACK_xxx
   * @param source (String) the id of the source control
   * @param item (String) the id of the source item
   * @param dataType (String)
   */
  public void handleNotifyDragOperationChanged( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DragOver
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   * @param operation (String) "copy", "move" or "link", optional
   * @param feedback (int) the feedback as in DND.FEEDBACK_xxx
   * @param source (String) the id of the source control
   * @param item (String) the id of the source item
   * @param dataType (String)
   */
  public void handleNotifyDragOver( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DragLeave
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   * @param operation (String) "copy", "move" or "link", optional
    * @param feedback (int) the feedback as in DND.FEEDBACK_xxx
   * @param source (String) the id of the source control
   * @param item (String) the id of the source item
   * @param dataType (String)
   */
  public void handleNotifyDragLeave( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DropAccept
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   * @param operation (String) "copy", "move" or "link", optional
   * @param feedback (int) the feedback as in DND.FEEDBACK_xxx
   * @param source (String) the id of the source control
   * @param item (String) the id of the source item
   * @param dataType (String)
   */
  public void handleNotifyDropAccept( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  /*
   * PROTOCOL NOTIFY DragEnd
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyDragEnd( T widget, JsonObject properties ) {
    // DND events are handled by DNDSupport
  }

  private static void processMouseEvent( int eventType, Control control, JsonObject properties ) {
    Event event = createMouseEvent( eventType, control, properties );
    boolean pass = false;
    if( control instanceof Scrollable ) {
      Scrollable scrollable = ( Scrollable )control;
      Rectangle clientArea = scrollable.getClientArea();
      pass = clientArea.contains( event.x, event.y );
    } else {
      pass = event.x >= 0 && event.y >= 0;
    }
    if( pass ) {
      control.notifyListeners( event.type, event );
    }
  }

  static Event createMouseEvent( int eventType, Control control, JsonObject properties ) {
    Event event = new Event();
    event.type = eventType;
    event.widget = control;
    event.button = properties.get( EVENT_PARAM_BUTTON ).asInt();
    int x = properties.get( EVENT_PARAM_X ).asInt();
    int y = properties.get( EVENT_PARAM_Y ).asInt();
    Point point = control.getDisplay().map( null, control, x, y );
    event.x = point.x;
    event.y = point.y;
    event.time = properties.get( EVENT_PARAM_TIME ).asInt();
    event.stateMask = readStateMask( properties ) | translateButton( event.button );
    // TODO: send count by the client
    event.count = determineCount( eventType, control );
    return event;
  }

  private static int determineCount( int eventType, Control control ) {
    if(    eventType == SWT.MouseDoubleClick
        || wasEventSent( getId( control ), EVENT_MOUSE_DOUBLE_CLICK ) )
    {
      return 2;
    }
    return 1;
  }

  private static void processTraverseEvent( Control control, JsonObject properties ) {
    int keyCode = properties.get( EVENT_PARAM_KEY_CODE ).asInt();
    int charCode = properties.get( EVENT_PARAM_CHAR_CODE ).asInt();
    int stateMask = readStateMask( properties );
    int traverseKey = getTraverseKey( keyCode, stateMask );
    if( traverseKey != SWT.TRAVERSE_NONE ) {
      Event event = createKeyEvent( keyCode, charCode, stateMask );
      event.detail = traverseKey;
      control.notifyListeners( SWT.Traverse, event );
    }
  }

  static Event createKeyEvent( JsonObject properties ) {
    int keyCode = properties.get( EVENT_PARAM_KEY_CODE ).asInt();
    int charCode = properties.get( EVENT_PARAM_CHAR_CODE ).asInt();
    int stateMask = readStateMask( properties );
    return createKeyEvent( keyCode, charCode, stateMask );
  }

  static Event createMenuDetectEvent( JsonObject properties ) {
    Event event = new Event();
    event.x = properties.get( EVENT_PARAM_X ).asInt();
    event.y = properties.get( EVENT_PARAM_Y ).asInt();
    return event;
  }

  static Event createKeyEvent( int keyCode, int charCode, int stateMask ) {
    Event event = new Event();
    event.keyCode = translateKeyCode( keyCode );
    if( charCode == 0 ) {
      if( ( event.keyCode & SWT.KEYCODE_BIT ) == 0 ) {
        event.character = translateCharacter( event.keyCode );
      }
    } else {
      event.character = translateCharacter( charCode );
      if( Character.isLetter( charCode ) ) {
        // NOTE : keycodes from browser are the upper-case character, in SWT it is the lower-case
        event.keyCode = Character.toLowerCase( charCode );
      }
    }
    event.stateMask = stateMask;
    return event;
  }

  static int getTraverseKey( int keyCode, int stateMask ) {
    int result = SWT.TRAVERSE_NONE;
    switch( keyCode ) {
      case 27:
        result = SWT.TRAVERSE_ESCAPE;
      break;
      case 13:
        result = SWT.TRAVERSE_RETURN;
      break;
      case 9:
        if( ( stateMask & SWT.MODIFIER_MASK ) == 0 ) {
          result = SWT.TRAVERSE_TAB_NEXT;
        } else if( stateMask == SWT.SHIFT ) {
          result = SWT.TRAVERSE_TAB_PREVIOUS;
        }
      break;
    }
    return result;
  }

  static int translateKeyCode( int keyCode ) {
    int result;
    switch( keyCode ) {
      case 16:
        result = SWT.SHIFT;
      break;
      case 17:
        result = SWT.CONTROL;
      break;
      case 18:
        result = SWT.ALT;
      break;
      case 20:
        result = SWT.CAPS_LOCK;
      break;
      case 38:
        result = SWT.ARROW_UP;
      break;
      case 37:
        result = SWT.ARROW_LEFT;
      break;
      case 39:
        result = SWT.ARROW_RIGHT;
      break;
      case 40:
        result = SWT.ARROW_DOWN;
      break;
      case 33:
        result = SWT.PAGE_UP;
      break;
      case 34:
        result = SWT.PAGE_DOWN;
      break;
      case 35:
        result = SWT.END;
      break;
      case 36:
        result = SWT.HOME;
      break;
      case 45:
        result = SWT.INSERT;
      break;
      case 46:
        result = SWT.DEL;
      break;
      case 112:
        result = SWT.F1;
      break;
      case 113:
        result = SWT.F2;
      break;
      case 114:
        result = SWT.F3;
      break;
      case 115:
        result = SWT.F4;
      break;
      case 116:
        result = SWT.F5;
      break;
      case 117:
        result = SWT.F6;
      break;
      case 118:
        result = SWT.F7;
      break;
      case 119:
        result = SWT.F8;
      break;
      case 120:
        result = SWT.F9;
      break;
      case 121:
        result = SWT.F10;
      break;
      case 122:
        result = SWT.F11;
      break;
      case 123:
        result = SWT.F12;
      break;
      case 144:
        result = SWT.NUM_LOCK;
      break;
      case 44:
        result = SWT.PRINT_SCREEN;
      break;
      case 145:
        result = SWT.SCROLL_LOCK;
      break;
      case 19:
        result = SWT.PAUSE;
      break;
      default:
        result = keyCode;
    }
    return result;
  }

  private static char translateCharacter( int keyCode ) {
    char result = ( char )0;
    if( Character.isDefined( ( char )keyCode ) ) {
      result = ( char )keyCode;
    }
    return result;
  }

}
