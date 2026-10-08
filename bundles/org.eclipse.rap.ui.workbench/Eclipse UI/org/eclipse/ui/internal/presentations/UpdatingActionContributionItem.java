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
package org.eclipse.ui.internal.presentations;

import org.eclipse.jface.action.ActionContributionItem;

/**
 */
public class UpdatingActionContributionItem extends ActionContributionItem {

    /**
     * @param action
     */
    public UpdatingActionContributionItem(ISelfUpdatingAction action) {
        super(action);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IContributionItem#isVisible()
     */
    public boolean isVisible() {
        ISelfUpdatingAction action = (ISelfUpdatingAction) getAction();
        return super.isVisible() && action.shouldBeVisible();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IContributionItem#update(java.lang.String)
     */
    public void update(String propertyName) {
        ISelfUpdatingAction action = (ISelfUpdatingAction) getAction();
        action.update();

        super.update(propertyName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IContributionItem#isDynamic()
     */
    public boolean isDynamic() {
        return true;
    }
}
