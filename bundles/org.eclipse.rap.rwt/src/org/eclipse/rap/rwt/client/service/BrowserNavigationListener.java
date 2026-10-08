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

import org.eclipse.swt.internal.SWTEventListener;


/**
 * An event handler that is invoked whenever the user navigates within the application, i.e. changes
 * the fragment part of the URL. This can happen by pressing the browser's <em>back</em> button, by
 * selecting an item from the browser's history, or by manually changing the fragment part of the
 * URL in the browser's URL bar.
 *
 * @see BrowserNavigation
 * @since 2.0
 */
public interface BrowserNavigationListener extends SWTEventListener {

  /**
   * Called when the user navigated within the application.
   *
   * @param event the event that contains details of the navigation
   */
  void navigated( BrowserNavigationEvent event );

}
