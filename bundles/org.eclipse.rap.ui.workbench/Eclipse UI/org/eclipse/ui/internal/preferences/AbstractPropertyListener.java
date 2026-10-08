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
package org.eclipse.ui.internal.preferences;

/**
 */
public abstract class AbstractPropertyListener implements IPropertyMapListener {

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.preferences.IPropertyMapListener#propertyChanged(java.lang.String[])
     */
    public void propertyChanged(String[] propertyIds) {
        update();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.preferences.IPropertyMapListener#listenerAttached()
     */
    public void listenerAttached() {
        update();
    }

    protected abstract void update();
}
