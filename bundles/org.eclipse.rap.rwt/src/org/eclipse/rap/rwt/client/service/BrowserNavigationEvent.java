/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rap.rwt.client.service;

import java.util.EventObject;


/**
 * Instances of this class provide information about a browser navigation event.
 *
 * @see BrowserNavigationListener
 * @see BrowserNavigation
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BrowserNavigationEvent extends EventObject {

  private final String state;

  public BrowserNavigationEvent( Object source, String state ) {
    super( source );
    this.state = state;
  }

  /**
   * Return the browser history state to which the user navigated.
   *
   * @return the browser history state
   */
  public String getState() {
    return state;
  }

}
