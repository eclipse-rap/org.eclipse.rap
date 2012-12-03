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



/**
 * @since 2.0
 * @deprecated Use {@link BrowserHistory} instead
 */
@Deprecated
public interface IBrowserHistory {

  /**
   * @deprecated Use {@link BrowserHistory} instead of {@link IBrowserHistory}
   */
  void createEntry( String id, String text );

  /**
   * @deprecated Use {@link BrowserHistory} instead of {@link IBrowserHistory}
   */
  void addBrowserHistoryListener( BrowserHistoryListener listener );

  /**
   * @deprecated Use {@link BrowserHistory} instead of {@link IBrowserHistory}
   */
  void removeBrowserHistoryListener( BrowserHistoryListener listener );

}
