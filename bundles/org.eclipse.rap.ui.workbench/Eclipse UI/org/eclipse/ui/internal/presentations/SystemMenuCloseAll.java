/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class SystemMenuCloseAll extends Action implements ISelfUpdatingAction {

    private IStackPresentationSite presentation;

    public SystemMenuCloseAll(IStackPresentationSite presentation) {
        this.presentation = presentation;
        setText(WorkbenchMessages.get().PartPane_closeAll);
    }

    public void dispose() {
        presentation = null;
    }

    public void run() {
        presentation.close(presentation.getPartList());
    }

    public void update() {
        IPresentablePart[] parts = presentation.getPartList();
        setEnabled(parts.length != 0);
    }

    public boolean shouldBeVisible() {
        return true;
    }

}
