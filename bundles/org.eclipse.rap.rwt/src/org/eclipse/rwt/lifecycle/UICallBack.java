/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.swt.widgets.Display;

/**
 * A utility class that provides some static helper methods to perform
 * commonly needed tasks with respect to background thread management.
 * 
 * @since 1.0
 */
public final class UICallBack {
    
  /**
   * Sometimes a background thread needs to access values that are stored
   * in the session object that started the thread. In particular these
   * values may be stored in session singletons. Accessing these singletons
   * directly from the background thread would fail. This method fakes the
   * missing request context and allows the runnable code to access those 
   * singletons.
   * 
   * @param display The display that is bound to the session that contains the
   *                data to which the current thread should get access.
   * @param runnable The runnable that contains the critical code that 
   *                 needs to have access to a request context.
   *        
   * @see org.eclipse.rwt.SessionSingletonBase
   * @see org.eclipse.rwt.internal.service.ContextProvider
   */
  public static void runNonUIThreadWithFakeContext( final Display display,
                                                    final Runnable runnable )
  {
    UICallBackServiceHandler.runNonUIThreadWithFakeContext( display,
                                                            runnable );
  }
  

  /**
   * To allow automatic UI-updates by server side background threads
   * activate the UICallBack mechanism. Call this method before the start of
   * a thread and {@link UICallBack#deactivate} at the end. Each activation
   * needs a session unique identifier as a kind of reference pointer to be able
   * to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   * 
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see Display#syncExec
   * @see Display#asyncExec
   * @see Display#getThread
   * @see Display#wake
   */
  public static void activate( final String id ) {
    UICallBackServiceHandler.activateUICallBacksFor( id );
  }
  
  /**
   * To allow automatic UI-updates by server side background threads
   * activate the UICallBack mechanism. Call {@link UICallBack#deactivate} method
   * before the start of a thread and deactivateUICallBack at the end. Each 
   * activation needs a session unique identifier as a kind of reference pointer
   * to be able to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   *          
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see Display#syncExec
   * @see Display#asyncExec
   * @see Display#getThread
   * @see Display#wake
   */
  public static void deactivate( final String id ) {
    UICallBackServiceHandler.deactivateUICallBacksFor( id );
  }
  
  private UICallBack() {
    // prevent instance creation
  }
}
