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
package org.eclipse.ui.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A PlaceHolder is a non-visible stand-in for a layout part.
 */
public class PartPlaceholder extends LayoutPart {

    /**
     * Placeholder ids may contain wildcards.  This is the wildcard string.
     * 
     */
    public static String WILD_CARD = "*"; //$NON-NLS-1$

    public PartPlaceholder(String id) {
        super(id);
    }

    /**
     * Creates the SWT control
     */
    public void createControl(Composite parent) {
        // do nothing
    }

    /**
     * Get the part control.  This method may return null.
     */
    public Control getControl() {
        return null;
    }

    /**
     * Returns whether this placeholder has a wildcard.
     * 
     */
    public boolean hasWildCard() {
        return getID().indexOf(WILD_CARD) != -1;
    }
}
