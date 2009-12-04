/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *   Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rwt;

import org.eclipse.rwt.events.BrowserHistoryListener;

/**
 * <p>
 * This interface rovides methods to use the browser's history for navigating
 * within the application. It is possible to create a history entry at the top
 * of the history stack and to handle a navigation change event.
 * </p>
 * <p>
 * Note that the browser history exists once per session, so using the
 * {@link IBrowserHistory} is only possible within the session context.
 * </p>
 *
 * @since 1.3
 */
public interface IBrowserHistory {

  /**
   * Creates an entry in the browser history.
   *
   * @param id Identifies the entry and should be unique among all entries.
   *          It is usually visible for the user within the address bar of
   *          the browser. Must neither be <code>null</code> not empty.
   * @param text A text for the user to identify the entry in the browser's UI
   *          or <code>null</code>.
   */
  void createEntry( String id, String text );

  /**
   * Adds a {@link BrowserHistoryListener} to the history support.
   *
   * @param listener the {@link BrowserHistoryListener}. Must not be
   *          <code>null</code>.
   */
  void addBrowserHistoryListener( BrowserHistoryListener listener );

  /**
   * Removes a {@link BrowserHistoryListener} from the history support.
   *
   * @param listener the {@link BrowserHistoryListener}. Must not be
   *          <code>null</code>.
   */
  void removeBrowserHistoryListener( BrowserHistoryListener listener );
}
