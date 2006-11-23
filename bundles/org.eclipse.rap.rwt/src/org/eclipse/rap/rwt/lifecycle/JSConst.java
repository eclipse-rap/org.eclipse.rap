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

package org.eclipse.rap.rwt.lifecycle;

/**
 * TODO [rh] JavaDoc
 */
public final class JSConst {
  
  //////////////////////////
  // Request parameter names
  
  // RWT keys used to identify which kind of RWT-Event is requested
  public static final String EVENT_WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.events.widgetSelected";
  public static final String EVENT_WIDGET_RESIZED 
    = "org.eclipse.rap.rwt.events.widgetResized";
  public static final String EVENT_WIDGET_MOVED
    = "org.eclipse.rap.rwt.events.widgetMoved";

  /** 
   * <p>Indicates that a shell was closed on the client side. The parameter 
   * value holds the id of the shell that was closed.</p> */
  public static final String EVENT_SHELL_CLOSED 
    = "org.eclipse.rap.rwt.widgets.Shell_close";
  
  // function pointers for client side event handling
  public static final String JS_WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.EventUtil.widgetSelected";
  public static final String JS_WIDGET_RESIZED
    = "org.eclipse.rap.rwt.EventUtil.widgetResized";
  public static final String JS_WIDGET_MOVED
    = "org.eclipse.rap.rwt.EventUtil.widgetMoved";
  public static final String JS_TEXT_MODIFIED
    = "org.eclipse.rap.rwt.EventUtil.modify";
  public static final String JS_SHELL_CLOSED
    = "org.eclipse.rap.rwt.EventUtil.shellClosed";
  public static final String JS_TREE_SELECTED 
    = "org.eclipse.rap.rwt.TreeUtil.widgetSelected";
  public static final String JS_CONTEXT_MENU 
    = "org.eclipse.rap.rwt.MenuUtil.contextMenu";
  
  // keys of the Qooxdoo listeners, used to register the client side 
  // eventhandlers 
  public static final String QX_EVENT_EXECUTE = "execute";
  public static final String QX_EVENT_CHANGE_LOCATION_X = "changeLeft";
  public static final String QX_EVENT_CHANGE_LOCATION_Y = "changeTop";
  public static final String QX_EVENT_CHANGE_WIDTH = "changeWidth";
  public static final String QX_EVENT_CHANGE_HEIGHT = "changeHeight";
  public static final String QX_EVENT_CHANGE_VISIBILITY = "changeVisibility";
  public static final String QX_EVENT_INPUT = "input";
  public static final String QX_EVENT_BLUR = "blur";
  public static final String QX_EVENT_CLICK = "click";
  public static final String QX_EVENT_CONTEXTMENU = "contextmenu";
  public static final String QX_EVENT_CHANGE_SELECTED = "changeSelected";
  public static final String QX_EVENT_CHANGE_CHECKED = "changeChecked";
  // field names
  public static final String QX_FIELD_LABEL = "label";
  public static final String QX_FIELD_ICON = "icon";
  
  private JSConst() {
    // prevent instantiation
  }
}
