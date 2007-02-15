/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt;



/**
 * TODO: [fappel] comment
 */
public class RWT {
  
  public static final int NULL = 0;
  public static final int NONE = 0;

  public static final int DEFAULT = -1;
  public static final int HORIZONTAL = 1 << 8;
  public static final int VERTICAL = 1 << 9;
  public static final int CENTER = 1 << 24;
  public static final int UP = 1 << 7;
  public static final int TOP = UP;
  public static final int DOWN = 1 << 10;
  public static final int BOTTOM = DOWN;
  public static final int LEAD = 1 << 14;
  public static final int LEFT = LEAD;
  public static final int TRAIL = 1 << 17;
  public static final int RIGHT = TRAIL;
  public static final int BEGINNING = 1;
  public static final int FILL = 4;
  public static final int KEYCODE_BIT = ( 1 << 24 );
  public static final int END = KEYCODE_BIT + 8;
  public static final int SEPARATOR = 1 << 1;
  public static final int PUSH = 1 << 3;
  public static final int RADIO = 1 << 4;
  public static final int CHECK = 1 << 5;
  public static final int ARROW = 1 << 2;
  public static final int TOGGLE = 1 << 1;
  public static final int BORDER = 1 << 11;
  public static final int FLAT = 1 << 23;
  public static final int NO_FOCUS = 1 << 19;
  public static final int H_SCROLL = 1 << 8;
  public static final int V_SCROLL = 1 << 9;
  public static final int READ_ONLY = 1 << 3;
  
  // Combo flags
  public static final int SIMPLE = 1 << 6;

  // menu flags
  public static final int BAR = 1 << 1;
  public static final int POP_UP = 1 << 3;
  public static final int DROP_DOWN = 1 << 2;
  public static final int CASCADE = 1 << 6;
  
  // text flags
  public static final int SINGLE = 1 << 2;
  public static final int MULTI = 1 << 1;
  public static final int WRAP = 1 << 6;
  public static final int PASSWORD = 1 << 22;
  

  // Shells
  public static final int NO_TRIM = 1 << 3;
  public static final int RESIZE = 1 << 4;
  public static final int TITLE = 1 << 5;
  public static final int CLOSE = 1 << 6;
  public static final int MIN = 1 << 7;
  public static final int MAX = 1 << 10;
  public static final int SHELL_TRIM = CLOSE | TITLE | MIN | MAX | RESIZE;
  public static final int DIALOG_TRIM = TITLE | CLOSE | BORDER;
  public static final int APPLICATION_MODAL = 1 << 16;

  public static final int SHADOW_IN = 1 << 2;
  public static final int SHADOW_OUT = 1 << 3;
  public static final int SHADOW_NONE = 1 << 5;

  // Dialog Icons

  /**
   * The <code>MessageBox</code> style constant for error icon
   * behavior (value is 1).
   */
  public static final int ICON_ERROR = 1;

  /**
   * The <code>MessageBox</code> style constant for information icon
   * behavior (value is 1&lt;&lt;1).
   */
  public static final int ICON_INFORMATION = 1 << 1;

  /**
   * The <code>MessageBox</code> style constant for question icon
   * behavior (value is 1&lt;&lt;2).
   */
  public static final int ICON_QUESTION = 1 << 2;

  /**
   * The <code>MessageBox</code> style constant for warning icon
   * behavior (value is 1&lt;&lt;3).
   */
  public static final int ICON_WARNING = 1 << 3;

  /**
   * The <code>MessageBox</code> style constant for "working" icon
   * behavior (value is 1&lt;&lt;4).
   */
  public static final int ICON_WORKING = 1 << 4;

  // Font style constants
  /**
   * The font style constant indicating a normal weight, non-italic font
   * (value is 0).
   */
  public static final int NORMAL = 0;
  
  /**
   * The font style constant indicating a bold weight font
   * (value is 1&lt;&lt;0).
   */
  public static final int BOLD = 1 << 0;
  
  /**
   * The font style constant indicating an italic font
   * (value is 1&lt;&lt;1).
   */
  public static final int ITALIC = 1 << 1;

  // Color constants
  /**
   * Default color white (value is 1).
   */
  public static final int COLOR_WHITE = 1;

  /**
   * Default color black (value is 2).
   */
  public static final int COLOR_BLACK = 2;

  /**
   * Default color red (value is 3).
   */
  public static final int COLOR_RED = 3;

  /**
   * Default color dark red (value is 4).
   */
  public static final int COLOR_DARK_RED = 4;

  /**
   * Default color green (value is 5).
   */
  public static final int COLOR_GREEN = 5;

  /**
   * Default color dark green (value is 6).
   */
  public static final int COLOR_DARK_GREEN = 6;

  /**
   * Default color yellow (value is 7).
   */
  public static final int COLOR_YELLOW = 7;

  /**
   * Default color dark yellow (value is 8).
   */
  public static final int COLOR_DARK_YELLOW = 8;

  /**
   * Default color blue (value is 9).
   */
  public static final int COLOR_BLUE = 9;

  /**
   * Default color dark blue (value is 10).
   */
  public static final int COLOR_DARK_BLUE = 10;

  /**
   * Default color magenta (value is 11).
   */
  public static final int COLOR_MAGENTA = 11;

  /**
   * Default color dark magenta (value is 12).
   */
  public static final int COLOR_DARK_MAGENTA = 12;

  /**
   * Default color cyan (value is 13).
   */
  public static final int COLOR_CYAN = 13;

  /**
   * Default color dark cyan (value is 14).
   */
  public static final int COLOR_DARK_CYAN = 14;

  /**
   * Default color gray (value is 15).
   */
  public static final int COLOR_GRAY = 15;

  /**
   * Default color dark gray (value is 16).
   */
  public static final int COLOR_DARK_GRAY = 16;
  
  
  // Error codes
  public static final int ERROR_UNSPECIFIED = 1;
  public static final int ERROR_NO_HANDLES = 2;
  public static final int ERROR_NO_MORE_CALLBACKS = 3;
  public static final int ERROR_NULL_ARGUMENT = 4;
  public static final int ERROR_INVALID_ARGUMENT = 5;
  public static final int ERROR_INVALID_RANGE = 6;
  public static final int ERROR_CANNOT_BE_ZERO = 7;
  public static final int ERROR_CANNOT_GET_ITEM = 8;
  public static final int ERROR_CANNOT_GET_SELECTION = 9;
  public static final int ERROR_CANNOT_INVERT_MATRIX = 10;
  public static final int ERROR_CANNOT_GET_ITEM_HEIGHT = 11;
  public static final int ERROR_CANNOT_GET_TEXT = 12;
  public static final int ERROR_CANNOT_SET_TEXT = 13;
  public static final int ERROR_ITEM_NOT_ADDED = 14;
  public static final int ERROR_ITEM_NOT_REMOVED = 15;
  public static final int ERROR_NO_GRAPHICS_LIBRARY = 16;
  public static final int ERROR_NOT_IMPLEMENTED = 20;
  public static final int ERROR_MENU_NOT_DROP_DOWN = 21;
  public static final int ERROR_THREAD_INVALID_ACCESS = 22;
  public static final int ERROR_WIDGET_DISPOSED = 24;
  public static final int ERROR_MENUITEM_NOT_CASCADE = 27;
  public static final int ERROR_CANNOT_SET_SELECTION = 28;
  public static final int ERROR_CANNOT_SET_MENU = 29;
  public static final int ERROR_CANNOT_SET_ENABLED = 30;
  public static final int ERROR_CANNOT_GET_ENABLED = 31;
  public static final int ERROR_INVALID_PARENT = 32;
  public static final int ERROR_MENU_NOT_BAR = 33;
  public static final int ERROR_CANNOT_GET_COUNT = 36;
  public static final int ERROR_MENU_NOT_POP_UP = 37;
  public static final int ERROR_UNSUPPORTED_DEPTH = 38;
  public static final int ERROR_IO = 39;
  public static final int ERROR_INVALID_IMAGE = 40;
  public static final int ERROR_UNSUPPORTED_FORMAT = 42;
  public static final int ERROR_INVALID_SUBCLASS = 43;
  public static final int ERROR_GRAPHIC_DISPOSED = 44;
  public static final int ERROR_DEVICE_DISPOSED = 45;
  public static final int ERROR_FAILED_EXEC = 46;
  public static final int ERROR_FAILED_LOAD_LIBRARY = 47;
  public static final int ERROR_INVALID_FONT = 48;
  public static final int ALT = 1 << 16;

  public static final int SHIFT = 1 << 17;
  public static final int CTRL = 1 << 18;
  public static final int CONTROL = CTRL;
  public static final int COMMAND = 1 << 22;

  public static void error( final int code ) {
    error( code, null );
  }
  
  public static void error( final int code, final Throwable throwable ) {
    error( code, throwable, null );
  }

  public static void error( final int code, 
                            final Throwable throwable, 
                            final String detail ) 
  {
    /*
    * This code prevents the creation of "chains" of SWTErrors and
    * SWTExceptions which in turn contain other SWTErrors and 
    * SWTExceptions as their throwable. This can occur when low level
    * code throws an exception past a point where a higher layer is
    * being "safe" and catching all exceptions. (Note that, this is
    * _a_bad_thing_ which we always try to avoid.)
    *
    * On the theory that the low level code is closest to the
    * original problem, we simply re-throw the original exception here.
    */
    if( throwable instanceof RWTError ) {
      throw ( RWTError )throwable;
    }
    if( throwable instanceof RWTException ) {
      throw ( RWTException )throwable;
    }
    String message = findErrorText( code );
    if( detail != null ) {
      message += detail;
    }
    switch( code ) {
      /* Null Arguments (non-fatal) */
      case ERROR_NULL_ARGUMENT:
        throw new NullPointerException( message );
      /* Illegal Arguments (non-fatal) */
      case ERROR_CANNOT_BE_ZERO:
      case ERROR_INVALID_ARGUMENT:
      case ERROR_MENU_NOT_BAR:
      case ERROR_MENU_NOT_DROP_DOWN:
      case ERROR_MENU_NOT_POP_UP:
      case ERROR_MENUITEM_NOT_CASCADE:
      case ERROR_INVALID_PARENT:
      case ERROR_INVALID_RANGE:
        throw new IllegalArgumentException( message );
        /* SWT Exceptions (non-fatal) */
      case ERROR_INVALID_SUBCLASS:
      case ERROR_THREAD_INVALID_ACCESS:
      case ERROR_WIDGET_DISPOSED:
      case ERROR_GRAPHIC_DISPOSED:
      case ERROR_DEVICE_DISPOSED:
      case ERROR_INVALID_IMAGE:
      case ERROR_UNSUPPORTED_DEPTH:
      case ERROR_UNSUPPORTED_FORMAT:
      case ERROR_FAILED_EXEC:
      case ERROR_CANNOT_INVERT_MATRIX:
      case ERROR_NO_GRAPHICS_LIBRARY:
      case ERROR_IO:
        RWTException exception = new RWTException( code, message );
        exception.throwable = throwable;
        throw exception;
      /* Operation System Errors (fatal, may occur only on some platforms) */
      case ERROR_CANNOT_GET_COUNT:
      case ERROR_CANNOT_GET_ENABLED:
      case ERROR_CANNOT_GET_ITEM:
      case ERROR_CANNOT_GET_ITEM_HEIGHT:
      case ERROR_CANNOT_GET_SELECTION:
      case ERROR_CANNOT_GET_TEXT:
      case ERROR_CANNOT_SET_ENABLED:
      case ERROR_CANNOT_SET_MENU:
      case ERROR_CANNOT_SET_SELECTION:
      case ERROR_CANNOT_SET_TEXT:
      case ERROR_ITEM_NOT_ADDED:
      case ERROR_ITEM_NOT_REMOVED:
      case ERROR_NO_HANDLES:
      // FALL THROUGH
      /* SWT Errors (fatal, may occur only on some platforms) */
      case ERROR_FAILED_LOAD_LIBRARY:
      case ERROR_NO_MORE_CALLBACKS:
      case ERROR_NOT_IMPLEMENTED:
      case ERROR_UNSPECIFIED: {
        RWTError error = new RWTError( code, message );
        error.throwable = throwable;
        throw error;
      }
    }
    /* Unknown/Undefined Error */
    RWTError error = new RWTError( code, message );
    error.throwable = throwable;
    throw error;
  }

  static String findErrorText( final int code ) {
    String result;
    switch( code ) {
      case ERROR_UNSPECIFIED:
        result = "Unspecified error"; //$NON-NLS-1$
      break;
      case ERROR_NO_HANDLES:
        result = "No more handles"; //$NON-NLS-1$
      break;
      case ERROR_NO_MORE_CALLBACKS:
        result = "No more callbacks"; //$NON-NLS-1$
      break;
      case ERROR_NULL_ARGUMENT:
        result = "Argument cannot be null"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_ARGUMENT:
        result = "Argument not valid"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_RANGE:
        result = "Index out of bounds"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_BE_ZERO:
        result = "Argument cannot be zero"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ITEM:
        result = "Cannot get item"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_SELECTION:
        result = "Cannot get selection"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ITEM_HEIGHT:
        result = "Cannot get item height"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_TEXT:
        result = "Cannot get text"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_TEXT:
        result = "Cannot set text"; //$NON-NLS-1$
      break;
      case ERROR_ITEM_NOT_ADDED:
        result = "Item not added"; //$NON-NLS-1$
      break;
      case ERROR_ITEM_NOT_REMOVED:
        result = "Item not removed"; //$NON-NLS-1$
      break;
      case ERROR_NOT_IMPLEMENTED:
        result = "Not implemented"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_DROP_DOWN:
        result = "Menu must be a drop down"; //$NON-NLS-1$
      break;
      case ERROR_THREAD_INVALID_ACCESS:
        result = "Invalid thread access"; //$NON-NLS-1$
      break;
      case ERROR_WIDGET_DISPOSED:
        result = "Widget is disposed"; //$NON-NLS-1$
      break;
      case ERROR_MENUITEM_NOT_CASCADE:
        result = "Menu item is not a CASCADE"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_SELECTION:
        result = "Cannot set selection"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_MENU:
        result = "Cannot set menu"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_ENABLED:
        result = "Cannot set the enabled state"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ENABLED:
        result = "Cannot get the enabled state"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_PARENT:
        result = "Widget has the wrong parent"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_BAR:
        result = "Menu is not a BAR"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_COUNT:
        result = "Cannot get count"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_POP_UP:
        result = "Menu is not a POP_UP"; //$NON-NLS-1$
      break;
      case ERROR_UNSUPPORTED_DEPTH:
        result = "Unsupported color depth"; //$NON-NLS-1$
      break;
      case ERROR_IO:
        result = "i/o error"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_IMAGE:
        result = "Invalid image"; //$NON-NLS-1$
      break;
      case ERROR_UNSUPPORTED_FORMAT:
        result = "Unsupported or unrecognized format"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_SUBCLASS:
        result = "Subclassing not allowed"; //$NON-NLS-1$
      break;
      case ERROR_GRAPHIC_DISPOSED:
        result = "Graphic is disposed"; //$NON-NLS-1$
      break;
      case ERROR_DEVICE_DISPOSED:
        result = "Device is disposed"; //$NON-NLS-1$
      break;
      case ERROR_FAILED_EXEC:
        result = "Failed to execute runnable"; //$NON-NLS-1$
      break;
      case ERROR_FAILED_LOAD_LIBRARY:
        result = "Unable to load library"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_INVERT_MATRIX:
        result = "Cannot invert matrix"; //$NON-NLS-1$
      break;
      case ERROR_NO_GRAPHICS_LIBRARY:
        result = "Unable to load graphics library"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_FONT:
        result = "Font not valid"; //$NON-NLS-1$
      break;
      default:
        result = "Unknown error"; //$NON-NLS-1$
    }
    return result;
  }  
}
