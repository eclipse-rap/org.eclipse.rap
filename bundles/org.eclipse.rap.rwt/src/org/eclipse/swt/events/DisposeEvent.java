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

import com.w4t.Adaptable;

/**
 * Instances of this class are sent as a result of
 * widgets being disposed.
 *
 * @see DisposeListener
 */
public final class DisposeEvent extends TypedEvent {

  public static final int WIDGET_DISPOSED = SWT.Dispose;
  private static final Class LISTENER = DisposeListener.class;


  public DisposeEvent( final Object source ) {
    super( source, WIDGET_DISPOSED );
  }

  protected void dispatchToObserver( final Object listener ) {
    ( ( DisposeListener )listener ).widgetDisposed( this );
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final DisposeListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final DisposeListener listener )
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