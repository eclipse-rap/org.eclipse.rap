/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;


/**
 * Commonly used request parameter names and JavaScript names.
 */
public final class JSConst {

  //////////////////////////
  // Request parameter names

  // SWT keys used to identify which kind of SWT-Event is requested
  public static final String EVENT_WIDGET_SELECTED
    = "org.eclipse.swt.events.widgetSelected";
  public static final String EVENT_WIDGET_DEFAULT_SELECTED
    = "org.eclipse.swt.events.widgetDefaultSelected";
  public static final String EVENT_WIDGET_RESIZED
    = "org.eclipse.swt.events.widgetResized";
  public static final String EVENT_WIDGET_MOVED
    = "org.eclipse.swt.events.widgetMoved";
  public static final String EVENT_WIDGET_ACTIVATED
    = "org.eclipse.swt.events.controlActivated";
  public static final String EVENT_SHELL_ACTIVATED
    = "org.eclipse.swt.events.shellActivated";
  public static final String EVENT_TREE_EXPANDED
    = "org.eclipse.swt.events.treeExpanded";
  public static final String EVENT_TREE_COLLAPSED
    = "org.eclipse.swt.events.treeCollapsed";
  public static final String EVENT_MODIFY_TEXT
    = "org.eclipse.swt.events.modifyText";
  public static final String EVENT_MENU_SHOWN
    = "org.eclipse.swt.events.menuShown";
  public static final String EVENT_MENU_HIDDEN
    = "org.eclipse.swt.events.menuHidden";
  public static final String EVENT_SET_DATA
    = "org.eclipse.swt.events.setData";
  public static final String EVENT_MOUSE_UP
    = "org.eclipse.swt.events.mouseUp";
  public static final String EVENT_MOUSE_DOWN
    = "org.eclipse.swt.events.mouseDown";
  public static final String EVENT_MOUSE_DOUBLE_CLICK
    = "org.eclipse.swt.events.mouseDoubleClick";
  public static final String EVENT_KEY_DOWN
    = "org.eclipse.swt.events.keyDown";
  public static final String EVENT_HELP
    = "org.eclipse.swt.events.help";
  public static final String EVENT_MENU_DETECT
    = "org.eclipse.swt.events.menuDetect";

  // Request cell tooltip text event
  public static final String EVENT_CELL_TOOLTIP_REQUESTED
    = "org.eclipse.swt.events.cellToolTipTextRequested";

  // Parameter names that specify further event details
  public static final String EVENT_WIDGET_SELECTED_DETAIL
    = "org.eclipse.swt.events.widgetSelected.detail";
  public static final String EVENT_WIDGET_SELECTED_ITEM
    = "org.eclipse.swt.events.widgetSelected.item";
  public static final String EVENT_WIDGET_SELECTED_INDEX
    = "org.eclipse.swt.events.widgetSelected.index";
  public static final String EVENT_WIDGET_SELECTED_MODIFIER
    = "org.eclipse.swt.events.widgetSelected.modifier";
  public static final String EVENT_SET_DATA_INDEX
    = "org.eclipse.swt.events.setData.index";
  public static final String EVENT_MOUSE_UP_BUTTON
    = "org.eclipse.swt.events.mouseUp.button";
  public static final String EVENT_MOUSE_UP_X
    = "org.eclipse.swt.events.mouseUp.x";
  public static final String EVENT_MOUSE_UP_Y
    = "org.eclipse.swt.events.mouseUp.y";
  public static final String EVENT_MOUSE_UP_TIME
    = "org.eclipse.swt.events.mouseUp.time";
  public static final String EVENT_MOUSE_UP_MODIFIER
    = "org.eclipse.swt.events.mouseUp.modifier";
  public static final String EVENT_MOUSE_DOWN_BUTTON
    = "org.eclipse.swt.events.mouseDown.button";
  public static final String EVENT_MOUSE_DOWN_X
    = "org.eclipse.swt.events.mouseDown.x";
  public static final String EVENT_MOUSE_DOWN_Y
    = "org.eclipse.swt.events.mouseDown.y";
  public static final String EVENT_MOUSE_DOWN_TIME
    = "org.eclipse.swt.events.mouseDown.time";
  public static final String EVENT_MOUSE_DOWN_MODIFIER
    = "org.eclipse.swt.events.mouseDown.modifier";
  public static final String EVENT_MOUSE_DOUBLE_CLICK_BUTTON
    = "org.eclipse.swt.events.mouseDoubleClick.button";
  public static final String EVENT_MOUSE_DOUBLE_CLICK_X
    = "org.eclipse.swt.events.mouseDoubleClick.x";
  public static final String EVENT_MOUSE_DOUBLE_CLICK_Y
    = "org.eclipse.swt.events.mouseDoubleClick.y";
  public static final String EVENT_MOUSE_DOUBLE_CLICK_TIME
    = "org.eclipse.swt.events.mouseDoubleClick.time";
  public static final String EVENT_MOUSE_DOUBLE_CLICK_MODIFIER
    = "org.eclipse.swt.events.mouseDoubleClick.modifier";
  public static final String EVENT_KEY_DOWN_KEY_CODE
    = "org.eclipse.swt.events.keyDown.keyCode";
  public static final String EVENT_KEY_DOWN_CHAR_CODE
    = "org.eclipse.swt.events.keyDown.charCode";
  public static final String EVENT_KEY_DOWN_MODIFIER
    = "org.eclipse.swt.events.keyDown.modifier";
  public static final String EVENT_MENU_DETECT_X
    = "org.eclipse.swt.events.menuDetect.x";
  public static final String EVENT_MENU_DETECT_Y
    = "org.eclipse.swt.events.menuDetect.y";

  // Request cell tooltip text event details
  public static final String EVENT_CELL_TOOLTIP_DETAILS
    = "org.eclipse.swt.events.cellToolTipTextRequested.cell";

  // Indicates that a shell was closed on the client side. The parameter
  // value holds the id of the shell that was closed.
  public static final String EVENT_SHELL_CLOSED = "org.eclipse.swt.widgets.Shell_close";

  // field names
  public static final String QX_FIELD_LABEL = "label";
  public static final String QX_FIELD_ICON = "icon";
  public static final String QX_FIELD_SELECTION = "selection";
  public static final String QX_FIELD_FONT = "font";
  public static final String QX_FIELD_COLOR = "textColor";
  public static final String QX_FIELD_BG_COLOR = "backgroundColor";
  public static final String QX_FIELD_BG_GRADIENT = "backgroundGradient";
  public static final String QX_FIELD_ORIENTATION = "orientation";
  public static final String QX_FIELD_CAPTION = "caption";
  public static final String QX_FIELD_ENABLED = "enabled";
  public static final String QX_FIELD_EDITABLE = "editable";
  public static final String QX_FIELD_VISIBLE = "visibility";
  public static final String QX_FIELD_APPEARANCE = "appearance";
  public static final String QX_FIELD_Z_INDEX = "zIndex";
  public static final String QX_FIELD_TAB_INDEX = "tabIndex";
  public static final String QX_FIELD_CURSOR = "cursor";
  public static final String QX_FIELD_PADDING = "padding";

  // functions
  public static final String QX_FUNC_ADD_STATE = "addState";
  public static final String QX_FUNC_REMOVE_STATE = "removeState";

  private JSConst() {
    // prevent instantiation
  }
}
