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

import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackManager;
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
   * @see org.eclipse.rap.rwt.SingletonUtil
   * @see org.eclipse.rap.rwt.internal.service.ContextProvider
   */
  public static void runNonUIThreadWithFakeContext( Display display, Runnable runnable ) {
    if( display == null || runnable == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    FakeContextUtil.runNonUIThreadWithFakeContext( display, runnable );
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
