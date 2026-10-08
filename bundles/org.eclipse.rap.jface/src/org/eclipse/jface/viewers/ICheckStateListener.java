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
package org.eclipse.jface.viewers;

/**
 * A listener which is notified of changes to the checked
 * state of items in checkbox viewers.
 *
 * @see CheckStateChangedEvent
 */
public interface ICheckStateListener {
    /**
     * Notifies of a change to the checked state of an element.
     *
     * @param event event object describing the change
     */
    void checkStateChanged(CheckStateChangedEvent event);
}
