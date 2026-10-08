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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.SubContributionItem;

/**
 * This class marks a sub contribution item as belonging to
 * an action set.
 */
public class ActionSetContributionItem extends SubContributionItem implements
        IActionSetContributionItem {

    /**
     * The action set id.
     */
    private String actionSetId;

    /**
     * Constructs a new item
     */
    public ActionSetContributionItem(IContributionItem item, String actionSetId) {
        super(item);
        this.actionSetId = actionSetId;
    }

    /**
     * Returns the action set id.
     */
    public String getActionSetId() {
        return actionSetId;
    }

    /**
     * Sets the action set id.
     */
    public void setActionSetId(String newActionSetId) {
        actionSetId = newActionSetId;
    }
}
