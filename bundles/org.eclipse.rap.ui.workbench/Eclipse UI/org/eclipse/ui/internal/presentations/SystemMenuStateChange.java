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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.presentations.IStackPresentationSite;

/**
 * 
 * 
 */
public class SystemMenuStateChange extends Action implements
        ISelfUpdatingAction {
    private IStackPresentationSite site;

    private int state;

    public SystemMenuStateChange(IStackPresentationSite site, String name,
            int state) {
        this.site = site;
        this.state = state;

        setText(name);
        update();
    }

    public void dispose() {
        this.site = null;
    }

    public void run() {
        site.setState(state);
    }

    public void update() {
        setEnabled(site.getState() != state && site.supportsState(state));
    }

    public boolean shouldBeVisible() {
        return site.supportsState(state);
    }

}
