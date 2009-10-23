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

import java.util.EventListener;

import org.eclipse.rwt.events.BrowserHistoryEvent;

/**
 * An event handler that is invoked after the user navigated to a previously
 * created history entry.
 * @see BrowserHistoryEvent
 * @since 1.3
 */
public interface BrowserHistoryListener extends EventListener {

  /**
   * The event handler method.
   * 
   * @param event the {@link BrowserHistoryEvent} object
   */
  void navigated( BrowserHistoryEvent event );
}
