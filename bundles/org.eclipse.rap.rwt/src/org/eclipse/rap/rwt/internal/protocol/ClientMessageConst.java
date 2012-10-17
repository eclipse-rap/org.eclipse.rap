/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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

  //////////////////////////
  // Request parameter names

  // SWT keys used to identify which kind of SWT-Event is requested
  public static final String EVENT_SELECTION = "Selection";
  public static final String EVENT_DEFAULT_SELECTION = "DefaultSelection";
  public static final String EVENT_WIDGET_RESIZED = "widgetResized";
  public static final String EVENT_WIDGET_MOVED = "widgetMoved";
  public static final String EVENT_CONTROL_ACTIVATED = "controlActivated";
  public static final String EVENT_SHELL_ACTIVATED = "shellActivated";
  public static final String EVENT_TREE_EXPANDED = "treeExpanded";
  public static final String EVENT_TREE_COLLAPSED = "treeCollapsed";
  public static final String EVENT_MODIFY_TEXT = "modifyText";
  public static final String EVENT_MENU_SHOWN = "menuShown";
  public static final String EVENT_MENU_HIDDEN = "menuHidden";
  public static final String EVENT_MOUSE_DOWN = "MouseDown";
  public static final String EVENT_MOUSE_UP = "MouseUp";
  public static final String EVENT_MOUSE_DOUBLE_CLICK = "MouseDoubleClick";
  public static final String EVENT_KEY_DOWN = "keyDown";
  public static final String EVENT_HELP = "Help";
  public static final String EVENT_MENU_DETECT = "menuDetect";

  // CTabFolder specific events
  public static final String EVENT_FOLDER_MINIMIZED = "ctabFolderMinimized";
  public static final String EVENT_FOLDER_MAXIMIZED = "ctabFolderMaximized";
  public static final String EVENT_FOLDER_RESTORED = "ctabFolderRestored";
  public static final String EVENT_SHOW_LIST = "ctabFolderShowList";

  // Indicates that a shell was closed on the client side.
  public static final String EVENT_SHELL_CLOSED = "shellClosed";

  public static final String EVENT_PARAM_DETAIL = "detail";
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
