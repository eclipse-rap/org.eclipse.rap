/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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

import java.io.Serializable;

/**
 * An abstract column layout data describing the information needed 
 * (by <code>TableLayout</code>) to properly lay out a table. 
 * <p>
 * This class is not intended to be subclassed outside the framework.
 * </p>
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class ColumnLayoutData implements Serializable {

    /**
     * Indicates whether the column is resizable.
     */
    public boolean resizable;

    /**
     * Creates a new column layout data object.
     *
     * @param resizable <code>true</code> if the column is resizable, and <code>false</code> if not
     */
    protected ColumnLayoutData(boolean resizable) {
        this.resizable = resizable;
    }
}
