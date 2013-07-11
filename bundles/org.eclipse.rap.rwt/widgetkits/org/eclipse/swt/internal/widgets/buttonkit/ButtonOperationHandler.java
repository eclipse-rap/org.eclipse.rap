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
package org.eclipse.swt.internal.widgets.buttonkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_BUTTON;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_CHAR_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_KEY_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_TIME;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_X;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_Y;
import static org.eclipse.swt.internal.events.EventLCAUtil.translateButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;


public class ButtonOperationHandler extends AbstractOperationHandler {

  private static final String PROP_SELECTION = "selection";

  private final Button button;

  public ButtonOperationHandler( Button button ) {
    this.button = button;
  }

  @Override
  public void handleNotify( String eventName, JsonObject properties ) {
    try {
      String name = "handleNotify" + eventName;
      Method method = getClass().getDeclaredMethod( name, JsonObject.class );
      method.invoke( this, properties );
    } catch( SecurityException exception ) {
      throw new RuntimeException( exception );
    } catch( NoSuchMethodException e ) {
      String message = eventName + " notify operation not supported by Button";
      throw new UnsupportedOperationException( message );
    } catch( IllegalArgumentException exception ) {
      throw new RuntimeException( exception );
    } catch( IllegalAccessException exception ) {
      throw new RuntimeException( exception );
    } catch( InvocationTargetException exception ) {
      throw new RuntimeException( exception );
    }
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifySelection( JsonObject properties ) {
    button.notifyListeners( SWT.Selection, createSelectionEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifyDefaultSelection( JsonObject properties ) {
    button.notifyListeners( SWT.DefaultSelection, createDefaultSelectionEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY FocusIn
   */
  public void handleNotifyFocusIn( JsonObject properties ) {
    button.notifyListeners( SWT.FocusIn, new Event() );
  }

  /*
   * PROTOCOL NOTIFY FocusOut
   */
  public void handleNotifyFocusOut( JsonObject properties ) {
    button.notifyListeners( SWT.FocusOut, new Event() );
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
  public void handleNotifyMouseDown( JsonObject properties ) {
    checkAndProcessMouseEvent( createMouseEvent( SWT.MouseDown, properties ) );
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
  public void handleNotifyMouseDoubleClick( JsonObject properties ) {
    checkAndProcessMouseEvent( createMouseEvent( SWT.MouseDoubleClick, properties ) );
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
  public void handleNotifyMouseUp( JsonObject properties ) {
    checkAndProcessMouseEvent( createMouseEvent( SWT.MouseUp, properties ) );
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
  public void handleNotifyTraverse( JsonObject properties ) {
    processTraverseEvent( properties );
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
  public void handleNotifyKeyDown( JsonObject properties ) {
    button.notifyListeners( SWT.KeyDown, createKeyEvent( properties ) );
    button.notifyListeners( SWT.KeyUp, createKeyEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY MenuDetect
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   */
  public void handleNotifyMenuDetect( JsonObject properties ) {
    button.notifyListeners( SWT.MenuDetect, createMenuDetectEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY Help
   */
  public void handleNotifyHelp( JsonObject properties ) {
    button.notifyListeners( SWT.Help, new Event() );
  }

  private Event createSelectionEvent( JsonObject properties ) {
    Event event = new Event();
    event.stateMask = readStateMask( properties );
    if( ( button.getStyle() & SWT.RADIO ) != 0 && !button.getSelection() ) {
      event.time = -1;
    }
    return event;
  }

  private static Event createDefaultSelectionEvent( JsonObject properties ) {
    Event event = new Event();
    event.stateMask = readStateMask( properties );
    return event;
  }

  private Event createMouseEvent( int eventType, JsonObject properties ) {
    Event event = new Event();
    event.type = eventType;
    event.widget = button;
    event.button = properties.get( EVENT_PARAM_BUTTON ).asInt();
    int x = properties.get( EVENT_PARAM_X ).asInt();
    int y = properties.get( EVENT_PARAM_Y ).asInt();
    Point point = button.getDisplay().map( null, button, x, y );
    event.x = point.x;
    event.y = point.y;
    event.time = properties.get( EVENT_PARAM_TIME ).asInt();
    event.stateMask = readStateMask( properties ) | translateButton( event.button );
    event.count = eventType == SWT.MouseDoubleClick ? 2 : 1;
    return event;
  }

  private Event createMenuDetectEvent( JsonObject properties ) {
    Event event = new Event();
    event.x = properties.get( EVENT_PARAM_X ).asInt();
    event.y = properties.get( EVENT_PARAM_Y ).asInt();
    return event;
  }

  private static void checkAndProcessMouseEvent( Event event ) {
    Button button = ( Button )event.widget;
    if( event.x >= 0 && event.y >= 0 ) {
      button.notifyListeners( event.type, event );
    }
  }

  private void processTraverseEvent( JsonObject properties ) {
    int keyCode = properties.get( EVENT_PARAM_KEY_CODE ).asInt();
    int charCode = properties.get( EVENT_PARAM_CHAR_CODE ).asInt();
    int stateMask = readStateMask( properties );
    int traverseKey = getTraverseKey( keyCode, stateMask );
    if( traverseKey != SWT.TRAVERSE_NONE ) {
      Event event = createKeyEvent( keyCode, charCode, stateMask );
      event.detail = traverseKey;
      button.notifyListeners( SWT.Traverse, event );
    }
  }

  private static Event createKeyEvent( JsonObject properties ) {
    int keyCode = properties.get( EVENT_PARAM_KEY_CODE ).asInt();
    int charCode = properties.get( EVENT_PARAM_CHAR_CODE ).asInt();
    int stateMask = readStateMask( properties );
    return createKeyEvent( keyCode, charCode, stateMask );
  }

  private static int readStateMask( JsonObject properties ) {
    boolean altKey = JsonValue.TRUE.equals( properties.get( "altKey" ) );
    boolean ctrlKey = JsonValue.TRUE.equals( properties.get( "ctrlKey" ) );
    boolean shiftKey = JsonValue.TRUE.equals( properties.get( "shiftKey" ) );
    return translateModifier( altKey, ctrlKey, shiftKey );
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

  private static int getTraverseKey( int keyCode, int stateMask ) {
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

  private static int translateKeyCode( int keyCode ) {
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

  private static int translateModifier( boolean altKey, boolean ctrlKey, boolean shiftKey ) {
    int result = 0;
    if( ctrlKey ) {
      result |= SWT.CTRL;
    }
    if( altKey ) {
      result |= SWT.ALT;
    }
    if( shiftKey ) {
      result |= SWT.SHIFT;
    }
    return result;
  }

  @Override
  public void handleSet( JsonObject properties ) {
    handleSetSelection( properties );
  }

  /*
   * PROTOCOL SET selection
   *
   * @param selection (boolean) true if the button was selected, otherwise false
   */
  private void handleSetSelection( JsonObject properties ) {
    JsonValue selection = properties.get( PROP_SELECTION );
    if( selection != null ) {
      button.setSelection( selection.asBoolean() );
    }
  }

}
