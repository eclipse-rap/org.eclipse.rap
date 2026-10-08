/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.ui.internal.presentations.util;


/**
 */
public abstract class TabDragHandler {

	// RAP [bm]: 
//    /**
//     * Returns the StackDropResult for the location being dragged over.
//     * 
//     * @param currentControl control being dragged over
//     * @param location mouse position (display coordinates)
//     * @param initialTab the index of the tab in this stack being dragged, 
//     * 			or -1 if dragging a tab from another stack. 
//     * @return the StackDropResult for this drag location
//     */
//    public abstract StackDropResult dragOver(Control currentControl,
//            Point location, int initialTab);

    public abstract int getInsertionPosition(Object cookie);
}
