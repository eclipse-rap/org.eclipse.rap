/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.jface.dialogs;

import java.io.Serializable;

/**
 * A listener which is notified when the current page of the multi-page dialog
 * is changed.
 * 
 * @see IPageChangeProvider
 * @see PageChangedEvent
 * 
 * @since 1.0
 */
public interface IPageChangedListener extends Serializable {
	/**
	 * Notifies that the selected page has changed.
	 * 
	 * @param event
	 *            event object describing the change
	 */
	public void pageChanged(PageChangedEvent event);
}
