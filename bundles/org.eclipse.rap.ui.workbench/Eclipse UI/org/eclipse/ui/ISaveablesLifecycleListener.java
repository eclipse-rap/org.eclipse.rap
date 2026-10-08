/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui;

/**
 * Listener for events fired by implementers of {@link ISaveablesSource}.
 * 
 * <p>
 * This service can be acquired from a part's service locator:
 * 
 * <pre>
 * ISaveablesLifecycleListener listener = (ISaveablesLifecycleListener) getSite()
 * 		.getService(ISaveablesLifecycleListener.class);
 * </pre>
 * 
 * or, in the case of implementers of {@link ISaveablesSource} that are not a
 * part, from the workbench:
 * 
 * <pre>
 * ISaveablesLifecycleListener listener = (ISaveablesLifecycleListener) workbench
 * 		.getService(ISaveablesLifecycleListener.class);
 * </pre>
 * 
 * <ul>
 * <li>This service is available globally.</li>
 * </ul>
 * </p>
 * 
 * @since 1.0
 */
public interface ISaveablesLifecycleListener {

	/**
	 * Handle the given event. This method must be called on the UI thread.
	 * 
	 * @param event
	 */
	public void handleLifecycleEvent(SaveablesLifecycleEvent event);

}
