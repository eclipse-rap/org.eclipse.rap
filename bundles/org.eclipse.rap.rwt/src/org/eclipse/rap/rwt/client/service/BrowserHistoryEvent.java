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

import org.eclipse.swt.internal.SWTEventObject;


/**
 * Instances of this class provide information about a browser history navigation event.
 *
 * @see BrowserHistoryListener
 * @see BrowserHistory
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class BrowserHistoryEvent extends SWTEventObject {

  /**
   * The browser history entry to which the user navigated.
   */
  public String entryId;

  public BrowserHistoryEvent( Object source, String entryId ) {
    super( source );
    this.entryId = entryId;
  }

}
