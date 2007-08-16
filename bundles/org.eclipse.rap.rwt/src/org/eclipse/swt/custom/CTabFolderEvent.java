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

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Widget;


public class CTabFolderEvent extends TypedEvent {
  
  // TODO [fappel]: Think about a better solution!
  //                Do not use SWT.None (0) as event handler identifier 
  //                -> causes problems with the filter implementation
  public static final int CLOSE = 1;
  public static final int MINIMIZE = 2;
  public static final int MAXIMIZE = 3;
  public static final int RESTORE = 4;
  public static final int SHOW_LIST = 5;
  
  private static final Class LISTENER = CTabFolder2Listener.class;
  
  public Widget item;
  public boolean doit;
  public int x;
  public int y;
  public int width;
  public int height;
  
  public CTabFolderEvent( final Object source, final int id ) {
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
  
  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
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
