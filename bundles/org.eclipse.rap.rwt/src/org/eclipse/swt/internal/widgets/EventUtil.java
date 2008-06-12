/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public final class EventUtil {

  private EventUtil() {
    // prevent instantiation
  }

  public static boolean isAccessible( final Widget widget ) {
    boolean result = !widget.isDisposed();
    if( result ) {
      if( widget instanceof Control ) {
        result = isAccessible( ( Control )widget );
      } else if( widget instanceof MenuItem ) {
        MenuItem menuItem = ( MenuItem )widget;
        result = isAccessible( menuItem );
      } else if( widget instanceof ToolItem ) {
        ToolItem toolItem = ( ToolItem )widget;
        result = isAccessible( toolItem );
      } else if( widget instanceof Menu ) {
        Menu menu = ( Menu )widget;
        result = isAccessible( menu );
      }
    }
    return result;
  }

  private static boolean isAccessible( final Control control ) {
    return    control.getEnabled() 
           && control.getVisible() 
           && isShellAccessible( control.getShell() );
  }

  private static boolean isAccessible( final Menu menu ) {
    return menu.getEnabled() && isShellAccessible( menu.getShell() );
  }

  private static boolean isAccessible( final MenuItem menuItem ) {
    Shell shell = menuItem.getParent().getShell();
    return menuItem.getEnabled() && isShellAccessible( shell );
  }

  private static boolean isAccessible( final ToolItem toolItem ) {
    Shell shell = toolItem.getParent().getShell();
    return toolItem.getEnabled() && isShellAccessible( shell );
  }

  private static boolean isShellAccessible( final Shell shell ) {
    Shell modalShell = null;
    Shell activeShell = shell.getDisplay().getActiveShell();
    if(    activeShell != null 
        && activeShell.isVisible() 
        && ( activeShell.getStyle() & SWT.APPLICATION_MODAL ) != 0 ) 
    {
      modalShell = activeShell;
    }
    return modalShell == null || shell == modalShell;
  }
}
