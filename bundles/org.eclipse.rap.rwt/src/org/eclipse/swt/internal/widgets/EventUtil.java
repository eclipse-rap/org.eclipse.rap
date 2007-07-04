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
    boolean result;
    result = control.getEnabled() && control.getVisible();
    if( result ) {
      Shell modalShell = getModalShell( control.getDisplay() );
      if( modalShell != null && control.getShell() != modalShell ) {
        result = false;
      }
    }
    return result;
  }

  private static boolean isAccessible( final Menu menu ) {
    boolean result = menu.getEnabled();
    if( result ) {
      Shell modalShell = getModalShell( menu.getDisplay() );
      if( modalShell != null && menu.getShell() != modalShell ) {
        result = false;
      }
    }
    return result;
  }
  
  private static boolean isAccessible( final MenuItem menuItem ) {
    boolean result = menuItem.getEnabled();
    if( result ) {
      Shell modalShell = getModalShell( menuItem.getDisplay() );
      if( modalShell != null && menuItem.getMenu().getShell() != modalShell ) {
        result = false;
      }
    }
    return result;
  }

  private static boolean isAccessible( final ToolItem toolItem ) {
    boolean result = toolItem.getEnabled();
    if( result ) {
      Shell modalShell = getModalShell( toolItem.getDisplay() );
      ToolBar toolBar = toolItem.getParent();
      if( modalShell != null && toolBar.getShell() != modalShell ) {
        result = false;
      }
    }
    return result;
  }
  
  private static Shell getModalShell( final Display display ) {
    Shell modalShell = null;
    Shell[] shells = display.getShells();
    for( int i = 0; modalShell == null && i < shells.length; i++ ) {
      Shell shell = shells[ i ];
      if(   ( shell.getStyle() & SWT.APPLICATION_MODAL ) != 0 
          && shell.isVisible() ) 
      {
        modalShell = shell;
      }
    }
    return modalShell;
  }
}
