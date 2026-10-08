/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.ui.presentations;

import org.eclipse.swt.graphics.Point;

/**
 * Interface to a menu created by a part that will be displayed in a presentation.
 * 
 * This interface is not intended to be implemented by clients.
 * 
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPartMenu {
    /**
     * Displays the local menu for this part as a popup at the given location.
     * 
     * @param location position to display the menu at (display coordinates, not null)
     */
    public void showMenu(Point location);
}
