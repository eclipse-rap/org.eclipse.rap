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
 * Convenience class for keeping effort of SWT to RWT transitions
 * as low as posssible.
 *  
 */
public class SWT {
  
  private SWT() {
    // prevent instance creation
  }
  
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
  public static final int SIMPLE = 1 << 6;
  public static final int BAR = 1 << 1;
  public static final int POP_UP = 1 << 3;
  public static final int DROP_DOWN = 1 << 2;
  public static final int CASCADE = 1 << 6;
  public static final int SINGLE = 1 << 2;
  public static final int MULTI = 1 << 1;
  public static final int WRAP = 1 << 6;
  public static final int PASSWORD = 1 << 22;
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
  public static final int ICON_ERROR = 1;
  public static final int ICON_INFORMATION = 1 << 1;
  public static final int ICON_QUESTION = 1 << 2;
  public static final int ICON_WARNING = 1 << 3;
  public static final int ICON_WORKING = 1 << 4;
  public static final int NORMAL = 0;
  public static final int BOLD = 1 << 0;
  public static final int ITALIC = 1 << 1;
  public static final int COLOR_WHITE = 1;
  public static final int COLOR_BLACK = 2;
  public static final int COLOR_RED = 3;
  public static final int COLOR_DARK_RED = 4;
  public static final int COLOR_GREEN = 5;
  public static final int COLOR_DARK_GREEN = 6;
  public static final int COLOR_YELLOW = 7;
  public static final int COLOR_DARK_YELLOW = 8;
  public static final int COLOR_BLUE = 9;
  public static final int COLOR_DARK_BLUE = 10;
  public static final int COLOR_MAGENTA = 11;
  public static final int COLOR_DARK_MAGENTA = 12;
  public static final int COLOR_CYAN = 13;
  public static final int COLOR_DARK_CYAN = 14;
  public static final int COLOR_GRAY = 15;
  public static final int COLOR_DARK_GRAY = 16;
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
}
