/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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

package org.eclipse.ui.internal.layout;

import org.eclipse.swt.widgets.Control;

/**
 * Layouts that implement this interface are capable of caching the
 * sizes of child controls in a manner that allows the information
 * for a single control to be flushed without affecting the remaining
 * controls. These layouts will ignore the "changed" arguments to layout(...)
 * and computeSize(...), however they will flush their cache for individual
 * controls when the flush(...) method is called.
 * <p>
 * This allows for much more efficient layouts, since most of the cache
 * can be reused when a child control changes.
 * </p>
 */
public interface ICachingLayout {
    /**
     * Flushes cached data for the given control
     */
    public void flush(Control dirtyControl);
}
