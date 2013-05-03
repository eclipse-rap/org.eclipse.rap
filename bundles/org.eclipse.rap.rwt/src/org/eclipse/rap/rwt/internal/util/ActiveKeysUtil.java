/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public final class ActiveKeysUtil {

  private static final Map<String,Integer> KEY_MAP = new HashMap<String,Integer>();
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
  }
  private final static String ALT = "ALT+";
  private final static String CTRL = "CTRL+";
  private final static String SHIFT = "SHIFT+";

  final static String PROP_ACTIVE_KEYS = "activeKeys";
  final static String PROP_CANCEL_KEYS = "cancelKeys";
  final static String PROP_MNEMONIC_ACTIVATOR = "mnemonicActivator";


  private ActiveKeysUtil() {
    // prevent instantiation
  }

  public static void preserveActiveKeys( Display display ) {
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_ACTIVE_KEYS, getActiveKeys( display ) );
  }

  public static void preserveActiveKeys( Control control ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( PROP_ACTIVE_KEYS, getActiveKeys( control ) );
  }

  public static void preserveCancelKeys( Display display ) {
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_CANCEL_KEYS, getCancelKeys( display ) );
  }

  public static void preserveCancelKeys( Control control ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( PROP_CANCEL_KEYS, getCancelKeys( control ) );
  }

  public static void preserveMnemonicActivator( Display display ) {
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_MNEMONIC_ACTIVATOR, getMnemonicActivator( display ) );
  }

  public static void renderActiveKeys( Display display ) {
    if( !display.isDisposed() ) {
      WidgetAdapter adapter = DisplayUtil.getAdapter( display );
      String[] newValue = getActiveKeys( display );
      String[] oldValue = ( String[] )adapter.getPreserved( PROP_ACTIVE_KEYS );
      boolean hasChanged = !Arrays.equals( oldValue, newValue );
      if( hasChanged ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( display );
        clientObject.set( "activeKeys", translateKeySequences( newValue ) );
      }
    }
  }

  public static void renderActiveKeys( Control control ) {
    if( !control.isDisposed() ) {
      WidgetAdapter adapter = WidgetUtil.getAdapter( control );
      String[] newValue = getActiveKeys( control );
      String[] oldValue = ( String[] )adapter.getPreserved( PROP_ACTIVE_KEYS );
      boolean hasChanged = !Arrays.equals( oldValue, newValue );
      if( hasChanged ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( control );
        clientObject.set( "activeKeys", translateKeySequences( newValue ) );
      }
    }
  }

  public static void renderCancelKeys( Display display ) {
    if( !display.isDisposed() ) {
      WidgetAdapter adapter = DisplayUtil.getAdapter( display );
      String[] newValue = getCancelKeys( display );
      String[] oldValue = ( String[] )adapter.getPreserved( PROP_CANCEL_KEYS );
      boolean hasChanged = !Arrays.equals( oldValue, newValue );
      if( hasChanged ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( display );
        clientObject.set( "cancelKeys", translateKeySequences( newValue ) );
      }
    }
  }

  public static void renderCancelKeys( Control control ) {
    if( !control.isDisposed() ) {
      WidgetAdapter adapter = WidgetUtil.getAdapter( control );
      String[] newValue = getCancelKeys( control );
      String[] oldValue = ( String[] )adapter.getPreserved( PROP_CANCEL_KEYS );
      boolean hasChanged = !Arrays.equals( oldValue, newValue );
      if( hasChanged ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( control );
        clientObject.set( "cancelKeys", translateKeySequences( newValue ) );
      }
    }
  }

  public static void renderMnemonicActivator( Display display ) {
    if( !display.isDisposed() ) {
      WidgetAdapter adapter = DisplayUtil.getAdapter( display );
      String newValue = getMnemonicActivator( display );
      String oldValue = ( String )adapter.getPreserved( PROP_MNEMONIC_ACTIVATOR );
      if( !equals( oldValue, newValue ) ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( display );
        clientObject.set( "mnemonicActivator", getModifierKeys( newValue ) );
      }
    }
  }

  private static String[] getActiveKeys( Display display ) {
    Object data = display.getData( RWT.ACTIVE_KEYS );
    String[] result = null;
    if( data != null ) {
      if( data instanceof String[] ) {
        result = getArrayCopy( data );
      } else {
        String mesg = "Illegal value for RWT.ACTIVE_KEYS in display data, must be a string array";
        throw new IllegalArgumentException( mesg );
      }
    }
    return result;
  }

  private static String[] getActiveKeys( Control control ) {
    Object data = control.getData( RWT.ACTIVE_KEYS );
    String[] result = null;
    if( data != null ) {
      if( data instanceof String[] ) {
        result = getArrayCopy( data );
      } else {
        String mesg = "Illegal value for RWT.ACTIVE_KEYS in widget data, must be a string array";
        throw new IllegalArgumentException( mesg );
      }
    }
    return result;
  }

  private static String[] getArrayCopy( Object data ) {
    String[] activeKeys = ( String[] )data;
    String[] result = new String[ activeKeys.length ];
    System.arraycopy( activeKeys, 0, result, 0, activeKeys.length );
    return result;
  }

  private static String[] getCancelKeys( Display display ) {
    String[] result = null;
    Object data = display.getData( RWT.CANCEL_KEYS );
    if( data != null ) {
      if( data instanceof String[] ) {
        result = getArrayCopy( data );
      } else {
        String mesg = "Illegal value for RWT.CANCEL_KEYS in display data, must be a string array";
        throw new IllegalArgumentException( mesg );
      }
    }
    return result;
  }

  private static String[] getCancelKeys( Control control ) {
    String[] result = null;
    Object data = control.getData( RWT.CANCEL_KEYS );
    if( data != null ) {
      if( data instanceof String[] ) {
        result = getArrayCopy( data );
      } else {
        String mesg = "Illegal value for RWT.CANCEL_KEYS in widget data, must be a string array";
        throw new IllegalArgumentException( mesg );
      }
    }
    return result;
  }

  private static String getMnemonicActivator( Display display ) {
    String result = null;
    Object data = display.getData( RWT.MNEMONIC_ACTIVATOR );
    if( data != null ) {
      if( data instanceof String ) {
        result = ( String )data;
        if( !result.endsWith( "+" ) ) {
          result += "+";
        }
      } else {
        String mesg = "Illegal value for RWT.MNEMONIC_ACTIVATOR in display data, must be a string";
        throw new IllegalArgumentException( mesg );
      }
    }
    return result;
  }

  private static JsonArray translateKeySequences( String[] activeKeys ) {
    JsonArray result = new JsonArray();
    if( activeKeys != null ) {
      for( int i = 0; i < activeKeys.length; i++ ) {
        result.add( translateKeySequence( activeKeys[ i ] ) );
      }
    }
    return result;
  }

  private static String translateKeySequence( String keySequence ) {
    if( keySequence == null ) {
      throw new NullPointerException( "Null argument" );
    }
    if( keySequence.trim().length() == 0 ) {
      throw new IllegalArgumentException( "Empty key sequence definition found" );
    }
    int lastPlusIndex = keySequence.lastIndexOf( "+" );
    String modifierPart = "";
    String keyPart = "";
    if( lastPlusIndex != -1 ) {
      modifierPart = keySequence.substring( 0, lastPlusIndex + 1 );
      keyPart = keySequence.substring( lastPlusIndex + 1 );
    } else {
      keyPart = keySequence;
    }
    int keyCode = getKeyCode( keyPart );
    if( keyCode != -1 ) {
      // TODO [tb] : use identifier instead of keycode
      keyPart = "#" + keyCode;
    }
    return getModifierKeys( modifierPart ) + keyPart;
  }

  private static String getModifierKeys( String modifier ) {
    StringBuilder result = new StringBuilder();
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
      throw new IllegalArgumentException( "Unrecognized modifier found in key sequence: " + modifier );
    }
    return result.toString();
  }

  private static int getKeyCode( String key ) {
    int result = -1;
    Object value = KEY_MAP.get( key );
    if( value instanceof Integer ) {
      result = ( ( Integer )value ).intValue();
    } else if( key.length() == 1 ) {
      if( Character.isLetterOrDigit( key.charAt( 0 ) ) ) {
        // NOTE: This works only for A-Z and 0-9 where keycode matches charcode
        result = key.toUpperCase().charAt( 0 );
      }
    } else {
      throw new IllegalArgumentException( "Unrecognized key: " + key );
    }
    return result;
  }

  private static boolean equals( Object object1, Object object2 ) {
    boolean result;
    if( object1 == object2 ) {
      result = true;
    } else if( object1 == null ) {
      result = false;
    } else {
      result = object1.equals( object2 );
    }
    return result;
  }

}
