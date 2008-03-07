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

package org.eclipse.swt.internal.widgets;

// TODO: [fappel] don't know whether it is a good idea to have a global
//                constant class for properties of different widgets...
public final class Props {

  // Control properties
  public static final String BOUNDS = "bounds";
  public static final String Z_INDEX = "zindex";
  public static final String MENU = "menu";
  public static final String VISIBLE = "visible";
  public static final String ENABLED = "enabled";
  public static final String CONTROL_LISTENERS = "hasControlListeners";
  
  // Scrollable
  public static final String CLIENT_AREA = "clientArea";

  // Button properties
  public static final String SELECTION_LISTENERS = "selectionListeners";
  public static final String ACTIVATE_LISTENER = "activateListener";
  public static final String FOCUS_LISTENER = "focusListener";
  
  // Text properties
  public static final String TEXT = "text";
  
  public static final String IMAGE = "image";
  
  // Table, TableItem and TableColumn properties
  public static final String SELECTION_INDICES = "selection";
  
  // CoolBar/CoolItem properties
  public static final String LOCKED = "locked";
  public static final String CONTROL = "control";
  
  public static final String TOOLTIP = "toolTip";
  
  public static final String BACKGROUND = "background";
  public static final String FOREGROUND = "foreground";
  public static final String FONT = "font";
  private Props() {
    // prevent instantiation
  }
}

