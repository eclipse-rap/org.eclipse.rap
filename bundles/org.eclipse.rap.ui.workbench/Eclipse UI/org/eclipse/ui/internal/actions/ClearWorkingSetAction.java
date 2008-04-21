/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.WorkingSetFilterActionGroup;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * Clears the selected working set in the working set action group.
 */
public class ClearWorkingSetAction extends Action {
    private WorkingSetFilterActionGroup actionGroup;

    /**
     * Creates a new instance of the receiver.
     * 
     * @param actionGroup the action group this action is created in
     */
    public ClearWorkingSetAction(WorkingSetFilterActionGroup actionGroup) {
        super(WorkbenchMessages.get().ClearWorkingSetAction_text);
        Assert.isNotNull(actionGroup);
        setToolTipText(WorkbenchMessages.get().ClearWorkingSetAction_toolTip);
        setEnabled(actionGroup.getWorkingSet() != null);
        // RAP [bm]: HelpSystem
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
//				IWorkbenchHelpContextIds.CLEAR_WORKING_SET_ACTION);
        // RAPEND: [bm] 
        this.actionGroup = actionGroup;
    }

    /**
     * Overrides method from Action
     * 
     * @see Action#run
     */
    public void run() {
        actionGroup.setWorkingSet(null);
    }
}
