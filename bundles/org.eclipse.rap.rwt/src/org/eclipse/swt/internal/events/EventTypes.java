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
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.dnd.DND;


public final class EventTypes {
  
  public static final int MIN_SWT_EVENT = SWT.None;
  public static final int MAX_SWT_EVENT = SWT.Skin;
  
  public static final int MIN_DND_EVENT = DND.DragEnd;
  public static final int MAX_DND_EVENT = DND.DragStart;
  
  public static final int LOCALTION_CHANGING = 5011;
  public static final int LOCALTION_CHANGED = 5012;
  
  public static final int PROGRESS_CHANGED = 5021;
  public static final int PROGRESS_COMPLETED = 5022;

  public static final int CTAB_FOLDER_CLOSE = 5031;
  public static final int CTAB_FOLDER_MINIMIZE = 5032;
  public static final int CTAB_FOLDER_MAXIMIZE = 5033;
  public static final int CTAB_FOLDER_RESTORE = 5034;
  public static final int CTAB_FOLDER_SHOW_LIST = 5035;
  
  public static final int WIDGET_DESELECTED = 5051;
  
  public static final int[] EVENT_ORDER = {
    SWT.Move,
    SWT.Resize,
    SWT.Deactivate,
    SWT.Activate,
    SWT.Close,
    SWT.Hide,
    SWT.Show,
    SWT.Dispose,
    SWT.SetData,
    SWT.FocusOut,
    SWT.FocusIn,
    SWT.Traverse,
    SWT.KeyDown,
    SWT.Expand,
    SWT.Collapse,
    SWT.Verify,
    SWT.Modify,
    SWT.MouseDown,
    SWT.MouseDoubleClick,
    SWT.MenuDetect,
    CTabFolderEvent.CLOSE,
    CTabFolderEvent.MINIMIZE,
    CTabFolderEvent.MAXIMIZE,
    CTabFolderEvent.RESTORE,
    CTabFolderEvent.SHOW_LIST,
    DeselectionEvent.WIDGET_DESELECTED,
    SWT.Selection,
    SWT.DefaultSelection,
    SWT.MouseUp,
    SWT.Help,
    SWT.KeyUp,
    SWT.DragDetect,
    DND.DragStart,
    DND.DragEnd,
    DND.DragSetData,
    DND.DragEnter,
    DND.DragOver,
    DND.DragLeave,
    DND.DropAccept,
    DND.Drop,
    DND.DragOperationChanged,
    LocationEvent.CHANGING,
    LocationEvent.CHANGED,
    ProgressEvent.CHANGED,
    ProgressEvent.COMPLETED,
    SWT.Arm,
    SWT.Paint
  };


  private EventTypes() {
  }
}
