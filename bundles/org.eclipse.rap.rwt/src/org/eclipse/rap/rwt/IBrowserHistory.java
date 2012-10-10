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
package org.eclipse.rap.rwt;

import org.eclipse.rap.rwt.client.service.BrowserHistory;


/**
 * <p>
 * This interface provides methods to use the browser's history for navigating
 * within the application. It is possible to create a history entry at the top
 * of the history stack and to handle a navigation change event.
 * </p>
 * <p>
 * Note that the browser history exists once per session, so using the
 * {@link IBrowserHistory} is only possible within the session context.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @deprecated Use BrowserHistory interface instead
 */
public interface IBrowserHistory extends BrowserHistory {

}
