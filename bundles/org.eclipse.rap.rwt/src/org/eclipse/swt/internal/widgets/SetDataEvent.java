/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

import com.w4t.Adaptable;


public class SetDataEvent extends TypedEvent {

  public static final int SET_DATA = SWT.SetData;
  
  private static final Class LISTENER = SetDataListener.class;

  public Item item;
  public int index;
  
  public SetDataEvent( final Control source, final Item item, final int index ) 
  {
    super( source, SET_DATA );
    this.item = item;
    this.index = index;
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case SET_DATA:
        ( ( SetDataListener )listener ).update( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final SetDataListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final SetDataListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
