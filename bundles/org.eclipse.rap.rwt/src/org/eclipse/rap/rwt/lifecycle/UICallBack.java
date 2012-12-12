/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.internal.lifecycle.ContextUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


/**
 * A utility class that provides methods to perform tasks related
 * to UI updates from background threads.
 *
 * @since 2.0
 */
public final class UICallBack {

  /**
   * Executes a given runnable in the context of the UI session that is associated with the given
   * display. This allows background threads to access values that are stored in the UI session,
   * including session singletons.
   * 
   * @param display the display that is bound to the UI session which the runnable needs to access
   * @param runnable the runnable to execute in the UI session context of the given display
   * @see org.eclipse.rap.rwt.SingletonUtil
   * @deprecated Use {@link UISession#exec(Runnable)} instead. You can obtain a reference to the UI
   *             session for a display by {@link RWT#getUISession(Display)}. Example:
   *             <code>RWT.getUISession( display ).exec( runnable )</code>.
   */
  @Deprecated
  public static void runNonUIThreadWithFakeContext( Display display, Runnable runnable ) {
    if( display == null || runnable == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    ContextUtil.runNonUIThreadWithFakeContext( display, runnable );
  }

  /**
   * Call this method to enable UI updates from background threads (e.g. via
   * <code>Display.asyncExec()</code>}.
   * The UI callback must be activated from the UI thread before the background thread starts.
   * Each activation is given a session-unique id to allow reference-counting activation and
   * deactivation.
   *
   * <p>If not deactivated explicitly, any active UI callbacks are released when the session
   * terminates.</p>
   *
   * <p>Note: this method must only be called from the UI-Thread of an RWT application.</p>
   *
   * <p>
   * Example code:
   * <pre></code>
   * final String callbackId = "callback id";
   * Runnable bgRunnable = new Runnable() {
   *   public void run() {
   *   // do some work...
   *   // schedule the UI update
   *   display.asyncExec( new Runnable() {
   *     public void run() {
   *       if( !widget.isDisposed() ) {
   *         // update the UI
   *       }
   *     }
   *   } );
   *   // Deactivate the UI call-back
   *   UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
   *     public void run() {
   *       UICallBack.deactivate( callbackId );
   *     }
   *   } );
   *   }
   * };
   * UICallBack.activate( callbackId );
   * Thread bgThread = new Thread( bgRunnable );
   * bgThread.setDaemon( true );
   * bgThread.start();
   * </code></pre>
   * </p>
   *
   * @param id a session unique identifier to trace the activation and
   *           deactivation. Must not be <code>null</code>.
   *
   * </ul>
   * @throws SWTException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the id is <code>null</code></li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the UI thread</li>
   * </ul>
   *
   * @see Display#syncExec
   * @see Display#asyncExec
   * @see Display#getThread
   * @see Display#wake
   */
  public static void activate( String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !ContextProvider.hasContext() || CurrentPhase.get() == null ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    UICallBackManager.getInstance().activateUICallBacksFor( id );
  }

  /**
   * This method deactivates a previously {@link #activate(String) activated} UI callback
   * with the same <code>id</code>.
   * Calling this method with an id that wasn't activated before has no effect.
   *
   * <p>For each id, the system maintains a reference counter so that multiple activations
   * of the same id must be followed by the same number deactivations in oder to actually
   * stop the UI callback.</p>
   *
   * <p>Note: this method must only be called from code that is associated with a session.
   * That is, either code running in the UI thread or executed via
   * {@link UICallBack#runNonUIThreadWithFakeContext(Display, Runnable)}</p>
   *
   * @param id A session unique identifier to trace the activation and
   *           deactivation. Must not be <code>null</code>
   *
   * </ul>
   * @throws SWTException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the id is <code>null</code></li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from session code</li>
   * </ul>
   *
   * @see Display#syncExec
   * @see Display#asyncExec
   * @see Display#getThread
   * @see Display#wake
   */
  public static void deactivate( String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !ContextProvider.hasContext() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    UICallBackManager.getInstance().deactivateUICallBacksFor( id );
  }

  private UICallBack() {
    // prevent instance creation
  }
}
