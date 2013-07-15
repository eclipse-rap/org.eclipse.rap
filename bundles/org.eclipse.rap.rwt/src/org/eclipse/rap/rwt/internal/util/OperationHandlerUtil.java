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
package org.eclipse.rap.rwt.internal.util;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_BUTTON;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_CHAR_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_DETAIL;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_HEIGHT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_KEY_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_TIME;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_WIDTH;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_X;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_Y;
import static org.eclipse.swt.internal.events.EventLCAUtil.translateButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Scrollable;


public class OperationHandlerUtil {

  public static void handleNotify( OperationHandler handler,
                                   String eventName,
                                   JsonObject properties )
  {
    try {
      String name = "handleNotify" + eventName;
      Method method = handler.getClass().getDeclaredMethod( name, JsonObject.class );
      method.invoke( handler, properties );
    } catch( SecurityException exception ) {
      throw new RuntimeException( exception );
    } catch( NoSuchMethodException e ) {
      String message = eventName + " notify operation not supported by this handler";
      throw new UnsupportedOperationException( message );
    } catch( IllegalArgumentException exception ) {
      throw new RuntimeException( exception );
    } catch( IllegalAccessException exception ) {
      throw new RuntimeException( exception );
    } catch( InvocationTargetException exception ) {
      throw new RuntimeException( exception );
    }
  }

  public static Event createSelectionEvent( int eventType, JsonObject properties )
  {
    Event event = new Event();
    event.type = eventType;
    event.stateMask = readStateMask( properties );
    event.detail = readDetail( properties );
    event.setBounds( readBounds( properties ) );
    return event;
  }

  public static void processMouseEvent( int eventType, Control control, JsonObject properties ) {
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

  public static Event createMouseEvent( int eventType, Control control, JsonObject properties ) {
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
    event.count = eventType == SWT.MouseDoubleClick ? 2 : 1;
    return event;
  }

  public static void processTraverseEvent( Control control, JsonObject properties ) {
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

  public static Event createKeyEvent( JsonObject properties ) {
    int keyCode = properties.get( EVENT_PARAM_KEY_CODE ).asInt();
    int charCode = properties.get( EVENT_PARAM_CHAR_CODE ).asInt();
    int stateMask = readStateMask( properties );
    return createKeyEvent( keyCode, charCode, stateMask );
  }

  public static Event createMenuDetectEvent( JsonObject properties ) {
    Event event = new Event();
    event.x = properties.get( EVENT_PARAM_X ).asInt();
    event.y = properties.get( EVENT_PARAM_Y ).asInt();
    return event;
  }

  private static Event createKeyEvent( int keyCode, int charCode, int stateMask ) {
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

  private static int readStateMask( JsonObject properties ) {
    int stateMask = SWT.NONE;
    if( JsonValue.TRUE.equals( properties.get( "altKey" ) ) ) {
      stateMask |= SWT.ALT;
    }
    if( JsonValue.TRUE.equals( properties.get( "ctrlKey" ) ) ) {
      stateMask |= SWT.CTRL;
    }
    if( JsonValue.TRUE.equals( properties.get( "shiftKey" ) ) ) {
      stateMask |= SWT.SHIFT;
    }
    return stateMask;
  }

  private static int readDetail( JsonObject properties ) {
    int detail = SWT.NONE;
    JsonValue value = properties.get( EVENT_PARAM_DETAIL );
    if( value != null && value.isString() ) {
      if( "check".equals( value.asString() ) ) {
        detail = SWT.CHECK;
      }
    }
    return detail;
  }

  private static Rectangle readBounds( JsonObject properties ) {
    Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
    JsonValue x = properties.get( EVENT_PARAM_X );
    bounds.x = x == null ? 0 : x.asInt();
    JsonValue y = properties.get( EVENT_PARAM_Y );
    bounds.y = y == null ? 0 : y.asInt();
    JsonValue width = properties.get( EVENT_PARAM_WIDTH );
    bounds.width = width == null ? 0 : width.asInt();
    JsonValue height = properties.get( EVENT_PARAM_HEIGHT );
    bounds.height = height == null ? 0 : height.asInt();
    return bounds;
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

  private OperationHandlerUtil() {
    // prevent instantiation
  }

}
