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

package org.eclipse.swt.custom;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.widgets.Widget;
import com.w4t.Adaptable;

public class CTabFolderEvent extends TypedEvent {
  
  private static final int CLOSE = 0;
  private static final int MINIMIZE = 1;
  private static final int MAXIMIZE = 2;
  private static final int RESTORE = 3;
  private static final int SHOW_LIST = 4;
  private static final Class LISTENER = CTabFolder2Listener.class;
  
  public static CTabFolderEvent close( final CTabItem item ) {
    CTabFolderEvent result = new CTabFolderEvent( item.getParent(), CLOSE );
    result.item = item;
    result.doit = true;
    return result;
  }
  
  public static CTabFolderEvent minimize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, MINIMIZE );
  }
  
  public static CTabFolderEvent maximize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, MAXIMIZE );
  }
  
  public static CTabFolderEvent restore( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, RESTORE );
  }
  
  public static CTabFolderEvent showList( final CTabFolder tabFolder ) {
    CTabFolderEvent result = new CTabFolderEvent( tabFolder, SHOW_LIST );
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    Rectangle chevronRect = folderAdapter.getChevronRect();    
    result.x = chevronRect.x;
    result.y = chevronRect.y;
    result.height = chevronRect.height;
    result.width = chevronRect.width;
    result.doit = true;
    return result;
  }
  
  public Widget item;
  public boolean doit;
  public int x;
  public int y;
  public int width;
  public int height;
  
  private CTabFolderEvent( final Object source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case CLOSE:
        ( ( CTabFolder2Listener )listener ).close( this );
      break;
      case MINIMIZE:
        ( ( CTabFolder2Listener )listener ).minimize( this );
      break;
      case MAXIMIZE:
        ( ( CTabFolder2Listener )listener ).maximize( this );
      break;
      case RESTORE:
        ( ( CTabFolder2Listener )listener ).restore( this );
      break;
      case SHOW_LIST:
        ( ( CTabFolder2Listener )listener ).showList( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static void addListener( final Adaptable adaptable,
                                  final CTabFolder2Listener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final CTabFolder2Listener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
  
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " item="
           + item
           + " doit="
           + doit
           + " x="
           + x
           + " y="
           + y
           + " width="
           + width
           + " height="
           + height
           + "}";
  }

}
