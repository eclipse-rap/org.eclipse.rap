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

package org.eclipse.swt.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import com.w4t.Adaptable;

/**
 * Instances of this class are sent as a result of
 * text being modified.
 *
 * @see ModifyListener
 */
public class ModifyEvent extends TypedEvent {

  public static final int MODIFY_TEXT = SWT.Modify;
  
  private static final Class LISTENER = ModifyListener.class;

  public ModifyEvent( final Control source ) {
    super( source, MODIFY_TEXT );
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case MODIFY_TEXT:
        ( ( ModifyListener )listener ).modifyText( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return true;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final ModifyListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final ModifyListener listener )
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
