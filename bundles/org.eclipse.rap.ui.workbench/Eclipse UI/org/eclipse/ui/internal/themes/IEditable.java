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

package org.eclipse.ui.internal.themes;

/**
 * Theme elements which may potentially be editted by the user should implement 
 * this interface.
 * 
 */
public interface IEditable {

    /**
     * Returns whether this object is editable.
     * 
     * @return whether this object is editable.
     */
    public boolean isEditable();

}
