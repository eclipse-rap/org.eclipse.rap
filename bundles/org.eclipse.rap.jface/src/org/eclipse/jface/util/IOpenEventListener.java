/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
package org.eclipse.jface.util;

import java.io.Serializable;

import org.eclipse.swt.events.SelectionEvent;

/**
 * Listener for open events which are generated on selection
 * of default selection depending on the user preferences.
 * 
 * <p>
 * Usage:
 * <pre>
 *	OpenStrategy handler = new OpenStrategy(control);
 *	handler.addOpenListener(new IOpenEventListener() {
 *		public void handleOpen(SelectionEvent e) {
 *			... // code to handle the open event.
 *		}
 *	});
 * </pre>
 * </p>
 *
 * @see OpenStrategy
 */
public interface IOpenEventListener extends Serializable {
    /**
     * Called when a selection or default selection occurs 
     * depending on the user preference. 
     * @param e the selection event
     */
    public void handleOpen(SelectionEvent e);
}
