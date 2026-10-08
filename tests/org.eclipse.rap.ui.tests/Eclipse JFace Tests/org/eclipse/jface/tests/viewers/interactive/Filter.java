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
package org.eclipse.jface.tests.viewers.interactive;

import java.util.ArrayList;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class Filter extends ViewerFilter {

    public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < elements.length; ++i) {
            // toss every second item
            if (i % 2 == 1) {
                result.add(elements[i]);
            }
        }
        return result.toArray();
    }

    public boolean isFilterProperty(Object element, Object aspect) {
        return false;
    }

    /* (non-Javadoc)
     * Method declared on ViewerFilter
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        // not used
        return false;
    }
}
