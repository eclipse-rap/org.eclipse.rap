/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.tweaklets;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 */
public class AllowGrabFocus extends GrabFocus {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocusManager#allowGrabFocus(org.eclipse.ui.IWorkbenchPart)
	 */
	public boolean grabFocusAllowed(IWorkbenchPart part) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocusManager#init(Display)
	 */
	public void init(Display display) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocusManager#dispose()
	 */
	public void dispose() {
	}
}
