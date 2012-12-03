/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rap.rwt.client.service;

import org.eclipse.swt.internal.SWTEventListener;


/**
 * An event handler that is invoked after the user navigated to a previously
 * created history entry.
 * @see BrowserHistoryEvent
 * @see BrowserHistory
 * @since 2.0
 */
public interface BrowserHistoryListener extends SWTEventListener {

  /**
   * The event handler method.
   *
   * @param event the {@link BrowserHistoryEvent} object
   */
  void navigated( BrowserHistoryEvent event );
}
