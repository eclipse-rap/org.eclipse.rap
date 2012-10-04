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
package org.eclipse.swt.internal.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;


public class EventTypes {
  
  public static final int MIN_SWT_EVENT = SWT.None;
  public static final int MAX_SWT_EVENT = SWT.Skin;
  
  public static final int PAINT = 9; // SWT.Paint
  
  public static final int MIN_DND_EVENT = DND.DragEnd;
  public static final int MAX_DND_EVENT = DND.DragStart;
  
  public static final int BROWSER_HISTORY_NAVIGATED = 5000;
  
  public static final int LOCALTION_CHANGING = 5011;
  public static final int LOCALTION_CHANGED = 5012;
  
  public static final int PROGRESS_CHANGED = 5021;
  public static final int PROGRESS_COMPLETED = 5022;

  public static final int CTAB_FOLDER_CLOSE = 5031;
  public static final int CTAB_FOLDER_MINIMIZE = 5032;
  public static final int CTAB_FOLDER_MAXIMIZE = 5033;
  public static final int CTAB_FOLDER_RESTORE = 5034;
  public static final int CTAB_FOLDER_SHOW_LIST = 5035;
  
  public static final int CONTROL_ACTIVATED = SWT.Activate;
  public static final int CONTROL_DEACTIVATED = SWT.Deactivate;
  
  public static final int WIDGET_DESELECTED = 5051;
  
}
