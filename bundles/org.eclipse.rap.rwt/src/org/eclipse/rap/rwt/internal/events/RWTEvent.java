/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.events;

import java.util.EventObject;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventTable;
import org.eclipse.swt.widgets.TypedListener;


public abstract class RWTEvent extends EventObject {

  private static final long serialVersionUID = 1L;
  
  private final int id;
  
  public RWTEvent( Object source, int id ) {
    super( source );
    this.id = id;
  }
  
  public int getID() {
    return id;
  }
  
  protected static boolean hasListener( Adaptable adaptable, int[] eventTypes ) {
    boolean result = false;
    EventTable eventTable = adaptable.getAdapter( EventTable.class );
    for( int i = 0; !result && i < eventTypes.length; i++ ) {
      result = eventTable.hooks( eventTypes[ i ] );
    }
    return result;
  }

  protected static void addListener( Adaptable adaptable, 
                                     int[] eventTypes, 
                                     SWTEventListener listener ) 
  {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    TypedListener typedListener = new TypedListener( listener );
    EventTable eventTable = adaptable.getAdapter( EventTable.class );
    for( int eventType : eventTypes ) {
      eventTable.hook( eventType, typedListener );
    }
  }

  protected static void removeListener( Adaptable adaptable, 
                                        int[] eventTypes, 
                                        SWTEventListener listener ) 
  {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    EventTable eventTable = adaptable.getAdapter( EventTable.class );
    for( int eventType : eventTypes ) {
      eventTable.unhook( eventType, listener );
    }
  }
  
}