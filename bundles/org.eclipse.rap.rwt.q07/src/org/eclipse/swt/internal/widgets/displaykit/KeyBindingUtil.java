/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IDisplayAdapter.IFilterEntry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;


public final class KeyBindingUtil {

  private static final String JSFUNC_SET_KEYBINDING_LIST
    = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings";

  private static final Map KEY_MAP = new HashMap();
  static {
    KEY_MAP.put( "BACKSPACE", new Integer( 8 ) );
    KEY_MAP.put( "BS", new Integer( 8 ) );
    KEY_MAP.put( "TAB", new Integer( 9 ) );
    KEY_MAP.put( "RETURN", new Integer( 13 ) );
    KEY_MAP.put( "ENTER", new Integer( 13 ) );
    KEY_MAP.put( "CR", new Integer( 13 ) );
    KEY_MAP.put( "PAUSE", new Integer( 19 ) );
    KEY_MAP.put( "BREAK", new Integer( 19 ) );
    KEY_MAP.put( "CAPS_LOCK", new Integer( 20 ) );
    KEY_MAP.put( "ESCAPE", new Integer( 27 ) );
    KEY_MAP.put( "ESC", new Integer( 27 ) );
    KEY_MAP.put( "SPACE", new Integer( 32 ) );
    KEY_MAP.put( "PAGE_UP", new Integer( 33 ) );
    KEY_MAP.put( "PAGE_DOWN", new Integer( 34 ) );
    KEY_MAP.put( "END", new Integer( 35 ) );
    KEY_MAP.put( "HOME", new Integer( 36 ) );
    KEY_MAP.put( "ARROW_LEFT", new Integer( 37 ) );
    KEY_MAP.put( "ARROW_UP", new Integer( 38 ) );
    KEY_MAP.put( "ARROW_RIGHT", new Integer( 39 ) );
    KEY_MAP.put( "ARROW_DOWN", new Integer( 40 ) );
    KEY_MAP.put( "PRINT_SCREEN", new Integer( 44 ) );
    KEY_MAP.put( "INSERT", new Integer( 45 ) );
    KEY_MAP.put( "DEL", new Integer( 46 ) );
    KEY_MAP.put( "DELETE", new Integer( 46 ) );
    KEY_MAP.put( "F1", new Integer( 112 ) );
    KEY_MAP.put( "F2", new Integer( 113 ) );
    KEY_MAP.put( "F3", new Integer( 114 ) );
    KEY_MAP.put( "F4", new Integer( 115 ) );
    KEY_MAP.put( "F5", new Integer( 116 ) );
    KEY_MAP.put( "F6", new Integer( 117 ) );
    KEY_MAP.put( "F7", new Integer( 118 ) );
    KEY_MAP.put( "F8", new Integer( 119 ) );
    KEY_MAP.put( "F9", new Integer( 120 ) );
    KEY_MAP.put( "F10", new Integer( 121 ) );
    KEY_MAP.put( "F11", new Integer( 122 ) );
    KEY_MAP.put( "F12", new Integer( 123 ) );
    KEY_MAP.put( "NUMPAD_0", new Integer( 96 ) );
    KEY_MAP.put( "NUMPAD_1", new Integer( 97 ) );
    KEY_MAP.put( "NUMPAD_2", new Integer( 98 ) );
    KEY_MAP.put( "NUMPAD_3", new Integer( 99 ) );
    KEY_MAP.put( "NUMPAD_4", new Integer( 100 ) );
    KEY_MAP.put( "NUMPAD_5", new Integer( 101 ) );
    KEY_MAP.put( "NUMPAD_6", new Integer( 102 ) );
    KEY_MAP.put( "NUMPAD_7", new Integer( 103 ) );
    KEY_MAP.put( "NUMPAD_8", new Integer( 104 ) );
    KEY_MAP.put( "NUMPAD_9", new Integer( 105 ) );
    KEY_MAP.put( "NUMPAD_MULTIPLY", new Integer( 106 ) );
    KEY_MAP.put( "NUMPAD_ADD", new Integer( 107 ) );
    KEY_MAP.put( "NUMPAD_SUBTRACT", new Integer( 109 ) );
    KEY_MAP.put( "NUMPAD_DECIMAL", new Integer( 110 ) );
    KEY_MAP.put( "NUMPAD_DIVIDE", new Integer( 111 ) );
    KEY_MAP.put( "NUM_LOCK", new Integer( 144 ) );
    KEY_MAP.put( "SCROLL_LOCK", new Integer( 145 ) );
    KEY_MAP.put( ",", new Integer( 188 ) );
    KEY_MAP.put( ".", new Integer( 190 ) );
    KEY_MAP.put( "/", new Integer( 191 ) );
    KEY_MAP.put( "`", new Integer( 192 ) );
    KEY_MAP.put( "[", new Integer( 219 ) );
    KEY_MAP.put( "\\", new Integer( 220 ) );
    KEY_MAP.put( "]", new Integer( 221 ) );
    KEY_MAP.put( "'", new Integer( 222 ) );
  }
  private final static String ALT = "ALT+";
  private final static String CTRL = "CTRL+";
  private final static String SHIFT = "SHIFT+";

  final static String PROP_KEYBINDING_LIST = "keyBindingList";

  private KeyBindingUtil() {
    // prevent instantiation
  }

  static void preserveKeyBindings( Display display ) {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_KEYBINDING_LIST, getKeyBindingList( display ) );
  }

  static void readKeyBindingEvents( final Display display ) {
    if( wasEventSent( JSConst.EVENT_KEY_DOWN ) ) {
      final int keyCode = readIntParam( JSConst.EVENT_KEY_DOWN_KEY_CODE );
      final int charCode = readIntParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE );
      final int stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_KEY_DOWN_MODIFIER );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          Event event = createEvent( display, keyCode, charCode, stateMask );
          processEvent( display, event );
        }
      } );
    }
  }

  static void writeKeyBindings( Display display ) throws IOException {
    if( !display.isDisposed() ) {
      String[] newValue = getKeyBindingList( display );
      if( hasKeyBindingListChanged( display, newValue ) ) {
        StringBuffer content = new StringBuffer();
        content.append( JSFUNC_SET_KEYBINDING_LIST );
        content.append( "(" );
        content.append( toJson( newValue ) );
        content.append( ");" );
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        Writer responseWriter = stateInfo.getResponseWriter();
        responseWriter.write( content.toString() );
      }
    }
  }

  private static boolean hasKeyBindingListChanged( Display display, String[] newValue ) {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    String[] oldValue = ( String[] )adapter.getPreserved( PROP_KEYBINDING_LIST );
    return !Arrays.equals( oldValue, newValue );
  }

  private static String[] getKeyBindingList( Display display ) {
    String[] result = null;
    Object data = display.getData( DisplayUtil.KEYBINDING_LIST );
    if( data != null ) {
      if( data instanceof String[] ) {
        String[] keyBindingList = ( String[] )data;
        result = new String[ keyBindingList.length ];
        System.arraycopy( keyBindingList, 0, result, 0, keyBindingList.length );
      } else {
        throw new IllegalArgumentException( "Key binding list should be a string array" );
      }
    }
    return result;
  }

  private static String toJson( String[] keyBindingList ) {
    StringBuffer json = new StringBuffer();
    json.append( "{" );
    if( keyBindingList != null ) {
      for( int i = 0; i < keyBindingList.length; i++ ) {
        json.append( "\"" );
        json.append( translateKeyBinding( keyBindingList[ i ] ) );
        json.append( "\":true" );
        if( i < keyBindingList.length - 1 ) {
          json.append( "," );
        }
      }
    }
    json.append( "}" );
    return json.toString();
  }

  private static String translateKeyBinding( String keyBinding ) {
    if( keyBinding == null ) {
      throw new NullPointerException( "Null argument" );
    }
    if( keyBinding.trim().length() == 0 ) {
      throw new IllegalArgumentException( "Empty key binding definition" );
    }
    int lastPlusIndex = keyBinding.lastIndexOf( "+" );
    String modifierPart = "";
    String keyPart = "";
    if( lastPlusIndex != -1 ) {
      modifierPart = keyBinding.substring( 0, lastPlusIndex + 1 );
      keyPart = keyBinding.substring( lastPlusIndex + 1 );
    } else {
      keyPart = keyBinding;
    }
    return getModifierKeys( modifierPart ) + getKeyCode( keyPart );
  }

  private static String getModifierKeys( String modifier ) {
    StringBuffer result = new StringBuffer();
    // order modifiers
    if( modifier.indexOf( ALT ) != -1 ) {
      result.append( ALT );
    }
    if( modifier.indexOf( CTRL ) != -1 ) {
      result.append( CTRL );
    }
    if( modifier.indexOf( SHIFT ) != -1 ) {
      result.append( SHIFT );
    }
    if( modifier.length() != result.length() ) {
      throw new IllegalArgumentException( "Unrecognized modifier: " + modifier );
    }
    return result.toString();
  }

  private static int getKeyCode( String key ) {
    int result = -1;
    Object value = KEY_MAP.get( key );
    if( value instanceof Integer ) {
      result = ( ( Integer )value ).intValue();
    } else if( key.length() == 1 ) {
      result = key.charAt( 0 );
    } else {
      throw new IllegalArgumentException( "Unrecognized key: " + key );
    }
    return result;
  }

  private static Event createEvent( Display display, int keyCode, int charCode, int stateMask ) {
    Event event = new Event();
    event.display = display;
    event.type = SWT.KeyDown;
    if( charCode == 0 ) {
      event.keyCode = translateKeyCode( keyCode );
      if( ( event.keyCode & SWT.KEYCODE_BIT ) == 0 ) {
        event.character = translateCharacter( event.keyCode );
      }
    } else {
      event.keyCode = charCode;
      event.character = translateCharacter( charCode );
    }
    event.stateMask = stateMask;
    return event;
  }

  private static void processEvent( Display display, Event event ) {
    IFilterEntry[] filters = getFilterEntries( display );
    for( int i = 0; i < filters.length; i++ ) {
      if( filters[ i ].getType() == event.type ) {
        filters[ i ].getListener().handleEvent( event );
      }
    }
  }

  private static IFilterEntry[] getFilterEntries( Display display ) {
    IDisplayAdapter adapter
      = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return adapter.getFilters();
  }

  private static boolean wasEventSent( String eventName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( eventName );
    return "w1".equals( widgetId );
  }

  private static int readIntParam( String paramName ) {
    String value = readStringParam( paramName );
    return NumberFormatUtil.parseInt( value );
  }

  private static String readStringParam( String paramName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    return request.getParameter( paramName );
  }

  // translates key code qooxdoo -> SWT
  private static int translateKeyCode( int keyCode ) {
    int result;
    switch( keyCode ) {
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
      case 96:
        result = SWT.KEYPAD_0;
      break;
      case 97:
        result = SWT.KEYPAD_1;
      break;
      case 98:
        result = SWT.KEYPAD_2;
      break;
      case 99:
        result = SWT.KEYPAD_3;
      break;
      case 100:
        result = SWT.KEYPAD_4;
      break;
      case 101:
        result = SWT.KEYPAD_5;
      break;
      case 102:
        result = SWT.KEYPAD_6;
      break;
      case 103:
        result = SWT.KEYPAD_7;
      break;
      case 104:
        result = SWT.KEYPAD_8;
      break;
      case 105:
        result = SWT.KEYPAD_9;
      break;
      case 106:
        result = SWT.KEYPAD_MULTIPLY;
      break;
      case 107:
        result = SWT.KEYPAD_ADD;
      break;
      case 109:
        result = SWT.KEYPAD_SUBTRACT;
      break;
      case 110:
        result = SWT.KEYPAD_DECIMAL;
      break;
      case 111:
        result = SWT.KEYPAD_DIVIDE;
      break;
      case 188:
        result = ',';
      break;
      case 190:
        result = '.';
      break;
      case 191:
        result = '/';
      break;
      case 192:
        result = '`';
      break;
      case 219:
        result = '[';
      break;
      case 220:
        result = '\\';
      break;
      case 221:
        result = ']';
      break;
      case 222:
        result = '\'';
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
