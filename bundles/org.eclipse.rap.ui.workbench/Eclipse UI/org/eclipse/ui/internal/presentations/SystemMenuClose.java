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

/**
 * This convenience class provides a "close" system menu item that closes
 * the currently selected pane in a presentation. Presentations can use
 * this to add a close item to their system menu.
 * 
 */
public final class SystemMenuClose extends Action implements ISelfUpdatingAction {

    private IStackPresentationSite site;
    private IPresentablePart part;

    public SystemMenuClose(IStackPresentationSite site) {
        this.site = site;
        setText(WorkbenchMessages.get().PartPane_close);
    }

    public void dispose() {
        site = null;
    }

    public void run() {
        if (part != null) {
            site.close(new IPresentablePart[] { part });
        }
    }

    public void setTarget(IPresentablePart presentablePart) {
        this.part = presentablePart;
        setEnabled(presentablePart != null && site.isCloseable(presentablePart));
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.presentations.ISelfUpdatingAction#update()
     */
    public void update() {
        setTarget(site.getSelectedPart());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.presentations.ISelfUpdatingAction#shouldBeVisible()
     */
    public boolean shouldBeVisible() {
        return true;
    }
}
