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

import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class SystemMenuRestore extends SystemMenuStateChange {

    /**
     * @param site
     * @param name
     * @param state
     */
    public SystemMenuRestore(IStackPresentationSite site) {
        super(site, WorkbenchMessages.get().PartPane_restore, IStackPresentationSite.STATE_RESTORED);
    }
}
