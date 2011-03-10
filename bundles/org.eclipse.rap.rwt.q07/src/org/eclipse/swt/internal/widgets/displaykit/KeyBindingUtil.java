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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IDisplayAdapter.IFilterEntry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;


public final class KeyBindingUtil {
  
  private static final String JSFUNC_SET_KEYBINDING_LIST
    = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings";

  private KeyBindingUtil() {
    // prevent instantiation
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
      String[] keyBindingList = ( String[] )display.getData( DisplayUtil.KEYBINDING_LIST );
      if( keyBindingList != null ) {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        HtmlResponseWriter writer = stateInfo.getResponseWriter();
        StringBuffer content = new StringBuffer();
        content.append( JSFUNC_SET_KEYBINDING_LIST );
        content.append( "(" );
        content.append( toJson( keyBindingList ) );
        content.append( ");" );
        writer.write( content.toString() );
        display.setData( DisplayUtil.KEYBINDING_LIST, null );
      }
    }
  }

  private static String toJson( String[] keyBindingList ) {
    StringBuffer json = new StringBuffer();
    json.append( "{" );
    for( int i = 0; i < keyBindingList.length; i++ ) {
      json.append( "\"" );
      json.append( getModifierKeys( keyBindingList[ i ] ) );
      json.append( getNaturalKey( keyBindingList[ i ] ) );
      json.append( "\":true" );
      if( i < keyBindingList.length - 1 ) {
        json.append( "," );
      }
    }
    json.append( "}" );
    return json.toString();
  }

  private static String getModifierKeys( String keyBinding ) {
    String modifierPart = keyBinding.substring( 0, keyBinding.indexOf( ',' ) );
    int modifierKeys = NumberFormatUtil.parseInt( modifierPart );
    StringBuffer result = new StringBuffer();
    if( ( modifierKeys & SWT.ALT ) != 0 ) {
      result.append( "ALT+" );
    }
    if( ( modifierKeys & SWT.CTRL ) != 0 ) {
      result.append( "CTRL+" );
    }
    if( ( modifierKeys & SWT.SHIFT ) != 0 ) {
      result.append( "SHIFT+" );
    }
    return result.toString();
  }

  private static int getNaturalKey( String keyBinding ) {
    String keyPart = keyBinding.substring( keyBinding.indexOf( ',' ) + 1 );
    int naturalKey = NumberFormatUtil.parseInt( keyPart );
    return translateNaturalKey( naturalKey );
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

  // translate key codes SWT -> qooxdoo
  private static int translateNaturalKey( int naturalKey ) {
    int result;
    switch( naturalKey ) {
      case SWT.CAPS_LOCK:
        result = 20;
      break;
      case SWT.ARROW_UP:
        result = 38;
      break;
      case SWT.ARROW_LEFT:
        result = 37;
      break;
      case SWT.ARROW_RIGHT:
        result = 39;
      break;
      case SWT.ARROW_DOWN:
        result = 40;
      break;
      case SWT.PAGE_UP:
        result = 33;
      break;
      case SWT.PAGE_DOWN:
        result = 34;
      break;
      case SWT.END:
        result = 35;
      break;
      case SWT.HOME:
        result = 36;
      break;
      case SWT.INSERT:
        result = 45;
      break;
      case SWT.DEL:
        result = 46;
      break;
      case SWT.F1:
        result = 112;
      break;
      case SWT.F2:
        result = 113;
      break;
      case SWT.F3:
        result = 114;
      break;
      case SWT.F4:
        result = 115;
      break;
      case SWT.F5:
        result = 116;
      break;
      case SWT.F6:
        result = 117;
      break;
      case SWT.F7:
        result = 118;
      break;
      case SWT.F8:
        result = 119;
      break;
      case SWT.F9:
        result = 120;
      break;
      case SWT.F10:
        result = 121;
      break;
      case SWT.F11:
        result = 122;
      break;
      case SWT.F12:
        result = 123;
      break;
      case SWT.NUM_LOCK:
        result = 144;
      break;
      case SWT.PRINT_SCREEN:
        result = 44;
      break;
      case SWT.SCROLL_LOCK:
        result = 145;
      break;
      case SWT.PAUSE:
        result = 19;
      break;
      case SWT.KEYPAD_0:
        result = 96;
      break;
      case SWT.KEYPAD_1:
        result = 97;
      break;
      case SWT.KEYPAD_2:
        result = 98;
      break;
      case SWT.KEYPAD_3:
        result = 99;
      break;
      case SWT.KEYPAD_4:
        result = 100;
      break;
      case SWT.KEYPAD_5:
        result = 101;
      break;
      case SWT.KEYPAD_6:
        result = 102;
      break;
      case SWT.KEYPAD_7:
        result = 103;
      break;
      case SWT.KEYPAD_8:
        result = 104;
      break;
      case SWT.KEYPAD_9:
        result = 105;
      break;
      case SWT.KEYPAD_MULTIPLY:
        result = 106;
      break;
      case SWT.KEYPAD_ADD:
        result = 107;
      break;
      case SWT.KEYPAD_SUBTRACT:
        result = 109;
      break;
      case SWT.KEYPAD_DECIMAL:
        result = 110;
      break;
      case SWT.KEYPAD_DIVIDE:
        result = 111;
      break;
      case ',':
        result = 188;
      break;
      case '.':
        result = 190;
      break;
      case '/':
        result = 191;
      break;
      case '`':
        result = 192;
      break;
      case '[':
        result = 219;
      break;
      case '\\':
        result = 220;
      break;
      case ']':
        result = 221;
      break;
      case '\'':
        result = 222;
      break;
      default:
        result = naturalKey;
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
