/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;


/**
 * Commonly used client message event and parameter names.
 */
public final class ClientMessageConst {

  // Head parameters
  public static final String RWT_INITIALIZE = "rwt_initialize";
  public static final String RWT_SHUTDOWN = "rwt_shutdown";
  public static final String QUERY_STRING = "queryString";
  public static final String UI_SESSION = "uiSession";

  // SWT keys used to identify which kind of SWT-Event is requested
  public static final String EVENT_SELECTION = "Selection";
  public static final String EVENT_DEFAULT_SELECTION = "DefaultSelection";
  public static final String EVENT_RESIZE = "Resize";
  public static final String EVENT_MOVE = "Move";
  public static final String EVENT_ACTIVATE = "Activate";
  public static final String EVENT_EXPAND = "Expand";
  public static final String EVENT_COLLAPSE = "Collapse";
  public static final String EVENT_MODIFY = "Modify";
  public static final String EVENT_SHOW = "Show";
  public static final String EVENT_HIDE = "Hide";
  public static final String EVENT_MOUSE_DOWN = "MouseDown";
  public static final String EVENT_MOUSE_UP = "MouseUp";
  public static final String EVENT_MOUSE_DOUBLE_CLICK = "MouseDoubleClick";
  public static final String EVENT_KEY_DOWN = "KeyDown";
  public static final String EVENT_TRAVERSE = "Traverse";
  public static final String EVENT_HELP = "Help";
  public static final String EVENT_MENU_DETECT = "MenuDetect";

  // CTabFolder specific events
  public static final String EVENT_FOLDER = "Folder";
  public static final String EVENT_FOLDER_DETAIL_MINIMIZE = "minimize";
  public static final String EVENT_FOLDER_DETAIL_MAXIMIZE = "maximize";
  public static final String EVENT_FOLDER_DETAIL_RESTORE = "restore";
  public static final String EVENT_FOLDER_DETAIL_CLOSE = "close";
  public static final String EVENT_FOLDER_DETAIL_SHOW_LIST = "showList";

  // Indicates that a shell was closed on the client side.
  public static final String EVENT_CLOSE = "Close";

  public static final String EVENT_PARAM_DETAIL = "detail";
  public static final String EVENT_PARAM_TEXT = "text";
  public static final String EVENT_PARAM_ITEM = "item";
  public static final String EVENT_PARAM_INDEX = "index";
  public static final String EVENT_PARAM_MODIFIER = "modifier";
  public static final String EVENT_PARAM_BUTTON = "button";
  public static final String EVENT_PARAM_X = "x";
  public static final String EVENT_PARAM_Y = "y";
  public static final String EVENT_PARAM_WIDTH = "width";
  public static final String EVENT_PARAM_HEIGHT = "height";
  public static final String EVENT_PARAM_TIME = "time";
  public static final String EVENT_PARAM_KEY_CODE = "keyCode";
  public static final String EVENT_PARAM_CHAR_CODE = "charCode";

  private ClientMessageConst() {
    // prevent instantiation
  }
}
